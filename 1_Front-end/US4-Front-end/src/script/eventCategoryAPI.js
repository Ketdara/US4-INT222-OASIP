// This file:
// - manages API for EventCategory

export const eventCategoryAPI = {
  getEventCategories: async function () {
    var res = null;
    try {
      res = await fetch(import.meta.env.VITE_BASE_URL + 'event-categories');
    }catch(err) {
      alert('Error: Could not fetch event categories');
      return null;
    }
    if (res.status === 200) {
      let eventCategories = await res.json();
      console.log('[getEventCategories] successful');
      return eventCategories;
    }
    console.log('[getEventCategories] Error: Unknown');
    alert('Error occurred when getting event categories');
    return null;
  },

  putEventCategory: async function (eventCategory) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + `event-categories/${eventCategory.id}`, { method: 'PUT',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        id: eventCategory.id,
        eventCategoryName: eventCategory.eventCategoryName,
        eventCategoryDescription: eventCategory.eventCategoryDescription,
        eventDuration: eventCategory.eventDuration,
      })
    })

    if(res.status === 200){
      console.log('[putEventCategory] Successful');
      return true;
    }
    if(res.status === 400) {
      res.json().then(promise => {
        console.log('[putEventCategory] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log('[putEventCategory] Error: Unknown');
    alert('Error occurred when putting event category');
    return false;
  }
}