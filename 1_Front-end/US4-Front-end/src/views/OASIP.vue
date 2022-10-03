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
  eventNotes: null
})

const resetPostUI = () => {
  for(let key in postUI.value) {
    postUI.value[key] = null;
  }
}

const isEditing = ref(false);

const toggleEdit = () => {
  isEditing.value = !isEditing.value;
}

const isModalOpen = ref(false);

const toggleModal = () => {
  if(eventCategories.value === null || eventCategories.value === []) {
    alert("No event category found");
    return;
  }
  isModalOpen.value = !isModalOpen.value;
}
</script>

<template>
<div class="pb-5">
  <div class="bg-black p-4 px-7 text-white">
    <h1 class="font-semibold text-2xl">OASIP</h1>
    <p v-if="currentName === null" class="text-l inline">Online Appointment Scheduling System for Integrated Project Clinics</p>
    <p v-else class="text-l inline">Welcome user: <span class="ml-1" style="color:Lime;">{{currentName.slice(0, 30)}}</span></p>
    <button class="mr-10 font-semibold float-right" @click="toggleModal">Event Category</button>
  </div>
  <div>
    <div>
      <view-config-event-category
        v-if="isModalOpen"
        :eventCategoryList='eventCategories'
        :selectedCategory='selectedCategory'
        @callPutEventCategory="putEventCategory"
        @toggleModal="toggleModal"
      />
    </div>
  </div>
  <div class="px-8">
    <div class="mt-8 grid grid-cols-2 gap-x-10 gap-y-8 rounded-lg">
      <!-- Event List & Filter -->
      <div class="bg-neutral-100 rounded-lg col-span-1 row-span-2 p-3"> 
        <h2 class="font-semibold">Event List : </h2>

        <div v-if="currentRole !== null">
          <filter-event
            :eventCategoryList='eventCategories'
            @setFilter="setFilter"
          />
          <div v-if="eventList.length > 0">
            <button v-for="page in maxPageNum" :key="page" @click="getEventsAsPage(page)" class="bg-black text-white rounded text-l active:bg-gray-600 py px-2 mx-1 mt-1 transition-color duration-000 delay-000">{{ page }}</button>
            <view-event-list 
              :eventList='eventList'
              :eventCategoryList='eventCategories'
              @callShowEvent="getEventById"
            />
          </div>
          <div class="m-5 text-l" v-else>
            <div v-if="currentFilter.by === 'all'">No scheduled event.</div>
            <div v-if="currentFilter.by === 'category'">No event of the specified category: {{currentFilter.category.eventCategoryName}}</div>
            <div v-if="currentFilter.by === 'upcoming'">No upcoming event.</div>
            <div v-if="currentFilter.by === 'past'">No past event.</div>
            <div v-if="currentFilter.by === 'date'">No event on the specified date: {{new Date(currentFilter.date).toDateString()}}</div>
          </div>
        </div>
        <div class="m-5 text-l" v-else>Please login.</div>
      </div>

      <!-- Create Event -->
      <div class="bg-neutral-700 rounded-lg p-3">
        <h2 class="font-semibold text-white">Create Event : </h2>
        <div v-if="currentRole === null || !currentRole.toString().match('lecturer')">
          <schedule-event
            :event='postUI'
            :eventCategoryList='eventCategories'
            @callCreateEvent="postEvent"
            :currentRole="currentRole"
            :currentEmail="currentEmail"
          />
        </div>
        <div class="m-5 text-l text-white" v-else>User prohibited.</div>
      </div>
      

      <!-- Event details -->
      <div class="bg-neutral-200 rounded-lg p-3">
        <h2 class="font-semibold">Show Details : </h2>
        <div v-if="currentRole !== null">
          <view-event-details
            v-if="!isEditing"
            :event='event'
            @callEditEvent="toggleEdit"
            @callRemoveEvent="deleteEvent"
            :currentRole="currentRole"
          />
          <div v-if="!currentRole.toString().match('lecturer')">
            <reschedule-event
            v-if="isEditing"
            :event='event'
            @callPutEventProceed="putEvent"
            @callPutEventCancel="toggleEdit"
            />
          </div>
        </div>
        <div class="m-5 text-l" v-else>Please login.</div>
      </div>

    </div>
  </div>
</div>
</template>

<style scoped></style>