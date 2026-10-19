import { AlertTriangle, Clock } from 'lucide-react'

interface StatusBadgeProps {
  variant: 'alert' | 'scheduled'
  label: string
}

const styles = {
  alert: 'bg-amber-100 text-amber-900',
  scheduled: 'bg-violet-100 text-violet-900',
} as const

/** Badge com icone + texto: o status nunca depende so da cor. */
export function StatusBadge({ variant, label }: StatusBadgeProps) {
  const Icon = variant === 'alert' ? AlertTriangle : Clock
  return (
    <span className={`inline-flex items-center gap-1 rounded px-2 py-0.5 text-xs font-medium ${styles[variant]}`}>
      <Icon aria-hidden="true" className="size-3" />
      {label}
    </span>
  )
}
