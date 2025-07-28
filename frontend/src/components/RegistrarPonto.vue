<template>
  <div class="bg-white p-6 rounded shadow-md w-full max-w-md">
    <h1 class="text-xl font-bold mb-4">Registrar Ponto</h1>
    <form @submit.prevent="registrar">
      <input v-model="nome" placeholder="Seu nome" class="border p-2 w-full mb-4" />
      <button class="bg-green-500 text-white px-4 py-2 rounded" type="submit">Registrar</button>
    </form>
    <p v-if="mensagem" class="mt-4 text-green-600">{{ mensagem }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const nome = ref('')
const mensagem = ref('')

async function registrar() {
  if (!nome.value.trim()) return

  await axios.post('http://localhost:8080/api/registrar-ponto', { nome: nome.value })
  mensagem.value = 'Ponto registrado com sucesso!'
  nome.value = ''
}
</script>
