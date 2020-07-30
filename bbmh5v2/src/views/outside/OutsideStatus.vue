<template>
  <div class="home-order-pay"
       style="min-height: 100vh;background: #ffffff">
    <van-nav-bar title="订单支付"
                 :fixed="true"
                 left-arrow
                 @click-left="onClickLeft" />
    <div style="height: 44px"></div>
    <div class="order-top-wrapper"
         v-show="(!isShowTime1&&!isShowTime2)">
      <div style="margin-top: 4vw"><span :class="iconName"></span></div>
      <div class="order-top-title1">{{orderStatusText}}</div>
    </div>
    <div class="red-tip-warpper flex-row-center" v-show="isShowTime2">一个二维码仅能成功支付一次</div>
    <div class="order-top-wrapper"
         v-show="isShowTime1">
      <div class="order-top-title" style="font-size: 18px">{{orderStatusText}}</div>
      <van-count-down :time="time"
                      style="margin: 0"
                      @finish="finshTick" />
    </div>
    <div class="infos-texts-wrapper">
      <div class="infos-status-wrapper">
        <div class="infos-status">
          <span>待支付：</span>
          <span>{{price || 0}}元</span>
        </div>
        <div class="infos-status infos-status1"
             style="">
          <span>订单编号：</span>
          <input type="text"
                 readonly
                 v-model="orderNo"
                 class="name-input1 over-text-show1 "
                 style="" />
        </div>
      </div>
      <div class="infos-text-top1"
           v-show="isShowAlipay==2">
        <van-loading type="spinner"
                     size="16px" />
        <div style="margin-left: 8px">支付宝账号正在加载中…</div>
      </div>
      <div class="infos-text-top2"
           v-show="isShowAlipay==100">
        <div class="infos-text-top1" style="border-top: none">
          <span class="icon-size-common icon-tip"></span>
          <div style="margin-left: 8px">匹配超时，请重新加载</div>
        </div>
        <div class="reload-btn"
             @click="clickRefeshStatus">重新加载</div>
      </div>
      <div class="infos-text-top2"
           v-show="isShowAlipay==0">
        <div class="infos-text-top1"
             style="padding-bottom: 0;flex-direction: column;border-top: none">
          <div style="color: #ffffff;font-size: 18px">匹配超时，请重新加载</div>
          <div class="reload-btn reload-btn1"
               @click="clickReload">重新加载</div>
        </div>
      </div>
    </div>
    <div class="alipay-wrapper"
         v-show="(orderStatusRef==2||orderStatusRef==12)"
         style="padding-bottom: 3vw">
      <div class="qrcode-content-wrapper flex-row-center">
        <div id="qrcode" v-show="!wxQrcodeUrl"></div>
        <img :src="wxQrcodeUrl"
             class="appay-wrapper-qcr" v-if="wxQrcodeUrl">
      </div>

      <div class="tip-content-wrapper flex-row-center">
        <div class="tip-content-one">
          <p>截屏</p>
          <p>保存</p>
        </div>
        <div class="tip-content-right">>></div>
        <div class="tip-content-one">
          <p>{{payType==1?'支付宝':'微信'}}</p>
          <p>扫一扫</p>
        </div>
        <div class="tip-content-right">>></div>
        <div class="tip-content-one">
          <p>相册</p>
          <p>选码</p>
        </div>
        <div class="tip-content-right">>></div>
        <div class="tip-content-one">
          <p>识别</p>
          <p>付款</p>
        </div>
      </div>

      <div class="appay-wrapper-time"
           v-show="isShowTime2">
        <div style="font-weight: bold;color:#666666">{{orderStatusText}}</div>
        <van-count-down :time="time"
                        style="margin: 0;font-weight: bold;color:#E93F3F;font-size: 20px"
                        @finish="finshTick" />
      </div>

      <div style="width: 88vw;margin: 1vh 0;position: relative;display: none">
        <div class="big flex-row-center open-alipay-btn" @click="onClickSucc2">
          <span style="color: #ffffff">点击启动支付宝</span>
        </div>
      </div>
    </div>
    <div style="height: 14vh" v-show="(orderStatusRef!=2&&orderStatusRef!=12)"></div>
    <div style="width: 88vw;margin: 1vh 6vw 3vh;">
      <button-xbd :disabled="false"
                  @click="onClickQuestion">点击这里可反馈问题</button-xbd>
    </div>
    <div class="mask-jump-alipay"
         @click="openUrl"
         v-show="isShowAlipayMask"></div>
  </div>
