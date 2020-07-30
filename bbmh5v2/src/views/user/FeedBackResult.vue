<template>
  <div class="msg-content">
    <van-nav-bar
      title="反馈结果"
      :fixed="true"
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
    >
      <span class="icon-size-common icon-del" slot="right" v-if="!hotList.length==0" />
    </van-nav-bar>
    <div style="height: 44px;width: 100%"></div>
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
        <item-card-feedback
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
import ItemCardFeedback from '@components/item-card-feedback'
import { USER_FEEDBACK_LIST, USER_FEEDBACK_DELETE } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
Vue.use(List).use(PullRefresh)
export default {
  name: 'FeedBackResult',
  components: {
    ItemCardFeedback
  },
  data () {
    return {
      page: 1, // 当前页
      perPage: 10,
      isNoMoreHots: true, // 是否没有更多数据
      hotList: [],
      isReLoading: false,
      isLoading: false,
      error: false
    }
  },
  created () {
    this.getHots()
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
    onClickRight () {
      const that = this
      if (this.hotList && this.hotList.length > 0) {
        this.$dialog.confirm({
          message: '您确定要清空记录吗？'
        }).then(() => {
          const { tokenBbm } = getLocalStorage('tokenBbm')
          let params = { token: tokenBbm }
          this.$vLoading.show()
          this.$reqPost(USER_FEEDBACK_DELETE, { ...params, sign: this.$getJsaParam(params) }).then(res => {
            if (res.status.code === 200) {
              that.$router.back()
            }
          }).catch(err => {
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
      let params = { token: tokenBbm, page: this.page, perPage: this.perPage }
      this.$vLoading.show()
      this.$reqPost(USER_FEEDBACK_LIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          const resData = res.data
          const lData = resData.feedbacks
          const pageData = resData.pageInfo
          that.isNoMoreHots = (pageData.currentPage >= pageData.totalPage) // 是否还有更多数据

          that.isLoading = false
          that.isReLoading = false
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
        that.isNoMoreHots = true
        that.error = true
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
    }
  }
}
</script>

<style scoped lang="less">
  .msg-content {
    /*height: 100vh;*/
    display: flex;
    flex-direction: column;
    background-color: #f3f3f3;
  }
  .msg-list {
    /*flex: 1;*/
    background-color: #f3f3f3;
    height: 94vh;
    overflow-y: scroll;
  }
  .icon-del {
    .bg-image('icon-del');
    vertical-align: middle;
  }
</style>
