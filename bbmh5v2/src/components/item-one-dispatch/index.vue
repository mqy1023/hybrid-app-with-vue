<template>
  <div class="item-order-main">
    <div class="item-order-content">
      <div class="item-order-num van-hairline--bottom" v-show="hotItem.payType == 1">实际交易金额：<span style="color: #4364FC;font-weight: bold">{{hotItem.trueTradeAmount || ''}}元</span></div>
      <div class="item-order-num van-hairline--bottom" v-show="hotItem.payType == 2">收款备注码：<span style="color: #E93F3F;font-weight: bold">{{hotItem.wxQrcodeRemark || ''}}</span></div>
      <div class="texts-center-wrapper flex-row-start">
        <div class="texts-center-left texts-center-common">
          <div class="texts-one-line">
            <span class="text-title">转卖金币：</span>
            <span class="text-value">{{cPriceShow}}</span>
            <span class="text-value">金币</span>
          </div>
          <div class="texts-one-line texts-one-line1">
            <div class="text-title" >下单时间：</div>
            <div class="text-value over-text-show" style="max-width: 35vw">{{timeBefore}}</div>
          </div>
        </div>
        <div class="texts-center-right texts-center-common" style="padding: 3vw 0">
          <div class="texts-one-line1">
            <span class="text-title">盈利金额：</span>
            <span class="right-money">{{hotItem.earn || '0.00'}}元</span>
          </div>
          <div class="texts-one-line1" v-show="hotItem.payType">
            <span class="text-title">支付方式：</span>
            <span class="right-money">{{hotItem.payType == 1 ? '支付宝' : '微信支付'}}</span>
          </div>
        </div>
      </div>
      <div class="info-bottom-wrapper flex-row-start">
        <div class="info-bottom-left">
          <span>{{cStatusEqual3 ? '您的确认收款时间' : '等待买家付款时间'}}</span>
          <van-count-down :time="timeForBuy"/>
        </div>
        <div class="info-bottom-right">
          <button-xbd :disabled="false" @click="onClickTopRight">我已收款</button-xbd>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import ButtonXbd from '@components/button-xbd'
import { CountDown } from 'vant'
Vue.use(CountDown)

export default {
  name: 'item-one-dispatch',
  components: {
    ButtonXbd
  },
  props: {
    hotItem: {
      type: Object,
      default: () => ({})
    },
    nowTimeStamp: {
      type: Number
    }
  },
  data () {
    return {
    }
  },
  created () {
  },
  computed: {
    cPriceShow () {
      let cPriceVal = this.hotItem.cPrice
      if (this.hotItem.cPrice.indexOf('.') > -1) {
        return cPriceVal.substring(0, cPriceVal.indexOf('.'))
      }
      return cPriceVal
    },
    timeBefore () {
      return this.$timeCompareWithNow(this.hotItem.cOrderCreateTimeStamp, this.nowTimeStamp)
    },
    cStatusEqual3 () {
      if (this.hotItem.cStatus == 3 && this.hotItem.userConfirmEndTimeStamp) {
        return true
      }
      return false
    },
    timeForBuy () {
      if (this.hotItem.cStatus == 3 && this.hotItem.userConfirmEndTimeStamp) {
        return this.$timeCompareWithBefore(this.hotItem.userConfirmEndTimeStamp, this.nowTimeStamp, true) * 1000
      } else if (this.hotItem.waitPayFinishTime) {
        console.log(this.$timeCompareWithBefore(this.hotItem.waitPayFinishTime, this.nowTimeStamp, true) * 1000)
        return this.$timeCompareWithBefore(this.hotItem.waitPayFinishTime, this.nowTimeStamp, true) * 1000
      }
      return 1000 * 1000
    }
  },
  methods: {
    onClickTopRight () {
      this.$emit('itemClick')
    }
  },
  destroyed () {
    this.timeout && clearTimeout(this.timeout)
  }
}
</script>

<style scoped lang="less">
  .item-order-main {
    box-sizing: border-box;
    width: 100vw;
    background-color: #f8f8f8;
    padding: 3.2vw 4vw 0;
    box-sizing: border-box;
    font-size: @font-size-md;
    .item-order-content {
      background-color: #ffffff;
      padding: 0 2vw 0;
      border-radius: 12px;
    }
    .item-order-num {
      height: 38px;
      line-height: 38px;
      font-weight: bold;
      font-size: @font-size-llg;
    }
    .flex-row-start {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
    }
    .texts-center-wrapper {
      .texts-center-common {
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        align-items: flex-start;
        height: 20vw;
        padding: 1.4vw 0;
      }
      .texts-center-right {
        align-items: flex-end;
      }
      .text-title {
        color: #666666;
      }
      .text-value {
        color: #333333;
      }
      .texts-one-line {
        display: flex;
        flex-direction: row;
        justify-content: flex-start;
        align-items: center;
      }
    }
    .info-bottom-left {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: flex-start;
      color: @xbd-theme-color;
      font-size: @font-size-lg;
    }
    .info-bottom-wrapper {
      padding: 4px 0 8px;
    }
    .van-count-down {
      height: 7vw !important;
      line-height: 7vw !important;
      font-size: @font-size-llg;
      color: @xbd-theme-color;
    }
    .info-bottom-right {
      width: 36vw;
      align-items: flex-end;
    }
  }

</style>
