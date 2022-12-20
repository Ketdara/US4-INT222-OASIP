<script setup>
defineEmits(['callEditEvent', 'callRemoveEvent'])
defineProps({
  event: {
    type: Object,
    default: null
  },
  currentRole: {
    type: String,
    default: null
  },
  isLoading: {
    type: Boolean,
    default: false
  }
})

const deleteConfirmation = () => {
  var isConfirm = confirm("Are you sure you want to remove this event?");
  return isConfirm;
}

</script>

<template>
  <div class="ml-5 mt-3">
    <div v-if="isLoading === true" class="font-semibold">Now loading...</div>
    <div v-else-if="Object.keys(event).length > 0">
      <ul class="my-4">
        <li class="m-2 font-bold text-l">Event: </li>
        <li class="m-2">▸ {{ event.eventCategory.eventCategoryName }}</li>
        <li class="m-2">▸ {{ new Date(event.eventStartTime) }}</li>
        <li class="m-2">▸ {{ event.eventDuration }} minutes</li>
      </ul>
      <ul class="my-4">
        <li class="m-2 font-bold text-l">Booking Information:</li>
        <li class="m-2">▸ {{ event.bookingName }} ({{ event.bookingEmail }})</li>
      </ul>
      <ul class="my-4">
        <li class="m-2 font-bold text-l">Additional Information:</li>
        <li class="m-2">▸ {{ event.eventNotes === null || event.eventNotes.length === 0 ? "No details" : event.eventNotes }}</li>
      </ul>
      <ul class="my-4">
        <li class="m-2 font-bold text-l">My attachment:</li>
        <li v-if="event.fileName === null" class="m-2">▸ No attachment </li>
        <li v-else class="m-2"><a :href="event.url" style="color :blue" :download="event.fileName">{{event.fileName}}</a></li>
      </ul>
      <div v-if="!currentRole.match('lecturer')" class="float-right">
        <button
          @click="$emit('callEditEvent');"
          class="font-semibold mt-1 mr-2 bg-black text-white rounded text-l hover:bg-gray-600 px-2 transition-color duration-200 delay-200">
          Edit
        </button>
        <button 
          @click="if(deleteConfirmation()) $emit('callRemoveEvent', event.id);" 
          class="font-semibold mt-1 bg-black text-white rounded text-l hover:bg-gray-600 px-2 transition-color duration-200 delay-200">
          Remove
        </button>
      </div>
    </div>
    <div v-else>No Selected Event.</div>
  </div>
</template>

<style scoped></style>
