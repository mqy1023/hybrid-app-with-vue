<template>
    <div class="setting-content">
      <van-nav-bar
        title="设置"
        :fixed='true'
        left-arrow
        @click-left="onClickLeft"
      />
      <div style="height:64px;background-color: #ffffff;"/>
      <div class="items-top-wrapper">
        <van-cell title="客服反馈" is-link @click="onClickModify"/>
        <van-cell title="清空缓存" is-link @click="onClickClear" v-show="!isIOS"/>
        <van-cell title="当前版本号" :value="version" />
      </div>

      <button-xbd :disabled="false" @click="onClickLogout" class="btn-xbd-default">退出登录</button-xbd>

    </div>
</template>

<script>
import ButtonXbd from '@components/button-xbd'
import { LOGIN_OUT } from '@api/login'
import { getLocalStorage, removeLocalStorage } from '@utils/local-storage'
export default {
  name: 'Setting',
  data () {
    return {
      isIOS: this.$isIos(),
      version: this.$store.state.app.appInfo.version || '1.0.0'
    }
  },
  components: {
    ButtonXbd
  },
  methods: {
    onClickLeft () {
      // this.$router.go(-1)
      const { userType } = getLocalStorage('userType')
      if (userType == 1) {
        this.$router.replace({ path: '/user' })
      } else {
        this.$router.replace({ path: '/user2' })
      }
    },
    onClickModify () {
      this.$router.push({ path: '/user/feedback' })
    },
    onClickClear () { // 清空安卓缓存数据
      this.$getNativeFuncFromApp('clearAndroidCache')
      this.$dialog.alert({
        message: '清空缓存可以解决数据缓存的bug，让app保持在最新版本哦~',
        confirmButtonText: '我知道了'
      }).then(() => {
        // that.$router.go(-1)
        this.$vLoading.show()
        this.timeOut = setTimeout(() => {
          this.$vLoading.hide()
          this.$toast('清除完成')
        }, Math.floor(Math.random() * 1000 + 1000))
      })
    },
    onClickLogout () {
      this.$dialog.confirm({
        title: '退出登录？',
        cancelButtonText: '不小心点错了',
        confirmButtonText: '确认退出',
        cancelButtonColor: '#333333'
      }).then(() => {
        this.$vLoading.show()
        this.$store.dispatch('resetUnReadMsgCount') // 未读消息为0
        const { tokenBbm } = getLocalStorage('tokenBbm')
        let params = { token: tokenBbm }
        this.$reqPost(LOGIN_OUT, { ...params, sign: this.$getJsaParam(params) }).then(res => {
          if (res.status.code === 200) {
            this.$getNativeFuncFromApp('clearUserId')
            removeLocalStorage('tokenBbm') // 清除原来的token的值
            this.$store.dispatch('updateToken', '') // 更新store中token的值
            this.$router.replace({ path: '/login' })
          } else {
            res.status.message && this.$toast(res.status.message)
          }
        }).catch(err => {
          console.log(err)
        })
      }).catch(() => {
        // on cancel
      })
    }
  },
  destroyed () {
    this.timeOut && clearTimeout(this.timeOut)
  }
}
</script>

<style scoped lang="less">
  .setting-content {
    background-color: #f8f8f8;
    height: 100%;
    .items-top-wrapper {
      background-color: #ffffff;
    }
  }
  .btn-xbd-default {
    margin-top: 18vh;
  }
  .btn-xbd-default /deep/ .button-xbd {
    background: #F8F8F8;
    border: 1px solid #dddddd;
    color: @color666
  }

</style>
