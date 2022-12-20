<script setup>
import { ref, computed } from 'vue';

defineEmits(['setFilter'])
const props = defineProps({
  eventCategoryList: {
    type: Array,
    default: []
  }
})

const filterBy = ref("all");
const filterCategory = ref(null);
const filterDate = ref(null);

const filter = computed(() => {
  return {
    by: filterBy.value,
    category: filterCategory.value && props.eventCategoryList.length > 0 ? props.eventCategoryList.find(category => category.eventCategoryName === filterCategory.value) : null,
    date: filterDate.value
  }
});

</script>

<template>
  <div class="all mt-2">
    <div>
      <div class="m-1">
        <label>Filter By: </label>
        <select v-model="filterBy">
          <option value="all">all</option>
          <option value="category">Event Category</option>
          <option value="upcoming">Upcoming</option>
          <option value="past">Past</option>
          <option value="date">Date</option>
        </select>
      </div>
      <div class="m-1" v-if="filterBy === 'category'">
        <label>Choose Event Category: </label>
        <select v-model="filterCategory">
          <option v-for="eventCategory in eventCategoryList" :key="eventCategory">{{eventCategory.eventCategoryName}}</option>
        </select>
      </div>
      <div class="m-1" v-if="filterBy === 'date'">
        <label>Choose Event Date: </label>
        <input type="date" v-model="filterDate">
      </div>
    </div>
    <button @click="$emit('setFilter', filter)" class="m-1 mb-2 bg-black text-white rounded text-l hover:bg-gray-600 px-2 transition-color duration-200 delay-200">Apply</button>
  </div>
</template>

<style scoped>
.all {
  padding-left: 12px;
}
</style>
