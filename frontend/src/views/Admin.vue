<template>
  <div class="container">
    <h1>Gerenciar Funcionários e Pontos</h1>

    <!-- Formulário para cadastrar funcionário -->
    <section class="card">
      <h2>Cadastrar Funcionário</h2>
      <form @submit.prevent="cadastrarFuncionario">
        <div class="form-group">
          <label>Nome:</label>
          <input v-model="novoFuncionario.nome" type="text" required />
        </div>
        <div class="form-group">
          <label>Matrícula:</label>
          <input v-model="novoFuncionario.matricula" type="text" required />
        </div>
        <button type="submit">Cadastrar</button>
      </form>
      <p v-if="mensagemErro" class="erro">{{ mensagemErro }}</p>
    </section>

    <!-- Listagem de funcionários -->
    <section class="card">
      <h2>Funcionários</h2>
      <select v-model="funcionarioSelecionadoId" @change="carregarPontos">
        <option disabled value="">Selecione um funcionário</option>
        <option v-for="f in funcionarios" :key="f.id" :value="f.id">
          {{ f.nome }} ({{ f.matricula }})
        </option>
      </select>

      <!-- Seleção do mês -->
      <label>Mês:</label>
      <select v-model="mesSelecionado" @change="carregarPontos">
        <option v-for="(nome, i) in meses" :key="i" :value="i + 1">
          {{ nome }}
        </option>
      </select>

      <!-- Botão de Exportar -->
      <div v-if="funcionarioSelecionadoId && mesSelecionado">
        <button @click="gerarRelatorioPDF" style="margin-top: 10px;">
          Exportar para PDF
        </button>
      </div>

      <!-- Listagem de pontos -->
      <div v-if="pontos.length">
        <h3>Pontos registrados</h3>
        <table>
          <thead>
          <tr>
            <th>Data</th>
            <th>Horário</th>
            <th>Ações</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="p in pontos" :key="p.id">
            <td>{{ formataData(p.data) }}</td>
            <td>
              <input type="time" v-model="horariosEditaveis[p.id]" />
            </td>
            <td>
              <button @click="editarHorario(p)">Salvar</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div v-else>
        <p>Nenhum ponto encontrado para este funcionário e mês.</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
import api from "../axios.js";  // axios configurado com interceptor para enviar token

// Estados reativos
const funcionarios = ref([]);
const pontos = ref([]);
const funcionarioSelecionadoId = ref("");
const mesSelecionado = ref(new Date().getMonth() + 1);
const meses = [
  "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
  "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
];
const novoFuncionario = reactive({
  nome: "",
  matricula: ""
});
const mensagemErro = ref("");
const horariosEditaveis = reactive({});

// Carrega lista de funcionários do backend
async function carregarFuncionarios() {
  try {
    const res = await api.get("/admin/funcionarios");
    funcionarios.value = res.data;
  } catch (error) {
    console.error("Erro ao carregar funcionários:", error);
    mensagemErro.value = "Erro ao carregar funcionários.";
  }
}

// Cadastra novo funcionário
async function cadastrarFuncionario() {
  mensagemErro.value = "";
  if (!novoFuncionario.nome.trim() || !novoFuncionario.matricula.trim()) {
    mensagemErro.value = "Nome e matrícula são obrigatórios.";
    return;
  }
  try {
    const res = await api.post("/admin/funcionarios", {
      nome: novoFuncionario.nome,
      matricula: novoFuncionario.matricula,
    });
    funcionarios.value.push(res.data);
    novoFuncionario.nome = "";
    novoFuncionario.matricula = "";
    alert("Funcionário cadastrado com sucesso!");
  } catch (error) {
    mensagemErro.value =
        error.response?.data?.message || "Erro ao cadastrar funcionário.";
  }
}

// Carrega os pontos do funcionário e mês selecionados
async function carregarPontos() {
  if (!funcionarioSelecionadoId.value || !mesSelecionado.value) {
    pontos.value = [];
    return;
  }

  const anoAtual = new Date().getFullYear();
  const mes = mesSelecionado.value;
  const inicio = `${anoAtual}-${String(mes).padStart(2, "0")}-01`;
  const ultimoDia = new Date(anoAtual, mes, 0).getDate();
  const fim = `${anoAtual}-${String(mes).padStart(2, "0")}-${ultimoDia}`;

  try {
    const res = await api.get("/admin/pontos", {
      params: {
        funcionarioId: funcionarioSelecionadoId.value,
        inicio,
        fim,
      },
    });
    pontos.value = res.data;

    pontos.value.forEach((p) => {
      horariosEditaveis[p.id] = p.hora ?? "08:00";
    });
  } catch (error) {
    console.error("Erro ao carregar pontos:", error);
    mensagemErro.value = "Erro ao carregar pontos.";
  }
}

// Edita horário de ponto
async function editarHorario(ponto) {
  const novoHorario = horariosEditaveis[ponto.id];
  if (!novoHorario) return;

  try {
    await api.put(`/admin/pontos/${ponto.id}`, {
      hora: novoHorario,
    });
    ponto.hora = novoHorario;
    alert("Horário atualizado com sucesso!");
  } catch (error) {
    console.error("Erro ao atualizar horário:", error);
    alert("Erro ao atualizar horário.");
  }
}

// Gera relatório PDF — usa endpoint correto /admin/relatorios/mensal e responseType blob
async function gerarRelatorioPDF() {
  if (!funcionarioSelecionadoId.value || !mesSelecionado.value) return;

  const anoAtual = new Date().getFullYear();
  const mes = mesSelecionado.value;

  try {
    const response = await api.get('/admin/relatorios/mensal', {
      params: {
        funcionarioId: funcionarioSelecionadoId.value,
        mes,
        ano: anoAtual
      },
      responseType: 'blob' // importante para pegar arquivo PDF
    });

    const fileURL = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
    window.open(fileURL, '_blank');

  } catch (error) {
    console.error("Erro ao gerar relatório PDF:", error);
    alert("Erro ao gerar relatório PDF.");
  }
}

function formataData(dataISO) {
  if (!dataISO) return "";
  const partes = dataISO.split("-");
  return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

onMounted(() => {
  carregarFuncionarios();
});
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 2rem auto;
  font-family: Arial, sans-serif;
  padding: 1rem;
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.card {
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 1rem 1.5rem;
  margin-bottom: 2rem;
  background-color: #f9f9f9;
}

form {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
}

.form-group {
  flex: 1 1 45%;
  display: flex;
  flex-direction: column;
}

label {
  margin-bottom: 0.3rem;
  font-weight: bold;
}

input[type="text"],
input[type="time"],
select {
  padding: 0.4rem 0.6rem;
  font-size: 1rem;
  border-radius: 4px;
  border: 1px solid #ccc;
}

button {
  padding: 0.5rem 1rem;
  background-color: #0077cc;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #005fa3;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

th,
td {
  border: 1px solid #ddd;
  padding: 0.6rem;
  text-align: center;
}

.erro {
  color: red;
  margin-top: 0.5rem;
  font-weight: bold;
}
</style>
