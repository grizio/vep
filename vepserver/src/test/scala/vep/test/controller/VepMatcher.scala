package vep.test.controller

import org.specs2.matcher.Matcher
import org.specs2.mutable.Specification
import vep.model.common._

trait VepMatcher {
  self: Specification =>

  def matchPartialResultErrors(field: String, errorCode: Int): Matcher[Either[ResultErrors, _]] = { result: Either[ResultErrors, _] =>
    (result must beAnInstanceOf[Left[ResultErrors, _]]) and
      (result.asInstanceOf[Left[ResultErrors, _]].a.errors must haveKey(field)) and
      (result.asInstanceOf[Left[ResultErrors, _]].a.errors.get(field).get must contain(errorCode))
  }

  def matchPartialResultStructuredErrors(expected: ErrorItem): Matcher[Either[ResultStructuredErrors, _]] = { result: Either[ResultStructuredErrors, _] =>
    (result must beAnInstanceOf[Left[ResultStructuredErrors, _]]) and
      (result.asInstanceOf[Left[ResultStructuredErrors, _]].a.errors must matchPartialErrorItem(expected))
  }

  def matchPartialErrorItem(expected: ErrorItem): Matcher[ErrorItem] = { value: ErrorItem =>
    (equalPartialErrorItem(value, expected), s"$value does not contain $expected")
  }

  def matchPartialErrorItemFinal(expected: ErrorItemFinal): Matcher[ErrorItemFinal] = { value: ErrorItemFinal =>
    (equalPartialErrorItemFinal(value, expected), s"$value does not contain $expected")
  }

  def matchPartialErrorItemSeq(expected: ErrorItemSeq): Matcher[ErrorItemSeq] = { value: ErrorItemSeq =>
    (equalPartialErrorItemSeq(value, expected), s"$value does not contain $expected")
  }

  def matchPartialErrorItemTree(expected: ErrorItemTree): Matcher[ErrorItemTree] = { value: ErrorItemTree =>
    (equalPartialErrorItemTree(value, expected), s"$value does not contain $expected")
  }

  def equalPartialErrorItem(value: ErrorItem, expected: ErrorItem): Boolean = expected match {
    case ErrorItemFinal(e) =>
      value.isInstanceOf[ErrorItemFinal] && equalPartialErrorItemFinal(value.asInstanceOf[ErrorItemFinal], expected.asInstanceOf[ErrorItemFinal])
    case ErrorItemSeq(e) =>
      value.isInstanceOf[ErrorItemSeq] && equalPartialErrorItemSeq(value.asInstanceOf[ErrorItemSeq], expected.asInstanceOf[ErrorItemSeq])
    case ErrorItemTree(e) =>
      value.isInstanceOf[ErrorItemTree] && equalPartialErrorItemTree(value.asInstanceOf[ErrorItemTree], expected.asInstanceOf[ErrorItemTree])
  }

  def equalPartialErrorItemFinal(value: ErrorItemFinal, expected: ErrorItemFinal): Boolean =
    expected.e forall { e => value.e.contains(e) }

  def equalPartialErrorItemSeq(value: ErrorItemSeq, expected: ErrorItemSeq): Boolean =
    expected.e forall { case (k, v) => value.e.contains(k) && equalPartialErrorItem(value.e(k), v) }

  def equalPartialErrorItemTree(value: ErrorItemTree, expected: ErrorItemTree): Boolean =
    expected.e forall { case (k, v) => value.e.contains(k) && equalPartialErrorItem(value.e(k), v) }
}
