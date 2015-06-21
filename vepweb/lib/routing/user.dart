part of vep.routing;

abstract class UserRouter {
  App get app;

  get userRoute => {
    'register': ngRoute(
        path: '/register',
        view: '/public/views/user/register.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb().child('register', '/user/register', 'Inscription')
    ),
    'user-list-role': ngRoute(
      path: '/role/management',
      view: '/public/views/user/role-management.html',
      preEnter: (_) => app.breadCrumb = new BreadCrumb().child('user-role-management', '/user/role/management', 'Gestion des rôles')
    )
  };
}