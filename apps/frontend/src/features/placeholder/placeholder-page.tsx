import { Card } from '@/shared/components/card'

/** Tela ainda sem mockup ([ABERTO]). Troque pela tela real quando existir. */
export function PlaceholderPage({ title }: { title: string }) {
  return (
    <Card className="p-6">
      <h1 className="text-xl font-semibold text-gray-900">{title}</h1>
      <p className="mt-2 text-sm text-gray-600">Esta tela ainda não tem mockup definido.</p>
    </Card>
  )
}
