<template>
    <div class="item-card-income van-hairline--bottom">
      <span :class="iconClass"></span>
      <div class="item-income-center">
        <div class="item-title">{{typeNameShow}}</div>
        <div class="item-time" v-show="tipText">{{tipText}}</div>
      </div>
      <div class="item-income-right">
        <div :class="moneyClass">{{moneyShow}}</div>
        <div class="item-time">{{msgItem.createTime || ''}}</div>
      </div>
    </div>
</template>

<script>
export default {
  name: 'item-card-income',
  data () {
    return {
      typeName: ''
    }
  },
  props: {
    msgItem: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
    moneyShow () {
      if (this.msgItem.type == 11 || this.msgItem.type == 22 || this.msgItem.type == 24 || this.msgItem.type == 35) {
        return '+' + this.msgItem.money + '金币'
      } else if (this.msgItem.type == 31 || this.msgItem.type == 32 || this.msgItem.type == 33) {
        return this.msgItem.money + '金币'
      }
      return '-' + this.msgItem.money + '金币'
    },
    moneyClass () {
      if (this.msgItem.type == 11 || this.msgItem.type == 22 || this.msgItem.type == 24 || this.msgItem.type == 35) {
        return 'money-right1'
      } else if (this.msgItem.type == 31 || this.msgItem.type == 32 || this.msgItem.type == 33) {
        return 'money-right3'
      }
      return 'money-right2'
    },
    iconClass () {
      let iconName = 'icon-gold'
      if (this.msgItem.type == 41) { // 派单金币(减 —)
        iconName += '0'
      } else if (this.msgItem.type == 11 || this.msgItem.type == 22 || this.msgItem.type == 24) { // 买金币
        iconName += '1'
      } else if (this.msgItem.type == 21 || this.msgItem.type == 23) { // 卖金币
        iconName += '2'
      } else if (this.msgItem.type == 31 || this.msgItem.type == 32 || this.msgItem.type == 33) { // 盈利金额转金币
        iconName += '3'
      } else if (this.msgItem.type == 34) { // 提现金币
        iconName += '4'
      } else if (this.msgItem.type == 35) { // 提现金币
        iconName += '3'
      }
      return 'icon-left ' + iconName
    },
    typeNameShow () {
      if (this.msgItem.type == 41) { // 派单金币(减 —)
        return '派单金币'
      } else if (this.msgItem.type == 11 || this.msgItem.type == 22 || this.msgItem.type == 24) { // 买金币
        return '购买金币'
      } else if (this.msgItem.type == 21 || this.msgItem.type == 23) { // 卖金币
        return '转卖金币'
      } else if (this.msgItem.type == 31 || this.msgItem.type == 32 || this.msgItem.type == 33) { // 盈利金额转金币
        return '盈利金额提现至总金币'
      } else if (this.msgItem.type == 34) { // 提现金币
        return '提现金币'
      } else if (this.msgItem.type == 35) { // 提现金币
        return '好友提现金额至盈利金额'
      }
      return ''
    },
    tipText () {
      if (this.msgItem.payType == 1) {
        return '支付方式：' + '支付宝支付'
      } else if (this.msgItem.payType == 2) {
        return '支付方式：' + '微信支付'
      } else if (this.msgItem.flowMobile) {
        return '购买人：' + this.msgItem.flowMobile
      }
      return ''
    }
  }
}
</script>

<style scoped lang="less">
.item-card-income {
  display: flex;
  width: 94vw;
  margin-left: 3vw;
  background-color: #fff;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
  padding: 10px 0;
  .icon-left {
    width: 44px;
    height: 44px;
    display: inline-block;
    background-size: contain;
    background-repeat: no-repeat;
  }
  .icon-gold0 {
    .bg-image('icon-gold0');
  }
  .icon-gold1 {
    .bg-image('icon-gold1');
  }
  .icon-gold2 {
    .bg-image('icon-gold2');
  }
  .icon-gold3 {
    .bg-image('icon-gold3');
  }
  .icon-gold4 {
    .bg-image('icon-gold4');
  }
  .item-income-center {
    flex: 1;
    margin-left: 1vw;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: flex-start;
    height: 12vw;
    .item-title {
      color: @color333;
      font-size: @font-size-lg;
    }
  }
  .item-time {
    color: @color999;
    font-size: @font-size-sm;
  }
  .item-income-right {
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    text-align: right;
    align-items: flex-end;
    height: 12vw;
  }
  .money-right1 {
    color: #4364FC;
    font-size: 16px;
  }
  .money-right2 {
    color: #E93F3F;
    font-size: 16px;
  }
  .money-right3 {
    color: #EEBA3A;
    font-size: 16px;
  }
}
</style>
