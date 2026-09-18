import {createApp} from 'vue'
import './style.css'
import App from './App.vue'
import 'virtual:uno.css'
import {setupRouter} from "/@/router";
import {setupStore} from "/@/store";
import directive from "/@/directive";


const app = createApp(App)
setupRouter(app)
setupStore(app)
app.use(directive);
app.mount('#app')
