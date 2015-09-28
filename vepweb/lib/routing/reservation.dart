part of vep.routing;

abstract class ReservationRouter {
  App get app;

  get reservationRoute => {
    'reservation-create': ngRoute(
        path: '/:theater/:session',
        view: '/public/views/session/form-reservation.html',
        preEnter: (_) => app.breadCrumb = new BreadCrumb()
        .child('sessions', '/sessions', 'Séances')
        .child('reservation-form', '/reservation/${_.parameters['theater']}/${_.parameters['session']}', 'Réserver des places')
    )
  };
}