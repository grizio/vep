import {Action} from 'fluxx'
import {ShowWithDependencies} from "../../production/show/showModel"
import {PageInformation} from "../pages/pageModel"

export const initialize = Action<{pages: Array<PageInformation>, shows: Array<ShowWithDependencies>}>("initialize")