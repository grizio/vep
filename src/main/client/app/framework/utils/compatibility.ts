export const acceptInputDate = checkInputType("date", "not-a-date")

export const acceptInputDateTimeLocal = checkInputType("datetime-local", "not-a-datetime")

export const acceptInputTime = checkInputType("time", "not-a-time")

function checkInputType(type: string, invalidValue: string) {
  const input = document.createElement('input');
  input.setAttribute('type',type);
  input.setAttribute('value', invalidValue);
  return (input.value !== invalidValue);
}