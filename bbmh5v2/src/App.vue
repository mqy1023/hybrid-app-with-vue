
<template>
  <div id="app">
    <keep-alive>
      <router-view class="view-router"  v-if="$route.meta.keepAlive"></router-view>
    </keep-alive>
    <router-view class="view-router" v-if="!$route.meta.keepAlive"></router-view>
    <router-view name="tabbar"></router-view>
  </div>
</template>
<script>
import { getLocalStorage, setLocalStorage } from '@utils/local-storage'
import { HOME_APP_VERSION_CHECK } from '@api/home'
export default {
  name: 'app',
  mounted () {
    console.log('app mouted')
    const that = this
    window.callByNativeJump = function callByNativeJump (jumpUrl, params) {
      that.$router.push({ path: jumpUrl })
    }

    window.callByNativeJgId = function callByNativeJump (jgId) { // 设置极光id
      setLocalStorage({ jgId: jgId })
    }

    window.callByAndroidHideLoading = function callByAndroidHideLoading () { // 隐藏加载loading
      that.$vLoading.hide()
    }
    window.callByAndroidShowLoading = function callByAndroidShowLoading () { // 隐藏加载loading
      that.$vLoading.show()
    }
    // this.$router.push({ path: '/user' })
    const { tokenBbm } = getLocalStorage('tokenBbm')
    this.$store.dispatch('resetUnReadMsgCount') // 未读消息为0
    // 获取用户信息, 有可能出现抢单页面reload出现未登录的情况
    if (tokenBbm) {
      this.$store.dispatch('updateToken', tokenBbm) // 更新store中token的值
    } else {
      this.$getNativeFuncFromApp('setOpenType', '0') // 设置下次默认打开登录页
    }
    if (!this.$store.state.user.userInfo.mobile && tokenBbm && location.href.indexOf('outside') < 0 && location.href.indexOf('login') < 0) {
      this.$store.dispatch('getUserInfo')
    }
    // ios页面输入框失焦要页面重置到顶部
    if (this.$isIos()) {
      let flag = false
      let myFunction
      document.body.addEventListener('focusin', () => {
        // 软键盘弹起事件
        flag = true
        clearTimeout(myFunction)
      })
      document.body.addEventListener('focusout', () => {
        // 软键盘关闭事件
        if (flag) {
          myFunction = setTimeout(function () {
            window.scrollTo({ top: 0, left: 0, behavior: 'smooth' }) // 重点  =======当键盘收起的时候让页面回到原始位置(这里的top可以根据你们个人的需求改变，并不一定要回到页面顶部)
          }, 200)
        }
        flag = false
      })
    }
    if (location.href.indexOf('outside') < 0 && location.href.indexOf('order') < 0) {
      this.$reqPost(HOME_APP_VERSION_CHECK, { app_version: this.$store.state.app.appInfo.version || '1.0.1' }).then(res => {
        if (res.status.code === 3001) {
          const appVersionData = res.data
          that.updataApp(appVersionData)
        }
      }).catch(err => {
        console.log(err)
      })
    }

    this.$getNativeFuncFromApp('isBbmAgentApp')
    window.callByNativeIsBbmAgentApp = this.onCallByNativeIsBbmAgentApp
    setTimeout(() => {
      if (!this.isBbmAgentApp && location.href.indexOf('http://v2.hahaha.vip') > -1 && location.href.indexOf('outside') < 0) { // 正式环境下
        this.$dialog.alert({
          message: '请先下载帮帮买app',
          confirmButtonText: '我知道了'
        }).then(() => {
          window.location.href = 'http://h5page.hahaha.vip/?v=1.0.1&q=2'
        })
      }
    }, 280)
  },
  methods: {
    onCallByNativeIsBbmAgentApp () {
      this.isBbmAgentApp = true
    },
    updataApp (appVersionData) {
      if (appVersionData.type == 1) { // 建议更新
        this.$dialog.confirm({
          title: '版本更新',
          message: appVersionData.content || '版本需要更新~',
          cancelButtonText: '我知道了',
          confirmButtonText: '确定更新',
          cancelButtonColor: '#333333'
        }).then(() => {
          // 原生跳转浏览器
          this.$getNativeFuncFromApp('jumpBrowser', appVersionData.url)
        }).catch(() => {
          // on cancel
        })
      } else if (appVersionData.type == 2) { // 强制更新
        this.$dialog.alert({
          title: '版本更新',
          message: appVersionData.content || '版本需要更新~',
          confirmButtonText: '确定更新'
        }).then(() => {
          this.$getNativeFuncFromApp('jumpBrowser', appVersionData.url)
        })
      }
    }
  }
}
</script>
<style lang="less">
  @import "./assets/reset.min.css";
  .van-dialog {
    .van-dialog__header {
      padding: 20px 30px !important;
    }
  }
  .van-count-down {
    height: 9vw !important;
    line-height: 9vw !important;
  }
  .van-pull-refresh::-webkit-scrollbar {
    display:none
  }
  .van-nav-bar .van-icon {
    font-size: 24px;
  }
</style>
