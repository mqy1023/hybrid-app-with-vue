<template>
    <div class="user-center-content">
      <van-nav-bar
        title="个人中心"
        :fixed='true'
        left-arrow
        @click-left="onClickLeft"
      />
      <div style="height:72px;background-color: #ffffff"/>
        <van-cell title="头像" is-link @click="onClickAvatar" style="align-items: center;padding: 0 0.42667rem" v-show="!isIOS" >
          <img
            slot
            :src="avatar||require('./imgs/avatar.png')"
            style="width: 8vw;height:8vw;border-radius: 4vw"
          />
        </van-cell>
        <van-cell title="头像" is-link style="position: relative;align-items: center;padding: 0 0.42667rem" v-show="isIOS" >
          <img
                  slot
                  :src="avatar||require('./imgs/avatar.png')"
                  style="width: 8vw;height:8vw;border-radius: 4vw"
          />
          <input class="fakefile" name="file" type="file" accept="image/png,image/gif,image/jpeg" @change="updateImg"/>
        </van-cell>
        <van-cell title="昵称" :value="nickname||'未填写'" is-link @click="onClickNickName(true)" />
        <van-cell title="性别" :value="genderShow" is-link @click="onClickSex(true)"  />
      <div style="height:24px"/>

      <button-xbd :disabled="false" @click="onClickSave" class="btn-xbd-default">保存</button-xbd>

      <van-action-sheet
        v-model="isShowSex"
        :actions="actions"
        @select="onSelectSex"
        cancel-text="取消"
        @cancel="onClickSex(false)"
      />
      <van-popup
        v-model="isShowNickName"
        round
        position="bottom"
        :style="{ height: '36%' }"
      >
        <div class="popup-nickname-top van-hairline--bottom">
          <div style="color: #999" @click="onClickNickName(false)">取消</div>
          <div @click="onClickOkNickName">确定</div>
        </div>
        <van-field
          v-model.trim="nicknameTemp"
          center
          maxlength="14"
          placeholder="请输入您的昵称"
          type="text"
        />
      </van-popup>
    </div>
</template>

<script>
import Vue from 'vue'
import { ActionSheet, Popup, Field } from 'vant'
import ButtonXbd from '@components/button-xbd'
import { USER_EDIT_INFOS, USER_UPLOAD_IMG } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
import axios from 'axios'
Vue.use(ActionSheet).use(Popup).use(Field)

export default {
  name: 'user-center',
  components: {
    ButtonXbd
  },
  data () {
    return {
      avatar: '',
      nickname: '',
      gender: '',
      nicknameTemp: '',
      isShowSex: false,
      isShowNickName: false,
      actions: [
        { name: '男', value: 2 },
        { name: '女', value: 3 }
      ],
      isIOS: this.$isIos()
    }
  },
  created () {
    this.nickname = this.$store.state.user.userInfo.username
    this.gender = this.$store.state.user.userInfo.gender
    this.avatar = this.$store.state.user.userInfo.avatar
    window.callByAndroidImgUrl = this.callByAndroidImgUrl
  },
  computed: {
    userAvatar () {
      const avatorUrl = this.avatar
      if (avatorUrl) {
        return avatorUrl
      }
      return require('./imgs/avatar.png')
    },
    genderShow () {
      const genderList = ['未填写', '男', '女']
      return genderList[this.gender - 1]
    }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1)
    },
    updateImg (e) {
      let file = e.target.files[0]
      this.$vLoading.show()
      let param = new FormData()
      param.append('file', file)
      let config = {
        headers: { 'Content-Type': 'multipart/form-data;boundary = ' + new Date().getTime() }
      }
      axios.post(USER_UPLOAD_IMG, param, config)
        .then(response => {
          this.$vLoading.hide()
          const resData = response.data
          if (resData.status.code == 200) {
            const imgUrl = resData.data.relative_url
            if (imgUrl) {
              this.avatar = imgUrl
            }
          }
        })
    },
    onClickAvatar () {
      const that = this
      if (!this.avatar) {
        this.$vLoading.show()
        this.$getNativeFuncFromApp('openPickerImgUpload', 'token11111')
        return
      }
      this.$dialog.confirm({
        title: '更换头像？',
        cancelButtonText: '不小心点错了',
        confirmButtonText: '确认更换',
        cancelButtonColor: '#333333'
      }).then(() => {
        // on confirm
        that.$vLoading.show()
        that.$getNativeFuncFromApp('openPickerImgUpload', 'token11111')
        // that.avatar = 'https://bangbangmai-prod.s3.ap-northeast-2.amazonaws.com/appeal/20190923164207_5d88855f83597_56.jpeg'
        // that.$vLoading.hide()
      }).catch(() => {
        // on cancel
      })
    },
    callByAndroidImgUrl (imgUrl) {
      this.$vLoading.hide()
      if (imgUrl) {
        this.avatar = imgUrl
      }
    },
    onClickSex (status) {
      this.isShowSex = status
    },
    onSelectSex (item) {
      this.isShowSex = false
      this.gender = item.value
    },
    onClickNickName (status) {
      this.isShowNickName = status
      if (status) {
        this.nicknameTemp = this.nickname
      } else {
        this.nicknameTemp = '' // 重置
      }
    },
    onClickOkNickName () {
      this.isShowNickName = false
      if (this.nicknameTemp) {
        this.nickname = this.nicknameTemp
      }
      console.log('onClickOkNickName')
    },
    onClickSave () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      if (!this.nickname) {
        this.$toast('请先填写昵称')
        return
      }
      if (!this.gender) {
        this.$toast('请先选择性别')
        return
      }
      let params = { editType: 1, token: tokenBbm, username: this.nickname, gender: this.gender }
      if (this.avatar && this.avatar.indexOf('http') > -1) {
        params = { ...params, avatar: this.avatar }
      }
      this.$vLoading.show()
      this.$reqPost(USER_EDIT_INFOS, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        res.status.message && this.$toast(res.status.message)
        if (res.status.code === 200) {
          that.$store.dispatch('getUserInfo')
        }
      }).catch(err => {
        console.log(err)
      })
    }
  }
}
</script>

<style scoped lang="less">
  .user-center-content {
    background-color: #f8f8f8;
    height: 100%;
  }
  .link-text {
    color: @xbd-theme-color;
  }
  .popup-nickname-top {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 14px;
    font-size: @font-size-md;
    color: @xbd-theme-color;
  }
  .fakefile {
    position: absolute;
    top: 0;
    left:0;
    right: 0;
    bottom: 0;
    width: 80vw;
    height: 8vw;
    opacity: 0;
  }
</style>
