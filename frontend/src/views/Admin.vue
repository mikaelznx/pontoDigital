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

    <!-- Listagem de funcionários para seleção -->
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
              <input
                  type="time"
                  v-model="horariosEditaveis[p.id]"
                  @change="editarHorario(p)"
              />
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
import { reactive, ref, onMounted, watch } from "vue";
import axios from "axios";

const funcionarios = ref([]);
const pontos = ref([]);
const funcionarioSelecionadoId = ref("");
const mesSelecionado = ref(new Date().getMonth() + 1);

const meses = [
  "Janeiro",
  "Fevereiro",
  "Março",
  "Abril",
  "Maio",
  "Junho",
  "Julho",
  "Agosto",
  "Setembro",
  "Outubro",
  "Novembro",
  "Dezembro",
];

const novoFuncionario = reactive({
  nome: "",
  matricula: "",
});

const mensagemErro = ref("");

const horariosEditaveis = reactive({});

// Função para carregar todos os funcionários
async function carregarFuncionarios() {
  try {
    const res = await axios.get("http://localhost:8080/api/admin/funcionarios");
    funcionarios.value = res.data;
  } catch (error) {
    console.error("Erro ao carregar funcionários:", error);
  }
}

// Função para cadastrar funcionário
async function cadastrarFuncionario() {
  mensagemErro.value = "";
  if (!novoFuncionario.nome.trim() || !novoFuncionario.matricula.trim()) {
    mensagemErro.value = "Nome e matrícula são obrigatórios.";
    return;
  }
  try {
    const res = await axios.post("http://localhost:8080/api/admin/funcionarios", {
      nome: novoFuncionario.nome,
      matricula: novoFuncionario.matricula,
    });
    funcionarios.value.push(res.data);
    novoFuncionario.nome = "";
    novoFuncionario.matricula = "";
  } catch (error) {
    mensagemErro.value =
        error.response?.data?.message || "Erro ao cadastrar funcionário.";
  }
}

// Função para carregar pontos do funcionário selecionado e mês selecionado
async function carregarPontos() {
  if (!funcionarioSelecionadoId.value || !mesSelecionado.value) {
    pontos.value = [];
    return;
  }
  try {
    const res = await axios.get('http://localhost:8080/api/pontos?role=admin')
    pontos.value = res.data
    pontos.value = res.data;

    // Preencher os horários editáveis
    pontos.value.forEach((p) => {
      horariosEditaveis[p.id] = p.hora ?? "08:00"; // default se null
    });
  } catch (error) {
    console.error("Erro ao carregar pontos:", error);
  }
}

// Formata data ISO (ex: 2025-07-28) para dd/mm/yyyy
function formataData(dataISO) {
  const d = new Date(dataISO);
  return d.toLocaleDateString("pt-BR");
}

// Função para editar horário (salva a alteração)
async function editarHorario(ponto) {
  const novoHorario = horariosEditaveis[ponto.id];
  if (!novoHorario) return;

  try {
    await axios.put(`http://localhost:8080/api/admin/pontos/${ponto.id}`, {
      hora: novoHorario,
    });
    // Atualiza localmente
    ponto.hora = novoHorario;
  } catch (error) {
    alert("Erro ao atualizar horário");
  }
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
}
</style>
