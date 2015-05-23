part of vep.routing;

abstract class UserRouter {
  App get app;

  get userRoute => {
    'register': ngRoute(
        path: '/register',
        view: '/public/views/user/register.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb().child('register', '/user/register', 'Inscription')
    )
  };
}