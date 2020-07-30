<template>
    <div class="msg-content">
      <van-nav-bar
        title="消息中心"
        :fixed="true"
        left-arrow
        @click-left="onClickLeft"
        @click-right="onClickRight"
      >
        <span class="icon-size-common icon-del" slot="right" v-if="!hotList.length==0" />
      </van-nav-bar>
      <div style="height:44px"></div>
      <van-pull-refresh class="msg-list-wrapper"
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
          <item-card-msg
            v-for="(item, i) in hotList"
            :key="i"
            :msgItem="item"
            @clickMsgItem="clickMsgItem(item)"
            @clickMsgDetail="clickMsgDetail(item)"
          />
        </van-list>
        <div class="empty-show icon-size-common" v-show="!isReLoading&&hotList.length==0"></div>
      </van-pull-refresh>
    </div>
</template>

<script>
import Vue from 'vue'
import { List, PullRefresh } from 'vant'
import itemCardMsg from '@components/item-card-msg'
import { HOME_MSG_LIST, HOME_MSG_READ, HOME_MSG_DELETE, HOME_BANK_INFOS } from '@api/home'
import { getLocalStorage } from '@utils/local-storage'
Vue.use(List).use(PullRefresh)
export default {
  name: 'Msg',
  components: {
    itemCardMsg
  },
  data () {
    return {
      currentPage: 1, // 当前页
      pageSize: 12,
      isNoMoreHots: true, // 是否没有更多数据
      hotList: [],
      isReLoading: false,
      isLoading: false,
      error: false
    }
  },
  created () {
    this.getHots()
    this.$store.dispatch('updateOrderInfo', '') // 清空原来订单信息
  },
  computed: {
    finishedText () {
      if (this.hotList.length < 20) {
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
    onClickRight () {
      const that = this
      if (this.hotList && this.hotList.length > 0) {
        this.$dialog.confirm({
          message: '您确定要清空所有消息吗？'
        }).then(() => {
          const { tokenBbm } = getLocalStorage('tokenBbm')
          let params = { token: tokenBbm }
          that.$vLoading.show()
          this.$reqPost(HOME_MSG_DELETE, { ...params, sign: this.$getJsaParam(params) }).then(res => {
            that.$vLoading.hide()
            if (res.status.code === 200) {
              // that.$store.dispatch('getUserInfo') // 为了更新用户的未读信息条数
              // that.isReLoading = true
              // that.currentPage = 1
              // that.getHots()
              that.$store.dispatch('resetUnReadMsgCount') // 未读消息为0
              that.currentPage = 1
              that.hotList = []
            }
          }).catch(err => {
            that.$vLoading.hide()
            console.log(err)
          })
        }).catch(() => {
          // on cancel
        })
      }
    },
    getHots () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm, page: this.currentPage, perPage: this.pageSize }
      this.$reqPost(HOME_MSG_LIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        that.isReLoading = false
        that.isLoading = false
        if (res.status.code === 200) {
          const resData = res.data
          const lData = resData.notifications
          const pageData = resData.pageInfo
          that.isNoMoreHots = (pageData.currentPage >= pageData.totalPage) // 是否还有更多数据

          that.isLoading = false
          if (Array.isArray(lData)) { // 返回数据中有数组数据
            if (that.currentPage === 1) { // 刷新重置列表数据
              this.isReLoading = false
              that.hotList = lData
            } else { // 上拉加载更多
              that.hotList = that.hotList.concat(lData)
            }
          }
        }
      }).catch(err => {
        console.log(err)
        that.isReLoading = false
        that.isLoading = false
        that.isNoMoreHots = true
        that.error = true
      })
    },
    getRefeshHotList () {
      if (!this.isLoading) {
        this.currentPage = 1
        this.getHots()
      } else {
        this.isReLoading = false
      }
    },
    getLoadHotList () {
      if (!this.isNoMoreHots && !this.isReLoading) {
        this.isLoading = true // 请求前要先设置标识
        this.currentPage += 1
        this.getHots()
      }
    },
    /* eslint-disable */
    clickMsgDetail (item) {
      this.clickMsgItem(item)
      if (item.openType == 2) { // 跳转到用户B的抢单页面
        this.$router.push({ path: '/dispatch' })
      } else if ((item.openType == 3 || item.openType == 4 || item.openType == 7) && item.jumpUrl) { // 3跳到用户B支付商家A的订单支付页面
        // const jumpParams = item.jumpUrl.split("&")
        // const jArr1 = jumpParams[0]
        // const jArr2 = jumpParams[1]
        // let bid, oid
        // if (jArr1.indexOf('order') > -1) {
        //   oid = jArr1.slice(jArr1.indexOf("=") + 1)
        //   bid = jArr2.slice(jArr2.indexOf("=") + 1)
        // } else {
        //   oid = jArr2.slice(jArr2.indexOf("=") + 1)
        //   bid = jArr1.slice(jArr1.indexOf("=") + 1)
        // }
        const jUrl = item.jumpUrl
        if (jUrl.indexOf("home/orderpay") > -1) {
          this.getBankInfo(jUrl.substring(jUrl.indexOf("oid=") + 4), jUrl)
          return
        }
        this.$router.push({ path: item.jumpUrl })
      }
    },
    clickMsgItem (item) {
      if (item.isRead == 1) { // 已读消息
        return
      }
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm, id: item.id }
      this.$reqPost(HOME_MSG_READ, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          that.$store.dispatch('decreaseUserUnReadMsgCount') // 未读消息减1
          that.isReLoading = true
          that.currentPage = 1
          that.getHots()
        }
      }).catch(err => {
        console.log(err)
      })
    },
    getBankInfo (oid, jUrl) {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { orderNumber: oid, token: tokenBbm }
      this.$vLoading.show()
      this.$reqPost(HOME_BANK_INFOS, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        that.nowTimeStamp = res.status.time
        if (res.status.code === 200) {
          that.orderStatus = res.data.orderStatus
          if (that.orderStatus == 1) {
            this.$router.push({ path: jUrl })
          } else if (that.orderStatus == 2 || that.orderStatus == 4) {
            that.$router.replace({ path: '/user/buyrec?tab=2' }) // 跳到我的购买订单——待转卖
          } else if (that.orderStatus == 7) {
            that.$router.replace({ path: '/user/buyrec?tab=3' }) // 跳到我的购买订单——被拒订单
          } else if (that.orderStatus == 3) {
            // this.$store.dispatch('updateOrderInfo', res.data)
            // that.$router.replace({ path: '/home/orderstatus?oid=' + oid })
            // that.$router.replace({ path: '/user/sellrec' }) // 跳到我的转卖订单
            // 跳转超时页面
            this.$store.dispatch('updateOrderInfo', res.data)
            that.$router.replace({ path: '/home/orderstatus?oid=' + oid })
          } else if (that.orderStatus == 5) {
            that.$router.replace({ path: '/user/sellrec' }) // 跳到我的转卖订单
            // that.$store.dispatch('updateOrderInfo', res.data)
            // this.$router.replace({ path: '/home/sell?oid=' + oid + '&val=' + res.data.productDetailValue })
          } else if (!that.orderTimeLimit) {
            let msgStr = '该笔订单已超时'
            if (that.orderStatus == 6) {
              msgStr = '该笔订单已取消'
            }
            that.$dialog.alert({
              message: msgStr,
              confirmButtonText: '我知道了'
            }).then(() => {
            })
          }
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
  /*height: 100vh;*/
  width: 100%;
  display: flex;
  flex-direction: column;
}
.msg-list {
  background-color: #f3f3f3;
}
.msg-list-height {
  min-height: 90vh;
  background-color: #f3f3f3;
}
.msg-list-wrapper {
  min-height:94vh;
  background-color: #f3f3f3;
}
.icon-del {
  .bg-image('icon-del');
  vertical-align: middle;
}
</style>
