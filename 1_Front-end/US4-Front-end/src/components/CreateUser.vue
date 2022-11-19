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
      role: null,
      password: null,
      confirmPassword: null
    }
  }
})

const newUser = computed(() => {
  return {
    name: props.user.name,
    email: props.user.email,
    role: newRole.value ? newRole.value : 'student',
    password: props.user.password
  }
});

const newRole = ref(null)

// const newPassword = ref(null)
// const confirmPassword = ref(null)

const checkConfirmPassword = () => {
  if(props.user.password === props.user.confirmPassword){
    return true;
  }else{
    alert("Confirm password do not match");
    return false;
  }
}

const checkInputInvalid = () => {
  if(newUser.value.name === null || newUser.value.name.trim().length === 0 || newUser.value.name.trim().length > 100) return true;
  if(newUser.value.email === null || newUser.value.email.trim().length === 0 || newUser.value.email.trim().length > 50) return true;
  if(props.user.password === null || props.user.password.trim().length === 0 || props.user.password.trim().length < 8 || props.user.password.trim().length > 14 || props.user.password.match(/\s/g)) return true;
  if(props.user.confirmPassword === null || props.user.confirmPassword.trim().length === 0 || props.user.confirmPassword.trim().length < 8 || props.user.confirmPassword.trim().length > 14) return true;

  return false;
}

const isShowPassword = ref(false)
const toggleShowPassword = () => {
  isShowPassword.value = !isShowPassword.value
}

</script>

<template>
<div class="ml-5">
  <div class="flex">
    <div class="columnA text-white text-right">
      <ul>
        <li class="m-1">Name:</li>
        <li class="m-1">Email:</li>
        <li class="m-1">Password:</li>
        <li class="m-1">Confirm Password:</li>
        <li class="m-1">Role:</li>
      </ul>
    </div>
    <div class="columnB">
      <ul>
        <li class="m-1"><input class="input" type="text" v-model="user.name"></li>
        <li class="m-1"><input class="input" type="text" v-model="user.email"></li>
        <li class="m-1"><input class="input" :type="isShowPassword ? 'text' : 'password'" v-model="user.password"></li>
        <li class="m-1"><input class="input" :type="isShowPassword ? 'text' : 'password'" v-model="user.confirmPassword"></li>

        <li class="m-1">
          <select class="input" v-model="newRole">
            <option v-for="role in roleList" :key="role">{{role}}</option>
          </select>
        </li>
      </ul>
    </div>
    <div class="columnC text-white">
      <ul>
        <li class="m-1"><br></li>
        <li class="m-1"><br></li>
        <li v-if="isShowPassword" class="m-2"><i id="show"><img src="../assets/eye.png"  width="20" @click="toggleShowPassword()"></i></li>
        <li v-else class="m-2"><i id="show"><img src="../assets/hidden.png"  width="20" @click="toggleShowPassword()"></i></li>
        <!-- NOTE: top ones for dev, bottom ones for live -->
        <!-- <li class="m-2"><i id="show"><img :src="isShowPassword ? '/us4/src/assets/eye.png' : '/us4/src/assets/hidden.png'"  width="20" @click="toggleShowPassword()"></i></li> -->
      </ul>
    </div>
    <div class="columnD text-white">
      <ul>
        <li class="m-1"><span v-if="user.name !== null"><span v-if="user.name.trim().length > 0" :style="user.name.trim().length <= 100 ? { color: 'white' } : { color: 'red' }">{{user.name.trim().length}}/100</span><br v-else></span><br v-else></li>
        <li class="m-1"><span v-if="user.email !== null"><span v-if="user.email.trim().length > 0" :style="user.email.trim().length <= 50 ? { color: 'white' } : { color: 'red' }">{{user.email.trim().length}}/50</span><br v-else></span><br v-else></li>
        <li class="m-1"><span v-if="user.password !== null"><span v-if="user.password.match(/\s/g)" style="color: red;">Whitespace <br>not allowed</span><span v-else-if="user.password.length > 0" :style="user.password.length <= 14 && user.password.length >= 8 ? { color: 'white' } : { color: 'red' }">{{user.password.length}} Characters<br>(must be 8-14)</span><br v-else></span><br v-else></li>
        <li class="m-1"><br></li>
      </ul>
    </div>
  </div>
  <div class="m-1">
    <button 
    :disabled="checkInputInvalid()"
    :style="!checkInputInvalid() ? { color: 'white' } : { color: 'transparent' }"
    @click="if(checkConfirmPassword()) $emit('callCreateUser', newUser);"
    class="font-semibold my-1 mt-2 bg-black text-white rounded text-l disabled:bg-transparent hover:bg-gray-600 px-4 transition-color duration-200 delay-200">
      Post
    </button>
  </div>
</div>
</template>

<style scoped>
.columnA {
  float: left;
  width: 170px;
}
.columnB {
  float: left;
  width: 280px;
}
.columnC {
  float: left;
  width: 50px;
}
.columnD {
  float: left;
  width: 120px;
}
/* .columnE {
  float: left;
  width: 120px;
} */
.input {
  width: 260px;
  border-radius: 6px;
}
#hide{
  /* display: none; */
  cursor: pointer;
}
#show{
  cursor: pointer;
}
</style>
