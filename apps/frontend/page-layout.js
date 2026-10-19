export const ICON_PATHS = {
  home: 'm3 10 9-7 9 7M5 9v11h14V9M9 20v-7h6v7',
  people: 'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2M16 3a4 4 0 0 1 0 8M22 21v-2a4 4 0 0 0-3-3.87M13 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0',
  add: 'M12 8v8M8 12h8M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0',
  folder: 'M3 7V5h6l2 2h10v13H3V7M3 10h18',
  clipboard: 'M9 4H5v17h14V4h-4M9 3h6v4H9zM8 12h8M8 16h5',
  archive: 'M3 4h18v4H3zM5 8v13h14V8M10 12h4',
  search: 'M21 21l-5-5M18 10a8 8 0 1 1-16 0 8 8 0 0 1 16 0',
  shield: 'M12 3 4 6v6c0 5 8 9 8 9s8-4 8-9V6l-8-3M9 12l2 2 4-4',
  menu: 'M4 6h16M4 12h16M4 18h16',
}

export const NAVIGATION = [
  { href: '/', label: 'Início', icon: 'home', page: 'inicio' },
  { href: 'estudantes.html', label: 'Consultar estudantes', icon: 'people', page: 'estudantes' },
  { href: 'cadastro-estudante.html', label: 'Registrar estudante', icon: 'add', page: 'cadastro' },
  { href: 'editar-estudo-caso.html', label: 'Estudos de caso', icon: 'folder', page: 'estudos' },
  { href: 'abertura-estudo-caso.html', label: 'Abrir estudo de caso', icon: 'clipboard', page: 'abertura' },
]

export function createIcon(name) {
  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg')
  svg.setAttribute('viewBox', '0 0 24 24')
  svg.setAttribute('class', 'ui-icon')
  svg.setAttribute('aria-hidden', 'true')
  const path = document.createElementNS(svg.namespaceURI, 'path')
  path.setAttribute('d', ICON_PATHS[name] || ICON_PATHS.folder)
  svg.append(path)
  return svg
}

function element(tag, className, text) {
  const node = document.createElement(tag)
  if (className) node.className = className
  if (text) node.textContent = text
  return node
}

export function mountShell(main, currentPage) {
  const skip = element('a', 'skip-link', 'Ir para o conteúdo')
  if (!main.id) main.id = 'conteudo-principal'
  skip.href = `#${main.id}`
  const shell = element('div', 'app-shell')
  const sidebar = element('aside', 'app-sidebar')
  sidebar.id = 'navegacao-principal'
  sidebar.setAttribute('aria-label', 'Menu principal')
  const brand = element('a', 'app-brand')
  brand.href = '/'
  brand.setAttribute('aria-label', 'CLAI — Início')
  const brandText = element('span', 'brand-mark', 'CLAI')
  brand.append(brandText)
  sidebar.append(brand)
  const nav = element('nav', 'app-nav')
  nav.setAttribute('aria-label', 'Navegação principal')
  NAVIGATION.forEach(item => {
    const link = element('a', 'nav-link')
    link.href = item.href
    if (currentPage === item.page || (currentPage === 'detalhes' && item.page === 'estudantes')) link.setAttribute('aria-current', 'page')
    link.append(createIcon(item.icon), element('span', '', item.label))
    nav.append(link)
  })
  sidebar.append(nav)
  const workspace = element('div', 'app-workspace')
  const topbar = element('header', 'app-topbar')
  const menu = element('button', 'mobile-menu')
  menu.type = 'button'
  menu.setAttribute('aria-label', 'Abrir menu de navegação')
  menu.setAttribute('aria-expanded', 'false')
  menu.setAttribute('aria-controls', sidebar.id)
  menu.append(createIcon('menu'))
  const breadcrumb = element('div', 'breadcrumbs')
  breadcrumb.append(element('span', '', 'CLAI'), element('span', '', '/'), element('strong', '', NAVIGATION.find(item => item.page === currentPage)?.label || 'Dados do estudante'))
  const meta = element('div', 'topbar-meta')
  meta.append(element('span', 'topbar-dot'), element('span', '', 'Acompanhamento inclusivo'), element('span', 'date-label', new Intl.DateTimeFormat('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date())))
  topbar.append(menu, breadcrumb, meta)
  const overlay = element('button', 'nav-overlay')
  overlay.type = 'button'
  overlay.setAttribute('aria-label', 'Fechar menu')
  const mobile = window.matchMedia('(max-width:760px)')
  const close = () => { document.body.classList.remove('sidebar-open'); menu.setAttribute('aria-expanded', 'false'); sidebar.inert = mobile.matches }
  close()
  menu.addEventListener('click', () => {
    const open = document.body.classList.toggle('sidebar-open')
    menu.setAttribute('aria-expanded', String(open))
    sidebar.inert = mobile.matches && !open
    if (open) brand.focus()
  })
  overlay.addEventListener('click', close)
  const onKeydown = event => {
    if (!document.body.classList.contains('sidebar-open')) return
    if (event.key === 'Escape') { close(); menu.focus() }
    if (event.key === 'Tab' && mobile.matches) {
      const links = sidebar.querySelectorAll('a,button')
      const first = links[0], last = links[links.length - 1]
      if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus() }
      if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus() }
    }
  }
  document.addEventListener('keydown', onKeydown)
  mobile.addEventListener('change', close)
  nav.addEventListener('click', close)
  const footer = element('footer', 'app-footer')
  const safety = element('div', 'footer-safety')
  safety.append(createIcon('shield'), element('span', '', 'Trate os dados dos estudantes com responsabilidade e compartilhe somente com a equipe responsável.'))
  const footerRow = element('div', 'footer-row')
  footerRow.append(element('span', '', 'CLAI · Sistema de Acompanhamento Inclusivo'), element('span', '', 'Inclusão se constrói em conjunto.'))
  footer.append(safety, footerRow)
  main.classList.add('app-main')
  workspace.append(topbar, main, footer)
  shell.append(sidebar, overlay, workspace)
  document.body.append(skip, shell)
  return () => { close(); document.removeEventListener('keydown', onKeydown); mobile.removeEventListener('change', close); skip.remove(); shell.remove() }
}

if (document.body.dataset.uiPage) {
  const main = document.querySelector('main')
  if (main) {
    document.querySelector('body > header')?.remove()
    mountShell(main, document.body.dataset.uiPage)
    main.querySelectorAll('table').forEach(table => {
      const scroll = element('div', 'table-scroll')
      scroll.setAttribute('role', 'region')
      scroll.setAttribute('aria-label', table.caption?.textContent || 'Tabela de dados')
      scroll.tabIndex = 0
      table.before(scroll)
      scroll.append(table)
    })
  }
}
