<template>
    <div class="setting-content">
      <van-nav-bar
        title="收款账号"
        :fixed='true'
        left-arrow
        @click-left="onClickLeft"
      >
      </van-nav-bar>
      <div style="height:52px;background-color: #ffffff;"/>
      <div class="items-top-wrapper">
        <div class="items-top-one" @click="onClickAlipay">
          <span class="icon-size-common icon-alipay icon-common"></span>
          <span>支付宝收款账号</span>
          <div class="top-one-right">
            <span :style="spanAliapyColor" >{{alipayStatusText}}</span>
            <van-icon name="arrow" />
          </div>
        </div>
        <div class="van-hairline--bottom"></div>
        <div class="items-top-one" @click="onClickWeixin">
          <span class="icon-size-common icon-weixin icon-common"></span>
          <span>微信收款账号</span>
          <div class="top-one-right">
            <span :style="spanWechatColor">{{weixinStatusText}}</span>
            <van-icon name="arrow" />
          </div>
        </div>
      </div>
      <div class="notice-texts-wrapper clearfix" style="margin-top: 3vh">
        <div class="notice-title">温馨提示：</div>
        <div class="one-line-text"><div>1. </div><div> 两个收款账号都填写，派单赚取的金币机会就越多；</div></div>
        <div class="one-line-text"><div>2. </div><div> 请填写真实有效的收款账号，方便收款；</div></div>

      </div>
    </div>
</template>

<script>

import { USER_CURR_WEIXIN } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'

export default {
  name: 'PayAccounts',
  data () {
    return {
      fromDispatch: 0,
      curWechatAccount: ''
    }
  },
  components: {
  },
  mounted () {
    // this.$store.dispatch('getUserInfo')
    if (this.$store.state.user.userAccountInfo.wechatStatus != 4) { // 1去查看，2待审核，3审核拒绝，4未完善
      this.getUserWechatAccount()
    }
    this.fromDispatch = this.$route.query.fromdispatch
  },
  computed: {
    alipayStatusText () {
      if (this.$store.state.user.userAccountInfo.alipayStatus == 1) {
        return '已完善'
      } else if (this.$store.state.user.userAccountInfo.alipayStatus == 2) {
        return '待审核'
      } else if (this.$store.state.user.userAccountInfo.alipayStatus == 3) {
        return '审核未通过'
      }
      return '去完善'
    },
    weixinStatusText () { // 收款账号
      if (this.$store.state.user.userAccountInfo.wechatStatus == 1) {
        return '去查看'
      } else if (this.$store.state.user.userAccountInfo.wechatStatus == 2) {
        return '待审核'
      } else if (this.$store.state.user.userAccountInfo.wechatStatus == 3) {
        return '审核未通过'
      }
      return '去完善'
    },
    spanAliapyColor () {
      if (this.$store.state.user.userAccountInfo.alipayStatus == 1) {
        return 'color: #666666'
      } else if (this.$store.state.user.userAccountInfo.alipayStatus == 2) {
        return 'color: #486EFF'
      } else if (this.$store.state.user.userAccountInfo.alipayStatus == 3) {
        return 'color: red'
      }
      return 'color: #486EFF'
    },
    spanWechatColor () {
      if (this.$store.state.user.userAccountInfo.wechatStatus == 1) {
        return 'color: #666666'
      } else if (this.$store.state.user.userAccountInfo.wechatStatus == 2) {
        return 'color: #486EFF'
      } else if (this.$store.state.user.userAccountInfo.wechatStatus == 3) {
        return 'color: red'
      }
      return 'color: #486EFF'
    }
  },
  methods: {
    onClickLeft () {
      if (this.fromDispatch) {
        this.$router.replace({ path: '/user2' })
      } else {
        this.$router.go(-1)
      }
    },
    getUserWechatAccount () {
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm }
      this.$vLoading.show()
      this.$reqPost(USER_CURR_WEIXIN, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.data.status == 1) {
          this.curWechatAccount = res.data.wechatAccount
        }
      }).catch(err => {
        console.log(err)
      })
    },
    onClickRight () {
    },
    onClickAlipay () {
      this.$router.replace({ path: '/user/alipayacc' })
    },
    onClickWeixin () {
      // if (this.curWechatAccount) {
      //   this.$router.replace({ path: '/user/weixinacc' })
      // this.$router.replace({ path: '/user/weixinaccrec?curweixin=' + this.curWechatAccount })
      // return
      // }
      if (this.$store.state.user.userAccountInfo.wechatStatus != 4 && !this.curWechatAccount) {
        this.$router.replace({ path: '/user/weixinrec' })
        return
      }
      this.$router.replace({ path: '/user/weixinacc' })
    }
  }
}
</script>

<style scoped lang="less">
  .setting-content {
    background-color: #f8f8f8;
    height: 100%;
    .items-top-wrapper {
      background-color: #ffffff;
      padding: 0 4vw;
      box-sizing: border-box;
      .items-top-one {
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
        font-size: @font-size-md;
        .top-one-right {
          display: flex;
          flex-direction: row;
          justify-content: flex-end;
          align-items: center;
          flex: 1;
          padding: 18px 0;
        }
      }
      .icon-common {
        width: 28px;
        height: 28px;
        margin-right: 2vw;
      }
      .icon-alipay {
        .bg-image('icon-alipay');
      }
      .icon-weixin {
        .bg-image('icon-weixin');
      }
      .van-icon {
        color: #999999;
        margin-left: 1vw;
      }
    }
  }

</style>
