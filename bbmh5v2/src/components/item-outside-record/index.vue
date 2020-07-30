<template>
    <div class="item-outside-record">
      <div class="item-record-top">
        <div class="item-record-texts" style="max-width: 72vw">
          <div>
            <span class="title-text">{{msgItem.price}}元</span>
            <span class="status-text" v-show="status">{{status}}</span>
          </div>
          <div>时间：{{msgItem.createTime}}</div>
          <div class="over-text-show" style="width: 100%">订单编号：{{msgItem.cOrderNo}}</div>
        </div>
        <div class="right-btn" v-show="isShowRightBtn" @click="onClickRightBtn">{{rightBtnText}}</div>
        <div v-show="msgItem.status==5" class="right-text-succ">交易成功</div>
        <div v-show="msgItem.status==6||msgItem.status==11" class="right-text-fail">交易失败</div>
        <div class="right-time-tick" v-show="msgItem.status==3||msgItem.status==9">
          <van-count-down :time="time" @finish="finshTick" :style="isEndType" />
          <div>卖家确认收款剩余时间</div>
          <div class="right-btn" @click="onClickRightBtn1">取消交易</div>
        </div>
      </div>
      <div class="btn-wrap" v-show="isShowComplBtn">
        <button-xbd :disabled="false" @click="onClickComplaint" class="btn-save">去申诉</button-xbd>
      </div>
      <div class="item-record-bottom van-hairline--top" v-show="msgItem.remark">
        <div class="over-text-show">
          <span>备注：</span>
          <span class="remark-text">{{msgItem.remark}}</span>
        </div>
      </div>
    </div>
</template>

<script>
import Vue from 'vue'
import { CountDown } from 'vant'
import ButtonXbd from '@components/button-xbd'
Vue.use(CountDown)
/* eslint-disable */
export default {
  name: 'item-outside-record',
  components: {
    ButtonXbd
  },
  props: {
    msgItem: {
      type: Object,
      default: () => ({})
    },
    nowTimeStamp: {
      type: Number
    }
  },
  data () {
    return {
      isFinishTick: false
    }
  },
  computed: {
    time () {
      // this.msgItem.userConfirmEndTimeStamp = '1569299916'
      if (this.msgItem.userConfirmEndTimeStamp && this.msgItem.status == 3) {
        return this.$timeCompareWithBefore(this.msgItem.userConfirmEndTimeStamp, this.nowTimeStamp, true) * 1000
      }
      return 0
    },
    isEndType () {
      if (this.isFinishTick) {
        return 'color: red'
      }
      return ''
    },
    status () {
      if (this.msgItem.status == 8 || this.msgItem.status == 10) {
        return '申诉中'
      }
      return ''
    },
    isShowComplBtn () {
      // return (this.msgItem.userConfirmEndTimeStamp && this.isFinishTick) || this.msgItem.status == 7
      // return (this.msgItem.userConfirmEndTimeStamp && this.isFinishTick)
      return this.msgItem.status == 9 && !this.msgItem.remark
    },
    isShowRightBtn () {
      return this.msgItem.status == 1 || (this.msgItem.status == 7 && !this.msgItem.remark) || this.msgItem.status == 8 || this.msgItem.status == 10 || this.msgItem.status == 2
    },
    rightBtnText () {
      if (this.msgItem.status == 1 || this.msgItem.status == 2) {
        return '去支付'
      } else if (this.msgItem.status == 7) {
        return '已支付？去申诉'
      } else if (this.msgItem.status == 8 || this.msgItem.status == 10) {
        return '取消交易'
      }
      return ''
    }
  },
  methods: {
    finshTick () {
      console.log('finshTick')
      this.isFinishTick = true
    },
    onClickComplaint () {
      this.$emit('clickComplaint')
    },
    onClickRightBtn () {
      this.$emit('clickRightBtn')
    },
    onClickRightBtn1 () {
      this.$emit('clickRightBtn1')
    }
  }
}
</script>

<style scoped lang="less">
.item-outside-record {
  display: flex;
  width: 92vw;
  background-color: #fff;
  flex-direction: column;
  margin: 3vw 4vw 0;
  padding: 0 10px;
  border-radius: 8px;
  box-sizing: border-box;
  justify-content: space-between;
  align-items: center;
  .item-record-top {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    width: 100%;
    align-items: center;
    .item-record-texts {
      flex: 1;
      height: 23vw;
      padding: 10px 0;
      color: @color666;
      display: flex;
      flex-direction: column;
      justify-content: space-around;
      align-items: flex-start;
      .title-text {
        color: @color333;
        font-size: @font-size-lg;
        font-weight: 400;
        margin-right: 6px;
      }
      .status-text {
        color: #fff;
        padding: 1px 8px;
        background-color: #E93F3F;
        border-top-left-radius: 14px;
        border-top-right-radius: 14px;
        border-bottom-right-radius: 14px;
      }
    }
    .right-btn {
      background: linear-gradient(left, #5FA4FF, #4364FC);
      padding: 6px 10px;
      border-radius: 16px;
      color: #ffffff;
    }
  }
  .item-record-bottom {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    div {
      flex: 1;
      width: 90vw;
      padding: 10px 0;
    }
    .remark-text {
      color: @xbd-red-color;
    }
  }
  .right-text-fail {
    color: @xbd-red-color;
  }
  .right-text-succ {
    color: @xbd-theme-color;
  }
  .right-time-tick {
    font-size: @font-size-md;
    color: @color999;
    display: flex;
    height: 22vw;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    .van-count-down {
      font-size: @font-size-lllg;
      margin-bottom: 6px;
      color: @xbd-theme-color
    }
  }
  .btn-wrap {
    width: 84vw;
    box-sizing: border-box;
    margin-bottom: 12px;
  }
}
</style>
