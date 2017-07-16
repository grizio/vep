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

  def isValid: Boolean

  def map[B](p: A => B): Validation[B]

  def flatMap[B](p: A => Validation[B]): Validation[B]

  def mapError(p: Seq[String] => Seq[String]): Validation[A]

  def filter(p: A => Boolean, error: String): Validation[A]

  def filterNot(p: A => Boolean, error: String): Validation[A] = filter(value => !p(value), error)

  def verify(p: => Boolean, error: String): Validation[A] = filter(_ => p, error)

  def verifyNot(p: => Boolean, error: String): Validation[A] = filterNot(_ => p, error)

  def whenValid(p: A => Unit): Validation[A]

  def whenInvalid(p: Seq[String] => Unit): Validation[A]
}

object Validation {
  type V[A] = Validation[A]
  def all[A, B](v1: V[A], v2: V[B]): V[A ~ B] = v1 ~ v2
  def all[A, B, C](v1: V[A], v2: V[B], v3: V[C]): V[A ~ B ~ C] = v1 ~ v2 ~ v3
  def all[A, B, C, D](v1: V[A], v2: V[B], v3: V[C], v4: V[D]): V[A ~ B ~ C ~ D] = v1 ~ v2 ~ v3 ~ v4
  def all[A, B, C, D, E](v1: V[A], v2: V[B], v3: V[C], v4: V[D], v5: V[E]): V[A ~ B ~ C ~ D ~ E] = v1 ~ v2 ~ v3 ~ v4 ~ v5
  def all[A, B, C, D, E, F](v1: V[A], v2: V[B], v3: V[C], v4: V[D], v5: V[E], v6: V[F]): V[A ~ B ~ C ~ D ~ E ~ F] = v1 ~ v2 ~ v3 ~ v4 ~ v5 ~ v6

  def sequence[A](validations: Seq[Validation[A]]): Validation[Seq[A]] = {
    if (validations.forall(_.isValid)) {
      Valid(validations.collect { case Valid(value) => value })
    } else {
      Invalid(validations.collect { case Invalid(errors) => errors }.flatten)
    }
  }
}

case class Valid[A](value: A) extends Validation[A] {
  def isValid: Boolean = true

  def map[B](operation: A => B): Validation[B] = {
    Valid(operation(value))
  }

  def flatMap[B](operation: A => Validation[B]): Validation[B] = {
    operation(value)
  }

  def mapError(p: Seq[String] => Seq[String]): Validation[A] = this

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
  def isValid: Boolean = false

  def map[B](p: Nothing => B): Validation[B] = this

  def flatMap[B](operation: Nothing => Validation[B]): Validation[B] = this

  def mapError(p: Seq[String] => Seq[String]): Validation[Nothing] = {
    Invalid(p(errors))
  }

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