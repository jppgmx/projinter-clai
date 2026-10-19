import tailwindcss from '@tailwindcss/vite'
import react from '@vitejs/plugin-react'
import { fileURLToPath, URL } from 'node:url'
import { copyFileSync, existsSync } from 'node:fs'
import { defineConfig, loadEnv, type Connect } from 'vite'

const pages = ["abertura-estudo-caso", "cadastro-estudante", "detalhes-estudante", "documentos-estudo-caso", "editar-estudante", "editar-estudo-caso", "estudantes", "situacao-estudo-caso"]
const aliases = new Set(pages.map(page => `/${page}.html`))
const rewrite: Connect.NextHandleFunction = (req, _res, next) => {
  if (req.url && aliases.has(req.url.split('?')[0])) req.url = `/html${req.url}`
  next()
}

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const target = env.VITE_API_PROXY_TARGET ?? 'http://localhost:8080'
  return {
    plugins: [react(), tailwindcss(), {
      name: 'preservar-urls-html',
      configureServer(server) { server.middlewares.use(rewrite) },
      configurePreviewServer(server) { server.middlewares.use(rewrite) },
      writeBundle() {
        for (const page of pages) {
          const source = new URL(`./dist/html/${page}.html`, import.meta.url)
          if (existsSync(source)) copyFileSync(source, new URL(`./dist/${page}.html`, import.meta.url))
        }
      },
    }],
    resolve: { alias: { '@': fileURLToPath(new URL('./js/src', import.meta.url)) } },
    build: { rollupOptions: { input: {
      index: fileURLToPath(new URL('./index.html', import.meta.url)),
      ...Object.fromEntries(pages.map(page => [page, fileURLToPath(new URL(`./html/${page}.html`, import.meta.url))])),
    } } },
    server: { port:4040, strictPort:true, proxy: { '/api': { target, changeOrigin:true } } },
    preview: { proxy: { '/api': { target, changeOrigin:true } } },
  }
})
