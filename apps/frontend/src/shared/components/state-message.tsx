interface StateMessageProps {
  kind: 'loading' | 'empty' | 'error'
  message?: string
}

const defaults = {
  loading: 'Carregando...',
  empty: 'Nada por aqui ainda.',
  error: 'Não foi possível carregar os dados.',
} as const

/** Estados de carregamento, vazio e erro (obrigatorios em toda tela que busca dados). */
export function StateMessage({ kind, message }: StateMessageProps) {
  return (
    <p
      role={kind === 'error' ? 'alert' : 'status'}
      className={`px-4 py-6 text-sm ${kind === 'error' ? 'text-red-700' : 'text-gray-600'}`}
    >
      {message ?? defaults[kind]}
    </p>
  )
}
