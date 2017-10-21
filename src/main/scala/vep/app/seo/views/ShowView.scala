package vep.app.seo.views

import vep.app.production.company.show.ShowWithDependencies
import vep.app.production.company.show.play.PlayView
import vep.framework.utils.DateUtils

import scala.xml.NodeSeq

object ShowView extends View {
  def view(show: ShowWithDependencies): String = render(show.show.title) {
    <header>{show.show.title}</header>
    <section>
      <div class="">
        <div>
           <div>
            {renderShow(show)}
            {renderPlays(show)}
          </div>
        </div>
      </div>
    </section>
  }

  def renderShow(show: ShowWithDependencies): NodeSeq = {
    <section>
      <h2>Présentation de {show.show.title}</h2>
      <div class="row">
        <div class="col-fill">
          {renderShowContent(show)}
        </div>
      </div>
    </section>
  }

  def renderShowContent(show: ShowWithDependencies): NodeSeq = {
    RichContent.format(show.show.content)
  }

  def renderPlays(show: ShowWithDependencies): NodeSeq = {
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

  def getFuturePlays(show: ShowWithDependencies): Seq[PlayView] = {
    show.plays.filter(play => play.date.isAfterNow)
  }
}
