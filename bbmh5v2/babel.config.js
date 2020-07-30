module.exports = {
  presets: [
    '@vue/app'
    // '@vue/cli-plugin-babel/preset'
  ],
  plugins: [
    ['import', {
      libraryName: 'vant',
      libraryDirectory: 'es',
      style: name => `${name}/style/less`
    }, 'vant'],
    ['@babel/plugin-syntax-dynamic-import']
  ]

}
