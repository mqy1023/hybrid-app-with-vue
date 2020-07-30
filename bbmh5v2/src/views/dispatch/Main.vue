<template>
  <div class="setting-content">
    <div class="top-wrapper">
      <div class="user-infos-wrapper">
        <div class="user-infos-left">
          <img slot :src="userAvatar" class="avator-img"/>
          <div class="user-phone">Hi，{{userInfo.username || userInfo.mobile}}</div>
        </div>
        <div class="user-infos-right">
          <div style="margin-right: 2vw;font-weight: bold">{{isOrdering?'开始盈利':'暂停盈利'}}</div>
          <van-switch :value="isOrdering" @input="onSwitchInput" active-color="#3761FF" inactive-color="#ddd" size="20"/>
        </div>
      </div>
      <div class="money-detail-wrapper">
        <div class="money-detail-one money-detail-first">
          <div class="money-detail-title">总金币（币）</div>
          <div class="num-show">{{balance}}</div>
        </div>
        <div class="money-detail-one">
          <div class="money-detail-title">交易中金币</div>
          <div class="num-show">{{onProgressOrderSum}}</div>
        </div>
        <div class="money-detail-one money-detail-last">
          <div class="money-detail-title">交易中订单</div>
          <div class="num-show">{{onProgressOrderCnt}}</div>
        </div>
      </div>
    </div>
    <div class="content-wrapper flex-column-start">
      <div class="matching-wrapper flex-column-center" v-if="isLoading">
        <img src="./imgs/gold_jump.gif" class="img-gif" />
        <div class="content-tip">交易配对中，请稍等...</div>
      </div>
      <div class="matching-wrapper flex-column-center" v-if="!isLoading&&(isShowStop)">
        <div class="icon-size-common icon-empty-show" />
        <div class="content-tip" v-if="isShowStop" style="padding: 0 24vw;text-align: center">您已暂停派单，如需继续派单， 请开启盈利按钮；</div>
      </div>
      <div class="matching-wrapper flex-column-center" v-if="!isLoading&&(isShowNoGold)">
        <div class="icon-size-common icon-empty-show icon-empty-show1" />
        <div class="content-tip" v-if="isShowNoGold">您的金币不足</div>
      </div>
      <div class="matching-wrapper flex-column-center" v-if="!isLoading&&(isShowFull)">
        <div class="icon-size-common icon-empty-show icon-empty-show2" />
        <div class="content-tip" v-if="isShowFull">您的额度已达到上限</div>
      </div>
      <div class="matching-wrapper flex-column-center" v-if="!isLoading&&(isShowUnit)">
        <div class="icon-size-common icon-empty-show" />
        <div class="content-tip" v-if="isShowUnit">请联系上级代理设置支付点位</div>
      </div>
      <div class="orders-wrapper" v-if="!isLoading&&hotList&&hotList.length>0">
        <item-one-dispatch
          v-for="(item, i) in hotList"
          :key="i"
          :hotItem="item"
          @itemClick="onItemClick(item)"
          :nowTimeStamp="nowTimeStamp"
        />
      </div>
    </div>
    <div class="float-btn-wrapper" v-if="hotList&&hotList.length>0">
      <div class="float-num-wrapper">
        <div class="float-btn icon-size-common"></div>
        <div class="float-num">{{hotList.length}}</div>
      </div>
    </div>
    <van-popup v-model="isShowPopup" :close-on-click-overlay="false">
      <div class="order-notice-wrapper">
        <div class="order-notice-top">抢单公告</div>
        <div class="order-notice-content-fake"></div>
        <div class="order-notice-content">
          <div>
            <span class="icon-size-common icon-order-notice"></span>
          </div>
          <div>{{showPopupText || '订单通道维护中，敬请谅解；'}}</div>
        </div>
        <span class="order-notice-bottom clearfix"></span>
        <div class="order-notice-bottom-btn">
          <button-xbd :disabled="false" @click="iKnown">我知道了</button-xbd>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'
import { Switch, Popup } from 'vant'
import itemOneDispatch from '@components/item-one-dispatch'
import { getLocalStorage, removeLocalStorage } from '@utils/local-storage'
import { clearRequest } from '@plugins/reqAxios'

