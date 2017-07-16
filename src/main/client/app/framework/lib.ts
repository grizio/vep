export interface Function0<A> {
  (): A
}

export interface Function1<A, B> {
  (a: A): B
}

export interface Function2<A, B, C> {
  (a: A, b: B): C
}