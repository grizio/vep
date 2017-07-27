import preact from "preact"
import {SessionState, sessionStore, SessionUser, UserRole} from "../session/sessionStore";
import StoreListenerComponent from "../utils/dom";
import {Function1} from "../lib";

interface HocOnLoggedInProps {
  user?: SessionUser
}

type OnLoggedInProps<P> = HocOnLoggedInProps & P

export function OnLoggedIn<P>(InnerComponent: new() => preact.Component<P, any> | Function1<P, JSX.Element> | any): preact.Component<P, any> {
  return class extends StoreListenerComponent<P, SessionState> {
    constructor() {
      super(sessionStore)
    }

    render(props: OnLoggedInProps<P>, state: SessionState) {
      if (state.user) {
        return (
          <InnerComponent
            {...props}
            user={state.user}
          />
        )
      } else {
        return null
      }
    }
  } as any
}

interface HocOnGrantedProps {
  user?: SessionUser
}

type OnGrantedProps<P> = HocOnGrantedProps & P

export function OnGranted<P>(InnerComponent: Function1<P, JSX.Element> | any, role: UserRole): Function1<OnGrantedProps<P>, JSX.Element> {
  return class extends StoreListenerComponent<P, SessionState> {
    constructor() {
      super(sessionStore)
    }

    render(props: OnGrantedProps<P>, state: SessionState) {
      if (state.user && state.user.role === role) {
        return (
          <InnerComponent
            {...props}
            user={state.user}
          />
        )
      } else {
        return null
      }
    }
  } as any
}