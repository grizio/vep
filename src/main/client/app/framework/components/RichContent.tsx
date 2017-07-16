import preact from "preact";
import {isNotLetter} from "../utils/strings";

interface RichContentProps {
  content: string
  limit?: number
}

export function RichContent(props: RichContentProps) {
  return (
    <div class="rich-content">
      {
        getLimitedText(props.content, props.limit).split("\n").map(paragraph => <p>{paragraph}</p>)
      }
    </div>
  )
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