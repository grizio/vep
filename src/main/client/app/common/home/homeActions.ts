import { Action } from 'fluxx'
import {Blog} from "../blog/blogModel";

export const initialize = Action<Array<Blog>>("initialize")