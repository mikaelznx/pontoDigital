<template>
  <div class="login-wrapper">
    <div class="particles"></div>

    <div class="login-card">
      <h1>Login Administrativo</h1>
      <p class="subtitle">Entre com suas credenciais</p>

      <div class="form-group">
        <label for="username">Usuário</label>
        <input v-model="username" id="username" type="text" placeholder="Digite seu usuário" />
      </div>

      <div class="form-group">
        <label for="password">Senha</label>
        <input v-model="password" id="password" type="password" placeholder="Digite sua senha" />
      </div>

      <button @click="login" :disabled="loading">
        {{ loading ? 'Entrando...' : 'Entrar' }}
      </button>

      <p v-if="erro" class="error-message">{{ erro }}</p>
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
const erro = ref('')
const loading = ref(false)

const login = async () => {
  erro.value = ''
  loading.value = true
  try {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      username: username.value,
      password: password.value
    })
    localStorage.setItem('token', response.data.token)
    router.push('/admin')
  } catch (err) {
    erro.value = 'Usuário ou senha inválidos.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #0f0f0f, #1f1f1f, #2a2a2a);
  background-size: 200% 200%;
  animation: gradientMove 20s ease infinite;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  padding: 20px;
}

.particles::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: radial-gradient(#ffffff10 1px, transparent 1px);
  background-size: 50px 50px;
  opacity: 0.1;
  z-index: 0;
}

.login-card {
  position: relative;
  z-index: 2;
  background: rgba(30, 30, 30, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 40px 30px;
  max-width: 400px;
  width: 100%;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
  color: #f1f1f1;
  animation: fadeIn 0.8s ease-out;
  transition: all 0.3s ease;
  text-align: center;
}

.login-card h1 {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #ffffff;
}

.subtitle {
  font-size: 14px;
  color: #bbbbbb;
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  font-size: 13px;
  color: #ccc;
  margin-bottom: 6px;
  display: block;
  text-align: left;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border-radius: 10px;
  border: none;
  background: rgba(255, 255, 255, 0.08);
  color: #f9f9f9;
  font-size: 14px;
  transition: outline 0.3s ease;
}

.form-group input:focus {
  outline: 2px solid #4cc9f0;
  background: rgba(255, 255, 255, 0.12);
}

button {
  width: 100%;
  padding: 12px;
  font-size: 15px;
  font-weight: bold;
  border: none;
  border-radius: 10px;
  background: #222;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s ease;
}

button:hover {
  background: #333;
}

button:disabled {
  background: #555;
  cursor: not-allowed;
}

.error-message {
  margin-top: 16px;
  color: #ff6b6b;
  font-size: 13px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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
