const dateFormatter = new Intl.DateTimeFormat('pt-BR', {
  day: '2-digit',
  month: '2-digit',
  year: 'numeric',
})

/** Formata como dd/mm/aaaa. */
export function formatDate(date: Date): string {
  return dateFormatter.format(date)
}
