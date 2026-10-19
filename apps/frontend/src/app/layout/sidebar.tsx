import { LogOut, UserRound } from 'lucide-react'
import { NavLink } from 'react-router-dom'
import { useAuth } from '@/features/auth/use-auth'
import { Logo } from '@/shared/components/logo'
import { isNavGroup, navItems } from './nav-items'
import { SidebarGroup } from './sidebar-group'

export function Sidebar() {
  const { user, signOut } = useAuth()

  return (
    <aside className="flex w-64 shrink-0 flex-col bg-black text-gray-200">
      <div className="px-6 py-8">
        <Logo className="text-3xl text-white" />
      </div>

      <nav aria-label="Navegação principal" className="flex-1 px-3">
        <ul className="space-y-1">
          {navItems.map((item) =>
            isNavGroup(item) ? (
              <SidebarGroup key={item.label} group={item} />
            ) : (
              <li key={item.to}>
                <NavLink
                  to={item.to}
                  end={item.to === '/'}
                  className={({ isActive }) =>
                    `flex items-center gap-3 rounded-md px-3 py-2 text-sm hover:bg-white/10 ${
                      isActive ? 'bg-white/15 font-semibold text-white' : 'text-gray-200'
                    }`
                  }
                >
                  <item.icon aria-hidden="true" className="size-5 shrink-0" />
                  {item.label}
                </NavLink>
              </li>
            ),
          )}
        </ul>
      </nav>

      <div className="space-y-3 px-4 py-6">
        <div className="flex items-center gap-3">
          <span className="flex size-10 items-center justify-center rounded-full bg-gray-300 text-gray-700">
            <UserRound aria-hidden="true" className="size-6" />
          </span>
          <span className="truncate text-sm text-white">{user?.nome}</span>
        </div>
        <button
          type="button"
          onClick={signOut}
          className="flex items-center gap-2 rounded-md px-2 py-1.5 text-sm font-medium text-red-400 hover:bg-white/10"
        >
          <LogOut aria-hidden="true" className="size-4" />
          Logout
        </button>
      </div>
    </aside>
  )
}
