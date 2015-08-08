package vep.test.router.api.show

import org.junit.runner.RunWith
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import vep.test.router.api.VepRouterDBInMemorySpecification

@RunWith(classOf[JUnitRunner])
class ShowRouterSearchSpecification extends Specification with VepRouterDBInMemorySpecification with JsonMatchers {
  def prepare() = prepareDB("user/users-with-roles", "show/show-search")

  sequential ^ "Specification of ShowRouter" >> {
    "search should" >> {
      val validSimpleUrl = "/shows"
      val validPageUrl = validSimpleUrl + "?p=2"
      val validTitleUrl = validSimpleUrl + "?t=e"
      val validAuthorUrl = validSimpleUrl + "?a=interne"
      val validDirectorUrl = validSimpleUrl + "?d=leslie"
      val validCompanyUrl = validSimpleUrl + "?c=la-companie-de-l-ourson-blanc"
      val validComplexUrl = validSimpleUrl + "?p=2&t=e&a=interne&d=leslie&c=la-companie-de-l-ourson-blanc"
      val ubuRoisUrl = validSimpleUrl + "?t=Ubu+roi&a=Interne+B&d=Olivier&c=atelier-theatre"

      "intercept a request to /shows as GET with valid entity" >> {
        prepare()
        Get(validSimpleUrl) ~>
          route ~> check {
          status === StatusCodes.OK
        }
      }

      "intercept a request to /shows?p=<page> as GET with valid entity" >> {
        prepare()
        Get(validPageUrl) ~>
          route ~> check {
          status === StatusCodes.OK
        }
      }

      "intercept a request to /shows?t=<title> as GET with valid entity" >> {
        prepare()
        Get(validTitleUrl) ~>
          route ~> check {
          status === StatusCodes.OK
        }
      }

      "intercept a request to /shows?a=<author> as GET with valid entity" >> {
        prepare()
        Get(validAuthorUrl) ~>
          route ~> check {
          status === StatusCodes.OK
        }
      }

      "intercept a request to /shows?d=<director> as GET with valid entity" >> {
        prepare()
        Get(validDirectorUrl) ~>
          route ~> check {
          status === StatusCodes.OK
        }
      }

      "intercept a request to /shows?c=<company> as GET with valid entity" >> {
        prepare()
        Get(validCompanyUrl) ~>
          route ~> check {
          status === StatusCodes.OK
        }
      }

      "intercept a request to /shows as GET with valid entity" >> {
        prepare()
        Get(validComplexUrl) ~>
          route ~> check {
          status === StatusCodes.OK
        }
      }

      "returns results validating given criteria" >> {
        prepare()
        Get(ubuRoisUrl) ~>
          route ~> check {
          (status === StatusCodes.OK) and (responseAs[String] must /("shows").andHave(exactly(
            /("canonical").andHave("ubu-roi") and
              /("title").andHave("Ubu roi") and
              /("author").andHave("Interne B") and
              /("director").andHave("Olivier") and
              /("company").andHave("atelier-theatre")
          )) and /("pageMax").andHave(1))
        }
      }
    }
  }
}