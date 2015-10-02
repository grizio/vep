package vep.utils

import scala.io.Source

object EmailUtils {
  def getText(file: String): String = Source.fromInputStream(getClass.getResourceAsStream("/mails/" + file), "UTF-8").mkString

  def replaceParam(text: String, name: String, value: String)(implicit asHTML: Boolean): String =
   if (asHTML) {
     text.replaceAll(s"\\{\\{\\s*$name\\s*\\}\\}", value.replaceAll("\\n", "<br/>"))
   } else {
     text.replaceAll(s"\\{\\{\\s*$name\\s*\\}\\}", value)
   }

  def replaceParams(text: String)(params: (String, String)*)(implicit asHTML: Boolean): String =
    params.foldLeft(text)((result, param) => replaceParam(result, param._1, param._2))
}
