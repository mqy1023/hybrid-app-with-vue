<template>
  <div class="bank-account-content">
    <van-nav-bar
      title="微信收款账号"
      :right-text="rightText"
      :fixed='true'
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
    />
    <div class="clear-navbar-top"></div>
    <div class="cur-weixin-wrapper" v-show="curWechatAccount">
      <div>当前使用中的微信支付账号是：</div>
      <div class="cur-weixin-account">{{curWechatAccount || ''}}</div>
    </div>
    <div class="bank-account-top">
      <div class="bank-account-one van-hairline--bottom">
        <div class="left-title"><span class="red-span">*</span>真实姓名</div>
        <van-field
          v-model.trim="name"
          left
          maxlength="6"
          placeholder="请输入微信实名认证的真实姓名"
          type="text"
        />
      </div>
      <div class="bank-account-one van-hairline--bottom">
        <div class="left-title"><span class="red-span">*</span>微信账号</div>
        <van-field
          v-model.trim="account"
          left
          maxlength="20"
          placeholder="请输入微信账号"
          type="text"
        />
      </div>
      <div class="bank-account-one van-hairline--bottom">
        <div class="left-title" style="width: 15vw"><span class="red-span">*</span>金额</div>
        <van-dropdown-menu :overlay="true" style="flex: 1">
          <van-dropdown-item :overlay="true" v-model="value" :options="option"
                             style="left: 7.5vw;right: 7.5vw"/>
        </van-dropdown-menu>
        <div class="" style="width: 20vw">元</div>
      </div>
      <div class="bank-account-one van-hairline--bottom">
        <div class="left-title">备注码</div>
        <input type="text" readonly v-model="wechatMemoCode"  style="display: none"/>
        <van-field
          v-model.trim="wechatMemoCode"
          left
          disabled
          maxlength="20"
          type="text"
        />
        <div class="copy-btn" @click="onClickCopy">复制</div>
      </div>
    </div>
    <div class="weixin-img-wrapper">
      <div class="weixin-title"><span class="red-span">*</span>上传带有<span class="red-span" style="font-weight: bold">备注码</span>的微信收款二维码</div>
      <div class="upload-imgs">
        <div class="img-box" v-show="imgSrc" style="position: relative">
          <img :src="imgSrc" alt="tp">
          <span class="img-del icon-size-common" @click="delAliapyCode" v-show="imgSrc"/>
        </div>
        <div v-show="(!imgSrc)&&isIOS" style="position: relative" >
          <img src="../../assets/imgs/upload_img.png" alt="upload">
          <input class="fakefile" name="file" type="file" accept="image/png,image/gif,image/jpeg" @change="updateImg"/>
        </div>
        <div v-show="(!imgSrc)&&!isIOS" @click="clickUpload">
          <img src="../../assets/imgs/upload_img.png" alt="upload">
        </div>
      </div>
    </div>
    <div class="notice-texts-wrapper" style="margin-top: 2vh;padding-bottom: 11vh">
      <div class="notice-title">温馨提示：</div>
      <div class="one-line-text">
        <div>1.</div>
        <div>请确保微信已经实名认证，并填写微信实名认证的真实姓名<span class="span-red">（不要填写微信昵称）</span></div>
      </div>
      <div class="one-line-text">
        <div>2.</div>
        <div>复制备注码，截图上传微信收款码时，在微信备注处粘贴备注码，方便买家给您转账</div>
      </div>
      <div class="one-line-text">
        <div>3.</div>
        <div>我们承诺不会向任何人透露您的个人信息</div>
      </div>
    </div>
    <div class="all-btns-wrapper">
      <div class="two-btn-wrap" v-show="curWechatAccount">
        <div class="btn-left btn-big"  @click="onClickCancel">查看当前使用中<br/>账号审核状态</div>
        <button-xbd :disabled="false" @click="onClickSave" class="btn-right">安全保存</button-xbd>
      </div>
      <div class="save-btn-wrap" v-show="!curWechatAccount">
        <button-xbd :disabled="false" @click="onClickSave" class="btn-save">安全保存</button-xbd>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import { Field, DropdownMenu, DropdownItem } from 'vant'
