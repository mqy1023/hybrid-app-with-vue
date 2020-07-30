<template>
  <div class="undone-order-wrapper">
    <div class="undone-order-item">
      <div class="undone-order-top van-hairline--bottom">
        <span>{{hotItem.username || ''}}</span>
        <div class="undone-order-top-right">
          <span>{{cStatusEqual3 ? '您的确认收款时间' : '等待买家付款时间'}}</span>
          <van-count-down :time="timeForBuy"/>
        </div>
      </div>
      <div class="texts-order-numer">订单编号：{{hotItem.orderNum || ''}}</div>
      <div class="texts-center-wrapper">
        <div class="texts-center-left">
          <div class="texts-one-line">
            <span>购买金额：</span>
            <span class="text-value">{{hotItem.cPrice || '0'}}元</span>
          </div>
          <div class="texts-one-line">
            <span>下单时间：</span>
            <span class="text-value">{{timeShow || ''}}</span>
          </div>
        </div>
        <div class="texts-center-right">
          <span class="right-money">{{hotItem.earn || '0'}}</span>
          <span class="right-text">赚取金额（元）</span>
        </div>
      </div>
      <div class="order-notice-bottom-btn">
        <button-xbd :disabled="false" @click="onClickTopRight">{{rightBtnText}}</button-xbd>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import { CountDown } from 'vant'
import ButtonXbd from '@components/button-xbd'
Vue.use(CountDown)

export default {
  name: 'item-one-order',
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
  computed: {
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
        return this.$timeCompareWithBefore(this.hotItem.waitPayFinishTime, this.nowTimeStamp, true) * 1000
      }
      return 1000 * 1000
    },
    timeShow () {
      return this.$timeCompareWithNow(this.hotItem.cOrderCreateTimeStamp, this.nowTimeStamp)
    },
    rightBtnText () {
      if (this.hotItem.status == 8 && this.hotItem.bAppealId) {
        return '问题已解决'
      }
      return '我已收款'
    }
  },
  methods: {
    onClickTopRight () {
      this.$emit('itemClick')
    }
  },
  destroyed () {
    // this.timeout && clearTimeout(this.timeout)
  }
}
</script>

<style scoped lang="less">
  .undone-order-item {
    padding: 0 20px 20px;
    margin-bottom: 14px;
    background-color: #ffffff;
    .undone-order-top {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 10px;
      height: 38px;
      line-height: 38px;
      font-weight: 400;
      font-size: @font-size-lg;
      .undone-order-top-right {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        text-align: right;
        color: #E93F3F;
        .van-count-down {
          width: 20vw;
          height: 32px;
          line-height: 32px;
          text-align: right;
          font-weight: 400;
          color: #E93F3F;
          font-size: @font-size-lg;
        }
      }
    }
  }
  .order-notice-bottom-btn {
    /*position: absolute;*/
    /*bottom: 3.6vw;*/
    /*left: 6vw;*/
    /*right: 6vw*/
  }
  .texts-order-numer {
    height: 32px;
    line-height: 32px;
    font-size: @font-size-md;
  }
  .texts-center-wrapper {
    padding-bottom: 14px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    .texts-center-left {
      flex: 1;
      .texts-one-line {
        font-size: @font-size-md;
        color: @color999;
        height: 24px;
        line-height: 24px;
        .text-value {
          color: @color333;
        }
      }
    }
    .texts-center-right {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      .right-money {
        font-size: @font-size-llllg;
        color: @xbd-theme-color;
        margin-bottom: 10px;
        font-weight: 500;
      }
      .right-text {
        font-size: @font-size-md;
        color: @color999
      }
    }
  }
</style>
