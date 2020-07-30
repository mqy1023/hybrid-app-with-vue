<template>
  <div class="user-main-content">
    <van-nav-bar
      title="个人中心"
      :fixed="true"
    />
    <div class="clear-navbar-top"></div>
    <van-pull-refresh v-model="isReLoading"
                      @refresh="getRefeshHotList" style="min-height: 84vh">
      <div class="fake-div"></div>
      <div class="user-top-wrapper" @click="onClickUserCenter">
        <img slot :src="userAvatar" class="avator-img"/>
        <div class="user-top-infos">
          <div class="user-phone">{{userInfo.username || userInfo.mobile}}</div>
          <div><span>我的邀请码：</span><span>{{userInfo.inviteCode}}</span><input v-model="userInfo.inviteCode" style="display: none" /><span class="btn-copy-code" @click="onCopyInvitecode">复制邀请码</span></div>
        </div>
        <van-icon name="arrow" style="font-size:20px"/>
      </div>
      <div class="money-detail-wrapper" style="margin-top: 2vh">
        <div class="detail-top-wrapper">
          <div class="detail-top-left" @click="onClickIncome(1)">
            <span>总金币明细</span>
            <van-icon name="arrow" style="font-size:15px;margin: 0 6px"/>
          </div>
          <div class="detail-top-right" @click="onClickIncome(2)">
            <span>提现明细</span>
            <van-icon name="arrow" style="font-size:15px;margin: 0 6px"/>
          </div>
        </div>
        <div class="detail-top-detail">
          <div class="detail-detail-one">
            <div>总金币（个）</div>
            <div class="money-text">{{moneyBalance}}</div>
            <span class="detail-btn" @click="onClickDeal">去转账</span>
          </div>
          <div class="detail-detail-one">
            <span>当前盈利金额</span>
            <div class="money-text">{{moneyRestBalance}}<span style="font-size: 14px"> 元</span></div>
            <span class="detail-btn" @click="onClickTransfer">提现</span>
          </div>
        </div>
        <div class="detail-bottom-wrapper">
          <div class="detail-bottom-one">
            <div class="money-text-bottom">
              <span>{{moneyAll}}</span>
              <span class="unit-yuan">元</span>
            </div>
            <span>总盈利</span>
          </div>
          <div class="detail-bottom-one">
            <div class="money-text-bottom">
              <span>{{moneyMonth}}</span>
              <span class="unit-yuan">元</span>
            </div>
            <span>本月盈利</span>
          </div>
          <div class="detail-bottom-one">
            <div class="money-text-bottom">
              <span>{{moneyYest}}</span>
              <span class="unit-yuan">元</span>
            </div>
            <span>昨日盈利</span>
          </div>
        </div>
      </div>
      <img src="./imgs/fake-bg-bottom.png" class="fake-bg-bottom"/>
      <item-user-main :icon="itemSettingChannel.icon"
                      :name="itemSettingChannel.name"
                      :tip="tipPayPoint"
                      smallTipText="支付点位变更，请前往确认"
                      @itemClick="onItemClick(itemSettingChannel)" />
      <item-user-main :icon="itemOrderList.icon"
                      :name="itemOrderList.name"
                      @itemClick="onItemClick(itemOrderList)" />
      <item-user-main :icon="itemBank.icon"
                      :name="itemBank.name"
                      :tip="tipBank"
                      @itemClick="onItemClick(itemBank)" />
      <item-user-main :icon="itemSetting.icon"
                      :name="itemSetting.name"
                      @itemClick="onItemClick(itemSetting)" />
    </van-pull-refresh>
  </div>
</template>

