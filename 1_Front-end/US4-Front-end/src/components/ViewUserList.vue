<script setup>
defineEmits(['callShowUser'])
defineProps({
  userList: {
    type: Array,
    default: []
  }
})
</script>

<template>
<div class="userList">
  <table class="content-table rounded-lg">
    <thead>
      <tr class="rounded-lg h-full">
        <th>Name</th>
        <th>Email</th>
        <th>Role</th> 
        <th></th>
      </tr>
    </thead>
    <tbody v-for="user in userList" :key="user">
      <tr>
        <td>{{ user.name}}</td>
        <td>{{ user.email}}</td>
        <td>{{ user.role}}</td>
        <td>
          <button @click="$emit('callShowUser', user.id)" class="font-semibold bg-black text-white rounded text-l hover:bg-gray-600 transition-color duration-200 delay-200">Show<br>Details</button>
        </td>
      </tr>
    </tbody>
  </table>
</div>
</template>

<style scoped>
.userList {
  padding-top: 11px;
  /* overflow-x: auto; */
  /* max-height: 500px; */
}
.content-table{
  border-collapse: collapse;
  font-size: small;
  /* min-height: 20em; */
}
.content-table thead tr{
  background-color: black;
  text-align: left;
  color: white;
}
.content-table th,
.content-table td{
  padding: 11px 15px;
}
</style>