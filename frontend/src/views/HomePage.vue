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
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 2.5rem;
  background: linear-gradient(135deg, #0f0f0f, #1f1f1f, #2a2a2a);
  background-size: 200% 200%;
  animation: gradientMove 20s ease infinite;
  color: #f1f1f1;
  padding: 20px;
  text-align: center;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

h1 {
  font-size: 2.8rem;
  font-weight: 700;
  text-shadow: 0 0 8px rgba(76, 201, 240, 0.8);
}

.buttons {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  width: 280px;
}

button {
  padding: 14px 0;
  font-size: 1.1rem;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  background: #222;
  color: #f1f1f1;
  box-shadow: 0 0 10px transparent;
  transition: background 0.3s ease, box-shadow 0.3s ease;
  font-weight: 600;
}

button:hover {
  background: #333;
  box-shadow: 0 0 15px #4cc9f0;
}

@keyframes gradientMove {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
</style>
