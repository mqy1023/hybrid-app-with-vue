<template>
  <div class="login-content">
    <van-nav-bar
      left-arrow
      title="修改密码"
      :fixed="true"
      @click-left="onClickLeft"
    />
    <div class="clear-navbar-top"/>
    <div class="login-big-title" style="padding: 20px 20px 0;">当前手机号</div>
    <div class="login-big-title" style="margin: 0;padding-top: 0">{{phone}}</div>
    <van-field type="number" clearable v-model.trim="code" placeholder="请输入验证码" maxlength="6">
      <van-button :disabled="disabledSend" @click="onSendCode" slot="button" size="small" type="primary">{{codeText}}
      </van-button>
    </van-field>
    <van-field :type="pwdText=='显示密码' ? 'password' : 'text'" clearable v-model.trim="pwd" placeholder="请输入6~15位数字与英文结合的密码" maxlength="15" >
      <van-button :disabled="false" @click="onShowOrHidePwd" slot="button" size="small" type="primary">{{pwdText}}
      </van-button>
    </van-field>
    <button-xbd :disabled="isDisabledStart" @click="onClickLogin">修改</button-xbd>
  </div>
</template>

<script>
import Vue from 'vue'
import { Field, Checkbox } from 'vant'
import ButtonXbd from '@components/button-xbd'
import { LOGIN_SENDCODE, LOGIN_PWD_SET } from '@api/login'
import { setLocalStorage } from '@utils/local-storage'
Vue.use(Field).use(Checkbox)

export default {
  name: 'LoginPwdReset',
  components: { // 注册组件
    ButtonXbd
  },
  data () {
    return {
      phone: this.phone,
      code: this.code,
      pwd: this.pwd,
      codeText: '获取验证码',
      pwdText: '显示密码',
      time: 60
    }
  },
  created () {
    this.phone = this.$store.state.user.userInfo.mobile || ''
  },
  computed: { // 监听子组件的disabled用于启用或禁用按钮
    isDisabledStart () {
      if (this.pwd && this.pwd.length > 5 && this.code && this.code.length === 6) {
        // 如果input框有值就让disabled为false 不禁用
        return false
      } else {
        return true
      }
    },
    disabledSend () {
      return this.time > 0 && this.time !== 60
    }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1) // 返回上一层
    },
    onShowOrHidePwd () {
      this.pwdText = (this.pwdText === '显示密码') ? '隐藏密码' : '显示密码'
    },
    onClickLogin () {
      const pwdVal = this.pwd
      if (!this.$isMatchPwd(pwdVal)) {
        this.$toast('请输入6~15位数字与英文结合的密码')
        return
      }
      this.timeout && clearTimeout(this.timeout) // 去掉验证码倒计时
      if (this.codeText != '获取验证码') {
        this.codeText = '重新获取验证码' // 去掉倒计时后重置发送验证码
      }
      this.time = 60
      let params = {
        mobile: this.phone,
        password: this.pwd,
        code: this.code
      }
      this.$vLoading.show()
      this.$reqPost(LOGIN_PWD_SET, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        res.status.message && this.$toast(res.status.message)
        if (res.status.code === 200) {
          this.$router.back()
        } else {
          this.pwd = ''
          this.code = ''
        }
      }).catch(err => {
        console.log(err)
      })
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
        type: '2' // 1 注册登录 2 设置密码
      }

      this.$reqPost(LOGIN_SENDCODE, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        res.status.message && this.$toast(res.status.message)
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
