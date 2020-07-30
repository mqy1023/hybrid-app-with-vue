<template>
  <div class="tran-complaint-content content">
    <van-nav-bar
      :title="titleText"
      right-text="提交"
      :fixed="true"
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
    />
    <div class="tran-complaint-reason clear-navbar-top">
      <div class="title-complaint" v-show="!fromc">申诉原因</div>
      <van-dropdown-menu v-show="!fromc">
        <van-dropdown-item v-model="value1" :options="option1" />
      </van-dropdown-menu>
    </div>
    <div class="reason-tip" v-show="value1==2&&!fromc">
      <span class="input-reason" >没找到符合的原因，请手动输入</span>
    </div>
    <div class="tran-complaint-desc">
      <div class="title-complaint" v-show="!fromc">描述</div>
      <div class="title-complaint" v-show="fromc">反馈原因</div>
      <div class="textarea-wrapper">
        <textarea placeholder="请输入您的描述" class="textarea-content" v-model.trim="desc" maxlength="500" />
        <span class="text-amount">{{textAreaCount}}</span>
      </div>
    </div>
    <div class="upload-wrapper">
      <div class="title-complaint">上传凭证<span style="font-size: 12px">{{imgCount}}</span></div>
      <div class="upload-imgs">
        <div class="img-box" v-for="(item, index) in imgList" :key="index">
          <img :src="item" alt="tp" @click="showImg(index)">
          <span class="img-close icon-size-common" @click="deleteImg(index)"></span>
        </div>
        <div v-show="imgList.length<3&&(isIOS||fromc)" style="position: relative" >
          <img src="../../assets/imgs/upload_img.png" alt="upload">
          <input class="fakefile" name="file" type="file" accept="image/png,image/gif,image/jpeg" @change="updateImg"/>
        </div>
        <div v-show="imgList.length<3&&(!isIOS&&!fromc)" @click="clickUpload">
          <img src="../../assets/imgs/upload_img.png" alt="upload">
        </div>
      </div>
    </div>
    <div class="phone-and-qq" v-show="fromc!=1">
      <div class="title-complaint">必填项<span>（务必填写真实的联系信息）</span><span style="color: red">*</span></div>
      <input v-model="phone" type="number" placeholder="请输入手机号" class="input-style-default" maxlength="11" oninput="if(value.length>11)value=value.slice(0,11)" />
      <input v-model="qq" type="number" placeholder="请输入QQ" class="input-style-default" maxlength="11" oninput="if(value.length>11)value=value.slice(0,11)" style="margin-top: 2vw" />
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import { OREDR_TRADE_COMPLAINT, OREDR_B_COMPL, OREDR_NOGET_MONEY } from '@api/order'
import { USER_UPLOAD_IMG } from '@api/user'
import { BOND_APPEAL } from '@api/bond'
import { OUTSIDE_C_APPEAL } from '@api/outside'
import { getLocalStorage } from '@utils/local-storage'
import { DropdownMenu, DropdownItem, ImagePreview } from 'vant'
import axios from 'axios'
Vue.use(DropdownMenu).use(DropdownItem).use(ImagePreview)

