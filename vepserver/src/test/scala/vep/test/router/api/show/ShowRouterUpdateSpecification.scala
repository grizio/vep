package vep.test.router.api.show

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.model.show.ShowFormBody
import vep.test.router.api.VepRouterDBInMemorySpecification

@RunWith(classOf[JUnitRunner])
class ShowRouterUpdateSpecification extends Specification with VepRouterDBInMemorySpecification {
  def prepare() = prepareDB("user/users-with-roles", "show/show-default")

   import InvalidShowEntitiesImplicits._
   import spray.httpx.SprayJsonSupport._
   import vep.model.show.ShowImplicits._

   sequential ^ "Specification of ShowRouter" >> {
     "update should" >> {
       val validUrl = "/show/existing-show"
       val validEntity = ShowFormBody(
         title = "My updated title",
         author = "My updated author",
         director = "My updated director",
         company = "existing-company-2",
         duration = None,
         content = None
       )
       val validEntityWithErrors = validEntity.copy(title = "")
       val invalidEntity = InvalidShowFormBody("")

       "intercept a request to /show/<canonical> as PUT with valid entity" >> {
         prepare()
         Put(validUrl, validEntity) ~>
           addCredentials(validCredentialsAdmin) ~>
           route ~> check {
           handled === true
         }
       }
       "refuse a request to /show/<canonical> as PUT when invalid entity" >> {
         prepare()
         Put(validUrl, invalidEntity) ~>
           addCredentials(validCredentialsAdmin) ~>
           route ~> check {
           handled === false
         }
       }
       "returns a code 401 when not authenticated" >> {
         prepare()
         Put(validUrl, validEntity) ~> route ~> check {
           status === StatusCodes.Unauthorized
         }
       }
       "returns a code 403 when authenticated but not authorized" >> {
         prepare()
         Put(validUrl, validEntity) ~>
           addCredentials(validCredentialsUser) ~>
           route ~> check {
           status === StatusCodes.Forbidden
         }
       }
       "returns a code 400 with map when error(s)" >> {
         prepare()
         Put(validUrl, validEntityWithErrors) ~>
           addCredentials(validCredentialsAdmin) ~>
           route ~> check {
           (status === StatusCodes.BadRequest) and
             (responseAs[String] must startWith("{"))
         }
       }
       "return a code 200 with nothing when success" >> {
         prepare()
         Put(validUrl, validEntity) ~>
           addCredentials(validCredentialsAdmin) ~>
           route ~> check {
           (status === StatusCodes.OK) and
             (responseAs[String] === "null")
         }
       }
     }
   }
 }