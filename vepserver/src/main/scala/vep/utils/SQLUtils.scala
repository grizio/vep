package vep.utils

/**
 * This class groups all utilities to manipulate SQL queries.
 */
object SQLUtils {
  /**
   * Builds WHERE part of a query in terms of given criteria.
   * It is useful when building dynamic queries as search queries for instance.
   * If there is no part of criteria which are non empty, returns an empty string.
   * If the result is non empty, adds a space before and after the generated query.
   *
   * {{{
   *   buildWhereQuery("a = 'a'", "b = 'b'") => " a = 'a' AND b = 'b' "
   *   buildWhereQuery("a = 'a'", "", "c = 'c'") => " a = 'a' AND c = 'c' "
   *   buildWhereQuery("", "", "") => ""
   *   buildWhereQuery(
   *     Some("aa") map { a => s"a = '$a'" } getOrElse "",
   *     None map { b => s"b = '$b'" } getOrElse "",
   *     Some("cc") map { c => s"c = '$c'" } getOrElse ""
   *   ) => " a = 'aa' AND c = 'cc' "
   * }}}
   *
   * @return The WHERE part of query.
   */
  def buildWhereQuery(criteria: String*): String = {
    val filteredCriteria = criteria.filter(!_.trim.isEmpty)
    if (filteredCriteria.isEmpty) ""
    else filteredCriteria.mkString(" ", " AND ", " ")
  }
}
