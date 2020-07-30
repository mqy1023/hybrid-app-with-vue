
const Tabbar = () =>
  import(/* webpackChunkName: "tabbar2" */ '@components/tabbar2')

export default [
  {
    path: '/dispatch',
    name: 'dispatch',
    components: {
      default: () => import(/* webpackChunkName: "dispatch" */ '@views/dispatch/Main'),
      tabbar: Tabbar
    },
    meta: {
      keepAlive: false,
      login: true
    }
  }
]
