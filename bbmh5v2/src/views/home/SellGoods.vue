<template>
  <div class="home-order-pay">
    <van-nav-bar
      title="订单状态"
      :fixed="true"
      left-arrow
      @click-left="onClickLeft"
    />
    <div class="order-top-wrapper clear-navbar-top">
      <div style="margin-top: 6vw"><span  :class="iconName"></span></div>
      <div class="order-top-title1">{{orderStatusText}}</div>
      <div class="refresh-btn-wrapper" @click="onClickefresh" v-show="isShowRef">
        <span class="icon-size-common icon-refresh"></span>
        <span>点击刷新查看商家确认收款状态</span>
      </div>
    </div>

    <div class="order-details-wrapper" style="margin-top: 5vh">
      <div class="detail-title">订单详情</div>
      <div class="one-line-detail">
        <span class="detail-left">购买金币(个)：</span>
        <span>{{(orderInfo.orderAllValue&&orderInfo.orderAllValue.slice(0, -3))}}</span>
      </div>
      <div class="one-line-detail">
        <span class="detail-left">总金额(元)：</span>
        <span>￥{{(orderInfo.orderAllValue)}}</span>
      </div>
      <div class="one-line-detail">
        <span class="detail-left">订单号：</span>
        <span>{{oid}}</span>
      </div>
      <div class="one-line-detail">
        <span class="detail-left">订单创建时间：</span>
        <span>{{(orderInfo.orderCreateTime)}}</span>
      </div>
    </div>
    <div class="two-btn-wrap two-btn-wrap1" v-show="orderStatusRef!=7&&orderStatusRef!=3">
      <button-xbd :disabled="false" @click="onClickCancel" class="btn-left">{{leftBtnText}}</button-xbd>
      <button-xbd :disabled="false" @click="onClickSucc" class="btn-right">{{rightBtnText}}</button-xbd>
    </div>
    <popup-xbd :is-show="isShowPopup" @cancelTradeClick="onCancelTrade" @confirmTradeClick="onConfirmTrade" />
  </div>
</template>

<script>
import Vue from 'vue'
import VueClipboard from 'vue-clipboard2'
import { CountDown, Popup, DropdownMenu, DropdownItem } from 'vant'
import ButtonXbd from '@components/button-xbd'
import { HOME_TRADE_STATUS, HOME_CHANGE_PRODUCT_STATUS, HOME_RESELL } from '@api/home'
import { getLocalStorage } from '@utils/local-storage'
import PopupXbd from '@components/popup-xbd'

Vue.use(CountDown).use(VueClipboard).use(Popup).use(DropdownMenu).use(DropdownItem)

