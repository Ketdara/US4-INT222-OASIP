<script setup>
import { ref, onBeforeMount } from 'vue';
import { userAPI } from "../script/userAPI.js";
import ViewUserList from '../components/ViewUserList.vue';
import ViewUserDetails from '../components/ViewUserDetails.vue';
import CreateUser from '../components/CreateUser.vue';
import EditUser from '../components/EditUser.vue';
import LoginUser from '../components/LoginUser.vue';
import MatchUser from '../components/MatchUser.vue';

const user = ref({});
const userList = ref([]);
const userPage = ref(null);

const roleList = ref(['admin', 'lecturer', 'student'])

const maxPageNum = ref(1)
const selectedPageNum = ref(1)

const updateUsers = async () => {
  if(maxPageNum.value < selectedPageNum.value) {
    selectedPageNum.value = maxPageNum.value
  }
  if(selectedPageNum.value < 1) {
    selectedPageNum.value = 1
  }
  await getUsersAsPage(selectedPageNum.value);
}

onBeforeMount(async () => {
  selectedPageNum.value = 1
  updateUsers();
})

const getUsersAsPage = async (pageNum) => {
  try {
    userPage.value = await userAPI.getUsersAsPage(pageNum-1);
  }catch(err) {
    alert(err);
  }
  if(userPage.value !== null) {
    userList.value = userPage.value.content;
    maxPageNum.value = userPage.value.totalPages;
  }
}

const getUserById = async (id) => {
  user.value = await userAPI.getUserById(id);
  isEditing.value = false;
}

const postUser = async (user) => {
  if(await userAPI.postUser(user)) {
    updateUsers();
    resetPostUI();
  }
}

const putUser = async (user) => {
  if(await userAPI.putUser(user)) {
    updateUsers();
    getUserById(user.id);
    isEditing.value = false;
  }
}

const deleteUser = async (id) => {
  if(await userAPI.deleteUserById(id)) {
    updateUsers();
    user.value = {};
  }
}

var postUI = ref({
  name: null,
  email: null,
  role: null,
  password: null,
  confirmPassword: null
})

const resetPostUI = () => {
  for(let key in postUI.value) {
    postUI.value[key] = null;
  }
}

const isEditing = ref(false);

const toggleEdit = () => {
  isEditing.value = !isEditing.value;
}

const isLoginOpen = ref(false);
const isMatchOpen = ref(false);

const toggleLogin = () => {
  isLoginOpen.value = !isLoginOpen.value;
}

const toggleMatch = () => {
  isMatchOpen.value = !isMatchOpen.value;
}

const match = async (credentials) => {
  if(await userAPI.matchUser(credentials)) {
    alert("Match Successful");
  }
}

const login = async (credentials) => {
  if(await userAPI.loginUser(credentials)) {
    alert("Login Successful");
    updateUsers();
    toggleLogin();
  }
}

const refresh = async () => {
  if(await userAPI.refreshToken()) {
    alert("Refresh Successful");
    updateUsers();
  }
}

const logout = () => {
  if(confirm("Are you sure you want to confirm logout?") === true){
    localStorage.clear();
    alert("Logout Successful");

    userList.value = null;
    maxPageNum.value = 1;
  }
}

</script>

<template>
<div class="pb-5">
  <div>
    <login-user
      v-if="isLoginOpen"
      @toggleModal="toggleLogin"
      @callLoginUser="login"/>
  </div>
  <div>
    <match-user
      v-if="isMatchOpen"
      @toggleModal="toggleMatch"
      @callMatchUser="match"/>
  </div>
  <div class="bg-black p-4 px-7 text-white">
    <h1 class="font-semibold text-2xl">OASIP</h1>
    <p class="text-l inline">Online Appointment Scheduling System for Integrated Project Clinics</p>
    <button class="mr-10 font-semibold float-right" @click="refresh">Refresh</button>
    <button class="mr-10 font-semibold float-right" @click="toggleLogin">Login</button>
    <button class="mr-10 font-semibold float-right" @click="logout">Logout</button>
    <button class="mr-10 font-semibold float-right" @click="toggleMatch">Match</button>
      <!-- Login -->

  <!-- End-Login -->
  </div>
  <div class="px-8">
    <div class="mt-8 grid grid-cols-2 gap-x-10 gap-y-8 rounded-lg">
      <!-- User List -->
      <div class="bg-neutral-100 rounded-lg col-span-1 row-span-2 p-3"> 
        <h2 class="font-semibold">User List : </h2>
        <div v-if="userList !== null && userList !== undefined">
          <div v-if="userList.length > 0">
            <button v-for="page in maxPageNum" :key="page" @click="getUsersAsPage(page)" class="bg-black text-white rounded text-l active:bg-gray-600 py px-2 mx-1 mt-1 transition-color duration-000 delay-000">{{ page }}</button>
            <view-user-list 
              :userList='userList'
              @callShowUser="getUserById"
            />
          </div>
        </div>
        <div class="m-5 text-l" v-else>
          <div>No user.</div>
        </div>
      </div>

      <!-- Create User -->
      <div class="bg-neutral-700 rounded-lg p-3">
        <h2 class="font-semibold text-white">Create User : </h2>
        <create-user
          :roleList='roleList'
          :user='postUI'
          @callCreateUser="postUser"
        />
      </div>

      <!-- User details -->
      <div class="bg-neutral-200 rounded-lg p-3">
        <h2 class="font-semibold">Show Details : </h2>
        <view-user-details
          v-if="!isEditing"
          :user='user'
          @callEditUser="toggleEdit"
          @callRemoveUser="deleteUser"
        />
        <edit-user
          v-if="isEditing"
          :roleList='roleList'
          :user='user'
          @callPutUserProceed="putUser"
          @callPutUserCancel="toggleEdit"
        />
      </div>
    </div>
  </div>
</div>
</template>

<style scoped></style>