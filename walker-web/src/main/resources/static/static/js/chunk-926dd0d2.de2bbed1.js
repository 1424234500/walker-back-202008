(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-926dd0d2"],{"5bc6":function(e,t,i){"use strict";i.r(t);var n=function(){var e=this,t=e.$createElement,i=e._self._c||t;return i("div",{staticClass:"app-container"},[i("div",{directives:[{name:"loading",rawName:"v-loading",value:e.loadingCols,expression:"loadingCols"}],staticClass:"div-box-down"},[i("form",{staticClass:"form-inline"},[e._l(e.colMapShow,(function(t,n){return i("div",{staticClass:"form-group"},[i("label",[e._v(e._s(""==t?n:t))]),i("input",{directives:[{name:"model",rawName:"v-model",value:e.rowSearch[n],expression:"rowSearch[key]"}],staticClass:"form-control",staticStyle:{width:"10em","margin-right":"1em"},attrs:{type:"text",placeholder:n},domProps:{value:e.rowSearch[n]},on:{keyup:function(t){return t.type.indexOf("key")||13===t.keyCode?e.getListPage():null},input:function(t){t.target.composing||e.$set(e.rowSearch,n,t.target.value)}}})])})),i("el-button-group",[i("el-button",{staticClass:"btn btn-primary",on:{click:function(t){return e.getListPage()}}},[e._v("查询")]),i("el-button",{staticClass:"btn btn-success",on:{click:function(t){return e.handlerAddColumn()}}},[e._v("添加")]),i("el-button",{staticClass:"btn btn-danger",on:{click:function(t){e.clearRowSearch(),e.getListPage()}}},[e._v("清除")])],1)],2)]),i("div",{directives:[{name:"show",rawName:"v-show",value:!e.loadingCols,expression:"!loadingCols"}]},[i("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.loadingList,expression:"loadingList"}],ref:"multipleTable",attrs:{data:e.list,"row-class-name":e.tableRowClassName,"highlight-current-row":"","element-loading-text":"Loading",border:"",fit:"",stripe:"","show-summary":"","sum-text":"S","max-height":"86%"},on:{"selection-change":e.handlerSelectionChange,"current-change":e.handlerCurrentChangeTable,"sort-change":e.handlerSortChange}},[i("el-table-column",{attrs:{fixed:"left",aligin:"center",type:"selection","min-width":"12px"}}),i("el-table-column",{attrs:{fixed:"left",align:"center",type:"index","min-width":"12px"}}),e._l(e.colMapShow,(function(t,n){return i("el-table-column",{key:n,attrs:{property:n,label:""==t?n:t,sortable:"","show-overflow-tooltip":"","min-width":"100px"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v(" "+e._s(t.row[t.column.property])+" ")]}}],null,!0)})})),i("el-table-column",{attrs:{label:"操作","show-overflow-tooltip":"",fixed:"right","min-width":"221px"},scopedSlots:e._u([{key:"default",fn:function(t){return[i("el-button-group",[i("el-button",{attrs:{size:"mini",type:"primary",icon:"el-icon-edit",circle:""},on:{click:function(i){return i.stopPropagation(),e.handlerChange(t.row)}}}),i("el-button",{attrs:{size:"mini",type:"success",icon:"el-icon-menu",circle:""},on:{click:function(i){return i.stopPropagation(),e.handlerShowTrigger(t.row)}}}),i("el-button",{attrs:{size:"mini",type:"warning",icon:"el-icon-search",circle:""},on:{click:function(i){return i.stopPropagation(),e.handlerShowHis(t.row)}}}),i("el-button",{attrs:{size:"mini",type:"danger",icon:"el-icon-delete",circle:""},on:{click:function(i){return i.stopPropagation(),e.handlerDelete(t.row)}}}),i("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(i){return i.stopPropagation(),e.handlerRun(t.row)}}},[e._v("立即执行")])],1)]}}])})],2),e.rowSelect.length>0?i("el-button",{staticClass:"btn btn-danger",staticStyle:{float:"left",margin:"4px 0px 0 2px"},on:{click:function(t){return e.handlerDeleteAll()}}},[e._v("删除选中")]):e._e(),i("el-pagination",{staticStyle:{float:"right",margin:"10px 20px 0 0"},attrs:{"current-page":e.page.nowpage,"page-size":e.page.shownum,"page-sizes":[2,4,8,16,32,64,128],"pager-count":9,layout:"total, sizes, prev, pager, next, jumper",total:e.page.num,background:""},on:{"current-change":e.handlerCurrentChange,"size-change":e.handlerSizeChange}}),i("el-dialog",{attrs:{title:"修改",visible:e.loadingUpdate,width:"86%","before-close":e.handlerCancel},on:{"update:visible":function(t){e.loadingUpdate=t}}},[[i("el-form",{directives:[{name:"loading",rawName:"v-loading",value:e.loadingSave,expression:"loadingSave"}],ref:"form",attrs:{model:e.rowUpdate,"label-width":"100px"}},[e._l(e.colMap,(function(t,n){return i("el-form-item",{key:n,attrs:{property:n,label:""==t?n:t}},[i("el-input",{attrs:{type:"text"},model:{value:e.rowUpdate[n],callback:function(t){e.$set(e.rowUpdate,n,t)},expression:"rowUpdate[key]"}})],1)})),i("el-form-item",[i("el-button-group",[i("el-button",{attrs:{type:"primary"},on:{click:function(t){return e.handlerSave()}}},[e._v("确定")]),i("el-button",{attrs:{type:"danger"},on:{click:function(t){return e.handlerCancel()}}},[e._v("取消")])],1)],1)],2)]],2),i("el-dialog",{attrs:{title:"触发器列表",visible:e.loadingUpdateTrigger,width:"86%"},on:{"update:visible":function(t){e.loadingUpdateTrigger=t}}},[[i("el-button-group",[i("el-button",{staticClass:"btn btn-success",on:{click:function(t){return e.handlerAddColumnTrigger()}}},[e._v("添加")]),i("el-button",{staticClass:"btn btn-danger",on:{click:function(t){return e.handlerSaveAllTriggers()}}},[e._v("保存")])],1),i("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.loadingTrigger,expression:"loadingTrigger"}],ref:"multipleTableTrigger",attrs:{data:e.listTrigger,"row-class-name":e.tableRowClassName,"element-loading-text":"Loading",border:"",fit:"",stripe:"","show-summary":"","sum-text":"S","highlight-current-row":"","max-height":"86%"},on:{"selection-change":e.handlerSelectionChangeTrigger,"current-change":e.handlerCurrentChangeTableTrigger,open:e.handlerOnShowTrigger}},[i("el-table-column",{attrs:{fixed:"left",aligin:"center",type:"selection","min-width":"12px"}}),i("el-table-column",{attrs:{fixed:"left",align:"center",type:"index","min-width":"12px"}}),e._l(e.colMapTrigger,(function(t,n){return i("el-table-column",{key:n,attrs:{property:n,label:""==t?n:t,sortable:"","show-overflow-tooltip":"","min-width":"100px"},scopedSlots:e._u([{key:"default",fn:function(t){return[i("input",{directives:[{name:"model",rawName:"v-model",value:t.row[t.column.property],expression:"scope.row[scope.column.property]"}],staticClass:"form-control",staticStyle:{width:"18em","margin-right":"1em"},attrs:{type:"text",placeholder:t.column.property},domProps:{value:t.row[t.column.property]},on:{input:function(i){i.target.composing||e.$set(t.row,t.column.property,i.target.value)}}})]}}],null,!0)})}))],2)]],2),i("el-dialog",{attrs:{title:"调度日志",visible:e.showDialogLog,width:"86%"},on:{"update:visible":function(t){e.showDialogLog=t}}},[e.showDialogLog?[i("mtable",{attrs:{props:e.showDialogLogParams}})]:e._e()],2)],1)])},o=[],r=(i("f559"),{filters:{statusFilter:function(e){var t={published:"success",draft:"gray",deleted:"danger"};return t[e]}},data:function(){return{list:[],colMap:{},colMapShow:{},colKey:"",rowSearch:{},rowUpdate:{},rowUpdateFrom:{},rowSelect:[],nowRow:null,page:{nowpage:1,num:0,order:"",pagenum:0,shownum:8},loadingList:!0,loadingCols:!0,loadingSave:!0,loadingUpdate:!1,nowRowTrigger:null,loadingTrigger:!1,loadingHis:!1,loadingUpdateTrigger:!1,showTrigger:{},colMapTrigger:{},colKeyTrigger:{},listTrigger:[],listTriggerOld:[],rowSelectTrigger:[],rowSelectHis:[],quartzTable:"W_QRTZ_JOB_DETAILS",quartzTableTrigger:"W_QRTZ_CRON_TRIGGERS",info:"",info2:"",showDialogLog:!1,showDialogLogParams:null}},created:function(){this.getColumns()},methods:{getColumns:function(){var e=this;this.loadingCols=!0,this.get("/common/getColsMap.do",{tableName:this.quartzTable}).then((function(t){e.colMap=t.data.colMap,e.page.order=t.data.colMap["S_MTIME"]?"S_MTIME DESC":"",delete e.colMap.JOB_DATA,delete e.colMap.SCHED_NAME,delete e.colMap.JOB_GROUP,delete e.colMap.IS_DURABLE,delete e.colMap.IS_NONCONCURRENT,delete e.colMap.IS_UPDATE_DATA,delete e.colMap.REQUESTS_RECOVERY,e.colMapShow={},e.colMapShow["JOB_NAME"]=e.colMap["JOB_NAME"],e.colMapShow["JOB_CLASS_NAME"]=e.colMap["JOB_CLASS_NAME"],e.colMapShow["DESCRIPTION"]=e.colMap["DESCRIPTION"],e.colKey="JOB_NAME",e.clearRowSearch(),e.loadingCols=!1,e.getListPage()})).catch((function(){e.loadingCols=!1}))},clearRowSearch:function(){this.rowSearch={},this.page.nowpage=1},getListPage:function(){var e=this;this.loadingList=!0;var t=this.assign({nowPage:this.page.nowpage,showNum:this.page.shownum,order:this.page.order},this.rowSearch);this.get("/quartz/findPage.do",t).then((function(t){e.list=t.data.data,e.page=t.data.page,e.info=t.info,e.loadingList=!1})).catch((function(){e.loadingList=!1}))},handlerCurrentChangeTable:function(e){console.info("handlerCurrentChangeTable",e),this.nowRow=e},handlerCurrentChangeTableTrigger:function(e){console.info("handlerCurrentChangeTableTrigger",e),this.nowRowTrigger=e},handlerAddColumn:function(){var e=this.assign(this.nowRow?this.nowRow:{},this.rowSearch);e[this.colKey]="",this.list.push(e),this.handlerChange(e)},handlerAddColumnTrigger:function(){var e=this.assign({},{});e["CRON_EXPRESSION"]=null==this.nowRowTrigger?"0 0 0 * * ?":this.nowRowTrigger["CRON_EXPRESSION"],e["DESCRIPTION"]=" 每天0点0分0秒 触发",e["S_FLAG"]="1",this.listTrigger.push(e),this.$refs.multipleTableTrigger.toggleRowSelection(e,!0)},handlerRun:function(e){var t=this;console.info("handlerRun "+JSON.stringify(e)),this.loadingList=!0,this.get("/quartz/run.do",e).then((function(e){t.info=e.info,t.loadingList=!1})).catch((function(){t.loadingList=!1}))},handlerChange:function(e){this.loadingUpdate=!this.loadingUpdate,this.loadingSave=!1,console.info("handlerChange "+JSON.stringify(e)),this.rowUpdateFrom=e,this.rowUpdate=JSON.parse(JSON.stringify(e)),this.loadingSave=!1},handlerCancel:function(){console.info("handlerCancel "+JSON.stringify(this.rowUpdate)),this.loadingUpdate=!this.loadingUpdate},handlerSave:function(){var e=this;console.info("handlerSave "+JSON.stringify(this.rowUpdate)),this.loadingSave=!0,this.rowUpdateFrom=Object.assign(this.rowUpdateFrom,this.rowUpdate);var t=this.rowUpdateFrom;this.post("/quartz/save.do",t).then((function(t){e.loadingSave=!1,e.loadingUpdate=!e.loadingUpdate})).catch((function(){e.loadingSave=!1}))},handlerDelete:function(e,t){var i=this;console.info("handlerDelete  "+JSON.stringify(e)),this.loadingList=!0;var n={JOB_NAME:e[this.colKey]};this.get("/quartz/delet.do",n).then((function(t){for(var n=0;n<i.list.length;n++)i.list[n]==e&&i.list.splice(n,1);i.loadingList=!1})).catch((function(){i.loadingList=!1}))},handlerDeleteAll:function(){var e=this;if(console.info("handlerDeleteAll  "+JSON.stringify(this.rowSelect)),this.rowSelect.length>0){this.loadingList=!0;for(var t="",i=0;i<this.rowSelect.length;i++)t+=this.rowSelect[i][this.colKey]+",";t=t.substring(0,t.length-1);var n={JOB_NAME:t};this.get("/quartz/delet.do",n).then((function(t){e.loadingList=!1;for(var i=0;i<e.rowSelect.length;i++)for(var n=0;n<e.list.length;n++)e.list[n]==e.rowSelect[i]&&e.list.splice(n,1);e.rowSelect=[]})).catch((function(){e.loadingList=!1}))}},handlerSelectionChange:function(e){console.info("handlerSelectionChange"+JSON.stringify(e)),console.info(e),this.rowSelect=e},handlerShowTrigger:function(e){var t=this;this.showTrigger=e,this.loadingUpdateTrigger=!this.loadingUpdateTrigger,this.loadingTrigger=!0,this.listTrigger=[],this.colMapTrigger={},this.colMapTrigger["CRON_EXPRESSION"]="表达式",this.colMapTrigger["DESCRIPTION"]="描述",this.colKeyTrigger="CRON_EXPRESSION";var i={nowPage:1,showNum:10};i[this.colKey]=e[this.colKey],this.get("/quartz/findPageTrigger.do",i).then((function(e){t.listTrigger=[];for(var i=0;i<e.data.data.length;i++)t.listTrigger.push(e.data.data[i]);t.info=e.info,t.listTriggerOld=[];for(i=0;i<t.listTrigger.length;i++)t.listTrigger[i].S_FLAG="1",t.listTriggerOld.push(t.listTrigger[i]);var n=t;setTimeout((function(){n.handlerOnShowTrigger()}),1),t.loadingTrigger=!1})).catch((function(){t.loadingTrigger=!1}))},handlerOnShowTrigger:function(){for(var e=0;e<this.listTrigger.length;e++)"1"==this.listTrigger[e].S_FLAG?this.$refs.multipleTableTrigger.toggleRowSelection(this.listTrigger[e],!0):this.$refs.multipleTableTrigger.toggleRowSelection(this.listTrigger[e],!1)},handlerShowHis:function(e){this.showDialogLogParams={database:"",table:"W_LOG_MODEL",params:{WAY:e["JOB_NAME"],URL:e["JOB_CLASS_NAME"]}},this.showDialogLog=!this.showDialogLog},handlerSaveAllTriggers:function(){var e=this;this.loadingTrigger=!0;for(var t=[],i=[],n={},o={},r=0;r<this.rowSelectTrigger.length;r++)n[this.rowSelectTrigger[r][this.colKeyTrigger]]="1";for(r=0;r<this.listTriggerOld.length;r++)o[this.listTriggerOld[r][this.colKeyTrigger]]=this.listTriggerOld[r]["S_FLAG"];for(var a in o)"1"!=o[a]||"0"!=n[a]&&null!=n[a]||i.push(a);for(var a in n)null!=o[a]&&"0"!=o[a]||"1"!=n[a]||t.push(a);if(t.length>0||i.length>0){var l=this.assign({},this.showTrigger);l["ON"]=t.join(","),l["OFF"]=i.join(","),this.get("/quartz/saveTriggers.do",l).then((function(t){e.loadingTrigger=!1})).catch((function(){e.loadingTrigger=!1}))}else this.$message({message:"没有改动",type:"waring"}),this.loadingTrigger=!1},handlerSelectionChangeTrigger:function(e){console.info("handlerSelectionChangeTrigger"+JSON.stringify(e)),this.rowSelectTrigger=e},handlerSortChange:function(e){console.info("handlerSortChange "+JSON.stringify(e)),this.page.order=e.prop+(e.order.startsWith("desc")?" desc":"")},handlerCurrentChange:function(e){console.info("handlerCurrentChange"),console.info(e),this.page.nowpage=e,this.getListPage()},handlerSizeChange:function(e){this.page.shownum=e,this.getListPage()},tableRowClassName:function(e){e.row;var t=e.rowIndex;return 1===t?"warning-row":3===t?"success-row":""}}}),a=r,l=(i("bdc3"),i("2877")),s=Object(l["a"])(a,n,o,!1,null,"34021093",null);t["default"]=s.exports},"5fc2":function(e,t,i){},bdc3:function(e,t,i){"use strict";var n=i("5fc2"),o=i.n(n);o.a}}]);