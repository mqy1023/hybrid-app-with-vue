<template>
    <div class="setting-content">
      <van-nav-bar
        title="设置好友支付通道点位"
        :fixed='true'
        left-arrow
        @click-left="onClickLeft"
      />
      <div style="height: 56px;"></div>
      <div>
        <div class="title">当前您的各支付通道点位</div>
        <div class="text"><span>支付宝支付通道点位：</span><span class="text-num">{{alipayPoint}}</span></div>
        <div class="text"><span>微信支付通道点位：</span><span class="text-num">{{wechatPoint}}</span></div>
      </div>
      <div style="margin-bottom: 5.2vh">
        <div class="title">请设置各支付通道点位</div>
        <div class="tip">提示：给好友设置点位时要比您的点位低</div>
        <div class="text text1"><span>支付宝支付通道点位：</span><input v-model.trim="nextAlipayPoint" placeholder="请输入" type="number" class="text-num"/></div>
        <div class="van-hairline--bottom"></div>
        <div class="text text1"><span>微信支付通道点位：</span><input v-model.trim="nextWechatPoint" placeholder="请输入" type="number" class="text-num"/></div>
        <div class="van-hairline--bottom"></div>
      </div>
      <button-xbd :disabled="isDisabledStart" @click="onClickSave">确定</button-xbd>

    </div>
</template>

