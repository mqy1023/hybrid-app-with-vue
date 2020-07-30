
const Tabbar = () =>
  import(/* webpackChunkName: "tabbar1" */ '@components/tabbar2')

const Rules = () =>
  import(/* webpackChunkName: "rules" */ '@views/game/Rules')
const TransferBank = () =>
  import(/* webpackChunkName: "transbank" */ '@views/game/TransferBank')
const TransferAlipay = () =>
  import(/* webpackChunkName: "transali" */ '@views/game/TransferAlipay')

export default [
  {
    path: '/game',
    name: 'game',
    components: {
      default: () => import(/* webpackChunkName: "home" */ '../views/game/Main.vue'),
      tabbar: Tabbar
    },
    meta: {
      keepAlive: true,
      login: true
    }
  },
  {
    path: '/game/rules',
    name: 'Rules',
    component: Rules,
    meta: {
      keepAlive: true
    }
  },
  {
    path: '/game/transbank',
    name: 'TransferBank',
    component: TransferBank,
    meta: {
      keepAlive: true
    }
  },
  {
    path: '/game/transali',
    name: 'TransferAlipay',
    component: TransferAlipay,
    meta: {
      keepAlive: true
    }
  }
]
