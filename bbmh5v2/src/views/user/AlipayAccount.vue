<template>
  <div class="bank-account-content">
    <van-nav-bar
      title="支付宝收款账号"
      :right-text="rightText"
      :fixed='true'
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
    />
    <div class="clear-navbar-top"></div>
    <div class="bank-account-top" v-show="!isHadData">
      <div class="bank-account-one van-hairline--bottom">
        <div class="left-title">真实姓名</div>
        <van-field
          v-model.trim="name"
          left
          maxlength="6"
          placeholder="请输入您的姓名"
          type="text"
        />
      </div>
      <div class="bank-account-one van-hairline--bottom" >
        <div class="left-title">支付宝账号</div>
        <van-field
          v-model.trim="cardNo"
          left
          maxlength="32"
          placeholder="请输入支付宝账号"
          type="text"
        />
      </div>
      <div class="bank-account-one van-hairline--bottom" style="box-sizing: border-box;width: 96vw;display: none">
        <div class="left-title">支付宝用户ID</div>
        <van-field
          v-model.trim="alipayAuthId"
          left
          maxlength="20"
          placeholder="请输入支付宝用户ID"
          type="text"
        />
      </div>
    </div>

    <div class="infos-texts-wrapper" v-show="isHadData">
      <div class="infos-text-top">
        <div class="infos-texts-one">
          <span>真实姓名：</span>
          <span>{{name}}</span>
        </div>
        <div class="infos-texts-one">
          <span>支付宝账号：</span>
          <span>{{cardNo}}</span>
        </div>
        <div class="infos-texts-one" style="display: none">
          <span>支付宝用户ID：</span>
          <span>{{alipayAuthId}}</span>
        </div>
      </div>
    </div>
    <div class="upload-wrapper" v-show="!isHadData">
      <div class="accout-title">上传支付宝收款二维码</div>
      <div style="position: relative" class="upload-img-wrapper" v-show="isIOS">
        <img :src="imgSrc" alt="upload" class="img-show">
        <input class="fakefile" name="file" type="file" accept="image/png,image/gif,image/jpeg" @change="uploadIosImg" v-show="!alipayCode"/>
        <span class="img-del icon-size-common" @click="delAliapyCode" v-show="alipayCode"/>
      </div>
      <div v-show="!isIOS" @click="clickUpload" style="text-align: center;position: relative">
        <img :src="imgSrc" alt="upload" class="img-show">
        <span class="img-del icon-size-common" @click="delAliapyCode" v-show="alipayCode"/>
      </div>
    </div>
    <div class="infos-texts-one1" v-show="isHadData&&alipayCode">
      <div class="accout-title">支付宝用户二维码：</div>
      <div style="text-align: center;margin-top: 10px">
        <img :src="alipayCode" class="img-show">
      </div>
    </div>
    <div class="bottom-texts" style="background: #f8f8f8;display: block">
      <div class="notice-texts-wrapper" style="padding-bottom: 3vw;">
        <div class="notice-title">温馨提示：</div>
        <div class="one-line-text">
          <div>1.</div>
          <div>请确保支付宝已经实名认证，并填写支付宝实名认证的真实姓名<span class="span-red">（不要填写支付宝昵称）</span></div>
        </div>
        <div class="one-line-text">
          <div>2.</div>
          <div>填写支付宝信息，方便买家给你转账</div>
        </div>
        <div class="one-line-text">
          <div>3.</div>
          <div>我们承诺不会向任何人透露您的个人信息</div>
        </div>
      </div>
    </div>
    <div class="all-btns-wrapper">
      <div class="two-btn-wrap" v-show="!isHadData&&isHadOrg">
        <button-xbd :disabled="false" @click="onClickCancel" class="btn-left">查看审核状态</button-xbd>
        <button-xbd :disabled="false" @click="onClickSave" class="btn-right">安全保存</button-xbd>
      </div>
      <div class="save-btn-wrap1" v-show="isHadData&&isHadOrg">
        <button-xbd :disabled="false" @click="onClickCancel" class="btn-see">查看审核状态</button-xbd>
      </div>
      <div class="save-btn-wrap" v-show="!isHadOrg">
        <button-xbd :disabled="false" @click="onClickSave" class="btn-save">安全保存</button-xbd>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'
