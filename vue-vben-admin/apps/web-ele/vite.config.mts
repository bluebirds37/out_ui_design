import { defineConfig } from '@vben/vite-config';

import ElementPlus from 'unplugin-element-plus/vite';

export default defineConfig(async () => {
  return {
    application: {},
    vite: {
      plugins: [
        ElementPlus({
          format: 'esm',
        }),
      ],
      server: {
        proxy: {
          '/api': {
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/api/, ''),
            // mock代理目标地址
            target: 'http://localhost:5320/api',
            ws: true,
          },
          '/trailnote-admin': {
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/trailnote-admin/, ''),
            target: 'http://127.0.0.1:8081',
            ws: false,
          },
        },
      },
    },
  };
});
