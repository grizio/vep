package vep.test.utils

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.utils.SQLUtils

@RunWith(classOf[JUnitRunner])
class SQLUtilsSpecification extends Specification {
  "Specifications of SQLUtils functions" >> {
    "SQLUtils.buildWhereQuery should" >> {
      "returns \" a = 'a' AND b = 'b' \" for buildWhereQuery(\"a = 'a'\", \"b = 'b'\")" >> {
        val expected = " a = 'a' AND b = 'b' "
        val result = SQLUtils.buildWhereQuery("a = 'a'", "b = 'b'")

        result === expected
      }

      "returns \" a = 'a' AND c = 'c' \" for buildWhereQuery(\"a = 'a'\", \"\", \"c = 'c'\")" >> {
        val expected = " a = 'a' AND c = 'c' "
        val result = SQLUtils.buildWhereQuery("a = 'a'", "", "c = 'c'")

        result === expected
      }

      "returns \"\" for buildWhereQuery(\"\", \"\", \"\")" >> {
        val expected = ""
        val result = SQLUtils.buildWhereQuery("", "", "")

        result === expected
      }

      """returns " a = 'aa' AND c = 'cc' " for buildWhereQuery(
        |  Some("aa") map { a => s"a = '$a'" } getOrElse "",
        |  None map { b => s"b = '$b'" } getOrElse "",
        |  Some("cc") map { c => s"c = '$c'" } getOrElse ""
        |)""".stripMargin >> {
        val expected = " a = 'aa' AND c = 'cc' "
        val result = SQLUtils.buildWhereQuery(
          Some("aa") map { a => s"a = '$a'" } getOrElse "",
          None map { b => s"b = '$b'" } getOrElse "",
          Some("cc") map { c => s"c = '$c'" } getOrElse ""
        )

        result === expected
      }
    }
  }
}
