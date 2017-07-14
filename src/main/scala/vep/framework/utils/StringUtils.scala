package vep.framework.utils

object StringUtils {
  val acceptedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

  def randomString(min: Int = 50, max: Int = 100): String = {
    if (min > max) {
      randomString(max, min)
    } else {
      (1 to NumberUtils.random(min, max))
        .map(_ => randomChar)
        .mkString("")
    }
  }

  def randomChar: Char = {
    acceptedChars.charAt(NumberUtils.random(0, acceptedChars.length))
  }
}
