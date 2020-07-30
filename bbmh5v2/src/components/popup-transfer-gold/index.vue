<template>
  <van-popup
    v-model="isShowPopup"
    round
    position="center"
    :close-on-click-overlay="false"
    :style="{ width: '88%' }"
  >
    <div class="common-popup-wrapper common-flex-center" v-if="isFirstStep">
      <div class="common-top-wrapper common-flex-center">
        <div class="title">卖出金币数量(个)</div>
        <input type="number" placeholder="请输入转卖的金币数" class="input-number" v-model.trim="goldVal" />
      </div>
      <div class="common-bottom-wrapper van-hairline--top common-flex-center">
        <div class="cancel-btn van-hairline--right" @click="clickCancel(1)">取消</div>
        <div class="confirm-btn" @click="clickConfirm(1)">提交订单</div>
      </div>
    </div>
    <div class="common-popup-wrapper common-flex-center" v-if="!isFirstStep">
      <div class="common-top-wrapper common-flex-center" style="justify-content: center">
        <div class="title1">您确定要给{{selectedMobile}}</div>
        <div class="title1">转<span class="color-theme">{{goldVal}}</span>金币吗？</div>
      </div>
      <div class="common-bottom-wrapper van-hairline--top common-flex-center">
        <div class="cancel-btn van-hairline--right" @click="clickCancel(2)">取消</div>
        <div class="confirm-btn" @click="clickConfirm(2)">确定</div>
      </div>
    </div>
  </van-popup>
</template>

<script>
import Vue from 'vue'
import { Popup } from 'vant'
import { USER_NEXT_GOLD } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'

Vue.use(Popup)

export default {
  name: 'popup-transfer-gold',
  data () {
    return {
      goldVal: ''
    }
  },
  props: {
    isShowPopup: {
      type: Boolean,
      default: false,
      required: true
    },
    isFirstStep: {
      type: Boolean,
      default: false,
      required: true
    },
    selectedMobile: {
      type: String
    },
    balance: {
      type: Number
    }
  },
  computed: {},
  methods: {
    closeMyself () {
    },
    clickCancel (type) {
      // this.isShowPopup = false
      // this.selectedMobile = ''
      // this.isFirstStep = true
      this.goldVal = ''
      if (type == 1) {
        this.$emit('popupStatus', false, true, true)
      } else {
        this.$emit('popupStatus', false, true, false)
      }
    },
    clickConfirm (type) {
      if (type == 1) {
        this.matchGold()
      } else {
        // this.isShowPopup = false
        this.$emit('popupStatus', false, false)
        this.onSubmitGold() // 调用转账金币接口
      }
    },
    matchGold () {
      let moneyVal = (this.goldVal && this.goldVal.trim()) || ''
      let msgAlert = ''
      if (!this.balance || parseInt(this.balance) == 0) {
        msgAlert = '金币不足'
      } else if (!moneyVal) {
        msgAlert = '请先输入转账金币个数'
      } else if (moneyVal.indexOf('.') > -1) {
        msgAlert = '不要输入小数点.'
      } else if (moneyVal % 100 != 0) {
        msgAlert = '请输入整百的金币'
      } else if (moneyVal > parseInt(this.balance)) {
        msgAlert = '转账金币不能比当前总金币多'
      } else if (parseInt(moneyVal) < 1 || moneyVal.indexOf('0') == 0) {
        msgAlert = '请输入正确金币个数'
      }
      if (msgAlert) {
        this.$toast(msgAlert)
        return
      }
      // this.isShowPopup = false
      this.$emit('popupStatus', false, true, false)

      // 显示第二步
      this.timeOut && clearTimeout(this.timeOut)
      this.timeOut = setTimeout(() => {
        // this.isFirstStep = false
        // this.isShowPopup = true
        this.$emit('popupStatus', true, false, false)
      }, 120)
    },
    onSubmitGold () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm, number: this.goldVal, mobile: this.selectedMobile }
      this.$vLoading.show()
      // 重置数据
      this.goldVal = ''
      // this.selectedMobile = ''
      // this.isFirstStep = true
      this.$emit('popupStatus', false, true, true)
      this.$reqPost(USER_NEXT_GOLD, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          // this.getHots()
          this.$store.dispatch('getUserInfo')
          // this.$toast('交易成功')
          this.$emit('callBack')
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
      })
    }
  }
}
</script>
<style lang="less" scoped>
  .common-flex-center {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
  .common-popup-wrapper {
    height: 46vw;
    width: 100%;
    box-sizing: border-box;
    font-size: @font-size-lg;
    .common-top-wrapper {
      display: flex;
      justify-content: space-around;
      flex: 1;
      padding: 0 18px;
      .title {
        font-weight: bold;
        margin-top: 2.2vh;
        font-size: @font-size-llg;
      }
      .title1 {
        text-align: center;
        font-size: @font-size-llg;
        margin: 0.6vh 0;
        .color-theme {
          color: @xbd-theme-color
        }
      }
      .input-number {
        padding: 10px 0;
        width: 72vw;
        border-radius: 10px;
        border: 1px solid #DDDDDD;
        text-align: center;
        margin-bottom: 1vh;
      }
    }
    .common-bottom-wrapper {
      flex-direction: row;
      width: 100%;
      div {
        flex: 1;
        text-align: center;
        padding: 16px 0;
      }
      .cancel-btn {
        color: #333333;
      }
      .confirm-btn {
        color: @xbd-theme-color
      }
    }
  }
</style>
