package vep.test.controller.show

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import vep.model.common.{ErrorCodes, ResultError, ResultSuccessEntity}
import vep.model.show._
import vep.service.AnormImplicits
import vep.test.controller.VepControllersDBInMemoryComponent

@RunWith(classOf[JUnitRunner])
class ShowControllerSearchSpecification extends Specification with VepControllersDBInMemoryComponent with AnormImplicits {
  def prepare(): Unit = {
    prepareDB("show/show-search")
  }

  // Usage of sequential because database state is reset between each test.
  sequential ^ "Specifications of ShowController" >> {
    "search should" >> {
      val defaultShowSearch = ShowSearch(p = None, t = None, a = None, d = None, c = None, o = None)

      "Returns the list of the first 20 entities ordered by title when no criteria" >> {
        prepare()
        val result = showController.search(defaultShowSearch)
        val expected = ShowSearchResponse(
          shows = Seq(
            ShowSearchResult(canonical = "amour-passion-et-cx-diesel", title = "Amour Passion et CX Diesel", author = "Celui-là même", director = Some("Un autre"), company = "la-cie-des-anjoues"),
            ShowSearchResult(canonical = "amour-passion-et-cx-diesel-2", title = "Amour Passion et CX Diesel 2", author = "Celui-là même", director = Some("Un autre"), company = "la-cie-des-anjoues"),
            ShowSearchResult(canonical = "dissonances", title = "Dissonances", author = "Editions Théâtrales", director = Some("Nicolas Berthoux"), company = "le-theater-de-la-queue-du-chat"),
            ShowSearchResult(canonical = "dissonances-2", title = "Dissonances 2", author = "Editions Théâtrales", director = Some("Nicolas Berthoux"), company = "le-theater-de-la-queue-du-chat"),
            ShowSearchResult(canonical = "donc", title = "Donc", author = "Jean-Yves PICQ", director = Some("Jean-Yves PICQ"), company = "l-atelier-de-la-comedie"),
            ShowSearchResult(canonical = "donc-2", title = "Donc 2", author = "Jean-Yves PICQ", director = Some("Jean-Yves PICQ"), company = "l-atelier-de-la-comedie"),
            ShowSearchResult(canonical = "le-mot-de-cambronne", title = "Le mot de Cambronne", author = "Sacha Guitry", director = Some("Sacha Guitry"), company = "uatl"),
            ShowSearchResult(canonical = "le-mot-de-cambronne-2", title = "Le mot de Cambronne 2", author = "Sacha Guitry", director = Some("Sacha Guitry"), company = "uatl"),
            ShowSearchResult(canonical = "le-mystere-de-la-baguette", title = "Le mystère de la baguette", author = "Interne A", director = Some("Olivier"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "le-mystere-de-la-baguette-2", title = "Le mystère de la baguette 2", author = "Interne E", director = Some("Olivier"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "les-justes", title = "Les Justes", author = "Albert Camus", director = Some("Bernard Clément"), company = "la-companie-de-l-ourson-blanc"),
            ShowSearchResult(canonical = "les-justes-2", title = "Les Justes 2", author = "Albert Camus", director = Some("Bernard Clément"), company = "la-companie-de-l-ourson-blanc"),
            ShowSearchResult(canonical = "poulard-et-fils", title = "Poulard et fils", author = "Bruno Chapelle", director = Some("Philippe Chauveau"), company = "replik"),
            ShowSearchResult(canonical = "poulard-et-fils-2", title = "Poulard et fils 2", author = "Bruno Chapelle", director = Some("Philippe Chauveau"), company = "replik"),
            ShowSearchResult(canonical = "scenes-de-vie", title = "Scènes de vie", author = "Interne D", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "scenes-de-vie-2", title = "Scènes de vie 2", author = "Interne H", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "t-emballe-pas", title = "T'emballe pas", author = "Christian Rossignol", director = Some("Mathieu Lancelot"), company = "scene-et-loire"),
            ShowSearchResult(canonical = "t-emballe-pas-2", title = "T'emballe pas 2", author = "Christian Rossignol", director = Some("Mathieu Lancelot"), company = "scene-et-loire"),
            ShowSearchResult(canonical = "ubu-roi", title = "Ubu roi", author = "Interne B", director = Some("Olivier"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "ubu-roi-2", title = "Ubu roi 2", author = "Interne F", director = Some("Olivier"), company = "atelier-theatre")
          ),
          pageMax = 2
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of the entities between 20 and 24 ordered by title when second page" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(p = Some(2)))
        val expected = ShowSearchResponse(
          shows = Seq(
            ShowSearchResult(canonical = "un-grand-cri-d-amour", title = "Un grand cri d'amour", author = "Josiane Balasko", director = Some("Bernard Clement"), company = "la-companie-de-l-ourson-blanc"),
            ShowSearchResult(canonical = "un-grand-cri-d-amour-2", title = "Un grand cri d'amour 2", author = "Josiane Balasko", director = Some("Bernard Clement"), company = "la-companie-de-l-ourson-blanc"),
            ShowSearchResult(canonical = "voyage-en-absurdie", title = "Voyage en absurdie", author = "Interne C", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "voyage-en-absurdie-2", title = "Voyage en absurdie 2", author = "Interne G", director = Some("Leslie Quévet"), company = "atelier-theatre")
          ),
          pageMax = 2
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of entities containing 'e' in their title ordered by title" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(t = Some("e")))
        val expected = ShowSearchResponse(
          shows = Seq(
            ShowSearchResult(canonical = "amour-passion-et-cx-diesel", title = "Amour Passion et CX Diesel", author = "Celui-là même", director = Some("Un autre"), company = "la-cie-des-anjoues"),
            ShowSearchResult(canonical = "amour-passion-et-cx-diesel-2", title = "Amour Passion et CX Diesel 2", author = "Celui-là même", director = Some("Un autre"), company = "la-cie-des-anjoues"),
            ShowSearchResult(canonical = "dissonances", title = "Dissonances", author = "Editions Théâtrales", director = Some("Nicolas Berthoux"), company = "le-theater-de-la-queue-du-chat"),
            ShowSearchResult(canonical = "dissonances-2", title = "Dissonances 2", author = "Editions Théâtrales", director = Some("Nicolas Berthoux"), company = "le-theater-de-la-queue-du-chat"),
            ShowSearchResult(canonical = "le-mot-de-cambronne", title = "Le mot de Cambronne", author = "Sacha Guitry", director = Some("Sacha Guitry"), company = "uatl"),
            ShowSearchResult(canonical = "le-mot-de-cambronne-2", title = "Le mot de Cambronne 2", author = "Sacha Guitry", director = Some("Sacha Guitry"), company = "uatl"),
            ShowSearchResult(canonical = "le-mystere-de-la-baguette", title = "Le mystère de la baguette", author = "Interne A", director = Some("Olivier"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "le-mystere-de-la-baguette-2", title = "Le mystère de la baguette 2", author = "Interne E", director = Some("Olivier"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "les-justes", title = "Les Justes", author = "Albert Camus", director = Some("Bernard Clément"), company = "la-companie-de-l-ourson-blanc"),
            ShowSearchResult(canonical = "les-justes-2", title = "Les Justes 2", author = "Albert Camus", director = Some("Bernard Clément"), company = "la-companie-de-l-ourson-blanc"),
            ShowSearchResult(canonical = "poulard-et-fils", title = "Poulard et fils", author = "Bruno Chapelle", director = Some("Philippe Chauveau"), company = "replik"),
            ShowSearchResult(canonical = "poulard-et-fils-2", title = "Poulard et fils 2", author = "Bruno Chapelle", director = Some("Philippe Chauveau"), company = "replik"),
            ShowSearchResult(canonical = "scenes-de-vie", title = "Scènes de vie", author = "Interne D", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "scenes-de-vie-2", title = "Scènes de vie 2", author = "Interne H", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "t-emballe-pas", title = "T'emballe pas", author = "Christian Rossignol", director = Some("Mathieu Lancelot"), company = "scene-et-loire"),
            ShowSearchResult(canonical = "t-emballe-pas-2", title = "T'emballe pas 2", author = "Christian Rossignol", director = Some("Mathieu Lancelot"), company = "scene-et-loire"),
            ShowSearchResult(canonical = "voyage-en-absurdie", title = "Voyage en absurdie", author = "Interne C", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "voyage-en-absurdie-2", title = "Voyage en absurdie 2", author = "Interne G", director = Some("Leslie Quévet"), company = "atelier-theatre")
          ),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of entities containing an author with 'interne'" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(a = Some("interne")))
        val expected = ShowSearchResponse(
          shows = Seq(
            ShowSearchResult(canonical = "le-mystere-de-la-baguette", title = "Le mystère de la baguette", author = "Interne A", director = Some("Olivier"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "le-mystere-de-la-baguette-2", title = "Le mystère de la baguette 2", author = "Interne E", director = Some("Olivier"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "scenes-de-vie", title = "Scènes de vie", author = "Interne D", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "scenes-de-vie-2", title = "Scènes de vie 2", author = "Interne H", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "ubu-roi", title = "Ubu roi", author = "Interne B", director = Some("Olivier"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "ubu-roi-2", title = "Ubu roi 2", author = "Interne F", director = Some("Olivier"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "voyage-en-absurdie", title = "Voyage en absurdie", author = "Interne C", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "voyage-en-absurdie-2", title = "Voyage en absurdie 2", author = "Interne G", director = Some("Leslie Quévet"), company = "atelier-theatre")
          ),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of entities containing a director with 'leslie'" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(d = Some("leslie")))
        val expected = ShowSearchResponse(
          shows = Seq(
            ShowSearchResult(canonical = "scenes-de-vie", title = "Scènes de vie", author = "Interne D", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "scenes-de-vie-2", title = "Scènes de vie 2", author = "Interne H", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "voyage-en-absurdie", title = "Voyage en absurdie", author = "Interne C", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "voyage-en-absurdie-2", title = "Voyage en absurdie 2", author = "Interne G", director = Some("Leslie Quévet"), company = "atelier-theatre")
          ),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of entities containing a company as 'la-companie-de-l-ourson-blanc'" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(c = Some("la-companie-de-l-ourson-blanc")))
        val expected = ShowSearchResponse(
          shows = Seq(
            ShowSearchResult(canonical = "les-justes", title = "Les Justes", author = "Albert Camus", director = Some("Bernard Clément"), company = "la-companie-de-l-ourson-blanc"),
            ShowSearchResult(canonical = "les-justes-2", title = "Les Justes 2", author = "Albert Camus", director = Some("Bernard Clément"), company = "la-companie-de-l-ourson-blanc"),
            ShowSearchResult(canonical = "un-grand-cri-d-amour", title = "Un grand cri d'amour", author = "Josiane Balasko", director = Some("Bernard Clement"), company = "la-companie-de-l-ourson-blanc"),
            ShowSearchResult(canonical = "un-grand-cri-d-amour-2", title = "Un grand cri d'amour 2", author = "Josiane Balasko", director = Some("Bernard Clement"), company = "la-companie-de-l-ourson-blanc")
          ),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns the list of entities containing a title with 'le' and an author with 'in'" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(t = Some("le"), a = Some("in")))
        val expected = ShowSearchResponse(
          shows = Seq(
            ShowSearchResult(canonical = "le-mystere-de-la-baguette", title = "Le mystère de la baguette", author = "Interne A", director = Some("Olivier"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "le-mystere-de-la-baguette-2", title = "Le mystère de la baguette 2", author = "Interne E", director = Some("Olivier"), company = "atelier-theatre")
          ),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns no entity containing a title with 'le' and an author with 'in' when page upper than maximal page" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(t = Some("le"), a = Some("in"), p = Some(2)))
        val expected = ShowSearchResponse(
          shows = Seq(),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns no entity containing a title with 'abcdef'" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(t = Some("abcdef")))
        val expected = ShowSearchResponse(
          shows = Seq(),
          pageMax = 0
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns entities containing director 'leslie' ordered by author" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(d = Some("leslie"), o = Some("a")))
        val expected = ShowSearchResponse(
          shows = Seq(
            ShowSearchResult(canonical = "voyage-en-absurdie", title = "Voyage en absurdie", author = "Interne C", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "scenes-de-vie", title = "Scènes de vie", author = "Interne D", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "voyage-en-absurdie-2", title = "Voyage en absurdie 2", author = "Interne G", director = Some("Leslie Quévet"), company = "atelier-theatre"),
            ShowSearchResult(canonical = "scenes-de-vie-2", title = "Scènes de vie 2", author = "Interne H", director = Some("Leslie Quévet"), company = "atelier-theatre")
          ),
          pageMax = 1
        )

        (result must beAnInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]]) and
          (result.asInstanceOf[Right[_, ResultSuccessEntity[ShowSearchResponse]]].b.entity must beEqualTo(expected))
      }

      "Returns an error when page is negative" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(p = Some(-1)))

        (result must beAnInstanceOf[Left[ResultError, _]]) and
          (result.asInstanceOf[Left[ResultError, _]].a.code must beEqualTo(ErrorCodes.negativeOrNull))
      }

      "Returns an error when page is null" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(p = Some(0)))

        (result must beAnInstanceOf[Left[ResultError, _]]) and
          (result.asInstanceOf[Left[ResultError, _]].a.code must beEqualTo(ErrorCodes.negativeOrNull))
      }

      "Returns an error when order is invalid" >> {
        prepare()
        val result = showController.search(defaultShowSearch.copy(o = Some("abcdef")))

        (result must beAnInstanceOf[Left[ResultError, _]]) and
          (result.asInstanceOf[Left[ResultError, _]].a.code must beEqualTo(ErrorCodes.unknownOrder))
      }
    }
  }
}

