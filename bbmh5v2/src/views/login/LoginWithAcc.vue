<template>
  <div class="login-content">
    <van-nav-bar
      left-arrow
      :fixed="true"
      @click-left="onClickLeft"
    />
    <div class="clear-navbar-top"/>
    <div class="login-big-title">立即注册</div>

    <van-field
      v-model.trim="phone"
      center
      clearable
      maxlength="11"
      placeholder="请输入11位手机号码"
      type="number"
    >
    </van-field>
    <van-field type="number" clearable v-model.trim="code" placeholder="请输入验证码" maxlength="6">
      <van-button :disabled="disabledSend" @click="onSendCode" slot="button" size="small" type="primary">{{codeText}}
      </van-button>
    </van-field>
    <van-field
      v-model.trim="inviteCode"
      center
      clearable
      maxlength="12"
      placeholder="请输入邀请码"
      type="text"
    >
    </van-field>
    <button-xbd :disabled="isDisabledStart" @click="onClickLogin">登录</button-xbd>
    <div style="height: 25vh"></div>
    <div class="bottom-login1" @click="onClick2PhoneLogin">快捷登录</div>
  </div>
</template>

<script>
import Vue from 'vue'
import { Field, Checkbox } from 'vant'
import ButtonXbd from '@components/button-xbd'
import { LOGIN_SENDCODE, LOGIN_REGISTER } from '@api/login'
import store from '@/store'
import { setLocalStorage, getLocalStorage } from '@utils/local-storage'
Vue.use(Field).use(Checkbox)

export default {
  name: 'LoginWithAcc',
  components: { // 注册组件
    ButtonXbd
  },
  data () {
    return {
      code: this.code,
      phone: this.phone,
      inviteCode: this.inviteCode,
      codeText: '获取验证码',
      time: 60
    }
  },
  mounted () {
    this.$getNativeFuncFromApp('getJPushId')
    this.$store.dispatch('resetUnReadMsgCount') // 未读消息为0
    this.$getNativeFuncFromApp('setOpenType', '0') // 代理级别和下次打开页面有关
  },
  computed: { // 监听子组件的disabled用于启用或禁用按钮
    isDisabledStart () {
      if (this.phone && this.phone.length === 11 && this.code && this.code.length === 6) {
        // 如果input框有值就让disabled为false 不禁用
        return false
      } else {
        return true
      }
    },
    disabledSend () {
      if (!this.phone || this.phone.length < 11) { // 输入手机号才可以发送验证码
        return true
      }
      return this.time > 0 && this.time !== 60
    }
  },
  methods: {
    onClickLeft () {
      // this.$router.go(-1) // 返回上一层
      // this.$router.replace({ path: '/login' })
      this.back2PhoneLogin()
    },
    onClick2PhoneLogin () {
      // this.$router.go(-1) // 返回上一层
      this.back2PhoneLogin()
    },
    onClickLogin () {
      const phoneVal = this.phone
      if (!this.$isMatchPhone(phoneVal)) {
        this.$toast('手机号输入不正确')
        return
      }
      if (!this.inviteCode) {
        this.$toast('请先填写邀请码')
        return
      }
      let params = {
        mobile: this.phone,
        mesCode: this.code,
        inviteCode: this.inviteCode || ''
      }
      this.$vLoading.show()
      this.$reqPost(LOGIN_REGISTER, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          const userType = res.data.userType
          setLocalStorage({
            tokenBbm: res.data.token,
            userType: userType // 1为一级代理，2为二级代理
          })
          this.$store.dispatch('updateToken', res.data.token)
          this.$store.dispatch('getUserInfo')
          // 设置极光推送别名和tag标签
          this.$getNativeFuncFromApp('setJPushAlias', res.data.userId)
          this.$getNativeFuncFromApp('setJPushTag', userType)
          this.$getNativeFuncFromApp('setOpenType', userType) // 代理级别和下次打开页面有关
          // setTimeout(() => {
          if (userType == 1) {
            this.$router.replace({ path: '/home' })
          } else if (userType == 2) {
            this.$router.replace({ path: '/dispatch' })
          }
          // this.routerRedirect()
          // }, 160)
        } else if (res.status.code === 4007) {
          this.$dialog.confirm({
            message: '该账号不存在，请核对账号后重试或重新去注册',
            confirmButtonText: '快速登录'
          }).then(() => {
            // this.$router.go(-1)
            // this.$router.replace({ path: '/login' })
            this.back2PhoneLogin()
          }).catch(() => {
            // on cancel
          })
        } else if (res.status.code == 4008 || res.status.code == 4032) { // 注册但没设置过密码
          let msgStr = '密码错误'
          if (res.status.code == 4032) {
            msgStr = '请先设置密码'
          }
          this.$dialog.confirm({
            message: msgStr,
            confirmButtonText: '设置密码'
          }).then(() => {
            this.$router.replace({ path: '/login/setpwd' })
          }).catch(() => {
            // on cancel
          })
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
      })
    },
    routerRedirect () {
      const { query } = this.$route
      this.$router.replace({
        path: query.redirect || '/home'
      })
    },
    back2PhoneLogin () {
      const { query } = this.$route
      if (query.redirect) { // 要跳转的情况
        this.$router.replace({
          path: '/login',
          query: query
        })
      } else {
        this.$router.replace({ path: '/login' })
      }
    },
    onSendCode () {
      const phoneVal = this.phone
      if (!this.$isMatchPhone(phoneVal)) {
        this.$toast('手机号输入不正确')
        return
      }
      let params = {
        mobile: this.phone,
        // requested_at: this.$getTimestamp(),
        type: '1' // 1 注册登录 2 设置密码
      }

      this.$reqPost(LOGIN_SENDCODE, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          this.tickTimer() // 验证码的时间设置
        } else {
          this.codeText = '重新获取验证码'
          this.time = 60
        }
      }).catch(err => {
        this.codeText = '重新获取验证码'
        this.time = 60
        console.log(err)
      })
    },
    tickTimer () {
      if (this.time > 1) {
        this.time--
        this.codeText = '重新发送(' + this.time + 's)'
        this.timeout = setTimeout(this.tickTimer, 1000)
      } else {
        this.time = 60
        this.codeText = '重新获取验证码'
      }
    }
  }
}
</script>

<style scoped lang="less">
  @import '../../assets/less/login.less';
</style>
