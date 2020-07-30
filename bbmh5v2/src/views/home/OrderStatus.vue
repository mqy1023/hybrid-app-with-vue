<template>
  <div class="home-order-pay">
    <van-nav-bar
      title="订单支付"
      :fixed="true"
      left-arrow
      @click-left="onClickLeft"
    />
    <div class="order-top-wrapper clear-navbar-top">
      <van-icon name="warning"/>
      <div class="order-text-status">订单超时交易失败</div>
    </div>
    <div class="order-details-wrapper">
      <div class="detail-title">订单详情</div>
      <div class="one-line-detail">
        <span class="detail-left">购买金币(个)：</span>
        <span>{{(orderInfo.orderAllValue&&orderInfo.orderAllValue.slice(0, -3))||orderInfo.orderAmount&&orderInfo.orderAmount.slice(0, -3)}}</span>
      </div>
      <div class="one-line-detail">
        <span class="detail-left">总金额(元)：</span>
        <span>￥{{orderInfo.orderAllValue || orderInfo.orderAmount}}</span>
      </div>
      <div class="one-line-detail">
        <span class="detail-left">订单号：</span>
        <span>{{orderInfo.orderNum||orderInfo.order_num}}</span>
      </div>
      <div class="one-line-detail">
        <span class="detail-left">订单交易时间：</span>
        <span>{{orderInfo.orderCreateTime||orderInfo.order_create_time}}</span>
      </div>
    </div>

    <div class="commit-btn-wrap" style="">
      <button-xbd :disabled="false" @click="onClickComplain" class="btn-cancel">
        已成功支付？<span>去申诉</span>
      </button-xbd>
    </div>

    <div class="commit-btn-wrap" style="display: none">
      <button-xbd :disabled="false" @click="onClickCancel" class="btn-success">撤销申诉</button-xbd>
    </div>

    <div class="notice-texts-wrapper clearfix" style="">
      <div class="notice-title">温馨提示：</div>
      <div class="one-line-text">
        <div style="height: 18px;line-height: 18px">我们申诉时间<span>有效时间三个小时</span>您的申诉时间从下午<span>{{orderInfo.appealFrom||orderInfo.appeal_from}}到{{orderInfo.appealTo||orderInfo.appeal_to}}</span>，你需要在申诉时间内去申诉上传您的支付成功的凭证，超过申诉时间订单关闭；</div>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import ButtonXbd from '@components/button-xbd'
import { HOME_TRADE_STATUS, HOME_BANK_INFOS } from '@api/home'
import { getLocalStorage } from '@utils/local-storage'

export default {
  name: 'OrderStatus',
  components: {
    ButtonXbd
  },
  data () {
    return {
      orderStatus: '',
      orderInfo: ''
    }
  },
  mounted () {
    this.oid = this.$route.query.oid // 订单id
    this.orderInfo = this.$store.state.order.orderInfo
    console.log('this.orderInfo', this.orderInfo)
    // this.getBankInfo()
  },
  computed: {
    // priceShow () {
    //   return '¥' + this.price + '/个'
    // }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1)
    },
    onClickSucc () {
      console.log('onClickSucc')
    },
    onClickCancel () {
      console.log('cancelTradeClick')
      this.$dialog.confirm({
        title: '您的问题确定解决了吗？',
        message: '你确定问题解决后，该笔订单将成功交易哦',
        cancelButtonText: '不小心点错了',
        confirmButtonText: '问题已解决',
        cancelButtonColor: '#333333'
      }).then(() => {
        // on confirm
      }).catch(() => {
        // on cancel
      })
    },
    onClickComplain () {
      console.log('confirmTradeClick')
      this.$dialog.confirm({
        message: '您确认要申诉吗？',
        cancelButtonText: '我再想想',
        confirmButtonText: '确定申诉',
        cancelButtonColor: '#333333'
      }).then(() => {
        // on confirm
        this.$router.replace({ path: '/order/complaint?oid=' + this.oid + '&fromover=1' })
      }).catch(() => {
        // on cancel
      })
    },
    getBankInfo () {
      if (!this.oid) {
        return
      }
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { orderNumber: this.oid, token: tokenBbm }
      this.$vLoading.show()
      this.$reqPost(HOME_BANK_INFOS, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          that.orderInfo = res.data
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
      })
    }
  }
}
</script>

<style scoped lang="less">
  @import '../../assets/less/home.less';
  .commit-btn-wrap {
    width: 88vw;
    display: flex;
    margin: 6vw;
  }

  .commit-btn-wrap /deep/ .button-xbd {
    /*width: 40vw;*/
    width: 88vw;
  }

  .commit-btn-wrap /deep/ .btn-cancel .button-xbd {
    background: #fff;
    border: 1px solid #DDDDDD;
    color: @color999;
    span {
      color: @xbd-theme-color;
    }
  }

  .van-icon-warning {
    font-size: 36px;
    color: @xbd-red-color;
    margin-bottom: 2vw;
    padding: 5vw 0 2vw;
  }

</style>
