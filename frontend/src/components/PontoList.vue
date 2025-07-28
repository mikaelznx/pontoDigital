<template>
  <div class="bg-white p-6 rounded shadow-md w-full max-w-3xl">
    <h2 class="text-xl font-bold mb-4">Lista de Pontos</h2>
    <div v-for="ponto in pontos" :key="ponto.id" class="border-b py-2 flex justify-between items-center">
      <div>
        <input v-model="ponto.nome" class="border p-1 mr-2" />
        <input v-model="ponto.dataHora" class="border p-1" />
      </div>
      <div>
        <button class="bg-blue-500 text-white px-2 py-1 rounded mr-2" @click="editar(ponto)">Salvar</button>
        <button class="bg-red-500 text-white px-2 py-1 rounded" @click="deletar(ponto.id)">Excluir</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const pontos = ref([])

async function carregar() {
  const res = await axios.get('http://localhost:8080/api/pontos?role=admin')
  pontos.value = res.data
}

async function editar(ponto) {
  await axios.put(`http://localhost:8080/api/ponto/${ponto.id}?role=admin`, ponto)
  alert('Ponto atualizado!')
}

async function deletar(id) {
  await axios.delete(`http://localhost:8080/api/ponto/${id}?role=admin`)
  pontos.value = pontos.value.filter(p => p.id !== id)
}

onMounted(carregar)
</script>
