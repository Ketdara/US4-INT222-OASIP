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
      eventNotes: null,
      attachment: []
    }
  },
  currentRole: {
    type: String,
    default: null
  },
  currentEmail: {
    type: String,
    default: null
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
    bookingEmail: props.currentRole !== null && props.currentRole.toString().match('student') ? props.currentEmail : props.event.bookingEmail,
    eventCategory: newEventCategory.value ? newEventCategory.value : null,
    eventStartTime: props.event.eventStartTime,
    eventNotes: props.event.eventNotes ? props.event.eventNotes : "",
    attachment: props.event.attachment ? props.event.attachment : null
  }
});

const checkInputInvalid = () => {
  if(newEvent.value.bookingName === null || newEvent.value.bookingName.trim().length === 0 || newEvent.value.bookingName.trim().length > 100) return true;
  if(newEvent.value.bookingEmail === null || newEvent.value.bookingEmail.trim().length === 0 || newEvent.value.bookingEmail.trim().length > 100) return true;
  if(newEvent.value.eventCategory === null) return true;
  if(newEvent.value.eventStartTime === null) return true;
  if(newEvent.value.eventNotes.trim().length > 500) return true;
  if(newEvent.value.attachment != null && newEvent.value.attachment.size > 10485760) return true;
  return false;
}

const selectFile = (event) => {
  if(event.target.files[0].size > 10485760) {
    alert("File size exceeds 10 MB limit");
    return false;
  }
  if(event.target.files[0].name.length > 500) {
    alert("File name exceeds 500 character limit");
    return false;
  }
  props.event.attachment = event.target.files[0];
  return true;
}

const url = ref("")

const now = ref(new Date())
const nowFormatted = ref(now.value.getFullYear() + "-" + ("0"+(now.value.getMonth()+1)).slice(-2) + "-" +
("0" + now.value.getDate()).slice(-2) + "T" + ("0" + now.value.getHours()).slice(-2) + ":" + ("0" + now.value.getMinutes()).slice(-2))

</script>

<template>
  <div class="ml-5">
    <div class="flex">
      <div class="columnA text-white text-right">
        <ul>
          <li class="m-1">Event Category:</li>
          <li class="m-1">Booking Name:</li>
          <li class="m-1">Booking Email:</li>
          <li class="m-1">Event Start Time:</li>
          <li class="m-1 inline text-left">Event Notes:(optional)</li>
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
          <li class="m-1 text-white" v-if="currentRole !== null && currentEmail !== null && currentRole.toString().match('student')">{{newEvent.bookingEmail}}</li>
          <li class="m-1" v-else><input class="input" type="text" v-model="event.bookingEmail"></li>
          <li class="m-1"><input class="input" type="datetime-local" v-model="event.eventStartTime" :min=nowFormatted></li>
          <li class="m-1"><input class="input" type="text" v-model="event.eventNotes"></li>
          
        </ul>
      </div>
      <div class="columnC text-white">
        <ul>
          <li class="m-1"><br></li>
          <li class="m-1"><span v-if="event.bookingName !== null"><span v-if="event.bookingName.trim().length > 0" :style="event.bookingName.trim().length <= 100 ? { color: 'white' } : { color: 'red' }">{{event.bookingName.trim().length}}/100</span></span><br v-else></li>
          <li class="m-1"><span v-if="event.bookingEmail !== null"><span v-if="event.bookingEmail.trim().length > 0" :style="event.bookingEmail.trim().length <= 100 ? { color: 'white' } : { color: 'red' }">{{event.bookingEmail.trim().length}}/100</span></span><br v-else></li>
          <li class="m-1"><br></li>
          <li class="m-1"><span v-if="event.eventNotes !== null"><span v-if="event.eventNotes.trim().length > 0" :style="event.eventNotes.trim().length <= 500 ? { color: 'white' } : { color: 'red' }">{{event.eventNotes.trim().length}}/500</span></span><br v-else></li>
        </ul>
      </div>
    </div>
    <div class="mx-1">
      <ul>
        <li class="text-white">
          <span class="columnA text-right">Attachment(10 MB): (optional)</span>
          <span class="fileSelect"><input type="file" @change="selectFile">
            <span v-if="event.attachment !== null && event.attachment.name !== undefined && event.attachment.name !== null">
              {{event.attachment.name.length > 22 ? event.attachment.name.slice(0, 10) + "..." + event.attachment.name.slice(-10) : event.attachment.name}}
              <button type="button" class="close" aria-label="Close" @click="event.attachment = null">
                <span aria-hidden="true">&times;</span>
              </button>
            </span>
          </span>
        </li>
      </ul>
      
      <button 
        :disabled="checkInputInvalid()"
        :style="!checkInputInvalid() ? { color: 'white' } : { color: 'transparent' }"
        @click="$emit('callCreateEvent', newEvent)"
        class="font-semibold my-2 mt-2 bg-black text-white rounded text-l disabled:bg-transparent hover:bg-gray-600 px-4 transition-color duration-200 delay-200">
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
input[type='file'] {
  color: transparent;
  width: 115px;
  overflow:hidden;
}
.fileSelect {
  white-space: nowrap;
}
.inline {
  display: inline;
}
</style>
