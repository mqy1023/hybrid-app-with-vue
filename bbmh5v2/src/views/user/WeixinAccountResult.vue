<template>
    <div class="msg-content">
      <van-nav-bar
        title="微信账号审核"
        :fixed="true"
        left-arrow
        @click-left="onClickLeft"
      >
      </van-nav-bar>
      <div style="height:44px"></div>
      <div class="weixin-account-wrapper flex-center">
        <span>当前微信支付账号是：</span>
        <span class="weixin-account">{{curweixin}}</span>
      </div>
      <van-pull-refresh class="msg-list-wrapper"
                        v-model="isReLoading"
                        @refresh="getRefeshHotList">
        <van-list v-model="isLoading"
                  :offset="100"
                  :finished-text="finishedText"
                  :error.sync="error"
                  :finished="isNoMoreHots"
                  @load="getLoadHotList"
                  error-text="请求失败，点击重新加载"
                  :immediate-check='false'
                  :class="hotList.length==0?'msg-list':'msg-list-height'"
                  v-show="hotList.length||isNoMoreHots">
          <item-account-weixin
            v-for="(item, i) in hotList"
            :key="i"
            :msgItem="item"
            @clickLeftBtn="onClickLeftBtn(item)"
            @clickRightBtn="onClickRightBtn(item)"
          />
        </van-list>
        <div class="empty-show icon-size-common" v-show="!isReLoading&&hotList.length==0"></div>
      </van-pull-refresh>
    </div>
</template>

<script>
import Vue from 'vue'
import { List, PullRefresh } from 'vant'
import ItemAccountWeixin from '@components/item-account-weixin'
import { USER_WEIXIN_LIST } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
Vue.use(List).use(PullRefresh)
export default {
  name: 'WeixinAccResult',
  components: {
    ItemAccountWeixin
  },
  data () {
    return {
      hotList: [],
      curweixin: '',
      account: '',
      name: '',
      isReLoading: false,
      isLoading: false,
      error: false,
      page: 1, // 当前页
      perPage: 8,
      isNoMoreHots: true // 是否没有更多数据
    }
  },
  mounted () {
    this.isReLoading = true
    this.curweixin = this.$route.query.curweixin // 当前使用的微信账号
    this.account = this.$route.query.account // 上次填写的真实姓名
    this.name = this.$route.query.name // 上次填写的真实姓名
    this.fromList = this.$route.query.fromlist // 从列表页来的
    if (!this.fromList) {
      this.$vLoading.show()
      this.$store.dispatch('getUserInfo')
    }
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
      // this.$router.go(-1)
      if (this.fromList) {
        this.$router.go(-1)
        return
      }
      this.$router.replace({ path: '/user/weixinacc?name=' + (this.name || '') + '&account=' + (this.account || '') })
    },
    getHots () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm, wechatAccount: this.curweixin, page: this.page, perPage: this.perPage }
      this.$reqPost(USER_WEIXIN_LIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        that.isReLoading = false
        that.isLoading = false
        if (res.status.code === 200) {
          const resData = res.data
          const lData = resData.list
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
        that.isReLoading = false
        that.isLoading = false
        that.error = true
      })
    },
    getLoadHotList () {
      if (!this.isNoMoreHots && !this.isReLoading) {
        this.isLoading = true // 请求前要先设置标识
        this.page += 1
        this.getHots()
      }
    },
    getRefeshHotList () {
      if (!this.isLoading) {
        this.isReLoading = true
        this.page = 1
        this.getHots()
      } else {
        this.isReLoading = false
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
  .weixin-account-wrapper {
    padding: 16px 0;
    background-color: #eeeeee;
    font-size: @font-size-lg;
  }
  .weixin-account {
    color: @xbd-theme-color;
  }
</style>
