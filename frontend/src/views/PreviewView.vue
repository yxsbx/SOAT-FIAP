<script setup>
import {ref} from 'vue';
import {
  BarChart3,
  CalendarClock,
  Car,
  CheckCircle2,
  ChevronRight,
  ClipboardList,
  FileText,
  Gauge,
  MapPin,
  Menu,
  MessageCircle,
  Package,
  Search,
  ShieldCheck,
  Smartphone,
  Store,
  UserCog,
  Wrench,
} from 'lucide-vue-next';

const activeAudience = ref('customer');

const workshopStats = [
  {label: 'OS em andamento', value: '25', tone: 'blue'},
  {label: 'Entregas este mês', value: '55', tone: 'green'},
  {label: 'Aguardando aprovação', value: '12', tone: 'amber'},
  {label: 'Peças para comprar', value: '8', tone: 'slate'},
];

const orderRows = [
  ['MCA1D23', 'Freios', 'Em execução', 'Hoje'],
  ['RCA3F67', 'Diagnóstico', 'Finalizada', 'Ontem'],
  ['TLG5H10', 'Preventiva', 'Recebida', 'Amanhã'],
  ['JPR8L76', 'Revisão', 'Aguardando aprovação', '2 dias'],
];

const customerBenefits = [
  {
    title: 'Diagnóstico mais adiantado',
    text: 'Antes de ir até a oficina, o cliente descreve ruídos, falhas, luzes no painel, histórico e urgência.',
    icon: FileText,
  },
  {
    title: 'Escolha transparente',
    text: 'Com o caso registrado, o cliente compara oficinas, lojas de peças e decide como quer comprar as peças.',
    icon: MapPin,
  },
  {
    title: 'Tudo acompanhado no site',
    text: 'Comunicação, orçamento, aceite, status da execução e finalização ficam centralizados no AutoCare Hub.',
    icon: MessageCircle,
  },
];

const partnerBenefits = [
  {
    title: 'Oficinas conectadas a lojas',
    text: 'A oficina pode ter estoque próprio ou comprar peças direto com lojas filiadas, com prazos e pedidos visíveis.',
    icon: Store,
  },
  {
    title: 'Gestão completa da operação',
    text: 'Ordens, orçamentos, clientes, estoque, faturamento, métricas, permissões e perfis em um painel único.',
    icon: Gauge,
  },
  {
    title: 'Status em tempo real para o cliente',
    text: 'O cliente acompanha o veículo sem ligar várias vezes, e a oficina reduz retrabalho de comunicação.',
    icon: Smartphone,
  },
];

const partnerDeliveries = [
  {
    title: 'Admin de oficina',
    text: 'Dono ou gerente controla acessos, permissões, faturamento, clientes, estoque, ordens e relatórios.',
    icon: ShieldCheck,
  },
  {
    title: 'Funcionário',
    text: 'Cada usuário vê apenas os módulos liberados para sua rotina: atendimento, pátio, peças ou serviços.',
    icon: UserCog,
  },
  {
    title: 'Loja de peças',
    text: 'Lojas parceiras acompanham pedidos, fornecedores, itens de maior prazo e relacionamento com oficinas.',
    icon: Package,
  },
  {
    title: 'Admin Master',
    text: 'Visão de plataforma com interessados, clientes ativos, uso por empresa, assinaturas e faturamento da plataforma.',
    icon: BarChart3,
  },
];

const mobileActions = [
  {label: 'Status', icon: Car},
  {label: 'Orçamento', icon: FileText},
  {label: 'Peças', icon: Package},
  {label: 'Mensagens', icon: MessageCircle},
];

const mobileUpdates = [
  {plate: 'MRA2E19', status: 'Em diagnóstico', note: 'Oficina analisando relato inicial'},
  {plate: 'VRA7B42', status: 'Aguardando aprovação', note: 'Orçamento aguardando aceite'},
];

function switchAudience(audience) {
  activeAudience.value = audience;
  window.scrollTo({top: 0, behavior: 'smooth'});
}

function scrollToSection(id) {
  const element = document.getElementById(id);

  if (!element) {
    return;
  }

  element.scrollIntoView({
    behavior: 'smooth',
    block: 'start',
  });
}
</script>

