interface LogoProps {
  className?: string
}

/** Logo CLAI em texto (sem imagem raster). O "A" e um triangulo, como no mockup. */
export function Logo({ className = '' }: LogoProps) {
  return (
    <span role="img" aria-label="CLAI" className={`font-extralight tracking-[0.18em] ${className}`}>
      <span aria-hidden="true">CL&Lambda;I</span>
    </span>
  )
}
