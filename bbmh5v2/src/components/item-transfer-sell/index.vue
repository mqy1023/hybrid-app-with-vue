<template>
    <div class="item-buy-record">
      <div class="phone">{{msgItem.mobile || ''}}</div>
      <div class="right-btns-wrapper" >
        <div style="position: relative" v-if="msgItem.orderWaitDealCount>0">
          <div class="right-btn right-btn1" @click="onClickWithDraw" >好友提现转账</div>
          <div class="tip-circle">{{msgItem.orderWaitDealCount}}</div>
        </div>
        <div class="right-btn right-btn2" @click="onClickRight">转账</div>
      </div>
    </div>
</template>

<script>
import Vue from 'vue'
import VueClipboard from 'vue-clipboard2'
Vue.use(VueClipboard)
export default {
  name: 'item-transfer-gold',
  props: {
    msgItem: {
      type: Object,
      default: () => ({})
    }
  },
  methods: {
    onClickWithDraw () {
      this.$emit('clickWithdraw')
    },
    onClickRight () {
      this.$emit('clickTransfer')
    }
  }
}
</script>

<style scoped lang="less">
.item-buy-record {
  display: flex;
  padding: 4vw 4vw 2vw;
  box-sizing: border-box;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  .phone {
    color: #666666;
    font-size: @font-size-lg;
  }
  .right-btns-wrapper {
    display: flex;
    justify-content: flex-end;
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
    margin-right: 10px;
    padding: 6px
  }
  .right-btn2 {
    padding: 7px 19px;
    background: linear-gradient(left, #5FA4FF, #4364FC);
    color: #ffffff;
  }
  .tip-circle {
    background-color: @xbd-red-color;
    color: #ffffff;
    position: absolute;
    right: 7px;
    top: -10px;
    width: 18px;
    height: 18px;
    line-height: 18px;
    font-size: @font-size-sm;
    text-align: center;
    border-radius: 9px;
  }
}
</style>
