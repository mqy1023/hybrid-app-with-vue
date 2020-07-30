import jsrsasign from 'jsrsasign'

const sortJson = (obj) => {
  let arr = []
  let num = 0
  for (var i in obj) {
    arr[num] = i
    num++
  }
  let sortArr = arr.sort()
  let str = ''
  for (let i = 0; i < sortArr.length; i++) {
    if (i) {
      str += '&'
    }
    str += sortArr[i] + '=' + obj[sortArr[i]]
  }

  return str
}
// jsa 签名算法
const getJsaCode = (newStr) => {
  // 因为后端提供的是pck#8的密钥对，所以这里使用 KEYUTIL.getKey来解析密钥
  var kTest = `-----BEGIN RSA PRIVATE KEY-----
MIICXwIBAAKBgQCdJg4wRCjb1fBxVIXdfGlMeqyUezf9QdRvrcYgs2GGnC3g5qfA
19ZAF21hiZ3PCs3a/m2hwqgkq56NYrg55B/MReqdb9zLoT0eXO+i5g6sP/2cLDu4
UoOE2Fee6gO/xEiAPl7R0f3FoWCfNTuz9bLOeUJmk2eO3UvkcnyNNv7WYQIDAQAB
AoGBAJbF0ksgugrhYkV5kM4F9Yk5EaeL6RYFOOrFURhCwslBwwrKghyVeKwZTgqM
3Gmutf7sqQbQGj7EoeSI2LRCWPZFIIr+YygBdq3Nwa8VDgb7dwUaick2J+hXApUS
/jz/We5GJlPzkopc+LKUv1LLJGJfJmLrRK+3VncJzUkiciT5AkEAypRWdw+1T5RU
RNGXoCfvd3E6doNOwRD7J3p4yMmCJWCzRtIiPNxzdKOKt0y/3t6u7f8Nf0+EbvIO
c8DCZVxsXwJBAMaWyrGCvgQ1+LGDOwkSCETOh/VJhn/lLXqGBWn4MeOBHz/KI4km
cWulKqaO2K/ZvmL2JXBFf2r6pUaf5kb/tT8CQQCuzMzHM7C5mD4Wm9wAADWrlPJj
bO45vdNZv14Vs3svkX9gbtXIOlkNnkiF3h35y6CaEGivIAggS5GY5A1ZtSR1AkEA
r5ALrZaaCfr022navRAWA05a37yajWK8K/9jteVXpXs3dxtrAwrbZoDkDc9nPT9Q
kwhs3K50EM15Y9BksgvJ3QJBALLISYqx5J35fdetm/e2ks8XOrEE2Vdws4F+Fzf7
8PvLuKMU8DpZZ710Yn0y2GQSwbJJz656Sw6JDwX9PNt/bys=
-----END RSA PRIVATE KEY-----
    `
  var kProd = `-----BEGIN RSA PRIVATE KEY-----
MIICXgIBAAKBgQDJQMKfT/fZI1WHXTq/0Zb9iC1HFD3SzjtJNSdb7ehpl50ihBGX
8TS3OMqw7EUpFbyc9RYrTSeD4KvrRsg99/cEKB6KsrF1CF6K+PmdnC4qxmzkQA32
vy2fMWfbGKbkcFvVN0Rb3z3SknCsM8ZuOUNgLrVv0LfpuOKmIqxMjkh+iQIDAQAB
AoGBAIuhcObTtuRijY7TclVv91CEUNXGB3bhO2T5igtQLzZDyPQPqZ0/7xFIR+kk
pH9y42AK84yrhUUMdntQqtHD3w86PdEbRgS9JOYr+sS5V1Xbam/3hnpLYP8t23l2
P7ve3lvtdLHpCVdWlC76mfAN2hwJRf4AYmvOG5k8lZ53nmdZAkEA5mncdRrHc2oS
HWyLsuadE8vztzIIwKwIqcpNuBGq7lpMAigReeOSkO5k479Gpr+yGEcFaEeyAGjF
mRBZUizfzwJBAN+Z7h0HcteeAlP+7cm6aWnL1UM/CA0O7w1ql7FyXMs35UWL/r2G
Hhc4oNZ7Ab1EsPZie+SUURNWd7FrmrNKuicCQQCZaTSKl9jiZGZJRwNAi0Fcc/gC
KoMfXauq+PKEMuc/KcnfybPRFw94BfIQnUM6NPosJeVYyBuhICEoR3VhPFWLAkEA
kFLt04TkRnHxtL+eQg/1TFlJrN75VlxmRjWf9eQIKw9ICCzFXxU7ROfDQM0ioUS+
KDoHF9H3xtCP70+yCbnZDwJAG9+sY37Uo5nlNtOTN6rtif4Oj8TO+bwz0I1NM0sT
Ud9XIHVDCIvDuDVUOhjcGDuZjaE8362emvgOgFxGczTVeQ==
-----END RSA PRIVATE KEY-----
    `
  let keyType = kProd
  if (location.href.indexOf('t-v2.hahaha') > 0 || location.href.indexOf('localhost') > 0) { // 测试环境
    keyType = kTest
  }
  // 创建RSAKey对象
  let rsa = new jsrsasign.RSAKey()
  // 将密钥转码
  rsa = jsrsasign.KEYUTIL.getKey(keyType)
  // 创建Signature对象，设置签名编码算法
  const sig = new jsrsasign.KJUR.crypto.Signature({ 'alg': 'SHA1withRSA' })

  sig.init(rsa) // 初始化

  sig.updateString(newStr) // 传入待加密字符串

  return jsrsasign.hextob64(sig.sign())// 生成密文
}

