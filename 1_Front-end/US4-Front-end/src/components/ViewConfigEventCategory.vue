<script setup>
import { ref, computed } from 'vue';

defineEmits(['callPutEventCategory','toggleModal'])
const props = defineProps({
  eventCategoryList: {
    type: Array,
    default: []
  },
  selectedCategory: {
    type: Object,
    default: null
  }
})

const editedCategory = ref({ 
  id: null,
  eventCategoryName: null,
  eventCategoryDescription: null,
  eventDuration: null
})

const mapEditedCategory = (eventCategory) => {
  editedCategory.value.id = eventCategory.id
  editedCategory.value.eventCategoryName = eventCategory.eventCategoryName
  editedCategory.value.eventCategoryDescription = eventCategory.eventCategoryDescription
  editedCategory.value.eventDuration = eventCategory.eventDuration
}
</script>
 
<template>
<div>
  <div class="bgModal eventCategoryList w-full text-l ">
  <div class="model-inside rounded-lg">
    <div class="grid grid-cols-3 gap-x-10 gap-y-8">
      <div class="rounded-lg col-span-2">
        <table class="content-table ">
          <thead>
            <tr>
              <th>Order</th>
              <th>Category Name</th>
              <th>Category Description</th>
              <th>Duration (min)</th>
              <th></th>
            </tr>
          </thead>
          <tbody v-for="(eventCategory, index) in eventCategoryList" :key="index">
            <tr>
              <td class="font-semibold">{{index + 1}}</td>
              <td>{{eventCategory.eventCategoryName}}</td>
              <td>{{eventCategory.eventCategoryDescription === null || eventCategory.eventCategoryDescription === 0 ? "No details" : eventCategory.eventCategoryDescription}}</td>
              <td>{{eventCategory.eventDuration}}</td>
              <td>
                <button @click="mapEditedCategory(eventCategory)" class="px-1 bg-black text-white rounded text-l hover:bg-gray-600 transition-color duration-200 delay-200">Edit</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="bg-neutral-700 rounded-lg p-4 pl-5">
        <h2 class="font-semibold text-white">Event Category Editor : </h2>
        <div class="text-white p-3 pl-4">
          <div v-if="editedCategory.id !== null">
            <div class="flex">
              <div class="text-white">
                <ul>
                  <li class="m-1">Category Name: <span v-if="editedCategory.eventCategoryName !== null"><span v-if="editedCategory.eventCategoryName.trim().length > 0" :style="editedCategory.eventCategoryName.trim().length <= 100 ? { color: 'white', float: 'right' } : { color: 'red', float: 'right' }">{{editedCategory.eventCategoryName.trim().length}}/100</span></span></li>
                  <li class="m-1 text-black"><input class="input" type="text" v-model="editedCategory.eventCategoryName"></li>
                  <li class="m-1">Category Description: <span v-if="editedCategory.eventCategoryDescription !== null"><span v-if="editedCategory.eventCategoryDescription.trim().length > 0" :style="editedCategory.eventCategoryDescription.trim().length <= 500 ? { color: 'white', float: 'right' } : { color: 'red', float: 'right' }">{{editedCategory.eventCategoryDescription.trim().length}}/500</span></span></li>
                  <li class="m-1 text-black"><input class="input" type="text" v-model="editedCategory.eventCategoryDescription"></li>
                  <li class="m-1">Duration (min): <span style="float: right">max: 480</span></li>
                  <li class="m-1 text-black"><input class="input" type="number" min="1" max="480" v-model="editedCategory.eventDuration"></li>
                </ul>
              </div>
            </div>
            <div>
              <div class="float-right pt-4">
                <button
                  @click="$emit('callPutEventCategory', editedCategory)"
                  class="font-semibold mt-1 mr-2 bg-black text-white rounded text-l hover:bg-gray-600 px-2 transition-color duration-200 delay-200">
                  Confirm
                </button>
                <button
                  @click="editedCategory.id = null"
                  class="font-semibold mt-1 bg-black text-white rounded text-l hover:bg-gray-600 px-2 transition-color duration-200 delay-200">
                  Cancel
                </button>
              </div>
            </div>
          </div>
          <div v-else class="m-1">No Selected Event Category.</div>
        </div>
      </div>
    </div>
    <button @click="$emit('toggleModal')" class="font-semibold px-1 mt-3 float-right bg-black text-white rounded text-l hover:bg-gray-600 transition-color duration-200 delay-200" >Close</button>
    </div>
  </div>
</div>
</template>
 
<style scoped>
.bgModal{
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.8);
    position: absolute;
    top: 0%;
    position: fixed;
    display: flex;
    align-items: center;
    justify-content: center;
}
.model-inside{
  background-color: #FFF ;
  padding: 50px;
  opacity: 1;
}
.eventCategoryList {
  padding-top: 11px;
  overflow-y: auto;
}
.content-table{
  border-collapse: collapse;
  font-size: small;
  
  /* min-height: 20em; */
}
.content-table thead tr{
  background-color: black;
  text-align: left;
  color: white;
}
.content-table th,
.content-table td{
  padding: 11px 15px;
  /* border-bottom: 1px solid #ddd; */
}
.column {
  float: left;
  width: 160px;
}

.input {
  width: 260px;
  border-radius: 6px;
}
</style>