import type { ReactNode } from 'react'

interface CardProps {
  children: ReactNode
  className?: string
  'aria-labelledby'?: string
}

export function Card({ children, className = '', 'aria-labelledby': labelledBy }: CardProps) {
  return (
    <section
      aria-labelledby={labelledBy}
      className={`rounded-xl bg-white shadow-sm ring-1 ring-black/5 ${className}`}
    >
      {children}
    </section>
  )
}
