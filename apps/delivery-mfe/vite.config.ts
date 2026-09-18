import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { federation } from '@module-federation/vite'
import mfConfig from './module-federation.config'

export default defineConfig({
  base: 'http://192.168.5.157:4174/',
  plugins: [react(), tailwindcss(), federation(mfConfig)],
  server: { host: '0.0.0.0', port: 4174, cors: true },
})
