/** Perfis de acesso (secao 2). Permissoes exatas: [ABERTO]. */
export type Perfil = 'psicopedagogo' | 'assistente_inclusivo' | 'coordenacao' | 'administracao'

export interface AuthUser {
  nome: string
  perfil: Perfil
}

export interface LoginPayload {
  usuario: string
  senha: string
}
