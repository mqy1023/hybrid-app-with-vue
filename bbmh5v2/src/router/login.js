
const LoginWithAcc = () =>
  import(/* webpackChunkName: "login" */ '@views/login/LoginWithAcc')
// const LoginWithPhone = () => import('@views/login/LoginWithPhone')
const LoginWithPhone = () =>
  import(/* webpackChunkName: "login" */ '@views/login/LoginWithPhone')
const LoginPwdReset = () =>
  import(/* webpackChunkName: "loginreset" */ '@views/login/LoginPwdReset')
const LoginPwdSet = () =>
  import(/* webpackChunkName: "pwdset" */ '@views/login/LoginPwdSet')

export default [
  {
    path: '/login',
    name: 'login',
    component: LoginWithPhone
  },
  {
    path: '/register/acc',
    name: 'loginAcc',
    component: LoginWithAcc
  },
  {
    path: '/login/reset',
    name: 'LoginPwdReset',
    component: LoginPwdReset
  },
  {
    path: '/login/setpwd',
    name: 'LoginPwdSet',
    component: LoginPwdSet
  }
]
