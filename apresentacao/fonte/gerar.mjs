// Gera os PDFs do pitch "Shark Tank" da Calculadora de Gastos:
//   01-Slides-Pitch.pdf, 02-Tempo-Apresentacao.pdf e um roteiro por integrante.
//
// Tudo (nomes, tempos, falas, numeros) mora neste arquivo, para os documentos
// nunca ficarem incoerentes entre si. Para trocar algo (ex.: o nome do 3o
// integrante em EQUIPE.m3), edite aqui e rode, nesta pasta:
//
//   node gerar.mjs
//
// Precisa do Microsoft Edge (ja vem no Windows): ele imprime o HTML em PDF.

import { writeFileSync, mkdirSync, existsSync, readdirSync, rmSync } from 'node:fs';
import { execFileSync } from 'node:child_process';
import { dirname, join, resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';
import { tmpdir } from 'node:os';

const AQUI = dirname(fileURLToPath(import.meta.url));
const SAIDA = resolve(AQUI, '..');
const HTML_DIR = join(AQUI, 'html');

const EDGE = [
  'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe',
  'C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe',
].find(existsSync);

// ---------------------------------------------------------------------------
// Dados da equipe e do pitch
// ---------------------------------------------------------------------------

const EQUIPE = {
  gabriel: { nome: 'Gabriel Cezar Oliveira Silva', curto: 'Gabriel', papel: 'Abertura e problema', cor: '#3A6EA5', ordem: 1 },
  lucas: { nome: 'Lucas Henrique Borille', curto: 'Lucas', papel: 'Produto, demo e tecnologia', cor: '#E0862B', ordem: 2 },
  m3: { nome: 'Integrante 3', curto: 'Integrante 3', papel: 'Negócio, números e o pedido', cor: '#7B5EA7', ordem: 3 },
};
const TODOS = { curto: 'Todos', cor: '#2D8A5B' };
const N1 = EQUIPE.gabriel.curto;
const N2 = EQUIPE.lucas.curto;
const N3 = EQUIPE.m3.curto;

const PITCH_SEG = 420; // 7 min de pitch
const TOTAL_SEG = 600; // + 3 min de perguntas

// Blocos de tempo (segundos). Cada bloco = 1 slide falado por 1 pessoa.
// Nas falas: [texto] = indicacao de cena; *texto* = enfatizar.
const BLOCOS = [
  {
    ini: 0, fim: 30, quem: 'gabriel', slide: 1, titulo: 'Abertura e o pedido',
    fala: [
      `Olá, sharks! Eu sou ${N1}, e comigo estão ${N2} e ${N3}. Nós somos a *Calculadora de Gastos* e viemos pedir *150 mil reais por 10%* da nossa empresa.`,
      `[pausa] Antes de explicar o porquê, uma pergunta: quem aqui sabe dizer quanto do salário do mês passado já tinha ido embora no dia 20? [pausa de 2 segundos, olhando para a turma]`,
    ],
    cola: ['Nomes da equipe', '150 mil por 10%', 'Pergunta: "quanto já tinha ido embora no dia 20?"'],
  },
  {
    ini: 30, fim: 90, quem: 'gabriel', slide: 2, titulo: 'O problema',
    fala: [
      `Se você hesitou, você não está só. Segundo a Serasa, em março de 2026 o Brasil chegou a *82,8 milhões de pessoas inadimplentes* — quase metade dos adultos com o nome negativado. E segundo a CNC, *82% das famílias* brasileiras têm alguma dívida, o maior nível da série histórica.`,
      `O problema não é só ganhar pouco. *É não enxergar.* O salário cai na conta, sai o aluguel, sai o mercado, sai o delivery, um Pix aqui, outro ali… e no fim do mês ninguém sabe para onde o dinheiro foi. A planilha dá trabalho e é abandonada na segunda semana. E o app do banco mostra um extrato — não um plano.`,
    ],
    cola: ['82,8 mi inadimplentes (Serasa, mar/2026)', 'Quase metade dos adultos', '82% das famílias com dívida (CNC)', '"Não é ganhar pouco, é não enxergar"', 'Planilha abandonada · banco = extrato'],
  },
  {
    ini: 90, fim: 135, quem: 'gabriel', slide: 3, titulo: 'Quem sente isso: a Ana',
    fala: [
      `Pensa na Ana: 24 anos, primeiro emprego, salário de 2.500 reais. Ela paga aluguel, come fora de vez em quando e sonha com uma viagem para a praia. Ela não é irresponsável — *só não tem um mapa.* No dia 20, ela olha o saldo baixo e não sabe se o problema foi o aluguel, o lazer ou aquele monte de comprinhas.`,
      `A Ana precisa de três respostas: quanto do salário já comprometi, com o quê, e quanto consigo guardar para o meu sonho. [aponte para as 3 perguntas no slide]`,
    ],
    transicao: `É exatamente isso que vamos mostrar agora. ${N2}, com você.`,
    cola: ['Ana, 24, 1º emprego, R$ 2.500', '"Não é irresponsável, só não tem mapa"', '3 perguntas: quanto · com o quê · quanto guardar'],
  },
  {
    ini: 135, fim: 165, quem: 'lucas', slide: 4, titulo: 'A solução em 4 passos',
    fala: [
      `Valeu, ${N1}. A Calculadora de Gastos transforma o salário num mapa em *quatro passos*. Um: você informa o salário, que vira a base de todos os cálculos. Dois: lança os gastos, já separados em três divisões. Três: o dashboard mostra na hora quanto do salário já foi comprometido. E quatro: você guarda dinheiro em metas, como um cofrinho.`,
    ],
    cola: ['Salário → Gastos → Dashboard → Metas', 'Salário = base de todos os cálculos'],
  },
  {
    ini: 165, fim: 195, quem: 'lucas', slide: 5, titulo: 'As 3 divisões',
    fala: [
      `O segredo está nas *três divisões*. Fixos: o que vem todo mês, como aluguel, contas e transporte. Variáveis: o que muda, como alimentação, lazer e compras. E Investimentos: o dinheiro que trabalha para você, como poupança e reserva de emergência. O app já vem com essas subdivisões prontas, e dá para criar as suas, como Academia ou Cripto. Ninguém precisa configurar nada para começar.`,
    ],
    cola: ['Fixos = todo mês', 'Variáveis = muda', 'Investimentos = trabalha para você', 'Subdivisões prontas + as suas'],
  },
  {
    ini: 195, fim: 255, quem: 'lucas', slide: 6, titulo: 'Demo do app',
    fala: [
      `[troque para o emulador, já preparado com os dados da Ana] Este é o Dashboard da Ana: salário de 2.500, total gasto de 2.020 e saldo de 480 reais. A barra mostra que *81% do salário* já está comprometido — e, se passar de 100%, o app avisa em vermelho. No gráfico, dá para ver que os Fixos são mais da metade dos gastos.`,
      `Agora, ao vivo: a Ana foi ao cinema. [aba Gastos → + Gasto → 40 → Variáveis → Lazer → Adicionar] Volto ao Dashboard… [aponte para a barra] *já foi para 82%.* Sem botão de atualizar.`,
      `Por último, Metas: a viagem para a praia está em 40%, 1.200 de 3.000 reais. Um toque em Guardar e o cofrinho cresce.`,
    ],
    cola: ['Dashboard: 2.500 · 2.020 · saldo 480', '81% comprometido; >100% = vermelho', 'Ao vivo: Cinema R$ 40 em Lazer → 82%', 'Metas: praia 40% (1.200 de 3.000)', 'Se o emulador falhar: fique no slide 6'],
  },
  {
    ini: 255, fim: 285, quem: 'lucas', slide: 7, titulo: 'Por dentro: tecnologia',
    fala: [
      `Por dentro, o app foi feito para crescer. É *Kotlin com Jetpack Compose*, em arquitetura *MVVM*: cada tela tem o seu ViewModel e se atualiza sozinha quando os dados mudam — foi o que vocês viram no 82%. Os dados ficam atrás de uma interface, o FinancasRepository: trocar por um banco de verdade, como o Room, é *mudar uma linha*, sem tocar nas telas. E os gráficos nós desenhamos do zero, com Canvas.`,
    ],
    transicao: `Agora, o negócio por trás disso. ${N3}, com você.`,
    cola: ['Kotlin + Jetpack Compose + Material 3', 'MVVM: tela se atualiza sozinha (o 82%)', 'FinancasRepository: banco novo = 1 linha', 'Gráficos com Canvas, sem biblioteca'],
  },
  {
    ini: 285, fim: 320, quem: 'm3', slide: 8, titulo: 'Concorrência e público',
    fala: [
      `Valeu, ${N2}. Hoje, quem quer controlar o dinheiro tem três caminhos. A planilha: flexível, mas trabalhosa. O app do banco: simples, mas só mostra o extrato. E os apps completos de finanças: planejam, mas assustam quem está começando, com tantas telas e configurações. Nós ocupamos *o quadrante vazio: simples e com planejamento de verdade.* Nosso público: jovens no primeiro emprego e universitários — quem está formando o hábito agora.`,
    ],
    cola: ['Planilha = trabalhosa', 'Banco = só extrato', 'Apps completos = assustam', 'Nós = simples + planeja', 'Público: 1º emprego e universitários'],
  },
  {
    ini: 320, fim: 365, quem: 'm3', slide: 9, titulo: 'Modelo de negócio',
    fala: [
      `Como ganhamos dinheiro? *Freemium.* O essencial é grátis para sempre: salário, três divisões, dashboard e metas. O Premium custa *9,90 por mês* e traz backup na nuvem, histórico mês a mês, alertas de orçamento e relatórios. E no B2B, empresas pagam *3 reais por colaborador por mês* para oferecer o app como benefício de saúde financeira.`,
      `Nossa meta em 12 meses: 50 mil downloads e 3% de conversão — *1.500 assinantes, cerca de 15 mil reais por mês.* E uma regra: nunca vamos vender dados de usuário.`,
    ],
    cola: ['Grátis para sempre: o essencial', 'Premium R$ 9,90/mês (R$ 79,90/ano)', 'B2B: R$ 3 por colaborador/mês', '50 mil × 3% = 1.500 assinantes ≈ R$ 15 mil/mês', 'Nunca vendemos dados'],
  },
  {
    ini: 365, fim: 400, quem: 'm3', slide: 10, titulo: 'Roadmap e uso do investimento',
    fala: [
      `Onde estamos: o MVP está pronto, com seis telas funcionando. E somos transparentes: hoje os dados ficam só na memória e *somem quando o app fecha*. Salvar no aparelho é o passo número um — e a arquitetura já está pronta para isso. Depois vêm histórico e alertas e, na versão 2.0, nuvem, iPhone e Open Finance. Os 150 mil vão *40% para produto, 35% para marketing e 25% para operação e LGPD.*`,
    ],
    cola: ['MVP pronto: 6 telas', 'Transparência: dados somem ao fechar → v1.1', 'v1.2 histórico/alertas · v2.0 nuvem, iPhone, Open Finance', '40% produto · 35% marketing · 25% operação/LGPD'],
  },
  {
    ini: 400, fim: 420, quem: 'm3', slide: 11, titulo: 'O pedido (fechamento)',
    fala: [
      `Então, sharks: estamos pedindo *150 mil reais por 10%* da Calculadora de Gastos. Com vocês, este MVP vira o app que ajuda milhões de brasileiros a sair do vermelho. [os três juntos] *Quem entra com a gente?*`,
    ],
    cola: ['150 mil por 10%', '"Sair do vermelho"', 'Os três juntos: "Quem entra com a gente?"'],
  },
  {
    ini: 420, fim: 600, quem: 'todos', slide: 11, titulo: 'Perguntas dos sharks',
    fala: [],
    cola: [],
  },
];

// Perguntas provaveis dos "sharks", com o dono do tema e uma resposta sugerida.
const PERGUNTAS = [
  { dono: 'gabriel', p: 'Por que o usuário vai continuar usando depois da primeira semana?', r: 'Porque o app dá motivo para voltar: as metas mostram o cofrinho crescendo e lançar um gasto leva segundos. Na versão 1.2 entram alertas quando uma divisão passa do limite.' },
  { dono: 'gabriel', p: 'Vocês conversaram com usuários de verdade?', r: 'Ainda não fizemos uma pesquisa formal — e é o primeiro passo com o investimento: testar com 100 universitários da nossa faculdade e medir quantos continuam usando depois de 30 dias.' },
  { dono: 'gabriel', p: 'E se um banco copiar a ideia?', r: 'O banco quer que você gaste no cartão dele; nós somos neutros e focados em quem está começando. Nosso diferencial é simplicidade e hábito — e uma equipe pequena se move mais rápido.' },
  { dono: 'lucas', p: 'Os dados somem quando o app fecha?', r: 'Hoje, sim: é um MVP com o repositório em memória. A arquitetura isola os dados atrás da interface FinancasRepository; implementar o Room é criar uma classe e trocar uma linha no AppContainer. É o item 1 do roadmap.' },
  { dono: 'lucas', p: 'E a segurança e a privacidade dos dados?', r: 'Não há login nem conexão com conta bancária: os dados ficam no aparelho. A foto do perfil usa o Photo Picker do Android, que não pede permissão de armazenamento. Quando houver nuvem, seguiremos a LGPD — por isso 25% do investimento.' },
  { dono: 'lucas', p: 'O dinheiro guardado nas metas sai do saldo?', r: 'Hoje, não: o dashboard mostra o "Guardado em metas" separado, e o que é lançado em Investimentos já entra no total comprometido. Na próxima versão, guardar numa meta poderá gerar o lançamento em Investimentos automaticamente.' },
  { dono: 'lucas', p: 'Por que só Android?', r: 'É onde está a grande maioria dos celulares no Brasil e é a plataforma da disciplina. Como a lógica está separada das telas, o iPhone entra na versão 2.0.' },
  { dono: 'm3', p: 'Como vocês chegaram a R$ 1,5 milhão de valuation sem receita?', r: 'É a nossa proposta para um MVP pronto e uma arquitetura preparada para crescer. Os 150 mil bancam 12 meses para bater a meta de 1.500 assinantes. Se acharem caro, estamos abertos a conversar sobre a participação.' },
  { dono: 'm3', p: 'Por que alguém pagaria, se a planilha é grátis?', r: 'O essencial continua grátis. Quem paga, paga por conveniência: backup, histórico, alertas e relatórios. A planilha é abandonada; o app está no bolso e responde em segundos.' },
  { dono: 'm3', p: 'Como vocês vão conseguir os primeiros usuários?', r: 'Começando pela nossa faculdade, com criadores de conteúdo de finanças e pelo canal B2B, em que uma única empresa traz centenas de usuários de uma vez.' },
  { dono: 'm3', p: '3% de conversão não é otimista?', r: 'É uma meta — e por isso vamos medir desde o primeiro mês. Se vier menor, o B2B compensa: uma empresa com 300 colaboradores já paga R$ 900 por mês.' },
];

// Checklist de cada integrante antes de entrar.
const CHECKLIST = {
  gabriel: [
    'Abra o 01-Slides-Pitch.pdf em tela cheia antes de começar (no Edge/Chrome: F11; no Adobe Reader: Ctrl+L).',
    `Você é o cronometrista a partir de 2:15: mostre "1 minuto" com a mão para ${N3} quando o relógio marcar 6:00.`,
    'Pratique a pausa depois da pergunta do slide 1 — ela é o gancho do pitch.',
    'Os números do slide 2 são da Serasa e da CNC: diga a fonte em voz alta, dá credibilidade.',
  ],
  lucas: [
    'Prepare o emulador 10 min antes. Tela Salário: nome "Ana", salário 2500.',
    'Lance em Gastos — Fixos: Aluguel 900, Internet e telefone 100, Transporte 150 · Variáveis: Alimentação 450, Lazer 170 · Investimentos: Reserva de emergência 250 (total R$ 2.020).',
    'Crie as metas "Viagem para a praia" (3000, guarde 1200) e "Notebook novo" (4000, guarde 600).',
    'NÃO feche o app depois de preparar: os dados ficam só na memória e somem ao fechar.',
    'Deixe o emulador aberto numa janela à parte (Alt+Tab pronto). Se algo falhar, fique no slide 6.',
  ],
  m3: [
    'Decore os números: 150 mil · 10% · 1,5 milhão · 9,90 · 3 reais · 50 mil · 3% · 1.500 · 15 mil · 40/35/25.',
    `Você é o cronometrista do bloco de ${N1} (0:00 a 2:15): sinalize se passar de 2:30.`,
    'No fechamento, olhe para os sharks e espere os três dizerem juntos "Quem entra com a gente?".',
    `Nas perguntas, você abre: escute, diga quem responde ("ótima pergunta — ${N2} explica") e complete com números.`,
  ],
};

// ---------------------------------------------------------------------------
// Utilitarios
// ---------------------------------------------------------------------------

const mmss = (s) => `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;
const dur = (s) => (s % 60 === 0 ? `${s / 60} min` : s < 60 ? `${s} s` : `${Math.floor(s / 60)} min ${s % 60} s`);
const esc = (s) => String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
const fmt = (s) => esc(s)
  .replace(/\[([^\]]+)\]/g, '<span class="cena">[$1]</span>')
  .replace(/\*([^*]+)\*/g, '<strong>$1</strong>');
const slug = (s) => s.normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[^A-Za-z0-9]+/g, '-').replace(/^-|-$/g, '');
const pessoa = (k) => (k === 'todos' ? TODOS : EQUIPE[k]);
const arquivoRoteiro = (k) => `0${2 + EQUIPE[k].ordem}-Roteiro-${slug(EQUIPE[k].curto)}.pdf`;

function faixaDoMembro(k) {
  const bs = BLOCOS.filter((b) => b.quem === k);
  return { ini: bs[0].ini, fim: bs[bs.length - 1].fim, slides: [bs[0].slide, bs[bs.length - 1].slide] };
}

// Donut em SVG (mesma ideia do GraficoPizza do app: arcos com um respiro entre as fatias).
function donut({ tam, esp, partes, respiro = 1.6, centro = '', rotulo = '', corCentro = 'inherit', corRotulo = 'inherit', fonteCentro = 28, fonteRotulo = 12 }) {
  const r = (tam - esp) / 2;
  const c = 2 * Math.PI * r;
  const total = partes.reduce((a, p) => a + p.v, 0);
  let acc = 0;
  const arcos = partes.map((p) => {
    const frac = p.v / total;
    const len = Math.max(frac * c - (partes.length > 1 ? (respiro / 360) * c : 0), 0);
    const off = -acc * c;
    acc += frac;
    return `<circle r="${r}" cx="${tam / 2}" cy="${tam / 2}" fill="none" stroke="${p.cor}" stroke-width="${esp}" stroke-dasharray="${len.toFixed(2)} ${(c - len).toFixed(2)}" stroke-dashoffset="${off.toFixed(2)}" transform="rotate(-90 ${tam / 2} ${tam / 2})"/>`;
  }).join('');
  return `<div class="donut" style="width:${tam}px;height:${tam}px">
    <svg width="${tam}" height="${tam}" viewBox="0 0 ${tam} ${tam}">${arcos}</svg>
    <div class="donut-c"><b style="font-size:${fonteCentro}px;color:${corCentro}">${centro}</b><span style="font-size:${fonteRotulo}px;color:${corRotulo}">${rotulo}</span></div>
  </div>`;
}

// ---------------------------------------------------------------------------
// Estilo compartilhado (cores tiradas do app: ui/theme/Cores.kt e Tema.kt)
// ---------------------------------------------------------------------------

const TOKENS = `
:root{
  --verde:#1B4332; --verde-2:#2D6A4F; --verde-claro:#74C69D; --verde-pos:#2D8A5B;
  --areia:#F3EDE3; --papel:#FFFBF5; --cartao:#E7EFE7; --tinta:#1F2A24; --tinta-2:#4A5A52; --linha:#DCD3C4;
  --azul:#3A6EA5; --laranja:#E0862B; --roxo:#7B5EA7; --vermelho:#C1443C; --ouro:#D4A72C;
  --noite:#0E2A1F;
}
*{box-sizing:border-box;margin:0;padding:0}
html{-webkit-print-color-adjust:exact;print-color-adjust:exact}
body{font-family:"Segoe UI Variable Text","Segoe UI",system-ui,sans-serif;color:var(--tinta);font-variant-numeric:tabular-nums}
.donut{position:relative;flex:none}
.donut svg{display:block}
.donut-c{position:absolute;inset:0;display:flex;flex-direction:column;align-items:center;justify-content:center;text-align:center;line-height:1.1}
.donut-c b{font-weight:700}
`;

// ---------------------------------------------------------------------------
// Slides (1280x720 = 16:9, mesmo tamanho do PowerPoint widescreen)
// ---------------------------------------------------------------------------

const CSS_SLIDES = `
@page{size:1280px 720px;margin:0}
html,body{background:var(--noite)}
.slide{width:1280px;height:720px;position:relative;overflow:hidden;background:var(--areia);padding:50px 64px 0;break-after:page}
.slide:last-child{break-after:auto}
.k{font-size:14px;font-weight:700;letter-spacing:.16em;text-transform:uppercase;color:var(--verde-2)}
.t{font-family:"Segoe UI Variable Display","Segoe UI",sans-serif;font-size:44px;line-height:1.08;font-weight:700;letter-spacing:-.015em;margin-top:10px;color:var(--verde)}
.t,.sub,.capa-sub,.frase,.promessa{text-wrap:balance}
.sub{font-size:20px;line-height:1.4;color:var(--tinta-2);margin-top:10px;max-width:920px}
.rodape{position:absolute;left:64px;right:64px;bottom:20px;display:flex;align-items:center;gap:14px;font-size:12.5px;color:var(--tinta-2)}
.rodape .esp{flex:1}
.chip{display:inline-flex;align-items:center;gap:7px;padding:4px 11px;border-radius:999px;background:rgba(27,67,50,.08);font-weight:600}
.chip i{width:8px;height:8px;border-radius:50%;display:inline-block}
.escuro{background:radial-gradient(1100px 720px at 82% 35%,#1E4D39 0%,#0E2A1F 58%,#08190F 100%);color:#F3EDE3}
.escuro .t{color:#fff}.escuro .k{color:var(--verde-claro)}.escuro .rodape{color:rgba(243,237,227,.65)}.escuro .chip{background:rgba(255,255,255,.08)}
.papel{background:var(--papel);border-radius:18px;box-shadow:0 1px 2px rgba(31,42,36,.06),0 6px 18px rgba(31,42,36,.06)}

/* 1 capa */
.capa{display:grid;grid-template-columns:1fr 460px;align-items:center;gap:40px;padding:0 72px}
.capa-t{font-family:"Segoe UI Variable Display","Segoe UI",sans-serif;font-size:96px;line-height:.95;font-weight:800;letter-spacing:-.03em;color:#fff;margin-top:18px}
.capa-sub{font-size:27px;line-height:1.3;margin-top:22px;color:#E4EFE7;max-width:600px}
.pedido-chip{display:inline-flex;align-items:center;gap:14px;margin-top:34px;padding:12px 20px 12px 14px;border:1.5px solid var(--ouro);border-radius:14px;font-size:21px}
.pedido-chip span{background:var(--ouro);color:#1A1405;font-size:13px;font-weight:800;letter-spacing:.12em;text-transform:uppercase;padding:6px 10px;border-radius:8px}
.pedido-chip b{color:#FFE7A3}
.nomes{margin-top:34px;font-size:16px;color:rgba(243,237,227,.8)}
.capa-graf{display:flex;flex-direction:column;align-items:center;gap:22px}
.leg{display:flex;gap:18px;font-size:14px;color:#E4EFE7}
.leg span{display:inline-flex;align-items:center;gap:7px}
.leg i{width:11px;height:11px;border-radius:3px;display:inline-block}

/* 2 problema */
.stats{display:grid;grid-template-columns:repeat(3,1fr);gap:24px;margin-top:52px}
.stat{padding:28px 28px 24px;border-top:6px solid var(--vermelho)}
.stat .n{font-family:"Segoe UI Variable Display","Segoe UI",sans-serif;font-size:74px;line-height:1;font-weight:800;letter-spacing:-.03em;color:var(--vermelho)}
.stat .d{font-size:21px;line-height:1.3;margin-top:12px;font-weight:600}
.stat .f{font-size:13px;margin-top:14px;color:var(--tinta-2);text-transform:uppercase;letter-spacing:.08em}
.frase{margin-top:58px;font-size:30px;line-height:1.3;color:var(--tinta);max-width:1000px}
.frase b{color:var(--verde)}
.fontes{position:absolute;left:64px;bottom:50px;font-size:12px;color:var(--tinta-2)}

/* 3 persona */
.persona{display:grid;grid-template-columns:470px 1fr;gap:40px;margin-top:46px}
.ana{padding:30px}
.ana-top{display:flex;gap:20px;align-items:center}
.avatar{width:92px;height:92px;border-radius:50%;background:var(--laranja);color:#fff;font-size:44px;font-weight:700;display:flex;align-items:center;justify-content:center;flex:none}
.ana h3{font-size:32px;line-height:1.1}
.ana .meta{font-size:17px;color:var(--tinta-2);margin-top:4px}
.tags{display:flex;flex-wrap:wrap;gap:8px;margin-top:22px}
.tags span{background:var(--cartao);border-radius:999px;padding:6px 13px;font-size:15px;font-weight:600;color:var(--verde)}
.ana blockquote{margin-top:24px;font-size:21px;line-height:1.4;font-style:italic;color:var(--tinta);border-left:4px solid var(--laranja);padding-left:16px}
.ana .ilustr{margin-top:18px;font-size:12px;color:var(--tinta-2);text-transform:uppercase;letter-spacing:.1em}
.perguntas h4{font-size:20px;color:var(--tinta-2);font-weight:600}
.perguntas ol{list-style:none;margin-top:14px;display:flex;flex-direction:column;gap:14px}
.perguntas li{display:flex;gap:18px;align-items:center;font-size:29px;font-weight:700;color:var(--verde);line-height:1.2}
.perguntas li b{width:52px;height:52px;border-radius:50%;background:var(--verde);color:#fff;font-size:24px;display:flex;align-items:center;justify-content:center;flex:none}
.hoje{display:grid;grid-template-columns:1fr 1fr;gap:16px;margin-top:28px}
.hoje div{border:1.5px dashed var(--linha);border-radius:14px;padding:14px 16px;font-size:16px;color:var(--tinta-2)}
.hoje strong{display:block;color:var(--tinta);font-size:17px;margin-bottom:2px}

/* 4 solucao */
.passos{display:grid;grid-template-columns:repeat(4,1fr);gap:34px;margin-top:54px}
.passo{position:relative;padding:28px 24px 26px;min-height:300px}
.passo .num{width:56px;height:56px;border-radius:16px;background:var(--verde);color:#fff;font-size:28px;font-weight:800;display:flex;align-items:center;justify-content:center}
.passo h3{font-size:30px;margin-top:22px;color:var(--verde)}
.passo p{font-size:18px;line-height:1.42;margin-top:10px;color:var(--tinta-2)}
.passo:not(:last-child)::after{content:"";position:absolute;right:-27px;top:50%;width:20px;height:20px;border-top:3.5px solid var(--verde-claro);border-right:3.5px solid var(--verde-claro);transform:translateY(-50%) rotate(45deg)}
.faixa{margin-top:42px;padding:16px 22px;border-radius:14px;background:var(--verde);color:#E4EFE7;font-size:19px;display:flex;gap:14px;align-items:center}
.faixa b{color:#fff}

/* 5 divisoes */
.divs{display:grid;grid-template-columns:repeat(3,1fr);gap:24px;margin-top:44px}
.div{overflow:hidden}
.div header{padding:20px 24px;color:#fff}
.div header h3{font-size:34px;line-height:1}
.div header p{font-size:17px;margin-top:6px;opacity:.92}
.div ul{list-style:none;padding:20px 24px 24px;display:flex;flex-direction:column;gap:14px}
.div li{font-size:20px;display:flex;align-items:center;gap:12px}
.div li i{width:9px;height:9px;border-radius:50%;flex:none}
.nota{margin-top:32px;border:2px dashed var(--verde-claro);border-radius:14px;padding:14px 20px;font-size:19px;color:var(--tinta)}
.nota b{color:var(--verde)}

/* 6 demo */
.demo{display:grid;grid-template-columns:350px 1fr;gap:26px;height:620px}
.demo-txt{padding-top:6px}
.demo-txt .t{font-size:40px}
.pontos{list-style:none;margin-top:26px;display:flex;flex-direction:column;gap:14px}
.pontos li{font-size:18px;line-height:1.38;padding-left:18px;border-left:4px solid var(--verde-claro)}
.pontos li b{color:var(--verde)}
.aovivo{margin-top:22px;background:var(--verde);color:#fff;border-radius:14px;padding:14px 16px;font-size:16px;line-height:1.4}
.aovivo span{display:block;font-size:12px;font-weight:800;letter-spacing:.14em;text-transform:uppercase;color:var(--verde-claro);margin-bottom:4px}
.fones{display:flex;gap:22px;align-items:flex-start;justify-content:flex-end;padding-top:4px}
.fone-wrap{display:flex;flex-direction:column;align-items:center;gap:10px}
.fone-wrap figcaption{font-size:14px;font-weight:600;color:var(--tinta-2)}

/* telefone (mock fiel as telas do app) */
.fone{width:360px;height:760px;border-radius:44px;background:#121714;padding:11px;zoom:.7;box-shadow:0 20px 44px rgba(14,42,31,.28)}
.tela{width:100%;height:100%;border-radius:34px;overflow:hidden;background:#F3EDE3;position:relative;font-family:Roboto,"Segoe UI",sans-serif;color:#2C332E}
.sb{height:30px;background:#1B4332;color:#fff;font-size:13px;display:flex;align-items:center;justify-content:space-between;padding:0 22px;font-weight:600}
.sb .ic{display:flex;gap:5px;align-items:center}
.sb .ic i{display:block;background:#fff;border-radius:2px}
.tab{height:58px;background:#FFFBF5;display:flex;align-items:center;padding:0 18px;font-size:22px}
.cont{padding:12px 12px;display:flex;flex-direction:column;gap:11px}
.mc{background:#E7EFE7;border-radius:16px;padding:14px;box-shadow:0 1px 3px rgba(0,0,0,.14);color:#41504A}
.mc h5{font-size:17px;font-weight:700;color:#2C332E;margin-bottom:10px;display:flex;justify-content:space-between;align-items:center}
.mc h5 small{font-size:12px;font-weight:400;color:#41504A;display:block;margin-top:2px}
.mc p{font-size:14px;margin-top:6px}
.tri{display:grid;grid-template-columns:repeat(3,1fr);gap:6px}
.tri small,.duo small{display:block;font-size:11px;font-weight:500;color:#41504A}
.tri b,.duo b{display:block;font-size:15px;font-weight:700;color:#2C332E;white-space:nowrap}
.duo{display:grid;grid-template-columns:1fr 1fr}
.pos{color:#2D8A5B!important}
.barra{height:10px;border-radius:5px;background:rgba(27,67,50,.18);overflow:hidden;margin-top:4px}
.barra i{display:block;height:100%;background:#1B4332;border-radius:5px}
.ola{font-size:24px;color:#2C332E;padding:2px 2px 0}
.legm{display:flex;flex-direction:column;gap:7px;margin-top:10px}
.legm div{display:flex;align-items:center;gap:8px;font-size:14px;color:#2C332E}
.legm i{width:12px;height:12px;border-radius:3px;flex:none}
.legm span{flex:1}
.legm em{font-style:normal;font-size:11px;color:#41504A;width:28px;text-align:right}
.big{font-size:24px;font-weight:700;color:#2C332E}
.obtn{border:1px solid #8A9A91;border-radius:20px;height:40px;display:flex;align-items:center;justify-content:center;font-size:14px;font-weight:600;color:#1B4332}
.item{display:flex;align-items:center;gap:8px;padding:7px 0;border-bottom:1px solid rgba(65,80,74,.18)}
.item:last-child{border-bottom:0}
.item div{flex:1}
.item b{font-size:14px;font-weight:400;color:#2C332E;display:block}
.item small{font-size:11px;color:#41504A}
.item strong{font-size:14px;color:#2C332E}
.lixo{width:20px;height:20px;color:#41504A;margin-left:6px;flex:none;display:block}
.dot{width:10px;height:10px;border-radius:50%}
.fbtn{display:inline-block;background:#1B4332;color:#fff;border-radius:20px;padding:9px 22px;font-size:14px;font-weight:600;margin-top:8px}
.fab{position:absolute;right:16px;bottom:92px;background:#CDE9DA;color:#1B4332;border-radius:16px;padding:16px 20px;font-weight:700;font-size:15px;box-shadow:0 3px 8px rgba(0,0,0,.25)}
.nav{position:absolute;left:0;right:0;bottom:0;height:78px;background:#EEF0E8;display:flex;justify-content:space-around;align-items:center;padding-bottom:4px}
.nav div{display:flex;flex-direction:column;align-items:center;font-size:12px;gap:4px;color:#41504A;font-weight:500}
.nav .ic{width:60px;height:30px;border-radius:15px;display:flex;align-items:center;justify-content:center}
.nav .on .ic{background:#DCEBE1;color:#1B4332}
.nav .on{color:#2C332E;font-weight:700}

/* 7 tecnologia */
.arq{display:flex;align-items:center;gap:14px;margin-top:50px}
.bx{padding:18px 20px;border-radius:16px;background:var(--papel);box-shadow:0 1px 2px rgba(31,42,36,.06),0 6px 18px rgba(31,42,36,.06);min-width:190px}
.bx small{display:block;font-size:12px;font-weight:700;letter-spacing:.1em;text-transform:uppercase;color:var(--tinta-2)}
.bx b{display:block;font-size:21px;color:var(--verde);margin-top:4px}
.bx.int{background:var(--verde);color:#fff}.bx.int small{color:var(--verde-claro)}.bx.int b{color:#fff}
.seta{flex:none;width:40px;height:3px;background:var(--verde-claro);position:relative}
.seta::after{content:"";position:absolute;right:-1px;top:-6px;border-left:12px solid var(--verde-claro);border-top:7.5px solid transparent;border-bottom:7.5px solid transparent}
.impl{display:flex;flex-direction:column;gap:12px}
.impl .bx{min-width:250px;padding:13px 18px}
.impl .bx b{font-size:17px}
.impl .prox{background:transparent;box-shadow:none;border:2px dashed var(--verde-claro)}
.linha1{margin-top:20px;font-size:20px;color:var(--tinta)}
.linha1 b{color:var(--verde)}
.tiles{display:grid;grid-template-columns:repeat(4,1fr);gap:20px;margin-top:42px}
.tile{padding:22px 22px 20px;min-height:170px;border-top:5px solid var(--verde-claro)}
.tile h4{font-size:21px;color:var(--verde)}
.tile p{font-size:16px;line-height:1.42;margin-top:8px;color:var(--tinta-2)}

/* 8 concorrencia */
.conc{display:grid;grid-template-columns:600px 1fr;gap:44px;margin-top:26px}
.matriz{position:relative;width:600px;height:470px}
.mq{position:absolute;inset:34px 70px 34px 70px;border-radius:16px;background:var(--papel);box-shadow:0 1px 2px rgba(31,42,36,.06),0 6px 18px rgba(31,42,36,.06)}
.mq::before{content:"";position:absolute;left:50%;top:14px;bottom:14px;width:2px;background:var(--linha)}
.mq::after{content:"";position:absolute;top:50%;left:14px;right:14px;height:2px;background:var(--linha)}
.ex{position:absolute;font-size:13px;font-weight:700;letter-spacing:.1em;text-transform:uppercase;color:var(--tinta-2)}
.pt{position:absolute;transform:translate(-50%,-50%);display:flex;flex-direction:column;align-items:center;gap:6px;text-align:center;z-index:1}
.pt i{width:22px;height:22px;border-radius:50%;background:#A8B3AD;display:block}
.pt span{font-size:16px;font-weight:600;color:var(--tinta-2);line-height:1.2}
.pt.nos i{width:40px;height:40px;background:var(--verde);box-shadow:0 0 0 10px rgba(116,198,157,.35)}
.pt.nos span{font-size:20px;color:var(--verde);font-weight:800}
.lado h4{font-size:14px;font-weight:700;letter-spacing:.14em;text-transform:uppercase;color:var(--verde-2)}
.publico{padding:24px 26px;margin-top:12px}
.publico b{display:block;font-size:26px;color:var(--verde);line-height:1.2}
.publico p{font-size:17px;color:var(--tinta-2);margin-top:6px}
.porque{list-style:none;margin-top:12px;display:flex;flex-direction:column;gap:12px}
.porque li{font-size:19px;line-height:1.35;display:flex;gap:12px}
.porque li::before{content:"";width:22px;height:22px;flex:none;border-radius:50%;background:var(--verde) url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath d='M6 12.5l4 4 8-9' fill='none' stroke='white' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E") center/16px no-repeat;margin-top:2px}

/* 9 modelo */
.planos{display:grid;grid-template-columns:1fr 1.08fr 1fr;gap:24px;margin-top:46px;align-items:stretch}
.plano{padding:26px 28px}
.plano small{font-size:13px;font-weight:800;letter-spacing:.14em;text-transform:uppercase;color:var(--verde-2)}
.plano .preco{font-family:"Segoe UI Variable Display","Segoe UI",sans-serif;font-size:52px;font-weight:800;letter-spacing:-.02em;color:var(--verde);margin-top:6px;line-height:1}
.plano .preco span{font-size:19px;font-weight:600;letter-spacing:0}
.plano .obs{font-size:15px;color:var(--tinta-2);margin-top:6px}
.plano ul{list-style:none;margin-top:18px;display:flex;flex-direction:column;gap:9px}
.plano li{font-size:17px;line-height:1.35;padding-left:20px;position:relative}
.plano li::before{content:"";position:absolute;left:0;top:8px;width:9px;height:9px;border-radius:50%;background:var(--verde-claro)}
.plano.destaque{background:var(--verde);color:#fff;transform:translateY(-8px);box-shadow:0 18px 36px rgba(14,42,31,.25)}
.plano.destaque small{color:var(--verde-claro)}.plano.destaque .preco{color:#fff}.plano.destaque .obs{color:#CDE9DA}
.metas12{display:grid;grid-template-columns:1.5fr 1fr;gap:20px;margin-top:38px}
.metas12 div{border-radius:14px;padding:16px 20px;font-size:18px;line-height:1.35}
.metas12 .m{background:#FFF3D1;border:1.5px solid var(--ouro)}
.metas12 .m b{color:#7A5A00}
.metas12 .r{background:var(--cartao);color:var(--verde);font-weight:600}

/* 10 roadmap */
.road{display:grid;grid-template-columns:repeat(4,1fr);gap:18px;margin-top:50px;position:relative}
.road::before{content:"";position:absolute;left:30px;right:calc(25% - 36px);top:21px;height:4px;background:var(--linha);border-radius:2px}
.et{position:relative}
.et .bola{width:46px;height:46px;border-radius:50%;background:var(--papel);border:4px solid var(--linha);display:flex;align-items:center;justify-content:center;font-weight:800;font-size:18px;color:var(--tinta-2);position:relative}
.et.feito .bola{background:var(--verde);border-color:var(--verde);color:#fff}
.et .ver{font-size:13px;font-weight:800;letter-spacing:.12em;text-transform:uppercase;color:var(--verde-2);margin-top:14px}
.et h4{font-size:22px;color:var(--verde);margin-top:4px;line-height:1.2}
.et p{font-size:16px;color:var(--tinta-2);margin-top:6px;line-height:1.4}
.uso{margin-top:58px}
.uso h4{font-size:14px;font-weight:700;letter-spacing:.14em;text-transform:uppercase;color:var(--verde-2)}
.pilha{display:flex;height:58px;border-radius:14px;overflow:hidden;margin-top:12px}
.pilha div{display:flex;align-items:center;padding:0 18px;color:#fff;font-size:19px;font-weight:700;gap:10px}
.pilha div span{font-weight:400;opacity:.9;font-size:16px}
.usoleg{display:flex;margin-top:10px}
.usoleg div{font-size:15px;color:var(--tinta-2);padding-right:16px;line-height:1.35}

/* 11 pedido */
.pedido{display:flex;flex-direction:column;align-items:center;justify-content:center;text-align:center;height:670px;padding-bottom:20px}
.valor{font-family:"Segoe UI Variable Display","Segoe UI",sans-serif;font-size:150px;line-height:.95;font-weight:800;letter-spacing:-.04em;color:var(--ouro);margin-top:18px}
.por{font-size:38px;font-weight:600;color:#fff;margin-top:10px}
.valuation{font-size:17px;color:rgba(243,237,227,.7);margin-top:10px;letter-spacing:.04em}
.promessa{font-size:25px;line-height:1.4;color:#E4EFE7;max-width:900px;margin-top:34px}
.quem{font-family:"Segoe UI Variable Display","Segoe UI",sans-serif;font-size:44px;font-weight:800;color:#fff;margin-top:30px}
.equipe{display:flex;gap:28px;margin-top:30px;font-size:16px;color:rgba(243,237,227,.85)}
.equipe span{display:inline-flex;align-items:center;gap:8px}
.equipe i{width:10px;height:10px;border-radius:50%;display:inline-block}
`;

const LIXO = '<svg class="lixo" viewBox="0 0 24 24"><path d="M9 3h6l1 2h4v2H4V5h4z" fill="currentColor"/><path d="M6.2 8.5h11.6l-.9 12.5H7.1z" fill="none" stroke="currentColor" stroke-width="1.9" stroke-linejoin="round"/><path d="M10 11.5v6.5M14 11.5v6.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"/></svg>';

const ICONES = {
  gastos: '<svg viewBox="0 0 24 24" width="22" height="22"><path d="M6 2.5h12v19l-3-2-3 2-3-2-3 2z" fill="none" stroke="currentColor" stroke-width="1.9" stroke-linejoin="round"/><path d="M9 8h6M9 12h6M9 16h4" stroke="currentColor" stroke-width="1.9" stroke-linecap="round"/></svg>',
  dashboard: '<svg viewBox="0 0 24 24" width="22" height="22"><path d="M11 3a9 9 0 1 0 9.9 10H11z" fill="currentColor"/><path d="M13 2v9h9a9 9 0 0 0-9-9z" fill="currentColor" opacity=".55"/></svg>',
  metas: '<svg viewBox="0 0 24 24" width="22" height="22"><ellipse cx="12" cy="13" rx="8.5" ry="6.5" fill="currentColor"/><rect x="7.5" y="17" width="2.6" height="4.5" rx="1.2" fill="currentColor"/><rect x="13.9" y="17" width="2.6" height="4.5" rx="1.2" fill="currentColor"/><rect x="10" y="3.5" width="4" height="4" rx="2" fill="currentColor"/></svg>',
  perfil: '<svg viewBox="0 0 24 24" width="22" height="22"><circle cx="12" cy="8" r="4.2" fill="currentColor"/><path d="M3.8 21c0-4.5 3.7-7.2 8.2-7.2s8.2 2.7 8.2 7.2z" fill="currentColor"/></svg>',
};

function fone(titulo, ativo, corpo, fab) {
  const itens = [['gastos', 'Gastos'], ['dashboard', 'Dashboard'], ['metas', 'Metas'], ['perfil', 'Perfil']];
  return `<div class="fone"><div class="tela">
    <div class="sb"><span>9:41</span><span class="ic"><i style="width:14px;height:9px"></i><i style="width:4px;height:9px"></i><i style="width:18px;height:9px"></i></span></div>
    <div class="tab">${titulo}</div>
    <div class="cont">${corpo}</div>
    ${fab ? `<div class="fab">+&nbsp;&nbsp;${fab}</div>` : ''}
    <div class="nav">${itens.map(([k, r]) => `<div class="${k === ativo ? 'on' : ''}"><span class="ic">${ICONES[k]}</span>${r}</div>`).join('')}</div>
  </div></div>`;
}

// Dados de exemplo da Ana (os mesmos que o Lucas prepara no emulador).
const ANA = { salario: 2500, fixos: 1150, variaveis: 620, invest: 250 };
const ANA_TOTAL = ANA.fixos + ANA.variaveis + ANA.invest; // 2020
const brl = (v) => 'R$ ' + v.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
const pct = (f) => Math.round(f * 100) + '%';

const TELA_GASTOS = fone('Meus gastos', 'gastos', `
  <div class="mc"><h5>Total gasto</h5><div class="big">${brl(ANA_TOTAL)}</div><p style="font-size:12px;margin-top:2px">${pct(ANA_TOTAL / ANA.salario)} do seu salário de ${brl(ANA.salario)}</p></div>
  <div class="obtn">+&nbsp;&nbsp;Nova subdivisão</div>
  <div class="mc"><h5><span>Fixos<small>${brl(ANA.fixos)}</small></span><i class="dot" style="background:#3A6EA5"></i></h5>
    <div class="item"><div><b>Aluguel do apê</b><small>Aluguel • 03/11/2026</small></div><strong>R$ 900,00</strong>${LIXO}</div>
    <div class="item"><div><b>Plano do celular</b><small>Internet e telefone • 03/11/2026</small></div><strong>R$ 100,00</strong>${LIXO}</div>
    <div class="item"><div><b>Ônibus do mês</b><small>Transporte • 03/11/2026</small></div><strong>R$ 150,00</strong>${LIXO}</div>
  </div>
  <div class="mc"><h5><span>Variáveis<small>${brl(ANA.variaveis)}</small></span><i class="dot" style="background:#E0862B"></i></h5>
    <div class="item"><div><b>Mercado</b><small>Alimentação • 03/11/2026</small></div><strong>R$ 450,00</strong>${LIXO}</div>
  </div>`, 'Gasto');

const TELA_DASH = fone('Dashboard', 'dashboard', `
  <div class="ola">Olá, Ana!</div>
  <div class="mc"><h5>Resumo do mês</h5><div class="tri">
    <div><small>Salário</small><b>${brl(ANA.salario)}</b></div>
    <div><small>Total gasto</small><b>${brl(ANA_TOTAL)}</b></div>
    <div><small>Saldo</small><b class="pos">${brl(ANA.salario - ANA_TOTAL)}</b></div></div></div>
  <div class="mc"><h5>Quanto do salário já foi comprometido</h5><div class="barra"><i style="width:${pct(ANA_TOTAL / ANA.salario)}"></i></div><p>${pct(ANA_TOTAL / ANA.salario)} do salário</p></div>
  <div class="mc"><h5>Gastos por categoria</h5>
    <div style="display:flex;justify-content:center">${donut({ tam: 168, esp: 32, partes: [{ v: ANA.fixos, cor: '#3A6EA5' }, { v: ANA.variaveis, cor: '#E0862B' }, { v: ANA.invest, cor: '#7B5EA7' }], centro: brl(ANA_TOTAL), rotulo: 'Total', fonteCentro: 16, fonteRotulo: 11, corCentro: '#2C332E', corRotulo: '#41504A' })}</div>
    <div class="legm">
      <div><i style="background:#3A6EA5"></i><span>Fixos</span><b>${brl(ANA.fixos)}</b><em>${pct(ANA.fixos / ANA_TOTAL)}</em></div>
      <div><i style="background:#E0862B"></i><span>Variáveis</span><b>${brl(ANA.variaveis)}</b><em>${pct(ANA.variaveis / ANA_TOTAL)}</em></div>
      <div><i style="background:#7B5EA7"></i><span>Investimentos</span><b>${brl(ANA.invest)}</b><em>${pct(ANA.invest / ANA_TOTAL)}</em></div>
    </div></div>`);

const TELA_METAS = fone('Minhas metas', 'metas', `
  <div class="mc"><h5>Cofrinho geral</h5><div class="duo"><div><small>Guardado</small><b>R$ 1.800,00</b></div><div><small>Objetivo</small><b>R$ 7.000,00</b></div></div><div class="barra" style="margin-top:12px"><i style="width:26%"></i></div></div>
  <div class="mc"><h5>Viagem para a praia${LIXO}</h5><p style="margin-top:0;color:#2C332E">R$ 1.200,00 de R$ 3.000,00</p><div class="barra" style="margin-top:8px"><i style="width:40%"></i></div><p style="font-size:12px;margin-top:4px">40%</p><p style="color:#2C332E;margin-top:2px">Faltam R$ 1.800,00</p><span class="fbtn">Guardar</span></div>
  <div class="mc"><h5>Notebook novo${LIXO}</h5><p style="margin-top:0;color:#2C332E">R$ 600,00 de R$ 4.000,00</p><div class="barra" style="margin-top:8px"><i style="width:15%"></i></div><p style="font-size:12px;margin-top:4px">15%</p><p style="color:#2C332E;margin-top:2px">Faltam R$ 3.400,00</p><span class="fbtn">Guardar</span></div>`, 'Nova meta');

function rodape(n, quem) {
  const p = pessoa(quem);
  return `<div class="rodape"><span>Calculadora de Gastos · Pitch</span><span class="esp"></span><span class="chip"><i style="background:${p.cor}"></i>${esc(p.curto)}</span><span>${n} / 11</span></div>`;
}

const SLIDES = [
  // 1 — capa
  `<section class="slide escuro capa">
    <div>
      <div class="k">Pitch · Desenvolvimento Mobile</div>
      <h1 class="capa-t">Calculadora<br>de Gastos</h1>
      <p class="capa-sub">Descubra para onde vai o seu salário — em 3 caixinhas.</p>
      <div class="pedido-chip"><span>O pedido</span><b>R$ 150 mil por 10% da empresa</b></div>
      <div class="nomes">${[EQUIPE.gabriel, EQUIPE.lucas, EQUIPE.m3].map((m) => esc(m.nome)).join('&nbsp;&nbsp;·&nbsp;&nbsp;')}</div>
    </div>
    <div class="capa-graf">
      ${donut({ tam: 400, esp: 58, respiro: 1.2, partes: [{ v: ANA.fixos, cor: '#3A6EA5' }, { v: ANA.variaveis, cor: '#E0862B' }, { v: ANA.invest, cor: '#7B5EA7' }, { v: ANA.salario - ANA_TOTAL, cor: 'rgba(255,255,255,.14)' }], centro: pct(ANA_TOTAL / ANA.salario), rotulo: 'do salário já<br>comprometido', fonteCentro: 92, fonteRotulo: 19, corCentro: '#fff', corRotulo: '#CDE9DA' })}
      <div class="leg"><span><i style="background:#3A6EA5"></i>Fixos</span><span><i style="background:#E0862B"></i>Variáveis</span><span><i style="background:#7B5EA7"></i>Investimentos</span><span><i style="background:rgba(255,255,255,.25)"></i>Livre</span></div>
    </div>
    ${rodape(1, 'gabriel')}
  </section>`,

  // 2 — problema
  `<section class="slide">
    <div class="k">O problema</div>
    <h1 class="t">O salário cai na conta… e some.</h1>
    <div class="stats">
      <div class="stat papel"><div class="n">82,8 mi</div><div class="d">de brasileiros inadimplentes</div><div class="f">Serasa · mar/2026</div></div>
      <div class="stat papel"><div class="n">49,9%</div><div class="d">dos adultos com o nome negativado</div><div class="f">Serasa · fev/2026</div></div>
      <div class="stat papel"><div class="n">82%</div><div class="d">das famílias têm alguma dívida — recorde da série</div><div class="f">CNC / Peic · ago/2026</div></div>
    </div>
    <p class="frase">O problema não é só ganhar pouco. <b>É não enxergar para onde o dinheiro vai.</b></p>
    <div class="fontes">Fontes: Serasa, Mapa da Inadimplência (fev. e mar./2026); CNC, Pesquisa de Endividamento e Inadimplência do Consumidor — Peic (ago./2026).</div>
    ${rodape(2, 'gabriel')}
  </section>`,

  // 3 — persona
  `<section class="slide">
    <div class="k">Quem sente isso</div>
    <h1 class="t">A Ana não é irresponsável. Ela só não tem um mapa.</h1>
    <div class="persona">
      <div class="ana papel">
        <div class="ana-top"><div class="avatar">A</div><div><h3>Ana, 24 anos</h3><div class="meta">Primeiro emprego · salário de R$ 2.500</div></div></div>
        <div class="tags"><span>Aluguel</span><span>Delivery</span><span>Pix aqui e ali</span><span>Sonha com a praia</span></div>
        <blockquote>“No dia 20 eu olho o saldo e não sei se foi o aluguel, o lazer ou as comprinhas.”</blockquote>
        <div class="ilustr">Persona ilustrativa</div>
      </div>
      <div class="perguntas">
        <h4>Ela precisa de 3 respostas:</h4>
        <ol>
          <li><b>1</b>Quanto do salário já comprometi?</li>
          <li><b>2</b>Com o quê?</li>
          <li><b>3</b>Quanto consigo guardar para a viagem?</li>
        </ol>
        <div class="hoje">
          <div><strong>Planilha</strong>Dá trabalho e é abandonada na 2ª semana.</div>
          <div><strong>App do banco</strong>Mostra o extrato, não um plano.</div>
        </div>
      </div>
    </div>
    ${rodape(3, 'gabriel')}
  </section>`,

  // 4 — solucao
  `<section class="slide">
    <div class="k">A solução</div>
    <h1 class="t">Em 1 minuto, o mapa do seu salário.</h1>
    <div class="passos">
      <div class="passo papel"><div class="num">1</div><h3>Salário</h3><p>Informe nome e salário: ele vira a base de todos os cálculos.</p></div>
      <div class="passo papel"><div class="num">2</div><h3>Gastos</h3><p>Lance cada gasto em Fixos, Variáveis ou Investimentos.</p></div>
      <div class="passo papel"><div class="num">3</div><h3>Dashboard</h3><p>Veja na hora quanto do salário já foi comprometido — e com o quê.</p></div>
      <div class="passo papel"><div class="num">4</div><h3>Metas</h3><p>Guarde dinheiro em cofrinhos com barra de progresso.</p></div>
    </div>
    <div class="faixa"><b>App Android · 6 telas:</b> Salário · Gastos · Dashboard · Metas · Detalhe da meta · Perfil</div>
    ${rodape(4, 'lucas')}
  </section>`,

  // 5 — divisoes
  `<section class="slide">
    <div class="k">Como funciona</div>
    <h1 class="t">Todo gasto cabe em uma de 3 caixinhas.</h1>
    <div class="divs">
      ${[
        ['Fixos', 'O que vem todo mês', '#3A6EA5', ['Aluguel', 'Contas de casa', 'Internet e telefone', 'Transporte', 'Educação']],
        ['Variáveis', 'O que muda de mês a mês', '#E0862B', ['Alimentação', 'Lazer', 'Saúde', 'Compras', 'Outros']],
        ['Investimentos', 'O dinheiro que trabalha para você', '#7B5EA7', ['Poupança', 'Renda fixa', 'Ações', 'Reserva de emergência']],
      ].map(([n, d, c, l]) => `<div class="div papel"><header style="background:${c}"><h3>${n}</h3><p>${d}</p></header><ul>${l.map((x) => `<li><i style="background:${c}"></i>${x}</li>`).join('')}</ul></div>`).join('')}
    </div>
    <div class="nota"><b>+ Subdivisões suas:</b> crie “Academia”, “Cripto”, “Pet”… e elas aparecem em todas as telas e gráficos.</div>
    ${rodape(5, 'lucas')}
  </section>`,

  // 6 — demo
  `<section class="slide">
    <div class="demo">
      <div class="demo-txt">
        <div class="k">Demo</div>
        <h1 class="t">O app funcionando</h1>
        <ul class="pontos">
          <li><b>81% do salário</b> comprometido; se passar de 100%, alerta em vermelho.</li>
          <li><b>Fixos são 57%</b> dos gastos da Ana: o gráfico mostra onde apertar.</li>
          <li><b>Viagem para a praia: 40%</b> guardado, com histórico de cada depósito.</li>
        </ul>
        <div class="aovivo"><span>Ao vivo</span>Lançar “Cinema · R$ 40” em Lazer e ver o dashboard ir para 82% na hora.</div>
      </div>
      <div class="fones">
        <figure class="fone-wrap">${TELA_GASTOS}<figcaption>Gastos</figcaption></figure>
        <figure class="fone-wrap">${TELA_DASH}<figcaption>Dashboard</figcaption></figure>
        <figure class="fone-wrap">${TELA_METAS}<figcaption>Metas</figcaption></figure>
      </div>
    </div>
    ${rodape(6, 'lucas')}
  </section>`,

  // 7 — tecnologia
  `<section class="slide">
    <div class="k">Por dentro</div>
    <h1 class="t">Feito para crescer sem reescrever.</h1>
    <div class="arq">
      <div class="bx"><small>Telas</small><b>Jetpack Compose</b></div>
      <div class="seta"></div>
      <div class="bx"><small>Estado</small><b>ViewModel + StateFlow</b></div>
      <div class="seta"></div>
      <div class="bx int"><small>Interface</small><b>FinancasRepository</b></div>
      <div class="seta"></div>
      <div class="impl">
        <div class="bx"><small>Hoje</small><b>InMemoryFinancasRepository</b></div>
        <div class="bx prox"><small>Próximo</small><b>Room / Firebase</b></div>
      </div>
    </div>
    <p class="linha1"><b>Trocar o banco de dados = mudar 1 linha no AppContainer.</b> Nenhuma tela e nenhum ViewModel mudam.</p>
    <div class="tiles">
      <div class="tile papel"><h4>Kotlin + Compose</h4><p>Material 3 e interface 100% declarativa, em MVVM.</p></div>
      <div class="tile papel"><h4>Gráficos próprios</h4><p>Donut e barras desenhados com Canvas, sem biblioteca externa.</p></div>
      <div class="tile papel"><h4>Privacidade</h4><p>Sem login; foto pelo Photo Picker, sem permissão de armazenamento.</p></div>
      <div class="tile papel"><h4>Testes unitários</h4><p>Cálculos de metas, repositório e conversão de valores.</p></div>
    </div>
    ${rodape(7, 'lucas')}
  </section>`,

  // 8 — concorrencia
  `<section class="slide">
    <div class="k">Concorrência</div>
    <h1 class="t">O quadrante vazio é nosso.</h1>
    <div class="conc">
      <div class="matriz">
        <div class="mq"></div>
        <div class="ex" style="top:6px;left:0;right:0;text-align:center">Ajuda a planejar</div>
        <div class="ex" style="bottom:6px;left:0;right:0;text-align:center">Só registra</div>
        <div class="ex" style="left:0;top:50%;transform:translateY(-50%);width:64px;text-align:right">Simples</div>
        <div class="ex" style="right:0;top:50%;transform:translateY(-50%);width:64px">Trabalhoso</div>
        <div class="pt nos" style="left:29%;top:29%"><i></i><span>Calculadora<br>de Gastos</span></div>
        <div class="pt" style="left:72%;top:27%"><i></i><span>Apps completos<br>de finanças</span></div>
        <div class="pt" style="left:30%;top:74%"><i></i><span>App do banco</span></div>
        <div class="pt" style="left:71%;top:68%"><i></i><span>Planilha</span></div>
      </div>
      <div class="lado">
        <h4>Para quem</h4>
        <div class="publico papel"><b>Jovens no 1º emprego e universitários</b><p>Quem está formando o hábito financeiro agora.</p></div>
        <h4 style="margin-top:26px">Por que ganhamos</h4>
        <ul class="porque">
          <li>Começa em 1 minuto, sem conectar conta bancária.</li>
          <li>O salário é a base de tudo: % comprometido em tempo real.</li>
          <li>Gastos e metas no mesmo app, em 3 caixinhas.</li>
        </ul>
      </div>
    </div>
    ${rodape(8, 'm3')}
  </section>`,

  // 9 — modelo de negocio
  `<section class="slide">
    <div class="k">Modelo de negócio</div>
    <h1 class="t">Freemium: grátis para usar, pago para ir além.</h1>
    <div class="planos">
      <div class="plano papel"><small>Grátis</small><div class="preco">R$ 0</div><div class="obs">para sempre</div>
        <ul><li>Salário e perfil</li><li>3 divisões + subdivisões próprias</li><li>Dashboard com gráficos</li><li>Metas (cofrinhos)</li></ul></div>
      <div class="plano papel destaque"><small>Premium</small><div class="preco">R$ 9,90<span> /mês</span></div><div class="obs">ou R$ 79,90 por ano</div>
        <ul><li>Backup e sincronização na nuvem</li><li>Histórico mês a mês</li><li>Alertas de orçamento por divisão</li><li>Relatórios em PDF</li></ul></div>
      <div class="plano papel"><small>Empresas (B2B)</small><div class="preco">R$ 3<span> /colaborador</span></div><div class="obs">por mês</div>
        <ul><li>Benefício de saúde financeira</li><li>Premium para todo o time</li><li>Painel anônimo e agregado para o RH</li></ul></div>
    </div>
    <div class="metas12">
      <div class="m"><b>Meta em 12 meses:</b> 50 mil downloads × 3% de conversão = <b>1.500 assinantes ≈ R$ 14,9 mil por mês</b></div>
      <div class="r">Regra de ouro: nunca vendemos dados do usuário.</div>
    </div>
    ${rodape(9, 'm3')}
  </section>`,

  // 10 — roadmap e uso do investimento
  `<section class="slide">
    <div class="k">Roadmap</div>
    <h1 class="t">Onde estamos e para onde vai o dinheiro.</h1>
    <div class="road">
      <div class="et feito"><div class="bola">✓</div><div class="ver">Hoje</div><h4>MVP pronto</h4><p>6 telas, 3 divisões, metas, dashboard e perfil.</p></div>
      <div class="et"><div class="bola">1</div><div class="ver">v1.1</div><h4>Dados salvos no aparelho</h4><p>Room. Hoje os dados somem ao fechar o app.</p></div>
      <div class="et"><div class="bola">2</div><div class="ver">v1.2</div><h4>Histórico e alertas</h4><p>Mês a mês e aviso quando uma divisão estoura.</p></div>
      <div class="et"><div class="bola">3</div><div class="ver">v2.0</div><h4>Nuvem, iPhone e Open Finance</h4><p>Sincronização, iOS e importação de extrato.</p></div>
    </div>
    <div class="uso">
      <h4>Uso dos R$ 150 mil</h4>
      <div class="pilha">
        <div style="width:40%;background:var(--verde)">40% <span>R$ 60 mil</span></div>
        <div style="width:35%;background:var(--verde-2)">35% <span>R$ 52,5 mil</span></div>
        <div style="width:25%;background:#5E8C76">25% <span>R$ 37,5 mil</span></div>
      </div>
      <div class="usoleg">
        <div style="width:40%"><b>Produto</b> — dados salvos, nuvem e versão iPhone</div>
        <div style="width:35%"><b>Marketing</b> — criadores de finanças e universidades</div>
        <div style="width:25%"><b>Operação e LGPD</b> — infra, jurídico e privacidade</div>
      </div>
    </div>
    ${rodape(10, 'm3')}
  </section>`,

  // 11 — o pedido
  `<section class="slide escuro">
    <div class="pedido">
      <div class="k">O pedido</div>
      <div class="valor">R$ 150 mil</div>
      <div class="por">por 10% da Calculadora de Gastos</div>
      <div class="valuation">VALUATION PROPOSTO: R$ 1,5 MILHÃO</div>
      <p class="promessa">Com vocês, este MVP vira o app que ajuda milhões de brasileiros a sair do vermelho.</p>
      <div class="quem">Quem entra com a gente?</div>
      <div class="equipe">${[EQUIPE.gabriel, EQUIPE.lucas, EQUIPE.m3].map((m) => `<span><i style="background:${m.cor}"></i>${esc(m.nome)}</span>`).join('')}</div>
    </div>
    ${rodape(11, 'm3')}
  </section>`,
];

const htmlSlides = `<!doctype html><html lang="pt-BR"><head><meta charset="utf-8"><title>Calculadora de Gastos — Pitch</title>
<style>${TOKENS}${CSS_SLIDES}</style></head><body>${SLIDES.join('\n')}
<script>/* slides.html#3 mostra so o slide 3 (para conferir no navegador) */
const n = +location.hash.slice(1);
if (n) document.querySelectorAll('.slide').forEach((s, i) => { if (i + 1 !== n) s.style.display = 'none'; });</script></body></html>`;

// ---------------------------------------------------------------------------
// Documentos A4 (tempo para o professor e roteiros)
// ---------------------------------------------------------------------------

const CSS_A4 = `
@page{size:A4;margin:14mm 15mm 15mm}
html,body{background:#fff}
body{font-size:10pt;line-height:1.45}
h1{font-family:"Segoe UI Variable Display","Segoe UI",sans-serif;font-size:24pt;line-height:1.1;letter-spacing:-.01em;color:var(--verde)}
h2{font-size:12.5pt;color:var(--verde);margin:11px 0 5px;display:flex;align-items:center;gap:8px}
h2::before{content:"";width:4px;height:15px;border-radius:2px;background:var(--verde-claro)}
.k{font-size:8pt;font-weight:700;letter-spacing:.16em;text-transform:uppercase;color:var(--verde-2)}
.cab{display:flex;justify-content:space-between;align-items:flex-end;gap:20px;padding-bottom:10px;border-bottom:2px solid var(--verde)}
.cab .dir{text-align:right;font-size:9pt;color:var(--tinta-2)}
.cab .dir b{color:var(--tinta)}
.caixas{display:grid;grid-template-columns:1.6fr 1fr;gap:12px;margin-top:14px}
.cx{border-radius:10px;padding:10px 13px;background:#F6F2EA}
.cx small{display:block;font-size:7.5pt;font-weight:700;letter-spacing:.14em;text-transform:uppercase;color:var(--verde-2);margin-bottom:3px}
.cx.ouro{background:#FFF3D1;border:1px solid var(--ouro)}
.cx.ouro b{font-size:13pt;color:#6B4F00}
/* linha do tempo */
.ldt{margin-top:8px}
.ldt .seg{display:flex;height:34px;border-radius:8px;overflow:hidden}
.ldt .seg div{display:flex;flex-direction:column;justify-content:center;padding:0 8px;color:#fff;font-size:8pt;line-height:1.15;white-space:nowrap;overflow:hidden}
.ldt .seg div b{font-size:8.8pt}
.ldt .seg .apagado{opacity:.28}
.ldt .marcas{position:relative;height:16px;margin-top:3px}
.ldt .marcas span{position:absolute;white-space:nowrap;transform:translateX(-50%);font-size:7.5pt;color:var(--tinta-2)}
/* tabela */
table{width:100%;border-collapse:collapse;font-size:9pt}
th{font-size:7.5pt;font-weight:700;letter-spacing:.1em;text-transform:uppercase;color:var(--tinta-2);text-align:left;padding:5px 6px;border-bottom:1.5px solid var(--verde)}
td{padding:3px 6px;border-bottom:1px solid #E6E0D5;vertical-align:top}
td.num{white-space:nowrap;font-variant-numeric:tabular-nums}
.quem{display:inline-flex;align-items:center;gap:6px;font-weight:600;white-space:nowrap}
.quem i{width:8px;height:8px;border-radius:50%;display:inline-block}
.check{width:13px;height:13px;border:1.4px solid var(--tinta-2);border-radius:3px;display:inline-block}
tr.qa td{background:#EEF6F0}
.combinados{display:grid;grid-template-columns:repeat(3,1fr);gap:10px}
.combinados div{border:1px solid #E6E0D5;border-radius:9px;padding:8px 11px;font-size:8.8pt}
.combinados b{display:block;color:var(--verde);font-size:9pt}
.notas{margin-top:6px}
.notas div{height:21px;border-bottom:1px solid #CFC7B8}
/* roteiro */
.faixa-membro{display:flex;gap:10px;margin-top:12px;flex-wrap:wrap}
.faixa-membro span{background:#F6F2EA;border-radius:999px;padding:4px 11px;font-size:9pt}
.faixa-membro b{color:var(--verde)}
.leg-fala{margin-left:auto;font-size:8pt;font-weight:400;color:var(--tinta-2)}
.leg-fala .cena{font-size:8pt}
.leg-fala strong{color:var(--verde)}
.bloco{display:grid;grid-template-columns:88px 1fr;gap:12px;padding:8px 0;border-bottom:1px solid #E6E0D5;break-inside:avoid}
.bloco .tempo{font-variant-numeric:tabular-nums}
.bloco .tempo b{display:block;font-size:13pt;color:var(--verde)}
.bloco .tempo span{display:block;font-size:8pt;color:var(--tinta-2)}
.bloco .tempo em{display:inline-block;margin-top:6px;font-style:normal;font-size:7.8pt;font-weight:700;letter-spacing:.06em;text-transform:uppercase;background:var(--verde);color:#fff;border-radius:5px;padding:2px 7px}
.bloco h3{font-size:11pt;color:var(--tinta);margin-bottom:4px}
.bloco p{font-size:11pt;line-height:1.5;margin-top:3px}
.bloco p strong{color:var(--verde)}
.cena{color:#9A5B12;font-style:italic;font-size:10pt}
.transicao{margin-top:8px;border-left:4px solid var(--ouro);background:#FFF8E6;padding:6px 10px;font-size:11pt;border-radius:0 8px 8px 0}
.transicao small{display:block;font-size:7.5pt;font-weight:700;letter-spacing:.12em;text-transform:uppercase;color:#7A5A00}
.cola{display:grid;grid-template-columns:1fr 1fr;gap:10px;border:1.5px dashed var(--verde-claro);border-radius:12px;padding:12px 14px;break-inside:avoid}
.cola div b{font-size:9pt;color:var(--verde)}
.cola ul{margin:3px 0 0 15px;font-size:9.5pt}
.qa-item{padding:8px 0;border-bottom:1px solid #E6E0D5;break-inside:avoid}
.qa-item b{display:block;font-size:10.5pt}
.qa-item p{font-size:10pt;color:var(--tinta-2);margin-top:2px}
.lista{margin:0 0 0 18px}
.lista li{margin:3px 0}
.regra{background:#EEF6F0;border-radius:10px;padding:9px 13px;font-size:9.5pt;margin-top:8px}
`;

function linhaDoTempo(destaque) {
  const segs = [
    { k: 'gabriel', ...faixaDoMembro('gabriel') },
    { k: 'lucas', ...faixaDoMembro('lucas') },
    { k: 'm3', ...faixaDoMembro('m3') },
    { k: 'todos', ini: PITCH_SEG, fim: TOTAL_SEG },
  ];
  const marcas = Array.from({ length: TOTAL_SEG / 60 + 1 }, (_, i) => `<span style="left:${(i * 60 / TOTAL_SEG) * 100}%;transform:translateX(${i === 0 ? '0' : i === TOTAL_SEG / 60 ? '-100%' : '-50%'})">${i} min</span>`).join('');
  return `<div class="ldt"><div class="seg">${segs.map((s) => {
    const p = pessoa(s.k);
    const nome = s.k === 'todos' ? 'Perguntas · todos' : p.curto;
    return `<div class="${destaque && destaque !== s.k ? 'apagado' : ''}" style="width:${((s.fim - s.ini) / TOTAL_SEG) * 100}%;background:${p.cor}"><b>${esc(nome)}</b>${mmss(s.ini)}–${mmss(s.fim)}</div>`;
  }).join('')}</div><div class="marcas">${marcas}</div></div>`;
}

function docA4(titulo, rodapeTxt, corpo) {
  const css = CSS_A4.replace('@page{size:A4;margin:14mm 15mm 15mm}',
    `@page{size:A4;margin:14mm 15mm 15mm;@bottom-left{content:"${rodapeTxt}";font:7.5pt "Segoe UI";color:#7A857F}@bottom-right{content:"Página " counter(page) " de " counter(pages);font:7.5pt "Segoe UI";color:#7A857F}}`);
  return `<!doctype html><html lang="pt-BR"><head><meta charset="utf-8"><title>${esc(titulo)}</title><style>${TOKENS}${css}</style></head><body>${corpo}</body></html>`;
}

// Tempo da apresentacao (folha pratica para o professor)
function htmlTempo() {
  const membros = ['gabriel', 'lucas', 'm3'].map((k) => {
    const f = faixaDoMembro(k);
    return `<tr><td><span class="quem"><i style="background:${EQUIPE[k].cor}"></i>${esc(EQUIPE[k].nome)}</span></td><td>${esc(EQUIPE[k].papel)}</td><td class="num">${mmss(f.ini)}–${mmss(f.fim)}</td><td class="num">${dur(f.fim - f.ini)}</td><td class="num">${f.slides[0]}–${f.slides[1]}</td></tr>`;
  }).join('');
  const linhas = BLOCOS.map((b, i) => {
    const p = pessoa(b.quem);
    return `<tr class="${b.quem === 'todos' ? 'qa' : ''}"><td class="num">${i + 1}</td><td class="num">${mmss(b.ini)}–${mmss(b.fim)}</td><td class="num">${dur(b.fim - b.ini)}</td><td><span class="quem"><i style="background:${p.cor}"></i>${esc(p.curto)}</span></td><td class="num">${b.quem === 'todos' ? '11' : b.slide}</td><td>${esc(b.titulo)}</td><td><span class="check"></span></td></tr>`;
  }).join('');
  const corpo = `
  <div class="cab">
    <div><div class="k">Tempo da apresentação · pitch “Shark Tank”</div><h1>Calculadora de Gastos</h1></div>
    <div class="dir">Desenvolvimento Mobile<br><b>Total: 10 min</b> = 7 de pitch + 3 de perguntas<br>Data: ____/____/______</div>
  </div>
  <div class="caixas">
    <div class="cx"><small>A ideia em 1 frase</small>App Android que mostra, em tempo real, quanto do salário já foi comprometido — separando os gastos em Fixos, Variáveis e Investimentos — e ajuda a guardar dinheiro em metas.</div>
    <div class="cx ouro"><small>O pedido aos sharks</small><b>R$ 150 mil por 10%</b><br>valuation proposto: R$ 1,5 milhão</div>
  </div>
  <h2>Linha do tempo</h2>
  ${linhaDoTempo()}
  <h2>Quem fala o quê</h2>
  <table><thead><tr><th>Integrante</th><th>Bloco</th><th>Quando</th><th>Duração</th><th>Slides</th></tr></thead><tbody>${membros}
  </tbody></table>
  <h2>Roteiro minuto a minuto</h2>
  <table><thead><tr><th>#</th><th>Quando</th><th>Dur.</th><th>Quem</th><th>Slide</th><th>Conteúdo</th><th>OK</th></tr></thead><tbody>${linhas}</tbody></table>
  <h2>Combinados da equipe</h2>
  <div class="combinados">
    <div><b>Cronômetro</b>${esc(N3)} controla o bloco de ${esc(N1)}; ${esc(N1)} controla o resto e sinaliza “1 minuto” aos 6:00.</div>
    <div><b>Plano B da demo</b>Se o emulador falhar, a demo segue pelo slide 6, que mostra as telas do app.</div>
    <div><b>Perguntas</b>Problema e usuário: ${esc(N1)} · produto e tecnologia: ${esc(N2)} · negócio e números: ${esc(N3)}.</div>
  </div>
  <h2>Anotações do avaliador</h2>
  <div class="notas"><div></div><div></div><div></div></div>`;
  return docA4('Tempo da apresentação', 'Calculadora de Gastos · Tempo da apresentação', corpo);
}

// Roteiro individual
function htmlRoteiro(k) {
  const m = EQUIPE[k];
  const f = faixaDoMembro(k);
  const meus = BLOCOS.filter((b) => b.quem === k);
  const ordem = ['gabriel', 'lucas', 'm3'];
  const i = ordem.indexOf(k);
  const antes = i > 0 ? EQUIPE[ordem[i - 1]] : null;
  const depois = i < ordem.length - 1 ? EQUIPE[ordem[i + 1]] : null;
  const blocos = meus.map((b) => `
    <div class="bloco">
      <div class="tempo"><b>${mmss(b.ini)}–${mmss(b.fim)}</b><span>${dur(b.fim - b.ini)}</span><em>Slide ${b.slide}</em></div>
      <div><h3>${esc(b.titulo)}</h3>${b.fala.map((p) => `<p>${fmt(p)}</p>`).join('')}
      ${b.transicao ? `<div class="transicao"><small>Passe a palavra</small>${fmt(b.transicao)}</div>` : ''}</div>
    </div>`).join('');
  const cola = meus.map((b) => `<div><b>Slide ${b.slide} · ${esc(b.titulo)}</b><ul>${b.cola.map((c) => `<li>${esc(c)}</li>`).join('')}</ul></div>`).join('');
  const qa = PERGUNTAS.filter((q) => q.dono === k).map((q) => `<div class="qa-item"><b>“${esc(q.p)}”</b><p>${esc(q.r)}</p></div>`).join('');
  const corpo = `
  <div class="cab">
    <div><div class="k">Roteiro de fala · bloco ${m.ordem} de 3</div><h1>${esc(m.nome)}</h1><div style="font-size:11pt;color:var(--tinta-2);margin-top:4px">${esc(m.papel)}</div></div>
    <div class="dir">Calculadora de Gastos<br>pitch “Shark Tank” · 10 min</div>
  </div>
  <div class="faixa-membro">
    <span>Você fala de <b>${mmss(f.ini)} a ${mmss(f.fim)}</b> · ${dur(f.fim - f.ini)}</span>
    <span>Slides <b>${f.slides[0]} a ${f.slides[1]}</b></span>
    ${antes ? `<span>Antes de você: <b>${esc(antes.curto)}</b></span>` : '<span>Você <b>abre</b> o pitch</span>'}
    ${depois ? `<span>Depois de você: <b>${esc(depois.curto)}</b></span>` : '<span>Você <b>fecha</b> o pitch</span>'}
  </div>
  <div style="margin-top:12px">${linhaDoTempo(k)}</div>
  <h2>Sua fala, slide a slide <small class="leg-fala"><span class="cena">[laranja]</span> = o que fazer, não leia em voz alta · <strong>negrito</strong> = enfatize</small></h2>
  ${blocos}
  <h2 style="break-before:page;margin-top:0">Cola de bolso (se der branco)</h2>
  <div class="cola">${cola}</div>
  <h2>Perguntas dos sharks que são suas (${mmss(PITCH_SEG)}–${mmss(TOTAL_SEG)})</h2>
  ${qa}
  <div class="regra"><b>Regra das perguntas:</b> quem é dono do tema responde; os outros só complementam com uma frase. Se ninguém souber: “Ótima pergunta — ainda não temos esse número, e é um dos primeiros que vamos medir.”</div>
  <h2>Antes de entrar</h2>
  <ul class="lista">${CHECKLIST[k].map((c) => `<li>${esc(c)}</li>`).join('')}</ul>`;
  return docA4(`Roteiro — ${m.curto}`, `Calculadora de Gastos · Roteiro de ${m.curto}`, corpo);
}

// ---------------------------------------------------------------------------
// Conferencias de coerencia (falham o script se algo nao bater)
// ---------------------------------------------------------------------------

function conferir() {
  const erros = [];
  BLOCOS.forEach((b, i) => {
    if (i > 0 && BLOCOS[i - 1].fim !== b.ini) erros.push(`buraco/sobreposição entre os blocos ${i} e ${i + 1}`);
  });
  if (BLOCOS[0].ini !== 0) erros.push('o primeiro bloco não começa em 0:00');
  if (BLOCOS[BLOCOS.length - 1].fim !== TOTAL_SEG) erros.push('o último bloco não termina em 10:00');
  const fimPitch = BLOCOS.filter((b) => b.quem !== 'todos').at(-1).fim;
  if (fimPitch !== PITCH_SEG) erros.push(`o pitch termina em ${mmss(fimPitch)}, e não em ${mmss(PITCH_SEG)}`);
  const slidesFalados = new Set(BLOCOS.filter((b) => b.quem !== 'todos').map((b) => b.slide));
  if (slidesFalados.size !== SLIDES.length) erros.push(`${slidesFalados.size} slides com fala, mas existem ${SLIDES.length} slides`);
  // Ritmo de fala: ~120 a ~175 palavras por minuto e o que cabe confortavelmente.
  for (const b of BLOCOS.filter((x) => x.quem !== 'todos')) {
    const texto = [...b.fala, b.transicao ?? ''].join(' ').replace(/\[[^\]]+\]/g, '');
    const palavras = texto.split(/\s+/).filter(Boolean).length;
    const ppm = palavras / ((b.fim - b.ini) / 60);
    b.palavras = palavras;
    if (ppm > 175) erros.push(`bloco "${b.titulo}" com ${palavras} palavras para ${dur(b.fim - b.ini)} (${Math.round(ppm)} palavras/min): corte a fala`);
  }
  if (erros.length) {
    console.error('Incoerências encontradas:\n - ' + erros.join('\n - '));
    process.exit(1);
  }
  for (const k of ['gabriel', 'lucas', 'm3']) {
    const bs = BLOCOS.filter((b) => b.quem === k);
    const pal = bs.reduce((a, b) => a + b.palavras, 0);
    const seg = bs.at(-1).fim - bs[0].ini;
    console.log(`${EQUIPE[k].curto.padEnd(14)} ${dur(seg).padEnd(10)} ${pal} palavras (${Math.round(pal / (seg / 60))} por minuto)`);
  }
}

// ---------------------------------------------------------------------------
// Saida
// ---------------------------------------------------------------------------

function imprimir(nomeHtml, html, nomePdf) {
  const caminhoHtml = join(HTML_DIR, nomeHtml);
  writeFileSync(caminhoHtml, html, 'utf8');
  const pdf = join(SAIDA, nomePdf);
  execFileSync(EDGE, [
    '--headless=new', '--disable-gpu', '--no-first-run', '--no-default-browser-check',
    `--user-data-dir=${join(tmpdir(), 'pitch-calculadora-edge')}`,
    '--no-pdf-header-footer', '--virtual-time-budget=4000',
    `--print-to-pdf=${pdf}`, pathToFileURL(caminhoHtml).href,
  ], { stdio: 'pipe' });
  console.log('  gerado:', nomePdf);
}

conferir();
if (!EDGE) {
  console.error('Microsoft Edge não encontrado: não consigo imprimir os PDFs.');
  process.exit(1);
}
mkdirSync(HTML_DIR, { recursive: true });
// Remove roteiros antigos (ex.: depois de trocar o nome do 3o integrante).
for (const f of readdirSync(SAIDA)) {
  if (/^0[3-5]-Roteiro-.*\.pdf$/.test(f)) rmSync(join(SAIDA, f));
}
imprimir('slides.html', htmlSlides, '01-Slides-Pitch.pdf');
imprimir('tempo.html', htmlTempo(), '02-Tempo-Apresentacao.pdf');
for (const k of ['gabriel', 'lucas', 'm3']) {
  imprimir(`roteiro-${k}.html`, htmlRoteiro(k), arquivoRoteiro(k));
}