import {
  ORDER_QD_LIST,
  OREDR_B_CONFIRM,
  OREDR_HAVE_STAYRESELL,
  OREDR_MATCH_STATUS,
  OREDR_MATCH_START,
  OREDR_MATCH_STOP
} from '@api/order'
import { USER_SELL_ORDER_COUNT } from '@api/user'
import ButtonXbd from '@components/button-xbd'
Vue.use(Switch).use(Popup)
export default {
  name: 'Dispatch',
  data () {
    return {
      isLoading: true, // 页面一进来去加载中
      isOrdering: true, // 是否在抢单中
      isShowNoGold: false, // 没有金币了
      isShowStop: false, // 暂停派单且列表为空
      isShowFull: false, // 额度达上限但列表为空
      isShowUnit: false, // 显示点位设置
      balance: '0', // 总金币
      onProgressOrderSum: 0, // 交易中金币
      onProgressOrderCnt: 0, // 交易中订单数
      hotList: [],
      onProgressList: [],
      nowTimeStamp: 1576748777,
      reqSellingCount: 0, // 周期性检查是否有转卖中的订单
      isShowPopup: false,
      showPopupText: '',
      hotList1: [
        {
          'uwcOrder': '58',
          'userId': '2',
          'orderNum': '201912131409570034',
          'cCustomerId': '3',
          'cOrder': '34',
          'cPrice': '100.00',
          'status': '2',
          'waitPayFinishTime': 1576748954,
          'userConfirmEndTimeStamp': null,
          'userConfirmEndTime': '1970-01-01 08:00:00',
          'canAppealEndTimeStamp': null,
          'wxQrcodeUrl': '2',
          'payType': '2',
          'cStatus': '1',
          'customerConfirmTime': null,
          'customerConfirmTimeStamp': false,
          'updateTime': '2019-12-13 14:09:57',
          'userConfirmTime': null,
          'userConfirmTimeStamp': false,
          'createTime': '2019-12-13 14:09:57',
          'userStatus': '1',
          'bStatus': '1',
          'statusMessage': 'waitDeal',
          'earn': '0.00',
          'cOrderCreateTime': '2019-12-13 13:45:15',
          'cOrderCreateTimeStamp': 1576235915,
          'trueTradeAmount': '',
          'wxQrcodeRemark': '2222'
        }
      ]
    }
  },
  components: {
    itemOneDispatch,
    ButtonXbd
  },
  computed: {
    userInfo () {
      return this.$store.state.user.userInfo
    },
    userAvatar () {
      const avatorUrl = this.userInfo.avatar
      if (avatorUrl) {
        return avatorUrl
      }
      return require('./imgs/avatar.png')
    }
  },
  mounted () {
    // this.$vLoading.show()
    this.getMatchStatus()
    // this.isLoading = false
    // this.isShowFull = true
  },
  destroyed () {
    this.cancelPreRequest()
    this.timerRef && clearTimeout(this.timerRef)
  },
  methods: {
    // onClickModify () {
    //   this.$router.push({ path: '/login/reset' })
    // },
    onSwitchInput (checked) {
      if (this.isShowUnit) { // 未设置点位
        this.$dialog.alert({
          message: '没有设置点位不能盈利哦~',
          confirmButtonText: '我知道了'
        }).then(() => {
        })
        return
      }
      let msgStr = checked ? '开始盈利？' : '暂停盈利？'
      this.$dialog.confirm({
        message: msgStr,
        confirmButtonText: '确定',
        cancelButtonColor: '#333333'
      }).then(() => {
        if (!checked) { // 暂停盈利
          if (this.hotList.length == 0) { // 空列表且暂停抢单
            this.isLoading = false
            this.isShowStop = true
          }
          this.stopOrder()
        } else {
          this.isShowStop = false
          this.startOrder()
        }
        this.isOrdering = checked
      }).catch(() => {
        this.isOrdering = !checked
      })
    },
    resetAllStatus (isClearData = 1) { // 重置所有状态
      this.timerRef && clearTimeout(this.timerRef)
      this.timerRef = null
      this.isOrdering = false
      this.isShowNoGold = false
      this.isLoading = false
      this.isShowStop = false
      this.isShowUnit = false
      if (isClearData) {
        this.hotList = []
      }
    },
    cancelPreRequest () { // 取消请求中的网络请求
      const CancelToken = axios.CancelToken
      clearRequest.source.cancel && clearRequest.source.cancel()
      clearRequest.source = CancelToken.source()
    },
    getMatchStatus () {
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm || this.$store.state.app.token }
      this.isReqSellingCount = true
      this.$reqPost(OREDR_MATCH_STATUS, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code == 200) {
          this.isOrdering = true
        } else if (res.status.code == 4106) {
          this.resetAllStatus()
        } else if (res.status.code == 4111) {
          this.$dialog.alert({
            message: res.status.message || '',
            confirmButtonText: '我知道了'
          }).then(() => {
            this.$router.replace({ path: '/user2' })
          })
          return
        }
        this.getHots()
      }).catch(err => {
        console.log(err)
      })
    },
    startOrder (isForce = 0) {
      if (this.isOrdering && !isForce) {
        this.$toast('已是开始盈利状态')
        return
      }
      if (this.hotList.length == 0) {
        this.isLoading = true
      }
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm || this.$store.state.app.token }

      this.cancelPreRequest()
      this.resetAllStatus()
      // this.$vLoading.show()
      this.$reqPost(OREDR_MATCH_START, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          this.isOrdering = true
          this.reqSellingCount = 0
          this.getHots()
        }
      }).catch(err => {
        console.log(err)
      })
    },
    stopOrder (isForce = 0) {
      if (!this.isOrdering && !isForce) {
        this.$toast('已是暂停盈利状态')
        return
      }
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm || this.$store.state.app.token }

      this.cancelPreRequest()
      this.timerRef && clearTimeout(this.timerRef) // 清空可能存在的定时器
      const that = this
      this.$vLoading.show()
      this.$reqPost(OREDR_MATCH_STOP, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          // this.isOrdering = false
          // this.isLoading = false
          this.resetAllStatus(0)
          // 弹框
          if ((isForce == 0 && that.hotList.length > 0) || (isForce == 1 && that.hotList.length > 1)) {
            this.$dialog.alert({
              message: '派单已暂停，将不会为你派下一单，但目前您有正在交易的订单，请先完成交易中的订单；',
              confirmButtonText: '我知道了'
            }).then(() => {
              that.getHots()
            })
          } else {
            that.hotList = [] // 清空前面唯一的一条信息
            this.isShowStop = true
          }
        }
      }).catch(err => {
        console.log(err)
      })
    },
    getHots () {
      if (this.isRefList) {
        return
      }
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')

      let uwcOrderStr = ''
      let uwcOrderArrAll = []
      let uwcOrderArr1 = [] // listData
      let uwcOrderArr2 = [] // onProgressList
      if (this.hotList && this.hotList.length > 0) {
        uwcOrderArr1 = this.hotList.map(item => item['uwcOrder'])
      }
      if (this.onProgressList && this.onProgressList.length > 0) {
        // 获取中间状态不为6没超时的id
        uwcOrderArr2 = this.onProgressList.filter(item => item.status != 6 && item.status != 5)
        if (uwcOrderArr2 && uwcOrderArr2.length > 0) {
          uwcOrderArr2 = uwcOrderArr2.map(item => item['uwcOrder'])
        }
      }
      uwcOrderArrAll = uwcOrderArr1.concat(uwcOrderArr2)
      if (uwcOrderArrAll && uwcOrderArrAll.length > 0) {
        uwcOrderArrAll = [...new Set(uwcOrderArrAll)] // 数组去重
        uwcOrderStr = uwcOrderArrAll.join(',')
      }
      let params = { uwcOrders: uwcOrderStr, token: tokenBbm || this.$store.state.app.token }
      this.isRefList = true
      // this.cancelPreRequest()
      // this.isLoading = true
      this.$reqPost(ORDER_QD_LIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        this.isRefList = false
        that.$vLoading.hide()
        that.nowTimeStamp = res.status.time
        // 重置几个状态
        // that.isLoading = false
        that.isShowNoGold = false // 不显示金币不足
        // that.isShowStop = false // 不显示停止
        if (res.status.code == 200) {
          this.getSellingCounts() // 判断转卖中状态
          const resData = res.data
          that.hotList = resData.listData
          that.handleData(resData)
          /* eslint-disable */

        } else if (res.status.code == 4106) { // 没有点击抢单
          this.resetAllStatus()
          if (res.data.listData && res.data.listData.length > 0) {
            that.hotList = res.data.listData
          }
          that.handleData(res.data)
        } else if (res.status.code == 4100) {
          that.isShowPopup = true
          that.showPopupText = res.data
        } else if (res.status.code == 4107 || res.status.code == 4027 || res.status.code == 4103 || res.status.code == 4104 || res.status.code == 4105) {
          let msgStr = '您的收款收款账号未填写，请前往我的-收款账号，去完善~'
          let btnStr = '去完善'
          if (res.status.code == 4107 || res.status.code == 4027) {
            msgStr = '您的收款信息正在审核中，暂时不能抢单，请前往我的-收款账号，查看审核结果；'
            btnStr = '立即查看'
          }
          if (res.status.code == 4103) {
            msgStr = '您还有未完善的收款信息，请前往我的-收款账号页面，去完善~'
            btnStr = '去完善'
          }
          if (res.status.code == 4105) {
            msgStr = '您的收款账号审核没有通过，请我的-收款账号页面， 去修改；'
            btnStr = '去修改'
          }
          this.$dialog.alert({
            message: msgStr,
            confirmButtonText: btnStr
          }).then(() => {
            this.$router.push({ path: '/user/payaccs?fromdispatch=1' })
          })
        } else if (res.status.code == 4026) { // 今日抢单限额
          that.$dialog.alert({
            title: '您今天的抢单 赚取的金币金额已经达到上限',
            message: '您明天可以继续转卖金币，继续抢单 赚取更多的现金；',
            confirmButtonText: '我知道了'
          }).then(() => {
            that.$router.replace({ path: '/user2' })
          })
        } else if (res.status.code == 4108) { // 金币余额不足
          that.$dialog.alert({
            title: '金币余额不足',
            confirmButtonText: '我知道了'
          }).then(() => {
            that.resetAllStatus()
            // 弹框
            if (that.hotList.length > 0) {
              this.isOrdering = true // 关闭开关，如果有交易中的单子是不可以暂停盈利的
              this.$dialog.alert({
                message: '派单已暂停，将不会为你派下一单，\n但目前您有正在交易的订单，\n请先完成交易中的订单；',
                confirmButtonText: '我知道了'
              }).then(() => {
                that.getHots()
              })
            } else {
              that.isShowNoGold = true // 显示金币不足
            }
          })
        } else if (res.status.code == 4109) { // 未设置下级点位
          that.resetAllStatus()
          that.isShowUnit = true
          // that.$dialog.alert({
          //   message: '请联系上级代理设置支付点位',
          //   confirmButtonText: '我知道了'
          // }).then(() => {
          //   // that.$router.replace({ path: '/user/unitset?fromdispatch=1' })
          //   that.$router.replace({ path: '/user2' })
          // })
        }
      }).catch(err => {
        console.log('err=' + err)
        // this.resetAllStatus()
        this.isRefList = false
        // this.isLoading = true
        // this.error = true
        this.resetAllStatus(0)
        this.cancelPreRequest()
        let curHref = window.location.href
        if (((this.hotList && this.hotList.length > 0) || err.toString().indexOf('timeout') > -1) && curHref.indexOf('dispatch') > -1 && curHref.indexOf('login') < 0) {
          this.getHots()
        }
      })
    },
    getSellingCounts () { // 一进来抢单页面，要先判断没有转卖中的订单
      this.reqSellingCount++
      if (this.reqSellingCount % 7 == 1) {
        this.isReqSellingCount = false
      }
      if (this.isReqSellingCount) {
        return
      }
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm || this.$store.state.app.token }
      this.isReqSellingCount = true
      // this.$vLoading.show()
      this.$reqPost(USER_SELL_ORDER_COUNT, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          const resData = res.data
          const count1 = resData['1']
          const count2 = resData['2']
          if (count1 == 0 && count2 == 0) { // 没有'转卖中'订单
            this.checkHavaStayResell()
          }
        }
      }).catch(err => {
        console.log(err)
      })
    },
    checkHavaStayResell () { // 检查是否还有可以"在线转卖"的单子
      if (this.isShowPopup) { // 抢单公告显示时不走下面检查接口
        return
      }
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm || this.$store.state.app.token }
      // this.$vLoading.show()
      this.$reqPost(OREDR_HAVE_STAYRESELL, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          if (this.isShowPopup) { // 抢单公告显示时不走下面检查接口
            return
          }
          const resData = res.data
          if (resData == 1) {
          } else if (resData == 2 || resData == 3) {
            this.$dialog.alert({
              message: '您还没有购买物品，请先找您的上级购买~',
              confirmButtonText: '我知道了'
            }).then(() => {
              that.isOrdering = false
            })
          }
        }
      }).catch(err => {
        console.log(err)
      })
    },
    handleData (resData) {
      const that = this
      if (resData.balanceInfo) {
        const balanceInfo = resData.balanceInfo
        this.balance = Vue.filter('parseNum')(balanceInfo.balance)
        this.onProgressOrderCnt = balanceInfo.onProgressOrderCnt
        this.onProgressOrderSum = balanceInfo.onProgressOrderSum
      }
      if (this.hotList.length == 0 && resData.listData.length == 0) {
        if (this.isOrdering) {
          this.isLoading = true // 加载中
          that.isShowStop = false // 显示停止
        } else {
          this.isLoading = false // 加载中
          that.isShowStop = true // 显示停止
        }
      } else {
        that.isLoading = false
        that.isShowStop = false
      }
      if (resData.onprogressMessage && resData.onprogressMessage.length > 0) {
        if (resData.onprogressMessage[0] && resData.onprogressMessage[1]) {
          that.$dialog.alert({
            message: resData.onprogressMessage[0],
            confirmButtonText: '我知道了'
          }).then(() => {
            that.$dialog.alert({
              message: resData.onprogressMessage[1],
              confirmButtonText: '我知道了'
            }).then(() => {
              // that.getMatchStatus()
              that.refreshList()
            })
          })
        } else if (resData.onprogressMessage[0]) {
          that.$dialog.alert({
            message: resData.onprogressMessage[0],
            confirmButtonText: '我知道了'
          }).then(() => {
            // that.getMatchStatus()
            that.refreshList()
          })
        }
      } else {
        that.refreshList()
      }
    },
    refreshList () {
      this.timerRef && clearTimeout(this.timerRef) // 清空可能存在的定时器
      this.timerRef = setTimeout(() => {
        let curHref = window.location.href
        // 当前页才刷新
        if (curHref.indexOf('dispatch') > -1 && curHref.indexOf('login') < 0) {
          // this.cancelPreRequest()
          this.getHots()
        } else {
          this.cancelPreRequest()
          this.timerRef && clearTimeout(this.timerRef) // 清空可能存在的定时器
        }
      }, 3 * 900)
    },
    onItemClick (item) {
      if (item.status == 8 && item.bAppealId) { // 申诉中
        return
      }
      this.cancelPreRequest()
      this.isRefList = true

      const that = this
      let titleStr = ''
      if (item.payType == 1) {
        titleStr = '支付宝支付金额：' + item.trueTradeAmount
      } else if (item.payType == 2) { // 微信
        titleStr = '收款备注码为：' + item.wxQrcodeRemark
      }
      this.$dialog.confirm({
        title: titleStr,
        message: '以上您确定您已收到付款;\n' + '您确定收到款项后，一旦确认，订单显示交易成功； 否则后果自负，与该平台无关；',
        cancelButtonText: '不小心点错了',
        confirmButtonText: '确定收款',
        cancelButtonColor: '#333333'
      }).then(() => {
        that.confirmOrderSucc(item) // 点击已收到了款
      }).catch(() => {
        // on cancel
        this.isRefList = false
        this.getMatchStatus()
      })
    },

    confirmOrderSucc (item) { // 2、点击"我已收到款"
      let reqUrl = OREDR_B_CONFIRM
      const that = this
      this.$vLoading.show()
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { uwcOrder: item.uwcOrder, token: tokenBbm || this.$store.state.app.token }

      this.cancelPreRequest()

      // this.resetAllStatus()
      this.isReqSellingCount = false
      this.$reqPost(reqUrl, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        this.$vLoading.hide()

        if (res.status.code === 200) {
          // that.isLoading = true
          that.$dialog.confirm({
            title: '恭喜您，订单撮合成功',
            message: '您可以前往“我的-我的转卖订单”查看已完成的订单',
            cancelButtonText: '暂停抢单',
            confirmButtonText: '继续抢单',
            cancelButtonColor: '#333333'
          }).then(() => {
            // on confirm
            // that.reload()
            this.isRefList = false
            this.isLoading = true
            this.startOrder(1)
            // that.getHots()
          }).catch(() => {
            // this.$router.replace('/user/sellrec?tab=3') // 查看订单
            this.isRefList = false
            this.isOrdering = false
            that.stopOrder(1)
          })
        }
      }).catch(err => {
        this.$vLoading.hide()
        console.log(err)
      })
    },
    iKnown () {
      this.$router.replace({ path: '/game' })
    }
  }
}
</script>

