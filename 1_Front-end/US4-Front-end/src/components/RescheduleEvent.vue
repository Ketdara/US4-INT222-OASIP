<script setup>
import { ref, computed } from 'vue';

defineEmits(['callPutEventProceed', 'callPutEventCancel'])
const props = defineProps({
  event: {
    type: Object,
    default: {
      id: null,
      bookingName: null,
      bookingEmail: null,
      eventCategory: null,
      eventStartTime: null,
      eventDuration: null,
      eventNotes: null
    }
  }
})

const startTimeInput = ref(new Date(new Date(props.event.eventStartTime).getTime() + 7*3600000).toISOString().slice(0, -8))

const putConfirmation = () => {
  if(confirm("Are you sure you want to confirm editing on this event?")) {
    props.event.eventStartTime = startTimeInput.value;
    return true;
  }
  return false;
}
</script>

<template>
<div class="ml-5 mt-3">
  <div v-if="Object.keys(event).length > 0">
    <ul class="my-4">
      <li class="m-2 font-bold text-l">Event: </li>
      <li class="m-2">▸ {{ event.eventCategory.eventCategoryName }}</li>
      <li class="m-2">▸ <input type="datetime-local" v-model="startTimeInput" class="input"></li>
      <li class="m-2">▸ {{ event.eventDuration }} minutes</li>
    </ul>
    <ul class="my-4">
      <li class="m-2 font-bold text-l">Booking Information:</li>
      <li class="m-2">▸ {{ event.bookingName }} ({{ event.bookingEmail }})</li>
    </ul>
    <ul class="my-4">
      <li class="m-2 font-bold text-l">Additional Information:</li>
      <li class="m-2">▸ <input type="text" v-model="event.eventNotes" class="input"><span class="ml-3 text-black" v-if="event.eventNotes !== null"><span v-if="event.eventNotes.trim().length > 0" :style="event.eventNotes.trim().length <= 500 ? { color: 'black' } : { color: 'red' }">{{event.eventNotes.trim().length}}/500</span></span></li>
    </ul>
    <div class="float-right">
      <button
        @click="if(putConfirmation()) $emit('callPutEventProceed', event);"
        class="font-semibold mt-1 mr-2 bg-black text-white rounded text-l hover:bg-gray-600 px-2 transition-color duration-200 delay-200">
        Confirm
      </button>
      <button 
        @click="$emit('callPutEventCancel');" 
        class="font-semibold mt-1 bg-black text-white rounded text-l hover:bg-gray-600 px-2 transition-color duration-200 delay-200">
        Cancel
      </button>
    </div>
  </div>
  <div v-else>No Selected Event.</div>
</div>
</template>

<style scoped>
.input {
  width: 240px;
  border-radius: 6px;
}
</style>