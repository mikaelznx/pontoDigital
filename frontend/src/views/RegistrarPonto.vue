<template>
  <div class="form-container">
    <h2>Registrar Entrada</h2>
    <input v-model="matricula" placeholder="Matrícula" />
    <button @click="registrar">Registrar</button>
    <p v-if="mensagem">{{ mensagem }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const matricula = ref('')
const mensagem = ref('')

const registrar = async () => {
  try {
    const response = await axios.post('http://localhost:8080/api/ponto', {
      matricula: matricula.value
    })
    mensagem.value = response.data
    matricula.value = ''
  } catch (error) {
    mensagem.value = 'Erro ao registrar ponto.'
  }
}
</script>

<style scoped>
.form-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: #f0f0f0;
  gap: 1rem;
}

input {
  padding: 10px;
  width: 250px;
  font-size: 16px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

button {
  padding: 10px 20px;
  font-size: 16px;
  border-radius: 6px;
  background-color: #28a745;
  color: white;
  border: none;
}

button:hover {
  background-color: #218838;
}
</style>