export default {
  name: 'TranComplaint',
  data () {
    return {
      value1: 0,
      option1: [
        { text: '商家一直不确认收款', value: 0 },
        { text: '时间超时，但是我已经付款成功', value: 1 },
        { text: '其他', value: 2 }
      ],
      descReason: '',
      imgList: [],
      phone: '',
      qq: '',
      desc: '',
      fromc: false,
      isIOS: this.$isIos(),
      titleText: '交易申诉'
    }
  },
  created () {
    this.oid = this.$route.query.oid // 订单id
    this.cid = this.$route.query.cid // c订单id
    this.fromc = this.$route.query.fromc // 抢单中申诉
    this.frombuy = this.$route.query.frombuy // 申诉成功跳转购买订单
    this.fromover = this.$route.query.fromover // 从超时过来的，申诉成功跳转购买订单
    this.fromrefuse = this.$route.query.fromrefuse // 从被拒过来的
    this.frombond = this.$route.query.frombond // 从保证金来的
    if (this.fromc) {
      this.option1 = [
        { text: '支付宝无法跳转', value: 1 },
        { text: '支付宝二维码失效', value: 2 },
        { text: '提示去支付宝首页扫一扫', value: 5 },
        { text: '风险链接', value: 6 },
        { text: '支付宝二维码无法识别', value: 7 },
        { text: '私人账户付款', value: 8 },
        { text: '存在账户风险提示', value: 9 },
        { text: '其他', value: 3 }
      ]
      this.value1 = 1
    }
    if (this.fromc) {
      this.titleText = '问题反馈'
    } else if (this.frombond) {
      this.titleText = '保证金申诉'
    }
    window.callByAndroidImgUrl = this.callByAndroidImgUrl
  },
  computed: {
    textAreaCount () {
      return (this.desc.length || 0) + '/500字'
    },
    imgCount () {
      return '（' + (this.imgList.length || 0) + '/3）'
    }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1) // 返回上一层
    },
    updateImg (e) {
      let file = e.target.files[0]
      this.$vLoading.show()
      let param = new FormData()
      param.append('file', file)
      let config = {
        headers: { 'Content-Type': 'multipart/form-data;boundary = ' + new Date().getTime() },
        timeout: 24000
      }
      axios.post(USER_UPLOAD_IMG, param, config)
        .then(response => {
          this.$vLoading.hide()
          const resData = response.data
          if (resData.status.code == 200) {
            const imgUrl = resData.data.relative_url
            if (this.imgList.length < 3 && imgUrl) {
              this.imgList = this.imgList.concat(imgUrl)
            }
          }
        }).catch(error => {
          this.$vLoading.hide()
          this.$toast('上传失败')
          console.log(error)
        })
    },
    onClickRight () {
      const that = this
      let reasonVal = '' // 申诉原因
      if (this.value1 === 0 || this.value1 === 1) {
        const arrMap = ['商家一直不确认收款', '时间超时，但是我已经付款成功']
        reasonVal = arrMap[this.value1]
        if (this.fromc) { // c外链问题反馈
          const arrMap1 = this.option1.filter(item => item.value == this.value1)
          reasonVal = arrMap1['text']
        }
      }
      const descVal = this.desc.trim()
      const phoneVal = this.phone.trim()
      const qqVal = this.qq.trim()
      if (!this.fromc) {
        if (!descVal) {
          this.$toast('请输入具体问题描述')
          return
        }
        if (!phoneVal) {
          this.$toast('请输入手机号')
          return
        }
        if (!this.$isMatchPhone(phoneVal)) {
          this.$toast('手机号输入不正确')
          return
        }
        if (!qqVal) {
          this.$toast('请输入QQ')
          return
        }
      } else {
        if (!descVal) {
          this.$toast('请输入具体问题描述')
          return
        }
      }
      const { tokenBbm } = getLocalStorage('tokenBbm')
      // const imgStr = 'http://image.newscat.com/ad/20190614064810_5d03432a0bce8_47.png,http://image.newscat.com/ad/20190614064810_5d03432a0bce8_47.png'
      const imgStr = this.imgList.join(',')
      this.$vLoading.show()
      let reqUrl = OREDR_TRADE_COMPLAINT
      let params = ''
      if (this.fromc) {
        reqUrl = OUTSIDE_C_APPEAL
        params = { uwcOrder: this.oid || 0, cOrder: this.cid, otherAppealReason: reasonVal || '其他', type: this.value1, remark: descVal, images: imgStr }
      } else {
        params = { orderNum: this.oid, token: tokenBbm, reason: reasonVal || '其他', detail: descVal, mobile: phoneVal, qq: qqVal, img_urls: imgStr }
      }

      // if (this.fromc) { // 我未收到款的调用
      //   let params1 = { uwcOrder: this.oid, token: tokenBbm }
      //   this.$reqPost(OREDR_NOGET_MONEY, { ...params1, sign: this.$getJsaParam(params1) }).then(res => {
      //     if (res.status.code === 200) {
      //     }
      //   })
      // }
      if (this.frombond) { // 保证金申诉
        reqUrl = BOND_APPEAL
        params['orderNum'] = this.oid
        params['imgUrls'] = imgStr
      }
      let uploadData = { ...params, sign: this.$getJsaParam(params) }
      if (this.fromc) { // 不需要sign
        uploadData = { ...params }
      }

      this.$reqPost(reqUrl, uploadData).then(res => {
        if (res.status.code === 200) {
          that.$dialog.alert({
            title: !this.fromc && '您的申诉已收到，我们的客服30分钟内联系您的~',
            message: this.fromc ? '感谢您的反馈~' : '请随时关注消息中心以及短信查看申诉进度',
            confirmButtonText: '我知道了'
          }).then(() => {
            if (this.frombuy) {
              that.$router.back() // 跳到我的转卖记录
            } else if (this.fromover && !this.frombond) {
              that.$router.replace({ path: '/user/buyrec?tab=4' }) // 跳到我的购买订单——超时订单
            } else if (this.fromrefuse) {
              that.$router.replace({ path: '/user/buyrec?tab=3' }) // 跳到我的购买订单——被拒订单
            } else if (this.frombond) {
              if (this.fromover) {
                that.$router.replace({ path: '/user/recharges?tab=4' }) // 跳到保证金明细——超时订单
              } else {
                that.$router.replace({ path: '/user/recharges?tab=3' }) // 跳到保证金明细——被拒订单
              }
            } else if (this.fromc) {
              this.$router.go(-1)
            } else {
              that.$router.replace({ path: '/user/sellrec?tab=2' }) // 跳到我的转卖记录
            }
          })
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
      })
    },
    clickUpload () {
      this.$vLoading.show()
      this.$getNativeFuncFromApp('openPickerImgUpload', 'token11111')
    },
    showImg (i) {
      this.instance = ImagePreview({
        images: this.imgList,
        startPosition: i
      })
    },
    deleteImg (i) {
      this.imgList.splice(i, 1)
    },
    callByAndroidImgUrl (imgUrl) {
      this.$vLoading.hide()
      if (this.imgList.length < 3 && imgUrl) {
        this.imgList = this.imgList.concat(imgUrl)
      }
    }
  },
  destroyed () {
    this.instance && this.instance.close()
  }
}

