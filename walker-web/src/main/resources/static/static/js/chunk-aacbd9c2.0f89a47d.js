(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-aacbd9c2"],{"9bea":function(e,t,n){"use strict";var a=n("ebe2"),o=n.n(a);o.a},ebe2:function(e,t,n){},f6dc:function(e,t,n){"use strict";n.r(t);var a=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"app-container"},[n("div",{directives:[{name:"loading",rawName:"v-loading",value:e.loadingCols,expression:"loadingCols"}],staticClass:"div-box-down"},[n("form",{staticClass:"form-inline"},[e._l(e.colMap,(function(t,a){return n("div",{staticClass:"form-group"},[n("label",[e._v(e._s(""==t?a:t))]),n("input",{directives:[{name:"model",rawName:"v-model",value:e.rowSearch[a],expression:"rowSearch[key]"}],staticClass:"form-control",staticStyle:{width:"10em","margin-right":"1em"},attrs:{type:"text",placeholder:a},domProps:{value:e.rowSearch[a]},on:{keyup:function(t){return t.type.indexOf("key")||13===t.keyCode?e.getListPage():null},input:function(t){t.target.composing||e.$set(e.rowSearch,a,t.target.value)}}})])})),n("el-button-group",[n("el-button",{staticClass:"btn btn-primary",on:{click:function(t){return e.getListPage()}}},[e._v("查询")]),n("el-button",{staticClass:"btn btn-success",on:{click:function(t){return e.handlerAddColumn()}}},[e._v("添加")]),n("el-button",{staticClass:"btn btn-danger",on:{click:function(t){e.clearRowSearch(),e.getListPage()}}},[e._v("清除")])],1)],2)]),n("div",{directives:[{name:"show",rawName:"v-show",value:!e.loadingCols,expression:"!loadingCols"}]},[n("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.loadingList,expression:"loadingList"}],ref:"multipleTable",attrs:{data:e.list,"row-class-name":e.tableRowClassName,"element-loading-text":"Loading",border:"",fit:"",stripe:"","show-summary":"","sum-text":"S","highlight-current-row":"","max-height":"86%"},on:{"selection-change":e.handlerSelectionChange,"sort-change":e.handlerSortChange}},[n("el-table-column",{attrs:{fixed:"left",aligin:"center",type:"selection","min-width":"12px"}}),n("el-table-column",{attrs:{fixed:"left",align:"center",type:"index","min-width":"12px"}}),e._l(e.colMap,(function(t,a){return n("el-table-column",{key:a,attrs:{property:a,label:""==t?a:t,sortable:"","show-overflow-tooltip":"","min-width":"100px"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v(" "+e._s(t.row[t.column.property])+" ")]}}],null,!0)})})),n("el-table-column",{attrs:{label:"操作","show-overflow-tooltip":"",fixed:"right","min-width":"111px"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("el-button-group",[n("el-button",{attrs:{size:"mini",type:"success",icon:"el-icon-download",circle:""},on:{click:function(n){return n.stopPropagation(),e.download(t.row)}}}),n("el-button",{attrs:{size:"mini",type:"primary",icon:"el-icon-edit",circle:""},on:{click:function(n){return n.stopPropagation(),e.handlerChange(t.row)}}}),n("el-button",{attrs:{size:"mini",type:"danger",icon:"el-icon-delete",circle:""},on:{click:function(n){return n.stopPropagation(),e.handlerDelete(t.row)}}})],1)]}}])},[e._v("success ")])],2),e.rowSelect.length>0?n("el-button",{staticClass:"btn btn-danger",staticStyle:{float:"left",margin:"4px 0px 0 2px"},on:{click:function(t){return e.handlerDeleteAll()}}},[e._v("删除选中")]):e._e(),n("el-pagination",{staticStyle:{float:"right",margin:"10px 20px 0 0"},attrs:{"current-page":e.page.nowpage,"page-size":e.page.shownum,"page-sizes":[2,4,8,16,32,64,128],"pager-count":9,layout:"total, sizes, prev, pager, next, jumper",total:e.page.num,background:""},on:{"current-change":e.handlerCurrentChange,"size-change":e.handlerSizeChange}}),n("el-dialog",{attrs:{title:"修改",visible:e.loadingUpdate,width:"86%","before-close":e.handlerCancel},on:{"update:visible":function(t){e.loadingUpdate=t}}},[[n("el-form",{directives:[{name:"loading",rawName:"v-loading",value:e.loadingSave,expression:"loadingSave"}],ref:"form",attrs:{model:e.rowUpdate,"label-width":"100px"}},[e._l(e.colMap,(function(t,a){return n("el-form-item",{key:a,attrs:{property:a,label:""==t?a:t}},[n("el-input",{attrs:{type:"text"},model:{value:e.rowUpdate[a],callback:function(t){e.$set(e.rowUpdate,a,t)},expression:"rowUpdate[key]"}})],1)})),n("el-form-item",[n("el-button",{attrs:{type:"primary"},on:{click:function(t){return e.handlerSave()}}},[e._v("确定")]),n("el-button",{attrs:{type:"danger"},on:{click:function(t){return e.handlerCancel()}}},[e._v("取消")])],1)],2)]],2)],1)])},o=[],i=(n("f559"),{filters:{statusFilter:function(e){var t={published:"success",draft:"gray",deleted:"danger"};return t[e]}},data:function(){return{list:[],colMap:{},colKey:"",rowSearch:{},rowUpdate:{},rowUpdateFrom:{},rowSelect:[],nowRow:null,page:{nowpage:1,num:0,order:"",pagenum:0,shownum:8},loadingList:!0,loadingCols:!0,loadingSave:!0,loadingUpdate:!1}},created:function(){this.getColumns()},methods:{getColumns:function(){var e=this;this.loadingCols=!0,this.get("/common/getColsMap.do",{tableName:"W_FILE_INDEX"}).then((function(t){e.colMap=t.data.colMap,e.colKey=t.data.colKey,e.clearRowSearch(),e.loadingCols=!1,e.getListPage()})).catch((function(){e.loadingCols=!1}))},clearRowSearch:function(){this.rowSearch={},this.page.nowpage=1},getListPage:function(){var e=this;this.loadingList=!0;var t=this.assign({nowPage:this.page.nowpage,showNum:this.page.shownum,order:this.page.order},this.rowSearch);this.get("/file/findPage.do",t).then((function(t){e.list=t.data.data,e.page=t.data.page,e.loadingList=!1})).catch((function(){e.loadingList=!1}))},handlerAddColumn:function(){var e=this.assign(this.nowRow?this.nowRow:{},this.rowSearch);e[this.colKey]="",e["S_FLAG"]="1",this.list.push(e),this.handlerChange(this.list[this.list.length-1])},handlerCurrentChangeTable:function(e){console.info("handlerCurrentChangeTable",e),this.nowRow=e},handlerChange:function(e){this.loadingUpdate=!this.loadingUpdate,this.loadingSave=!1,console.info("handlerChange "+JSON.stringify(e)),this.rowUpdateFrom=e,this.rowUpdate=JSON.parse(JSON.stringify(e)),this.loadingSave=!1},handlerCancel:function(){console.info("handlerCancel "+JSON.stringify(this.rowUpdate)),this.loadingUpdate=!this.loadingUpdate},handlerSave:function(){var e=this;console.info("handlerSave "+JSON.stringify(this.rowUpdate)),this.loadingSave=!0,this.rowUpdateFrom=Object.assign(this.rowUpdateFrom,this.rowUpdate);var t=this.rowUpdateFrom;this.post("/file/save.do",t).then((function(t){e.loadingSave=!1,e.loadingUpdate=!e.loadingUpdate})).catch((function(){e.loadingSave=!1}))},handlerDelete:function(e,t){var n=this;console.info("handlerDelete  "+JSON.stringify(e)),this.loadingList=!0;var a={ids:e[this.colKey]};this.get("/file/deleteByIds.do",a).then((function(t){for(var a=0;a<n.list.length;a++)n.list[a]==e&&n.list.splice(a,1);n.loadingList=!1})).catch((function(){n.loadingList=!1}))},handlerDeleteAll:function(){var e=this;if(console.info("handlerDeleteAll  "+JSON.stringify(this.rowSelect)),this.rowSelect.length>0){this.loadingList=!0;for(var t="",n=0;n<this.rowSelect.length;n++)t+=this.rowSelect[n][this.colKey]+",";t=t.substring(0,t.length-1);var a={ids:t};this.get("/file/deleteByIds.do",a).then((function(t){e.loadingList=!1;for(var n=0;n<e.rowSelect.length;n++)for(var a=0;a<e.list.length;a++)e.list[a]==e.rowSelect[n]&&e.list.splice(a,1);e.rowSelect=[]})).catch((function(){e.loadingList=!1}))}},handlerSelectionChange:function(e){console.info("handlerSelectionChange"+JSON.stringify(e)),console.info(e),this.rowSelect=e},handlerSortChange:function(e){console.info("handlerSortChange "+JSON.stringify(e)),this.page.order=e.prop+(e.order.startsWith("desc")?" desc":"")},handlerCurrentChange:function(e){console.info("handlerCurrentChange"),console.info(e),this.page.nowpage=e,this.getListPage()},handlerSizeChange:function(e){this.page.shownum=e,this.getListPage()},tableRowClassName:function(e){e.row;var t=e.rowIndex;return 1===t?"warning-row":3===t?"success-row":""},download:function(e){this.$message.success("下载文件"+e.NAME);var t=document.createElement("a");t.href="/file/download.do?key="+e.ID,t.click()}}}),l=i,r=(n("9bea"),n("2877")),s=Object(r["a"])(l,a,o,!1,null,"2b13eb8d",null);t["default"]=s.exports}}]);