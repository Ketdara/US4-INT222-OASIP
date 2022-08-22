<script setup>
import { ref, computed } from 'vue';

defineEmits(['callCreateEvent'])
const props = defineProps({
  eventCategoryList: {
    type: Array,
    default: []
  },
  eventCategoryList: {
    type: Array,
    default: []
  },
  event: {
    type: Object,
    default: {
      bookingName: null,
      bookingEmail: null,
      eventCategory: null,
      eventStartTime: null,
      eventNotes: null
    }
  }
})

const selectedCategory = ref(null)
const newEventCategory = computed(() => {
  if(props.eventCategoryList === null) return null;
  return props.eventCategoryList.find(category => category.eventCategoryName === selectedCategory.value)
});

const newEvent = computed(() => {
  return {
    bookingName: props.event.bookingName,
    bookingEmail: props.event.bookingEmail,
    eventCategory: newEventCategory.value ? newEventCategory.value : null,
    eventStartTime: props.event.eventStartTime,
    eventNotes: props.event.eventNotes ? props.event.eventNotes : ""
  }
});

const checkInputInvalid = () => {
  if(newEvent.value.bookingName === null || newEvent.value.bookingName.trim().length === 0 || newEvent.value.bookingName.trim().length > 100) return true;
  if(newEvent.value.bookingEmail === null || newEvent.value.bookingEmail.trim().length === 0 || newEvent.value.bookingEmail.trim().length > 100) return true;
  if(newEvent.value.eventCategory === null) return true;
  if(newEvent.value.eventStartTime === null) return true;
  if(newEvent.value.eventNotes.trim().length > 500) return true;
  return false;
}
</script>

<template>
<div class="ml-5">
  <div class="flex">
    <div class="columnA text-white">
      <ul>
        <li class="m-1">Event Category:</li>
        <li class="m-1">Booking Name:</li>
        <li class="m-1">Booking Email:</li>
        <li class="m-1">Event Start Time:</li>
        <li class="m-1">Event Notes:</li>
      </ul>
    </div>
    <div class="columnB">
      <ul>
        <li class="m-1">
          <select class="input" v-model="selectedCategory">
            <option v-for="eventCategory in eventCategoryList" :key="eventCategory">{{eventCategory.eventCategoryName}}</option>
          </select>
        </li>
        <li class="m-1"><input class="input" type="text" v-model="event.bookingName"></li>
        <li class="m-1"><input class="input" type="text" v-model="event.bookingEmail"></li>
        <li class="m-1"><input class="input" type="datetime-local" v-model="event.eventStartTime"></li>
        <li class="m-1"><input class="input" type="text" v-model="event.eventNotes"></li>
      </ul>
    </div><div class="columnC text-white">
      <ul>
        <li class="m-1"><br></li>
        <li class="m-1"><span v-if="event.bookingName !== null"><span v-if="event.bookingName.trim().length > 0" :style="event.bookingName.trim().length <= 100 ? { color: 'white' } : { color: 'red' }">{{event.bookingName.trim().length}}/100</span></span><br v-else></li>
        <li class="m-1"><span v-if="event.bookingEmail !== null"><span v-if="event.bookingEmail.trim().length > 0" :style="event.bookingEmail.trim().length <= 100 ? { color: 'white' } : { color: 'red' }">{{event.bookingEmail.trim().length}}/100</span></span><br v-else></li>
        <li class="m-1"><br></li>
        <li class="m-1"><span v-if="event.eventNotes !== null"><span v-if="event.eventNotes.trim().length > 0" :style="event.eventNotes.trim().length <= 500 ? { color: 'white' } : { color: 'red' }">{{event.eventNotes.trim().length}}/500</span></span><br v-else></li>
      </ul>
    </div>
  </div>
  <div class="m-1">
    <button 
    :disabled="checkInputInvalid()"
    :style="!checkInputInvalid() ? { color: 'white' } : { color: 'transparent' }"
    @click="$emit('callCreateEvent', newEvent)"
    class="font-semibold my-1 mt-2 bg-black text-white rounded text-l disabled:bg-transparent hover:bg-gray-600 px-4 transition-color duration-200 delay-200">
      Post
    </button>
  </div>
</div>
</template>

<style scoped>
.columnA {
  float: left;
  width: 160px;
}
.columnB {
  float: left;
  width: 280px;
}
.columnC {
  float: left;
  width: 100px;
}
.input {
  width: 260px;
  border-radius: 6px;
}

</style>
