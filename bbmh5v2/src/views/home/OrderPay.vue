<template>
  <div class="home-order-pay">
    <van-nav-bar
      title="订单支付"
      :fixed="true"
      left-arrow
      @click-left="onClickLeft"
    />
    <div class="order-top-wrapper clear-navbar-top">
      <div class="order-top-title">订单剩余时间</div>
      <van-count-down :time="time" @finish="finshTick" v-show="orderStatus==1" style="margin: 0"/>
    </div>
    <div class="tip-text clearfix">
      <van-icon name="warning"/>
      <span>平台不支持自动扣款，请使用您的银联向以下账户自行转账!</span>
    </div>
    <div class="infos-texts-wrapper" style="margin-top: 0">
      <div class="infos-text-bottom" style="border-top: none;padding-bottom: 0">
        <div class="infos-bottom-one" style="font-size: 18px;padding: 2vw 0">
          <span style="min-width: 32vw">待支付：{{orderAmount}}元</span>
        </div>
        <div class="infos-bottom-one" style="font-size: 18px;padding: 2vw 0;display: none">
          <span style="min-width: 26vw">支付方式：</span>
          <van-dropdown-menu :overlay="true" style="flex: 1">
            <van-dropdown-item :overlay="true" v-model="value" :options="option"
                               style="left: 7.5vw;right: 7.5vw"/>
          </van-dropdown-menu>
        </div>
      </div>
      <div class="infos-text-top" style="border-top: 1px solid #ffffff" v-show="value==2">
        <div class="infos-texts-one1">
          <span>钱包地址：</span>
          <input type="text" readonly v-model="wallet"  style="display: none"/>
          <div class="right-text-wrapper">
            <div class="right-text">{{wallet}}</div>
          </div>
        </div>
        <div style="text-align: center">
          <button type="button" @click="onCopyAccount" style="font-size: 14px;width: 58vw;margin-top: 2vw;height: 26px;line-height: 26px">复制钱包地址</button>
        </div>
      </div>
      <div class="infos-text-top" style="border-top: 1px solid #ffffff;" v-show="value==1">
        <div class="infos-texts-one">
          <span>姓名：{{businessBankUsername}}</span>
          <input type="text" readonly v-model="businessBankUsername"  style="display: none"/>
          <button type="button" @click="onCopyName" style="font-size: 12px;margin-left: 4vw">复制姓名</button>
        </div>
        <div class="infos-texts-one">
          <span>账号：{{businessBankAccount}}</span>
          <input type="text" readonly v-model="businessBankAccount"  style="display: none"/>
          <button type="button" @click="onCopyBankAccount" style="font-size: 12px;margin-left: 4vw">复制账号</button>
        </div>
        <div class="infos-texts-one">
          <span>银行名称：{{businessBankName}}</span>
        </div>
      </div>
    </div>

    <div class="notice-texts-wrapper clearfix" style="margin-top: 0" v-show="value==1">
      <div class="notice-title">特别声明：</div>
      <div class="one-line-text">
        <div>1.</div>
        <div>收款银行卡<span class="link-text">不定期更换</span>，每次转账时<span class="link-text">请核实收款人姓名以及收款账号是否一致</span>，应个人问题出现损失自行承担！</div>
      </div>
      <div class="one-line-text">
        <div>2.</div>
        <div> 转账给商家，必须使用在帮帮买App添加过的银行卡号，一旦不使用在帮帮买App添加的卡号，出现的转账问题，需自己承担，平台不承担责任；</div>
      </div>
      <div class="one-line-text">
        <div>3.</div>
        <div> 转账时，不要在支付宝-"转账到银行卡"转账，请使用对应的银行卡App，在银行卡App里给商家转账，
          <router-link to="/game/transbank" class="link-text">具体查看转账教程</router-link>
        </div>
      </div>
    </div>

    <div class="notice-texts-wrapper clearfix" style="min-height: 50vw" v-show="value==2">
      <div class="notice-title">特别声明：</div>
      <div class="one-line-text">
        <div> 提交订单后，请务必立即联系您的客服<span class="link-text">确认订单汇率</span>再进行支付~</div>
      </div>
    </div>

    <div class="two-btn-wrap" style="margin-top: 4vw">
      <button-xbd :disabled="false" @click="onClickCancel" class="btn-left">取消交易</button-xbd>
      <button-xbd :disabled="false" @click="onClickSucc" class="btn-right">我已成功付款</button-xbd>
    </div>

    <popup-xbd :is-show="isShowPopup" @cancelTradeClick="onCancelTrade" @confirmTradeClick="onConfirmTrade" />
  </div>
