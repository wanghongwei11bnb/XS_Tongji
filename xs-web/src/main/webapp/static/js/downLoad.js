var downloadObj = Object.create(playObj);
var obj02 = {
	dom:$('.download'),
	slide:function(){},
	downmashow:function(){
		fnAddSpeedJs($('.bluemashow').get(0),{'opacity':'100'});
		$('.bluemashow').css('display','block');
	},
	downmahide:function(){
		fnAddSpeedJs($('.bluemashow').get(0),{'opacity':'0'});
		$('.bluemashow').css('display','none')
	},
	miniAppShow:function(){
		fnAddSpeedJs($('.miniAppShow').get(0),{'opacity':'100'});
		$('.miniAppShow').css('display','block');
	},
	miniAppHide:function(){
		fnAddSpeedJs($('.miniAppShow').get(0),{'opacity':'0'});
		$('.miniAppShow').css('display','none')
	},

	download:function(){
		$('.dl_icon1').click(function(){
			location.href ='https://itunes.apple.com/us/app/享睡空间/id1267711750?l=zh&ls=1&mt=8';
		});
		$('.dl_icon2').click(function(){
			location.href ='http://a.app.qq.com/o/simple.jsp?pkgname=com.xiangshuispace.capsule_android';
		});
		$('.phone_app_ios_store').click(function(){
			location.href ='https://itunes.apple.com/us/app/享睡空间/id1267711750?l=zh&ls=1&mt=8';
		});
		$('.phone_android').click(function(){
			location.href ='http://a.app.qq.com/o/simple.jsp?pkgname=com.xiangshuispace.capsule_android';
		});
	}

};

$.extend(downloadObj,obj02);
downloadObj.download();
