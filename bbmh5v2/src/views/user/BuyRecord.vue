<template>
    <div class="msg-content">
      <van-nav-bar
        title="我的购买订单"
        :fixed='true'
        left-arrow
        @click-left="onClickLeft"
      />
      <div style="height:44px"></div>
      <span class="order-count order-count1" v-show="count1>0">{{count1}}</span>
      <span class="order-count order-count2" v-show="count2>0">{{count2}}</span>
      <span class="order-count order-count3" v-show="count3>0">{{count3}}</span>
      <span class="order-count order-count4" v-show="count4>0">{{count4}}</span>
      <div class="buy-record-content">
        <van-tabs
          v-model="active"
          @click="onClickTab"
          :sticky="true"
          :offset-top="44"
          title-active-color="#4364FC"
          color="#4364FC"
          :swipe-threshold="5">
          <van-tab
            v-for="(tab, index)  in tabsItem"
            :title="tab.name"
            :key="index">
            <div class="buy-record-wrapper">
              <van-pull-refresh class="msg-list"
                                v-model="isReLoading"
                                @refresh="getRefeshHotList">
                <van-list v-model="isLoading"
                          :offset="100"
                          :finished="isNoMoreHots"
                          :finished-text="finishedText"
                          :error.sync="error"
                          error-text="请求失败，点击重新加载"
                          :immediate-check='false'
                          @load="getLoadHotList"
                          :class="hotList.length==0?'msg-list':'msg-list-height'"
                          v-show="hotList.length||isNoMoreHots">
                  <item-buy-record
                    v-for="(item, i) in hotList"
                    :key="i"
                    :buyType="type"
                    :msgItem="item"
                    @clickRightBtn="onClickRightBtn(item)"
                  />
                </van-list>
                <div class="empty-show icon-size-common" v-if="hotList.length==0"></div>
              </van-pull-refresh>
            </div>
          </van-tab>
        </van-tabs>
      </div>
    </div>
</template>

