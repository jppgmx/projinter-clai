import { Link } from 'react-router-dom'
import { Card } from '@/shared/components/card'

export function PlaceholderPage({ title }: { title: string }) {
  return <Card className="p-6"><h1 className="text-xl font-semibold text-gray-900">{title}</h1><p className="mt-2 text-sm text-gray-600">Este endereço não está disponível.</p><Link to="/" className="mt-4 inline-block text-sm text-indigo-700 underline">Voltar ao início</Link></Card>
}
