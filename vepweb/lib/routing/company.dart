part of vep.routing;

abstract class CompanyRouter {
  App get app;

  CompanyService get companyService;

  get companyRoute => {
    'company-one': ngRoute(
      path: 'y', // ="company"
      mount: {
        'company-create': ngRoute(
            path: '/create',
            view: '/public/views/company/form.html',
            preEnter: (_) => app.breadCrumb = new BreadCrumb()
            .child('company-management', '/companies', 'Gestion des troupes')
            .child('company-form', '/company/create', 'Nouvelle troupe')
        ),
        'company-update': ngRoute(
            path: '/update/:canonical',
            view: '/public/views/company/form.html',
            preEnter: (RoutePreEnterEvent _) => app.breadCrumb = new BreadCrumb()
            .child('company-management', '/companies', 'Gestion des troupes')
            .child('company-form', '/company/update/${_.parameters['canonical']}', 'Mise à jour de la troupe')
        ),
        'company-read': ngRoute(
            path: '/:canonical',
            view: '/public/views/company/read.html',
            preEnter: (_) => app.breadCrumb = new BreadCrumb().child('company-read', '/company/${_.parameters['canonical']}', '')
        )
      }
    ),
    'company-list': ngRoute(
      path: 'ies', // ="companies"
      view: '/public/views/company/list.html',
      preEnter: (_) => app.breadCrumb = new BreadCrumb().child('company-management', '/companies', 'Gestion des troupes')
    )
  };
}