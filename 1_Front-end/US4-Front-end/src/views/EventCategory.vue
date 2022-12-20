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

onBeforeMount(async () => {
  await getEventCategories();
})

const getEventCategories = async () => {
  eventCategories.value = await eventCategoryAPI.getEventCategories();
}

const putEventCategory = async (eventCategory) => {
  if(await eventCategoryAPI.putEventCategory(eventCategory)) {
    await getEventCategories();
    updateEvents();
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

<style scoped></style>