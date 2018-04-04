var bPause = false;
function startMove(obj,attr,iTarget,fnMoveEnd)
{
    if(obj.timer)
    {
        clearInterval(obj.timer);
    }
    obj.timer=setInterval(function(){
        if(bPause)
        {
            return;
        }
        doMove(obj,attr,iTarget,fnMoveEnd);
    },30)
}
function getAttr(obj,attr)
{
    if(obj.currentStyle)
    {
        return obj.currentStyle[attr];
    }
    else
    {
        return document.defaultView.getComputedStyle(obj,false)[attr];
    }
}
function doMove(obj,attr,iTarget,fnMoveEnd)
{
    var iSpeed=0;
    var weizhi=0;
    if(attr=="opacity")
    {
        weizhi=parseFloat(getAttr(obj,"opacity"));
    }
    else
    {
        weizhi=parseFloat(getAttr(obj,attr))
    }
    if(Math.abs(iTarget-weizhi)<1/100)
    {
        clearInterval(obj.timer);
        obj.timer=null;
        if(fnMoveEnd)
        {
            fnMoveEnd();
        }
    }
    else
    {
        iSpeed=(iTarget-weizhi)/8;
        if(attr=="opacity")
        {
            obj.style.filter="alpha(opacity:"+(weizhi+iSpeed)*100+")";
            obj.style.opacity=weizhi+iSpeed;
        }
        else
        {
            iSpeed=iSpeed>0?Math.ceil(iSpeed):Math.floor(iSpeed);
            obj.style[attr]=weizhi+iSpeed+"px";
        }
    }
}
function leaveMessage()
{
    var oText=document.getElementById("txt1");
    createDom(oText.value);
    oText.value="";
}
var appraiseSave;
function createDom(sTxt,idName)
{
    var oUl=document.getElementById(idName);
    var aLi=oUl.getElementsByTagName("li");
    var oLi=document.createElement("li");
    var iHeight=0;
    var appraise_text_width = (window.innerWidth-document.getElementById('order').clientWidth)*0.9;
    var userName = '用户'+ (sTxt.phone ? subLastStringUtil(sTxt.phone,4) : subLastStringUtil(sTxt.uin,4));
    if(sTxt.appraise || sTxt.suggest){
        var appraiseText = sTxt.suggest ? sTxt.suggest : sTxt.appraise.join('、');
        oLi.innerHTML = "<div class='appraise_text' style='width: "+appraise_text_width +"px'>"+userName+' : &nbsp;&nbsp;'+appraiseText+"</div><div style='float: right'>"+ dateUtil('Y-m-d h:i:s',sTxt.createtime) +"</div>";
    }else{
        oLi.innerHTML = ""
    }
    oLi.style.filter="alpha(opacity:0)";
    oLi.style.opacity=0;
    oLi.style.width='100%';
    if(aLi.length){
        oUl.insertBefore(oLi,aLi[0])
    }
    else{
        oUl.appendChild(oLi)
    }
    //开始运动
    iHeight=oLi.offsetHeight;
    oLi.style.height="0px";
    oLi.style.overflow='hidden';
    startMove(oLi,"height",iHeight,function(){
        startMove(oLi,"opacity",1)
    });
    appraiseSave = dateUtil('Y-m-d h:i:s',sTxt.createtime);
}