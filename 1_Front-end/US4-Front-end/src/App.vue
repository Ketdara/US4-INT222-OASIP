<script setup>
import { ref, onBeforeMount, onBeforeUpdate, onMounted } from 'vue';
// import { useRoute } from 'vue-router';
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
import router from "./router/index.js"

import { PublicClientApplication } from '@azure/msal-browser';

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
  updateCurrentUser();
})
const updateCurrentUser = () => {
  currentName.value = localStorage.getItem('name');
  currentEmail.value = localStorage.getItem('email');
  currentRole.value = localStorage.getItem('role');
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
    // updateUsers();
    toggleLogin();
    // router.go({ name: 'OASIP'});
    if(router.currentRoute.value.name === "OASIP") router.go({ name: 'OASIP'});
    else router.push({ name: 'OASIP'});
  }
}

onBeforeUpdate(async () => {
  updateCurrentUser();
})

// Logout
const userList = ref([]);

// function deleteAllCookies() {
//     var cookies = document.cookie.split(";");

//     for (var i = 0; i < cookies.length; i++) {
//         var cookie = cookies[i];
//         var eqPos = cookie.indexOf("=");
//         var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
//         document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
//     }
// }

const logout = async () => {
  if(confirm("Are you sure you want to confirm logout?") === true){

    // let logoutRequest = {
    //   account: msalInstance.value.getAccountByHomeId(localStorage.getItem('homeAccountId')),
    //   mainWindowRedirectUri: "http://localhost:3000/us4/",
    // };
    // // console.log(msalInstance.value.getAccountByHomeId(localStorage.getItem('homeAccountId')));
    // await msalInstance.value.logoutPopup(logoutRequest);
    // console.log(router.currentRoute.value.name);
    // console.log(this.$route.name);
    
    if(router.currentRoute.value.name === "OASIP") router.go({ name: 'OASIP'});
    else router.push({ name: 'OASIP'});

    localStorage.clear();
    updateCurrentUser();
    // deleteAllCookies();

    alert("Logout Successful");

  }
}

const msalConfig = {
  auth: {
    clientId: '8a7111b7-e23e-436d-a4ad-2a1e0ac20367',
    authority:
      'https://login.microsoftonline.com/6f4432dc-20d2-441d-b1db-ac3380ba633d',
  },
  // cache: {
  //   cacheLocation: 'sessionStorage',
  // },
  // scopes: ["user.read"],
}

const msalInstance = ref(new PublicClientApplication(msalConfig));
// const oasipAccount = ref(null)

const msSignIn = async () => {
  await msalInstance.value
    .loginPopup({prompt: 'login'})
    .then(() => {
      let accounts = msalInstance.value.getAllAccounts();
      // oasipAccount.value = accounts[0];
      // console.log(accounts[0]);

      localStorage.setItem('homeAccountId', accounts[0].homeAccountId);

      msLogIn(accounts[0]);
    })
    .catch(error => {
      console.error(`error during authentication: ${error}`);
    });
}

const msLogIn = async (account) => {
  // oasipAccount.value = account;
  // console.log(oasipAccount.value.idTokenClaims);
  await getIDToken(account);
  await getAccessToken();
}

const getIDToken = async (account) => {
  let request = {
    account: account,
    // prompt: 'select_account',
    // prompt: 'login',
  };
  try {
    let tokenResponse = await msalInstance.value.acquireTokenSilent(request);
    // console.log(`ID token: ${tokenResponse.idToken}`)
    localStorage.setItem('idToken', tokenResponse.idToken);
  } catch (error) {
      console.log(`error during ID token acquisition: ${error}`);
  }
}

const getAccessToken = async () => {
  if(await userAPI.loginMSUser()) {
    alert("Login Successful");
    updateCurrentUser();

    toggleLogin();
    // router.go({ name: 'OASIP'});
    if(router.currentRoute.value.name === "OASIP") router.go({ name: 'OASIP'});
    else router.push({ name: 'OASIP'});
  }
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
        @callLoginMS="msSignIn"
      />
    </div>
</header>
</template>
