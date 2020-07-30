import Vue from 'vue'

import * as common from './common'

Object.keys(common).forEach(k => Vue.filter(k, common[k]))
