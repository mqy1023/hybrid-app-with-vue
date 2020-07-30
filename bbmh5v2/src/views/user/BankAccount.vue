<template>
    <div class="bank-account-content">
      <van-nav-bar
        title="银行卡账号"
        :right-text="rightText"
        :fixed='true'
        left-arrow
        @click-left="onClickLeft"
        @click-right="onClickRight"
      />
      <div class="clear-navbar-top"></div>
      <div class="bank-account-top" v-show="!isHadData">
        <div class="bank-account-one van-hairline--bottom">
          <div class="left-title">持卡人姓名</div>
          <van-field
            v-model.trim="name"
            left
            maxlength="6"
            placeholder="请输入您的姓名"
            type="text"
          />
        </div>
        <div class="bank-account-one van-hairline--bottom">
          <div class="left-title">银行卡号</div>
          <van-field
            v-model.trim="cardNo"
            left
            maxlength="19"
            placeholder="请输入银行卡号"
            type="number"
          />
        </div>
        <div class="bank-account-one">
          <div class="left-title">银行名称</div>
          <van-field
            v-model.trim="bankname"
            left
            maxlength="20"
            placeholder="请输入银行名称"
            type="text"
          />
        </div>
      </div>

      <div class="infos-texts-wrapper"  v-show="isHadData">
        <div class="infos-text-top">
          <div class="infos-texts-one">
            <span>姓名：</span>
            <span>{{name}}</span>
          </div>
          <div class="infos-texts-one">
            <span>账号：</span>
            <span>{{cardNo}}</span>
          </div>
          <div class="infos-texts-one">
            <span>银行名称：</span>
            <span>{{bankname}}</span>
          </div>
        </div>
      </div>

      <div class="notice-texts-wrapper">
        <div class="notice-title">温馨提示：</div>
        <div class="one-line-text">
          <div>1.</div>
          <div>请确保银行卡已经实名认证，并填写银行卡实名认证的真实姓名</div>
        </div>
        <div class="one-line-text">
          <div>2.</div>
          <div>填写银行卡信息，方便提现</div>
        </div>
        <div class="one-line-text">
          <div>3.</div>
          <div>我们承诺不会向任何人透露您的个人信息</div>
        </div>
      </div>

      <button-xbd :disabled="false" v-show="!isHadData" @click="onClickSave" class="btn-xbd-default">安全保存</button-xbd>

    </div>
</template>

<script>
import Vue from 'vue'
import { Field } from 'vant'
import ButtonXbd from '@components/button-xbd'
import { USER_EDIT_INFOS } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
Vue.use(Field)

export default {
  name: 'BankAccount',
  components: {
    ButtonXbd
  },
  data () {
    return {
      name: '',
      cardNo: '',
      bankname: '',
      isHadData: false,
      fromHome: 0
    }
  },
  mounted () {
    this.isHadData = !!this.$store.state.user.userAccountInfo.bankUsername
    this.name = this.$store.state.user.userAccountInfo.bankUsername
    this.cardNo = this.$store.state.user.userAccountInfo.bankAccount
    this.bankname = this.$store.state.user.userAccountInfo.bankName
    this.fromHome = this.$route.query.fromhome
  },
  computed: {
    rightText () {
      if (this.isHadData) {
        return '更换银行卡'
      } else {
        return ''
      }
    }
  },
  methods: {
    onClickLeft () {
      // this.$router.go(-1)
      if (this.fromHome) {
        this.$router.replace({ path: '/user' })
      } else {
        this.$router.go(-1)
      }
    },
    onClickRight () {
      if (!this.rightText || this.rightText === '') {
        return
      }
      this.isHadData = false
      console.log('onClickRight')
    },
    onClickSave () {
      const cardNoVal = this.cardNo
      if (!this.name) {
        this.$toast('请先输入您的姓名')
        return
      }
      if (!this.cardNo) {
        this.$toast('请先输入银行卡号')
        return
      }
      if (!this.bankname) {
        this.$toast('请先输入银行名称')
        return
      }
      if (this.cardNoVal && this.cardNoVal.length > 15) {
        this.$toast('请输入正确的银行卡')
        return
      }
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { editType: 2, token: tokenBbm, bankUsername: this.name, bankAccount: this.cardNo, bankName: this.bankname }
      this.$vLoading.show()
      this.$reqPost(USER_EDIT_INFOS, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        res.status.message && this.$toast(res.status.message)
        if (res.status.code === 200) {
          that.$store.dispatch('getUserInfo')
          that.$router.back()
        }
      }).catch(err => {
        console.log(err)
      })
    }
  }
}
</script>

<style scoped lang="less">
  .bank-account-content {
    height: 100%;
    background-color: #f8f8f8;
    .bank-account-top {
      background-color: #ffffff;
      font-size: @font-size-md;
      padding-top: 4vw;
      margin-bottom: 4vw;
      .bank-account-one {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
        margin-left: 4vw;
        padding: 0.6vw 0;
        .left-title {
          width: 30vw
        }
      }
    }

    .bank-infos-wrapper {
      margin: 4vw
    }
  }

</style>
