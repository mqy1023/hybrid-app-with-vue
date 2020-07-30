<template>
    <div class="msg-content">
      <van-nav-bar
        title="切换微信支付账号"
        :fixed="true"
        left-arrow
        @click-left="onClickLeft"
      >
      </van-nav-bar>
      <div style="height:44px"></div>
      <div class="weixin-account-wrapper" v-show="curweixin">
        <div>当前使用中的微信支付账号是：</div>
        <div class="weixin-account">{{curweixin}}</div>
      </div>
      <van-pull-refresh class="msg-list-wrapper"
                        v-model="isReLoading"
                        @refresh="getRefeshHotList">
        <van-list v-model="isLoading"
                  :offset="100"
                  :finished-text="finishedText"
                  :finished="true"
                  :error.sync="error"
                  error-text="请求失败，点击重新加载"
                  :immediate-check='false'
                  :class="hotList.length==0?'msg-list':'msg-list-height'"
                  v-show="hotList.length">
          <item-one-weixin
            v-for="(item, i) in hotList"
            :key="i"
            :msgItem="item"
            :curWechat="curweixin"
            @clickLeftBtn="onClickLeftBtn(item)"
            @clickRightBtn="onClickRightBtn(item)"
          />
        </van-list>
        <div class="empty-wrapper flex-center" v-show="!isReLoading&&hotList.length==0">
          <div class="empty-show icon-size-common"></div>
          <div class="text-btn-wrapper">
            <div class="text-empty">暂时没有填写微信收款账号</div>
            <button-xbd :disabled="false" @click="onClickLeft" class="btn-save">去添加</button-xbd>
          </div>
        </div>

        <div class="empty-wrapper flex-center add-btn-wrapper" v-show="!isReLoading&&hotList.length==1&&hotList[0]['wechatAccount']==curweixin">
          <div class="text-btn-wrapper">
            <div class="text-empty">继续添加新的微信收款账号</div>
            <button-xbd :disabled="false" @click="onClickLeft" class="btn-save">去添加</button-xbd>
          </div>
        </div>

      </van-pull-refresh>
    </div>
</template>

<script>
import Vue from 'vue'
import ButtonXbd from '@components/button-xbd'
import { List, PullRefresh } from 'vant'
import ItemOneWeixin from '@components/item-one-weixin'
import { USER_WEIXIN_ACCOUNT_LIST, USER_WEIXIN_CHANGE } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
Vue.use(List).use(PullRefresh)
export default {
  name: 'WeixinResult',
  components: {
    ItemOneWeixin,
    ButtonXbd
  },
  data () {
    return {
      hotList: [],
      curweixin: '',
      account: '',
      name: '',
      isReLoading: false,
      isLoading: false,
      error: false
    }
  },
  mounted () {
    this.isReLoading = true
    this.getHots()
    this.curweixin = this.$route.query.curweixin // 当前使用的微信账号
    this.name = this.$route.query.name // 填写的真实姓名
    this.account = this.$route.query.account // 填写的微信账号
  },
  computed: {
    finishedText () {
      // if (this.hotList.length < 12) {
      //   return ''
      // } else {
      //   return '没有更多了'
      // }
      return ''
    }
  },
  methods: {
    onClickLeft () {
      // this.$router.go(-1)
      this.$router.replace({ path: '/user/weixinacc?account=' + (this.account || '') + '&name=' + (this.name || '') })
    },
    onClickLeftBtn (item) {
      this.$router.push({ path: '/user/weixinaccrec?curweixin=' + item.wechatAccount + '&fromlist=1' })
    },
    onClickRightBtn (item) {
      if (this.curweixin == item.wechatAccount) {
        this.$toast('当前微信支付账号已是这个账号，不能切换自己')
        return
      }
      let msgStr = '您确定要切换到' + item.wechatAccount + '支付账号吗？切换成功后，需要您登录微信使用' + item.wechatAccount + '微信账号去收款哟!'
      this.$dialog.confirm({
        message: msgStr,
        cancelButtonText: '暂不切换',
        confirmButtonText: '一键切换',
        cancelButtonColor: '#333333'
      }).then(() => {
        this.confirmChangeAccount(item) // 切换
      }).catch(() => {
      })
    },
    confirmChangeAccount (item) {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm, wechatAccount: item.wechatAccount }
      this.$reqPost(USER_WEIXIN_CHANGE, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          this.$toast('切换成功')
          this.curweixin = item.wechatAccount
          this.$router.go(-1)
        }
      }).catch(err => {
        console.log(err)
      })
    },
    getHots () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm }
      this.$reqPost(USER_WEIXIN_ACCOUNT_LIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        that.isReLoading = false
        that.isLoading = false
        if (res.status.code === 200) {
          that.hotList = res.data
        }
      }).catch(err => {
        console.log(err)
        that.isReLoading = false
        that.isLoading = false
        that.error = true
      })
    },
    getRefeshHotList () {
      if (!this.isLoading) {
        this.isReLoading = true
        this.getHots()
      } else {
        this.isReLoading = false
      }
    }
  }
}
</script>

<style scoped lang="less">
.msg-content {
  /*height: 100vh;*/
  width: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
}
.msg-list {
  background-color: #f3f3f3;
}
.msg-list-height {
  min-height: 90vh;
  background-color: #f3f3f3;
}
.msg-list-wrapper {
  min-height:94vh;
  background-color: #f3f3f3;
}
.icon-del {
  .bg-image('icon-del');
  vertical-align: middle;
}
  .weixin-account-wrapper {
    text-align: center;
    padding: 12px 0;
    background-color: #eeeeee;
    font-size: @font-size-lg;
  }
  .weixin-account {
    margin-top: 4px;
    color: @xbd-theme-color;
  }
  .empty-wrapper {
    flex-direction: column;
  }
  .empty-show {
    height: 50vw;
    width: 50vw;
    margin: 26vw 0 0 0;
    background-size: contain;
  }
  .text-empty {
    font-size: @font-size-lg;
    color: #333333;
    margin-bottom: 4vw;
  }
  .add-btn-wrapper {
    position: absolute;
    top: 50vw;
    left: 0;
    right: 0;
  }
</style>
