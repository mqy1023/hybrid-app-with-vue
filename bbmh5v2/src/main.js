import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import { Lazyload, Icon, Cell, CellGroup, Loading, Button, Toast, NavBar, Dialog } from 'vant'
// import '@/assets/css/iconfont/iconfont.css'
import '@assets/less/base/index.less'
import 'lib-flexible/flexible.js' // 移动端适配
import reqAxios from '@plugins/reqAxios'
import vLoading from '@components/xbd-loading' // 加载动画插件
import * as rsaUtil from '@plugins/rsaUtil'
import '@/filters' // filters

// 加入vue原型中方便使用
Object.keys(rsaUtil).forEach((k) => (Vue.prototype[`$${k}`] = rsaUtil[k]))

Vue.use(vLoading)
Vue.use(reqAxios)
Vue.use(Icon)
Vue.use(Dialog)
Vue.use(Cell)
Vue.use(CellGroup)
Vue.use(Loading)
Vue.use(Button)
Vue.use(Toast)
Vue.use(NavBar)
Vue.use(Lazyload, {
  preLoad: 1.3,
  error: require('@/assets/imgs/goods_default.png'),
  loading: require('@/assets/imgs/goods_default.png'),
  attempt: 1,
  listenEvents: ['scroll'],
  lazyComponent: true
})

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