import VueClipboard from 'vue-clipboard2'
import ButtonXbd from '@components/button-xbd'
import { USER_UPLOAD_IMG, USER_EDIT_WECHAT, USER_CURR_WEIXIN, USER_WEIXIN_MEMO, USER_WEIXIN_COUNT_LIST } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
import axios from 'axios'
import { isEmptyStr } from '@utils/common'

Vue.use(Field).use(DropdownMenu).use(DropdownItem).use(VueClipboard)

export default {
  name: 'WeixinAccount',
  components: {
    ButtonXbd
  },
  data () {
    return {
      name: '',
      account: '',
      isHadOrg: true,
      isIOS: this.$isIos(),
      imgSrc: '',
      value: 0,
      option: [],
      curWechatAccount: '',
      wechatMemoCode: '',
      isCanJump: false
    }
  },
  mounted () {
    this.account = this.$route.query.account // 上次填写的
    this.name = this.$route.query.name // 上次填写的
    this.isHadOrg = !!(this.$store.state.user.userAccountInfo.wechatUsername)
    // this.name = this.$store.state.user.userAccountInfo.wechatUsername
    if (this.$store.state.user.userAccountInfo.wechatQrCode) {
      // this.imgSrc = this.$store.state.user.userAccountInfo.wechatQrCode
    }
    window.callByAndroidImgUrl = this.callByAndroidImgUrl
    this.getUserWechatAccount()
    this.getWechatMemoCode()
    this.getAmountList()
  },
  destroyed () {
    this.timeOut && clearTimeout(this.timeOut)
    this.fakeTimeOut && clearTimeout(this.fakeTimeOut)
  },
  computed: {
    rightText () {
      // if (this.curWechatAccount) {
      if (this.$store.state.user.userAccountInfo.wechatStatus == 4) {
        return ''
      }
      return '切换微信账号'
      // } else {
      //   return ''
      // }
    }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1)
    },
    onClickRight () {
      if (!this.rightText || this.rightText === '' || !this.isCanJump) {
        return
      }
      // if (!this.isCanJump) {
      //   return
      // }
      this.$router.replace({ path: '/user/weixinrec?curweixin=' + (this.curWechatAccount || '') + '&account=' + (this.account || '') + '&name=' + (this.name || '') })
    },
    clickUpload () {
      this.$vLoading.show()
      this.$getNativeFuncFromApp('openPickerImgUpload', 'token11111')
    },
    callByAndroidImgUrl (imgUrl) {
      this.imgSrc = imgUrl
      this.$vLoading.show()
      this.fakeTimeOut = setTimeout(() => { // android效果上做优化
        this.$vLoading.hide()
      }, 3200)
    },
    onClickCopy () {
      this.$copyText(this.wechatMemoCode).then((e) => {
        this.$toast('复制成功')
      }, (e) => {
        this.$toast('复制失败')
      })
    },
    delAliapyCode (e) {
      e.stopPropagation()
      this.$dialog.confirm({
        message: '删除收款码图片，需要重新上传哦~',
        cancelButtonText: '不小心点错了',
        confirmButtonText: '确认',
        cancelButtonColor: '#333333'
      }).then(() => {
        this.imgSrc = ''
      }).catch(() => {
      })
    },
    postImg (file) { // 上传操作
      this.$vLoading.show()
      let param = new FormData()
      param.append('file', file)
      // param.append('type', 'wxQrcode')
      let config = {
        headers: { 'Content-Type': 'multipart/form-data;boundary = ' + new Date().getTime() },
        timeout: 24000
      }
      axios.post(USER_UPLOAD_IMG, param, config)
        .then(response => {
          this.$vLoading.hide()
          // el.target.value = '' // 重置file
          const resData = response.data
          if (resData.status.code == 200) {
            this.imgSrc = resData.data.relative_url
          }
        }).catch(error => {
          this.$vLoading.hide()
          this.$toast('上传失败')
          console.log(error)
        })
    },
    updateImg (el) {
      let file = el.target.files[0]
      if (file.size <= 0.2 * 1024 * 1024) { // 500k以下
        this.postImg(file)
      } else {
        this.compressImg(file)
      }
      el.target.value = '' // 重置file
    },
    compressImg (file) {
      let reader = new FileReader()
      let image = new Image()
      let that = this
      reader.readAsDataURL(file)
      reader.onload = function () {
        file.src = this.result
        image.onload = function () {
          let width = image.width
          let height = image.height
          file.width = width
          file.height = height
          that.imgL = file.src // 页面上显示所选择的图片
        }
        console.log(file)
        image.src = file.src // 页面上显示所选择的图片
        let qualityVAal = 0.24
        let fileOneMSize = file.size / (1024 * 1024)
        if (fileOneMSize >= 1 && fileOneMSize <= 2) { // 判断图片的大小，超过1M 进行压缩
          qualityVAal = 0.12
        } else if (fileOneMSize > 2) {
          qualityVAal = 0.05
        }
        that.imgCompress(file, { quality: qualityVAal })
        // } else {
        //   that.partitionBase = file.src.split(',')[1] // 这里是因为后台需要 base64和图片type类型两个数据，所以进行了处理
        //   that.imgType = '.' + file.type.split('/')[1]
        // }
      }
    },
    imgCompress (path, obj) { // path是指上传的图片，obj是压缩的品质，越低越模糊
      let _this = this // 这里的this 是把vue的实例对象指向改变为_this
      var img = new Image()
      img.src = path.src
      img.onload = function () {
        var that = this // 这里的this 是把img的对象指向改变为that
        // 默认按比例压缩
        let w = that.width
        let h = that.height
        let scale = w / h
        w = obj.width || w
        h = obj.height || (w / scale)
        var quality = 0.5 // 默认图片质量为0.5
        // 生成canvas
        var canvas = document.createElement('canvas')
        var ctx = canvas.getContext('2d')
        // 创建属性节点
        var anw = document.createAttribute('width')
        anw.nodeValue = w
        var anh = document.createAttribute('height')
        anh.nodeValue = h
        canvas.setAttributeNode(anw)
        canvas.setAttributeNode(anh)
        ctx.drawImage(that, 0, 0, w, h)
        // 图像质量
        if (obj.quality && obj.quality <= 1 && obj.quality > 0) {
          quality = obj.quality
        }
        // quality值越小，所绘制出的图像越模糊
        var base64 = canvas.toDataURL('image/jpeg', quality)
        // 回调函数返回base64的值
        var urlFile = _this.convertBase64UrlToBlob(base64) // 这个地方的处理是为了把压缩的base64转化为对象，获得压缩后图片的大小size，方便对压缩后的图片再次进行判断；
        _this.postImg(urlFile)
        // console.log(urlFile)
        // if (urlFile.size / 1024 > 1025) {
        //   // _this.$msgbox("图片过大，请重新上传图片")
        // } else {
        //   _this.partitionBase = base64.split(',')[1]
        //   _this.imgType = '.' + urlFile.type.split('/')[1]
        // }
      }
    },
    convertBase64UrlToBlob (urlData) { // 将base64码转化为file（Blob）, 此处函数对压缩后的base64经过处理返回{size: "", type: ""}
      const arr = urlData.split(',')
      let mime = arr[0].match(/:(.*?);/)[1]
      let bstr = atob(arr[1])
      let n = bstr.length
      let u8arr = new Uint8Array(n)
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n)
      }
      return new Blob([u8arr], { type: mime })
    },
    onClickCancel () {
      if (!this.isCanJump) {
        return
      }
      this.$router.replace({ path: '/user/weixinaccrec?curweixin=' + this.curWechatAccount })
    },
    getAmountList () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm }
      this.$vLoading.show()
      this.$reqPost(USER_WEIXIN_COUNT_LIST, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          this.option = res.data
          this.value = res.data[0]['value']
        }
      }).catch(err => {
        console.log(err)
      })
    },
    getWechatMemoCode () {
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm }
      this.$vLoading.show()
      this.$reqPost(USER_WEIXIN_MEMO, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code == 200) {
          this.wechatMemoCode = res.data.wechatMemoCode
        }
      }).catch(err => {
        console.log(err)
      })
    },
    getUserWechatAccount () {
      const { tokenBbm } = getLocalStorage('tokenBbm')
      let params = { token: tokenBbm }
      this.$vLoading.show()
      this.isCanJump = false
      this.$reqPost(USER_CURR_WEIXIN, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        this.isCanJump = true
        if (res.data.status == 1) {
          this.curWechatAccount = res.data.wechatAccount
        }
      }).catch(err => {
        console.log(err)
      })
    },
    onClickSave () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      if (isEmptyStr(this.name)) {
        this.$toast('请输入真实姓名')
        return
      }
      if (isEmptyStr(this.account)) {
        this.$toast('请输入微信账号')
        return
      }
      if (isEmptyStr(this.imgSrc)) {
        this.$toast('请先上传微信收款二维码')
        return
      }
      let selectAmountObj = this.option.filter(item => item.value == this.value)
      let params = { wechatQrCode: this.imgSrc, token: tokenBbm, wechatUsername: this.name, wechatAccount: this.account, wechatAmount: selectAmountObj[0]['text'], wechatMemoCode: this.wechatMemoCode }
      this.$vLoading.show()
      this.$reqPost(USER_EDIT_WECHAT, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          that.$dialog.alert({
            message: '保存成功，到审核页面查看',
            confirmButtonText: '确定',
            cancelButtonColor: '#333333'
          }).then(() => {
            // that.name = ''
            that.value = that.option[0]['value']
            that.imgSrc = ''
            this.$router.replace({ path: '/user/weixinaccrec?curweixin=' + this.account + '&name=' + that.name + '&account=' + that.account })
            // that.account = ''
          })
          // that.$store.dispatch('getUserInfo')
          // that.getWechatMemoCode()
          // that.getUserWechatAccount()
          // that.name = ''
          // that.account = ''
          // that.value = that.option[0]['value']
          // that.imgSrc = ''
          // that.isHadOrg = true
          // that.$router.back()
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
  .bank-account-content {
    height: 100%;
    padding-bottom: 14vh;
    background-color: #f8f8f8;
    .red-span {
      color: @xbd-red-color;
    }
    .bank-account-top {
      background-color: #ffffff;
      font-size: @font-size-md;
      margin-bottom: 4vw;
      .bank-account-one {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
        margin-left: 4vw;
        padding: 0.6vw 0;
        .left-title {
          width: 28vw
        }
      }
    }
    .cur-weixin-wrapper {
      padding: 12px 0;
      background-color: #eeeeee;
      font-size: 16px;
      text-align: center;
      .cur-weixin-account {
        margin-top: 6px;
        color: @xbd-theme-color;
      }
    }

    .bank-infos-wrapper {
      margin: 4vw
    }
    .infos-texts-wrapper {
      /*background-image: url("./imgs/bg-alipay.png");*/
      background: linear-gradient(left, #5FA4FF, #4364FC);
      background-size: 100% 100%;
    }
  }

  .save-btn-wrap {
    width: 88vw;
    display: flex;
    margin: 2.4vw 6vw;
  }

  .two-btn-wrap {
    margin: 2.4vw 6vw;
  }

  .save-btn-wrap /deep/ .button-xbd {
    width: 88vw;
  }

  .save-btn-wrap1 {
    width: 88vw;
    display: flex;
    margin: 2.4vw 6vw;
  }

  .btn-see /deep/ .button-xbd {
    background: #fff;
    width: 88vw;
    border-radius: 22px;
    border: 1px solid #DDDDDD;
    color: #666;
  }
  .weixin-img-wrapper {
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    .weixin-title {
      font-size: @font-size-llg;
      font-weight: bold;
    }
    img {
      width: 32vw;
      height: 32vw;
      margin: 2vh 0 0;
    }
    .img-box img {
      width: 50vw;
      height: 62vw;
      margin: 2vh 0 0;
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
  .all-btns-wrapper {
    position: fixed;
    bottom: 0;
    padding: 2vw 0;
    background-color: #ffffff;
  }
  .copy-btn {
    background: linear-gradient(left, #5FA4FF, #4364FC);
    padding: 6px 14px;
    border-radius: 12px;
    color: #ffffff;
    font-size: @font-size-xs;
    width: 25vw;
    text-align: center;
    margin: 0 8vw;
  }
  .btn-big {
    width: 38vw;
    text-align: center;
    height: 34px;
    padding-top: 8px;
    background: #FFFFFF;
    color: #333333;
    border: 1px solid #DDDDDD;
    font-size: 14px;
    outline: none;
    border-radius: 22px;
  }
  .img-del {
    position: absolute;
    top: 6px;
    right: -6px;
    .bg-image('icon-close');
    width: 24px;
    height: 24px;
  }

</style>
