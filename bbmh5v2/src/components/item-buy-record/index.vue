<template>
    <div class="item-buy-record">
      <div class="item-record-top">
        <div class="item-record-texts">
          <div>
            <span>交易商品：</span><span class="title-text">{{unitValue}}金币</span>
            <span class="status-text" v-show="status">{{status}}</span>
          </div>
          <div>交易金额：<span class="title-text">{{msgItem.orderAllValue}}元</span></div>
          <div>交易数量：{{msgItem.orderCount}}个</div>
        </div>
        <div style="display: flex;flex-direction: column;align-items: flex-end">
          <div v-show="rightTopText" :class="rightTextStatus">{{rightTopText}}</div>
          <div class="right-btn" v-show="rightBtnText" @click="onClickRight">{{rightBtnText}}</div>
        </div>
      </div>
      <div class="item-record-bottom van-hairline--top">
        <div class="order-number-show" style="margin-top: 6px">
          <span>订单编号：</span>
          <span>{{msgItem.orderNumber}}</span>
          <input type="text" disabled v-model="msgItem.orderNumber" style="display: none" >
          <span class="right-btn right-btn-cpoy" @click="onClickCopy" style="margin-left: 3vw;">复制</span>
        </div>
        <div class="">
          <span>创建时间：</span>
          <span>{{msgItem.orderCreateTime}}</span>
        </div>
      </div>
    </div>
</template>

<script>
import Vue from 'vue'
import VueClipboard from 'vue-clipboard2'
Vue.use(VueClipboard)
export default {
  name: 'item-buy-record',
  props: {
    msgItem: {
      type: Object,
      default: () => ({})
    },
    buyType: {
      type: Number
    }
  },
  computed: {
    status () {
      if (this.msgItem.orderAppealStatus == 1 && this.msgItem.orderStatus != 2 && this.msgItem.orderStatus != 4 && this.msgItem.orderStatus != 5) {
        return '申诉中'
      } else if (this.msgItem.orderAppealStatus == 2) {
        return '已撤销申诉'
      }
      return ''
    },
    unitValue () {
      let orderUnitValue = this.msgItem.orderUnitValue
      if (orderUnitValue.indexOf('.') > 0) {
        return orderUnitValue.substring(0, orderUnitValue.indexOf('.'))
      }
      return orderUnitValue
    },
    rightTopText () {
      if (this.msgItem.orderStatus == 2) {
        return '等待商家确认收款'
      } else if (this.msgItem.orderStatus == 4 && this.buyType != 5) {
        if (this.msgItem.alipayStatus == 0) {
          return '支付宝信息待审核'
        } else if (this.msgItem.alipayStatus == 2) {
          return '支付宝信息审核不通过'
        } else if (this.msgItem.alipayStatus == 3) {
          return '支付宝信息未填写'
        } else if (this.msgItem.alipayStatus == 5) {
          return '支付宝信息未完善'
        } else {
          return '交易成功'
        }
      } else if (this.msgItem.orderStatus == 5 || this.msgItem.orderStatus == 4) {
        return '交易成功'
      } else if (this.msgItem.orderStatus == 6) {
        return '交易失败'
      }
      return ''
    },
    rightTextStatus () {
      if (this.msgItem.orderStatus == 2) {
        return 'right-text right-text-fail'
      } else if (this.msgItem.orderStatus == 4 && this.buyType != 5) {
        if (this.msgItem.alipayStatus == 0 || this.msgItem.alipayStatus == 2 || this.msgItem.alipayStatus == 3 || this.msgItem.alipayStatus == 5) {
          return 'right-text right-text-fail'
        } else {
          return 'right-text right-text-succ'
        }
      } else if (this.msgItem.orderStatus == 5 || this.msgItem.orderStatus == 4) {
        return 'right-text right-text-succ'
      } else if (this.msgItem.orderStatus == 6) {
        return 'right-text right-text-fail'
      }
      return ''
    },
    rightBtnText () {
      if (this.msgItem.orderStatus == 1) {
        return '去支付'
      } else if (this.msgItem.orderStatus == 3 && (!this.msgItem.orderAppealStatus || this.msgItem.orderAppealStatus == 0)) { // 超时+未申诉，显示"已支付，去申诉?"
        return '已支付,去申诉?'
      } else if (this.msgItem.orderStatus == 3 && this.msgItem.orderAppealStatus == 1) { // 超时+已申诉，显示"撤销申诉"
        return '撤销申诉'
      } else if (this.msgItem.orderStatus == 4 && this.buyType != 5) {
        if (this.msgItem.alipayStatus == 2) {
          return '去修改'
        } else if (this.msgItem.alipayStatus == 3) {
          return '去填写'
        } else if (this.msgItem.alipayStatus == 5) {
          return '去完善'
        } else if (this.msgItem.alipayStatus == 4) {
          return '在线转卖'
        }
      } else if (this.msgItem.orderStatus == 7 && (!this.msgItem.orderAppealStatus || this.msgItem.orderAppealStatus == 0)) {
        return '已支付,去申诉?'
      } else if (this.msgItem.orderStatus == 7 && this.msgItem.orderAppealStatus == 1) {
        return '撤销申诉'
      }
      return ''
    }
  },
  methods: {
    onClickRight () {
      this.$emit('clickRightBtn')
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
.item-buy-record {
  display: flex;
  width: 92vw;
  background-color: #fff;
  flex-direction: column;
  margin: 3vw 4vw 0;
  padding: 10px;
  border-radius: 8px;
  box-sizing: border-box;
  .item-record-top {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    .item-record-texts {
      flex: 1;
      height: 16vw;
      color: @color666;
      display: flex;
      flex-direction: column;
      justify-content: space-around;
      font-size: @font-size-md;
      align-items: flex-start;
      .status-text {
        color: #fff;
        padding: 1px 4px;
        font-size: @font-size-xs;
        background-color: #E93F3F;
        border-top-left-radius: 12px;
        border-top-right-radius: 12px;
        border-bottom-right-radius: 12px;
      }
    }
    .right-text {
      margin: 6px 0
    }
    .right-text-succ {
      color: @xbd-theme-color;
    }
    .right-text-fail {
      color: @xbd-red-color;
    }
  }
  .title-text {
    color: @color333;
    font-weight: bold;
    margin-right: 6px;
  }
  .right-btn {
    background: linear-gradient(left, #5FA4FF, #4364FC);
    padding: 6px 14px;
    border-radius: 16px;
    color: #ffffff;
    font-size: @font-size-xs;
  }
  .right-btn-cpoy {
    padding: 2px 14px;
    border-radius: 14px;
    font-size: @font-size-xs;
    background: #ffffff;
    border: 1px solid #DDDDDD;
    color: #666666;
  }
  .item-record-bottom {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-start;
    margin-top: 8px;
    div {
      flex: 1;
      width: 88vw;
      height: 20px;
      line-height: 20px;
    }
  }
  .order-number-show {
    display: flex;
    flex-direction: row;
    align-items: center;
    input {
      flex: 1;
      background-color: #ffffff;
    }
  }
}
</style>
