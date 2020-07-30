<template>
    <div class="setting-content">
      <van-nav-bar
        title="转卖"
        :fixed='true'
        left-arrow
        @click-left="onClickLeft"
        @click-right="onClickRight"
      >
        <van-icon name="search" slot="right" />
      </van-nav-bar>
      <div style="height: 44px;"></div>
      <div class="infos-top-wrapper">
        <div class="title" style="margin-top: 3vh">当前总金币</div>
        <div class="gold">{{balanceShow}}<span> 币</span></div>
        <div class="tips">提示：1金币 = 1元</div>
        <div class="text">当前可转卖金币: {{sellBalanceShow}} <span style="color:red;margin-left: 8px" v-show="noSellBalanceShow!=0">盈利提现：{{noSellBalanceShow}}且不可转卖</span></div>
        <div class="title-wrapper"><span class="phone-text">手机号</span><span class="operate-text">操作</span></div>
      </div>
      <van-pull-refresh class="msg-list-wrapper"
                        v-model="isReLoading"
                        @refresh="getRefeshHotList">
        <van-list v-model="isLoading"
                  :offset="100"
                  :finished="isNoMoreHots"
                  :finished-text="finishedText"
                  :error.sync="error"
                  error-text="请求失败，点击重新加载"
                  :immediate-check='false'
                  @load="getLoadHotList"
                  :class="hotList.length==0?'msg-list':'msg-list-height'"
                  v-show="hotList.length||isNoMoreHots">
          <item-transfer-sell
            v-for="(item, i) in hotList"
            :key="i"
            :msgItem="item"
            @clickWithdraw="onClickWithdraw(item)"
            @clickTransfer="onClickTransfer(item)"
          />
        </van-list>
        <div class="empty-show icon-size-common" v-show="!isReLoading&&hotList.length==0"></div>
      </van-pull-refresh>
      <popup-transfer-gold :isShowPopup="isShowPopup" :selectedMobile="selectedMobile" :isFirstStep="isFirstStep" :balance="balance" @popupStatus="onChangePopupStatus" @callBack="toGoldDetail" />
    </div>
</template>

<script>
import Vue from 'vue'
import { List, PullRefresh } from 'vant'
import itemTransferSell from '@components/item-transfer-sell'
import PopupTransferGold from '@components/popup-transfer-gold'
import { USER_INVITE_LIST, USER_NEXT_GOLD } from '@api/user'
import { getLocalStorage, removeLocalStorage } from '@utils/local-storage'
Vue.use(List).use(PullRefresh)

