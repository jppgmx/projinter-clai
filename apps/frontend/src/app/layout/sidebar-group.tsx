import { ChevronDown } from 'lucide-react'
import { useId, useState } from 'react'
import { NavLink, useLocation } from 'react-router-dom'
import type { NavGroup } from './nav-items'

export function SidebarGroup({ group }: { group: NavGroup }) {
  const { pathname } = useLocation()
  const isInside = group.children.some((child) => pathname.startsWith(child.to))
  const [open, setOpen] = useState(isInside)
  const panelId = useId()
  const Icon = group.icon

  return (
    <li>
      <button
        type="button"
        aria-expanded={open}
        aria-controls={panelId}
        onClick={() => setOpen((value) => !value)}
        className={`flex w-full items-center gap-3 rounded-md px-3 py-2 text-left text-sm hover:bg-white/10 ${
          isInside ? 'bg-white/15 font-semibold text-white' : 'text-gray-200'
        }`}
      >
        <Icon aria-hidden="true" className="size-5 shrink-0" />
        <span className="flex-1">{group.label}</span>
        <ChevronDown aria-hidden="true" className={`size-4 transition-transform ${open ? 'rotate-180' : ''}`} />
      </button>
      <ul id={panelId} hidden={!open} className="mt-1 space-y-1 pl-11">
        {group.children.map((child) => (
          <li key={child.to}>
            <NavLink
              to={child.to}
              className={({ isActive }) =>
                `block rounded-md px-3 py-1.5 text-sm hover:bg-white/10 ${
                  isActive ? 'bg-white/15 font-semibold text-white' : 'text-gray-300'
                }`
              }
            >
              {child.label}
            </NavLink>
          </li>
        ))}
      </ul>
    </li>
  )
}
