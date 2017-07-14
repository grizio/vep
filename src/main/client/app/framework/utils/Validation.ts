interface Function<A, B> {
  (input: A): B
}

export interface Validation<A> {
  map<B>(op: Function<A, B>): Validation<B>

  filter(check: Function<A, boolean>, message: string): Validation<A>

  flatMap<B>(op: Function<A, Validation<B>>): Validation<B>

  fold<B>(onError: Function<ReadonlyArray<string>, B>, onSuccess: Function<A, B>): B

  listErrors(): ReadonlyArray<string>
}

export function Valid<A>(value: A): Validation<A> {
  return new ValidImpl(value)
}

export function Invalid(errors: Array<string> | ReadonlyArray<string>): Validation<any> {
  return new InvalidImpl(errors)
}

class ValidImpl<A> implements Validation<A> {
  value: A

  constructor(value: A) {
    this.value = value
    Object.freeze(this)
  }

  map<B>(op: Function<A, B>): Validation<B> {
    return Valid(op(this.value))
  }

  filter(check: Function<A, boolean>, error: string): Validation<A> {
    if (check(this.value)) {
      return this
    } else {
      return Invalid([error])
    }
  }

  flatMap<B>(op: Function<A, Validation<B>>): Validation<B> {
    return op(this.value)
  }

  fold<B>(onError: Function<ReadonlyArray<string>, B>, onSuccess: Function<A, B>): B {
    return onSuccess(this.value)
  }

  listErrors(): Array<string> {
    return []
  }
}

class InvalidImpl implements Validation<any> {
  errors: ReadonlyArray<string>

  constructor(errors: Array<string> | ReadonlyArray<string>) {
    this.errors = errors
    Object.freeze(this)
  }

  map<B>(op: Function<any, B>): Validation<B> {
    return Invalid(this.errors)
  }

  filter(check: Function<any, boolean>, message: string): Validation<any> {
    return this
  }

  flatMap<B>(op: Function<any, Validation<B>>): Validation<B> {
    return Invalid(this.errors)
  }

  fold<B>(onError: Function<ReadonlyArray<string>, B>, onSuccess: Function<any, B>): B {
    return onError(this.errors)
  }

  listErrors(): ReadonlyArray<string> {
    return this.errors
  }
}

export interface FieldValidation<A> {
  value: A
  errors: Array<string>
  changed: boolean
}

export function defaultFieldValidation<A>(defaultValue: A): FieldValidation<A> {
  return {
    value: defaultValue,
    errors: [],
    changed: false
  }
}

export function updateFieldValidation<A>(previous: FieldValidation<A>, value: A, validation: Validation<A>): FieldValidation<A> {
  return Object.assign({}, previous, {
    value: value,
    errors: validation.listErrors(),
    changed: true
  })
}

export function updateUnchangedFieldValidation<A>(previous: FieldValidation<A>, value: A, validation: Validation<A>): FieldValidation<A> {
  return Object.assign({}, previous, {
    value: value,
    errors: validation.listErrors()
  })
}