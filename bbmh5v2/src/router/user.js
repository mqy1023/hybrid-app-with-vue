
const Tabbar1 = () =>
  import(/* webpackChunkName: "tabbar1" */ '@components/tabbar1')
const Tabbar2 = () =>
  import(/* webpackChunkName: "tabbar1" */ '@components/tabbar2')

const Income = () =>
  import(/* webpackChunkName: "income" */ '@views/user/Income')
const BuyRecord = () =>
  import(/* webpackChunkName: "buyrec" */ '@views/user/BuyRecord')
const SellRecord = () =>
  import(/* webpackChunkName: "sellrec" */ '@views/user/SellRecord')
const Setting = () =>
  import(/* webpackChunkName: "home" */ '@views/user/Setting')
const UserCenter = () =>
  import(/* webpackChunkName: "usercenter" */ '@views/user/UserCenter')
const BankAccount = () =>
  import(/* webpackChunkName: "home" */ '@views/user/BankAccount')
const AlipayAccount = () =>
  import(/* webpackChunkName: "accalipay" */ '@views/user/AlipayAccount')
const WeixinAccount = () =>
  import(/* webpackChunkName: "accweixin" */ '@views/user/WeixinAccount')
const WeixinResult = () =>
  import(/* webpackChunkName: "weixinrec" */ '@views/user/WeixinResult')
const WeixinAccountResult = () =>
  import(/* webpackChunkName: "weixinaccrec" */ '@views/user/WeixinAccountResult')
const UserFeedBack = () =>
  import(/* webpackChunkName: "userfeedback" */ '@views/user/UserFeedBack')
const FeedBackResult = () =>
  import(/* webpackChunkName: "feedbackres" */ '@views/user/FeedBackResult')
const AlipayResult = () =>
  import(/* webpackChunkName: "alipayrec" */ '@views/user/AlipayResult')
const UnitSetting = () =>
  import(/* webpackChunkName: "unitsetting" */ '@views/user/UnitSetting')
const TransferSell = () =>
  import(/* webpackChunkName: "transfergsell" */ '@views/user/TransferSell')
const TransferGold = () =>
  import(/* webpackChunkName: "transferggold" */ '@views/user/TransferGold')
const TransferSearch = () =>
  import(/* webpackChunkName: "transfersearch" */ '@views/user/TransferSearch')
const GoldOperationList = () =>
  import(/* webpackChunkName: "goldoperationlist" */ '@views/user/GoldOperationList')
const WithdrawList = () =>
  import(/* webpackChunkName: "withdrawlist" */ '@views/user/WithdrawList')
const PayAccounts = () =>
  import(/* webpackChunkName: "payaccounts" */ '@views/user/PayAccounts')

export default [
  {
    path: '/user',
    name: 'user',
    components: {
      default: () => import(/* webpackChunkName: "home" */ '../views/user/Main.vue'),
      tabbar: Tabbar1
    },
    meta: {
      keepAlive: true,
      login: true
    }
  },
  {
    path: '/user2',
    name: 'user2',
    components: {
      default: () => import(/* webpackChunkName: "home" */ '../views/user/Main2.vue'),
      tabbar: Tabbar2
    },
    meta: {
      keepAlive: true,
      login: true
    }
  },
  {
    path: '/user/income',
    name: 'Income',
    component: Income,
    meta: {
      login: true
    }
  },
  {
    path: '/user/buyrec',
    name: 'BuyRecord',
    component: BuyRecord,
    meta: {
      login: true,
      keepAlive: true
    }
  },
  {
    path: '/user/sellrec',
    name: 'SellRecord',
    component: SellRecord,
    meta: {
      login: true
    }
  },
  {
    path: '/user/setting',
    name: 'Setting',
    component: Setting,
    meta: {
      login: true
    }
  },
  {
    path: '/user/center',
    name: 'UserCenter',
    component: UserCenter,
    meta: {
      login: true
    }
  },
  {
    path: '/user/bankacc',
    name: 'BankAccount',
    component: BankAccount,
    meta: {
      login: true
    }
  },
  {
    path: '/user/alipayacc',
    name: 'AlipayAccount',
    component: AlipayAccount,
    meta: {
      login: true
    }
  },
  {
    path: '/user/weixinacc',
    name: 'WeixinAccount',
    component: WeixinAccount,
    meta: {
      login: true
    }
  },
  {
    path: '/user/weixinrec',
    name: 'WeixinResult',
    component: WeixinResult,
    meta: {
      login: true
    }
  },
  {
    path: '/user/weixinaccrec',
    name: 'WeixinAccResult',
    component: WeixinAccountResult,
    meta: {
      login: true
    }
  },
  {
    path: '/user/feedback',
    name: 'UserFeedBack',
    component: UserFeedBack,
    meta: {
      login: true
    }
  },
  {
    path: '/user/alipayrec',
    name: 'AlipayResult',
    component: AlipayResult,
    meta: {
      login: true
    }
  },
  {
    path: '/user/feedbackres',
    name: 'FeedBackResult',
    component: FeedBackResult,
    meta: {
      login: false
    }
  },
  {
    path: '/user/unitset',
    name: 'UnitSetting',
    component: UnitSetting,
    meta: {
      login: false
    }
  },
  {
    path: '/user/transfersell',
    name: 'TransferSell',
    component: TransferSell,
    meta: {
      login: true,
      keepAlive: false
    }
  },
  {
    path: '/user/transfergold',
    name: 'TransferGold',
    component: TransferGold,
    meta: {
      login: true,
      keepAlive: false
    }
  },
  {
    path: '/user/transearch',
    name: 'TransferSearch',
    component: TransferSearch,
    meta: {
      login: false
    }
  },
  {
    path: '/user/goldoperlist',
    name: 'GoldOperationList',
    component: GoldOperationList,
    meta: {
      login: false
    }
  },
  {
    path: '/user/wdlist',
    name: 'WithdrawList',
    component: WithdrawList,
    meta: {
      login: true
    }
  },
  {
    path: '/user/payaccs',
    name: 'PayAccounts',
    component: PayAccounts,
    meta: {
      login: true
    }
  }
]
