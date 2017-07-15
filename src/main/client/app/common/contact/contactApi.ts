import {request} from "../../framework/utils/http";
import {Contact} from "./contactModel";

export function sendContactMessage(contact: Contact) {
  return request({
    method: "POST",
    url: "/contact",
    entity: contact
  })
}