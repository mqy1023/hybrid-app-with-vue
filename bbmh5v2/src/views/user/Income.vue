<template>
    <div class="msg-content">
      <van-nav-bar
        :title="type==1?'总金币明细':'提现明细'"
        :fixed="true"
        left-arrow
        @click-left="onClickLeft"
      />
      <div style="height: 46px"></div>
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
                  class="list-style van-clearfix1"
                  v-show="hotList.length||isNoMoreHots">
          <item-card-income
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
import itemCardIncome from '@components/item-card-income'
import { USER_BUY_SALE_LIST } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
Vue.use(List).use(PullRefresh)
export default {
  name: 'Income',
  components: {
    itemCardIncome
  },
  data () {
    return {
      currentPage: 1, // 当前页
      pageSize: 14,
      isNoMoreHots: true, // 是否没有更多数据
      hotList: [],
      isReLoading: false,
      isLoading: false,
      error: false,
      type: '1'
    }
  },
  mounted () {
    this.type = this.$route.query.type
    this.$vLoading.show()
    this.getHots()
  },
  computed: {
    finishedText () {
      if (this.hotList.length < 16) {
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
      let params = { token: tokenBbm, page: this.currentPage, perPage: this.pageSize, goldType: this.type }
      this.$reqPost(USER_BUY_SALE_LIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        that.isReLoading = false
        that.isLoading = false
        if (res.status.code === 200) {
          const resData = res.data
          const lData = resData.list
          const pageData = resData.pageInfo
          that.isNoMoreHots = (pageData.currentPage >= pageData.totalPage) // 是否还有更多数据
          if (Array.isArray(lData)) { // 返回数据中有数组数据
            if (that.currentPage === 1) { // 刷新重置列表数据
              that.hotList = lData
            } else { // 上拉加载更多
              that.hotList = that.hotList.concat(lData)
            }
          }
        }
      }).catch(err => {
        console.log(err)
        this.isReLoading = false
        this.isLoading = false
        this.isNoMoreHots = true
        this.error = true
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
    }
  }
}
</script>

<style scoped lang="less">
.msg-content {
  /*height: 100vh;*/
  height: 100%;
  display: flex;
  flex-direction: column;
  width: 100%;
}
.msg-list {
  /*flex: 1;*/
  height: 100%;
  background-color: #ffffff;
  overflow-y: scroll;
}
.van-pull-refresh::-webkit-scrollbar {
  display:none
}
</style>
