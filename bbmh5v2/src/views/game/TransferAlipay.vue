<template>
    <div class="game-main-content">
      <van-nav-bar
        title="转账教程"
        :fixed="true"
        left-arrow
        @click-left="onClickLeft"
      />
      <div class="game-img-wrapper clear-navbar-top">
        <img v-for="(img, index) in imageList" v-lazy="img" width="100%" :key="index" class="one-img" >
      </div>
    </div>
</template>

<script>
export default {
  name: 'TransferAlipay',
  data () {
    return {
      imageList: [
        require('./imgs/alipay1.png'),
        require('./imgs/alipay2.png'),
        require('./imgs/alipay3.png'),
        require('./imgs/alipay4.png'),
        require('./imgs/alipay5.png'),
        require('./imgs/alipay6.png')
      ]
    }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1) // 返回上一层
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

</style>
