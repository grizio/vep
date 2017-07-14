package vep.framework.validation

import spray.json.JsonFormat

case class ~[+A, +B](a: A, b: B)

sealed trait Validation[+A] {
  def ~[B](other: Validation[B]): Validation[A ~ B] =
    (this, other) match {
      case (Valid(a), Valid(b)) => Valid(new ~(a, b))
      case (Invalid(errorMessagesA), Invalid(errorMessagesB)) => Invalid(errorMessagesA ++ errorMessagesB)
      case (Invalid(errorMessagesA), _) => Invalid(errorMessagesA)
      case (_, Invalid(errorMessagesB)) => Invalid(errorMessagesB)
    }

  def map[B](p: A => B): Validation[B]

  def filter(p: A => Boolean, error: String): Validation[A]

  def filterNot(p: A => Boolean, error: String): Validation[A] = filter(value => !p(value), error)

  def verify(p: => Boolean, error: String): Validation[A] = filter(_ => p, error)

  def verifyNot(p: => Boolean, error: String): Validation[A] = filterNot(_ => p, error)

  def whenValid(p: A => Unit): Validation[A]

  def whenInvalid(p: Seq[String] => Unit): Validation[A]
}

case class Valid[A](value: A) extends Validation[A] {
  def map[B](operation: A => B): Validation[B] = {
    Valid(operation(value))
  }

  override def filter(predicate: (A) => Boolean, error: String): Validation[A] = {
    if (predicate(value)) {
      this
    } else {
      Invalid(error)
    }
  }

  override def whenValid(p: (A) => Unit): Validation[A] = {
    p(value)
    this
  }

  override def whenInvalid(p: (Seq[String]) => Unit): Validation[A] = this
}

case class Invalid(errors: Seq[String]) extends Validation[Nothing] {
  def map[B](p: Nothing => B): Validation[B] = this

  override def filter(p: (Nothing) => Boolean, error: String): Validation[Nothing] = this

  override def whenValid(p: (Nothing) => Unit): Validation[Nothing] = this

  override def whenInvalid(p: (Seq[String]) => Unit): Validation[Nothing] = {
    p(errors)
    this
  }
}

object Invalid {
  import spray.json.DefaultJsonProtocol._

  def apply(errors: String*)(implicit dummyImplicit: DummyImplicit): Invalid = new Invalid(errors)

  implicit def invalidFormat: JsonFormat[Invalid] = jsonFormat1(Invalid.apply)
}