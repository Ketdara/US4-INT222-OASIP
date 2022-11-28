<script setup>
import { ref, onBeforeMount, onBeforeUpdate, onMounted } from 'vue';
import { eventAPI } from "../src/script/eventAPI.js";
import { eventCategoryAPI } from "../src/script/eventCategoryAPI.js";
import ViewEventList from '../src/components/ViewEventList.vue';
import ViewEventDetails from '../src/components/ViewEventDetails.vue';
import ScheduleEvent from '../src/components/ScheduleEvent.vue';
import RescheduleEvent from '../src/components/RescheduleEvent.vue';
import FilterEvent from '../src/components/FilterEvent.vue';
import ViewConfigEventCategory from '../src/components/ViewConfigEventCategory.vue';
import LoginUser from '../src/components/LoginUser.vue';
import { userAPI } from "../src/script/userAPI.js";

// import customTokenCredential from './CustomTokenProvider';
// import { BlobServiceClient } from '@azure/storage-blob';
import { PublicClientApplication } from '@azure/msal-browser';
// import { mapMutations } from 'vuex';

// Show username
const currentName = ref(null);
const currentEmail = ref(null);
const currentRole = ref(null);
const eventCategories = ref([]);
const eventList = ref([]);
onBeforeUpdate(async () => {
  updateCurrentUser();
})

onBeforeMount(async () => {
  selectedPageNum.value = 1
  updateCurrentUser();
  updateEvents();
  await getEventCategories();
})
const updateCurrentUser = () => {
  currentName.value = localStorage.getItem('name');
  currentEmail.value = localStorage.getItem('email');
  currentRole.value = localStorage.getItem('role');
}

const getEventCategories = async () => {
  eventCategories.value = await eventCategoryAPI.getEventCategories();
}
// Login
const isLoginOpen = ref(false);

const toggleLogin = () => {
  isLoginOpen.value = !isLoginOpen.value;
}

const login = async (credentials) => {
  if(await userAPI.loginUser(credentials)) {
    alert("Login Successful");
    updateCurrentUser();
    updateUsers();
    toggleLogin();
  }
}

const postUser = async (user) => {
  if(await userAPI.postUser(user)) {
    updateUsers();
    resetPostUI();
  }
}

onBeforeUpdate(async () => {
  updateCurrentUser();
})

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

var currentFilter = ref({
  by: "all",
  category: null,
  date: null
});

// Logout
const userList = ref([]);
const logout = () => {
  if(confirm("Are you sure you want to confirm logout?") === true){
    localStorage.clear();
    updateCurrentUser();
    alert("Logout Successful");

    userList.value = null;
    maxPageNum.value = 1;
  }
}

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
const userPage = ref(null);

const getEventsAsPage = async (pageNum) => {
  try {
    eventPage.value = await eventAPI.getEventsAsPage(pageNum-1, currentFilter.value);
  }catch(err) {
    alert(err);
  }
  if(eventPage.value !== null) {
    eventList.value = eventPage.value.content;
    maxPageNum.value = eventPage.value.totalPages;
  }
}
const eventPage = ref(null);

const updateEvents = async () => {
  if(maxPageNum.value < selectedPageNum.value) {
    selectedPageNum.value = maxPageNum.value
  }
  if(selectedPageNum.value < 1) {
    selectedPageNum.value = 1
  }
  await getEventsAsPage(selectedPageNum.value);
}

const msalConfig = {
  auth: {
    clientId: '8a7111b7-e23e-436d-a4ad-2a1e0ac20367',
    authority:
      'https://login.microsoftonline.com/6f4432dc-20d2-441d-b1db-ac3380ba633d',
  },
  cache: {
    cacheLocation: 'sessionStorage',
  },
  // scopes: ["user.read"],
}

const msalInstance = ref(new PublicClientApplication(msalConfig));
const oasipAccount = ref(null)

const msSignIn = async () => {
  await msalInstance.value
    .loginPopup({})
    .then(() => {
      let accounts = msalInstance.value.getAllAccounts();
      oasipAccount.value = accounts[0];
      msLogIn(oasipAccount.value);
    })
    .catch(error => {
      console.error(`error during authentication: ${error}`);
    });
}

const msLogIn = async (account) => {
  oasipAccount.value = account;
  // console.log(oasipAccount.value.idTokenClaims);
  await getIDToken(account);
  await getAccessToken();
}

const getIDToken = async (account) => {
  let request = {
    account: account
  };
  try {
    let tokenResponse = await msalInstance.value.acquireTokenSilent(request);
    console.log(`ID token: ${tokenResponse.idToken}`)
    localStorage.setItem('idToken', tokenResponse.idToken);
  } catch (error) {
      console.log(`error during ID token acquisition: ${error}`);
  }
}

const getAccessToken = async () => {
  await userAPI.loginMSUser();
}
</script>

<template>
<header class="text-gray-600 body-font">
  <div class="container mx-auto flex flex-wrap p-5 flex-col md:flex-row items-center mt-5">
    <p class="flex title-font font-medium items-center text-gray-900 mb-4 md:mb-0">
      <span class="ml-3 text-2xl font-bold">OASIP</span>
    </p>
    <nav class="md:ml-auto md:mr-auto flex flex-wrap items-center text-xl font-medium justify-center ">
      <router-link :to="{ name: 'OASIP'}" class="mr-7 hover:text-gray-900 hover:bg-gray-100 rounded">Events</router-link>
      <router-link :to="{ name: 'EventCategory'}" class="mr-7 hover:text-gray-900 hover:bg-gray-100 rounded">Event Category</router-link>
      <router-link :to="{ name: 'User'}" class="mr-7 hover:text-gray-900 hover:bg-gray-100 rounded">User</router-link>
      <button @click="msSignIn">TestMSLogin</button>
    </nav>
    <div>
      <button v-if="currentName === null" class="font-medium float-right bg-gray-100 border-0 py-1 px-3 focus:outline-none hover:bg-gray-200 rounded text-xl mt-4 md:mt-0" @click="toggleLogin">Login</button>
      <button v-else class="font-medium float-right bg-gray-100 border-0 py-1 px-3 focus:outline-none hover:bg-gray-200 rounded mt-4 md:mt-0 text-xl" @click="logout">Logout</button>
    </div>
      <!-- SHOW USERNAME -->
      <span class="ml-5 text-xl">
        <p v-if="currentName === null" class="text-l"></p>
        <p v-else class="text-indigo-500 font-semibold ">{{currentName.slice(0, 30)}}</p>
      </span>
      <!-- END SHOW USERNAME -->
  </div>
      <div class="container mx-auto flex flex-wrap p-5 flex-col md:flex-row items-center"> 
        <router-view> </router-view>
      </div>
    <div> 
      <login-user
        v-if="isLoginOpen"
        @toggleModal="toggleLogin"
        @callLoginUser="login"
      />
    </div>
</header>
</template>
