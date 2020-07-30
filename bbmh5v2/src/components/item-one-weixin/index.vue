<template>
    <div class="item-one-alipay">
      <div class="item-one-top">
        <div class="item-top-left">
          <div class="item-one">
            <span>姓名：</span>
            <span>{{msgItem.wechatUsername}}</span>
          </div>
          <div class="item-one">
            <span>微信账号：</span>
            <span>{{msgItem.wechatAccount}}</span>
          </div>
        </div>
      </div>
      <div class="item-one-bottom van-hairline--top">
        <div class="one-btn-wrapper" @click="onClickLeft">
          查看当前账号审核状态
        </div>
        <div class="one-btn-wrapper one-btn-wrapper-right" @click="onClickRight" v-show="curWechat!=msgItem.wechatAccount">
          一键切换微信支付账号
        </div>
      </div>
    </div>
</template>

<script>
export default {
  name: 'item-one-alipay',
  props: {
    msgItem: {
      type: Object,
      default: () => ({})
    },
    curWechat: {
      type: String
    }
  },
  methods: {
    onClickLeft () {
      this.$emit('clickLeftBtn')
    },
    onClickRight () {
      this.$emit('clickRightBtn')
    }
  }
}
</script>

<style scoped lang="less">
.item-one-alipay {
  display: flex;
  width: 100vw;
  flex-direction: column;
  margin: 0 0 3vw 0;
  text-align: center;
  box-sizing: border-box;
  justify-content: space-between;
  align-items: center;
  color: @color666;
  background-color: #fff;
  font-size: @font-size-md;
  .item-one-top {
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 8px 14px;
    box-sizing: border-box;
    .item-top-left {
      text-align: left;
    }
    .item-one {
      height: 22px;
      line-height: 22px;
    }
  }
  .item-one-bottom {
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    padding: 8px 4vw;
    box-sizing: border-box;
    .one-btn-wrapper {
      background-color: white;
      color: #333333;
      font-size: @font-size-md;
      width: 43vw;
      text-align: center;
      border: 1px solid #DDDDDD;
      height: 36px;
      line-height: 36px;
      border-radius: 16px;
    }
    .one-btn-wrapper-right {
      background: linear-gradient(left, #5FA4FF, #4364FC);
      color: white;
      margin-left: 6vw;
      border: none;
    }
  }
}
</style>
