<template>
    <div class="game-main-content">
      <van-nav-bar
        title="玩法指南"
        :fixed="true"
      />
      <div class="game-img-wrapper clear-navbar-top">
        <img v-for="(img, index) in imageList" v-lazy="img" width="100%" :key="index" class="one-img" >
        <div style="position: relative; margin-bottom: 48px">
          <img v-lazy="require('./imgs/new-guide2.png')" width="100%" class="one-img one-img1" >
          <button type="default" :class="isIOS?'btn-h5-bottom btn-h5-bottom1':'btn-h5-bottom'" @click="onClickRules">我知道了</button>
        </div>
      </div>
      <div style="height:1px" v-show="isIOS"></div>
    </div>
</template>

<script>
export default {
  name: 'Game',
  data () {
    return {
      imageList: [
        require('./imgs/new-guide1.png')
      ],
      isIOS: this.$isIos()
    }
  },
  methods: {
    onClickRules () {
      this.$router.push({ path: '/dispatch' })
    }
  }
}
</script>

<style scoped lang="less">
  .game-main-content {
    background-color: #ffffff;
  }
  .game-img-wrapper {
    img {
      margin: 0;
      vertical-align:top;
      display:block
    }
    .one-img1 {
      margin-bottom: 46px;
    }
  }
  .btn-h5-bottom {
    position: absolute;
    bottom: 14vw;
    left: 6vw;
    width: 88vw;
    height: 16vw;
    background-image: url("./imgs/bg-new-btn.png");
    background-size: 100% 100%;
    color: #1A0B87;
    background-color: transparent;
    font-size: @font-size-lllg;
    border: none;
    outline: none;
    font-weight: 400;
  }
  .btn-h5-bottom1 {
    bottom: 16vw;
  }

</style>
