import preact from "preact";

interface PageProps {
  title: string
}

export default function Page(props: PageProps) {
  return (
    <div class="page">
      <header>
        {props.title}
      </header>
      <section>
        {props["children"]}
      </section>
    </div>
  )
}