</template>

<script>
import Vue from 'vue'
import VueClipboard from 'vue-clipboard2'
import { CountDown, Popup, DropdownMenu, DropdownItem } from 'vant'
import { HOME_CHANGE_PRODUCT_STATUS, HOME_BANK_INFOS, HOME_CHANGE_PAY_TYPE } from '@api/home'
import ButtonXbd from '@components/button-xbd'
import PopupXbd from '@components/popup-xbd'
import { getLocalStorage } from '@utils/local-storage'

Vue.use(CountDown).use(VueClipboard).use(Popup).use(DropdownMenu).use(DropdownItem)

export default {
  name: 'OrderPay',
  components: {
    ButtonXbd,
    PopupXbd
  },
  data () {
    return {
      // time: 10 * 1000,
      isShowPopup: false,
      businessBankUsername: '',
      businessBankName: '',
      businessBankAccount: '',
      wallet: '',
      businessProductId: '',
      oid: '',
      orderAmount: '', // 总价,
      orderTimeLimit: '',
      nowTimeStamp: '',
      orderStatus: '', // 超时状态
      value: 1,
      option: [
        { text: '银行卡支付', value: 1 },
        { text: 'USDT支付', value: 2 }
      ]
    }
  },
  mounted () {
    this.oid = this.$route.query.oid // 传过来的订单id
    if (this.$route.query.fromlist) { // 从我的购买记录列表页跳转过来的
      this.businessProductId = this.$route.query.bid // 传过来商家id
    }
    this.getBankInfo()
  },
  computed: {
    time () {
      if (!this.orderTimeLimit || this.orderTimeLimit == 100) {
        return 1000 * 1000 // 防止一下子触发超时订单接口
      }
      // if (this.orderTimeLimit == 100) {
      //   return 1000 * 1000
      // }
      // const nowTimestamp = Math.floor(new Date().getTime() / 1000)
      // return ((this.orderInfo.orderTimeLimit || this.orderTimeLimit) - nowTimestamp) * 1000
      return this.$timeCompareWithBefore(this.orderTimeLimit, this.nowTimeStamp, true) * 1000
    }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1)
    },
    onCopyName () {
      this.$copyText(this.businessBankUsername).then((e) => {
        this.$toast('复制成功')
      }, (e) => {
        this.$toast('复制失败')
      })
    },
    onCopyAccount () {
      this.$copyText(this.wallet).then((e) => {
        this.$toast('复制成功')
      }, (e) => {
        this.$toast('复制失败')
      })
    },
    onCopyBankAccount () {
      this.$copyText(this.businessBankAccount).then((e) => {
        this.$toast('复制成功')
      }, (e) => {
        this.$toast('复制失败')
      })
    },
    finshTick () {
      console.log('finished tick time')
      this.changeOrderStatus(3) // 超时
    },
    onClickCancel () {
      this.isShowPopup = true
    },
    onClickSucc () {
      console.log('onClickSucc')
      this.$dialog.confirm({
        title: '确认支付',
        message: '请确认您已向卖方付款，恶意点击将被冻结？',
        cancelButtonText: '不小心点错了',
        confirmButtonText: '确认支付',
        cancelButtonColor: '#333333'
      }).then(() => {
        // on confirm
        this.changeOrderStatus(2)
        // this.changePayType()
        // this.$router.push({ path: '/home/sell' })
      }).catch(() => {
        // on cancel
      })
    },
    onCancelTrade (detail) {
      console.log('cancelTradeClick', detail)
      this.isShowPopup = false
      this.changeOrderStatus(6, detail)
    },
    onConfirmTrade () {
      console.log('confirmTradeClick')
      this.isShowPopup = false
    },
    getBankInfo () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { orderNumber: this.oid, token: tokenBbm }
      this.$vLoading.show()
      this.$reqPost(HOME_BANK_INFOS, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        that.nowTimeStamp = res.status.time
        if (res.status.code === 200) {
          that.businessBankUsername = res.data.businessBankUsername
          that.businessBankName = res.data.businessBankName
          that.businessBankAccount = res.data.businessBankAccount
          that.wallet = res.data.wallet

          that.orderAmount = res.data.orderAmount
          that.businessProductId = res.data.business_product_id
          that.orderTimeLimit = res.data.orderTimeLimit
          that.orderStatus = res.data.orderStatus
          if (that.orderStatus == 2 || that.orderStatus == 4) {
            that.$router.replace({ path: '/user/buyrec?tab=2' }) // 跳到我的购买订单——待转卖
          } else if (that.orderStatus == 7) {
            that.$router.replace({ path: '/user/buyrec?tab=3' }) // 跳到我的购买订单——被拒订单
          } else if (that.orderStatus == 3) {
            this.$store.dispatch('updateOrderInfo', res.data)
            that.$router.replace({ path: '/home/orderstatus?oid=' + this.oid })
          } else if (that.orderStatus == 5) {
            that.$router.replace({ path: '/user/sellrec' }) // 跳到我的转卖订单
          } else if (!that.orderTimeLimit) {
            let msgStr = '该笔订单已超时'
            if (that.orderStatus == 6) {
              msgStr = '该笔订单已取消'
            }
            that.orderTimeLimit = 100 // fake
            that.$dialog.alert({
              message: msgStr,
              confirmButtonText: '我知道了'
            }).then(() => {
              that.$router.go(-1) // 返回上一层
            })
          }
        } else if (res.status.code === 6017 || res.status.code === 6014) {
          that.$dialog.alert({
            message: res.status.message,
            confirmButtonText: '我知道了'
          }).then(() => {
            that.$router.go(-1) // 返回上一层
          })
        }
      }).catch(err => {
        console.log(err)
      })
    },
    changeOrderStatus (status, detailStr) {
      // this.$router.replace({ path: '/home/orderstatus' })
      // return
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      this.$vLoading.show()
      let params = { status: status, token: tokenBbm, detail: detailStr || '', orderNum: this.oid }
      this.$reqPost(HOME_CHANGE_PRODUCT_STATUS, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          if (status === 6) { // 取消
            this.$router.replace({ path: '/home' })
          } else if (status === 2) { // 支付成功
            that.$store.dispatch('updateOrderInfo', res.data)
            this.$router.replace({ path: '/home/sell?oid=' + res.data.orderNum })
          } else { // 超时
            that.$store.dispatch('updateOrderInfo', res.data)
            this.$router.replace({ path: '/home/orderstatus?oid=' + res.data.orderNum })
          }
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
        if (status === 3) {
          this.$dialog.alert({
            message: '该笔订单已超时',
            confirmButtonText: '我知道了'
          }).then(() => {
            that.$router.go(-1) // 返回上一层
          })
        }
      })
    },
    changePayType () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      this.$vLoading.show()
      let params = { orderNum: this.oid, token: tokenBbm, payWay: this.value }
      this.$reqPost(HOME_CHANGE_PAY_TYPE, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          this.changeOrderStatus(2)
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
        if (status === 3) {
          this.$dialog.alert({
            message: '该笔订单已超时',
            confirmButtonText: '我知道了'
          }).then(() => {
            that.$router.go(-1) // 返回上一层
          })
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
  @import '../../assets/less/home.less';

  .tip-text {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    padding: 2vw 4vw;
    text-align: left;
    font-size: @font-size-md;
    span {
      line-height: 16px;
      color: @color2121
    }
  }

  .van-icon-warning {
    font-size: 26px;
    color: red;
    margin-right: 2vw;
  }
  .infos-texts-one1 {
    display: flex;
    flex-direction: row;
    .right-text-wrapper {
      display: flex;
      flex: 1;
      flex-direction: column;
    }
    .right-text {
      text-align: justify;
      text-justify: newspaper;
      word-break: break-all;
    }
  }
  .van-dropdown-menu {
    height: 36px;
  }
  .van-popup--top {
    width: 80vw;
  }

</style>
