package vep.model.common

abstract class ErrorItem {
  def isEmpty: Boolean

  def isNotEmpty = !isEmpty

  def contains(key: Any): Boolean = false

  def get(key: Any): Option[ErrorItem]
}

case class ErrorItemFinal(e: Seq[Int]) extends ErrorItem {
  def isEmpty = e.isEmpty

  override def contains(key: Any) = key match {
    case keyInt: Int => e.contains(keyInt)
    case _ => false
  }

  override def get(key: Any): Option[ErrorItem] = None
}

case class ErrorItemTree(e: Map[String, ErrorItem]) extends ErrorItem {
  def isEmpty = e.isEmpty

  override def contains(key: Any) = key match {
    case keyStr: String => e.contains(keyStr)
    case _ => false
  }

  override def get(key: Any): Option[ErrorItem] = key match {
    case keyStr: String => e.get(keyStr)
    case _ => None
  }
}

object ErrorItemTree {
  def apply(e: (String, ErrorItem)*) = new ErrorItemTree(Map(e: _*))
}

case class ErrorItemSeq(e: Map[Int, ErrorItem]) extends ErrorItem {
  def isEmpty = e.isEmpty

  override def get(key: Any): Option[ErrorItem] = key match {
    case keyInt: Int => e.get(keyInt)
    case _ => None
  }
}

object ErrorItemSeq {
  def apply(e: (Int, ErrorItem)*) = new ErrorItemSeq(Map(e: _*))
}

object ErrorItem {
  def seqToErrorItemSeq[A](items: Seq[A], errorCode: Int)(check: (A => Boolean)): ErrorItemSeq = {
    var result = ErrorItemSeq(Map[Int, ErrorItem]())
    for ((item, index) <- items.view.zipWithIndex) {
      if (check(item)) {
        result = ErrorItemSeq(result.e + (index -> ErrorItemFinal(Seq(errorCode))))
      }
    }
    result
  }

  def build(pairs: (String, Int)*): ErrorItemTree = {
    var map = Map[String, Seq[Int]]()
    pairs foreach { case (k, v) => map = map + (k -> (map.getOrElse(k, Seq[Int]()) :+ v)) }
    ErrorItemTree(map map { case (k, v) => k -> ErrorItemFinal(v) })
  }
}
