<script setup></script>

<template>
<div class="bg-neutral-200 p-4 px-7">
  <div class="float-right mt-12 mr-10 text-l font-semibold text-white">
    <router-link :to="{ name: 'OASIP'}" class="mr-10"> OASIP</router-link>
    <router-link :to="{ name: 'User'}"> User</router-link>
  </div>
  <div>
    <router-view> </router-view>
  </div>
</div>
</template>

<style scoped></style>
