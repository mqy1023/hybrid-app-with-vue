<template>
    <div class="game-main-content">
      <van-nav-bar
        title="抢单攻略"
        :fixed="true"
        left-arrow
        @click-left="onClickLeft"
      />
      <div class="game-img-wrapper clear-navbar-top">
        <img v-for="(img, index) in imageList" v-lazy="img" width="100%" :key="index" class="one-img" >
        <div style="position: relative;">
          <img v-lazy="require('./imgs/rule5.png')" width="100%" class="one-img" >
          <button type="default" class="btn-h5-bottom" @click="onClickBtn">我已知晓  立即抢单</button>
        </div>
      </div>
    </div>
</template>

<script>
export default {
  name: 'Rule',
  data () {
    return {
      imageList: [
        require('./imgs/rule1.png'),
        require('./imgs/rule2.png'),
        require('./imgs/rule3.png'),
        require('./imgs/rule4.png')
      ]
    }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1) // 返回上一层
    },
    onClickBtn () {
      this.$router.push({ path: '/dispatch' })
    }
  }
}
</script>

<style scoped lang="less">
  .game-main-content {
  }
  .game-img-wrapper {
    img {
      margin: 0;
      vertical-align:top;
      display:block
    }
  }
  .btn-h5-bottom {
    position: absolute;
    bottom: 26vw;
    left: 11vw;
    width: 78vw;
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

</style>
