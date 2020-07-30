<template>
  <div class="setting-content">
    <div class="van-nav-bar van-nav-bar--fixed van-hairline--bottom nav-bar" style="z-index: 1;">
      <van-icon name="arrow-left" @click="onClickLeft" />
      <div class="van-nav-bar__title1 van-ellipsis1 search-content">
        <van-icon name="search" />
        <div class="search-text">
          <input v-model.trim="phone" placeholder="请输入购买人手机号" type="number"/>
          <van-icon name="close" v-if="isShowClose" @click="onClearPhone" />
        </div>
      </div>
      <div class="right-content" @click="onSearch">
        搜索
      </div>
    </div>
    <div class="title-wrapper"><span class="phone-text">手机号</span><span class="operate-text">操作</span></div>
    <van-pull-refresh class="msg-list-wrapper"
                      @refresh="getRefeshHotList"
                      v-model="isReLoading">
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
import itemTransferSell from '@components/item-transfer-sell'
import PopupTransferGold from '@components/popup-transfer-gold'
import { USER_INVITE_LIST, USER_NEXT_GOLD } from '@api/user'
import { getLocalStorage, removeLocalStorage } from '@utils/local-storage'
import Vue from 'vue'
import { List, PullRefresh } from 'vant'

Vue.use(List).use(PullRefresh)

export default {
  name: 'TransferSearch',
  data () {
    return {
      isShowClose: false,
      phone: '',
      currentPage: 1, // 当前页
      pageSize: 18,
      isNoMoreHots: true, // 是否没有更多数据
      hotList: [],
      isReLoading: false,
      isLoading: false,
      error: false,
      balance: 0,
      selectedMobile: '',
      isShowPopup: false,
      isFirstStep: true
    }
  },
  components: {
    itemTransferSell,
    PopupTransferGold
  },
  watch: {
    phone (newVal) {
      if (newVal != null && newVal != '') {
        this.isShowClose = true
      } else {
        this.isShowClose = false
      }
    }
  },
  computed: {
    finishedText () {
      if (this.hotList.length < 30) {
        return ''
      } else {
        return '没有更多了'
      }
    }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1)
    },
    onClearPhone () {
      this.phone = ''
      this.hotList = []
    },
    onSearch () {
      this.currentPage = 1
      if (this.phone.length < 3) {
        this.$toast('至少输入3位手机号')
        return
      }
      this.getHots()
    },
    getRefeshHotList () {
      if (!this.isLoading) {
        this.currentPage = 1
        this.getHots()
      } else {
        this.isReLoading = false
      }
    },
    getLoadHotList () {
      if (!this.isNoMoreHots && !this.isReLoading) {
        this.isLoading = true // 请求前要先设置标识
        this.currentPage += 1
        this.getHots()
      }
    },
    toGoldDetail () {
      this.$router.replace({ path: '/user/income?type=1' })
    },
    getHots () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm, page: this.currentPage, perPage: this.pageSize, mobile: this.phone }
      this.$vLoading.show()
      this.$reqPost(USER_INVITE_LIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        that.isReLoading = false
        that.isLoading = false
        if (res.status.code === 200) {
          const resData = res.data
          const lData = resData.list
          const pageData = resData.pageInfo
          that.isNoMoreHots = (pageData.currentPage >= pageData.totalPage) // 是否还有更多数据
          that.balance = res.data.balance
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
    onClickTransfer (item) {
      console.log(item.mobile)
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
    onClickWithdraw (item) { // 提现转账
      this.$router.replace({ path: '/user/wdlist?phone=' + item.mobile })
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
    background-color: #f8f8f8;
    height: 100%;
    .items-top-wrapper {
      background-color: #ffffff;
    }
  }
  .nav-bar {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 4px 12px;
    box-sizing: border-box;
  }
  .search-content {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    height: 32px;
    line-height: 32px;
    margin: 7px 2vw;
    padding: 0 8px;
    background-color: #f9f9f9;
    border-radius: 16px;
    flex: 1;
    .search-text {
      flex: 1;
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: row;
      justify-content: flex-start;
      align-items: center;
      input {
        background-color: transparent;
        width: 90%;
        margin-left: 3%;
      }
    }
  }
  .right-content {
    padding: 0 8px;
    font-size: @font-size-md;
  }
  .van-icon-close {
    color: #BCBCBC;
    font-size: 18px;
  }
  .title-wrapper {
    position: fixed;
    top: 46px;
    display: flex;
    height: 6vh;
    line-height: 6vh;
    padding: 0 18vw 0 10vw;
    /*margin-top: 3vh;*/
    width: 100%;
    box-sizing: border-box;
    justify-content: space-between;
    align-items: center;
    color: #666666;
    font-weight: bold;
    font-size: @font-size-lg;
    background-color: #ffffff;
    z-index: 1000;
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
    min-height: 86vh;
    background-color: #f9f9f9;
  }
  .msg-list-wrapper {
    min-height:87vh;
    margin-top: 13vh;
    background-color: #f9f9f9;
  }
  .empty-show {
    height: 28vh;
    width: 50vw;
    margin: 10vh 25vw 0;
  }

</style>
