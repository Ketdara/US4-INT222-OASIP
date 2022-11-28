import { createApp } from 'vue'
import App from './App.vue'
import Emitter from 'tiny-emitter';
import router from './router'
import './index.css'

const app = createApp(App)
app.config.globalProperties.$emitter = new Emitter();

app.use(router)
app.mount('#app')