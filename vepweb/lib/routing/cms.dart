part of vep.routing;

abstract class CmsRouter {
  App get app;
  PageService get pageService;

  get cmsRoute => {
    'page-create': ngRoute(
        path: '/pages/create',
        view: '/public/views/cms/page/form.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb()
          .child('cms-page-management', '/cms/pages', 'Gestion des pages')
          .child('cms-page-form', '/cms/pages/create', 'Nouvelle page')
    ),
    'page-list': ngRoute(
        path: '/pages',
        view: '/public/views/cms/page/list.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb().child('cms-page-management', '/cms/pages', 'Gestion des pages')
    )
  };
}