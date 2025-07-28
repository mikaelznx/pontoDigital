<template>
  <div class="container">
    <h1>Livro de Ponto</h1>
    <div class="buttons">
      <button @click="irParaRegistro">Registrar Entrada</button>
      <button @click="irParaLogin">Acesso do Administrador</button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()

const irParaRegistro = () => router.push('/registrar')
const irParaLogin = () => router.push('/login')
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  gap: 2rem;
  background: #f9f9f9;
}

.buttons {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

button {
  padding: 12px 20px;
  font-size: 16px;
  border: none;
  border-radius: 8px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  width: 250px;
}

button:hover {
  background-color: #0056b3;
}
</style>
