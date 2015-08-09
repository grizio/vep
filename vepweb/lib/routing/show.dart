part of vep.routing;

abstract class ShowRouter {
  App get app;

  get showRoute => {
    'show-create': ngRoute(
        path: '/create',
        view: '/public/views/show/form.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb()
        .child('shows', '/shows', 'Pièces')
        .child('show-form', '/show/create', 'Nouvelle pièce')
    ),
    'show-update': ngRoute(
        path: '/update/:canonical',
        view: '/public/views/show/form.html',
        preEnter: (RoutePreEnterEvent _) => app.breadCrumb = new BreadCrumb()
        .child('shows', '/shows', 'Pièces')
        .child('show-form', '/show/update/${_.parameters['canonical']}', 'Mise à jour de la pièce')
    ),
    'show-read': ngRoute(
        path: '/read/:canonical',
        view: '/public/views/show/read.html',
        preEnter: (RoutePreEnterEvent _) => app.breadCrumb = new BreadCrumb()
        .child('shows', '/shows', 'Pièces')
        .child('show-read', '/show/read/${_.parameters['canonical']}', '')
    ),
    'show-list': ngRoute(
        path: 's', // =shows
        view: '/public/views/show/list.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb().child('shows', '/shows', 'Pièces')
    )
  };
}