<template>
  <div class="item-card-hot clearfix" @click="itemClick">
    <img v-lazy="hotItem.img_url" />
    <div class="item-texts">
      <div class="item-name over-text-show">{{hotItem.type_name || ''}}</div>
      <div class="item-price over-text-show">{{moneys || ''}}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'item-card-hot',
  props: {
    hotItem: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
    moneys () {
      if (this.hotItem.min < 1) {
        return '￥' + this.hotItem.max
      }
      return '￥' + (this.hotItem.min || '') + '~' + (this.hotItem.max || '')
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
  .item-card-hot {
    display: flex;
    box-sizing: border-box;
    width: 44vw;
    /*height: 38vw;*/
    flex-direction: column;
    margin: 0 2vw 4vw;
    img {
      width: 100%;
      height: 33vw;
    }
    div {
      text-align: left
    }
    .item-texts {
      padding: 0;
    }
    .item-name {
      font-size: @font-size-lg;
      font-weight: 400;
      height: 24px;
      line-height: 24px;
    }
    .item-price {
      font-size: @font-size-md;
      height: 18px;
      line-height: 18px;
      color: @xbd-red-color;
    }
  }

</style>
