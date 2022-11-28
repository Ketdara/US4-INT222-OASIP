<script setup>
import { ref, onBeforeMount, onBeforeUpdate } from 'vue';
import { eventAPI } from "../script/eventAPI.js";
import { eventCategoryAPI } from "../script/eventCategoryAPI.js";
import ViewEventList from '../components/ViewEventList.vue';
import ViewEventDetails from '../components/ViewEventDetails.vue';
import ScheduleEvent from '../components/ScheduleEvent.vue';
import RescheduleEvent from '../components/RescheduleEvent.vue';
import FilterEvent from '../components/FilterEvent.vue';
import ViewConfigEventCategory from '../components/ViewConfigEventCategory.vue';

const event = ref({});
const eventList = ref([]);
const eventPage = ref(null);
const eventCategories = ref([]);
const selectedCategory = ref(null)

const maxPageNum = ref(1)
const selectedPageNum = ref(1)

const currentName = ref(null);
const currentEmail = ref(null);
const currentRole = ref(null);

onBeforeUpdate(async () => {
  updateCurrentUser();
})

const updateCurrentUser = () => {
  currentName.value = localStorage.getItem('name');
  currentEmail.value = localStorage.getItem('email');
  currentRole.value = localStorage.getItem('role');
}

var currentFilter = ref({
  by: "all",
  category: null,
  date: null
});

const updateEvents = async () => {
  if(maxPageNum.value < selectedPageNum.value) {
    selectedPageNum.value = maxPageNum.value
  }
  if(selectedPageNum.value < 1) {
    selectedPageNum.value = 1
  }
  await getEventsAsPage(selectedPageNum.value);
}

const setFilter = (filter) => {
  currentFilter.value = filter;
  updateEvents();
}

onBeforeMount(async () => {
  selectedPageNum.value = 1
  updateEvents();
  await getEventCategories();
  updateCurrentUser();
})

const getEventsAsPage = async (pageNum) => {
  try {
    eventPage.value = await eventAPI.getEventsAsPage(pageNum-1, currentFilter.value);
  }catch(err) {
    alert(err);
  }
  if(eventPage.value !== null) {
    eventList.value = eventPage.value.content;
    maxPageNum.value = eventPage.value.totalPages;
  }
}

const getEventCategories = async () => {
  eventCategories.value = await eventCategoryAPI.getEventCategories();
}

const getEventById = async (id) => {
  event.value = await eventAPI.getEventById(id);
  isEditing.value = false;
}

const postEvent = async (event) => {
  if(await eventAPI.postEvent(event)) {
    if(currentRole.value !== null) updateEvents();
    resetPostUI();
  }
}

const putEvent = async (event) => {
  if(await eventAPI.putEvent(event)) {
    updateEvents();
    await getEventById(event.id);
    isEditing.value = false;
  }
}

const putEventCategory = async (eventCategory) => {
  if(await eventCategoryAPI.putEventCategory(eventCategory)) {
    await getEventCategories();
    updateEvents();
  }
}

const deleteEvent = async (id) => {
  if(await eventAPI.deleteEventById(id)) {
    updateEvents();
    event.value = {};
  }
}

var postUI = ref({
  bookingName: null,
  bookingEmail: null,
  eventCategory: null,
  eventStartTime: null,
  eventNotes: null,
  attachment: []
})

const resetPostUI = () => {
  for(let key in postUI.value) {
    postUI.value[key] = null;
  }
}

</script>

 
<template>
<div>
    <div class="px-20 ml-10 -mr-10">
    <view-config-event-category
      :eventCategoryList='eventCategories'
      :selectedCategory='selectedCategory'
      @callPutEventCategory="putEventCategory"
    />
  </div>
</div>


</template>
 
<style>

</style>