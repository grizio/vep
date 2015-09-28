package vep.service.show

import anorm.SqlParser._
import anorm.~
import vep.model.show.{Show, ShowDetail, ShowSearchResult}

object ShowParsers {
  lazy val show =
    int("id") ~
      str("canonical") ~
      str("title") ~
      str("author") ~
      get[Option[String]]("director") ~
      int("company") ~
      get[Option[Int]]("duration") ~
      get[Option[String]]("content") map {
      case id ~ canonical ~ title ~ author ~ director ~ company ~ duration ~ content =>
        Show(id, canonical, title, author, director, company, duration, content)
    }

  lazy val showSearchResult =
    str("shows.canonical") ~
      str("title") ~
      str("author") ~
      get[Option[String]]("director") ~
      str("company") map {
      case canonical ~ title ~ author ~ director ~ company =>
        ShowSearchResult(canonical, title, author, director, company)
    }

  lazy val showDetail =
    str("shows.canonical") ~
      str("title") ~
      str("author") ~
      get[Option[String]]("director") ~
      str("company") ~
      get[Option[Int]]("duration") ~
      get[Option[String]]("content") map {
      case canonical ~ title ~ author ~ director ~ company ~ duration ~ content =>
        ShowDetail(canonical, title, author, director, company, duration, content)
    }
}
