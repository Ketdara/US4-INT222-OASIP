export const userAPI = {
  getUsersAsPage: async function (pageNum) {
    var res = null;
    try{
      res = await fetch(import.meta.env.VITE_BASE_URL + `users?page=${pageNum}`);
    }catch(err) {
      alert('Error: Could not fetch users');
      return null;
    }

    if (res.status === 200) {
      let userPage = await res.json();
      console.log(`[getUsersAsPage: ${pageNum}] Successful`);
      return userPage;
    }
    console.log(`[getUsersAsPage: ${pageNum}] Error: Unknown`);
    alert('Error occurred when getting users');
    return null;
  },

  getUserById: async function (id) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + `users/${id}`);
    if(res.status === 200) {
      let user = await res.json();
      console.log('[getUserById] Successful');
      return user;
    }
    if(res.status === 404){
      console.log('[getUserById] Error: User not found or does not exist');
      alert('Error occurred: User not found or does not exist');
      return null;
    }
    console.log('[getUserById] Error: Unknown');
    alert('Error occurred when getting user');
    return null;
  },

  postUser: async function (user) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + 'users', { method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        name: user.name,
        email: user.email,
        role: user.role,
        password: user.password
      })
    })

    if(res.status === 201){
      console.log('[postUser] Successful');
      return true;
    }
    if(res.status === 400) {
      res.json().then(promise => {
        console.log('[postUser] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log('[postUser] Error: Unknown');
    alert('Error occurred when posting user');
    return false;
  },

  putUser: async function (user) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + `users/${user.id}`, { method: 'PUT',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        id: user.id,
        name: user.name,
        email: user.email,
        role: user.role
      })
    })

    if(res.status === 200){
      console.log('[putUser] Successful');
      return true;
    }
    if(res.status === 400) {
      res.json().then(promise => {
        console.log('[putUser] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log('[putUser] Error: Unknown');
    alert('Error occurred when putting user');
    return false;
  },

  deleteUserById: async function (id) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + `users/${id}`, {method: 'DELETE'});

    if(res.status === 200) {
      console.log('[deleteUserById] Successful');
      return true;
    }
    if(res.status === 404){
      res.json().then(promise => {
        console.log('[deleteUserById] Error: User not found or does not exist');
        alert('Error occurred: User not found or does not exist');
      });
      return false;
    }
    console.log('[deleteUserById] Error: Unknown');
    alert('Error occurred when deleting user');
    return false;
  },

  loginUser: async function (user) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + 'users/login', { method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        email: user.email,
        password: user.password
      })
    })

    if(res.status === 200){
      let tokenObj = await res.json();
      console.log('[loginUser] Successful');
      return tokenObj.token;
    }
    if(res.status === 400 || res.status === 401 || res.status === 404) {
      res.json().then(promise => {
        console.log('[loginUser] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return null;
    }
    console.log('[loginUser] Error: Unknown');
    alert('Error occurred when login user');
    return null;
  },

  matchUser: async function (user) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + 'users/match', { method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        email: user.email,
        password: user.password
      })
    })

    if(res.status === 200){
      console.log('[matchUser] Successful');
      return true;
    }
    if(res.status === 400 || res.status === 401 || res.status === 404) {
      res.json().then(promise => {
        console.log('[matchUser] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log('[matchUser] Error: Unknown');
    alert('Error occurred when login user');
    return false;
  }
};