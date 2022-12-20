<script setup>
defineEmits(['callShowEvent', 'callFilterEvent'])
defineProps({
  eventList: {
    type: Array,
    default: []
  }
})
</script>

<template>
  <div class="eventList">
    <table class="content-table">
      <thead>
        <tr class="rounded-lg h-full">
          <th>Date</th>
          <th>Start Time (Local)</th>
          <th>Duration (min)</th>
          <th>Category</th>
          <th>Booking Name</th>
          <th></th>
        </tr>
      </thead>
      <tbody v-for="event in eventList" :key="event">
        <tr>
          <td>{{ new Date(event.eventStartTime).toDateString().slice(4) }}</td>
          <td>{{ new Date(event.eventStartTime).toTimeString().slice(0, 5) }}</td>
          <td>{{ event.eventDuration }}</td>
          <td>{{ event.eventCategory.eventCategoryName }}</td>
          <td>{{ event.bookingName }}</td>
          <td>
            <button @click="$emit('callShowEvent', event.id)" class="font-semibold bg-black text-white rounded text-l hover:bg-gray-600 transition-color duration-200 delay-200">Show<br>Details</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.eventList {
  padding-top: 11px;
}
.content-table{
  border-collapse: collapse;
  font-size: small;
}
.content-table thead tr{
  background-color: black;
  text-align: left;
  color: white;
}
.content-table th,
.content-table td{
  padding: 11px 15px;
}
</style>