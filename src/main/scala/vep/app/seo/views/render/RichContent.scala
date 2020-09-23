package vep.app.seo.views.render

import scala.collection.mutable.ListBuffer
import scala.xml.{Node, NodeSeq, Text}

object RichContent {
  case class Group(
    _type: GroupType.Value,
    lines: Seq[String]
  )

  object GroupType extends Enumeration {
    val p, h2, h3, h4, h5, ul, blockquote, hr = Value
  }

  def format(content: String): NodeSeq = {
    group(content.split("\n")).map(formatGroup)
  }

  def group(lines: Seq[String]): Seq[Group] = {
    val result = ListBuffer[Group]()
    var remainingLines = lines
    while (remainingLines.nonEmpty) {
      val (group, nextLines) = processNextGroup(remainingLines)
      group.foreach(result.append(_))
      remainingLines = nextLines
    }
    result.toSeq
  }

  def processNextGroup(lines: Seq[String]): (Option[Group], Seq[String]) = {
    import GroupType._
    val firstLine = lines.head
    if (firstLine.startsWith("####")) {
      (Some(Group(h5, Seq(firstLine.substring(4)))), lines.drop(1))
    } else if (firstLine.startsWith("###")) {
      (Some(Group(h4, Seq(firstLine.substring(3)))), lines.drop(1))
    } else if (firstLine.startsWith("##")) {
      (Some(Group(h3, Seq(firstLine.substring(2)))), lines.drop(1))
    } else if (firstLine.startsWith("#")) {
      (Some(Group(h2, Seq(firstLine.substring(1)))), lines.drop(1))
    } else if (firstLine.startsWith("*")) {
      val (groupLines, remainingLines) = extractLinesByPrefix(lines, "*")
      (Some(Group(ul, groupLines)), remainingLines)
    } else if (firstLine.startsWith(">")) {
      val (groupLines, remainingLines) = extractLinesByPrefix(lines, ">")
      (Some(Group(blockquote, groupLines)), remainingLines)
    } else if (firstLine.startsWith("---")) {
      (Some(Group(hr, Seq.empty)), lines.drop(1))
    } else if (firstLine.trim().length > 0) {
      (Some(Group(p, Seq(firstLine))), lines.drop(1))
    } else {
      (None, lines.drop(1))
    }
  }

  def extractLinesByPrefix(lines: Seq[String], prefix: String): (Seq[String], Seq[String]) = {
    val groupLines = ListBuffer[String]()
    var i = 0
    while (i < lines.length && lines(i).startsWith(prefix)) {
      groupLines.append(lines(i).substring(prefix.length))
      i = i + 1
    }
    (groupLines.toSeq, lines.drop(i))
  }

  def formatGroup(group: Group): Node = {
    group._type match {
      case GroupType.p => <p>{group.lines.map(formatInline)}</p>
      case GroupType.h2 => <h2>{group.lines.map(formatInline)}</h2>
      case GroupType.h3 => <h3>{group.lines.map(formatInline)}</h3>
      case GroupType.h4 => <h4>{group.lines.map(formatInline)}</h4>
      case GroupType.h5 => <h5>{group.lines.map(formatInline)}</h5>
      case GroupType.ul => <ul>
            {group.lines.map(line => <li>{formatInline(line)}</li>)}
          </ul>
      case GroupType.blockquote => <blockquote>
            <p>
              {format(group.lines.mkString("\n"))}
            </p>
          </blockquote>
      case GroupType.hr => <hr />
    }
  }

  val strong = "(.*)\\*\\*(.*)\\*\\*(.*)".r
  val em = "(.*)//(.*)//(.*)".r
  val image = "(.*)\\[\\[(.*)\\]\\](.*)".r
  val customUrl = "(.*)\\[([^\\]]+)\\]\\(([^\\)]+)\\)(.*)".r
  val url = "(.*)(https?:\\/\\/[a-z0-9-]+(\\.[a-z0-9-]+)*(\\/[^\\s]+)*)(.*)".r

  def formatInline(content: String): NodeSeq = {
    if (content.nonEmpty) {
      if (strong.findFirstIn(content).nonEmpty) {
        val strongResult = strong.findFirstMatchIn(content).get
        Seq(
          formatInline(strongResult.group(1)),
          Seq(<strong>{formatInline(strongResult.group(2))}</strong>),
          formatInline(strongResult.group(3))
        ).flatten
      } else if (em.findFirstIn(content).nonEmpty) {
        val emResult = em.findFirstMatchIn(content).get
        Seq(
          formatInline(emResult.group(1)),
          Seq(<em>{formatInline(emResult.group(2))}</em>),
          formatInline(emResult.group(3))
        ).flatten
      } else if (image.findFirstIn(content).nonEmpty) {
        val imageResult = image.findFirstMatchIn(content).get
        Seq(
          formatInline(imageResult.group(1)),
          Seq(<img src={imageResult.group(2)} />),
          formatInline(imageResult.group(3))
        ).flatten
      } else if (customUrl.findFirstIn(content).nonEmpty) {
        val customUrlResult = customUrl.findFirstMatchIn(content).get
        Seq(
          formatInline(customUrlResult.group(1)),
          Seq(createLink(customUrlResult.group(3), customUrlResult.group(2))),
          formatInline(customUrlResult.group(4))
        ).flatten
      } else if (url.findFirstIn(content).nonEmpty) {
        val urlResult = url.findFirstMatchIn(content).get
        Seq(
          formatInline(urlResult.group(1)),
          Seq(createLink(urlResult.group(2), urlResult.group(2))),
          formatInline(urlResult.group(5))
        ).flatten
      } else {
        Seq(Text(content))
      }
    } else {
      Seq(Text(""))
    }
  }

  def createLink(href: String, content: String): Node = {
    if (href.startsWith("http://") || href.startsWith("https://")) {
      <a href={href} target="_blank">{content}</a>
    } else if (href.startsWith("/")) {
      <a href={href}>{content}</a>
    } else {
      <a href={s"//${href}"} target="_blank">{content}</a>
    }
  }
}
