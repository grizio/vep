package vep.app.seo.views

import vep.app.production.company.show.play.{PlayView, PlayWithDependencies}
import vep.framework.utils.{DateUtils, StringUtils}

import scala.xml.{Node, NodeSeq}

object PlayView extends View {
  def view(play: PlayWithDependencies, playsOfShow: Seq[PlayView]): String = render(title(play)) {
    <header>{title(play)}</header>
    <section>
      <div class="">
        <div>
          <div>
            {renderInformation(play, playsOfShow)}
          </div>
        </div>
      </div>
    </section>
  }

  def title(play: PlayWithDependencies): String = {
    s"${StringUtils.capitalizeFirstLetter(DateUtils.longDate(play.play.date))} • ${play.show.title}"
  }

  def renderInformation(play: PlayWithDependencies, playsOfShow: Seq[PlayView]): Node = {
    <section>
        <h2>{play.show.title}</h2>
        <div class="row">
          <div class="col-fill">
            {renderShowInformation(play)}
            {renderOtherPlays(play, playsOfShow)}
          </div>
        </div>
      </section>
  }

  def renderShowInformation(play: PlayWithDependencies): NodeSeq = {
    RichContent.format(play.show.content)
  }

  def renderOtherPlays(play: PlayWithDependencies, playsOfShow: Seq[PlayView]): Node = {
    val otherPlays = playsOfShow.filter(_.id != play.play.id).filter(_.date.isAfterNow)
    if (otherPlays.nonEmpty) {
      <div>
        <h3>Autres séances</h3>
        <ul>
          {otherPlays.map(otherPlay =>
            <li>
              <Link href={s"/production/companies/${play.company.id}/shows/${play.show.id}/plays/page/${otherPlay.id}"}>
                {StringUtils.capitalizeFirstLetter(DateUtils.longDate(otherPlay.date))}
              </Link>
            </li>
        )}
        </ul>
      </div>
    } else {
      <div></div>
    }
  }
}
