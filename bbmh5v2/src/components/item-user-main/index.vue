<template>
  <div class="item-main-user" @click="itemClick">
    <img class="icon-left" :src="iconl"/>
    <div class="item-center-wrapper">
      <div class="title-text">{{name}}</div>
      <div class="title-text-tip" v-if="isShowDot&&smallTipText">{{smallTipText}}</div>
      <span class="red-circle" v-if="isShowDot&&smallTipText"></span>
    </div>
    <div class="item-right-wrapper">
      <span class="title-text1" v-show="tip" :style="tipColor">{{tip}}</span>
      <van-icon name="arrow"/>
    </div>
  </div>
</template>

<script>
export default {
  name: 'item-main-user',
  props: {
    icon: {
      type: String,
      required: true
    },
    name: {
      type: String,
      required: true
    },
    smallTipText: {
      type: String,
      required: false
    },
    tip: {
      type: String
    }
  },
  data () {
    return {
      isShowDot: this.$store.state.user.userInfo.pointStatus == 2
    }
  },
  watch: {
    '$store.state.user.userInfo.pointStatus': 'isShowTip'
  },
  computed: {
    iconl () {
      return require('./' + this.icon + '.png')
    },
    tipColor () {
      if (this.tip == '已完善') {
        return 'color: #aaa'
      }
      return ''
    }
  },
  methods: {
    itemClick () {
      this.$emit('itemClick')
    },
    isShowTip (pointStatus) {
      if (pointStatus == 2) { // 下级通道的点位未确认
        this.isShowDot = true
      } else {
        this.isShowDot = false
      }
    }
  }
}
</script>

<style scoped lang="less">
  .item-main-user {
    display: flex;
    width: 100%;
    height: 11vw;
    padding: 0 4vw;
    box-sizing: border-box;
    color: @color333;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .icon-left {
      width: 20px;
      height: 20px;
      margin-right: 8px;
    }
    .van-icon-arrow {
      font-size: 16px;
      color: #999
    }
    .title-text {
      color: @color333;
      font-size: @font-size-lg;
    }
    .title-text-tip {
      color: @xbd-red-color;
      font-size: @font-size-xs;
    }
    .title-text1 {
      font-size: @font-size-md;
      color: @xbd-theme-color;
    }
    .item-center-wrapper {
      display: flex;
      height: 100%;
      flex-direction: column;
      justify-content: space-around;
      align-items: flex-start;
      position: relative;
      .red-circle {
        position: absolute;
        right: -6px;
        top: 0;
        background: @xbd-red-color;
        width: 8px;
        height: 8px;
        border-radius: 4px;
      }
    }
    .item-right-wrapper {
      display: flex;
      flex-direction: row;
      justify-content: flex-end;
      align-items: center;
      flex: 1
    }
  }
</style>
