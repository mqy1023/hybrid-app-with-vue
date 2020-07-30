<template>
  <div class="dialog">
    <div class="dialog-cover back" v-if="isShow" @click="closeMyself">
      <div class="dialog-content" v-if="isShow">
        <div class="dialog_head back ">
          <div class="header">取消交易</div>
          <div class="reasons">请确认是否关闭这笔交易，请填写关闭原因</div>
        </div>
        <div class="dialog_main">
          <van-dropdown-menu :overlay="false" style="flex: 1">
            <van-dropdown-item :overlay="false" v-model="value" :options="option"
                               style="left: 7.5vw;right: 7.5vw"/>
          </van-dropdown-menu>
        </div>
        <textarea v-model="reason" :class="value == 2 ? 'text-area' : 'no-display'" maxlength="200" placeholder="请输入取消交易的原因"/>
        <div class="fake-btns-height"></div>
        <div class="btns-bottom">
          <button class="van-button van-button--default van-button--large van-dialog__cancel"
                  style="color: rgb(153, 153, 153); border-right: none;" @click="onCancelTrade">
            <span class="van-button__text">确认关闭</span>
          </button>
          <button @click="onConfirmTrade"
            class="van-button van-button--default van-button--large van-dialog__confirm">
            <span class="van-button__text">继续支付</span></button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import { DropdownMenu, DropdownItem } from 'vant'

Vue.use(DropdownMenu).use(DropdownItem)

export default {
  name: 'popup-xbd',
  data () {
    return {
      value: 0,
      option: [
        { text: '支付超时，重新购买', value: 0 },
        { text: '我不想买了', value: 1 },
        { text: '其他', value: 2 }
      ],
      reason: ''
    }
  },
  watch: {
    value (val) {
      if (this.reason) {
        this.reason = '' // 重置填写的原因
      }
    }
  },
  props: {
    isShow: {
      type: Boolean,
      default: false,
      required: true
    }
  },
  methods: {
    closeMyself () {
      this.$emit('on-close')
    },
    onCancelTrade () {
      let reasonBack = ''
      if (this.value === 2 && this.reason.trim() === '') {
        this.$toast('请输入取消交易原因')
        return
      } else if (this.value === 2) {
        reasonBack = this.reason
      } else if (this.value === 1) {
        reasonBack = '我不想买了'
      } else {
        reasonBack = '支付超时，重新购买'
      }
      this.$emit('cancelTradeClick', reasonBack)
    },
    onConfirmTrade () {
      this.reason = ''
      this.$emit('confirmTradeClick')
    }
  }
}
</script>
<style lang="less" scoped>
  // 最外层 设置position定位
  .dialog {
    position: relative;
    font-size: 16px;
  }
  .no-display {
    display: none;
  }

  .dialog-cover {
    background: rgba(0, 0, 0, 0.7);
    position: fixed;
    z-index: 200;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  // 内容层 z-index要比遮罩大，否则会被遮盖，
  .dialog-content {
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-items: center;
    background-color: #fff;
    width: 85%;
    min-height: 64vw;
    border-radius: 5px;
    z-index: 30000;
    .dialog_head {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      padding: 20px 20px 0;
      .header {
        font-size: @font-size-lllg;
        color: @color222;
        margin-bottom: 10px;
      }
      .reasons {
        font-size: @font-size-md;
        color: @color666;
      }
    }
    .dialog_main {
      display: flex;
      justify-content: center;
      align-content: center;
      width: 100%;
      box-sizing: border-box;
    }
    .text-area {
      width: 100%;
      border: none;
      outline:none;
      resize:none;
      padding: 8px 20px;
      box-sizing: border-box;
      font-size: @font-size-md;
      flex: 1
    }
    .fake-btns-height {
      height: 50px
    }
    .btns-bottom {
      display: flex;
      position: absolute;
      bottom: 0;
      width: 100%;
      .van-dialog__cancel {
        border-bottom-left-radius: 5px;
      }
      .van-dialog__confirm {
        border-bottom-right-radius: 5px;
      }
    }
  }
  .van-dropdown-item {
    left: 7.5vw;
    right: 7.5vw;
  }
  .van-dropdown-item--down {
    width: 85vw
  }
</style>
