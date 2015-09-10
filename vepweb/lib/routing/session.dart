part of vep.routing;

abstract class SessionRouter {
  App get app;

  get sessionRoute => {
    'session-create': ngRoute(
        path: '/create',
        view: '/public/views/session/form.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb()
        .child('sessions', '/sessions', 'Séances')
        .child('session-form', '/session/create', 'Nouvelle séance')
    ),
    'session-update': ngRoute(
        path: '/update/:theater/:canonical',
        view: '/public/views/session/form-update.html',
        preEnter: (RoutePreEnterEvent _) => app.breadCrumb = new BreadCrumb()
        .child('sessions', '/sessions', 'Séances')
        .child('session-form', '/session/update/${_.parameters['theater']}/${_.parameters['canonical']}', 'Mise à jour de la séance')
    ),
    'session-read': ngRoute(
        path: '/read/:theater/:canonical',
        view: '/public/views/session/read.html',
        preEnter: (RoutePreEnterEvent _) => app.breadCrumb = new BreadCrumb()
        .child('sessions', '/sessions', 'Séances')
        .child('session-read', '/session/read/${_.parameters['theater']}/${_.parameters['canonical']}', '')
    ),
    'session-list': ngRoute(
        path: 's', // =shows
        view: '/public/views/session/list.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb().child('sessions', '/sessions', 'Séances')
    )
  };
}