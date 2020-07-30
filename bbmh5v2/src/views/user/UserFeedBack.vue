<template>
  <div class="tran-complaint-content content">
    <van-nav-bar
      title="客服反馈"
      right-text="反馈结果"
      :fixed="true"
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
    />
    <div class="tran-complaint-reason clear-navbar-top">
      <div class="title-complaint">反馈类型</div>
      <van-dropdown-menu>
        <van-dropdown-item v-model="value" :options="options" />
      </van-dropdown-menu>
    </div>
    <div class="tran-complaint-desc">
      <div class="title-complaint">请描述具体问题</div>
      <div class="textarea-wrapper">
        <textarea placeholder="请输入你的问题描述或建议" class="textarea-content" v-model="desc" maxlength="1000" />
        <span class="text-amount">{{textAreaCount}}</span>
      </div>
    </div>
    <div class="upload-wrapper">
      <div class="title-complaint">添加图片<span style="font-size: 12px">{{imgCount}}</span></div>

      <div class="upload-imgs">
        <div class="img-box" v-for="(item, index) in imgList" :key="index">
          <img :src="item" alt="tp" @click="showImg(index)">
          <span class="img-close icon-size-common" @click="deleteImg(index)"></span>
        </div>
        <div v-show="imgList.length<3&&isIOS" style="position: relative" >
          <img src="../../assets/imgs/upload_img.png" alt="upload">
          <input class="fakefile" name="file" type="file" accept="image/png,image/gif,image/jpeg" @change="updateImg"/>
        </div>
        <div v-show="imgList.length<3&&!isIOS" @click="clickUpload">
          <img src="../../assets/imgs/upload_img.png" alt="upload">
        </div>
      </div>
    </div>

    <div class="phone-and-qq">
      <div class="title-complaint">必填项<span>（务必填写真实的联系信息）</span><span style="color: red">*</span></div>
      <input v-model="phone" type="number" placeholder="请留下手机号/QQ" class="input-style-default" maxlength="13" oninput="if(value.length>13)value=value.slice(0,13)" />
    </div>
    <button-xbd :disabled="false" @click="onClickCommit" class="btn-xbd-default">提交</button-xbd>
  </div>
</template>

<script>
import Vue from 'vue'
import { DropdownMenu, DropdownItem, ImagePreview } from 'vant'
import ButtonXbd from '@components/button-xbd'
import { USER_FEEDBACK, USER_UPLOAD_IMG } from '@api/user'
import { getLocalStorage } from '@utils/local-storage'
import axios from 'axios'
Vue.use(DropdownMenu).use(DropdownItem).use(ImagePreview)

export default {
  name: 'FeedBack',
  components: {
    ButtonXbd
  },
  data () {
    return {
      value: 'help',
      options: [
        { text: '帮助', value: 'help' },
        { text: 'bug', value: 'bug' },
        { text: '建议', value: 'suggestion' }
      ],
      imgList: [],
      phone: '',
      qq: '',
      desc: '',
      isIOS: this.$isIos()
    }
  },
  created () {
    window.callByAndroidImgUrl = this.callByAndroidImgUrl
  },
  computed: {
    textAreaCount () {
      return (this.desc.length || 0) + '/1000字'
    },
    imgCount () {
      return '（' + (this.imgList.length || 0) + '/3）'
    }
  },
  methods: {
    // 返回布尔值
    beforeRead (file) {
      if (file.type !== 'image/jpeg' && file.type !== 'image/png') {
        this.$toast('请上传 jpg/png 格式图片')
        return false
      }
      return true
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
    onClickLeft () {
      this.$router.go(-1) // 返回上一层
    },
    onClickRight () {
      this.$router.replace({ path: '/user/feedbackres' })
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
    },
    onClickCommit () {
      const that = this
      const descVal = this.desc.trim()
      if (!descVal) {
        this.$toast('请输入具体问题描述')
        return
      }
      const phoneVal = this.phone.trim()
      if (!phoneVal) {
        this.$toast('请输入手机号/QQ')
        return
      }
      const { tokenBbm } = getLocalStorage('tokenBbm')
      // const imgStr = 'http://image.newscat.com/ad/20190614064810_5d03432a0bce8_47.png,http://image.newscat.com/ad/20190614064810_5d03432a0bce8_47.png'
      const imgStr = this.imgList.join(',')
      let params = { category: this.value, token: tokenBbm, description: descVal, contact: phoneVal, images: imgStr }
      this.$vLoading.show()
      this.$reqPost(USER_FEEDBACK, { ...params, sign: this.$getJsaParam(params) }).then(res => {
        if (res.status.code === 200) {
          that.$dialog.alert({
            title: '您的反馈已受理',
            message: '请在“客服反馈-反馈结果”中查看',
            confirmButtonText: '我知道了'
          }).then(() => {
            that.$router.go(-1) // 返回上一层
          })
        } else {
          res.status.message && this.$toast(res.status.message)
        }
      }).catch(err => {
        console.log(err)
      })
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
  .btn-xbd-default {
    margin: 6vw 0 0;
  }
</style>
