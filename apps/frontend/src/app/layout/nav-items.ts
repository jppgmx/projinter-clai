import {
  ClipboardList,
  FileText,
  HeartHandshake,
  Home,
  Landmark,
  SlidersHorizontal,
  Users,
  type LucideIcon,
} from 'lucide-react'

export interface NavLeaf {
  label: string
  to: string
  icon: LucideIcon
}

export interface NavGroup {
  label: string
  icon: LucideIcon
  children: { label: string; to: string }[]
}

export type NavItem = NavLeaf | NavGroup

export function isNavGroup(item: NavItem): item is NavGroup {
  return 'children' in item
}

/**
 * Ordem da sidebar conforme o mockup.
 * [ABERTO] Os subitens de "Planos e Estudos de caso" foram assumidos.
 * [ABERTO] Visibilidade por perfil: por ora todos veem tudo (a regra real vem do back end).
 */
export const navItems: NavItem[] = [
  { label: 'Início', to: '/', icon: Home },
  { label: 'Alunos', to: '/alunos', icon: Users },
  { label: 'Atendimentos', to: '/atendimentos', icon: ClipboardList },
  {
    label: 'Planos e Estudos de caso',
    icon: FileText,
    children: [
      { label: 'Planos de ação', to: '/planos-estudos/planos' },
      { label: 'Estudos de caso', to: '/planos-estudos/estudos' },
    ],
  },
  { label: 'Alunos apoiadores', to: '/alunos-apoiadores', icon: HeartHandshake },
  { label: 'Demandas externas', to: '/demandas-externas', icon: Landmark },
  { label: 'Administração', to: '/administracao', icon: SlidersHorizontal },
]
