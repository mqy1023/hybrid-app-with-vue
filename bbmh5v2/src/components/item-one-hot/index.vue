<template>
    <div class="item-one-hot" @click="itemClick">
        <div class="item-name over-text-show">{{item.value}}金币</div>
        <div class="item-price">
          <span>售价</span>
          <span>{{item.value || ''}}</span>
          <span>元</span>
        </div>
    </div>
</template>

<script>
export default {
  name: 'item-one-hot',
  props: {
    item: {
      type: Object,
      default: () => ({})
    }
  },
  methods: {
    itemClick () {
      this.$emit('itemClick')
    }
  }
}
</script>

<style scoped lang="less">
.item-one-hot {
  display: flex;
  width: 30vw;
  height: 17vw;
  flex-direction: column;
  float: left;
  margin: 0 1vw 3vw;
  border: 1px solid #ddd;
  border-radius: 6px;
  text-align: center;
  box-sizing: border-box;
  justify-content: space-around;
  align-items: center;
  color: @color999;
  padding: 8px 0;
  div {
    line-height: 20px;
  }
  .item-name {
    font-size: @font-size-lg;
    width: 94%;
  }
}
.item-one-hot.choose-active {
  color: @xbd-theme-color;
  border: 1px solid #4364FC;
  .item-money-sell {
    color: red
  }
}
</style>
