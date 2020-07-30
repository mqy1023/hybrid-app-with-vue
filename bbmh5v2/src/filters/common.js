export const isEmptyStr = str => {
  return typeof str === 'undefined' || str === null || str === ''
}

export const parseNum = num => {
  return (num.toString().indexOf('.') !== -1) ? num.toLocaleString() : num.toString().replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
}

// export { isEmptyStr, parsenum }