</script>

<style scoped lang="less">
  .tran-complaint-content {
    padding: 4vw;
  }
  .title-complaint {
    font-size: @font-size-llg;
    color: #333333;
    margin: 5vw 0 2vw;
    span {
      font-size: @font-size-sm;
    }
  }
  .reason-tip {
    color: #DDDDDD;
    font-size: @font-size-sm;
    margin: 2vw 0;
  }
  .input-reason {
    width: 92vw;
    border-radius: 6px;
    padding: 10px 0;
    box-sizing: border-box;
    color: #333
  }
  .textarea-wrapper {
    position: relative;
    height: 30vw;
    box-sizing: border-box;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 10px;
  }
  .textarea-content {
    width: 100%;
    height: 84%;
    box-sizing: border-box;
    font-size: @font-size-md;
    outline:none;
    resize:none;
    border: none
  }
  .text-amount {
    position: absolute;
    right: 10px;
    bottom: 10px;
    color: #DDDDDD;
  }
  .upload-imgs {
    display: flex;
    div {
      width: 28vw;
      height: 28vw;
      img {
        width: 100%;
        height: 100%
      }
    }
    .img-box {
      position: relative;
      margin-right: 4vw;
    }
    .img-close {
      .bg-image('icon-close');
      position: absolute;
      right: 2px;
      top: 2px
    }
  }
  .fakefile {
    position: absolute;
    top: 0;
    left:0;
    right: 0;
    bottom: 0;
    width: 28vw;
    height: 28vw;
    opacity: 0;
  }
</style>
