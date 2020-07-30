<template>
  <div class="home-content">
    <van-pull-refresh v-model="isReLoading"
                      @refresh="getRefeshHotList">
      <div class="home-top-wrapper">
        <div class="msg-icon-wrapper" @click="onClickMsg">
          <span class="icon-msg icon-size-common" />
          <span class="red-circle" v-show="unReadMsgCount>0">{{unReadMsgCount}}</span>
        </div>
        <div class="swiper-wrapper">
          <van-swipe @change="onChange" :autoplay="2600" class="child-full-parent">
            <van-swipe-item v-for="(item, index) in banners" :key="index" @click="onClickSwipe(item)">
              <img v-lazy="item.link" width="100%" height="100%"/>
            </van-swipe-item>
            <div class="custom-indicator" slot="indicator" v-show="banners.length>1">
              <span v-for="(image1, index1) in banners" :key="index1" :class="{'active-dot':currentDot == index1}"></span>
            </div>
          </van-swipe>
        </div>
      </div>
      <div class="item-top-title ">
        <div style="height: 1.6vh"/>
        <div class="choose-text1">选择充值面额</div>
        <div class="choose-text2">购买的金额越大，赚取的越多</div>
      </div>
      <div class="all-choose">
      <item-one-hot
        v-for="(item, index) in hotList"
        :key="index + 5"
        :item="item"
        :class="{'choose-active':isActive == index}"
        @itemClick="onItemClick(index, item)"
      />
    </div>
    </van-pull-refresh>
    <van-popup
      v-model="isShowPopup"
      round
      @close="closePopup"
      safe-area-inset-bottom
      position="bottom"
      :style="{ height: '30%' }"
    >
      <div class="popup-content">
        <div class="popup-title">购买详情</div>
        <div class="popup-center">
          <div class="popup-center-one">
            <span>购买金币（个）：</span>
            <span>{{goldNumShow}}</span>
          </div>
          <div class="popup-center-one">
            <span>金币售价（元）：</span>
            <span>{{chooseItem.value}}</span>
          </div>
          <div class="popup-center-num">
            <span style="font-weight: bold;color: #222222">购买数量：</span>
            <van-stepper v-model="num" min="1" integer />
          </div>
        </div>
        <div class="popup-bottom">
          <div class="popup-bottom-left">
            <span>数量：</span>
            <span>{{num}}</span>
            <span>；</span>
            <span>合计：</span>
            <span style="color: #4364FC;font-size: 18px">￥{{num * chooseItem.value || ''}}</span>
          </div>
          <div class="popup-bottom-right" @click="onClickCommit">
            提交订单
          </div>
          </div>
      </div>
    </van-popup>
  </div>
</template>

<script>
import Vue from 'vue'
import { Swipe, SwipeItem, Popup, Stepper, List, PullRefresh } from 'vant'
import itemOneHot from '@components/item-one-hot'
import { HOME_BANNERS, HOME_HOTS, HOME_BUY_PRODUCT } from '@api/home'
import { getLocalStorage } from '@utils/local-storage'
Vue.use(Swipe).use(SwipeItem).use(Popup).use(Stepper).use(List).use(PullRefresh)