const isAndroid = () => {
  const u = navigator.userAgent
  return u.indexOf('Android') > -1 || u.indexOf('Linux') > -1
  // const isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1
  // android终端或者uc浏览器
  // const isiOS = !!u.match(/\(i[^]+( U)? CPU.+Mac OS X/)
}
const isIos = () => {
  const u = navigator.userAgent
  return !!u.match(/\(i[^]+( U)? CPU.+Mac OS X/)
}
// 判断移动设备
const isMobileApp = () => {
  if (/Android|iPhone/i.test(navigator.userAgent)) {
    return 1
  }
  return 0
}

// 调用原生方法
const getNativeFuncFromApp = (name, params) => {
  if (isAndroid()) {
    if (window.android && window.android[name]) {
      if (params) {
        window.android[name](params)
      } else {
        window.android[name]()
      }
    }
  } else if (isIos()) {
    if (window.webkit && window.webkit.messageHandlers[name]) { // 调用ios原生方法
      window.webkit.messageHandlers[name].postMessage(params)
    }
  }
}

// 最终传参
const getJsaParam = (param) => {
  return getJsaCode(sortJson(param))
}

const getTimestamp = () => { // 获取时间戳前十位
  const timestamp = new Date().getTime() + ''
  return timestamp.substring(0, 10)
}

const isMatchPhone = (phone) => {
  return (/^1[3456789]\d{9}$/.test(phone))
}

const isMatchPwd = (pwd) => {
  return (/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,15}$/).test(pwd)
}

const isMatchBank = (bank) => {
  return (/^(?:[1-9]{1})(?:\d{15}|\d{18})$/).test(bank)
}

const timeCompareWithNow = (timestamp, nowTimestamp) => { // 还剩下多少时间
  // const nowTimestamp = Math.floor(new Date().getTime() / 1000)
  if (!nowTimestamp) {
    nowTimestamp = Math.floor(new Date().getTime() / 1000)
  }
  let compareTimestamp = nowTimestamp - timestamp // 相差秒杀
  compareTimestamp = Math.max(compareTimestamp, 0)
  let backTimeText = ''
  if (compareTimestamp > 3600) {
    const h = Math.floor(compareTimestamp / 3600)
    backTimeText = h + '小时'
    const restMin = compareTimestamp - h * 3600
    if (restMin > 60) {
      const m = Math.floor(restMin / 60)
      backTimeText += m + '分钟前'
      // const resSec = restMin - m * 60
      // backTimeText += resSec + '秒'
    } else {
      // backTimeText += restMin + '秒'
      backTimeText += '前'
    }
  } else if (compareTimestamp > 60) {
    const m = Math.floor(compareTimestamp / 60)
    backTimeText += m + '分钟前'
    // const restMin = compareTimestamp - m * 60
    // backTimeText += restMin + '秒'
  } else {
    // backTimeText += compareTimestamp + '秒'
    backTimeText += '刚刚'
  }
  return backTimeText
}

const timeCompareWithBefore = (timestamp, nowTimestamp, isJustBackTimeStamp) => { // // 还有多少时间
  // const nowTimestamp = Math.floor(new Date().getTime() / 1000)
  const compareTimestamp = timestamp - nowTimestamp // 相差秒杀
  if (compareTimestamp <= 0) {
    return 0
  }
  if (isJustBackTimeStamp) {
    return compareTimestamp
  }
  let backTimeText = ''
  if (compareTimestamp > 3600) {
    const h = Math.floor(compareTimestamp / 3600)
    backTimeText = h + '小时'
    const restMin = compareTimestamp - h * 3600
    if (restMin > 60) {
      const m = Math.floor(restMin / 60)
      backTimeText += m + '分钟前'
      // const resSec = restMin - m * 60
      // backTimeText += resSec + '秒'
    } else {
      // backTimeText += restMin + '秒'
      backTimeText += '前'
    }
  } else if (compareTimestamp > 60) {
    const m = Math.floor(compareTimestamp / 60)
    backTimeText += m + '分钟前'
    // const restMin = compareTimestamp - m * 60
    // backTimeText += restMin + '秒'
  } else {
    // backTimeText += compareTimestamp + '秒'
    backTimeText += '刚刚'
  }
  return backTimeText
}

export { getNativeFuncFromApp, getJsaParam, getTimestamp, isMatchPhone, isMatchPwd, isMatchBank, isAndroid, isIos, timeCompareWithNow, timeCompareWithBefore }
