
const OutsideStatus = () =>
  import(/* webpackChunkName: "outsidestatus" */ '@views/outside/OutsideStatus')

export default [
  {
    path: '/outside/status',
    name: 'OutsideStatus',
    component: OutsideStatus,
    meta: {
      login: false
    }
  }
]
