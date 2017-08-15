import preact from "preact";
import {isNotLetter} from "../utils/strings";
import * as arrays from "../utils/arrays";
import {Link} from "preact-router/src";

interface RichContentProps {
  content: string
  limit?: number
}

export function RichContent(props: RichContentProps) {
  if (props.content) {
    return (
      <div class="rich-content">
        {format(getLimitedText(props.content, props.limit))}
      </div>
    )
  } else {
    return (
      <div class="rich-content"/>
    )
  }
}

function getLimitedText(content: string, limit: number): string {
  if (limit) {
    let result = content.substr(0, limit)
    for (let i = limit ; i < content.length ; i++) {
      if (isNotLetter(content.charAt(i))) {
        return result + "…"
      } else {
        result += content.charAt(i)
      }
    }
    return result
  } else {
    return content
  }
}

type GroupType = "p" | "h2" | "h3" | "h4" | "h5" | "ul" | "blockquote" | "hr";

interface Group {
  type: GroupType
  lines: Array<string>
}

function format(content: string) {
  return group(content.split("\n")).map(formatGroup)
}

function group(lines: Array<string>): Array<Group> {
  const result: Array<Group> = [];
  let remainingLines = lines;
  while (remainingLines.length) {
    const [group, nextLines] = processNextGroup(remainingLines)
    if (group) {
      result.push(group)
    }
    remainingLines = nextLines
  }
  return result;
}

function processNextGroup(lines: Array<string>): [Group, Array<string>] {
  const firstLine = lines[0]
  if (firstLine.startsWith("####")) {
    return [{type: "h5", lines: [firstLine.substr(4)]}, lines.slice(1)]
  } else if (firstLine.startsWith("###")) {
    return [{type: "h4", lines: [firstLine.substr(3)]}, lines.slice(1)]
  } else if (firstLine.startsWith("##")) {
    return [{type: "h3", lines: [firstLine.substr(2)]}, lines.slice(1)]
  } else if (firstLine.startsWith("#")) {
    return [{type: "h2", lines: [firstLine.substr(1)]}, lines.slice(1)]
  } else if (firstLine.startsWith("*")) {
    const [groupLines, remainingLines] = extractLinesByPrefix(lines, "*")
    return [{type: "ul", lines: groupLines}, remainingLines]
  } else if (firstLine.startsWith(">")) {
    const [groupLines, remainingLines] = extractLinesByPrefix(lines, ">")
    return [{type: "blockquote", lines: groupLines}, remainingLines]
  } else if (firstLine.startsWith("---")) {
    return [{type: "hr", lines: []}, lines.slice(1)]
  } else if (firstLine.trim().length) {
    return [{type: "p", lines: [firstLine]}, lines.slice(1)]
  } else {
    return [null, lines.slice(1)]
  }
}

function extractLinesByPrefix(lines: Array<string>, prefix: string): [Array<string>, Array<string>] {
  const groupLines = []
  let i;
  for (i = 0 ; i < lines.length ; i++) {
    const line = lines[i]
    if (line.startsWith(prefix)) {
      groupLines.push(line.substr(prefix.length))
    } else {
      break
    }
  }
  return [groupLines, lines.slice(i)]
}

function formatGroup(group: Group): preact.VNode {
  switch (group.type) {
    case "p":
      return <p>{group.lines.map(formatInline)}</p>
    case "h2":
      return <h2>{group.lines.map(formatInline)}</h2>
    case "h3":
      return <h3>{group.lines.map(formatInline)}</h3>
    case "h4":
      return <h4>{group.lines.map(formatInline)}</h4>
    case "h5":
      return <h5>{group.lines.map(formatInline)}</h5>
    case "ul":
      return (
        <ul>
          {group.lines.map(line => <li>{formatInline(line)}</li>)}
        </ul>
      )
    case "blockquote":
      return (
        <blockquote>
          <p>
            {format(group.lines.join("\n"))}
          </p>
        </blockquote>
      )
    case "hr":
      return <hr />
  }
}

const strong = new RegExp("(.*)\\*\\*(.*)\\*\\*(.*)")
const em = new RegExp("(.*)//(.*)//(.*)")
const image = new RegExp("(.*)\\[\\[(.*)\\]\\](.*)")
const customUrl = new RegExp("(.*)\\[([^\\]]+)\\]\\(([^\\)]+)\\)(.*)")
const url = new RegExp("(.*)(https?:\\/\\/[a-z0-9-]+(\\.[a-z0-9-]+)*(\\/[^\\s]+)*)(.*)")

function formatInline(content: string): Array<preact.VNode | Element | string> {
  if (content && content.length) {
    if (strong.test(content)) {
      const strongResult = strong.exec(content)
      return arrays.concat(
        formatInline(strongResult[1]),
        [<strong>{formatInline(strongResult[2])}</strong>],
        formatInline(strongResult[3])
      )
    } else if (em.test(content)) {
      const emResult = em.exec(content)
      return arrays.concat(
        formatInline(emResult[1]),
        [<em>{formatInline(emResult[2])}</em>],
        formatInline(emResult[3])
      )
    } else if (image.test(content)) {
      const imageResult = image.exec(content)
      return arrays.concat(
        formatInline(imageResult[1]),
        [<img src={imageResult[2]} />],
        formatInline(imageResult[3])
      )
    } else if (customUrl.test(content)) {
      const customUrlResult = customUrl.exec(content)
      return arrays.concat(
        formatInline(customUrlResult[1]),
        [<Link href={customUrlResult[3]}>{customUrlResult[2]}</Link>],
        formatInline(customUrlResult[4])
      )
    } else if (url.test(content)) {
      const urlResult = url.exec(content)
      return arrays.concat(
        formatInline(urlResult[1]),
        [<Link href={urlResult[2]}>{urlResult[2]}</Link>],
        formatInline(urlResult[5])
      )
    } else {
      return [content]
    }
  } else {
    return [""]
  }
}