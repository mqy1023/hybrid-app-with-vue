function getQueryString(name) {
  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
  var url = decodeURI(window.location.search);
  var r = url.substr(1).match(reg);
  if (r != null) return unescape(r[2]);
  return null;
}

(function () {
  function setPxPerRem() { // 把viewport分成10份的rem，html标签的font-size设置为1rem的大小;
    var pxPerRem = document.getElementsByTagName("html")[0].offsetWidth / 10;
    document.getElementsByTagName("html")[0].setAttribute("style", "font-size:" + pxPerRem + "px !important");
  }

  setPxPerRem();
})();


window.onload = function () {
  document.addEventListener('gesturestart', function (e) {
    e.preventDefault();
  });
  document.addEventListener('dblclick', function (e) {
    e.preventDefault();
  });
  // 阻止双击放大
  var lastTouchEnd = 0;
  document.addEventListener('touchstart', function (event) {
    if (event.touches.length > 1) {
      event.preventDefault();
    }
  });
  document.addEventListener('touchend', function (event) {
    var now = (new Date()).getTime();
    if (now - lastTouchEnd <= 300) {
      event.preventDefault();
    }
    lastTouchEnd = now;
  }, false);

  // 阻止双指放大
  document.addEventListener('gesturestart', function (event) {
    event.preventDefault();
  });
  var roll = document.body.scrollTop;
}

let toastTimer;

function showToast(msg, duration) {
  clearTimeout(toastTimer);
  const ToastBox = document.getElementsByClassName('toastDiv-box')[0];
  if (ToastBox) document.body.removeChild(ToastBox);//防止重复弹出
  duration = isNaN(duration) ? 3000 : duration;
  const m = document.createElement('div');
  m.className = 'toastDiv-box';
  m.innerHTML = msg;
  m.style.cssText = "box-sizing:border-box;width: 50%;min-width: 150px;opacity: 0.8;height: auto;color: rgb(255, 255, 255);text-align: center;padding: 12px;border-radius: 8px;position: fixed;top: 40%;left: 25%;z-index: 999999;background: rgb(0, 0, 0);font-size: 14px;overflow: hidden;";
  document.body.appendChild(m);
  toastTimer = setTimeout(function () {
    const d = 0.5;
    m.style.webkitTransition = '-webkit-transform ' + d + 's ease-in, opacity ' + d + 's ease-in';
    m.style.opacity = '0';
    setTimeout(function () {
      document.body.removeChild(m);
    }, d * 1000);
  }, duration);
}

function showLoading() {
  clearTimeout(toastTimer);
  const loadingBox = document.getElementsByClassName('loadingDiv-box')[0];
  if (loadingBox) document.body.removeChild(loadingBox);//防止重复弹出
  const loadingImg = document.createElement('img');
  loadingImg.src = 'static/imgs/gif-loading.gif'
  loadingImg.style.cssText = "width:12vw;height:12vw;";
  const loadingDiv = document.createElement('div');
  const loadingBody = document.createElement('div');
  loadingBody.className = 'loadingDiv-box';
  loadingDiv.style.cssText = "width:24vw;height:24vw;background: rgb(0, 0, 0);opacity: 0.8;border-radius: 2vw;overflow: hidden;display: flex;justify-content: center;align-items: center;";
  loadingDiv.appendChild(loadingImg)
  // loadingDiv.innerHTML = loadingImg;
  loadingBody.style.cssText = "position: fixed;left: 0;right: 0;top: 0;bottom: 0;opacity: 0.6;z-index: 999999;background: rgb(255, 255, 255);overflow: hidden;display: flex;justify-content: center;align-items: center;";
  loadingBody.appendChild(loadingDiv)
  document.body.appendChild(loadingBody);
}

function hideLoading() {
  const loadingBox = document.getElementsByClassName('loadingDiv-box')[0];
  if (loadingBox) document.body.removeChild(loadingBox);
}

function countDownShow(compareTimestamp) {
  if (compareTimestamp <= 0 || !compareTimestamp) {
    return '00:00:00'
  }
  let hours = '00', mins = '00', sec = '00'
  if (compareTimestamp >= 3600) {
    hours = Math.floor(compareTimestamp / 3600)
    compareTimestamp = compareTimestamp - hours * 3600
    if (compareTimestamp >= 60) {
      mins = Math.floor(compareTimestamp / 60)
      sec = compareTimestamp - mins * 60
    } else {
      sec = compareTimestamp
    }
  } else if (compareTimestamp >= 60) {
    mins = Math.floor(compareTimestamp / 60)
    sec = compareTimestamp - mins * 60
  } else {
    sec = compareTimestamp
  }
  hours += ''
  mins += ''
  sec += ''
  return (hours.length == 2 ? hours : '0' + hours) + ':' + (mins.length == 2 ? mins : '0' + mins) + ':' + (sec.length == 2 ? sec : '0' + sec)
}


// 最终传参
const getJsaParam = (param) => {
  return getJsaCode(sortJson(param))
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
  if (location.href.indexOf('t-v2.bangbangmai') > 0 || location.href.indexOf('localhost') > 0) { // 测试环境
    keyType = kTest
  }

  // 创建RSAKey对象
  let rsa = new RSAKey()
  // 将密钥转码
  rsa = KEYUTIL.getKey(keyType)
  // 创建Signature对象，设置签名编码算法
  const sig = new KJUR.crypto.Signature({'alg': 'SHA1withRSA'})

  sig.init(rsa) // 初始化

  sig.updateString(newStr) // 传入待加密字符串

  return hextob64(sig.sign())// 生成密文
}

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
