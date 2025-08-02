<template>
  <div :class="['container', isDarkMode ? 'dark' : 'light']">
    <header class="header">
      <h1>Gerenciar Funcionários e Pontos</h1>
      <button @click="toggleDarkMode" class="toggle-btn">
        {{ isDarkMode ? 'Modo Claro' : 'Modo Escuro' }}
      </button>
    </header>

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

const isDarkMode = ref(false);

// Tenta carregar a preferência do localStorage ao montar
onMounted(() => {
  carregarFuncionarios();
  const saved = localStorage.getItem("darkMode");
  if (saved === "true") isDarkMode.value = true;
});

function toggleDarkMode() {
  isDarkMode.value = !isDarkMode.value;
  localStorage.setItem("darkMode", isDarkMode.value);
}

// --- resto do seu código (funções API, cadastrarFuncionario, carregarPontos, etc.) ---

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
</script>

<style scoped>
.container {
  max-width: 900px;
  margin: 2.5rem auto;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
  padding: 2rem 3rem;
  border-radius: 14px;
  transition: background-color 0.4s ease, color 0.4s ease;
}

/* DARK MODE */
.container.dark {
  background: #1f1f1f;
  color: #e0e0e0;
  box-shadow: 0 6px 18px rgb(0 0 0 / 0.8);
}

.container.dark h1 {
  color: #d0d0d0;
}

.container.dark h2 {
  color: #aad4ff;
  border-bottom: 3px solid #4cc9f0;
}

.container.dark .card {
  background-color: #2a2a2a;
  box-shadow: 0 10px 24px rgb(0 0 0 / 0.9);
}

.container.dark .form-group label {
  color: #c5d7ff;
}

.container.dark input,
.container.dark select {
  background: #1f1f1f;
  border: 1.5px solid #444;
  color: #e0e0e0;
}

.container.dark input:focus,
.container.dark select:focus {
  border-color: #4cc9f0;
  box-shadow: 0 0 10px #4cc9f0aa;
  background: #272727;
}

.container.dark table {
  background: #2a2a2a;
  color: #d0d0d0;
  box-shadow: 0 4px 16px rgb(0 0 0 / 0.7);
}

.container.dark thead tr {
  background-color: #4cc9f0;
  color: #222;
  box-shadow: inset 0 -4px 6px rgb(0 0 0 / 0.3);
}

.container.dark tbody tr:hover {
  background-color: #3a3f4a;
}

.container.dark .btn-primary {
  background-color: #276fbf;
  box-shadow: 0 0 6px #276fbfcc;
}

.container.dark .btn-primary:hover {
  background-color: #1f548c;
  box-shadow: 0 0 20px #4cc9f0;
}

.container.dark .btn-secondary {
  background-color: #2d995b;
  box-shadow: 0 0 6px #2d995bcc;
}

.container.dark .btn-secondary:hover {
  background-color: #1f703f;
  box-shadow: 0 0 15px #27ae60;
}

.container.dark .erro {
  color: #ff6b6b;
}

/* LIGHT MODE */
.container.light {
  background: #fff;
  color: #222;
  box-shadow: 0 4px 12px rgb(0 0 0 / 0.1);
}

.container.light h1 {
  color: #222;
}

.container.light h2 {
  color: #444;
  border-bottom: 2px solid #0077cc;
}

.container.light .card {
  background-color: #f7f9fc;
  box-shadow: 0 2px 6px rgb(0 0 0 / 0.05);
}

.container.light .form-group label {
  color: #333;
}

.container.light input,
.container.light select {
  background: #fff;
  border: 1.8px solid #ccc;
  color: #222;
}

.container.light input:focus,
.container.light select:focus {
  border-color: #0077cc;
  box-shadow: 0 0 6px #66aaff88;
  background: #fff;
}

.container.light table {
  background: white;
  color: #222;
  box-shadow: 0 2px 8px rgb(0 0 0 / 0.07);
}

.container.light thead tr {
  background-color: #0077cc;
  color: white;
  box-shadow: inset 0 -4px 6px rgb(0 0 0 / 0.3);
}

.container.light tbody tr:hover {
  background-color: #eef3f7;
}

.container.light .btn-primary {
  background-color: #0077cc;
}

.container.light .btn-primary:hover {
  background-color: #005fa3;
}

.container.light .btn-secondary {
  background-color: #4caf50;
}

.container.light .btn-secondary:hover {
  background-color: #3e8e41;
}

.container.light .erro {
  color: #d93025;
}

/* GERAL */
h1 {
  text-align: center;
  font-weight: 700;
  font-size: 2.2rem;
  margin-bottom: 2rem;
}

h2 {
  font-weight: 600;
  margin-bottom: 1rem;
  padding-bottom: 0.4rem;
}

.card {
  padding: 1.8rem 2rem;
  margin-bottom: 2.5rem;
  border-radius: 10px;
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
}

input[type="text"],
input[type="number"],
input[type="time"],
input[type="date"],
select,
.select-carga {
  padding: 0.65rem 0.9rem;
  font-size: 1rem;
  border-radius: 8px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input[type="text"],
input[type="number"],
input[type="time"],
input[type="date"],
select,
.select-carga {
  border-style: solid;
  border-width: 1.5px;
}

.btn-primary {
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.85rem 2rem;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
  align-self: center;
  width: 220px;
  letter-spacing: 0.02em;
}

.btn-secondary {
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.55rem 1.5rem;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
  letter-spacing: 0.01em;
}

.filtros {
  display: flex;
  gap: 1.8rem;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.8rem;
  flex-wrap: wrap;
}

.select-func,
.select-mes {
  padding: 0.65rem 1rem;
  font-size: 1rem;
  border-radius: 8px;
  min-width: 240px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.label-mes {
  font-weight: 600;
  margin-left: 12px;
  white-space: nowrap;
  letter-spacing: 0.02em;
}

.export-btn-container {
  text-align: center;
  margin-bottom: 2rem;
}

.tabela-pontos-container {
  overflow-x: auto;
  border-radius: 14px;
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  font-size: 1rem;
  border-radius: 14px;
  overflow: hidden;
}

thead tr {
  font-weight: 700;
  letter-spacing: 0.03em;
}

th,
td {
  border-bottom: 1.5px solid;
  padding: 1rem 1.2rem;
  text-align: center;
  vertical-align: middle;
}

tbody tr:hover {
  cursor: default;
}

input.input-date,
input.input-time {
  padding: 0.5rem 0.7rem;
  border-radius: 6px;
  font-size: 1rem;
  width: 120px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  font-family: inherit;
}

input.input-date,
input.input-time {
  border-style: solid;
  border-width: 1.5px;
}

input.input-date:focus,
input.input-time:focus {
  outline: none;
}

.erro {
  margin-top: 1rem;
  font-weight: 700;
  text-align: center;
  font-size: 1rem;
  letter-spacing: 0.01em;
}
</style>
