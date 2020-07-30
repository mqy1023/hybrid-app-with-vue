import LoadingComponent from './vLoading.vue'

let instance = null

const vLoading = {
  install: (Vue) => {
    if (!instance) {
      const VLoading = Vue.extend(LoadingComponent)
      instance = new VLoading({ el: document.createElement('div') })
      document.body.appendChild(instance.$el)
    }
    instance.isShow = false
    let customMethods = {
      show () {
        instance.isShow = true
      },
      hide () {
        instance.isShow = false
      }
    }
    if (!Vue.$vLoading) {
      Vue.$vLoading = customMethods
      Vue.prototype.$vLoading = Vue.$vLoading
    }
  }
}
export default vLoading
