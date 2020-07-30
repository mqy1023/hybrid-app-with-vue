
const Tabbar = () =>
  import(/* webpackChunkName: "tabbar1" */ '@components/tabbar1')

const HomeMsg = () =>
  import(/* webpackChunkName: "homemsg" */ '@views/home/Msg')

const OrderPay = () =>
  import(/* webpackChunkName: "orderpay" */ '@views/home/OrderPay')

const OrderStatus = () =>
  import(/* webpackChunkName: "orderstatus" */ '@views/home/OrderStatus')

const SellGoods = () =>
  import(/* webpackChunkName: "sellgoods" */ '@views/home/SellGoods')
const OrderTranComplaint = () =>
  import(/* webpackChunkName: "ordercomplaint" */ '@views/order/TranComplaint')

export default [
  {
    path: '/home',
    name: 'home',
    components: {
      default: () => import(/* webpackChunkName: "home" */ '../views/home/Main.vue'),
      tabbar: Tabbar
    },
    meta: {
      keepAlive: true,
      login: true
    }
  },
  {
    path: '/home/msg',
    name: 'HomeMsg',
    component: HomeMsg,
    meta: {
      login: true
    }
  },
  {
    path: '/home/orderpay',
    name: 'OrderPay',
    component: OrderPay,
    meta: {
      login: true
    }
  },
  {
    path: '/home/orderstatus',
    name: 'OrderStatus',
    component: OrderStatus,
    meta: {
      keepAlive: false,
      login: true
    }
  },
  {
    path: '/home/sell',
    name: 'SellGoods',
    component: SellGoods,
    meta: {
      login: true
    }
  },
  {
    path: '/order/complaint',
    name: 'OrderTranComplaint',
    component: OrderTranComplaint,
    meta: {
      login: false
    }
  },
  {
    path: '*',
    redirect: {
      name: 'home'
    }
  }
]
