package vep.app.common.blog

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import vep.app.user.UserService
import vep.framework.router.RouterComponent

import scala.concurrent.ExecutionContext

class BlogRouter(
  blogVerifications: BlogVerifications,
  blogService: BlogService,
  val userService: UserService,
  val executionContext: ExecutionContext
) extends RouterComponent {
  lazy val route: Route = {
    findLast ~ find ~ create ~ update
  }

  def findLast: Route = publicGet("blog" / "last") {
    Ok(blogService.findLast())
  }

  def find: Route = publicGet("blog" / Segment).apply { blogId =>
    found(blogService.find(blogId)) { blog =>
      Ok(blog)
    }
  }

  def create: Route = adminPost("blog", as[BlogCreation]).apply { (blog, _) =>
    verifying(blogVerifications.verifyCreation(blog)) { validBlog =>
      verifying(blogService.create(validBlog)) { savedBlog =>
        Ok(savedBlog)
      }
    }
  }

  def update: Route = adminPut("blog" / Segment, as[BlogUpdate]).apply { (blogId, blog, _) =>
    found(blogService.find(blogId)) { existingBlog =>
      verifying(blogVerifications.verifyUpdate(blog, existingBlog)) { validBlog =>
        verifying(blogService.update(validBlog)) { savedBlog =>
          Ok(savedBlog)
        }
      }
    }
  }
}
