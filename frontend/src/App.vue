<template>
  <div class="container">
    <h1>Registro de Ponto</h1>
    <form @submit.prevent="registrarPonto">
      <input v-model="matricula" placeholder="Digite a matrícula ou use o leitor" autofocus />
      <button type="submit">Registrar</button>
    </form>
    <p v-if="mensagem">{{ mensagem }}</p>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      matricula: '',
      mensagem: ''
    }
  },
  methods: {
    async registrarPonto() {
      try {
        const response = await axios.post('http://localhost:8080/api/registrar-ponto', this.matricula, {
          headers: { 'Content-Type': 'text/plain' }
        })
        this.mensagem = response.data
        this.matricula = ''
      } catch (error) {
        this.mensagem = 'Erro ao registrar ponto'
      }
    }
  }
}
</script>

<style>
.container {
  max-width: 400px;
  margin: auto;
  padding-top: 100px;
  font-family: sans-serif;
}
input {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
}
button {
  width: 100%;
  padding: 10px;
}
</style>
