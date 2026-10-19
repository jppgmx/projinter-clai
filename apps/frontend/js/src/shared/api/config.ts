/** URL base da API. Em dev, "/api" passa pelo proxy do Vite (sem CORS). */
export const API_URL: string = import.meta.env.VITE_API_URL ?? '/api'
