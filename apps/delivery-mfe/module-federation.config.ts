import { createModuleFederationConfig } from '@module-federation/vite'

export default createModuleFederationConfig({
  name: 'delivery_mfe',
  filename: 'remoteEntry.js',
  manifest: true,
  bundleAllCSS: true,
  exposes: {
    './DeliveryApp': './src/export-app.tsx',
  },
  shared: {},
})
