import {fileURLToPath, URL} from 'node:url';
import {defineConfig} from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url)),
        },
    },
    server: {
        host: '0.0.0.0',
        port: 5173,
        strictPort: false,
        allowedHosts: true,
        proxy: {
            '/api': 'http://localhost:8080',
            '/v3/api-docs': 'http://localhost:8080',
            '/swagger-ui': 'http://localhost:8080',
            '/openapi.yaml': 'http://localhost:8080',
        },
    },
});
