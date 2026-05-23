export const commandGroups = [
  {
    title: 'Ambiente local',
    items: [
      {
        label: 'Subir PostgreSQL',
        command: 'docker compose up -d postgres',
        detail: 'Inicializa somente o banco usado pela API.',
      },
      {
        label: 'Rodar API',
        command: 'mvn spring-boot:run',
        detail: 'Sobe o backend em http://localhost:8080.',
      },
      {
        label: 'Rodar frontend',
        command: 'cd frontend && npm run dev',
        detail: 'Sobe o Vue 3 em http://localhost:5173.',
      },
    ],
  },
  {
    title: 'Docker',
    items: [
      {
        label: 'API e banco',
        command: 'docker compose up --build',
        detail: 'Builda a imagem e sobe todo o ambiente.',
      },
      {
        label: 'Parar containers',
        command: 'docker compose down',
        detail: 'Para a aplicação e o PostgreSQL.',
      },
      {
        label: 'Limpar volume',
        command: 'docker compose down -v',
        detail: 'Remove os dados locais do banco.',
      },
    ],
  },
  {
    title: 'Qualidade',
    items: [
      {
        label: 'Testes backend',
        command: 'mvn test',
        detail: 'Executa a suíte Java com H2 em memória.',
      },
      {
        label: 'Build frontend',
        command: 'cd frontend && npm run build',
        detail: 'Gera os assets de produção do Vue.',
      },
      {
        label: 'Vulnerabilidades',
        command: 'mvn dependency-check:check',
        detail: 'Gera relatórios em target/dependency-check.',
      },
    ],
  },
];
