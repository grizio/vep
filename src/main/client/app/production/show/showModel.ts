import {Company} from "../company/companyModel";
import {Play} from "../play/playModel";

export interface Show {
  id: string
  title: string
  author: string
  director: string
  content: string
}

export interface ShowCreation {
  title: string
  author: string
  director: string
  content: string
}

export interface ShowMeta {
  id: string
  title: string
  company: string
}

export interface ShowWithDependencies {
  show: Show
  company: Company
  plays: Array<Play>
}