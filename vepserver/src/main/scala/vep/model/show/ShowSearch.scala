package vep.model.show

import vep.model.common.{ErrorCodes, VerifiableUnique}

// The usage of one letter as parameter is to reduce URL length in router
case class ShowSearch(p: Option[Int], t: Option[String], a: Option[String], d: Option[String], c: Option[String], o: Option[String]) extends VerifiableUnique {
  def page = p

  def title = t

  def author = a

  def director = d

  def company = c

  def order = o

  override def verify: Boolean = {
    if (page exists {
      _ <= 0
    }) {
      setError(ErrorCodes.negativeOrNull)
      false
    } else if (order exists {
      !ShowSearchColumns.validOrderColumns.contains(_)
    }) {
      setError(ErrorCodes.unknownOrder)
      false
    } else {
      true
    }
  }
}

object ShowSearchColumns {
  lazy val orderColumn = Map(
    "default" -> "title",
    "t" -> "title",
    "a" -> "author",
    "d" -> "director",
    "c" -> "company"
  )
  lazy val validOrderColumns = orderColumn.keySet
}


/* Helper query:
SELECT CONCAT('ShowSearchResult(canonical = "', shows.canonical, '", title = "', title, '", author = "', author, '", director = "', director, '", company = "', company.canonical, '")')
FROM shows
LEFT JOIN company ON company = company.id;
*/
// The usage of this class for search response instead of Show is to reduce response length.
case class ShowSearchResult(canonical: String, title: String, author: String, director: Option[String], company: String)

case class ShowSearchResponse(shows: Seq[ShowSearchResult], pageMax: Int)