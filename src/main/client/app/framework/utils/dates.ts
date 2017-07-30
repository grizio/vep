export function longDateFormat(date: Date): string {
  if (date) {
    return date.toLocaleString(
      "fr",
      {
        year: "numeric",
        month: "long",
        day: "2-digit",
        weekday: "long",
        timeZone: "UTC"
      }
    )
  } else {
    return null
  }
}

export function shortDateFormat(date: Date): string {
  if (date) {
    return date.toLocaleString(
      "fr",
      {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        timeZone: "UTC"
      }
    )
  } else {
    return null
  }
}

export function longDateTimeFormat(date: Date): string {
  if (date) {
    return date.toLocaleString(
      "fr",
      {
        year: "numeric",
        month: "long",
        day: "2-digit",
        weekday: "long",
        hour: "2-digit",
        minute: "2-digit",
        timeZone: "UTC"
      }
    )
  } else {
    return null
  }
}

export function shortDateTimeFormat(date: Date): string {
  if (date) {
    return date.toLocaleString(
      "fr",
      {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        timeZone: "UTC"
      }
    )
  } else {
    return null
  }
}

export function timeFormat(date: Date): string {
  if (date) {
    return date.toLocaleString(
      "fr",
      {
        hour: "2-digit",
        minute: "2-digit",
        timeZone: "UTC"
      }
    )
  } else {
    return null
  }
}

export function localDateIsoFormat(date: Date): string {
  if (date) {
    // date.toISOString return yyyy-MM-ddTHH:ii:ss.SSSZ
    // We do not want the .SSSZ part
    const isoDate = date.toISOString()
    const indexOfDot = isoDate.indexOf("T")
    if (indexOfDot >= 0) {
      return isoDate.substr(0, indexOfDot)
    } else {
      return isoDate
    }
  } else {
    return null
  }
}

export function localDateTimeIsoFormat(date: Date): string {
  if (date) {
    // date.toISOString return yyyy-MM-ddTHH:ii:ss.SSSZ
    // We do not want the .SSSZ part
    const isoDate = date.toISOString()
    const indexOfDot = isoDate.indexOf(".")
    if (indexOfDot >= 0) {
      return isoDate.substr(0, indexOfDot)
    } else {
      return isoDate
    }
  } else {
    return null
  }
}

export function shortDateFormatToDate(text: string): Date {
  if (/[0-9]{2}\/[0-9]{2}\/[0-9]{4}/.test(text)) {
    const day = text.substr(0, 2)
    const month = text.substr(3, 2)
    const year = text.substr(6, 4)
    return new Date(`${year}-${month}-${day}T00:00:00.000Z`)
  } else {
    return null
  }
}

export function shortDateTimeFormatToDate(text: string): Date {
  if (/[0-9]{2}\/[0-9]{2}\/[0-9]{4} [0-2]{2}:[0-9]{2}/.test(text)) {
    const day = text.substr(0, 2)
    const month = text.substr(3, 2)
    const year = text.substr(6, 4)
    const hour = text.substr(11, 2)
    const minute = text.substr(14, 2)
    return new Date(`${year}-${month}-${day}T${hour}:${minute}:00.000Z`)
  } else {
    return null
  }
}

export function timeFormatToDate(text: string): Date {
  if (/[0-2]{2}:[0-9]{2}/.test(text)) {
    const hour = text.substr(0, 2)
    const minute = text.substr(3, 2)
    return new Date(`T${hour}:${minute}:00.000Z`)
  } else {
    return null
  }
}

export function localIsoFormatToDate(text: string): Date {
  if (text) {
    const containsTime = text.indexOf(":") >= 0
    if (containsTime) {
      return new Date(`${text}Z`)
    } else {
      return new Date(text)
    }
  } else {
    return null
  }
}

export function now() {
  return toUTC(new Date());
}

export function toUTC(date: Date) {
  return new Date(Date.UTC(
    date.getFullYear(),
    date.getMonth(),
    date.getDate(),
    date.getHours(),
    date.getMinutes(),
    date.getSeconds(),
    date.getMilliseconds()
  ))
}

export function equals(date: Date, reference: Date): boolean {
  return date.getTime() === reference.getTime()
}

export function isAfter(date: Date, reference: Date): boolean {
  return date.getTime() > reference.getTime()
}

export function isBefore(date: Date, reference: Date): boolean {
  return date.getTime() < reference.getTime()
}

export function isAfterOrEquals(date: Date, reference: Date): boolean {
  return date.getTime() >= reference.getTime()
}

export function isBeforeOrEquals(date: Date, reference: Date): boolean {
  return date.getTime() <= reference.getTime()
}

export function isAfterNow(date: Date): boolean {
  return date.getTime() > now().getTime()
}

export function isBeforeNow(date: Date): boolean {
  return date.getTime() < now().getTime()
}