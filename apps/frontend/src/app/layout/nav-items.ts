import { ClipboardList, FileText, Home, UserPlus, Users, type LucideIcon } from 'lucide-react'

export interface NavLeaf { label: string; to: string; icon: LucideIcon }
export interface NavGroup { label: string; icon: LucideIcon; children: { label: string; to: string }[] }
export type NavItem = NavLeaf | NavGroup
export function isNavGroup(item: NavItem): item is NavGroup { return 'children' in item }

export const navItems: NavItem[] = [
  { label: 'Início', to: '/', icon: Home },
  { label: 'Consultar estudantes', to: '/estudantes.html', icon: Users },
  { label: 'Registrar estudante', to: '/cadastro-estudante.html', icon: UserPlus },
  { label: 'Estudos de caso', to: '/editar-estudo-caso.html', icon: FileText },
  { label: 'Abrir estudo de caso', to: '/abertura-estudo-caso.html', icon: ClipboardList },
]
