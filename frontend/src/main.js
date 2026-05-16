import { createApp } from 'vue'
import { createRouter, createWebHashHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import Home from './views/Home.vue'
import OwnerManage from './views/OwnerManage.vue'
import BillManage from './views/BillManage.vue'
import PaymentManage from './views/PaymentManage.vue'
import DiscountManage from './views/DiscountManage.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/owner', component: OwnerManage },
  { path: '/bill', component: BillManage },
  { path: '/payment', component: PaymentManage },
  { path: '/discount', component: DiscountManage }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
