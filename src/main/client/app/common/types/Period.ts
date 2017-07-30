import {
  defaultFieldValidation, FieldValidation, Invalid, updateFieldValidation, updateUnchangedFieldValidation, Valid,
  Validation
} from "../../framework/utils/Validation";
import {
  isAfterNow, isBeforeNow, isBeforeOrEquals, localDateTimeIsoFormat,
  localIsoFormatToDate, shortDateFormat
} from "../../framework/utils/dates";
import messages from "../../framework/messages";
import {copy} from "../../framework/utils/object";

export interface Period {
  start: Date
  end: Date
}

export interface PeriodValidation {
  start: FieldValidation<Date>
  end: FieldValidation<Date>
}

export function updatePeriodValidationStart(period: FieldValidation<PeriodValidation>, value: Date): FieldValidation<PeriodValidation> {
  const updatedPeriod = copy(period.value, {
    start: updateFieldValidation(period.value.start, value, Valid(value)),
    end: updateUnchangedFieldValidation(period.value.end, period.value.end.value, validatePeriodEnd(period.value.end.value, value))
  })
  return updateFieldValidation(period, updatedPeriod, Valid(updatedPeriod))
}

export function updatePeriodValidationEnd(period: FieldValidation<PeriodValidation>, value: Date): FieldValidation<PeriodValidation> {
  const updatedPeriod = copy(period.value, {
    end: updateFieldValidation(period.value.end, value, validatePeriodEnd(value, period.value.start.value))
  })
  return updateFieldValidation(period, updatedPeriod, Valid(updatedPeriod))
}

function validatePeriodEnd(end: Date, start: Date): Validation<Date> {
  if (start && end) {
    if (isBeforeOrEquals(start, end)) {
      return Valid(end)
    } else {
      return Invalid([messages.common.invalidPeriod])
    }
  } else {
    return Valid(end)
  }
}

export function periodValidationToPeriod(periodValidation: PeriodValidation): Period {
  return {
    start: periodValidation.start.value,
    end: periodValidation.end.value
  }
}

export function periodToPeriodValidation(period: Period): PeriodValidation {
  return {
    start: defaultFieldValidation(period.start),
    end: defaultFieldValidation(period.end)
  }
}

export function periodToJson(period: Period): Period {
  return copy(period, {
    start: localDateTimeIsoFormat(period.start),
    end: localDateTimeIsoFormat(period.end)
  } as any)
}

export function jsonToPeriod(period: Period): Period {
  return copy(period, {
    start: localIsoFormatToDate(period.start as any),
    end: localIsoFormatToDate(period.end as any)
  } as any)
}

export function isPassed(period: Period): boolean {
  return isBeforeNow(period.end)
}

export function isCurrent(period: Period): boolean {
  return isAfterNow(period.start) && isBeforeNow(period.end)
}

export function isFuture(period: Period): boolean {
  return isAfterNow(period.start)
}

export function shortPeriodFormat(period: Period): string {
  return `Du ${shortDateFormat(period.start)} au ${shortDateFormat(period.end)}`
}