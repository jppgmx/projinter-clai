import { useEffect } from 'react'
import { useParams } from 'react-router-dom'

/** Mantém os endereços React anteriores apontando para os fluxos HTML existentes. */
export function ExistingPage({ to, student = false }: { to: string; student?: boolean }) {
  const { alunoId } = useParams()
  const target = student && alunoId ? `${to}?matricula=${encodeURIComponent(alunoId)}` : to
  useEffect(() => { window.location.replace(target) }, [target])
  return <p role="status">Abrindo página. <a href={target}>Continuar</a></p>
}
