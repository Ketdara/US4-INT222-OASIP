export const userAPI = {

  getUsersAsPage: async function (pageNum) {
    var res = null;
    try{
      res = await fetch(import.meta.env.VITE_BASE_URL + `users?page=${pageNum}`, {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('jwtToken')}` }
      });
    }catch(err) {
      alert('Error: Could not fetch users');
      return null;
    }

    if (res.status === 200) {
      let userPage = await res.json();
      console.log(`[getUsersAsPage: ${pageNum}] Successful`);
      return userPage;
    }
    if(res.status === 400 || res.status === 401 || res.status === 404) {
      res.json().then(promise => {
        console.log(`[getUsersAsPage: ${pageNum}] Error: ` + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
        
        if(promise.message.toLowerCase().includes("expired") === true) {
          localStorage.setItem('isJwtExpired', true)
        }
      });
      return false;
    }
    if(res.status === 403) {
      res.json().then(promise => {
        console.log(`[getUsersAsPage: ${pageNum}] Error: ` + promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log(`[getUsersAsPage: ${pageNum}] Error: Unknown`);
    alert('Error occurred when getting users');
    return null;
  },

  getUserById: async function (id) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + `users/${id}`, {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('jwtToken')}` }
    });

    if(res.status === 200) {
      let user = await res.json();
      console.log('[getUserById] Successful');
      return user;
    }
    if(res.status === 400 || res.status === 401 || res.status === 403 || res.status === 404) {
      res.json().then(promise => {
        console.log('[getUserById] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log('[getUserById] Error: Unknown');
    alert('Error occurred when getting user');
    return null;
  },

  postUser: async function (user) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + 'users', { method: 'POST',
      headers: { 'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`, 'content-type': 'application/json' },
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
    if(res.status === 400 || res.status === 401 || res.status === 403 || res.status === 404) {
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
      headers: { 'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`, 'content-type': 'application/json' },
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
    if(res.status === 400 || res.status === 401 || res.status === 403 || res.status === 404) {
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
    const res = await fetch(import.meta.env.VITE_BASE_URL + `users/${id}`, { method: 'DELETE',
      headers: { 'Authorization': `Bearer ${localStorage.getItem('jwtToken')}` }
    });

    if(res.status === 200) {
      console.log('[deleteUserById] Successful');
      return true;
    }
    if(res.status === 400 || res.status === 401 || res.status === 403 || res.status === 404){
      res.json().then(promise => {
        console.log('[deleteUserById] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
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
      let userData = await res.json(); 
      console.log('[loginUser] Successful');

      localStorage.setItem('jwtToken', userData.jwtToken);
      localStorage.setItem('refreshToken', userData.refreshToken);
      localStorage.setItem('email', userData.email)
      localStorage.setItem('role', userData.role)
      localStorage.setItem('name', userData.name)

      localStorage.removeItem('isJwtExpired')
      return true;
    }
    if(res.status === 400 || res.status === 401 || res.status === 404) {
      res.json().then(promise => {
        console.log('[loginUser] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log('[loginUser] Error: Unknown');
    alert('Error occurred when login user');
    return false;
  },

  loginMSUser: async function (idToken) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + 'users/ms-login', { method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        idToken: localStorage.getItem('idToken'),
      })
    })

    if(res.status === 200){
      let userData = await res.json(); 
      console.log('[loginMSUser] Successful');

      localStorage.setItem('jwtToken', userData.jwtToken);
      localStorage.setItem('refreshToken', userData.refreshToken);
      localStorage.setItem('email', userData.email)
      localStorage.setItem('role', userData.role)
      localStorage.setItem('name', userData.name)

      localStorage.removeItem('isJwtExpired')
      return true;
    }
    if(res.status === 400 || res.status === 401 || res.status === 404) {
      res.json().then(promise => {
        console.log('[loginMSUser] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log('[loginMSUser] Error: Unknown');
    alert('Error occurred when login user');
    return false;
  },

  matchUser: async function (user) {
    const res = await fetch(import.meta.env.VITE_BASE_URL + 'users/match', { method: 'POST',
      headers: { 'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`, 'content-type': 'application/json' },
      body: JSON.stringify({
        email: user.email,
        password: user.password
      })
    })

    if(res.status === 200){
      console.log('[matchUser] Successful');
      return true;
    }
    if(res.status === 400 || res.status === 401 || res.status === 403 || res.status === 404) {
      res.json().then(promise => {
        console.log('[matchUser] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log('[matchUser] Error: Unknown');
    alert('Error occurred when login user');
    return false;
  },

  refreshToken: async function () {
    const res = await fetch(import.meta.env.VITE_BASE_URL + 'users/refresh', { method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        refreshToken: localStorage.getItem('refreshToken')
      })
    })

    if(res.status === 200){
      let tokens = await res.json();
      console.log('[refreshToken] Successful');

      localStorage.setItem('jwtToken', tokens.jwtToken);
      localStorage.setItem('refreshToken', tokens.refreshToken);
      return true;
    }
    if(res.status === 400 || res.status === 401 || res.status === 404) {
      res.json().then(promise => {
        console.log('[refreshToken] Error: ' + promise.message.replace(/; /g, '\n'));
        alert(promise.message.replace(/; /g, '\n'));
      });
      return false;
    }
    console.log('[refreshToken] Error: Unknown');
    alert('Error occurred when login user');
    return false;
  }

};