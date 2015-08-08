package vep.service.show

import anorm.SqlParser._
import anorm._
import vep.AnormClient
import vep.model.show.{ShowSearch, ShowSearchColumns, ShowSearchResult}
import vep.service.AnormImplicits
import vep.utils.DB

trait ShowSearchServiceComponent {
  def showSearchService: ShowSearchService

  trait ShowSearchService {
    /**
     * Searches companies with given criteria.
     * @param showSearch The criteria of search
     * @param maxResult The maximum result to return
     * @return The list of found companies, limited by {{{maxResult}}} results
     */
    def search(showSearch: ShowSearch, maxResult: Int): Seq[ShowSearchResult]

    /**
     * Counts the number of results according to given search criteria
     * @param showSearch The criteria of search
     * @return The number of lines according to given search
     */
    def count(showSearch: ShowSearch): Int
  }

}

trait ShowSearchServiceProductionComponent extends ShowSearchServiceComponent {
  self: AnormClient =>

  override lazy val showSearchService = new ShowSearchServiceProduction

  class ShowSearchServiceProduction extends ShowSearchService with AnormImplicits {

    override def search(showSearch: ShowSearch, maxResult: Int): Seq[ShowSearchResult] = DB.withConnection { implicit c =>
      val sql =
        s"""SELECT s.canonical, s.title, s.author, s.director, c.canonical AS company
           |FROM $sqlTables
            |${sqlSearchCriteria(showSearch)}
            |ORDER BY ${ShowSearchColumns.orderColumn.getOrElse(showSearch.order.getOrElse("default"), "title")} ASC
                                                                                                                  |LIMIT {firstResult}, {maxResult}
                                                                                                                  |""".stripMargin
      querySearchCriteria(sql, showSearch)
        .on("firstResult" -> (showSearch.page.getOrElse(1) - 1) * maxResult)
        .on("maxResult" -> maxResult)
        .as(ShowParsers.showSearchResult *)
    }

    override def count(showSearch: ShowSearch): Int = DB.withConnection { implicit c =>
      val sql = s"""SELECT count(*) FROM $sqlTables ${sqlSearchCriteria(showSearch)}"""
      querySearchCriteria(sql, showSearch)
        .as(scalar[Int].single)
    }

    lazy val sqlTables: String = "shows s LEFT JOIN company c ON s.company = c.id"

    def sqlSearchCriteria(showSearch: ShowSearch): String = {
      val criteria = Seq(
        showSearch.title match { case Some(t) => "s.title LIKE {title}" case None => "" },
        showSearch.author match { case Some(a) => "s.author LIKE {author}" case None => "" },
        showSearch.director match { case Some(d) => "s.director LIKE {director}" case None => "" },
        showSearch.company match { case Some(c) => "c.canonical = {company}" case None => "" }
      ).filter(!_.isEmpty)

      if (criteria.isEmpty) ""
      else criteria.mkString("WHERE ", " AND ", " ")
    }

    def querySearchCriteria(sql: String, showSearch: ShowSearch): SimpleSql[Row] = {
      var query: SimpleSql[Row] = SQL(sql)
      showSearch.title foreach { title => query = query.on("title" -> s"%$title%") }
      showSearch.author foreach { author => query = query.on("author" -> s"%$author%") }
      showSearch.director foreach { director => query = query.on("director" -> s"%$director%") }
      showSearch.company foreach { company => query = query.on("company" -> company) }
      query
    }
  }

}
