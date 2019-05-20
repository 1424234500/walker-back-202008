/**
 * 工具类服务
 */

(function () { 'use strict';
    angular.module('com.common')
        // 工具类方法
        .service('tools', ['$injector', '$rootScope', '$window', '$q', function ($injector, $rootScope, $window, $q) {
            /**
             * 替换文本中的链接(如网址http://www.baidu.com)
             * @param itemCon:文本内容
             */
            this.replaceLink = function (itemCon) {
                var urlReg = new RegExp(/(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?/g);
                var results = itemCon.match(urlReg);
                var arr = [];
                if (results) {//匹配url
                    for (var i = 0; i < results.length; i++) {
                        var strUrl = results[i];
                        var arep = "<a href=\"javascript:window.open('" + strUrl + "', '_blank', 'location=no');\" class='webchat_txt_tagA'>" + strUrl + "</a>"
                        itemCon = itemCon.replace(strUrl, arep);
                        var tempArr = itemCon.split(arep);
                        arr.push(tempArr[0] + arep);
                        itemCon = tempArr[1];
                    }
                }
                if (arr.length > 0) {
                    arr = arr.join("") + itemCon;
                } else {
                    arr = itemCon;
                }
                // return arr;
                return strUrl;
            };

            /**
             * 把带html群组的字符串处理成普通字符串
             */
            this.html2String = function (str) {
                if (!str) {
                    return "";
                }
                str = str.replace(/&nbsp;/g, " ");
                str = str.replace(/<p>/g, "");
                str = str.replace(/<\/p>/g, "\n");
                str = str.replace(/<br\/>/g, "\n");
                return str;
            };
            /**
             * 生成唯一识别码
             **/
            this.guid = function () {
                function s4() {
                    return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1);
                }

                return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
            };
            /**
             * 生成uuid
             * @param len 长度
             * @param radix
             * @returns {string}
             */
            this.uuid = function (len,radix) {
                var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
                    var chars = CHARS, uuid = [], i;
                    radix = radix || chars.length;
                    if (len) {
                        // Compact form
                        for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
                    } else {
                        // rfc4122, version 4 form
                        var r;
                        // rfc4122 requires these characters
                        uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
                        uuid[14] = '4';

                        // Fill in random data.  At i==19 set the high bits of clock sequence as
                        // per rfc4122, sec. 4.1.5
                        for (i = 0; i < 36; i++) {
                            if (!uuid[i]) {
                                r = 0 | Math.random()*16;
                                uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
                            }
                        }

                };
                return uuid.join('');
            };
            /**
             * 打开url
             */
            this.openUrl = function (url) {
                $window.open(url);
            };
            /**
             * 立即刷新页面，适用于不会自动刷新页面的情况：比如根据广播事件调整界面等情况
             * @param $scope 当前上下文对象
             * @param fn 要求执行的方法名
             */
            this.applySafe = function ($scope, fn) {
                ($scope.$$phase || $scope.$root.$$phase) ? fn() : $scope.$apply(fn);
            };
            /**
             * [getStyle 获取指定dom的指定样式]
             * @param  {[Object]} dom   [原生dom对象]
             * @param  {[string]} style [样式名，例如：marginTop]
             */
            this.getStyle = function (dom, style) {
                return $window.getComputedStyle(dom)[style];
            };
            /**
             * [setTitle 修改页面title]
             * @param  {[String]} title   [标题]
             */
            this.setTitle = function (title) {
                $rootScope._$title$_ = title;
            };
            /**
             * [showTopTips 顶部显示一个提示]
             * @param  {[string]} text [显示内容]
             * @param {[String]} type [类型，分为：normal, error, warning]
             */
            this.tip = function(text, type) {
                if (!type) {
                    type = 'normal';
                }
                $rootScope.$broadcast('$$showTopTips', {'text':text, 'type':type});
            };
            this.openLoad = function(time) {
                if (!time) {
                    time = 5000;
                }
                $rootScope.$broadcast('$$openLoad', {'time':time});
            };
            this.closeLoad = function() {
                $rootScope.$broadcast('$$closeLoad');
            };

            //获取操作系统
            this.getDetectOS = function detectOS() {
                var sUserAgent = navigator.userAgent;
                var isWin = (navigator.platform == "Win32") || (navigator.platform == "Windows");
                var isMac = (navigator.platform == "Mac68K") || (navigator.platform == "MacPPC") || (navigator.platform == "Macintosh") || (navigator.platform == "MacIntel");
                if (isMac) return "Mac";
                var isUnix = (navigator.platform == "X11") && !isWin && !isMac;
                if (isUnix) return "Unix";
                var isLinux = (String(navigator.platform).indexOf("Linux") > -1);
                if (isLinux) return "Linux";
                if (isWin) {
                    var isWin2K = sUserAgent.indexOf("Windows NT 5.0") > -1 || sUserAgent.indexOf("Windows 2000") > -1;
                    if (isWin2K) return "Win2000";
                    var isWinXP = sUserAgent.indexOf("Windows NT 5.1") > -1 || sUserAgent.indexOf("Windows XP") > -1;
                    if (isWinXP) return "WinXP";
                    var isWin2003 = sUserAgent.indexOf("Windows NT 5.2") > -1 || sUserAgent.indexOf("Windows 2003") > -1;
                    if (isWin2003) return "Win2003";
                    var isWinVista= sUserAgent.indexOf("Windows NT 6.0") > -1 || sUserAgent.indexOf("Windows Vista") > -1;
                    if (isWinVista) return "WinVista";
                    var isWin7 = sUserAgent.indexOf("Windows NT 6.1") > -1 || sUserAgent.indexOf("Windows 7") > -1;
                    if (isWin7) return "Win7";
                    var isWin8 = sUserAgent.indexOf("Windows NT 6.2") > -1 || sUserAgent.indexOf("Windows 8") > -1;
                    if (isWin8)  return "Win8";
                    var findFlag = sUserAgent.indexOf("Windows NT 6.3") > -1 || sUserAgent.indexOf("Windows 8.1") > -1;
                    if (findFlag) return "Win8.1";
                    var findFlag = sUserAgent.indexOf("Windows NT 10.0") > -1 || sUserAgent.indexOf("Windows 10") > -1;
                    if (findFlag) return "Win10";
                    return "Windows"
                }
                return "other";
            };

            this.browser = function() {
                var e, ua = navigator.userAgent.toLowerCase();
                if (null != ua.match(/trident/)) {
                    e = {
                        browser: "msie",
                        version: null != ua.match(/msie ([\d.]+)/) ? ua.match(/msie ([\d.]+)/)[1] : ua.match(/rv:([\d.]+)/)[1]
                    }
                } else {
                    var a = /(msie) ([\w.]+)/.exec(ua) || /(chrome)[ \/]([\w.]+)/.exec(ua) || /(webkit)[ \/]([\w.]+)/.exec(ua) || /(opera)(?:.*version|)[ \/]([\w.]+)/.exec(ua) || ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua) || [];
                    e = {
                        browser: a[1] || "",
                        version: a[2] || "0"
                    }
                }
                var n = {};
                return e.browser && (n[e.browser] = !0, n.version = e.version), n.chrome ? n.webkit = !0 : n.webkit && (n.safari = !0), n
            }();
            this.isImage = function(fileType) {
                var isImage = false;
                if (fileType&&fileType != 'image/vnd.ms-modi') {
                    if (fileType.indexOf('image') != -1
                    || fileType.indexOf('.jpg') != -1
                    || fileType.indexOf('.jpeg') != -1
                    || fileType.indexOf('.bmp') != -1
                    || fileType.indexOf('.gif') != -1
                    || fileType.indexOf('.png') != -1){
                        isImage = true;
                    }
                }
                return isImage;
            };
            // 校验电话号码
            this.checkTelphone = function(tel) {
                var phone = /^0\d{2,3}-?\d{7,8}$/;
                return phone.test(tel);
            };
            // 校验手机号码
            this.checkMobile = function(tel) {
                var mobile = /^1[3|5|7|8|9]\d{9}$/;
                return mobile.test(tel);
            };
            // 校验邮箱
            this.checkEmail = function(mail) {
                var regex = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
                return regex.test(mail);
            };
        }]);
})();