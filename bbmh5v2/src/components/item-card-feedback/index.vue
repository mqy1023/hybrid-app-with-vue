<template>
    <div class="item-card-msg clearfix">
      <div class="item-msg-top padding-common center-horizontal">
        <span class="item-type">{{itemType}}</span>
        <span class="item-time">{{itemTime || ''}}</span>
      </div>
      <div class="item-msg-center van-hairline--top van-hairline--bottom">
        <div>{{msgItem.description || ''}}</div>
        <div class="imgs-wrapper">
          <div class="img-box" v-for="(item, index) in imgList" :key="index">
            <img :src="item" alt="tp" @click="showImg(index)">
          </div>
        </div>
      </div>
      <div class="item-msg-bottom ">
        <span>{{replyText}}</span>
        <span></span>
      </div>
    </div>
</template>

<script>
import Vue from 'vue'
import { ImagePreview } from 'vant'
Vue.use(ImagePreview)
export default {
  name: 'item-card-feedback',
  props: {
    msgItem: {
      type: Object,
      default: () => ({})
    }
    // imgList: {
    //   type: Array,
    //   default: () => (['https://img.yzcdn.cn/2.jpg', 'https://img.yzcdn.cn/2.jpg',
    //     'https://img.yzcdn.cn/2.jpg'])
    // }
  },
  computed: {
    itemType () {
      let mapTypes = { 'help': '帮助', 'bug': 'bug', 'suggestion': '建议' }
      let mapVal = mapTypes[this.msgItem.category]
      return '反馈类型：' + (mapVal || 'bug')
    },
    itemTime () {
      return '反馈时间：' + this.msgItem.createTime
    },
    imgList () {
      if (!this.msgItem.images || this.msgItem.images === '') {
        return []
      }
      return this.msgItem.images.split(',')
    },
    replyText () {
      if (this.msgItem.replyStatus == 0) {
        return '暂无回复内容，耐心等待~'
      }
      return '客服反馈：' + this.msgItem.reply
    }
  },
  methods: {
    showImg (i) {
      this.instance = ImagePreview({
        images: this.imgList,
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
  min-height: 16vh;
  background-color: #fff;
  flex-direction: column;
  margin-top: 2vw;
  .center-horizontal {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center
  }
  .item-msg-bottom {
    margin: 4vw;
    font-size: @font-size-md;
    color: @color999;
  }
  .item-type {
    font-size: @font-size-lg;
    color: @color333;
  }
  .item-time {
    color: @color999;
    font-size: @font-size-md;
  }
  .item-msg-center {
    flex: 1;
    text-align: left;
    line-height: 20px;
    margin: 0 4vw;
    color: @color666;
    font-size: @font-size-md;
    padding: 10px 0;
  }
  .padding-common {
    padding: 4vw
  }
  .imgs-wrapper {
    display: flex;
    margin-top: 8px;
    .img-box {
      margin-right: 4vw;
      img {
        width: 28vw;
        height: 28vw;
      }
    }
    .img-box:last-child {
      margin-right: 0;
    }
  }
}
</style>
