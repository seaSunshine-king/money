//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});


var phoneFlag = 0;
var submit = false;
//注册电话号码验证
$(function (){
	$("#phone").blur(function (){
	var phone = $.trim($("#phone").val());
	//为null
	if(phone==null){
		phoneFlag = 0;
		//错误提示
		showError("phone","电话号码不能为空");
		return;
	}
	//为空格
	if (phone==""){
		phoneFlag = 0;
		showError("phone","电话号码不能为空");
		return;
	}
	//长度
	if(phone.length!=11){
		phoneFlag = 0;
		//错误提示
		showError("phone","电话号码长度有误");
		return;
	}
		//格式
	if(!(/^1[1-9]\d{9}$/.test(phone))){
		phoneFlag = 0;
		showError("phone","电话号码格式有误");
		return;
	}
	$.get("phone",{"phone":phone},function (message){
		if(message.code==0){
			phoneFlag=0;
			showError("phone","该号码已被注册");
		}
		if(message.code==1){
			phoneFlag=1;
		}
		if(phoneFlag==1 && passwordFlag==1 && submit==true && verify_Code==1) {
			alert(verify_Code)
			var verifyCode = $.trim($("#messageCode").val());
			var phone = $.trim($("#phone").val());
			var password = $.trim($("#loginPassword").val());
			var data = {phone: phone, loginPassword: $.md5(password),verifyCode:verifyCode};
			$.post("/_005-money-web/loan/page/registerSubmit", data, function (message) {
				if(message.code==0){
					showError("messageCode",message.message);
				}else if(message.code==1){
					var ReturnUrl = $.trim($("#ReturnUrl").val());
					alert(message.message)
					window.location.href = rootPath+"/loan/page/realName?ReturnUrl="+ReturnUrl;
				}
			});
		}

	});

		//错误隐藏
		hideError("phone");
		//显示成功
		showSuccess("phone");
		phoneFlag = 1;
		return true;
	});

	var passwordFlag = 0;
	//密码校验
	$("#loginPassword").blur(function (){
		var password = $.trim($("#loginPassword").val());
		if(password==null){
			passwordFlag = 0;
			showError("loginPassword","密码不能为空");
			return;
		}
		if(password==""){
			passwordFlag = 0;
			showError("loginPassword","密码不能为空");
			return;
		}
		if(!/^[0-9a-zA-Z]+$/.test(password)){
			passwordFlag = 0;
			showError("loginPassword","密码只能由数字和大小写英文字母组成");
			return;
		}

		if(!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(password)){
			passwordFlag = 0;
			showError("loginPassword","密码应同时同时包含英文和数字");
			return;
		}
		if(password.length<6 || password.length>20){
			passwordFlag = 0;
			showError("loginPassword","密码长度有误");
			return;
		}
		//错误隐藏
		hideError("loginPassword");
		//显示成功
		showSuccess("loginPassword");

		passwordFlag = 1;
		return true;

	});

	//点击短信验证码发送短信
	$("#messageCodeBtn").click(function (){
		/*hideError("message");*/
		var x = $("#loginPassword").blur();
		var y = $("#phone").blur();
		if(phoneFlag==1 && passwordFlag==1){
			var phone = $.trim($("#phone").val());
			var data = {"phone":phone};
			/*如果写在ajax中，当前对象就是ajax对象，写在外面才是按钮对象*/
			var _this = $(this);
			if(!$(this).hasClass("on")){
			$.get("/_005-money-web/loan/page/verifyCode",data,function (message){
				alert(message.message);
				if(message.code==0){
					showError("massage",message.message);
				}
				if(message.code==1){
						$.leftTime(60,function (d){
							if(d.status){
								_this.addClass("on");
								_this.html((d.s == "00"?"60":d.s)+"秒后重新获取");
							}else {
								_this.removeClass("on");
								_this.html("获取验证码");
							}
						});
					}else {
						showError("message", "请稍后重试...");
					}
			});
			}
		}
	});

	var verify_Code = 0;
	/*验证码前端校验*/
	$("#messageCode").blur(function (){
		var code = $.trim($("#messageCode").val());
		if(code==null){
			verify_Code = 0;
			showError("messageCode","验证码不能为空");
			return;
		}
		if(code==""){
			verify_Code = 0;
			showError("messageCode","验证码不能为空");
			return;
		}
		if(code.length!=4){
			verify_Code = 0;
			showError("messageCode","验证码长度错误");
			return;
		}
		if(!/^\d{4}$/.test(code)){
			verify_Code = 0;
			showError("messageCode","验证码格式错误");
			return;
		}
		//隐藏错误信息
		hideError("messageCode");
		//显示成功
		showSuccess("messageCode");

		verify_Code = 1;
		return;
	});

$("#btnRegist").click(function (){
	submit=true;
	var x = $("#loginPassword").blur();
	var y = $("#phone").blur();
	$("#messageCode").blur();
	/*alert(phoneFlag)
	alert(passwordFlag)*/
});

//已有账号，去登陆
	$("#toLogin").click(function (){
		var ReturnUrl = $.trim($("#ReturnUrl").val());
		var data = {ReturnUrl:ReturnUrl};
		window.location.href = rootPath+"/loan/page/login?ReturnUrl="+ReturnUrl;
	});

});
