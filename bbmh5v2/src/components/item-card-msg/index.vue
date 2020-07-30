<template>
    <div class="item-card-msg clearfix" @click="onClickMsgItem">
      <div class="item-msg-top" style="box-sizing: border-box">
        <div class="item-type over-text-show">{{msgItem.title}}
        </div>
        <div class="circle-wrapper">
          <div class="red-circle" v-if="isReadShow" />
        </div>
      </div>
      <div class="item-time over-text-show" style="margin: 0 4vw;text-align: left">{{msgItem.createTime || ''}}</div>
      <div class="item-msg-center padding-common">
        <div style="margin-bottom: 6px">{{msgItem.content || ''}}</div>
        <div class="imgs-wrapper">
          <img v-lazy="item" class="img-box" v-for="(item, index) in imgsSmall" :key="index+item" @click="showImg(index,$event)" />
        </div>
      </div>
      <div class="item-msg-bottom center-horizontal van-hairline--top" @click="onClickMsgDetail($event)" v-if="msgItem.jumpUrl&&msgItem.openType!=1">
        <span>查看详情</span>
        <van-icon name="arrow" />
      </div>
    </div>
</template>

<script>
import Vue from 'vue'
import { ImagePreview } from 'vant'
Vue.use(ImagePreview)
export default {
  name: 'item-card-msg',
  props: {
    msgItem: {
      type: Object,
      default: () => ({})
    }
  },
  /* eslint-disable */
  computed: {
    isReadShow () {
      return this.msgItem.isRead == 0
      // return true
    },
    imgsAll () {
      if (this.msgItem.images) {
        return this.msgItem.images.split(',')
      }
      return []
    },
    imgsSmall () {
      if (this.imgsAll.length > 0) {
        return this.imgsAll.slice(0, 3)
      }
      return ''
    }
  },
  methods: {
    onClickMsgItem () {
      this.$emit('clickMsgItem')
    },
    onClickMsgDetail (e) {
      e.preventDefault()
      e.stopPropagation()
      this.$emit('clickMsgDetail')
    },
    showImg (i, e) {
      e.preventDefault()
      e.stopPropagation()
      this.instance = ImagePreview({
        images: this.imgsAll,
        startPosition: i
      })
    }
  },
  destroyed () {
    this.instance && this.instance.close()
  }
}
</script>

<style scoped lang="less">
.item-card-msg {
  display: flex;
  width: 100%;
  background-color: #fff;
  flex-direction: column;
  margin-top: 2vw;
  .item-msg-top {
    height: 30px;
    line-height: 30px;
    margin: 0 4vw;
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center
  }
  .center-horizontal {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center
  }
  .item-msg-bottom {
    margin: 0 4vw;
    padding: 10px 0;
    font-size: @font-size-md;
    color: @color999;
  }
  .item-type {
    font-size: 16px;
    font-weight: bold;
    position: relative;
  }
  .circle-wrapper {
    width: 10px;
    height: 10px;
    position: relative;
    .red-circle {
      display: block;
      position: absolute;
      right: 0px;
      top: -4px;
      width: 6px;
      height: 6px;
      border-radius: 4px;
      background-color: @xbd-red-color;
    }
  }

  .item-time {
    color: @color666;
    font-size: @font-size-md;
  }
  .item-msg-center {
    flex: 1;
    text-align: left;
    line-height: 20px;
    color: @color666;
    font-size: @font-size-md;
  }
  .padding-common {
    padding: 2vw 4vw
  }
  .imgs-wrapper {
    .img-box {
      width: 29vw;
      height: 29vw;
      margin-right: 2vw;
    }
    .img-box:last-child {
      margin-right: 0;
    }
  }
}
</style>
