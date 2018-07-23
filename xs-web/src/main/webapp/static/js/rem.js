(function (doc, win) {
    //orientationchange : 判断手机是水平方向还是垂直方向，感应方向

    //doc ==》 document对象
    //doc.documentElement ==> 得到文档的根元素-->  <html>
    //之所以要得到文档的根元素，是为了计算网页所打开时屏幕的真实宽度
    var docEl = doc.documentElement,
        resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',
        recalc = function () {
            var clientWidth = docEl.clientWidth;
            if (!clientWidth) return;
            //320 是我们默认的手机屏幕
            //clientWidth 是我们页面打开时所得到的屏幕（可看见页面的真实宽度）宽度真实的宽度值
            //这两者相除得到一个放大或缩小的比例值
            //320 ip5 --> 20px
            //414 ip6s --> 25px;
            //width:2rem;
            docEl.style.fontSize = 20 * (clientWidth / 320) + 'px';
            //设置根元素font-size
        };
    /*600px
     20 * 600/320  -- >  [2 -- 3] 放大范围

     200/320 -- > [0.5 -- 0.1] 缩小*/
    if (!doc.addEventListener) return;
    win.addEventListener(resizeEvt, recalc, false);
    doc.addEventListener('DOMContentLoaded', recalc, false);
    //当dom加载完成时，或者 屏幕垂直、水平方向有改变进行html的根元素计算
})(document, window);

//如果你不想进行一个响应式设计的开发，你可以直接把font-size写死

//判断浏览器内核是否为trident,是否为ie浏览器
function isIe(){
    if(navigator.userAgent.toLocaleLowerCase().indexOf('trident')>-1){
        return true
    }else{
        return false
    }
}
var dynamicLoading = {
    css: function(path){
        if(!path || path.length === 0){
            throw new Error('argument "path" is required !');
        }
        var head = document.getElementsByTagName('head')[0];
        var link = document.createElement('link');
        link.href = path;
        link.rel = 'stylesheet';
        link.type = 'text/css';
        head.appendChild(link);
    },
    js: function(path){
        if(!path || path.length === 0){
            throw new Error('argument "path" is required !');
        }
        var head = document.getElementsByTagName('head')[0];
        var script = document.createElement('script');
        script.src = path;
        script.type = 'text/javascript';
        head.appendChild(script);
    }
};
if(isIe()){
    dynamicLoading.js("/static/js/swiper2.min.js");
    dynamicLoading.css("/static/css/swiper2.min.css");
}else{
    dynamicLoading.js("/static/js/swiper3.min.js");
    dynamicLoading.css("/static/css/swiper3.min.css");
}
if(!document.addEventListener){
    dynamicLoading.js("/static/js/ieBetter.js");
}

window.isIE8 = navigator.appVersion.indexOf('MSIE 8.0') != -1;