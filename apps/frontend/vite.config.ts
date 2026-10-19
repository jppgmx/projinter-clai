import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const apiTarget = env.VITE_API_PROXY_TARGET ?? 'http://localhost:8080'

  return {
    plugins: [react(), tailwindcss()],
    resolve: {
      alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
    },
    build: {
      rollupOptions: {
        input: Object.fromEntries(['index', 'estudantes', 'cadastro-estudante', 'detalhes-estudante', 'abertura-estudo-caso', 'editar-estudo-caso']
          .map(page => [page, fileURLToPath(new URL(`./${page}.html`, import.meta.url))])),
      },
    },
    server: {
      port: 4040,
      strictPort: true,
      // Evita CORS em desenvolvimento: /api -> back end Spring
      proxy: { '/api': { target: apiTarget, changeOrigin: true } },
    },
  }
})
