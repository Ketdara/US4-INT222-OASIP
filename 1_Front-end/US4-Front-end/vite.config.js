// import { defineConfig } from 'vite'
// import vue from '@vitejs/plugin-vue'

// // https://vitejs.dev/config/
// export default defineConfig({
//   plugins: [vue()]
// })

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  base: '/us4/',
  plugins:[vue  ()],
  // server:{
  //   proxy:{
  //     '/api': {
  //       target:'http://localhost:8080',
  //       changeOrigin:true,
  //       secure:false,
  //       // rewrite:(path) => path.replace(/^\/api/, '')
  //     }
  //   }
  // }
})