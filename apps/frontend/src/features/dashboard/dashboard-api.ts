import { useQuery } from '@tanstack/react-query'
import { apiClient } from '@/shared/api/api-client'
import type { StudentSummary, StudySummary } from './types'

export function useDashboard() {
  const students = useQuery({ queryKey: ['estudantes'], queryFn: () => apiClient.get<StudentSummary[]>('/estudantes') })
  const studies = useQuery({ queryKey: ['estudos-caso'], queryFn: () => apiClient.get<StudySummary[]>('/estudos-caso') })
  return { students, studies }
}
