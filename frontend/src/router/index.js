import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'          // página inicial com os botões
import RegistrarPonto from '../views/RegistrarPonto.vue'
import Login from '../views/Login.vue'
import Admin from '../views/Admin.vue'

const routes = [
    { path: '/', component: HomePage },
    { path: '/registrar', component: RegistrarPonto },
    { path: '/login', component: Login },
    { path: '/admin', component: Admin }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
