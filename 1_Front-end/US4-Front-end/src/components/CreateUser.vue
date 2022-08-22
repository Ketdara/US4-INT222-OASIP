<script setup>
import { ref, computed } from 'vue';

defineEmits(['callCreateUser'])
const props = defineProps({
  roleList: {
    type: Array,
    default: []
  },
  user: {
    type: Object,
    default: {
      name: null,
      email: null,
      role: null
    }
  }
})

// const roleList = ref(['admin', 'lecturer', 'student'])

const selectedRole = ref(null)
// const newUserRole = computed(() => {
//   return roleList.value.find(role => role === selectedRole.value)
// });

const newUser = computed(() => {
  return {
    name: props.user.name,
    email: props.user.email,
    // role: newUserRole.value ? newUserRole.value : 'student',
    role: selectedRole.value ? selectedRole.value : 'student',
  }
});

const checkInputInvalid = () => {
  if(newUser.value.name === null || newUser.value.name.trim().length === 0 || newUser.value.name.trim().length > 100) return true;
  if(newUser.value.email === null || newUser.value.email.trim().length === 0 || newUser.value.email.trim().length > 50) return true;
  // if(newUser.value.role === null) return true;
  return false;
}
</script>

<template>
<div class="ml-5">
  <div class="flex">
    <div class="columnA text-white">
      <ul>
        <li class="m-1">Name:</li>
        <li class="m-1">Email:</li>
        <li class="m-1">Role:</li>
      </ul>
    </div>
    <div class="columnB">
      <ul>
        <li class="m-1"><input class="input" type="text" v-model="user.name"></li>
        <li class="m-1"><input class="input" type="text" v-model="user.email"></li>
        <li class="m-1">
          <select class="input" v-model="selectedRole">
            <option v-for="role in roleList" :key="role">{{role}}</option>
          </select>
        </li>
      </ul>
    </div><div class="columnC text-white">
      <ul>
        <li class="m-1"><span v-if="user.name !== null"><span v-if="user.name.trim().length > 0" :style="user.name.trim().length <= 100 ? { color: 'white' } : { color: 'red' }">{{user.name.trim().length}}/100</span></span><br v-else></li>
        <li class="m-1"><span v-if="user.email !== null"><span v-if="user.email.trim().length > 0" :style="user.email.trim().length <= 50 ? { color: 'white' } : { color: 'red' }">{{user.email.trim().length}}/50</span></span><br v-else></li>
        <li class="m-1"><br></li>
      </ul>
    </div>
  </div>
  <div class="m-1">
    <button 
    :disabled="checkInputInvalid()"
    :style="!checkInputInvalid() ? { color: 'white' } : { color: 'transparent' }"
    @click="$emit('callCreateUser', newUser)"
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