export default {
  name: 'home',
  components: {
    itemOneHot
  },
  data () {
    return {
      isReLoading: false,
      banners: [],
      currentDot: 0,
      pageCount: 1,
      hotList: [],
      isActive: -1,
      chooseItem: {},
      isShowPopup: false,
      num: 1,
      isRequesting: false
    }
  },
  created () {
  },
  mounted () {
    this.$vLoading.show()
    this.isRequesting = true
    this.getBanners()
    this.productDetail()
  },
  activated () {
    this.pageCount++
    if ((this.banners.length == 0 || this.hotList.length == 0 || this.pageCount % 10 == 0) && !this.isRequesting) {
      this.$vLoading.show()
      this.getBanners()
      this.productDetail()
    }
    const { tokenBbm } = getLocalStorage('tokenBbm')
    if (tokenBbm) {
      this.$store.dispatch('getUserInfo')
    }
  },
  computed: {
    unReadMsgCount () { // 未读消息条数
      return this.$store.state.user.userInfo.notReadNotificationNum
    },
    goldNumShow () {
      const goldNum = (this.chooseItem.value || '0') + ''
      if (!goldNum) {
        return 0
      } else if (goldNum && goldNum.indexOf('.') > -1) {
        return goldNum.slice(0, -3)
      }
      return goldNum
    }
  },
  methods: {
    onClickMsg (e) {
      this.$router.push({ path: '/home/msg' })
      e.stopPropagation()
      e.preventDefault()
    },
    getRefeshHotList () {
      this.$vLoading.show()
      this.getBanners()
      this.productDetail()
    },
    getBanners () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm, type: 1 }
      this.$reqPost(HOME_BANNERS, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          this.banners = res.data
        }
      }).catch(err => {
        console.log(err)
      })
    },
    productDetail () {
      const that = this
      this.$vLoading.show()
      this.$reqPost(HOME_HOTS, { sign: this.$getJsaParam({ }) }).then(res => {
        this.isRequesting = false
        this.isReLoading = false
        if (res.status.code === 200) {
          that.hotList = res.data
        }
      }).catch(err => {
        console.log(err)
        this.isRequesting = false
        this.isReLoading = false
      })
    },
    onItemClick (index, item) {
      // if (!this.$store.state.user.userAccountInfo.bankUsername) { // 还没上传银行卡信息
      //   this.$dialog.confirm({
      //     message: '您还没有上传银行卡账号，请填写您的银行账号！',
      //     confirmButtonText: '去填写',
      //     cancelButtonColor: '#333333'
      //   }).then(() => {
      //     this.$router.push({ path: '/user/bankacc?fromhome=1' })
      //   }).catch(() => {
      //   })
      //   return
      // } else if (this.$store.state.user.userAccountInfo.bankStatus == 2) {
      //   this.$dialog.alert({
      //     message: '银行卡账号在审核中，审核通过后才能购买~',
      //     confirmButtonText: '我知道了'
      //   }).then(() => {
      //   })
      //   return
      // } else if (this.$store.state.user.userAccountInfo.bankStatus == 3) {
      //   this.$dialog.confirm({
      //     message: '银行卡账号审核未通过，请重新填写',
      //     confirmButtonText: '去填写',
      //     cancelButtonColor: '#333333'
      //   }).then(() => {
      //     this.$router.push({ path: '/user/bankacc?fromhome=1' })
      //   }).catch(() => {
      //   })
      //   return
      // }
      this.isActive = index
      this.chooseItem = item
      this.isShowPopup = true
    },
    onClickSwipe (item) {
      console.log('item=', item)
      // const jumpLink = item.openTypeArgs
      // if (jumpLink) {
      //   if (jumpLink.indexOf('http') === 0) {
      //     window.location.href = jumpLink
      //   } else {
      //     this.$router.push({ path: jumpLink })
      //   }
      // }
    },
    closePopup () {
      this.isShowPopup = false
      this.num = 1 // 重置数量
      this.isActive = -1
      this.chooseItem = {}
    },
    onClickCommit () {
      const { tokenBbm } = getLocalStorage('tokenBbm')
      // const params = { ...this.chooseItem, token: tokenBbm }
      const that = this
      const { productId, value } = this.chooseItem
      const params = { productId, value, count: this.num, token: tokenBbm }
      this.$vLoading.show()
      this.closePopup()
      this.$reqPost(HOME_BUY_PRODUCT, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        // res.status.message && this.$toast(res.status.message)
        if (res.status.code === 200) {
          this.$router.push({ path: '/home/orderpay?oid=' + res.data.orderNum })
        } else if (res.status.code === 5067) {
          this.$dialog.alert({
            message: '今日购买达到上限了呦~，请明天继续购买~',
            confirmButtonText: '我知道了'
          }).then(() => {
            // that.$router.go(-1)
          })
        } else if (res.status.code === 6020) {
          this.$dialog.alert({
            message: '您还没有上传银行卡账号请填写您的银行账号!',
            confirmButtonText: '去填写'
          }).then(() => {
            this.$router.push({ path: '/user/bankacc' })
          })
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
      })
    },
    onChange (index) {
      this.currentDot = index
    }
  }
}
</script>
<style scoped lang="less">
  .home-content {
    height: 95vh;
    /*height: 100%;*/
    /*margin-bottom: 46px;*/
  }
  .home-top-wrapper {
    position: relative;
    height: 220px
  }
  .swiper-wrapper {
    height: 216px;
    position: relative;
  }
  .msg-icon-wrapper {
    position: absolute;
    padding: 6px;
    top: 3.4vw;
    left: 2.4vw;
    z-index: 999;
    color: #ffffff;
    .icon-msg {
      width: 24px;
      height: 24px;
      .bg-image('icon-msg');
    }
    .red-circle {
      position: absolute;
      top: -2px;
      right: -2px;
      width: 14px;
      height: 14px;
      line-height: 14px;
      border-radius: 8px;
      background-color: #E93F3F;
      text-align: center;
      display: inline-block;
    }
  }
  .custom-indicator {
    position: absolute;
    bottom: 16px;
    right: 8px;
    span {
      display: inline-block;
      width: 6px;
      height: 6px;
      border-radius: 6px;
      /*background-color: rgba(255, 255, 255, 0.6);*/
      background-color: #fff;
      margin: 0 2px
    }
    .active-dot {
      width: 14px;
      background-color: #fff;
    }
  }
  .item-top-title {
    padding: 4vw 3vw
  }
  .all-choose {
    margin: 0 2vw 2vw;
    overflow:hidden;
    padding-bottom: 8vh;
  }
  .choose-text1 {
    font-size: @font-size-lllg;
    margin-bottom: 6px;
  }
  .choose-text2 {
    font-size: @font-size-md;
    color: @xbd-theme-color;
  }
  .popup-content {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    height: 100%;
    width: 100%;
    box-sizing: border-box;
    .popup-title {
      padding: 2.6vh 0 1vh;
      color: #222222;
      font-size: @font-size-llg;
      font-weight: bold;
    }
    .popup-center {
      flex: 1;
      width: 96vw;
      display: flex;
      justify-content: space-around;
      padding: 1vh 0;
      align-items: flex-start;
      color: #999999;
      font-size: @font-size-md;
      flex-direction: column;
      margin-left: 4vw;
      .popup-center-num {
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
    .popup-bottom {
      width: 100vw;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: #F9F9F9;
      font-size: @font-size-md;
      color: #333333;
      line-height: 14vw;
      height: 14vw;
      .popup-bottom-left {
        flex: 1;
        padding: 0 4vw;
        display: flex;
        justify-content: flex-start;
        align-items: center;
      }
      .popup-bottom-right {
        padding: 0 8vw;
        color: #ffffff;
        font-size: @font-size-lg;
        background: linear-gradient(left, #5FA4FF, #4364FC);
      }
    }
  }

</style>