export default {
  name: 'TransferSell',
  data () {
    return {
      totalBalance: 0,
      balance: 0,
      earnBalance: 0,
      currentPage: 1, // 当前页
      pageSize: 8,
      isNoMoreHots: true, // 是否没有更多数据
      hotList: [],
      isReLoading: false,
      isLoading: false,
      error: false,
      selectedMobile: '',
      isShowPopup: false,
      isFirstStep: true
    }
  },
  components: {
    itemTransferSell,
    PopupTransferGold
  },
  computed: {
    balanceShow () {
      if (!this.totalBalance) {
        return '0'
      }
      return Vue.filter('parseNum')(this.totalBalance)
    },
    sellBalanceShow () {
      if (!this.balance) {
        return '0'
      }
      return Vue.filter('parseNum')(this.balance)
    },
    noSellBalanceShow () {
      if (!this.earnBalance) {
        return '0'
      }
      return Vue.filter('parseNum')(this.earnBalance)
    },
    finishedText () {
      if (this.hotList.length < 16) {
        return ''
      } else {
        return '没有更多了'
      }
    }
  },
  mounted () {
    if (this.$store.state.user.userInfo.nextPointStatus != 2) { // 未设置下级支付通道
      this.showSetPayPoint()
    }
    this.getHots()
  },
  destroyed () {
    this.timeOut && clearTimeout(this.timeOut)
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1)
    },
    onClickRight () {
      this.$router.push({ path: '/user/transearch' })
    },
    showSetPayPoint () {
      this.$dialog.confirm({
        message: '您还没有给您的好友设置支付通道点位',
        confirmButtonText: '去设置',
        cancelButtonColor: '#333333'
      }).then(() => {
        this.$router.replace({ path: '/user/unitset' })
      }).catch(() => {
        // on cancel
      })
    },
    getHots () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm, page: this.currentPage, perPage: this.pageSize }
      this.$vLoading.show()
      this.$reqPost(USER_INVITE_LIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        that.isReLoading = false
        that.isLoading = false
        if (res.status.code === 200) {
          const resData = res.data
          const lData = resData.list
          const pageData = resData.pageInfo
          that.isNoMoreHots = (pageData.currentPage >= pageData.totalPage) // 是否还有更多数据
          that.totalBalance = res.data.totalBalance
          that.balance = res.data.balance
          that.earnBalance = res.data.earnBalance

          that.isLoading = false
          if (Array.isArray(lData)) { // 返回数据中有数组数据
            if (that.currentPage === 1) { // 刷新重置列表数据
              this.isReLoading = false
              that.hotList = lData
            } else { // 上拉加载更多
              that.hotList = that.hotList.concat(lData)
            }
          }
        }
      }).catch(err => {
        console.log(err)
        that.isReLoading = false
        that.isLoading = false
        that.isNoMoreHots = true
        that.error = true
      })
    },
    getRefeshHotList () {
      if (!this.isLoading) {
        this.currentPage = 1
        this.getHots()
      } else {
        this.isReLoading = false
      }
    },
    toGoldDetail () {
      this.$router.replace({ path: '/user/income?type=1' })
    },
    getLoadHotList () {
      if (!this.isNoMoreHots && !this.isReLoading) {
        this.isLoading = true // 请求前要先设置标识
        this.currentPage += 1
        this.getHots()
      }
    },
    onClickTransfer (item) {
      console.log(item.mobile, item.pointStatus)
      if (this.balance < 1) {
        this.$toast('金币不足')
        return
      }
      if (this.$store.state.user.userInfo.nextPointStatus != 2) { // 未设置下级支付通道
        this.showSetPayPoint()
        return
      } else if (item.pointStatus == 2) { // 下级点位未设置
        this.$dialog.confirm({
          message: '您的好友没有同意您设置的支付点位，是否继续转卖？',
          confirmButtonText: '继续转卖',
          cancelButtonText: '不同意转卖',
          cancelButtonColor: '#333333'
        }).then(() => {
          this.selectedMobile = item.mobile
          this.isShowPopup = true
        }).catch(() => {
          // on cancel
        })
        return
      }
      this.selectedMobile = item.mobile
      this.isShowPopup = true
    },
    onClickWithdraw (item) { // 提现转账
      this.$router.push({ path: '/user/wdlist?phone=' + item.mobile })
    },
    onChangePopupStatus (isShow, isFirstStep, isResetSelectedMobile) {
      console.log('a=', isShow)
      console.log('b=', isFirstStep)
      console.log('c=', isResetSelectedMobile)
      this.isShowPopup = isShow
      this.isFirstStep = isFirstStep
      if (isResetSelectedMobile) {
        this.selectedMobile = ''
      }
    }
  }
}
</script>

<style scoped lang="less">
  .setting-content {
    background-color: #ffffff;
    height: 100vh;
    width: 100vw;
    .infos-top-wrapper {
      display: flex;
      justify-content: space-around;
      align-items: center;
      flex-direction: column;
      height: 28vh;
      padding: 0;
      width: 100vw;
      box-sizing: border-box;
      position: fixed;
      z-index: 1000;
      background-color: #ffffff;
      .title {
        color: @xbd-theme-color;
        font-size: @font-size-llg;
      }
      .gold {
        color: @xbd-theme-color;
        height: 44px;
        line-height: 44px;
        font-size: @font-size-llllg;
        font-weight: bold;
        span {
          font-size: @font-size-lg;
        }
      }
      .tips {
        color: #999999;
        font-size: @font-size-md;
      }
      .text {
        color: #666666;
        font-size: @font-size-md;
        margin-top: 2vh;
      }
    }
    .title-wrapper {
      display: flex;
      height: 6vh;
      line-height: 6vh;
      padding: 0 17vw 0 10vw;
      margin-top: 2vh;
      width: 100%;
      box-sizing: border-box;
      justify-content: space-between;
      align-items: center;
      color: #666666;
      font-weight: bold;
      font-size: @font-size-lg;
    }
  }
  .msg-content {
    /*height: 100vh;*/
    width: 100%;
    display: flex;
    flex-direction: column;
  }
  .msg-list {
    background-color: #f9f9f9;
  }
  .msg-list-height {
    min-height: 68vh;
    background-color: #f9f9f9;
  }
  .msg-list-wrapper {
    min-height: 70vh;
    margin-top: 28vh;
    background-color: #f9f9f9;
  }
  .empty-show {
    height: 28vh;
    width: 50vw;
    margin: 10vh 25vw 0;
  }
</style>
