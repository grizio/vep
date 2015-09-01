package vep.service.session

import anorm.SqlParser._
import anorm._
import org.joda.time.DateTime
import vep.AnormClient
import vep.model.session.{SessionSearch, SessionSearchColumns, SessionSearchResult}
import vep.service.{AnormImplicits, VepServicesComponent}
import vep.utils.{DB, DateUtils, SQLUtils}

trait SessionSearchServiceComponent {
  def sessionSearchService: SessionSearchService

  trait SessionSearchService {
    /**
     * Searches sessions in terms of given criteria.
     * Limits results with future sessions only.
     *
     * @param sessionSearch The session search
     * @param maxResult The maximum of results to return
     * @return Thi lest of sessions
     */
    def search(sessionSearch: SessionSearch, maxResult: Int): Seq[SessionSearchResult]

    /**
     * Counts the number of results according to given search criteria
     * @param sessionSearch The criteria of search
     * @return The number of lines according to given search
     */
    def count(sessionSearch: SessionSearch): Int
  }

}

trait SessionSearchServiceProductionComponent extends SessionSearchServiceComponent {
  self: AnormClient with VepServicesComponent =>

  lazy val sessionSearchService = new SessionSearchServiceProduction

  class SessionSearchServiceProduction extends SessionSearchService with AnormImplicits {
    override def search(sessionSearch: SessionSearch, maxResult: Int): Seq[SessionSearchResult] = DB.withConnection { implicit c =>
      val order = SessionSearchColumns.orderColumn(sessionSearch.order getOrElse "default")
      val sessions = querySearchCriteria(
        s"""
           |SELECT t.canonical, t.name as theaterName, s.id, s.canonical, s.date,
           |(SELECT min(sh.canonical) FROM session_show ss JOIN shows sh on ss.shows = sh.id WHERE ss.session = s.id) as shows
           |FROM session s
           |JOIN theater t ON s.theater = t.id
           |WHERE ${sqlSearchCriteria(sessionSearch)}
            |ORDER BY $order ASC ${if (order != "date") ", date ASC" else ""}
            |LIMIT ${((sessionSearch.page getOrElse 1) - 1) * maxResult}, $maxResult
        """.stripMargin,
        sessionSearch
      ).as(SessionParsers.sessionSearchParser *)

      sessions map { session =>
        val shows = SQL("SELECT canonical, title FROM shows s JOIN session_show ss ON s.id = ss.shows WHERE ss.session = {session} ORDER BY ss.num ASC")
          .on("session" -> session.id)
          .as(SessionParsers.sessionSearchShowParser *)

        SessionSearchResult(session.canonical, session.theater, session.theaterName, shows, session.date)
      }
    }

    override def count(sessionSearch: SessionSearch): Int = DB.withConnection { implicit c =>
      querySearchCriteria(
        s"""
           |SELECT count(*)
           |FROM session s
           |JOIN theater t ON s.theater = t.id
           |WHERE ${sqlSearchCriteria(sessionSearch)}
        """.stripMargin,
        sessionSearch
      ).as(scalar[Int].single)
    }

    def sqlSearchCriteria(sessionSearch: SessionSearch): String =
      SQLUtils.buildWhereQuery(
        sessionSearch.theater map { t => "t.canonical LIKE {theater}" } getOrElse "",
        sessionSearch.show map { s => "s.id IN (SELECT session FROM session_show WHERE shows IN (SELECT id FROM shows WHERE title LIKE {show}))" } getOrElse "",
        (sessionSearch.startDate, sessionSearch.endDate) match {
          case (Some(sd), Some(ed)) => "s.date BETWEEN {startDate} AND {endDate}"
          case (Some(sd), None) => "s.date >= {startDate}"
          case (None, Some(ed)) => "s.date <= {endDate}"
          case (None, None) => ""
        },
        "s.date >= {now}"
      )

    def querySearchCriteria(sql: String, sessionSearch: SessionSearch): SimpleSql[Row] = {
      var query: SimpleSql[Row] = SQL(sql)
      sessionSearch.theater foreach { t => query = query.on("theater" -> t) }
      sessionSearch.show foreach { s => query = query.on("show" -> s"%$s%") }
      sessionSearch.dtStartDate foreach { sd => query = query.on("startDate" -> DateUtils.toStringSQL(sd)) }
      sessionSearch.dtEndDate foreach { ed => query = query.on("endDate" -> DateUtils.toStringSQL(ed)) }
      query = query.on("now" -> DateUtils.toStringSQL(new DateTime()))
      query
    }
  }

}