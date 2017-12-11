package vep.app.seo.views.render

import vep.app.production.company.show.ShowWithDependencies
import vep.app.production.company.show.play.PlayView
import vep.framework.utils.DateUtils

import scala.xml.NodeSeq

trait ShowRender extends Render {
  def renderShow(show: ShowWithDependencies): String = render(show.show.title) {
    <header>{show.show.title}</header>
    <section>
      <div class="">
        <div>
           <div>
            {renderShowPart(show)}
            {renderPlays(show)}
          </div>
        </div>
      </div>
    </section>
  }

  private def renderShowPart(show: ShowWithDependencies): NodeSeq = {
    <section>
      <h2>Présentation de {show.show.title}</h2>
      <div class="row">
        <div class="col-fill">
          {renderShowContent(show)}
        </div>
      </div>
    </section>
  }

  private def renderShowContent(show: ShowWithDependencies): NodeSeq = {
    RichContent.format(show.show.content)
  }

  private def renderPlays(show: ShowWithDependencies): NodeSeq = {
    <section>
      <h2>Prochaines séances pour {show.show.title}</h2>
      { Cards.renderCardCollection(
          columns = 3,
          children = getFuturePlays(show).map { play =>
            Cards.renderCard(
              title = DateUtils.longDate(play.date),
              children = Seq(
                <div>
                  <p>🏠 {play.theater.name}</p>
                  <p>Tarifs :</p>
                  <ul>
                    {play.prices.map { price =>
                    <li>{price.name}: {price.value}€ {if (price.condition.nonEmpty) s"(${price.condition})" else ""}</li>
                  }}
                  </ul>
                </div>
              )
            )
          }
      ) }
    </section>
  }

  private def getFuturePlays(show: ShowWithDependencies): Seq[PlayView] = {
    show.plays.filter(play => DateUtils.isAfterNow(play.date))
  }
}
