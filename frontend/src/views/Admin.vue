<template>
  <div class="container">
    <h1>Gerenciar Funcionários e Pontos</h1>

    <!-- Formulário para cadastrar funcionário -->
    <section class="card">
      <h2>Cadastrar Funcionário</h2>
      <form @submit.prevent="cadastrarFuncionario" class="form-cadastro">
        <div class="form-group">
          <label>Nome:</label>
          <input v-model="novoFuncionario.nome" type="text" required />
        </div>
        <div class="form-group">
          <label>Matrícula:</label>
          <input v-model="novoFuncionario.matricula" type="text" required />
        </div>
        <div class="form-group">
          <label>Carga Horária (horas/dia):</label>
          <select v-model="novoFuncionario.cargaHoraria" required class="select-carga">
            <option value="">Selecione a carga horária</option>
            <option value="4">4 horas</option>
            <option value="6">6 horas</option>
            <option value="8">8 horas</option>
            <option value="custom">Personalizada</option>
          </select>
        </div>
        <div v-if="novoFuncionario.cargaHoraria === 'custom'" class="form-group">
          <label>Horas Personalizadas:</label>
          <input
              v-model.number="novoFuncionario.cargaHorariaCustom"
              type="number"
              min="1"
              max="12"
              step="0.5"
              placeholder="Ex: 7.5"
              required
          />
        </div>
        <button type="submit" class="btn-primary">Cadastrar</button>
      </form>
      <p v-if="mensagemErro" class="erro">{{ mensagemErro }}</p>
    </section>

    <!-- Listagem de funcionários -->
    <section class="card">
      <h2>Funcionários</h2>
      <div class="filtros">
        <select v-model="funcionarioSelecionadoId" @change="carregarPontos" class="select-func">
          <option disabled value="">Selecione um funcionário</option>
          <option v-for="f in funcionarios" :key="f.id" :value="f.id">
            {{ f.nome }} ({{ f.matricula }}) - {{ f.cargaHoraria }}h/dia
          </option>
        </select>

        <!-- Seleção do mês -->
        <label class="label-mes" for="mes-select">Mês:</label>
        <select id="mes-select" v-model="mesSelecionado" @change="carregarPontos" class="select-mes">
          <option v-for="(nome, i) in meses" :key="i" :value="i + 1">
            {{ nome }}
          </option>
        </select>
      </div>

      <!-- Botão de Exportar -->
      <div v-if="funcionarioSelecionadoId && mesSelecionado" class="export-btn-container">
        <button @click="gerarRelatorioPDF" class="btn-primary">
          Exportar para PDF
        </button>
      </div>

      <!-- Listagem de pontos -->
      <div v-if="pontos.length" class="tabela-pontos-container">
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
            <!-- Input para data -->
            <td>
              <input
                  type="date"
                  v-model="datasEditaveis[p.id]"
                  :max="ultimoDiaMes"
                  class="input-date"
              />
            </td>

            <!-- Input para horário -->
            <td>
              <input
                  type="time"
                  v-model="horariosEditaveis[p.id]"
                  class="input-time"
              />
            </td>

            <!-- Botão salvar -->
            <td>
              <button @click="editarPonto(p)" class="btn-secondary">Salvar</button>
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
import { reactive, ref, onMounted, computed } from "vue";
import api from "../axios.js";

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
  matricula: "",
  cargaHoraria: "",
  cargaHorariaCustom: null
});
const mensagemErro = ref("");
const horariosEditaveis = reactive({});
const datasEditaveis = reactive({});

const ultimoDiaMes = computed(() => {
  const anoAtual = new Date().getFullYear();
  const mes = mesSelecionado.value;
  const ultimoDia = new Date(anoAtual, mes, 0).getDate();
  const mesStr = String(mes).padStart(2, "0");
  const diaStr = String(ultimoDia).padStart(2, "0");
  return `${anoAtual}-${mesStr}-${diaStr}`;
});

async function carregarFuncionarios() {
  try {
    const res = await api.get("/admin/funcionarios");
    funcionarios.value = res.data;
  } catch (error) {
    console.error("Erro ao carregar funcionários:", error);
    mensagemErro.value = "Erro ao carregar funcionários.";
  }
}

