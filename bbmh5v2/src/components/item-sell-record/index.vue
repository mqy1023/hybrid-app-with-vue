<template>
    <div class="item-sell-record" @click="click2Item">
      <div class="item-record-top">
        <div class="item-record-texts" style="">
          <div style="font-weight: bold;font-size: 14px">转卖金额：{{msgItem.tradeAmount || msgItem.businessProductResellPrice}}元</div>
          <div v-show="msgItem.orderPayType">支付方式：{{msgItem.orderPayType==1?'支付宝':'微信'}}</div>
          <div v-show="msgItem.orderNumber" class="order-number-show">
            订单编号：{{msgItem.orderNumber}}
            <input type="text" disabled v-model="msgItem.orderNumber" style="display: none">
            <span class="right-btn right-btn-cpoy" @click="onClickCopy" style="margin-left: 1vw;">复制</span>
          </div>
          <div v-show="sellType!=1">抢单时间：{{msgItem.orderCreateTime}}</div>
        </div>
        <div class="right-btn" @click="click2See" v-show="msgItem.orderStatus==2">查看</div>
        <div class="right-text1" v-show="msgItem.orderStatus == 5&&sellType!=1">交易成功</div>
        <div class="right-text2" v-show="typeShow">{{typeShow}}</div>
      </div>
      <div class="item-record-bottom van-hairline--top" v-if="msgItem.orderRemark">
        <div>
          <span>备注：</span>
          <span class="text-mark">{{msgItem.orderRemark || ''}}</span>
        </div>
      </div>
    </div>
</template>

<script>
import Vue from 'vue'
import VueClipboard from 'vue-clipboard2'
Vue.use(VueClipboard)
export default {
  name: 'item-sell-record',
  props: {
    msgItem: {
      type: Object,
      default: () => ({})
    },
    sellType: {
      type: Number
    }
  },
  /* eslint-disable */
  computed: {
    typeShow () {
      const orderStatus = { '6': '订单取消', '8': '申诉中', '12': '订单超时' }
      const oStatus = this.msgItem.orderStatus
      if (oStatus == 6 || oStatus == 8 || oStatus == 12) {
        return orderStatus[oStatus]
      }
      return ''
    }
  },
  methods: {
    click2See () {
      this.$emit('click2See')
    },
    click2Item () {
      const oStatus = this.msgItem.orderStatus
      if (oStatus == 8) {
        this.$emit('click2Item')
      }
    },
    onClickCopy () {
      this.$copyText(this.msgItem.orderNumber).then((e) => {
        this.$toast('复制成功')
      }, (e) => {
        this.$toast('复制失败')
      })
    }
  }
}
</script>

<style scoped lang="less">
.item-sell-record {
  display: flex;
  width: 96vw;
  background-color: #fff;
  flex-direction: column;
  margin: 3vw 2vw 0;
  padding: 8px;
  border-radius: 8px;
  box-sizing: border-box;
  font-size: @font-size-md;
  .item-record-top {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    .item-record-texts {
      flex: 1;
      color: @color666;
      display: flex;
      flex-direction: column;
      justify-content: space-around;
      align-items: flex-start;
      > div {
        height: 20px;
        line-height: 20px;
      }
      .title-text {
        color: @color333;
        font-size: @font-size-lg;
        font-weight: 400;
        margin-right: 6px;
      }
    }
    .right-text1 {
      color: @xbd-theme-color;
    }
    .right-text2 {
      color: @xbd-red-color
    }
  }
  .right-btn {
    background: linear-gradient(left, #5FA4FF, #4364FC);
    padding: 6px 10px;
    border-radius: 16px;
    color: #ffffff;
    font-size: @font-size-sm;
  }
  .right-btn-cpoy {
    background: #ffffff;
    border: 1px solid #DDDDDD;
    color: #333333;
    padding: 2px 8px;
    border-radius: 10px;
    font-size: @font-size-xs;
  }
  .order-number-show {
    display: flex;
    flex-direction: row;
    align-items: center;
    width: 100%;
    input {
      flex: 1;
      background-color: #ffffff;
    }
  }
  .item-record-bottom {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-items: flex-start;
    padding-top: 8px;
    margin-top: 4px;
    .text-mark {
      color: #E93F3F
    }
  }
}
</style>