<style scoped lang="less">
  .setting-content {
    background-color: #f8f8f8;
    height: 93vh;
    display: flex;
    flex-direction: column;
  }

  .top-wrapper {
    padding: 0 4vw;
    background-color: #ffffff;
    z-index: 1000;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 44vw;
    width: 100vw;
    box-sizing: border-box;
    display: flex;
    justify-content: space-around;
    align-items: center;
    flex-direction: column;
  }

  .user-infos-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    margin-top: 1.6vw;
    .user-infos-left {
      display: flex;
      justify-content: flex-start;
      align-items: center;
      font-size: @font-size-lllg;
      color: #333333;
      font-weight: bold;
      .avator-img {
        width: 10vw;
        height: 10vw;
        border-radius: 6vw;
        margin-right: 2vw;
      }
    }
    .user-infos-right {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      font-size: @font-size-lg;
      color: #333333;
      font-weight: bold;
    }
  }

  .money-detail-wrapper {
    background: linear-gradient(left, #5FA4FF, #4364FC);
    -webkit-box-shadow: 1px 2px 2px #7AAFFF;
    box-shadow: 1px 2px 2px #7AAFFF;
    padding: 4vw;
    width: 100%;
    box-sizing: border-box;
    border-radius: 12px;
    margin-bottom: 1vw;
    color: #ffffff;
    font-size: @font-size-md;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: row;
    .money-detail-one {
      flex: 1;
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      .money-detail-title {
        margin-bottom: 5vw;
      }
      .num-show {
        font-weight: bold;
        font-size: @font-size-lllg;
      }
    }
    .money-detail-first {
      align-items: flex-start;
    }
    .money-detail-last {
      align-items: flex-end;
    }
  }
  .content-tip {
    margin-top: 1vh;
    color: #999999;
    font-size: @font-size-lg;
  }
  .flex-column-start {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-start;
  }
  .flex-column-center {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
  .content-wrapper {
    flex: 1;
    margin-top: 44vw;
    .matching-wrapper {
      height: 52vh;
      .img-gif {
        height: 20vh
      }
      .icon-empty-show {
        .bg-image('order-empty');
        width: 58vw;
        height: 36vw;
        background-size: cover;
      }
      .icon-empty-show1 {
        .bg-image('gold-empty');
      }
      .icon-empty-show2 {
        .bg-image('order-full');
      }
    }
    .orders-wrapper {
      .item-order-main:last-of-type {
        padding-bottom: 12vh;
      }
    }
  }
  .float-btn-wrapper {
    position: fixed;
    bottom: 10vh;
    right: 1.4vw;
    .float-num-wrapper {
      position: relative;
      height: 100%;
      width: 100%
    }
    .float-btn {
      .bg-image('float-btn');
      width: 54px;
      height: 54px;
    }
    .float-num {
      padding: 2px 6px;
      position: absolute;
      top: -4px;
      right: 3px;
      color: #ffffff;
      background-color: #E93F3F;
      border-top-left-radius: 8px;
      border-top-right-radius: 8px;
      border-bottom-right-radius: 8px;
    }
  }
  .order-notice-wrapper {
    width: 86vw;
    height: 73vw;
    box-sizing: border-box;
    background-color: transparent;
    position: relative;
    .order-notice-top {
      width: 80vw;
      margin-left: 3vw;
      border-top-left-radius: 8px;
      border-top-right-radius: 8px;
      box-sizing: border-box;
      font-size: @font-size-llg;
      font-weight: 500;
      text-align: center;
      line-height: 24px;
      padding: 20px 0 30px;
      background-color: #ffffff;
    }
    .order-notice-content {
      z-index: 100;
      position: absolute;
      bottom: 36vw;
      left: 14vw;
      right: 14vw;
      box-sizing: border-box;
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      font-size: @font-size-lg;
      line-height: 26px;
      background-color: #ffffff;
      .icon-order-notice {
        .bg-image('icon-fail');
        width: 42px;
        height: 42px;
        margin-right: 14px;
      }
    }
    .order-notice-content-fake {
      background: #ffffff;
      width: 80vw;
      height: 24vw;
      margin: 0 3vw;
      box-sizing: border-box;
    }
    .order-notice-bottom {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 42vw;
      display: block;
      background-size: 100% 100%;
      .bg-image('bg-order-notice');
    }
    .order-notice-bottom-btn {
      position: absolute;
      bottom: 3.6vw;
      left: 6vw;
      right: 6vw
    }
  }


</style>
