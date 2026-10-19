import { zodResolver } from '@hookform/resolvers/zod'
import { useForm } from 'react-hook-form'
import { Navigate, useNavigate } from 'react-router-dom'
import { ApiError } from '@/shared/api/api-error'
import { Logo } from '@/shared/components/logo'
import { loginSchema, type LoginFormValues } from './login-schema'
import { useAuth } from './use-auth'

const inputClasses =
  'mt-1 block w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-900 aria-[invalid=true]:border-red-600'

export function LoginPage() {
  const { user, signIn } = useAuth()
  const navigate = useNavigate()
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({ resolver: zodResolver(loginSchema) })

  if (user) return <Navigate to="/" replace />

  async function onSubmit(values: LoginFormValues) {
    try {
      await signIn(values)
      navigate('/', { replace: true })
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Não foi possível entrar.'
      setError('root', { message })
    }
  }

  return (
    <main className="relative flex min-h-screen flex-col items-center justify-center overflow-hidden bg-white px-4">
      <Logo className="text-5xl text-black" />

      <form onSubmit={handleSubmit(onSubmit)} noValidate className="mt-16 w-full max-w-xs">
        <div>
          <label htmlFor="usuario" className="block text-sm text-gray-700">
            Usuário
          </label>
          <input
            id="usuario"
            type="text"
            autoComplete="username"
            aria-invalid={errors.usuario ? 'true' : 'false'}
            aria-describedby={errors.usuario ? 'usuario-erro' : undefined}
            className={inputClasses}
            {...register('usuario')}
          />
          {errors.usuario && (
            <p id="usuario-erro" className="mt-1 text-xs text-red-700">
              {errors.usuario.message}
            </p>
          )}
        </div>

        <div className="mt-4">
          <label htmlFor="senha" className="block text-sm text-gray-700">
            Senha
          </label>
          <input
            id="senha"
            type="password"
            autoComplete="current-password"
            aria-invalid={errors.senha ? 'true' : 'false'}
            aria-describedby={errors.senha ? 'senha-erro' : undefined}
            className={inputClasses}
            {...register('senha')}
          />
          {errors.senha && (
            <p id="senha-erro" className="mt-1 text-xs text-red-700">
              {errors.senha.message}
            </p>
          )}
        </div>

        <div className="mt-1 text-right">
          {/* [ABERTO] fluxo de recuperacao de senha ainda nao definido */}
          <a href="#" className="text-xs text-gray-900 underline">
            Esqueci a senha?
          </a>
        </div>

        {errors.root && (
          <p role="alert" className="mt-3 text-sm text-red-700">
            {errors.root.message}
          </p>
        )}

        <button
          type="submit"
          disabled={isSubmitting}
          className="mt-3 w-full rounded-md bg-black px-4 py-2.5 text-sm font-semibold text-white hover:bg-gray-800 disabled:opacity-60"
        >
          {isSubmitting ? 'Entrando...' : 'Login'}
        </button>
      </form>

      <svg
        aria-hidden="true"
        viewBox="0 0 200 220"
        className="pointer-events-none absolute bottom-0 left-0 h-56 w-48 text-neutral-950"
        preserveAspectRatio="none"
      >
        <path
          fill="currentColor"
          d="M0 60 C40 70 60 75 85 50 C100 35 115 25 130 45 C170 105 205 160 195 220 L0 220 Z"
        />
      </svg>
    </main>
  )
}
