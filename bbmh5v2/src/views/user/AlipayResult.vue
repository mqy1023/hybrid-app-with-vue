<template>
    <div class="msg-content">
      <van-nav-bar
        title="支付宝账号审核"
        :fixed="true"
        left-arrow
        @click-left="onClickLeft"
      >
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
          <item-one-alipay
            v-for="(item, i) in hotList"
            :key="i"
            :msgItem="item"
          />
        </van-list>
        <div class="empty-show icon-size-common" v-show="!isReLoading&&hotList.length==0"></div>
      </van-pull-refresh>
    </div>
</template>

<script>
import Vue from 'vue'
import { List, PullRefresh } from 'vant'
import ItemOneAlipay from '@components/item-one-alipay'
import { USER_ALIPAY_LIST } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
Vue.use(List).use(PullRefresh)
export default {
  name: 'AlipayResult',
  components: {
    ItemOneAlipay
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
  mounted () {
    this.isReLoading = true
    this.getHots()
  },
  computed: {
    finishedText () {
      if (this.hotList.length < 12) {
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
    getHots () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm, page: this.currentPage, perPage: this.pageSize }
      this.$reqPost(USER_ALIPAY_LIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        that.isReLoading = false
        that.isLoading = false
        if (res.status.code === 200) {
          const resData = res.data
          const lData = resData.list
          const pageData = resData.pageInfo
          that.isNoMoreHots = (pageData.currentPage >= pageData.totalPage) // 是否还有更多数据

          that.isLoading = false
          if (Array.isArray(lData)) { // 返回数据中有数组数据
            if (that.currentPage === 1) { // 刷新重置列表数据
              this.isReLoading = false
              that.hotList = lData // todo remove
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
        this.isReLoading = true
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
