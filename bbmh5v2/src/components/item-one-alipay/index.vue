<template>
    <div class="item-one-alipay">
      <div class="item-one-top">
        <div class="item-top-left">
          <div>
            <span>姓名：</span>
            <span>{{msgItem.alipayNameTo}}</span>
          </div>
          <div style="margin: 2vw 0;">
            <span>支付宝账号：</span>
            <span>{{msgItem.alipayAccountTo}}</span>
          </div>
          <div style="margin-bottom: 2vw;" v-if="msgItem.alipayAuthIdTo">
            <span>支付宝用户ID：</span>
            <span>{{msgItem.alipayAuthIdTo}}</span>
          </div>
          <div>
            <span>添加时间：</span>
            <span>{{msgItem.createTime}}</span>
          </div>
        </div>
        <div class="item-top-right" :style="rightStatusStyle">
          {{status}}
        </div>
      </div>
      <div class="item-one-bottom van-hairline--top" v-show="msgItem.checkContent">
        <div style="margin-left: 12px;width: 12vw;text-align: left">备注：</div>
        <div class="text-remark over-text-show" style="max-width: 78vw">{{msgItem.checkContent || ""}}</div>
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
    }
  },
  computed: {
    status () {
      if (this.msgItem.alipayStatus == 2) {
        return '待审核'
      } else if (this.msgItem.alipayStatus == 1) {
        return '审核通过'
      }
      return '审核失败'
    },
    rightStatusStyle () {
      if (this.msgItem.alipayStatus == 1) {
        return 'color: #4364FC'
      } else if (this.msgItem.alipayStatus == 3) {
        return 'color: #E93F3F'
      }
      return 'color: #999999'
    }
  },
  methods: {
  }
}
</script>

<style scoped lang="less">
.item-one-alipay {
  display: flex;
  width: 92vw;
  flex-direction: column;
  margin: 3vw 4vw;
  border-radius: 12px;
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
    padding: 12px;
    box-sizing: border-box;
    .item-top-left {
      text-align: left;
    }
  }
  .item-one-bottom {
    width: 100%;
    box-sizing: border-box;
    margin: 0 8px;
    height: 25px;
    line-height: 25px;
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    .text-remark {
      color: @xbd-red-color;
    }
  }
}
</style>
