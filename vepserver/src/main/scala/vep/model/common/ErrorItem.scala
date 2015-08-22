package vep.model.common

abstract class ErrorItem {
  def isEmpty: Boolean

  def contains(key: String) = false
}

case class ErrorItemFinal(e: Seq[Int]) extends ErrorItem {
  def isEmpty = e.isEmpty
}

case class ErrorItemTree(e: Map[String, ErrorItem]) extends ErrorItem {
  def isEmpty = e.isEmpty

  override def contains(key: String) = e.contains(key)
}

case class ErrorItemSeq(e: Map[Int, ErrorItem]) extends ErrorItem {
  def isEmpty = e.isEmpty
}

object ErrorItem {
  def seqToErrorItemSeq[A](items: Seq[A], errorCode: Int)(check: (A => Boolean)): ErrorItemSeq = {
    var result = ErrorItemSeq(Map())
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
