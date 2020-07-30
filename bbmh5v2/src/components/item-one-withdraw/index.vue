<template>
    <div class="item-buy-record">
      <div class="left-text-wrapper">
        <div style="font-weight: bold">提现金额：{{msgItem.amount}}元</div>
        <div>订单编号：{{msgItem.orderNum}}</div>
        <div>状态：<span :class="statusText">{{statusShow}}</span></div>
        <div>提现时间：{{msgItem.createTime}}</div>
      </div>
      <div class="right-btns-wrapper" v-show="msgItem.status==0">
        <div class="right-btn right-btn1" @click="onClickWithDraw">转账</div>
      </div>
    </div>
</template>

<script>
import Vue from 'vue'
import VueClipboard from 'vue-clipboard2'
Vue.use(VueClipboard)
export default {
  name: 'item-one-withdraw',
  props: {
    msgItem: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
    statusText () {
      return 'span' + this.msgItem.status
    },
    statusShow () {
      if (this.msgItem.status == 0) {
        return '转金币进行中'
      } else if (this.msgItem.status == 1) {
        return '已转至总金币账户'
      }
      return '转金币失败'
    }
  },
  methods: {
    onClickWithDraw () {
      this.$emit('clickWithdraw')
    }
  }
}
</script>

<style scoped lang="less">
.item-buy-record {
  display: flex;
  width: 92vw;
  box-sizing: border-box;
  background-color: #ffffff;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 10px;
  margin: 2vw 4vw;
  font-size: @font-size-md;
  border-radius: 12px;
  .left-text-wrapper {
    flex: 1;
    >div {
      height: 24px;
      line-height: 24px;
    }
  }
  .right-btns-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .right-btn {
    padding: 6px 18px;
    border-radius: 14px;
    font-size: @font-size-sm;
  }
  .right-btn1 {
    background-color: #ffffff;
    border: 1px solid #3761FF;
    color: #3761FF;
  }
  .span1 {
    color: @xbd-theme-color;
  }
  .span2 {
    color: @xbd-red-color;
  }
}
</style>