export default {
  name: 'SellGoods',
  components: {
    ButtonXbd,
    PopupXbd
  },
  data () {
    return {
      type: '',
      price: 0,
      isShowPopup: false,
      orderStatusRef: -1,
      oid: '',
      orderInfo: {}
    }
  },
  mounted () {
    this.oid = this.$route.query.oid // 订单id
    this.orderInfo = this.$store.state.order.orderInfo
    this.onClickefresh()
  },
  computed: {
    leftBtnText () {
      if (this.orderStatusRef === 4) { // 支付宝状态是通过状态
        return '我暂时不卖'
      } else if (this.orderStatusRef === 7) { // todo
        return '取消交易'
      }
      return '返回首页'
    },
    rightBtnText () {
      // if (this.orderStatusRef === 4 && this.alipayStatusRef == 1) {
      //   return '立即转卖'
      // }else
      if (this.orderStatusRef === 7) { // todo
        return '申诉我已付款'
      } else if (this.orderStatusRef === 3) { // todo
        return '我要申诉'
      }
      return '我的购买订单'
    },
    iconName () {
      if (this.orderStatusRef === 4) {
        return 'icon-succ icon-size-common1'
      } else if (this.orderStatusRef === 7 || this.orderStatusRef === 3) {
        return 'icon-fail icon-size-common1'
      }
      return ''
    },
    orderStatusText () {
      if (this.orderStatusRef === 2) {
        return '等待商家确认收款'
      } else if (this.orderStatusRef === 4) {
        return '订单购买成功'
      } else if (this.orderStatusRef === 7) {
        return '订单交易失败，商家未收到款'
      } else if (this.orderStatusRef === 3) {
        return '订单超时交易失败'
      }
      return '等待商家确认收款'
    },
    isShowRef () {
      return (this.orderStatusRef !== 7 && this.orderStatusRef !== 3)
    }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1)
    },
    onClickefresh () {
      if (!this.oid) {
        return
      }
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm || this.$store.state.app.token, orderNum: this.oid }
      this.$vLoading.show()
      this.$reqPost(HOME_TRADE_STATUS, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          that.orderStatusRef = res.data.orderStatus
          // that.orderStatusRef = 3

          if (that.orderStatusRef == 3) {
            that.$router.replace({ path: '/home/orderstatus?oid=' + this.oid })
          }
          if (that.orderStatusRef == 5) {
            this.$router.replace({ path: '/user/buyrec?tab=5' }) // 跳到历史订单
          }
        }
      }).catch(err => {
        console.log(err)
      })
    },
    onCancelTrade (detail) {
      console.log('cancelTradeClick', detail)
      this.isShowPopup = false
      this.changeOrderStatus(6, detail)
    },
    onConfirmTrade () {
      console.log('confirmTradeClick')
      this.isShowPopup = false
    },
    onClickCancel () {
      console.log('cancelTradeClick')
      if (this.orderStatusRef == 4) { // 支付宝通过状态
        this.$dialog.confirm({
          title: '您确定今天放弃不转卖吗？转卖的价格可能会出现波动哟~',
          message: '一旦暂时放弃转卖“请去-我的-我的购买订单-待转卖',
          cancelButtonText: '不小心点错了',
          confirmButtonText: '确认暂时放弃',
          cancelButtonColor: '#333333'
        }).then(() => {
          this.$router.replace({ path: '/user/buyrec?tab=2' }) // 跳到待转卖
        }).catch(() => {
          // on cancel
        })
      } else if (this.orderStatusRef === 7) { // 取消交易
        this.isShowPopup = true
      } else {
        this.$router.replace({ path: '/home' }) // 返回首页
      }
    },
    onClickSucc () {
      console.log('confirmTradeClick')
      if (this.orderStatusRef == 3) { // todo
        this.$router.replace({ path: '/order/complaint?oid=' + this.oid + '&fromover=1' })
        return
      } else if (this.orderStatusRef == 7) { // todo
        this.$router.replace({ path: '/order/complaint?oid=' + this.oid + '&fromrefuse=1' })
        return
      } else if (this.orderStatusRef == 4) { // 成功跳转历史订单
        this.$router.replace({ path: '/user/buyrec?tab=5' }) // 跳到购买订单记录
        return
      }

      this.$router.replace({ path: '/user/buyrec?tab=2' }) // 跳到购买订单记录
    },
    changeOrderStatus (status, detailStr) {
      // this.$router.replace({ path: '/home/orderstatus' })
      // return
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { status: status, token: tokenBbm, detail: detailStr, orderNum: this.oid }
      this.$reqPost(HOME_CHANGE_PRODUCT_STATUS, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          if (status === 6) { // 取消
            this.$router.replace({ path: '/home' })
          } else if (status === 2) { // 支付成功
            this.$store.dispatch('updateOrderInfo', res.data)
            this.$router.replace({ path: '/home/sell?oid=' + res.data.orderNum })
          } else { // 超时
            this.$router.replace({ path: '/home/orderstatus?oid=' + res.data.order_num })
          }
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
        if (status === 3) {
          this.$dialog.alert({
            message: '该笔订单已超时',
            confirmButtonText: '我知道了'
          }).then(() => {
            that.$router.go(-1) // 返回上一层
          })
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
  @import '../../assets/less/home.less';

  .van-icon-warning {
    font-size: 26px;
    color: red;
    margin-right: 2vw;
  }
  .refresh-btn-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 24px;
    background: linear-gradient(left, #5FA4FF, #4364FC);
    padding: 14px 0;
    width: 92vw;
    margin: 6vw 4vw 0;
    box-sizing: border-box;
    font-size: @font-size-lllg;
    color: #fff;
    .icon-refresh {
      .bg-image('icon-hand');
      margin-right: 8px;
    }
  }
  .icon-fail {
    .bg-image('icon-fail');
  }
  .icon-succ {
    .bg-image('icon-succ');
  }
  .icon-size-common1 {
    width: 30px;
    height: 30px;
    display: inline-block;
    margin: 0 2vw;
    background-size: contain;
    background-repeat: no-repeat;
  }
  .order-top-title1 {
    color: #666;
    font-size: @font-size-llg;
    margin:  2vw 0 4vw;
  }
  .two-btn-wrap1 {
    position: absolute;
    bottom: 6vh;
  }

</style>
