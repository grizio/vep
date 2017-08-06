export interface Blog {
  id: string
  title: string
  date: Date
  content: string
}

export interface BlogCreation {
  title: string
  content: string
}

export interface BlogUpdate {
  id: string
  title: string
  content: string
}