</template>

<script>
import Vue from 'vue'
import VueClipboard from 'vue-clipboard2'
import QRCode from 'qrcodejs2'
import { CountDown, DropdownMenu, DropdownItem, Loading } from 'vant'
import ButtonXbd from '@components/button-xbd'
import { OUTSIDE_ORDER_INFO, OUTSIDE_C_CANCEL_NEW, OUTSIDE_OPEN_ALIPAY, OUTSIDE_C_CONFIRM, OUTSIDE_PUSH_OREDR, OUTSIDE_TO_PAY, OUTSIDE_REORDER, OUTSIDE_PAY_AGAIN } from '@api/outside'

Vue.use(CountDown).use(VueClipboard).use(DropdownMenu).use(DropdownItem).use(Loading)

// v-show="orderStatusRef!=3&&orderStatusRef!=5&&orderStatusRef!=6&&orderStatusRef!=11&&orderStatusRef!=100&&!isOverTime"
export default {
  name: 'OutsidePay',
  components: {
    ButtonXbd
  },
  data () {
    return {
      orderNo: '',
      name: '',
      account: '',
      price: '',
      price1: '',
      orderStatusRef: '',
      waitPayFinishTime: '',
      isFinishTick: false,
      canAppealEndTime: '', // 允许申诉截止时间戳,
      isReLoading: true,
      uwcOrder: '',
      wxQrcodeUrl: '',
      copyOrderNum: '',
      orderNum: '',
      email: '',
      nowTimeStamp: '', // 服务器返回的当前时间戳
      resetTime: '',
      userNumber: '',
      cCustomerId: '',
      isOverTime: false, // 超时并过了申诉时间，不显示下面两个按钮
      alipayAuthId: '', // 支付宝用户id
      isHadQrCode: false,
      isShowAlipayMask: false,
      payType: 1
    }
  },
  created () {
  },
  mounted () {
    this.cOrder = this.$route.query.cid || 1
    this.cCustomerId = this.$route.query.cCustomerId
    this.price1 = this.$route.query.price
    this.payType = this.$route.query.payType
    this.pushOrder()
  },
  destroyed () {
    this.timeInt && clearTimeout(this.timeInt)
  },
  computed: {
    time () {
      if (!this.waitPayFinishTime || this.orderStatusRef == 9) {
        return 0
      }
      const nTimestamp = Math.floor(new Date().getTime() / 1000)
      return (this.waitPayFinishTime - (this.nowTimeStamp || nTimestamp)) * 1000
    },
    iconName () {
      if (this.orderStatusRef == 5) {
        return 'icon-succ icon-size-common1'
      } else if (this.orderStatusRef == 7 || this.orderStatusRef == 6 || this.orderStatusRef == 11) {
        return 'icon-fail icon-size-common1'
      }
      return ''
    },
    orderStatusText () {
      if (this.orderStatusRef == 1) {
        return '等待匹配中...'
      } else if (this.orderStatusRef == 5) {
        return '订单购买成功'
      } else if (this.orderStatusRef == 2 || this.orderStatusRef == 12) {
        return '请在下面时间内完成付款'
      } else if (this.orderStatusRef == 3 || this.orderStatusRef == 9) {
        return '平台确认收款剩余时间'
      } else if (this.orderStatusRef == 7) {
        return '订单超时交易失败'
      } else if (this.orderStatusRef == 6) {
        return '交易失败-取消交易'
      } else if (this.orderStatusRef == 8) {
        return '正在申诉中...'
      } else if (this.orderStatusRef == 11) {
        return '加载超时'
      }
      return '等待商家确认收款'
    },
    isShowAlipay () {
      if (this.orderStatusRef == 100) { // 加载错误
        return 100
      }
      if (this.orderStatusRef == 1) {
        return 2
      }
      if (this.orderStatusRef == 11) {
        return 0
      }
      return 1
    },
    // isShowTime () {
    //   return this.orderStatusRef == 1 || this.orderStatusRef == 2 || this.orderStatusRef == 3 || this.orderStatusRef == 9 || this.orderStatusRef == 12
    // },
    isShowTime1 () {
      return this.orderStatusRef == 1 || this.orderStatusRef == 3 || this.orderStatusRef == 9
    },
    isShowTime2 () {
      return this.orderStatusRef == 2 || this.orderStatusRef == 12
    }
  },
  watch: {
    nowTimeStamp (val) { // canAppealEndTime变化
      if (val && this.canAppealEndTime) {
        this.resetTime = this.$timeCompareWithBefore(this.canAppealEndTime, this.nowTimeStamp)
      } else {
        this.resetTime = ''
      }
    }
  },
  methods: {
    openUrl (e) { // 打开链接为  http://daifu.jiajiahebao.com/a.html?aid=1&m=2
      // if (!this.wxQrcodeUrl) {
      let searchStr = window.location.href.split('?')[1] || ''
      let url = 'http://' + location.host + '/b.html?' + searchStr
      if (searchStr.indexOf('aid') < 0) { // 最初的链接
        url += '&aid=' + this.alipayAuthId + '&m=' + this.price1 + '&o=' + this.copyOrderNum
        // url += '&aid=' + this.alipayAuthId + '&m=' + this.price1 + '&o=' + this.orderNum
      }
      // const href = 'alipays://platformapi/startapp?appId=20000067&url=' + encodeURIComponent(url)
      const href = 'alipays://platformapi/startapp' // 仅打开支付宝
      window.location.replace(href)
      this.isShowAlipayMask = false
      e && e.preventDefault()
      e && e.stopPropagation()
      // }
    },
    genQrcode () {
      let qrcodeUrl = 'alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={"s": "money", "u": "' + this.alipayAuthId + '", "a": "' + this.price1 + '", "m": "bbm-' + this.orderNum + '"}'
      let qrcode = new QRCode('qrcode', {
        width: 170,
        height: 170, // 高度
        text: qrcodeUrl // 二维码内容
      })
      // 跳转打开支付宝app
      // const aid = this.$route.query.aid || ''
      // if (aid && !this.$isIos()) {
      // if (!this.$isIos()) {
      //   this.isShowAlipayMask = true
      // } else if (this.$isIos()) {
      // if (!this.wxQrcodeUrl) {
      //   this.openUrl()
      // }
      // }
    },
    onClickQuestion () {
      this.$router.push({ path: '/order/complaint?oid=' + this.uwcOrder + '&fromc=1' })
    },
    onClickAlipay () {
      this.$router.push({ path: '/game/transali' })
    },
    onClickLeft () {
      this.$router.go(-1)
    },
    onCopyOrderNum () {
      this.$copyText(this.copyOrderNum).then((e) => {
        this.$toast('复制成功')
      }, (e) => {
        this.$toast('复制失败')
      })
    },
    finshTick () {
      console.log('finished tick time')
      this.isFinishTick = true
    },
    onClickSucc2 () {
      // const openUrl = 'http://' + location.host + '/a.html?aid=' + this.alipayAuthId + '&m=' + this.price1 + '&o=' + this.orderNum
      // window.open(openUrl, '_blank')
      let params = { cOrder: this.cOrder }
      this.$reqPost(OUTSIDE_OPEN_ALIPAY, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        // window.location.href = hostName + '/a.html?aid=' + this.alipayAuthId + '&m=' + this.price1 + '&u=' + this.name
      })
      this.openUrl()
    },
    clickRefeshStatus () {
      if (!this.isReLoading) {
        this.onClickefresh()
      }
    },
    pushOrder () {
      const that = this
      let params = { cOrder: this.cOrder, price: this.price1, cCustomerId: this.cCustomerId, payType: this.payType }
      // this.$vLoading.show()
      this.$reqPost(OUTSIDE_PUSH_OREDR, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        this.onClickefresh()
        // this.timeInt = setInterval(() => { // 30秒刷新一次
        // this.clickRefeshStatus()
        // }, 1 * 1000)
      }).catch(err => {
        console.log(err)
        this.isClickReload = false
        this.orderStatusRef = 100 // todo open
        this.isReLoading = false
      })
    },
    clickReload () { // 重新加载 OUTSIDE_REORDER
      const that = this
      if (this.isClickReload) {
        return
      }
      let params = { cOrder: this.cOrder, price: this.price1, cCustomerId: this.cCustomerId, payType: this.payType }
      this.$vLoading.show()
      this.timeInt && clearTimeout(this.timeInt)
      this.isClickReload = true
      this.$reqPost(OUTSIDE_REORDER, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        this.$vLoading.hide()
        if (res.status.code === 200) {
          // that.timeInt = setInterval(() => { // 30秒刷新一次
          that.clickRefeshStatus()
          // }, 1 * 1000)
        }
      }).catch(err => {
        console.log(err)
        this.$vLoading.hide()
        this.isClickReload = false
        this.orderStatusRef = 100
        this.isReLoading = false
      })
    },
    onClickefresh () {
      const that = this
      if (this.isClickefresh) {
        return
      }
      let params = { cOrder: this.cOrder }
      // this.$vLoading.show()
      this.isClickefresh = true
      this.$reqPost(OUTSIDE_ORDER_INFO, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        this.isReLoading = false
        this.isClickefresh = false
        this.isClickReload = false
        this.timeInt && clearTimeout(this.timeInt)
        if (res.status.code === 200) {
          that.nowTimeStamp = res.status.time
          that.orderNo = res.data.cOrderNo
          that.name = res.data.zfbName
          that.account = res.data.zfbAccount
          that.price = res.data.price
          that.wxQrcodeUrl = res.data.wxQrcodeUrl
          that.copyOrderNum = res.data.copyOrderNum
          that.orderStatusRef = res.data.status
          that.canAppealEndTime = res.data.canAppealEndTime
          that.uwcOrder = res.data.uwcOrder
          that.orderNum = res.data.orderNum
          that.email = res.data.appealMail
          if (res.data.alipayAuthId) {
            that.alipayAuthId = res.data.alipayAuthId
          }
          // that.cCustomerId = res.data.cCustomerId
          that.userNumber = res.data.userNumber
          if (that.orderStatusRef == 7 || that.orderStatusRef == 9) { // 过了申诉时间
            that.isOverTime = true
          }
          // that.orderStatusRef = 9
          // that.canAppealEndTime = '1569138513'
          if (that.orderStatusRef == 1) {
            that.waitPayFinishTime = res.data.customerOrderMatchEndTime
            // that.waitPayFinishTime = '1569138513'
          } else if (that.orderStatusRef == 2 || that.orderStatusRef == 12) {
            that.waitPayFinishTime = res.data.waitPayFinishTime
            if (that.wxQrcodeUrl) {
            } else if (!this.isHadQrCode) {
              that.isHadQrCode = true
              that.genQrcode()
            }
            // that.waitPayFinishTime = '1569138513'
          } else if (that.orderStatusRef == 3) {
            that.waitPayFinishTime = res.data.userConfirmEndTimeStamp
            // that.waitPayFinishTime = '1569138513'
          } else if (that.orderStatusRef == 9) {
            // that.waitPayFinishTime = Math.floor(new Date().getTime() / 1000)
            that.waitPayFinishTime = res.data.canAppealEndTime
          } else if (that.orderStatusRef == 11 || that.orderStatusRef == 6) {
            that.timeInt && clearTimeout(that.timeInt) // 清除定时器
          }
          let timePerRequest = 920
          if (that.orderStatusRef != 1) { // 待匹配
            timePerRequest = 10 * 1000
          }
          if (that.orderStatusRef == 2 || that.orderStatusRef == 12) { // 匹配到了
            timePerRequest = 5 * 1000
          }
          this.timeInt = setTimeout(() => { // 返回后再去刷新
            this.onClickefresh()
          }, timePerRequest)
        }
      }).catch(err => {
        console.log(err)
        this.isClickefresh = false
        this.orderStatusRef = 100
        this.isReLoading = false
      })
    }
  }
}
</script>

