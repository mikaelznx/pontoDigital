<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-200">
    <div class="bg-white p-8 rounded-xl shadow-xl w-full max-w-md text-center">
      <h2 class="text-2xl font-bold mb-4">Login do Administrador</h2>
      <input v-model="username" placeholder="Usuário" class="mb-3 w-full px-4 py-2 border rounded" />
      <input v-model="password" placeholder="Senha" type="password" class="mb-4 w-full px-4 py-2 border rounded" />
      <button @click="login"
              class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 rounded">
        Entrar
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const username = ref('')
const password = ref('')

const login = async () => {
  try {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      username: username.value,
      password: password.value
    })

    localStorage.setItem('token', response.data.token)
    router.push('/admin')
  } catch (err) {
    alert("Credenciais inválidas.")
  }
}
</script>
