export function copy<A>(source: A, elements: Partial<A>): A {
  return Object.assign({}, source, elements)
}

export function nullOrUndefined<A>(value: A): boolean {
  return value === null || value === undefined
}

export function notNullOrUndefined<A>(value: A): boolean {
  return value !== null && value !== undefined
}