<script>
import ButtonXbd from '@components/button-xbd'
import { USER_PAY_POINT, USER_SET_PAYPOINT, USER_CONFIRM_POINT } from '@api/user'
import { getLocalStorage, removeLocalStorage } from '@utils/local-storage'
export default {
  name: 'UnitSetting',
  data () {
    return {
      oldNextAlipayPoint: '',
      oldNextWechatPoint: '',
      alipayPoint: '',
      wechatPoint: '',
      nextPointStatus: 1, // 1未设置，2已设置
      nextAlipayPoint: '',
      nextWechatPoint: '',
      fromDispatch: 0
    }
  },
  components: {
    ButtonXbd
  },
  mounted () {
    this.getPayPoint()
    this.fromDispatch = this.$route.query.fromdispatch
  },
  computed: { // 监听子组件的disabled用于启用或禁用按钮
    isDisabledStart () {
      if (this.nextAlipayPoint && this.nextWechatPoint) { // todo open
      // if (this.nextAlipayPoint) {
        return false
      } else {
        return true
      }
    }
  },
  methods: {
    onClickLeft () {
      if (!this.fromDispatch) {
        this.$router.go(-1)
        return
      }
      const { userType } = getLocalStorage('userType')
      if (userType == 1) { // 一级代理
        this.$router.replace({ path: '/user' })
      } else {
        this.$router.replace({ path: '/user2' })
      }
    },
    getPayPoint () {
      this.$vLoading.show()
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm }
      this.$reqPost(USER_PAY_POINT, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          const dataVal = res.data
          this.oldNextAlipayPoint = dataVal.nextAlipayPoint
          this.oldNextWechatPoint = dataVal.nextWechatPoint
          this.alipayPoint = dataVal.alipayPoint
          this.wechatPoint = dataVal.wechatPoint
          this.nextPointStatus = dataVal.nextPointStatus
          this.nextAlipayPoint = dataVal.nextAlipayPoint || ''
          this.nextWechatPoint = dataVal.nextWechatPoint || ''
          if (dataVal.pointStatus == 2) { // 下级的点位更改尚未确认
            let msgStr = '支付宝支付通道点位：由' + dataVal.alipayPoint + '变更为' + dataVal.alipayNoConfirmPoint
            msgStr += '\n微信支付通道点位：由' + dataVal.wechatPoint + '变更为' + dataVal.wechatNoConfirmPoint
            that.$dialog.confirm({
              title: '您的通道支付点位发生变更：',
              message: msgStr,
              cancelButtonText: '拒绝变更',
              confirmButtonText: '同意变更',
              cancelButtonColor: '#333333'
            }).then(() => {
              that.confirmChange()
            }).catch(() => {
              that.$dialog.confirm({
                title: '请再次确认：是否同意您的上级发起的点位变更？',
                message: '(拒绝点位变更可能会造成进货困难等问题)',
                cancelButtonText: '拒绝变更',
                confirmButtonText: '同意变更',
                cancelButtonColor: '#333333'
              }).then(() => {
                that.confirmChange()
              }).catch(() => {
                // on cancel
              })
            })
          }
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
      })
    },
    confirmChange () {
      const { tokenBbm } = getLocalStorage('tokenBbm')
      const { userType } = getLocalStorage('userType')
      this.$vLoading.show()
      let params = { pointStatus: 1, token: tokenBbm }
      this.$reqPost(USER_CONFIRM_POINT, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          this.$store.dispatch('getUserInfo')
          this.$dialog.alert({
            message: '更改成功',
            confirmButtonText: '我知道了'
          }).then(() => {
            if (!this.fromDispatch) {
              this.$router.go(-1)
              return
            }
            if (userType == 1) { // 一级代理
              this.$router.replace({ path: '/user' })
            } else {
              this.$router.replace({ path: '/user2' })
            }
          })
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
      })
    },
    onClickSave () {
      if (parseFloat(this.nextAlipayPoint) > parseFloat(this.alipayPoint) || parseFloat(this.nextWechatPoint) > parseFloat(this.wechatPoint)) {
      // if (parseFloat(this.nextAlipayPoint) > parseFloat(this.alipayPoint)) {
        this.$dialog.alert({
          message: '下级代理点位不能比自己点位高',
          confirmButtonText: '我知道了'
        }).then(() => {
        })
        return
      }
      if (parseFloat(this.nextAlipayPoint) == parseFloat(this.oldNextAlipayPoint) && parseFloat(this.nextWechatPoint) == parseFloat(this.oldNextWechatPoint)) {
      // if (parseFloat(this.nextAlipayPoint) == parseFloat(this.oldNextAlipayPoint)) {
        this.$dialog.alert({
          message: '您给下级设置的点位无变化，无需设置',
          confirmButtonText: '我知道了'
        }).then(() => {
        })
        return
      }
      const nextAlipayPointStr = this.nextAlipayPoint
      const nextWechatPointStr = this.nextWechatPoint
      let errMsg = ''
      if (nextAlipayPointStr.indexOf('.') > 0 && nextAlipayPointStr.split('.')[1].length > 2) {
        errMsg = '支付宝支付通道点位最多两位小数点'
      }
      if (nextWechatPointStr.indexOf('.') > 0 && nextWechatPointStr.split('.')[1].length > 2) {
        errMsg = '微信支付通道点位最多两位小数点'
      }
      if (errMsg) {
        this.$dialog.alert({
          message: errMsg,
          confirmButtonText: '我知道了'
        }).then(() => {
        })
        return
      }
      const { userType } = getLocalStorage('userType')
      this.$dialog.confirm({
        title: '你确定设置的点位正确吗',
        message: '支付宝通道点位：' + this.nextAlipayPoint + '\n微信通道点位：' + this.nextWechatPoint,
        // message: '支付宝通道点位：' + this.nextAlipayPoint,
        cancelButtonText: '我再想想',
        confirmButtonText: '确定',
        cancelButtonColor: '#333333'
      }).then(() => {
        this.$vLoading.show()
        const { tokenBbm } = getLocalStorage('tokenBbm')
        let params = { alipayPoint: this.nextAlipayPoint, wechatPoint: this.nextWechatPoint || '', token: tokenBbm } // todo open
        // let params = { alipayPoint: this.nextAlipayPoint, wechatPoint: '', token: tokenBbm }
        this.$reqPost(USER_SET_PAYPOINT, { ...params, sign: this.$getJsaParam(params) }).then(res => {
          if (res.status.code === 200) {
            this.$store.dispatch('getUserInfo')
            this.$dialog.alert({
              message: '设置成功',
              confirmButtonText: '我知道了'
            }).then(() => {
              if (!this.fromDispatch) {
                this.$router.go(-1)
                return
              }
              if (userType == 1) { // 一级代理
                this.$router.replace({ path: '/user' })
              } else {
                this.$router.replace({ path: '/user2' })
              }
            })
          } else {
            res.status.message && this.$toast(res.status.message)
          }
        }).catch(err => {
          console.log(err)
        })
      }).catch(() => {
        // on cancel
      })
    }
  }
}
</script>

<style scoped lang="less">
  .setting-content {
    background-color: #ffffff;
    height: 100vh;
    padding: 0 4vw;
    .items-top-wrapper {
    }
  }
  .title {
    margin-top: 3vh;
    font-size: @font-size-llg;
    height: 36px;
    line-height: 36px;
    color: #222222;
  }
  .tip {
    font-size: @font-size-sm;
    margin-bottom: 1vh;
    color: #999999;
    height: 20px;
    line-height: 20px;
  }
  .text {
    font-size: @font-size-md;
    color: #666666;
    height: 34px;
    line-height: 34px;
    width: 100%;
  }
  input {
    background-color: transparent;
  }
  ::-webkit-input-placeholder {
    color: #DDDDDD;
    font-weight: 400;
  }
  .text-num {
    font-weight: bold;
  }
</style>
