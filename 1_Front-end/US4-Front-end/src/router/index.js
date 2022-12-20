// This file:
// - manages Router

import {createRouter, createWebHistory} from 'vue-router'
import OASIP from '../views/OASIP.vue'
import User from '../views/User.vue'
import EventCategory from '../views/EventCategory.vue'

const history = createWebHistory(import.meta.env.BASE_URL)
const routes = [
  {
    path: '/',
    name: 'OASIP',
    component: OASIP
  },
  {
    path: '/user',
    name: 'User',
    component: User
  },
  {
    path: '/eventCategory',
    name: 'EventCategory',
    component: EventCategory
  }
]

const router = createRouter({history, routes})
export default router