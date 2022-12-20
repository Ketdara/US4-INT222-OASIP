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
  eventDuration: null,
  eventCategoryOwners: null
})

const mapEditedCategory = (eventCategory) => {
  editedCategory.value.id = eventCategory.id
  editedCategory.value.eventCategoryName = eventCategory.eventCategoryName
  editedCategory.value.eventCategoryDescription = eventCategory.eventCategoryDescription
  editedCategory.value.eventDuration = eventCategory.eventDuration
  editedCategory.value.eventCategoryOwners = eventCategory.eventCategoryOwners
}
</script>
 
<template>
<div class="grid grid-cols-2">
  <div v-for="(eventCategory, index) in eventCategoryList" :key="index">
  <section class="text-gray-600 body-font">
    <div class="container px-5 py-10 mx-auto">
      <div class="flex flex-wrap -m-4">
        <div class="p-4 lg:w-2/3">
          <div class="h-full bg-gray-100 bg-opacity-75 px-8 pt-16 pb-24 rounded-lg overflow-hidden text-center relative">
            <h2 class="tracking-widest text-xs title-font font-medium text-gray-400 mb-1">CATEGORY</h2>
            <h1 class="title-font sm:text-2xl text-xl font-medium text-gray-900 mb-3">{{eventCategory.eventCategoryName}}l</h1>
            <p class="leading-relaxed mb-3">{{eventCategory.eventCategoryDescription === null || eventCategory.eventCategoryDescription === 0 ? "No details" : eventCategory.eventCategoryDescription}}</p>
            <ol>
              <h1 class="font-medium">Lecturer</h1>
              <div v-if="eventCategory.eventCategoryOwners.length > 0">
                <li v-for="owner in eventCategory.eventCategoryOwners" :key="owner">
                  <div>{{owner.name}}</div>
                  <div>({{owner.email}})</div>
                </li>
              </div>
              <div v-else>-</div>
              <br>
            </ol>
            <p class="font-medium">Duration (min) : {{eventCategory.eventDuration}}</p>
            <button @click="mapEditedCategory(eventCategory)" class="px-3 bg-black text-white rounded text-l hover:bg-gray-600 transition-color duration-200 delay-200 mt-4 float-right">Edit</button>
            </div>
          </div>
        </div>
      </div>  
  </section>
  </div>
  <!-- <div class="overflow-scroll"> -->
  <div v-if="editedCategory.id !== null">
    <section class="text-gray-600 body-font ">
      <div class="container px-5 py-10 mx-auto">
        <div class="flex flex-wrap -m-4">
          <div class="p-4 lg:w-2/3">
            <div class="h-full bg-gray-100 bg-opacity-75 px-8 pt-16 pb-24 rounded-lg overflow-hidden text-center relative">
              <h2 class="tracking-widest text-m title-font font-bold text-black-800 mb-1"> Event Category Editor :</h2>
              <ul class="text-left">
                    <li class="m-1">Category Name: <span v-if="editedCategory.eventCategoryName !== null"><span v-if="editedCategory.eventCategoryName.trim().length > 0" :style="editedCategory.eventCategoryName.trim().length <= 100 ? { color: 'white', float: 'right' } : { color: 'red', float: 'right' }">{{editedCategory.eventCategoryName.trim().length}}/100</span></span></li>
                    <li class="m-1 text-black"><input class="input" type="text" v-model="editedCategory.eventCategoryName"></li>
                    <li class="m-1">Category Description: <span v-if="editedCategory.eventCategoryDescription !== null"><span v-if="editedCategory.eventCategoryDescription.trim().length > 0" :style="editedCategory.eventCategoryDescription.trim().length <= 500 ? { color: 'white', float: 'right' } : { color: 'red', float: 'right' }">{{editedCategory.eventCategoryDescription.trim().length}}/500</span></span></li>
                    <li class="m-1 text-black"><input class="input" type="text" v-model="editedCategory.eventCategoryDescription"></li>
                    <li class="m-1">Duration (min): <span style="float: right">max: 480</span></li>
                    <li class="m-1 text-black"><input class="input" type="number" min="1" max="480" v-model="editedCategory.eventDuration"></li>
                    <!-- <li class="m-1">Config owner:</li>
                    <li class="m-1" v-for="owner in editedCategory.eventCategoryOwners" :key="owner">
                      <div>
                        <button type="button" class="close m-1" aria-label="Close">
                          <span aria-hidden="true">&times;</span>
                        </button>
                        {{owner.name}}
                      </div>
                    </li>
                    <select class="input text-black" v-model="selectedCategory">
                      <option>Larry</option>
                    </select> -->
                  </ul>

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
            </div>
          </div>
        </div>  
    </section>
    </div>
  <!-- </div> -->
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