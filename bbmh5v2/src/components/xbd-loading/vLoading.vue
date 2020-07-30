<template>
    <div class="loading-wrapper" v-if="isShow">
      <div class="loading-center">
        <van-loading type="spinner" color="#f1f1f1" size="34px" vertical></van-loading>
      </div>
    </div>
</template>

<script>
import Vue from 'vue'
import { Loading } from 'vant'
Vue.use(Loading)
export default {
  name: 'vloading',
  props: {
    isShow: {
      type: Boolean,
      require: true,
      default: true
    }
  }
}
</script>

<style scoped lang="less">
.loading-wrapper {
  position: fixed;
  top:0;
  left:0;
  z-index:9999999;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0);
  display: flex;
  justify-content: center;
  align-items: center;
  .loading-center {
    width: 18vw;
    height: 18vw;
    border-radius: 16px;
    background-color: rgba(88,88,88,0.8);
    display: flex;
    justify-content: center;
    align-items: center;
  }
}
</style>