import { Field } from 'vant'
import ButtonXbd from '@components/button-xbd'
import { USER_EDIT_INFOS, USER_UPLOAD_IMG } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
import { isEmptyStr } from '@utils/common'

Vue.use(Field)

export default {
  name: 'AlipayAccount',
  components: {
    ButtonXbd
  },
  data () {
    return {
      name: '',
      cardNo: '',
      alipayAuthId: '',
      isHadData: true,
      isIOS: this.$isIos(),
      alipayCode: '', // http://chuantu.xyz/t6/710/1578628491x1031866013.png
      alipayCodeUrl: ''
    }
  },
  created () {
    this.isHadData = !!this.$store.state.user.userAccountInfo.alipayName && !!this.$store.state.user.userAccountInfo.alipayCode
    this.isHadOrg = !!this.$store.state.user.userAccountInfo.alipayName
    this.name = this.$store.state.user.userAccountInfo.alipayName
    this.cardNo = this.$store.state.user.userAccountInfo.alipayAccount
    this.alipayAuthId = this.$store.state.user.userAccountInfo.alipayAuthId
    this.alipayCode = this.$store.state.user.userAccountInfo.alipayCode
    if (this.$store.state.user.userAccountInfo.alipayStatus == 2) {
      this.isHadData = false
    }
    window.callByAndroidImgUrl = this.callByAndroidImgUrl
  },
  mounted () {
    // console.log('this is current player instance object', this.player)
  },
  destroyed () {
    this.timeOut && clearTimeout(this.timeOut)
  },
  computed: {
    rightText () {
      if (this.isHadData) {
        return '更换支付宝'
      } else {
        return ''
      }
    },
    imgSrc () {
      if (this.alipayCode) {
        return this.alipayCode
      }
      return require('./imgs/upload_img.png')
    }
  },
  methods: {
    onClickLeft () {
      this.$router.go(-1)
    },
    onClickRight () {
      if (!this.rightText || this.rightText === '') {
        return
      }
      this.isHadData = false
      console.log('onClickRight')
    },
    onClickCancel () {
      console.log('onClickCancel')
      this.$router.push({ path: '/user/alipayrec' })
    },
    delAliapyCode (e) {
      e.stopPropagation()
      this.$dialog.confirm({
        message: '删除二维码图片，需要重新上传哦~',
        cancelButtonText: '不小心点错了',
        confirmButtonText: '确认',
        cancelButtonColor: '#333333'
      }).then(() => {
        this.alipayCode = ''
      }).catch(() => {
        // on cancel
      })
    },
    clickUpload () {
      if (this.alipayCode) { // 先删除原来的二维码才能上传
        return
      }
      this.$vLoading.show()
      this.$getNativeFuncFromApp('openPickerImgUpload', 'token11111')
    },
    onClickSave () {
      const that = this
      const { tokenBbm } = getLocalStorage('tokenBbm')
      const alpId = this.alipayAuthId && this.alipayAuthId.replace(/\s+/g, '')
      // this.alipayAuthId = alpId
      if (isEmptyStr(this.name) || isEmptyStr(this.cardNo)) {
        this.$toast('请输入完整信息')
        return
      }
      if (!this.alipayCode) {
        this.$toast('请先上传支付宝二维码')
        return
      }
      // let params = { editType: 3, token: tokenBbm, alipayName: this.name, alipayAccount: this.cardNo, alipayAuthId: alpId }
      let params = { editType: 3, token: tokenBbm, alipayName: this.name, alipayAccount: this.cardNo, alipayCode: this.alipayCode }
      this.$vLoading.show()
      this.$reqPost(USER_EDIT_INFOS, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        res.status.message && this.$toast(res.status.message)
        if (res.status.code === 200) {
          that.$store.dispatch('getUserInfo')
          this.isHadData = true
          this.isHadOrg = true
          // that.$router.back()
        }
      }).catch(err => {
        console.log(err)
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
            this.alipayCode = resData.data.relative_url
          }
        }).catch(error => {
          this.$vLoading.hide()
          this.$toast('上传失败')
          console.log(error)
        })
    },
    uploadIosImg (el) {
      let file = el.target.files[0]
      if (file.size <= 0.2 * 1024 * 1024) { // 200k以下
        this.postImg(file)
      } else {
        this.compressImg(file)
      }
      el.target.value = '' // 重置file
    },
    // uploadIosImg (e) { // ios端上传图片
    //   const file = e.target.files[0]
    //   const that = this
    //   this.$vLoading.show()
    //   let param = new FormData()
    //   param.append('file', file)
    //   let config = {
    //     headers: { 'Content-Type': 'multipart/form-data;boundary = ' + new Date().getTime() },
    //     timeout: 24000
    //   }
    //   axios.post(USER_UPLOAD_IMG, param, config)
    //     .then(response => {
    //       this.$vLoading.hide()
    //       const resData = response.data
    //       if (resData.status.code == 200) {
    //         this.alipayCode = resData.data.relative_url
    //       }
    //     }).catch(error => {
    //       this.$vLoading.hide()
    //       this.$toast('上传失败')
    //       console.log(error)
    //     })
    // },
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
    callByAndroidImgUrl (imgUrl) { // android端上传图片
      // this.$vLoading.hide()
      this.alipayCode = imgUrl
      this.$vLoading.show()
      this.fakeTimeOut = setTimeout(() => { // android效果上做优化
        this.$vLoading.hide()
      }, 3200)
    },
    downloadIamge (imgsrc, name) { // 下载图片地址和图片名
      var image = new Image()
      // 解决跨域 Canvas 污染问题
      image.setAttribute('crossOrigin', 'anonymous')
      image.onload = function () {
        var canvas = document.createElement('canvas')
        canvas.width = image.width
        canvas.height = image.height
        var context = canvas.getContext('2d')
        context.drawImage(image, 0, 0, image.width, image.height)
        var url = canvas.toDataURL('image/png') // 得到图片的base64编码数据

        var a = document.createElement('a') // 生成一个a元素
        var event = new MouseEvent('click') // 创建一个单击事件
        a.download = name || 'bbmqrcode' // 设置图片名称
        a.href = url // 将生成的URL设置为a.href属性
        a.dispatchEvent(event) // 触发a的单击事件
      }
      image.src = imgsrc
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
    .infos-texts-wrapper {
      background-image: url("./imgs/bg-alipay.png");
      background-size: 100% 100%;
    }
  }
  .accout-title {
    margin: 0 4vw 3vw;
    font-size: @font-size-llg;
  }
  .upload-img-wrapper {
    text-align: center;
    .fakefile {
      position: absolute;
      top: 0;
      left: 29vw;
      right: 29vw;
      bottom: 0;
      width: 42vw;
      height: 42vw;
      opacity: 0;
    }
  }
  .img-show {
    width: 42vw;
    height: 42vw;
  }
  .img-del {
    position: absolute;
    top: -2vw;
    right: 27vw;
    .bg-image('icon-close');
    width: 24px;
    height: 24px;
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

  .one-title-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 46px;
    line-height: 46px;
    span {
      margin: 0 12px;
      font-size: @font-size-llg;
      color: #333;
      font-weight: bold;
    }
    img {
      width: 4.8vw
    }
  }

  .alipay-imgs-wrapper {
    margin: 1vh 0;
    img {
      width: 88vw;
      margin-left: 6vw;
    }
  }

  .guide-imgs-wrapper img {
    width: 60vw;
    /*height: 80vw;*/
    margin-left: 20vw;
  }

  .qrcode-img {
    width: 48vw;
    height: 48vw;
    margin: 2vh 26vw;
  }

  .all-btns-wrapper {
    position: fixed;
    bottom: 0;
    padding: 2vw 0;
    background-color: #ffffff;
  }
  .bottom-texts {
    position: absolute;
    bottom: 22vw;
    left: 0;
    right: 0;
  }

</style>