<script>
import Vue from 'vue'
import { Tab, Tabs, List, PullRefresh } from 'vant'
import ItemBuyRecord from '@components/item-buy-record'
import { USER_BUY_ORDERLIST, USER_BUY_ORDER_COUNT } from '@api/user'
import { HOME_RESELL } from '@api/home'
import { OREDR_CANCEL_COMPLAINT } from '@api/order'
import { getLocalStorage } from '@utils/local-storage'
Vue.use(Tab).use(Tabs).use(List).use(PullRefresh)
export default {
  name: 'BuyRecord',
  components: {
    ItemBuyRecord
  },
  data () {
    return {
      tabsItem: [{ name: '待支付' },
        { name: '待转卖' },
        { name: '被拒订单' },
        { name: '超时订单' },
        { name: '历史订单' }
      ],
      active: 0,
      type: 1,
      page: 1, // 当前页
      perPage: 8,
      isNoMoreHots: true, // 是否没有更多数据
      hotList: [],
      isReLoading: true,
      isLoading: false,
      error: false,
      count1: 0,
      count2: 0,
      count3: 0,
      count4: 0,
      count5: 0
      // alipayStatus: ''
    }
  },
  created () {
  },
  activated () {
    console.log('buy record activated')
    const tabOpen = this.$route.query.tab
    if (tabOpen) {
      this.active = tabOpen - 1
      this.type = tabOpen
    }
    // this.$store.dispatch('getUserInfo')
    // this.alipayStatus = this.$store.state.user.userAccountInfo.alipayStatus // 支付宝是否可用
    this.hotList = []
    this.isReLoading = true
    this.page = 1
    this.getHots()
    this.getCounts()
  },
  computed: {
    finishedText () {
      if (this.hotList.length < 11) {
        return ''
      } else {
        return '没有更多了'
      }
    }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1)
    },
    onClickTab (index) {
      this.type = index + 1
      this.isReLoading = true
      this.page = 1
      this.error = false
      this.hotList = []
      // this.$store.dispatch('getUserInfo')
      // this.alipayStatus = this.$store.state.user.userAccountInfo.alipayStatus // 支付宝是否可用
      this.getHots()
      this.getCounts()
    },
    getCounts () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm }
      this.$reqPost(USER_BUY_ORDER_COUNT, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          const resData = res.data
          this.count1 = resData['1']
          this.count2 = resData['2']
          this.count3 = resData['3']
          this.count4 = resData['4']
          this.count5 = resData['5']
        }
      }).catch(err => {
        console.log(err)
      })
    },
    getHots () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { type: this.type, token: tokenBbm, page: this.page, perPage: this.perPage }
      this.$vLoading.show()
      this.$reqPost(USER_BUY_ORDERLIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        that.isReLoading = false
        that.isLoading = false
        if (res.status.code === 200) {
          const resData = res.data
          const lData = resData.orderList
          const pageData = resData.pageInfo
          that.isNoMoreHots = (pageData.currentPage >= pageData.totalPage) // 是否还有更多数据
          if (Array.isArray(lData)) { // 返回数据中有数组数据
            if (that.page === 1) { // 刷新重置列表数据
              that.hotList = lData
            } else { // 上拉加载更多
              that.hotList = that.hotList.concat(lData)
            }
          }
        }
      }).catch(err => {
        console.log(err)
        this.hotList = []
        this.isReLoading = false
        this.isLoading = false
        this.isNoMoreHots = true
        this.error = true
      })
    },
    getRefeshHotList () {
      if (!this.isLoading) {
        this.page = 1
        this.getHots()
        // this.$store.dispatch('getUserInfo')
        // this.alipayStatus = this.$store.state.user.userAccountInfo.alipayStatus // 支付宝是否可用
      } else {
        this.isReLoading = false
      }
    },
    getLoadHotList () {
      if (!this.isNoMoreHots && !this.isReLoading) {
        this.isLoading = true // 请求前要先设置标识
        this.page += 1
        this.getHots()
      }
    },
    onClickRightBtn (item) { // 去支付，去申诉，撤销申诉；在线转卖（已废弃）
      // this.alipayStatus = this.$store.state.user.userAccountInfo.alipayStatus // 支付宝是否可用
      if (item.orderStatus == 1) { // 去支付
        this.$store.dispatch('updateOrderInfo', item)
        this.$router.push({ path: '/home/orderpay?fromlist=1&bid=' + item.businessProductId + '&oid=' + item.orderNumber })
      } else if (item.orderStatus == 3 && (!item.orderAppealStatus || item.orderAppealStatus == 0)) { // 去申诉
        this.$router.push({ path: '/order/complaint?oid=' + item.orderNumber + '&frombuy=1' })
      } else if (item.orderStatus == 7 && (!item.orderAppealStatus || item.orderAppealStatus == 0)) { // 去申诉
        this.$router.push({ path: '/order/complaint?oid=' + item.orderNumber + '&frombuy=1' })
      } else if ((item.orderStatus == 3 || item.orderStatus == 7) && item.orderAppealStatus) { // 撤销申诉
        this.$dialog.confirm({
          message: '您的问题确定解决了吗?',
          cancelButtonText: '不小心点错了',
          confirmButtonText: '问题已解决',
          cancelButtonColor: '#333333'
        }).then(() => {
          this.changeOrderStatus(item.orderNumber, 1)
        }).catch(() => {
          // on cancel
        })
      } else if (item.orderStatus == 4) { // 在线转卖（已废弃）
        // if (item.alipayStatus == 4) {
        //   this.changeOrderStatus(item.orderNumber, 2, item.businessProductResellPrice)
        // } else if (item.alipayStatus != 4) {
        //   this.$router.push({ path: '/user/alipayacc' })
        // }
      } else {
      }
    },
    changeOrderStatus (orderNo, status, businessProductResellPrice) {
      let reqUrl = OREDR_CANCEL_COMPLAINT // 撤销申诉
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      if (status === 2 || status === 3) { // 单个在线转卖，全部在线转卖
        reqUrl = HOME_RESELL
      }
      let params = { orderNum: orderNo, token: tokenBbm }
      this.$vLoading.show()
      this.$reqPost(reqUrl, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          if (status === 1) { // 撤销申诉
            this.getRefeshHotList()
          } else if (status === 2 || status === 3) { // 转卖成功
            this.$router.replace({ path: '/dispatch?price=' + businessProductResellPrice })
          }
          // else { // 超时
          //   that.$store.dispatch('updateOrderInfo', res.data)
          //   this.$router.replace({ path: '/home/orderstatus?oid=' + res.data.order_num })
          // }
        } else if (res.status.code == 5082) {
          this.$dialog.alert({
            message: '今日转卖达到上限了呦~，请明天继续转卖~',
            confirmButtonText: '我知道了'
          }).then(() => {
          })
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

<style scoped lang="less">
.msg-content {
  height: 100vh;
  /*display: flex;*/
  /*flex-direction: column;*/
  position: relative;
}
.buy-record-content {
  width: 100%;
  flex: 1;
  position: relative;
  .van-tabs {
    height: 100%;
  }
  .van-tabs__content {
    height: 100%;
  }
  .empty-show {
    .bg-image('img-empty');
    height: 68vh;
    width: 60vw;
    margin: 20vh 20vw 0;
  }
}
.msg-list {
  background-color: #f3f3f3;
}
.msg-list-height {
  min-height: 82vh;
  background-color: #f3f3f3;
}
.buy-record-wrapper {
  min-height:90vh;
  padding-bottom: 20px;
  background-color: #f3f3f3;
}
  .order-count {
    position: fixed;
    border-radius: 8px;
    border-bottom-left-radius: 0;
    top: 48px;
    width: 20px;
    height: 16px;
    color: #fff;
    background-color: @xbd-red-color;
    font-size: @font-size-xs;
    text-align: center;
    z-index: 1000;
    vertical-align: middle;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .order-count1 {
    left: 15.6vw;
  }
  .order-count2 {
    left: 35.4vw;
  }
  .order-count3 {
    right: 37.4vw;
  }
  .order-count4 {
    right: 17.2vw;
  }
  .order-count5 {
    right: 0vw;
  }
</style>
