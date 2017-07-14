package vep.framework.utils

object NumberUtils {
  def isDouble(string: String): Boolean = {
    try {
      string.toDouble
      true
    } catch {
      case _: Throwable => false
    }
  }

  def toDoubleOpt(string: String): Option[Double] = {
    try {
      Some(string.toDouble)
    } catch {
      case _: Throwable => None
    }
  }

  def isInt(string: String): Boolean = {
    try {
      string.toInt
      true
    } catch {
      case _: Throwable => false
    }
  }

  def toIntOpt(string: String): Option[Int] = {
    try {
      Some(string.toInt)
    } catch {
      case _: Throwable => None
    }
  }

  def isLong(string: String): Boolean = {
    try {
      string.toLong
      true
    } catch {
      case _: Throwable => false
    }
  }

  def toLongOpt(string: String): Option[Long] = {
    try {
      Some(string.toLong)
    } catch {
      case _: Throwable => None
    }
  }

  def random(min: Int, max: Int): Int = {
    Math.floor(Math.random() * (max - min) + min).toInt
  }
}
