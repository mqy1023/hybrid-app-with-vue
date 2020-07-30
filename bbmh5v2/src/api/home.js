
// 首页轮播图
// export const HOME_BANNERS = 'banner/list'
export const HOME_BANNERS = 'banner/getBannerList'

// export const HOME_HOTS = 'post/list'
export const HOME_HOTS = 'product/index' // 热门推荐
export const HOME_HOTS_DETAIL = 'product/productDetail' // 热门推荐详情接口

export const HOME_MSG_LIST = 'notification/list' // 消息列表
export const HOME_MSG_READ = 'notification/editNotificationReadStatus' // 读消息
export const HOME_MSG_DELETE = 'notification/delete' // 删除消息

export const HOME_BUY_PRODUCT = 'businessToUser/buyProduct' // 用户购买商品
export const HOME_CHANGE_PRODUCT_STATUS = 'businessToUser/changeOrderStatus' // 取消，确认和超时订单接口
export const HOME_TRADE_STATUS = 'businessToUser/getConfirmInfo' // 用户刷新获取商户确认状态和用户的支付宝状态
export const HOME_RESELL = 'tradeToUser/userResell' // 用户转卖
export const HOME_CHANGE_PAY_TYPE = 'businessToUser/updatePayWay' // 修改支付方式

export const HOME_BANK_INFOS = 'userInfo/businessBankInfo' // 支付订单 —— 银行信息
export const HOME_APP_VERSION_CHECK = 'appVersion/check' // app版本检查更新