async function cadastrarFuncionario() {
  mensagemErro.value = "";

  if (!novoFuncionario.nome.trim() || !novoFuncionario.matricula.trim() || !novoFuncionario.cargaHoraria) {
    mensagemErro.value = "Nome, matrícula e carga horária são obrigatórios.";
    return;
  }

  if (novoFuncionario.cargaHoraria === 'custom' && !novoFuncionario.cargaHorariaCustom) {
    mensagemErro.value = "Informe a carga horária personalizada.";
    return;
  }

  const cargaHorariaFinal = novoFuncionario.cargaHoraria === 'custom'
      ? novoFuncionario.cargaHorariaCustom
      : parseInt(novoFuncionario.cargaHoraria);

  try {
    const res = await api.post("/admin/funcionarios", {
      nome: novoFuncionario.nome,
      matricula: novoFuncionario.matricula,
      cargaHoraria: cargaHorariaFinal
    });

    funcionarios.value.push(res.data);

    // Limpar formulário
    novoFuncionario.nome = "";
    novoFuncionario.matricula = "";
    novoFuncionario.cargaHoraria = "";
    novoFuncionario.cargaHorariaCustom = null;

    alert("Funcionário cadastrado com sucesso!");
  } catch (error) {
    mensagemErro.value =
        error.response?.data?.message || "Erro ao cadastrar funcionário.";
  }
}

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
      datasEditaveis[p.id] = p.data;
    });
  } catch (error) {
    console.error("Erro ao carregar pontos:", error);
    mensagemErro.value = "Erro ao carregar pontos.";
  }
}

async function editarPonto(ponto) {
  const novoHorario = horariosEditaveis[ponto.id];
  const novaData = datasEditaveis[ponto.id];
  if (!novoHorario || !novaData) return;

  try {
    await api.put(`/admin/pontos/${ponto.id}`, {
      hora: novoHorario,
      data: novaData,
    });
    ponto.hora = novoHorario;
    ponto.data = novaData;
    alert("Ponto atualizado com sucesso!");
  } catch (error) {
    console.error("Erro ao atualizar ponto:", error);
    alert("Erro ao atualizar ponto.");
  }
}

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
      responseType: 'blob'
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
  max-width: 900px;
  margin: 2rem auto;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
  padding: 1rem 2rem;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgb(0 0 0 / 0.1);
}

h1 {
  text-align: center;
  font-weight: 700;
  font-size: 2.2rem;
  margin-bottom: 2rem;
  color: #222;
}

h2 {
  font-weight: 600;
  margin-bottom: 1rem;
  color: #444;
  border-bottom: 2px solid #0077cc;
  padding-bottom: 0.4rem;
}

.card {
  background-color: #f7f9fc;
  padding: 1.8rem 2rem;
  margin-bottom: 2.5rem;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgb(0 0 0 / 0.05);
}

.form-cadastro {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  align-items: flex-end;
  justify-content: center;
}

.form-group {
  flex: 1 1 40%;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

label {
  font-weight: 600;
  color: #333;
}

input[type="text"],
input[type="number"],
input[type="time"],
input[type="date"],
select,
.select-carga {
  padding: 0.6rem 0.8rem;
  font-size: 1rem;
  border-radius: 6px;
  border: 1.8px solid #ccc;
  transition: border-color 0.25s ease;
}

input[type="text"]:focus,
input[type="number"]:focus,
input[type="time"]:focus,
input[type="date"]:focus,
select:focus,
.select-carga:focus {
  outline: none;
  border-color: #0077cc;
  box-shadow: 0 0 6px #66aaff88;
}

.btn-primary {
  background-color: #0077cc;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 0.7rem 1.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
  align-self: center;
}

.btn-primary:hover {
  background-color: #005fa3;
}

.btn-secondary {
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 0.45rem 1.3rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-secondary:hover {
  background-color: #3e8e41;
}

.filtros {
  display: flex;
  gap: 1.5rem;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.4rem;
  flex-wrap: wrap;
}

.select-func,
.select-mes {
  padding: 0.5rem 0.9rem;
  font-size: 1rem;
  border-radius: 6px;
  border: 1.8px solid #ccc;
  min-width: 230px;
  transition: border-color 0.25s ease;
}

.select-func:focus,
.select-mes:focus {
  outline: none;
  border-color: #0077cc;
  box-shadow: 0 0 6px #66aaff88;
}

.label-mes {
  font-weight: 600;
  color: #333;
  margin-left: 10px;
  white-space: nowrap;
}

.export-btn-container {
  text-align: center;
  margin-bottom: 1.5rem;
}

.tabela-pontos-container {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 1rem;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgb(0 0 0 / 0.07);
}

th,
td {
  border: 1px solid #ddd;
  padding: 0.75rem 1rem;
  text-align: center;
  vertical-align: middle;
}

th {
  background-color: #0077cc;
  color: white;
  font-weight: 600;
}

input.input-date,
input.input-time {
  padding: 0.4rem 0.6rem;
  border-radius: 5px;
  border: 1.5px solid #ccc;
  font-size: 1rem;
  width: 120px;
  transition: border-color 0.25s ease;
}

input.input-date:focus,
input.input-time:focus {
  outline: none;
  border-color: #0077cc;
  box-shadow: 0 0 6px #66aaff88;
}

.erro {
  color: #d93025;
  margin-top: 0.75rem;
  font-weight: 700;
  text-align: center;
  font-size: 1rem;
}
</style>