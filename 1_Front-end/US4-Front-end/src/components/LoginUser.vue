<script setup>
  import { ref } from 'vue';
defineEmits(['toggleModal', 'callLoginUser', 'callLoginMS'])

const credentials = ref({
  email: null,
  password: null
})

const isShowPassword = ref(false)
const toggleShowPassword = () => {
  isShowPassword.value = !isShowPassword.value
}
</script>
 
<template>
<div class="bgModal form w-full text-l text-black ">
    <div class="model-inside rounded-lg">
        <div class="container">
            <h1 class="text-3xl font-bold mb-5 text-center text-black">Login</h1>
            <label class="text-lg"><b>Email</b></label>
            <br><input class="mt-2 mb-2 inputField" type="text" v-model="credentials.email">
            <br><p class="-mt-1 text-slate-500"> Example: name@gmail.com</p>
            <br><label class="text-lg"><b>Password</b></label>
            <br><input class="mt-2 mb-2 inputField" :type="isShowPassword ? 'text' : 'password'" v-model="credentials.password">
            <button v-if="isShowPassword" class="m-2"><i id="show"><img src="../assets/eye.png"  width="20" @click="toggleShowPassword()"></i></button>
            <button v-else class="m-2"><i id="show"><img src="../assets/hidden.png"  width="20" @click="toggleShowPassword()"></i></button>
            <br><p class="-mt-1 text-slate-500"> Must have at least 8 characters</p>


            <!-- Button Login -->
            <div className='mt-6 flex flex-col'>
                    <button className='active:scale-[.98] active:duration-75 transition-all hover:scale-[1.01]  ease-in-out transform py-3 bg-black rounded-xl text-white font-bold text-sm'
                    @click="$emit('callLoginUser', credentials)">
                    Login</button>
                    <br><span><p class="-mt-2 mb-4 text-center">or</p></span>
              <!-- Button login with MS -->
                    <button 
                        className='active:scale-[.98] active:duration-75 transition-all hover:scale-[1.01]  ease-in-out transform py-3  rounded-xl text-gray-700 font-semibold text-sm border-2 border-gray-100 '
                        @click="$emit('callLoginMS')">
                            Login with MS
                    </button>
                    </div>
              <div>
                
            <!-- Create account -->
            <!-- <p class="text-sm font-light text-gray-500 dark:text-gray-500 mt-7">
              Don't have an account?
              <a href="/us4/user" class="font-medium text-primary-600 hover:underline dark:text-primary-500">Sign up</a>
            </p> -->
                   <!-- Close -->
                <button @click="$emit('toggleModal')" class="font-semibold px-1 mt-4 float-right bg-black text-white rounded text-l hover:bg-gray-600 hover:bg-gray-400 transition-color duration-200 delay-200" >Close</button>
              </div>
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
.form {
  padding-top: 11px;
  overflow-y: auto;
}
.container {
  padding: 16px;
}
.inputField{
  border: 2px solid black;
  border-radius: 5px;
  width: 15em;

}
</style>
