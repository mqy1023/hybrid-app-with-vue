const path = require('path')

function resolve (dir) {
  return path.join(__dirname, dir)
}

const isProduction = process.env.NODE_ENV === 'production'
const cdn = {
  css: [],
  js: [
    'https://cdn.bootcss.com/vue/2.6.10/vue.min.js',
    'https://cdn.bootcss.com/vue-router/3.0.1/vue-router.min.js',
    'https://cdn.bootcss.com/vuex/3.0.1/vuex.min.js',
    'https://cdn.bootcss.com/axios/0.19.0/axios.min.js',
    'https://cdn.bootcss.com/jsrsasign/8.0.12/jsrsasign-all-min.js'
  ]
}

module.exports = {
  assetsDir: 'static',
  publicPath: './',
  chainWebpack: (config) => {
    config.resolve.alias
      .set('@', resolve('src'))
      .set('@assets', resolve('src/assets'))
      .set('@views', resolve('src/views'))
      .set('@components', resolve('src/components'))
      .set('@utils', resolve('src/utils'))
      .set('@plugins', resolve('src/plugins'))
      .set('@api', resolve('src/api'))

    // 生产环境配置
    if (isProduction) {
      // 生产环境注入cdn
      config.plugin('html')
        .tap(args => {
          args[0].cdn = cdn
          return args
        })
      // config.plugins.delete('prefetch') // 移除 prefetch 插件
      // config.plugins.delete('preload') // 移除 preload 插件
    }
  },
  productionSourceMap: false, // 生产环境是否生成 sourceMap 文件
  css: { // css修改vant定制主题等
    extract: true, // 是否使用css分离插件 ExtractTextPlugin
    sourceMap: false, // 开启 CSS source maps?
    loaderOptions: {
      less: { // 修改vant定制主题等
        modifyVars: {
          red: '#ee0a24',
          blue: '#4364FC',
          orange: '#f08d49',
          'text-color': '#323233',
          'nav-bar-arrow-size': '22px',
          'nav-bar-title-font-size': '18px',
          'tabbar-height': '56px',
          'tabbar-item-margin-bottom': '6px',
          'tabbar-item-icon-size': '22px'
        }
      }
    } // css预设器配置项 详见https://cli.vuejs.org/zh/config/#css-loaderoptions
    // modules: false // 启用 CSS modules for all css / pre-processor files.
  },
  pluginOptions: {
    'style-resources-loader': {
      preProcessor: 'less',
      patterns: ['src/assets/less/base/index.less']
    }
  },
  configureWebpack: config => {
    if (isProduction) {
      // 用cdn方式引入
      config.externals = {
        'vue': 'Vue',
        'vuex': 'Vuex',
        'vue-router': 'VueRouter',
        'axios': 'axios',
        'jsrsasign': 'jsrsasign'
      }
    }
  },
  devServer: {
    proxy: {
      '/api': { // http://daifu.jiajiahebao.com  http://www.bangbangmai.vip
        target: process.env.NODE_ENV === 'development' ? 'http://t-v2.bangbangmai.vip/' : '/',
        changeOrigin: true, // 是否允许跨越
        ws: false, // 值为false防止控制台报错★★★★★★★★
        pathRewrite: {
          // ▼或者后台接口统一加一个前缀如api，然后这里不写 '^/' 写 '^/api'。
          '^/': ''
        }
      }
    }
  }
}
