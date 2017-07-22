import {Function1, Function2} from "../lib";
export function min<A>(array: Array<A>): A {
  if (array.length) {
    let minValue = array[0]
    for (let i = 1 ; i < array.length ; i++) {
      if (array[i] < minValue) {
        minValue = array[i]
      }
    }
    return minValue
  } else {
    return null
  }
}

export function max<A>(array: Array<A>): A {
  if (array.length) {
    let maxValue = array[0]
    for (let i = 1 ; i < array.length ; i++) {
      if (array[i] > maxValue) {
        maxValue = array[i]
      }
    }
    return maxValue
  } else {
    return null
  }
}

export function append<A>(array: Array<A>, element: A): Array<A> {
  const newArray = ([] as Array<A>).concat(array)
  newArray.push(element)
  return newArray
}

export function replace<A>(array: Array<A>, index: number, element: A): Array<A> {
  const newArray = ([] as Array<A>).concat(array)
  newArray[index] = element
  return newArray
}

export function remove<A>(array: Array<A>, index: number): Array<A> {
  const newArray = ([] as Array<A>).concat(array)
  newArray.splice(index, 1)
  return newArray
}

export function isEmpty<A>(array: Array<A> | ReadonlyArray<A>): boolean {
  return !isNotEmpty(array)
}

export function isNotEmpty<A>(array: Array<A> | ReadonlyArray<A>): boolean {
  return array && array.length > 0
}

export function allEmpty<A>(...arrays: Array<Array<A> | ReadonlyArray<A>>): boolean {
  return isEmpty(arrays) || arrays.every(isEmpty)
}

export function fill<A>(length: number, element: A = null): Array<A> {
  return Array(length).fill(element)
}

export function fillWith<A>(length: number, computeElement: Function1<number, A>): Array<A> {
  return Array(length)
    .fill(null)
    .map((_, index) => computeElement(index))
}

export function sort<A>(array: Array<A>, compare: Function2<A, A, number>): Array<A> {
  const arrayToSort = [].concat(array)
  arrayToSort.sort(compare)
  return arrayToSort
}