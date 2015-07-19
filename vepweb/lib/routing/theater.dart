part of vep.routing;

abstract class TheaterRouter {
  App get app;
  TheaterService get theaterService;

  get theaterRoute => {
    'theater-create': ngRoute(
        path: '/create',
        view: '/public/views/theater/form.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb()
        .child('theater-management', '/theaters', 'Gestion des théâtres')
        .child('theater-form', '/theater/create', 'Nouveau théâtre')
    ),
    'theater-update': ngRoute(
        path: '/update/:canonical',
        view: '/public/views/theater/form.html',
        preEnter: (RoutePreEnterEvent _) => app.breadCrumb = new BreadCrumb()
        .child('theater-management', '/theaters', 'Gestion des théâtres')
        .child('theater-form', '/theater/update/${_.parameters['canonical']}', 'Mise à jour du théâtre')
    ),
    'theater-list': ngRoute(
        path: 's', // ="/theaters"
        view: '/public/views/theater/list.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb().child('theater-management', '/theaters', 'Gestion des théâtres')
    ),
    'theater-read': ngRoute(
      path: '/:canonical',
      view: '/public/views/theater/read.html',
      preEnter: (_) => app.breadCrumb = new BreadCrumb().child('theater-read', '/theater/${_.parameters['canonical']}', '')
    )
  };
}