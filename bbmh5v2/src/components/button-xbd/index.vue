<template>
  <div class="button">
    <button :disabled="disabled"
            @click="onClick"
            :type="type"
            class="button-xbd"
            :class="{'is-disabled':disabled}"
    >
      <slot></slot>
    </button>
  </div>
</template>

<script>
export default {
  name: 'ButtonXbd',
  props: {
    disabled: {
      type: Boolean,
      default: true
    },
    type: String
  },
  data () {
    return {
    }
  },
  methods: {
    onClick () {
      if (!this.disabled) {
        this.$emit('click')
      }
    }
  }
}
</script>

<style scoped lang="less">
.button-xbd {
  width: 100%;
  height: 46px;
  line-height: 46px;
  color: white;
  background: linear-gradient(left, #5FA4FF,#4364FC);
  font-size: @font-size-lg;
  border: none;
  outline: none;
  border-radius: 22px;
}
.is-disabled {
  background: #ddd;
  color: #fff;
}

</style>
