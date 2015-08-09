part of vep.routing;

abstract class ShowRouter {
  App get app;

  get showRoute => {
    'show-create': ngRoute(
        path: '/create',
        view: '/public/views/show/form.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb()
        .child('show-management', '/shows', 'Gestion des pièces')
        .child('show-form', '/show/create', 'Nouvelle pièce')
    ),
    'show-update': ngRoute(
        path: '/update/:canonical',
        view: '/public/views/show/form.html',
        preEnter: (RoutePreEnterEvent _) => app.breadCrumb = new BreadCrumb()
        .child('show-management', '/shows', 'Gestion des pièces')
        .child('show-form', '/show/update/${_.parameters['canonical']}', 'Mise à jour de la pièce')
    ),
    'show-list': ngRoute(
        path: 's', // =shows
        view: '/public/views/show/list.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb().child('shows', '/shows', 'Spectacles')
    )
  };
}