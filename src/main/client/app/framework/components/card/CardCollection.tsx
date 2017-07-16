import preact from "preact"
import {fill, fillWith} from "../../utils/arrays";

interface CardCollectionProps {
  columns: number
  children?: Array<preact.VNode>
}

export default function CardCollection(props: CardCollectionProps) {
  if (props.children && props.children.length > 0) {
    const rows = Math.ceil(props.children.length / props.columns)
    return (
      <div>
        {fillWith(rows, (row) => renderRow(props, row))}
      </div>
    )
  } else {
    return null
  }
}

function renderRow(props: CardCollectionProps, row: number) {
  if ((row + 1) * props.columns > props.children.length) {
    const remainingColumns = (row + 1) * props.columns - props.children.length
    return (
      <div class="row">
        {props.children.slice(row * props.columns).map(renderColumn)}
        {fill(remainingColumns, renderColumn(null))}
      </div>
    )
  } else {
    return (
      <div class="row">
        {props.children.slice(row * props.columns, row * props.columns + props.columns).map(renderColumn)}
      </div>
    )
  }
}

function renderColumn(card: preact.VNode) {
  return <div class="col-1">{card}</div>
}