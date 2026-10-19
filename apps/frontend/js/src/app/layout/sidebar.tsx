import { NavLink } from 'react-router-dom'
import { Logo } from '@/shared/components/logo'
import { isNavGroup, navItems } from './nav-items'
import { SidebarGroup } from './sidebar-group'

export function Sidebar() {
  return (
    <aside className="flex w-64 shrink-0 flex-col bg-black text-gray-200 max-md:w-full">
      <div className="px-6 py-8"><Logo className="text-3xl text-white" /></div>
      <nav aria-label="Navegação principal" className="flex-1 px-3 pb-6">
        <ul className="space-y-1">
          {navItems.map(item => isNavGroup(item) ? <SidebarGroup key={item.label} group={item} /> :
            <li key={item.to}>{item.to.endsWith('.html') ?
              <a href={item.to} className="flex items-center gap-3 rounded-md px-3 py-2 text-sm text-gray-200 hover:bg-white/10"><item.icon aria-hidden="true" className="size-5 shrink-0" />{item.label}</a> :
              <NavLink to={item.to} end={item.to === '/'} className={({ isActive }) => `flex items-center gap-3 rounded-md px-3 py-2 text-sm hover:bg-white/10 ${isActive ? 'bg-white/15 font-semibold text-white' : 'text-gray-200'}`}><item.icon aria-hidden="true" className="size-5 shrink-0" />{item.label}</NavLink>
            }</li>)}
        </ul>
      </nav>
    </aside>
  )
}
