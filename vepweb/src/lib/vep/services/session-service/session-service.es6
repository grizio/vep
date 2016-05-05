(() => {
  const http = window.vep.http;

  // @singleton
  class SessionService {
    find(theater, session) {
      return http.get(`/session/${theater}/${session}`);
    }

    findAll() {
      return new Promise((resolve, reject) => {
        // Current API adaptation - will need an update
        var process = (page, sessionsStack) => {
          http.get(`/sessions?p=${page}`).then((result) => {
            sessionsStack.push.apply(sessionsStack, result.sessions);
            if (page >= result.pageMax) {
              resolve(sessionsStack);
            } else {
              process(page + 1, sessionsStack);
            }
          })
        };
        process(1, []);
      });
    }

    create(session) {
      return http.post(`/session/${session.theater}`, {
        date: session.date,
        reservationEndDate: session.reservationEndDate,
        name: session.shows[0],
        prices: session.prices.map((p) => {
          return {
            name: p.name,
            price: parseInt(p.price) * 100,
            condition: p.condition
          }
        }),
        shows: session.shows
      });
    }

    reserve(theater, session, reservation) {
      return http.post(`reservation/${theater}/${session}`, reservation);
    }

    findReservedSeats(theater, session) {
      return http.get(`/reservation/${theater}/${session}/plan`).then((_) => _.seats);
    }

    findReservations(theater, session) {
      return http.get(`/reservation/${theater}/${session}/list`).then((_) => _.reservations);
    }
  }

  window.vep = window.vep || {};
  window.vep.services = window.vep.services || {};
  window.vep.services.session = new SessionService();
})();