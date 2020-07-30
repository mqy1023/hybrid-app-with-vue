<template>
  <van-tabbar
    v-model="active"
    active-color="#333"
    safe-area-inset-bottom
    inactive-color="#999"
    style="z-index: 1999">
    <van-tabbar-item
      v-for="(tab, index) in tabbar"
      :to="tab.path"
      :dot="isShowDot&&index==2"
      :key="index">
      <span>{{tab.name}}</span>
      <img
        slot="icon"
        slot-scope="props"
        :src="props.active ? tab.active : tab.normal"
        :info="tab.info"
      >
    </van-tabbar-item>
  </van-tabbar>
</template>

<script>
import { Tabbar, TabbarItem } from 'vant'

export default {
  data () {
    return {
      active: 0,
      isShowDot: this.$store.state.user.userInfo.pointStatus == 2,
      tabbar: [
        {
          name: '派单',
          path: '/dispatch',
          pathName: 'dispatch',
          dot: false,
          info: '',
          normal: require('./tab_dispatch_default.png'),
          active: require('./tab_dispatch_active.png')
        },
        {
          name: '玩法',
          path: '/game',
          pathName: 'game',
          dot: false,
          info: '',
          normal: require('./tab_game_default.png'),
          active: require('./tab_game_active.png')
        },
        {
          name: '我的',
          path: '/user2',
          pathName: 'user2',
          dot: false,
          info: '',
          normal: require('./tab_user_default.png'),
          active: require('./tab_user_active.png')
        }
      ]
    }
  },

  watch: {
    $route: 'changeActive',
    '$store.state.user.userInfo.pointStatus': 'activeTabDotActive'
  },

  created () {
    const toName = this.$route.name
    this.setActive(toName)
  },

  methods: {
    changeActive ({ name }) {
      this.setActive(name)
    },
    setActive (name) {
      this.tabbar.forEach((tab, i) => {
        if (tab.pathName === name) {
          this.active = i
          return false
        }
      })
    },
    activeTabDotActive (pointStatus) {
      if (pointStatus == 2) { // 下级通道的点位未确认
        this.isShowDot = true
      } else {
        this.isShowDot = false
      }
    }
  },

  components: {
    [Tabbar.name]: Tabbar,
    [TabbarItem.name]: TabbarItem
  }
}
</script>
