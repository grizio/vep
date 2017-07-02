import preact from "preact";

interface Props {
  path: string
  user?: string
}

interface State {
  time: number
  count: number
}

export default class Profile extends preact.Component<Props, State> {
  timer: NodeJS.Timer

  state = {
    time: Date.now(),
    count: 10
  }

  // gets called when this route is navigated to
  componentDidMount() {
    // start a timer for the clock:
    this.timer = setInterval(this.updateTime, 1000);
  }

  // gets called just before navigating away from the route
  componentWillUnmount() {
    clearInterval(this.timer);
  }

  // update the current time
  updateTime = () => {
    this.setState({time: Date.now()});
  };

  increment = () => {
    this.setState({count: this.state.count + 1});
  };

  // Note: `user` comes from the URL, courtesy of our router
  render(props: Props, state: State) {
    const {user} = props
    const {time, count} = state
    return (
      <div class="profile">
        <h1>Profile: {user}</h1>
        <p>This is the user profile for a user named { user }.</p>

        <div>Current time: {new Date(time).toLocaleString()}</div>

        <p>
          <button onClick={this.increment}>Click Me</button>
          {' '}
          Clicked {count} times.
        </p>
      </div>
    );
  }
}
