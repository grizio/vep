export function nonEmpty(str: string | null) {
  return str && str.length > 0
}

export function isEmpty(str: string | null) {
  return nonEmpty(str)
}

export function contains(str: string | null, search: string | null): boolean {
  if (str === null && search === null) {
    return true
  } else if (str === null && search !== null) {
    return false
  } else if (str !== null && search === null) {
    return true
  } else {
    return str.indexOf(search) >= 0
  }
}

export function containsNot(str: string | null, search: string | null): boolean {
  return !contains(str, search)
}

/**
 * Creates a UUID like
 */
export function uuid(): string {
  const part = () => Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1)
  return part() + part() + part() + part() + part()
}

export function forceStartWith(str: string, prefix: string): string {
  return str.startsWith(prefix)
    ? str
    : prefix + str
}

export function isUppercase(char: string): boolean {
  return char && char === char.toUpperCase()
}

export function isNotUppercase(char: string): boolean {
  return !isUppercase(char)
}

export function isLowercase(char: string): boolean {
  return char && char === char.toLowerCase()
}

export function isNotLowercase(char: string): boolean {
  return !isLowercase(char)
}

export function isNumber(char: string): boolean {
  return char && new RegExp("[0-9]").test(char)
}

export function isNotNumber(char: string): boolean {
  return !isNumber(char)
}

export function isLetter(char: string): boolean {
  return char && char.toUpperCase() !== char.toLowerCase()
}

export function isNotLetter(char: string): boolean {
  return !isLetter(char)
}

export function hasUppercase(str: string): boolean {
  if (!str) return false

  for (let i = 0; i < str.length; i++) {
    if (isUppercase(str.charAt(i))) {
      return true
    }
  }
  return false
}

export function hasNotUppercase(str: string): boolean {
  return !hasUppercase(str)
}

export function hasLowercase(str: string): boolean {
  if (!str) return false

  for (let i = 0; i < str.length; i++) {
    if (isLowercase(str.charAt(i))) {
      return true
    }
  }
  return false
}

export function hasNotLowercase(str: string): boolean {
  return !hasLowercase(str)
}

export function hasNumber(str: string): boolean {
  if (!str) return false

  for (let i = 0; i < str.length; i++) {
    if (isNumber(str.charAt(i))) {
      return true
    }
  }
  return false
}