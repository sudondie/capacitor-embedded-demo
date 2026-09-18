import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { federation } from '@module-federation/vite'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  return {
    plugins: [
      react(),
      tailwindcss(),
      federation({
        name: 'profile_shell',
        remotes: {
          // The Vite runtime currently loses a manifest's publicPath in an
          // https://localhost Capacitor WebView and falls back to /remoteEntry.js.
          // Use the ESM entry explicitly in this spike; Vite's `base` on the
          // remote still resolves all of its chunks against the remote origin.
          delivery_mfe: {
            type: 'module',
            name: 'delivery_mfe',
            entry: env.VITE_DELIVERY_REMOTE_ENTRY.replace('mf-manifest.json', 'remoteEntry.js'),
          },
        },
        // Delivery owns React 18 while this shell uses React 19. They must not
        // be forced into a singleton share scope.
        shared: {},
      }),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
  }
})
