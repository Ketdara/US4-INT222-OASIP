<script setup>
  import { ref } from 'vue';
defineEmits(['toggleModal', 'callMatchUser'])

const credentials = ref({
  email: null,
  password: null
})
</script>
 
<template>
<div class="bgModal form w-full text-l text-black ">
    <div class="model-inside rounded-lg">
        <div class="container">
            <h1 class="text-3xl font-bold mb-5 text-center text-black">Match</h1>
            <label class="text-lg"><b>Email</b></label>
            <br><input class="m-2 inputField" type="text" placeholder="Enter Email" v-model="credentials.email">
            <br><label class="text-lg"><b>Password</b></label>
            <br><input class="m-2 inputField" type="password" placeholder="Enter Password" v-model="credentials.password"><br>
            <!-- Button Match -->
            <button
              type="button"
              class="m-4 ml-2 px-7 py-3 bg-black text-white font-medium text-sm leading-snug uppercase rounded shadow-md hover:bg-gray-400 hover:shadow-lg focus:shadow-lg focus:outline-none focus:ring-0 active:bg-black-400 active:shadow-lg transition duration-150 ease-in-out"
              @click="$emit('callMatchUser', credentials)"
            >
              Match</button>
              <div>
                   <!-- Close -->
                <button @click="$emit('toggleModal')" class="font-semibold px-1 mt-3 float-right bg-black text-white rounded text-l hover:bg-gray-600 hover:bg-gray-400 transition-color duration-200 delay-200" >Close</button>
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
