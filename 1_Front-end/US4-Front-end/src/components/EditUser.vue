<script setup>
import { ref, computed } from 'vue';

defineEmits(['callPutUserProceed', 'callPutUserCancel'])
const props = defineProps({
  roleList: {
    type: Array,
    default: []
  },
  user: {
    type: Object,
    default: {
      id: null,
      name: null,
      email: null,
      role: null,
    }
  }
})

const putConfirmation = () => {
  var sameUser = newName.value.trim() == props.user.name && newEmail.value.trim() == props.user.email && newRole.value.trim() == props.user.role;
  if(sameUser === true){
    alert("User information unchanged")
    return false;
  }
  if(!confirm("Are you sure you want to confirm editing on this user?")) return false;

  mapUser();
  return true;
}

const mapUser = () => {
  props.user.name = newName.value.trim();
  props.user.email = newEmail.value.trim();
  props.user.role = newRole.value.trim();
}

const newName = ref(props.user.name);
const newEmail = ref(props.user.email);
const newRole = ref(props.user.role);

</script>

<template>
  <div class="ml-5 mt-3">
    <div v-if="Object.keys(user).length > 0">
      <ul class="my-4">
      <li class="m-2 font-bold text-l">User Information: </li>
      <li class="m-2">▸ <input type="text" v-model="newName" class="input"><span class="ml-3 text-black" v-if="newName !== null"><span v-if="newName.trim().length > 0" :style="newName.trim().length <= 100 ? { color: 'black' } : { color: 'red' }">{{newName.trim().length}}/100</span></span></li>
      <li class="m-2">▸ <input type="text" v-model="newEmail" class="input"><span class="ml-3 text-black" v-if="newEmail !== null"><span v-if="newEmail.trim().length > 0" :style="newEmail.trim().length <= 50 ? { color: 'black' } : { color: 'red' }">{{newEmail.trim().length}}/50</span></span></li>
        <li class="m-2">▸ 
          <select class="input" v-model="newRole">
            <option v-for="role in roleList" :key="role">{{role}}</option>
          </select>
        </li>
      </ul>
      <ul class="my-4">
        <li class="m-2 font-bold text-l">Time Information:</li>
        <li class="m-2">Created ▸ {{ new Date(user.createdOn) }}</li>
        <li class="m-2">Updated ▸ {{ new Date(user.updatedOn) }}</li>
      </ul>
      <div class="float-right">
        <button
          @click="if(putConfirmation()) $emit('callPutUserProceed', user);"
          class="font-semibold mt-1 mr-2 bg-black text-white rounded text-l hover:bg-gray-600 px-2 transition-color duration-200 delay-200">
          Confirm
        </button>
        <button 
          @click="$emit('callPutUserCancel');" 
          class="font-semibold mt-1 bg-black text-white rounded text-l hover:bg-gray-600 px-2 transition-color duration-200 delay-200">
          Cancel
        </button>
      </div>
    </div>
    <div v-else>No Selected User.</div>
  </div>
</template>

<style scoped>
.input {
  width: 240px;
  border-radius: 6px;
}
</style>