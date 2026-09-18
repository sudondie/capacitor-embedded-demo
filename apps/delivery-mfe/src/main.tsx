import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App'
import './style.css'

const demoPlatform = {
  delivery: { getCurrentLocation: async () => ({ latitude: 55.7558, longitude: 37.6173 }) },
  feedback: { success: async () => undefined },
  analytics: { track: async () => undefined },
}
createRoot(document.getElementById('root')!).render(<StrictMode><App platform={demoPlatform} /></StrictMode>)
