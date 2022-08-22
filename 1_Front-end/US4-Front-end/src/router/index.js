// This file:
// - manages Router

import {createRouter, createWebHistory} from 'vue-router'
import OASIP from '../views/OASIP.vue'

const history = createWebHistory(import.meta.env.BASE_URL)
// const history = createWebHistory()
const routes = [
  {
    path: '/',
    name: 'OASIP',
    component: OASIP
  }
]

const router = createRouter({history, routes})
export default router