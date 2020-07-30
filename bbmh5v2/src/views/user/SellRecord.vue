<template>
    <div class="msg-content">
      <van-nav-bar
        title="我的转卖订单"
        :fixed='true'
        left-arrow
        @click-left="onClickLeft"
      />
      <div style="height:44px"></div>
      <span class="order-count order-count2" v-show="count2>0">{{count2}}</span>
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
                  <item-sell-record
                    v-for="(item, i) in hotList"
                    :key="i"
                    :sellType="type"
                    :msgItem="item"
                    @click2See="onClick2See(item)"
                    @click2Item="onClick2Item"
                  />
                </van-list>
                <div class="empty-show-div" v-show="!isReLoading&&hotList.length==0">
                  <div class="empty-show icon-size-common" v-show="!isReLoading&&hotList.length==0"></div>
                  <button-xbd :disabled="false" @click="onClick2Order" class="btn-xbd-default" style="display: none">去派单盈利</button-xbd>
                </div>
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
import ItemSellRecord from '@components/item-sell-record'
import ButtonXbd from '@components/button-xbd'
import { USER_SELL_ORDERLIST, USER_SELL_ORDER_COUNT } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
Vue.use(Tab).use(Tabs).use(List).use(PullRefresh)
export default {
  name: 'SellRecord',
  components: {
    ItemSellRecord,
    ButtonXbd
  },
  data () {
    return {
      tabsItem: [{ name: '交易中', type: 1 },
        { name: '历史交易', type: 2 }
      ],
      active: 0,
      type: 2,
      page: 1, // 当前页
      perPage: 10,
      isNoMoreHots: true, // 是否没有更多数据
      hotList: [],
      isReLoading: true,
      isLoading: false,
      error: false,
      count2: 0
    }
  },
  created () {
    const tabOpen = this.$route.query.tab
    if (tabOpen) {
      this.active = tabOpen - 1
      this.type = Number(tabOpen) + 1
    }
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
    onClickTab (index, title) {
      this.type = index + 2
      this.isReLoading = true
      this.page = 1
      this.error = false
      this.hotList = []
      this.getHots()
    },
    getCounts () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm }
      this.$reqPost(USER_SELL_ORDER_COUNT, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          const resData = res.data
          this.count2 = resData['2']
        }
      }).catch(err => {
        console.log(err)
      })
    },
    getHots () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { type: this.type, token: tokenBbm, page: this.page, perPage: this.perPage }
      this.$reqPost(USER_SELL_ORDERLIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
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
    onClick2Order () { // 查看
      this.$router.replace({ path: '/dispatch' })
    },
    onClick2See (item) { // 查看
      this.$router.replace({ path: '/dispatch?price=' + item.tradeAmount })
    },
    onClick2Item () { // 跳转抢单列表
      this.$router.replace({ path: '/dispatch' })
    }
  }
}
</script>

<style scoped lang="less">
.msg-content {
  /*height: 100vh;*/
  /*display: flex;*/
  /*flex-direction: column;*/
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
  .empty-show-div {
    height: 88vh;
    .empty-show {
      .bg-image('img-empty');
      height: 36vh;
      width: 60vw;
      margin: 12vh 20vw 0;
    }
    .btn-xbd-default {
      margin-top: 0;
    }
  }
}
.msg-list {
  background-color: #f3f3f3;
}
.msg-list-height {
  min-height: 86vh;
  background-color: #f3f3f3;
}
.buy-record-wrapper {
  /*height:80vh;*/
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
  text-align: center;
  z-index: 1000;
  vertical-align: middle;
  display: flex;
  justify-content: center;
  align-items: center;
}
.order-count1 {
  left: 23vw;
}
.order-count2 {
  left: 31vw;
}
.order-count3 {
  right: 13vw;
}
</style>
