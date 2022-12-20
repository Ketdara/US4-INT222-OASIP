<script setup>
import { ref, onBeforeMount, onBeforeUpdate } from 'vue';
import { userAPI } from "../script/userAPI.js";
import { eventCategoryAPI } from "../script/eventCategoryAPI.js";
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
const eventCategories = ref([]);

const maxPageNum = ref(1)
const selectedPageNum = ref(1)

const currentName = ref(null);
const currentEmail = ref(null);
const currentRole = ref(null);
const currentToken = ref(null);
const currentRefreshToken = ref(null);
const isSessionExpired = ref(null)

onBeforeUpdate(async () => {
  updateCurrentUser();
})

const updateCurrentUser = async () => {
  currentName.value = localStorage.getItem('name');
  currentEmail.value = localStorage.getItem('email');
  currentRole.value = localStorage.getItem('role');
  currentToken.value = localStorage.getItem('jwtToken');
  currentRefreshToken.value = localStorage.getItem('refreshToken');
  isSessionExpired.value = localStorage.getItem('isJwtExpired')
}

const updateUsers = async () => {
  if(isSessionExpired.value === 'true') return
  
  if(maxPageNum.value < selectedPageNum.value) {
    selectedPageNum.value = maxPageNum.value
  }
  if(selectedPageNum.value < 1) {
    selectedPageNum.value = 1
  }
  await getUsersAsPage(selectedPageNum.value);
  await getEventCategories()
  
}

onBeforeMount(async () => {
  selectedPageNum.value = 1
  await updateCurrentUser();
  updateUsers();
})

const getUsersAsPage = async (pageNum) => {
  if(isSessionExpired.value == 'true') return
  
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
  if(eventCategoryOwners.value.includes(id)) {
    if(confirm("This user is a category owner, are you sure you want to remove this user?") === true) {
      if(await userAPI.deleteUserById(id)) {
        updateUsers();
        user.value = {};
      }
    }
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
    updateCurrentUser();
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
    updateCurrentUser();
    alert("Logout Successful");
    
    userList.value = null;
    maxPageNum.value = 1;
  }
}
const eventCategoryOwners = ref([])

const getEventCategories = async () => {
  eventCategories.value = await eventCategoryAPI.getEventCategories();
  eventCategories.value.forEach(category => {
    category.eventCategoryOwners.forEach(owner => {
      eventCategoryOwners.value.push(owner.id);
      eventCategoryOwners.value = eventCategoryOwners.value.filter((element, index) => {
        return eventCategoryOwners.value.indexOf(element) === index
      })
    })
  });
}

</script>

<template>
  <div>
    <div>
      <match-user
      v-if="isMatchOpen"
      @toggleModal="toggleMatch"
      @callMatchUser="match"/>
    </div>
    <div class="ml-10 -mb-2">
      <button v-if="currentToken !== null && currentRefreshToken !== null" class="mr-5" @click="refresh">Refresh</button>
      <button v-if="currentRole !== null && currentRole.toString().match('admin')" class="mr-5" @click="toggleMatch">Match</button>  
    </div> 
    <!-- End-buttons -->
    
    <div class="grid justify-items-start ">
      <div class="px-8">
        <div class="mt-8 grid grid-cols-2 gap-x-10 gap-y-8 rounded-lg">
          <!-- User List -->
          <div class="bg-neutral-100 rounded-lg col-span-1 row-span-2 p-3"> 
            <h2 class="font-semibold">User List : </h2>
            <div v-if="currentRole !== null && currentEmail !== null">
              <div v-if="currentRole.toString().match('admin')">
                <div v-if="userList !== null && userList !== undefined">
                  
                  <div v-if="userList.length > 0">
                    <button v-for="page in maxPageNum" :key="page" @click="getUsersAsPage(page)" class="bg-black text-white rounded text-l active:bg-gray-600 py px-2 mx-1 mt-1 transition-color duration-000 delay-000">{{ page }}</button>
                    <view-user-list 
                    :userList='userList'
                    @callShowUser="getUserById"
                    />
                  </div>
                  
                </div>
                <div class="m-5 text-l grid grid-cols-5 gap-x-10 gap-y-8" v-else>No user.</div> 
              </div>
              <div class="m-5 text-l grid grid-cols-5 gap-x-10 gap-y-8 " v-else>User prohibited.</div>
            </div>
            <div class="m-5 text-l" v-else-if="isSessionExpired === 'true'">
              <span style="color:crimson">Session expired</span>, please refresh your token in the user page.
            </div>
            <div class="m-5 text-l grid grid-cols-5 gap-x-10 gap-y-8" v-else>Please login.</div>
            
          </div>
          <!-- Create User -->
          <div class="bg-neutral-700 rounded-lg p-3">
            <h2 class="font-semibold text-white">Create User : </h2>
            <div v-if="currentRole !== null && currentEmail !== null" >
              <div v-if="currentRole.toString().match('admin')">
                <create-user
                :roleList='roleList'
                :user='postUI'
                :currentRole='currentRole'
                :eventCategoryList='eventCategories'
                @callCreateUser="postUser"
                />
              </div>
              <div class="text-white ml-5" v-else >User prohibited.</div>
            </div>
            <div class="m-5 text-white" v-else-if="isSessionExpired === 'true'">
              Session expired.
            </div>
            <div class="m-5 text-l text-white" v-else>Please login.</div> 
          </div>
          
          <!-- User details -->
          <div class="bg-neutral-200 rounded-lg p-3">
            <h2 class="font-semibold">Show Details : </h2>
            <div v-if="currentRole !== null && currentEmail !== null" >
              <div v-if="currentRole.toString().match('admin')">
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
              <div v-else class="ml-5">User prohibited.</div>
            </div>
            <div class="m-5 text-l" v-else-if="isSessionExpired === 'true'">
              Session expired.
            </div>
            <div class="m-5 text-l" v-else>Please login.</div> 
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>