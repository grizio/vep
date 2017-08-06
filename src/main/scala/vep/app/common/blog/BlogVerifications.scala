package vep.app.common.blog

import vep.app.common.verifications.CommonVerifications
import vep.framework.validation._
import java.util.UUID

import org.joda.time.DateTime

class BlogVerifications(
  commonVerifications: CommonVerifications,
  pageService: BlogService
) {
  def verifyCreation(blog: BlogCreation): Validation[Blog] = {
    Validation.all(
      commonVerifications.verifyNonBlank(blog.title),
      commonVerifications.verifyNonBlank(blog.content)
    ).map {
      case title ~ content =>
        Blog(
          id = UUID.randomUUID().toString,
          title = title,
          date = DateTime.now,
          content = content
        )
    }
  }

  def verifyUpdate(blog: BlogUpdate, currentBlog: Blog): Validation[Blog] = {
    Validation.all(
      commonVerifications.verifyEquals(blog.id, currentBlog.id),
      commonVerifications.verifyNonBlank(blog.title),
      commonVerifications.verifyNonBlank(blog.content)
    ).map(_ => currentBlog.copy(
      title = blog.title,
      content = blog.content
    ))
  }
}
