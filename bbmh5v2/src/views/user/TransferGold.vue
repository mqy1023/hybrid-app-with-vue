<template>
    <div class="setting-content">
      <van-nav-bar
        title="提现盈利金额"
        :fixed='true'
        left-arrow
        @click-left="onClickLeft"
        @click-right="onClickRight"
      >
        <span slot="right">{{rightTitleText}}</span>
      </van-nav-bar>
      <div style="height: 76px;"></div>
      <div class="top-info-wrapper">
        <div class="title">当前剩余提现盈利金额</div>
        <div class="title title1">{{moneyRestBalance}}<span class="title">元</span></div>
        <input type="number" class="input-num" placeholder="请输入提现金额" v-model.trim="amount" />
        <div style="width: 92vw;margin-top: 1vh">
          <button-xbd :disabled="isDisabledStart" @click="onClickSave">提现</button-xbd>
        </div>
      </div>

      <div class="notice-texts-wrapper clearfix" style="margin-top: 4vh">
        <div class="notice-title">提示：</div>
        <div class="one-line-text">
          <div>1.</div>
          <div> 提现盈利金额如果不满足整百需要找上级去线下转差额，上级给你充值总金币，您可以派单，交易成功钱到账；</div>
        </div>
      </div>
    </div>
</template>

<script>
import Vue from 'vue'
import { USER_TRANSFER_GOLD } from '@api/user'
import { getLocalStorage, removeLocalStorage } from '@utils/local-storage'
import ButtonXbd from '@components/button-xbd'

export default {
  name: 'TransferGold',
  data () {
    return {
      amount: '',
      userType: ''
    }
  },
  components: {
    ButtonXbd
  },
  computed: {
    rightTitleText () {
      if (this.userType == 2) {
        return '我的提现记录'
      }
      return ''
    },
    moneyRestBalance () { // 剩余提现金额
      return (this.$store.state.user.userInfo.earnMoney || '0.00')
    },
    isDisabledStart () {
      if (!this.amount) {
        return true
      }
      return false
    }
  },
  mounted () {
    const { userType } = getLocalStorage('userType')
    this.userType = userType
  },
  destroyed () {
    this.timeOut && clearTimeout(this.timeOut)
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1)
    },
    onClickRight () {
      if (!this.rightTitleText) {
        return
      }
      this.$router.push({ path: '/user/goldoperlist' })
    },
    showSetPayPoint () {
      this.$dialog.confirm({
        message: '您还没有给您的好友设置支付通道点位',
        confirmButtonText: '去设置',
        cancelButtonColor: '#333333'
      }).then(() => {
        this.$router.replace({ path: '/user/unitset' })
      }).catch(() => {
        // on cancel
      })
    },
    onClickSave () {
      let moneyVal = (this.amount && this.amount.trim()) || ''
      let msgAlert = ''
      if (!this.moneyRestBalance || parseInt(this.moneyRestBalance) == 0) {
        msgAlert = '金额不足'
      } else if (!moneyVal) {
        msgAlert = '请先输入转账金币个数'
      } else if (moneyVal.indexOf('.') > -1 || moneyVal.indexOf('-') > -1) {
        msgAlert = '请输入正整数金币数量'
      } else if (moneyVal > parseInt(this.moneyRestBalance)) {
        msgAlert = '转金币个数不能比提现金额多'
      } else if (parseInt(moneyVal) < 1 || moneyVal.indexOf('0') == 0) {
        msgAlert = '请输入正确金额个数'
      }
      // else if (parseInt(moneyVal) > 100 && moneyVal % 100 != 0) {
      //   msgAlert = '大于100，请输入整百的金币'
      // }
      if (msgAlert) {
        this.$toast(msgAlert)
        return
      }
      let msgStr = '您确定要把盈利金额' + moneyVal + '转到总金币账户里吗？'
      let confirmType = 1
      if (moneyVal % 100 != 0 && this.userType == 2) { // 二级不是整百
        confirmType = 2
        const fullMoney = Math.ceil(moneyVal / 100) * 100
        msgStr = '您提现金额' + moneyVal + '元，不满足直接整百金额提现，您需要线下找上级补差价' + (fullMoney - moneyVal) + '元，成功后总金币账户会增加' + fullMoney + '金币呦~；'
      }
      this.$dialog.confirm({
        message: msgStr,
        confirmButtonText: '确定',
        cancelButtonColor: '#333333'
      }).then(() => {
        this.postChangeGold(confirmType)
      }).catch(() => {
      })
    },
    postChangeGold (confirmType) {
      const that = this
      this.$vLoading.show()
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm, amount: this.amount }
      this.amount = '' // 重置
      this.$reqPost(USER_TRANSFER_GOLD, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          this.$store.dispatch('getUserInfo')
          if (this.userType == 2 && confirmType == 2) { // 二级非整百跳转到记录页
            that.onClickRight()
            return
          }
          this.$dialog.alert({
            message: '金额转金币成功',
            confirmButtonText: '我知道了'
          }).then(() => {
            if (this.userType == 1) {
              this.$router.replace({ path: '/user/income?type=2' })
            }
          })
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
      })
    },
    onClickTransfer (item) {
      console.log(item.mobile)
      if (this.$store.state.user.userInfo.nextPointStatus != 2) { // 未设置下级支付通道
        this.showSetPayPoint()
        return
      }
      this.selectedMobile = item.mobile
      this.isShowPopup = true
    }
  }
}
</script>

<style scoped lang="less">
  .setting-content {
    background-color: #ffffff;
    height: 100vh;
    width: 100vw;
  }
  .top-info-wrapper {
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
  .title {
    font-size: @font-size-lg;
    color: @xbd-theme-color;
  }
  .title1 {
    font-size: @font-size-llllg;
    font-weight: bold;
    margin-top: 3vw
  }
  .input-num {
    width: 92vw;
    padding: 10px 0;
    border: 1px solid #DDDDDD;
    border-radius: 20px;
    margin: 7vw 0 6vw;
    text-align: center;
    font-size: @font-size-md;
  }
</style>
