package vep.app.common.verifications

import vep.app.common.CommonMessages
import vep.framework.validation.{Invalid, Valid, Validation}

trait SeqVerifications {
  def verifyNonEmptySeq[A](value: Seq[A]): Validation[Seq[A]] = {
    Valid(value)
      .filter(_.nonEmpty, CommonMessages.errorIsSeqEmpty)
  }

  def verifyAllUnique[A](value: Seq[A]): Validation[Seq[A]] = verifyAllUnique(value, CommonMessages.duplicatedElement)
  def verifyAllUnique[A](value: Seq[A], errorMessage: A => String): Validation[Seq[A]] = {
    value.zipWithIndex.find {
      case (seat, index) => value.indexOf(seat) != index
    } match {
      case Some((duplicatedElement, _)) => Invalid(errorMessage(duplicatedElement))
      case None => Valid(value)
    }
  }

  def verifyContainsNoForbiddenElements[A](value: Seq[A], forbiddenElements: Seq[A]): Validation[Seq[A]] = {
    verifyContainsNoForbiddenElements(value, forbiddenElements, CommonMessages.duplicatedElement)
  }
  def verifyContainsNoForbiddenElements[A](value: Seq[A], forbiddenElements: Seq[A], errorMessage: A => String): Validation[Seq[A]] = {
    value.find(forbiddenElements.contains) match {
      case Some(forbiddenElement) => Invalid(errorMessage(forbiddenElement))
      case None => Valid(value)
    }
  }
}
