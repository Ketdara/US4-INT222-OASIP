<script setup>
defineEmits(['callEditUser', 'callRemoveUser'])
defineProps({
  user: {
    type: Object,
    default: null
  }
})

const deleteConfirmation = () => {
  var isConfirm = confirm("Are you sure you want to remove this user?");
  return isConfirm;
}

</script>

<template>
<div class="ml-5 mt-3">
  <div v-if="Object.keys(user).length > 0">
    <ul class="my-4">
      <li class="m-2 font-bold text-l">User Information: </li>
      <li class="m-2">▸ {{ user.name }}</li>
      <li class="m-2">▸ {{ user.email }}</li>
      <li class="m-2">▸ {{ user.role }}</li>
    </ul>
    <ul class="my-4">
      <li class="m-2 font-bold text-l">Time Information:</li>
      <li class="m-2">Created ▸ {{ new Date(user.createdOn) }}</li>
      <li class="m-2">Updated ▸ {{ new Date(user.updatedOn) }}</li>
    </ul>
    <div class="float-right">
      <button
        @click="$emit('callEditUser');"
        class="font-semibold mt-1 mr-2 bg-black text-white rounded text-l hover:bg-gray-600 px-2 transition-color duration-200 delay-200">
        Edit
      </button>
      <button 
        @click="if(deleteConfirmation()) $emit('callRemoveUser', user.id);" 
        class="font-semibold mt-1 bg-black text-white rounded text-l hover:bg-gray-600 px-2 transition-color duration-200 delay-200">
        Remove
      </button>
    </div>
  </div>
  <div v-else>No Selected User.</div>
</div>
</template>

<style scoped>

</style>
