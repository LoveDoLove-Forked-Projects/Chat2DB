import Store from 'electron-store';

const store = new Store({
  name: process.env.APP_NAME,
});

export default store;
