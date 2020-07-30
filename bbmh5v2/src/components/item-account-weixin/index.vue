<template>
    <div class="item-one-alipay">
      <div class="item-one-top">
        <div class="item-top-left">
          <div class="one-line">
            <span>姓名：</span>
            <span>{{msgItem.wechatUsername}}</span>
          </div>
          <div class="one-line">
            <span>收款金额：</span>
            <span>{{msgItem.wechatAmount}}</span>
          </div>
          <div class="one-line">
            <span>备注码：</span>
            <span>{{msgItem.wechatMemoCode}}</span>
          </div>
          <div style="margin: 2vw 0;display: flex;justify-content: flex-start;align-items: center;flex-direction: row">
            <span>微信支付二维码：</span>
            <img :src="msgItem.wechatQrCode" class="img-weixin" @click="onClickImg"/>
          </div>
          <div class="one-line">
            <span>审核状态：</span>
            <span :style="rightStatusStyle">{{status}}</span>
          </div>
          <div class="flex-center" v-show="msgItem.checkContent" style="justify-content: flex-start;margin-top: 1vw">
            <div style="min-width: 12vw">
              <span>备注：</span>
            </div>
            <div style="display: inline-block">
              <span>{{msgItem.checkContent}}</span>
            </div>
          </div>
        </div>
      </div>
      <van-image-preview
        v-model="show"
        :showIndex="false"
        :images="images"
      >
      </van-image-preview>
    </div>
</template>

<script>
import Vue from 'vue'
import { ImagePreview } from 'vant'
Vue.use(ImagePreview)

export default {
  name: 'item-one-alipay',
  props: {
    msgItem: {
      type: Object,
      default: () => ({})
    }
  },
  data () {
    return {
      show: false
    }
  },
  computed: {
    status () {
      if (this.msgItem.wechatStatus == 2) {
        return '待审核'
      } else if (this.msgItem.wechatStatus == 1) {
        return '审核通过'
      }
      return '审核失败'
    },
    rightStatusStyle () {
      if (this.msgItem.wechatStatus == 1) {
        return 'color: #4364FC'
      } else if (this.msgItem.wechatStatus == 3) {
        return 'color: #E93F3F'
      }
      return 'color: #999999'
    },
    images () {
      if (this.msgItem.wechatQrCode) {
        return [].concat(this.msgItem.wechatQrCode)
      }
      return []
    }
  },
  methods: {
    onClickImg () {
      this.show = true
    }
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
  .one-line {
    height: 22px;
    line-height: 22px;
  }
  .img-weixin {
    width: 22vw;
    height: 32vw
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