<script>
import Vue from 'vue'
import { List, PullRefresh } from 'vant'
import VueClipboard from 'vue-clipboard2'
import ItemUserMain from '@components/item-user-main'
import { USER_INFO } from '@api/user'
import store from '@/store'
import { getLocalStorage } from '@utils/local-storage'
Vue.use(List).use(PullRefresh).use(VueClipboard)
export default {
  name: 'Main',
  components: {
    ItemUserMain
  },
  data () {
    return {
      isReLoading: false,
      pageCount: 1,
      itemSettingChannel: { icon: 'icon-unit-set', name: '设置好友支付通道点位', path: '/user/unitset' },
      itemOrderList: { icon: 'icon-buy-order', name: '我的购买订单', path: '/user/buyrec' },
      itemBank: { icon: 'icon-bank', name: '银行卡账号', path: '/user/bankacc' },
      itemSetting: { icon: 'icon-setting', name: '设置', path: '/user/setting' }
    }
  },
  mounted () {
    this.$vLoading.show()
    this.$store.dispatch('getUserInfo')
  },
  activated () {
    const uId = this.$store.state.user.userInfo.id
    if (!uId) {
      this.$vLoading.show()
      this.$store.dispatch('getUserInfo')
      return
    }
    if (this.pageCount % 4 == 3) { // 每5次刷新一下用户数据
      this.$vLoading.show()
      this.$store.dispatch('getUserInfo')
    }
    this.pageCount++
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
    },
    userAccountInfo () {
      return this.$store.state.user.userAccountInfo
    },
    moneyBalance () { // 金币余额
      if (!this.$store.state.user.userInfo.totalBalance) {
        return '0'
      }
      return Vue.filter('parseNum')(this.$store.state.user.userInfo.totalBalance)
    },
    moneyRestBalance () { // 剩余提现金额
      return (this.$store.state.user.userInfo.earnMoney || '0.00')
    },
    moneyAll () { // 总盈利
      return (this.$store.state.user.userInfo.allProfit || '0.00')
    },
    moneyMonth () { // 本月盈利
      return (this.$store.state.user.userInfo.monthProfit || '0.00')
    },
    moneyYest () { // 昨日盈利
      return (this.$store.state.user.userInfo.yesterdayProfit || '0.00')
    },
    tipPayPoint () {
      if (this.$store.state.user.userInfo.nextPointStatus == 2) {
        return '已完善'
      }
      return '去完善'
    },
    tipBank () {
      if (this.$store.state.user.userAccountInfo.bankStatus == 1) {
        return '已完善'
      } else if (this.$store.state.user.userAccountInfo.bankStatus == 2) {
        return '待审核'
      }
      return '去完善'
    }
  },
  methods: {
    onItemClick (item) {
      if (item.path == '/user/setting') {
        this.$router.replace({ path: item.path })
        return
      }
      this.$router.push({ path: item.path })
    },
    onClickIncome (type) {
      this.$router.push({ path: '/user/income?type=' + type })
    },
    onClickUserCenter () {
      this.$router.push({ path: '/user/center' })
    },
    getRefeshHotList () {
      this.$vLoading.show()
      this.$store.dispatch('getUserInfo')
      this.refTime && clearTimeout(this.refTime)
      this.refTime = setTimeout(() => {
        this.isReLoading = false
      }, 600)
    },
    onCopyInvitecode (e) {
      console.log(e)
      e.stopPropagation()
      this.$copyText(this.userInfo.inviteCode).then((e) => {
        this.$toast('复制成功')
      }, (e) => {
        this.$toast('复制失败')
      })
    },
    onClickDeal () {
      this.$router.push({ path: '/user/transfersell' })
    },
    onClickTransfer () {
      this.$router.push({ path: '/user/transfergold' })
    }
  },
  destroyed () {
    this.refTime && clearTimeout(this.refTime)
  }
}
</script>

<style scoped lang="less">
  .user-main-content {
    height: 100%;
    .fake-div {
      height: 2vh
    }
  }

  .user-top-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 0 4vw;
    .avator-img {
      width: 14vw;
      height: 14vw;
      border-radius: 7vw;
    }
    .user-top-infos {
      flex: 1;
      height: 14vw;
      display: flex;
      flex-direction: column;
      justify-content: space-around;
      margin-left: 3vw;
      color: #666666;
      font-size: @font-size-md;
      .btn-copy-code {
        padding: 2px 12px;
        border: 1px solid #DDDDDD;
        border-radius: 12px;
        margin-left: 12px;
        font-size: @font-size-sm;
      }
    }
    .user-phone {
      text-align: left;
      font-size: @font-size-lllg;
      color: @color333;
    }
  }

  .money-detail-wrapper {
    margin: 4vw 4vw 0;
    background: linear-gradient(left, #5FA4FF, #4364FC);
    border-radius: 12px;
    box-sizing: border-box;
    padding: 14px 0 14px;
    color: #fff;
    .detail-top-wrapper {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
      .detail-top-left {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        background: linear-gradient(left, #4364FC, #5FA4FF);
        padding: 8px 0 8px 14px;
        border-top-right-radius: 14px;
        border-bottom-right-radius: 14px;
      }
      .detail-top-right {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        background: linear-gradient(left, #5FA4FF, #4364FC);
        padding: 8px 0 8px 14px;
        border-top-left-radius: 14px;
        border-bottom-left-radius: 14px;
      }
    }
    .detail-top-detail {
      display: flex;
      flex-direction: row;
      justify-content: space-around;
      align-items: center;
      padding: 10px 14px 22px;
      font-size: @font-size-md;
      .detail-detail-one {
        flex: 1;
        .money-text {
          margin: 1.6vh 0;
          font-size: @font-size-llllg;
          font-weight: bold;
        }
        .detail-btn {
          padding: 4px 10px;
          background-color: #ffffff;
          border-radius: 12px;
          font-size: @font-size-sm;
          color: #486EFF;
        }
      }
    }
    .detail-bottom-wrapper {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 14px;
      border-top: 1px solid #C9E0FE;
      .money-text-bottom {
        font-size: @font-size-lllg;
        margin-bottom: 6px;
      }
      .detail-bottom-one {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
        flex: 1;
        .unit-yuan {
          font-size: @font-size-sm;
          margin-left: 2px;
        }
      }
    }
  }

  .fake-bg-bottom {
    width: 92vw;
    height: 3vw;
    margin-left: 4vw;
  }
</style>