<template>
  <main :class="{ 'partner-mode': activeAudience === 'partner' }" class="preview-shell">
    <header class="preview-header">
      <RouterLink class="preview-brand" to="/preview" @click="activeAudience = 'customer'">
        <span><Wrench :size="24"/></span>
        <strong>AutoCare Hub</strong>
      </RouterLink>
      <nav>
        <button
            :class="{ active: activeAudience === 'customer' }"
            type="button"
            @click="switchAudience('customer')"
        >
          Para clientes
        </button>
        <button
            :class="{ active: activeAudience === 'partner' }"
            type="button"
            @click="switchAudience('partner')"
        >
          Para parceiros
        </button>
        <a v-if="activeAudience === 'customer'" href="#diagnostico" @click.prevent="scrollToSection('diagnostico')">
          Como funciona
        </a>
        <a v-else href="#diferenciais" @click.prevent="scrollToSection('diferenciais')">Diferenciais</a>
      </nav>
      <RouterLink class="preview-login" to="/login">
        Já tenho acesso
        <ChevronRight :size="17"/>
      </RouterLink>
    </header>

    <template v-if="activeAudience === 'customer'">
      <section class="preview-hero customer-hero">
        <div class="preview-copy">
          <span class="preview-eyebrow">Cuidado transparente para o seu veículo</span>
          <h1>Diagnóstico, orçamento e status do veículo sem idas desnecessárias à oficina.</h1>
          <p>
            A AutoCare Hub ajuda você a registrar os sintomas do veículo, escolher oficinas e lojas de
            peças com mais clareza e acompanhar tudo pelo site: comunicação, orçamento, aceite e
            finalização do serviço.
          </p>
          <div class="preview-actions">
            <RouterLink class="preview-primary" to="/login">
              Entrar como cliente
              <ChevronRight :size="18"/>
            </RouterLink>
            <a class="preview-secondary" href="#diagnostico" @click.prevent="scrollToSection('diagnostico')">
              Entender o diagnóstico
            </a>
          </div>
        </div>

        <div aria-label="Prévia da experiência do cliente" class="customer-app-preview">
          <div class="customer-symptom-card">
            <span>Anamnese do veículo</span>
            <h2>Conte o que está acontecendo antes de sair de casa</h2>
            <div class="symptom-form-preview">
              <label>Modelo <strong>Honda Fit 2018</strong></label>
              <label>Sintoma <strong>Barulho ao frear e vibração no volante</strong></label>
              <label>Urgência <strong>Preciso resolver ainda esta semana</strong></label>
            </div>
            <div class="customer-choice-row">
              <article>
                <Wrench :size="18"/>
                <strong>3 oficinas próximas</strong>
                <small>Compare atendimento e disponibilidade</small>
              </article>
              <article>
                <Store :size="18"/>
                <strong>Comprar peças</strong>
                <small>Na loja parceira ou direto com a oficina</small>
              </article>
            </div>
          </div>
        </div>
      </section>

      <section id="diagnostico" class="preview-features">
        <div class="feature-intro">
          <span>Jornada do cliente</span>
          <h2>Menos deslocamento, mais clareza</h2>
          <p>
            O cliente não precisa ir várias vezes à oficina para explicar o problema, pedir retorno
            ou aprovar orçamento. O histórico fica organizado e acessível no AutoCare Hub.
          </p>
        </div>

        <div class="feature-list">
          <article v-for="feature in customerBenefits" :key="feature.title">
            <component :is="feature.icon" :size="30"/>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.text }}</p>
          </article>
        </div>
      </section>

      <section id="localização" class="preview-views">
        <div class="feature-intro">
          <span>Após cadastro</span>
          <h2>Busca por CEP para encontrar atendimento perto de você</h2>
          <p>
            A área do cliente permite informar o CEP para visualizar oficinas e lojas de peças
            próximas, acompanhar veículos em atendimento e consultar orçamentos em aberto.
          </p>
        </div>
        <div class="customer-cep-preview">
          <label>
            <span>
              <Search :size="18"/>
              CEP
            </span>
            <strong>01310-100</strong>
          </label>
          <article>
            <MapPin :size="20"/>
            <div>
              <strong>Oficina Vila Auto</strong>
              <span>1,2 km - diagnóstico, freios e revisão</span>
            </div>
          </article>
          <article>
            <Store :size="20"/>
            <div>
              <strong>Peças Centro Sul</strong>
              <span>2,4 km - filtros, óleo, pastilhas e bateria</span>
            </div>
          </article>
        </div>
      </section>
    </template>

    <template v-else>
      <section class="preview-hero partner-hero">
        <div class="preview-copy">
          <span class="preview-eyebrow">Para oficinas e lojas de peças</span>
          <h1>Venda mais, organize a operação e aproxime oficina, loja e cliente.</h1>
          <p>
            Oficinas ganham gestão completa, permissões, orçamento, estoque e métricas. Lojas de
            peças se conectam a oficinas, acompanham pedidos e ajudam a reduzir atrasos de peças
            difíceis ou importadas.
          </p>
          <div class="preview-actions">
            <RouterLink class="preview-primary" to="/demo">
              Testar versão simplificada
              <ChevronRight :size="18"/>
            </RouterLink>
            <RouterLink class="preview-secondary" to="/login">Já tenho cadastro</RouterLink>
          </div>
        </div>

        <div aria-label="Prévia visual do sistema" class="device-showcase">
          <div class="laptop-mockup">
            <div class="mockup-topbar">
              <div>
                <Wrench :size="22"/>
                <strong>AutoCare Hub</strong>
              </div>
              <span><Search :size="14"/> Buscar clientes, placas, peças, ordens...</span>
            </div>
            <div class="mockup-body">
              <aside>
                <i>
                  <Gauge :size="16"/>
                </i>
                <i>
                  <ClipboardList :size="16"/>
                </i>
                <i>
                  <Car :size="16"/>
                </i>
                <i>
                  <Package :size="16"/>
                </i>
                <i>
                  <Wrench :size="16"/>
                </i>
              </aside>
              <section>
                <div class="mockup-hero-card">
                  <span>Gestão inteligente</span>
                  <strong>Controle de oficina em tempo real</strong>
                </div>
                <div class="mockup-kpis">
                  <article v-for="stat in workshopStats" :key="stat.label" :class="`tone-${stat.tone}`">
                    <strong>{{ stat.value }}</strong>
                    <span>{{ stat.label }}</span>
                  </article>
                </div>
                <div class="mockup-table">
                  <div v-for="row in orderRows" :key="row[0]" class="mockup-row">
                    <span>{{ row[0] }}</span>
                    <span>{{ row[1] }}</span>
                    <strong>{{ row[2] }}</strong>
                    <small>{{ row[3] }}</small>
                  </div>
                </div>
              </section>
            </div>
          </div>

          <div class="phone-mockup">
            <div class="phone-screen">
              <header>
                <Wrench :size="18"/>
                <strong>AutoCare Hub</strong>
                <Menu :size="16"/>
              </header>
              <section>
                <div>
                  <span>Olá, Oficina</span>
                  <strong>25 ordens</strong>
                </div>
                <small>Hoje</small>
              </section>
              <div class="phone-alert">
                <CheckCircle2 :size="15"/>
                <span>Aplicativo focado no que precisa de resposta rápida.</span>
              </div>
              <div class="phone-grid">
                <article v-for="action in mobileActions" :key="action.label">
                  <component :is="action.icon" :size="26"/>
                  <span>{{ action.label }}</span>
                </article>
              </div>
              <div class="phone-updates">
                <article v-for="update in mobileUpdates" :key="update.plate">
                  <strong>{{ update.plate }}</strong>
                  <span>{{ update.status }}</span>
                  <small>{{ update.note }}</small>
                </article>
              </div>
              <nav class="phone-tabbar">
                <span>Início</span>
                <span>Ordens</span>
                <span>Peças</span>
              </nav>
            </div>
          </div>
        </div>
      </section>

      <section id="diferenciais" class="preview-features">
        <div class="feature-intro">
          <span>Diferenciais para parceiros</span>
          <h2>Mais eficiência para oficina e mais demanda para lojas</h2>
          <p>
            A AutoCare Hub aproxima quem atende o veículo de quem fornece as peças, com comunicação
            clara para o cliente e gestão operacional para o parceiro.
          </p>
        </div>

        <div class="feature-list">
          <article v-for="feature in partnerBenefits" :key="feature.title">
            <component :is="feature.icon" :size="30"/>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.text }}</p>
          </article>
        </div>
      </section>

      <section id="views" class="preview-views">
        <div class="feature-intro">
          <span>Perfis no sistema</span>
          <h2>O que cada parceiro consegue fazer com AutoCare Hub</h2>
          <p>
            A plataforma separa permissões e telas por perfil para que cada pessoa veja o que
            precisa: operação, financeiro, estoque, fornecedores, clientes ou indicadores.
          </p>
        </div>

        <div class="platform-view-list">
          <article v-for="view in partnerDeliveries" :key="view.title">
            <component :is="view.icon" :size="28"/>
            <strong>{{ view.title }}</strong>
            <span>{{ view.text }}</span>
          </article>
        </div>
      </section>

      <section id="mobile" class="preview-mobile-band">
        <div>
          <span>Responsivo</span>
          <h2>Mobile para respostas rápidas, desktop para gestão completa</h2>
          <p id="preview-mobile-band-p">
            No celular, o parceiro acompanha status, mensagens, aprovações e alertas. Permissões,
            faturamento, relatórios e cadastros complexos ficam melhores na visão desktop.
          </p>
        </div>
        <div class="mobile-experience-panel">
          <ul>
            <li>
              <CheckCircle2 :size="18"/>
              Status do cliente em tempo real
            </li>
            <li>
              <CheckCircle2 :size="18"/>
              Gestão de usuários e permissões
            </li>
            <li>
              <CheckCircle2 :size="18"/>
              Estoque, fornecedores e lojas parceiras
            </li>
            <li>
              <CalendarClock :size="18"/>
              Métricas, faturamento e prazo previsto x realizado
            </li>
          </ul>
          <p>
            A demo pública é oferecida apenas para oficinas e lojas de peças. O cliente acessa
            sua área depois do cadastro, com busca por CEP e acompanhamento do próprio veículo.
          </p>
          <Smartphone :size="88" class="mobile-band-icon"/>
        </div>
      </section>
    </template>
  </main>
</template>
