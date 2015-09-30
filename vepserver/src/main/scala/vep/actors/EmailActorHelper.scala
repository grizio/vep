package vep.actors

import scala.io.Source

trait EmailActorHelper {
  def getText(file: String): String = Source.fromInputStream(getClass.getResourceAsStream("/mails/" + file), "UTF-8").mkString

  def replaceParam(text: String, name: String, value: String)(implicit asHTML: Boolean): String =
   if (asHTML) {
     text.replaceAll(s"\\{\\{\\s*$name\\s*\\}\\}", value.replaceAll("\\n", "<br/>"))
   } else {
     text.replaceAll(s"\\{\\{\\s*$name\\s*\\}\\}", value)
   }
}