<style scoped lang="less">
  @import "../../assets/less/home.less";
  .van-icon-warning {
    font-size: 24px;
    color: @xbd-red-color;
    margin-right: 2vw;
  }

  .infos-status-wrapper {
    padding: 12px 10px 6px;
    font-size: @font-size-llg;
    /*border-bottom: 1px solid #dddddd;*/
  }
  .red-tip-warpper {
    width: 92vw;
    margin-top: 4vw;
    margin-left: 4vw;
    box-sizing: border-box;
    font-size: @font-size-llg;
    background-color: #ff5b5b;
    border-radius: 8px;
    padding: 14px 0;
    color: #ffffff;
  }

  .icon-warning {
    font-size: 44px;
    color: @xbd-red-color;
    margin-bottom: 1vw;
    padding: 4vw 0 1vw;
  }

  .icon-fail {
    .bg-image("icon-fail");
  }

  .icon-succ {
    .bg-image("icon-succ");
  }

  .icon-size-common1 {
    width: 34px;
    height: 34px;
    display: inline-block;
    margin: 0 2vw;
    background-size: contain;
    background-repeat: no-repeat;
  }

  .order-top-title1 {
    color: #666;
    font-size: @font-size-lg;
    margin: 2vw 0 4vw;
  }

  .infos-text-top1 {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 12px 0;
    font-size: @font-size-md;
    border-top: 1px solid #ffffff;
  }
  .flex-row-center {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
  }

  .infos-text-top2 {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    border-top: 1px solid #ffffff;
  }
  .infos-status1 {
    margin-top: 6px;
    font-size: 15px;
    display: flex;
    align-items: center;
    box-sizing: border-box;
    input {
      flex: 1;
    }
  }
  .reload-btn {
    width: 28vw;
    padding: 6px;
    margin-bottom: 12px;
    border-radius: 16px;
    border: 1px solid #ddd;
    font-size: @font-size-lg;
    text-align: center;
  }
  .reload-btn1 {
    margin: 10px 0;
  }

  .icon-tip {
    .bg-image("icon-tip");
    width: 14px;
    height: 14px;
  }
  .infos-texts-one {
    display: flex;
    flex-direction: row;
    align-items: center;
    input {
      flex: 1;
    }
  }
  .alipay-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    font-size: 14px;
    .qrcode-content-wrapper {
      width: 100%;
      position: relative;
    }
    .appay-wrapper-qcr {
      width: 170px;
      height: 170px;
    }
    .appay-wrapper-time {
      text-align: center;
      font-weight: bold;
      font-size: 18px;
      margin-top: 4vw;
    }
  }
  .red {
    color: @xbd-red-color;
  }
  .big {
    font-weight: bold;
  }
  .open-alipay-btn {
    background: #06c55b;
    padding: 14px;
    border-radius: 22px;
    width: 100%;
    box-sizing: border-box;
    font-size: 17px;
  }
  .tip-content-wrapper {
    width: 100vw;
    margin-top: 4vw;
    .tip-content-one {
      width: 15vw;
      height: 15vw;
      border: 1px solid #4f94f6;
      color: #4f94f6;
      text-align: center;
      padding: 2vw 0;
      box-sizing: border-box;
      p {
        height: 5.5vw;
        line-height: 5.5vw;
        text-align: center;
      }
    }
    .tip-content-right {
      color: #4f94f6;
      margin: 0 2.2vw;
    }
  }
  .mask-jump-alipay {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(255, 255, 255, 0.1);
  }
</style>
