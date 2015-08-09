part of vep.routing;

abstract class ShowRouter {
  App get app;

  get showRoute => {
    'show-list': ngRoute(
        path: 's', // =shows
        view: '/public/views/show/list.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb().child('shows', '/shows', 'Spectacles')
    )
  };
}