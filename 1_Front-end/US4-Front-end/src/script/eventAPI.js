// This file:
// - manages API for Event
// - translates Date-Time from Local <--> UTC

export const eventAPI = {
  timezoneOffset: new Date().getTimezoneOffset(),
  URLParam: null,

  localToUTC: function (event) {
    if(event.eventStartTime == null) return;
    event.eventStartTime = new Date(event.eventStartTime).toISOString();
  },
  UTCToLocal: function (event) {
    if(event.eventStartTime == null) return;
    event.eventStartTime = new Date(event.eventStartTime);
  },
  dateToUTCTime: function (date) {
    if(date == null) return null;
    return new Date(new Date(date).getTime() + (this.timezoneOffset * 60000)).toISOString();
  },
  nowToUTCTime: function () {
    return new Date().toISOString();
  },

  getURLParam: function(pageNum, filter) {
    var paging = `?page=${pageNum}`;
    try{
      if(filter.by === 'all') return paging;
      if(filter.by === 'category' && filter.category !== null) return `/category${paging}&categoryId=${filter.category.id}`;
      if(filter.by === 'upcoming') return `/upcoming${paging}&now=${this.nowToUTCTime()}`;
      if(filter.by === 'past') return `/past${paging}&now=${this.nowToUTCTime()}`;
      if(filter.by === 'date' && filter.date !== null) return `/date${paging}&date=${this.dateToUTCTime(filter.date)}`;
      throw "Filter parameter is missing";
    }catch(err) {
      throw "Filter parameter is missing";
    }
  },
  
  getEventsAsPage: async function (pageNum, filter) {
    try{
      this.URLParam = this.getURLParam(pageNum, filter);
    }catch(err) {
      alert(err);
      return null;
    }

    var res = null;
    try{
      res = await fetch(import.meta.env.VITE_BASE_URL + `events${this.URLParam}`);
    }catch(err) {
      alert('Error: Could not fetch events');
      return null;
    }

    if (res.status === 200) {
      let eventPage = await res.json();
      eventPage.content.forEach(event => {
        this.UTCToLocal(event);
      })
      console.log(`[getEventsAsPage: ${pageNum}] Successful`);
      return eventPage;
    }
    console.log(`[getEventsAsPage: ${pageNum}] Error: Unknown`);
    alert('Error occurred when getting events');
    return null;
  },

  getEventById: async function (id) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + `events/${id}`);
    if(res.status === 200) {
      let event = await res.json();
      this.UTCToLocal(event);
      console.log('[getEventById] Successful');
      return event;
    }
    if(res.status === 404){
      console.log('[getEventById] Error: Event not found or does not exist');
      alert('Error occurred: Event not found or does not exist');
      return null;
    }
    console.log('[getEventById] Error: Unknown');
    alert('Error occurred when getting event');
    return null;
  },

  postEvent: async function (event) {
    this.localToUTC(event);
    // console.log(event);
    const res = await fetch(import.meta.env.VITE_BASE_URL + 'events', { method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        bookingName: event.bookingName,
        bookingEmail: event.bookingEmail,
        eventCategory: event.eventCategory,
        eventStartTime: event.eventStartTime === "1970-01-01 00:00:00" ? null : event.eventStartTime,
        eventNotes: event.eventNotes
      })
    })

    if(res.status === 201){
      console.log('[postEvent] Successful');
      return true;
    }
    if(res.status === 400) {
      res.json().then(promise => {
        console.log('[postEvent] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log('[postEvent] Error: Unknown');
    alert('Error occurred when posting event');
    return false;
  },

  putEvent: async function (event) {
    this.localToUTC(event);
    // console.log(event);
    const res = await fetch(import.meta.env.VITE_BASE_URL + `events/${event.id}`, { method: 'PUT',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        id: event.id,
        bookingName: event.bookingName,
        bookingEmail: event.bookingEmail,
        eventCategory: event.eventCategory,
        eventStartTime: event.eventStartTime === "1970-01-01 00:00:00" ? null : event.eventStartTime,
        eventNotes: event.eventNotes
      })
    })

    if(res.status === 200){
      console.log('[putEvent] Successful');
      return true;
    }
    if(res.status === 400) {
      res.json().then(promise => {
        console.log('[putEvent] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log('[putEvent] Error: Unknown');
    alert('Error occurred when putting event');
    return false;
  },

  deleteEventById: async function (id) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + `events/${id}`, {method: 'DELETE'});

    if(res.status === 200) {
      console.log('[deleteEventById] Successful');
      return true;
    }
    if(res.status === 404){
      res.json().then(promise => {
        console.log('[deleteEventById] Error: Event not found or does not exist');
        alert('Error occurred: Event not found or does not exist');
      });
      return false;
    }
    console.log('[deleteEventById] Error: Unknown');
    alert('Error occurred when deleting event');
    return false;
  },
};