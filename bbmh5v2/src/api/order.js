export const ORDER_LIST = '/order-list'

export const ORDER_QD_LIST = '/getQdList' // 抢单列表

export const OREDR_TRADE_COMPLAINT = '/businessToUser/userAppeal' // 交易申诉
export const OREDR_CANCEL_COMPLAINT = '/businessToUser/userCancelAppeal' // 撤销交易申诉

export const OREDR_B_CONFIRM = '/userConfirm' // B用户 点击确认收款
export const OREDR_B_MATCH = '/matchQdOrder' // B用户 点击抢单
export const OREDR_REF_STATUS = '/getOrderStatusFromUser' // 用户获取订单状态

export const OREDR_NOGET_MONEY = '/userNoGetMoney' // B用户点击未收到款
export const OREDR_B_CANCEL_COMPL = '/userCancelAppeal' // B用户 撤销申诉
export const OREDR_B_COMPL = '/userAppeal' // B用户 申诉
export const OREDR_HAVE_STAYRESELL = '/checkUserHaveStayResell' // 检查用户有没有需要点击转卖的订单
export const OREDR_DEL_PUNISHMENT = '/delPunishment' // 删除用户的惩罚时间
export const OREDR_LAMP = '/lamp' // 抢单跑马灯文字
export const OREDR_MATCH_STATUS = '/tradeToUser/getMatchStatus' // 获取抢单状态接口
export const OREDR_MATCH_START = '/tradeToUser/startMatch' // 点击开始抢单接口
export const OREDR_MATCH_STOP = '/tradeToUser/stopMatch' // 点击暂停抢单接口
