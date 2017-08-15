package vep.app.common.blog

import scalikejdbc._
import vep.framework.database.DatabaseContainer
import vep.framework.validation.{Valid, Validation}

class BlogService(
) extends DatabaseContainer {
  def findLast(): Seq[Blog] = withQueryConnection { implicit session =>
    findLastBlog()
  }

  private def findLastBlog()(implicit session: DBSession): Seq[Blog] = {
    sql"""
      SELECT * FROM blog
      ORDER BY date DESC
      LIMIT 5
    """
      .map(Blog.apply)
      .list()
      .apply()
  }

  def find(id: String): Option[Blog] = withQueryConnection { implicit session =>
    findBlog(id)
  }

  private def findBlog(id: String)(implicit session: DBSession): Option[Blog] = {
    sql"""
      SELECT * FROM blog
      WHERE id = ${id}
    """
      .map(Blog.apply)
      .single()
      .apply()
  }

  def create(blog: Blog): Validation[Blog] = withCommandTransaction { implicit session =>
    insertBlog(blog)
    Valid(blog)
  }

  private def insertBlog(blog: Blog)(implicit session: DBSession): Unit = {
    sql"""
      INSERT INTO blog(id, title, date, content)
      VALUES (${blog.id}, ${blog.title}, ${blog.date}, ${blog.content})
    """
      .execute()
      .apply()
  }

  def update(blog: Blog): Validation[Blog] = withCommandTransaction { implicit session =>
    updateBlog(blog)
    Valid(blog)
  }

  private def updateBlog(blog: Blog)(implicit session: DBSession): Unit = {
    sql"""
      UPDATE blog
      SET title = ${blog.title},
          content = ${blog.content}
      WHERE id = ${blog.id}
    """
      .execute()
      .apply()
  }
}