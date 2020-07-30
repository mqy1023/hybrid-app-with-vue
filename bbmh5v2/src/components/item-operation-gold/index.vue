<template>
    <div class="item-operation-gold">
      <div>盈利金额转金币：{{msgItem.amount}}元</div>
      <div>状态：<span :class="msgItem.status==0?'span0':'span1'" v-show="msgItem.status!=2">{{statusShow}}</span></div>
      <div v-show="msgItem.status==2">状态：<span class="span2">{{statusShow}}</span></div>
      <div v-if="msgItem.orderNum" class="order-num-wrapper">
        <div>订单编号：{{msgItem.orderNum}}</div>
        <input v-model="msgItem.orderNum" style="display: none"/>
        <div @click="onClickCopy" class="copy-btn">复制</div>
      </div>
      <div style="margin-bottom: 10px">转金币时间：<span>{{msgItem.createTime}}</span></div>
      <div class="van-hairline--top over-text-show" v-show="msgItem.remark" style="max-width: 88vw;padding: 4px 0">备注：<span style="color:#E93F3F">{{msgItem.remark}}</span></div>
    </div>
</template>

<script>
import Vue from 'vue'
import VueClipboard from 'vue-clipboard2'
Vue.use(VueClipboard)
export default {
  name: 'item-operation-gold',
  props: {
    msgItem: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
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
    onClickCopy () {
      this.$copyText(this.msgItem.orderNum).then((e) => {
        this.$toast('复制成功')
      }, (e) => {
        this.$toast('复制失败')
      })
    }
  }
}
</script>

<style scoped lang="less">
.item-operation-gold {
  display: flex;
  width: 92vw;
  box-sizing: border-box;
  background-color: #ffffff;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  padding: 10px 10px 0;
  margin: 2vw 4vw;
  font-size: @font-size-md;
  border-radius: 12px;
  >div {
    height: 24px;
    line-height: 24px;
  }
  .order-num-wrapper {
    display: flex;
    justify-content: flex-start;
    flex-direction: row;
    align-items: center;
  }
  .copy-btn {
    background: linear-gradient(left, #5FA4FF, #4364FC);
    padding: 1px 10px;
    border-radius: 12px;
    color: #ffffff;
    font-size: @font-size-xs;
    margin-left: 4vw;
  }
  .span1 {
    color: @xbd-theme-color;
  }
  .span2 {
    color: @xbd-red-color;
  }
}
</style>
