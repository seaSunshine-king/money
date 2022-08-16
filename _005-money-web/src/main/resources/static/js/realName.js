
//同意实名认证协议
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

var i =1;
$(function (){

	var s =  0;
	var phoneFlag = 0;
	/*电话号码非空，长度。格式验证*/
	$("#phone").blur(function (){
		var phone = $.trim($("#phone").val());

		//非空
		if(phone==null){
			showError("phone","电话号码不能为空");
			phoneFlag = 0;
			return;
		}
		//空格
		if(phone==""){
			showError("phone","电话号码不能为空");
			phoneFlag = 0;
			return;
		}

		//长度
		if(phone.length!=11){
			showError("phone","电话号码长度有误");
			phoneFlag = 0;
			return;
		}
		//格式
		if(!(/^1[1-9]\d{9}$/.test(phone))) {
			showError("phone", "电话号码格式有误");
			phoneFlag = 0;
			return;
		}
		hideError("phone");
		showSuccess("phone");
		phoneFlag =1;
		return true;

	});

	var realNameFlag = 0;
	//姓名校验
	$("#realName").blur(function (){
		var realName = $.trim($("#realName").val());
		//非空
		if(realName==null){
			showError("realName","姓名不能为空");
			realNameFlag = 0;
			return;
		}

		//空格
		if(realName==""){
			showError("realName","姓名不能为空");
			realNameFlag = 0;
			return;
		}

		//格式
		if(!/^[\u4E00-\u9FA5]{2,10}(·[\u4E00-\u9FA5]{2,10}){0,2}$/.test(realName)){
			showError("realName","姓名格式错误");
			realNameFlag = 0;
			return;
		}

		hideError("realName");
		showSuccess("realName");
		realNameFlag=1;
		return true;

	});

	var idCardFlag = 0;
	//身份证号校验
	$("#idCard").blur(function (){
		var idCard = $.trim($("#idCard").val());

		//非空
		if(idCard==null){
			showError("idCard","身份证号码不能为空");
			idCardFlag = 0;
			return;
		}
		//空格
		if(idCard==""){
			showError("idCard","身份证号码不能为空");
			idCardFlag = 0;
			return;
		}

		//长度
		if(idCard.length!=18){
			showError("idCard","身份证号码长度有误");
			idCardFlag = 0;
			return;
		}

		//格式
		//  /^([1-6][1-9]|50)\d{4}\d{2}((0[1-9])|10|11|12)(([0-2][1-9])|10|20|30|31)\d{3}$/
		if(!/^([1-6][1-9]|50)\d{4}(18|19|20)\d{2}((0[1-9])|10|11|12)(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(idCard)){
			showError("idCard","身份证号码格式错误");
			idCardFlag = 0;
			return;
		}

		var realName = $.trim($("#realName").val());
		var data = {name:realName,idCard:idCard};
		$.get("/_005-money-web/loan/page/realNameAndIdCard",data,function (message){
			if(message.code==0){
				showError("idCard",message.message);
				idCardFlag=0;
			}
			if(message.code==1){
				showSuccess("idCard");
				idCardFlag=1;
			}
		});

		hideError("idCard");
		showSuccess("idCard");
		idCardFlag =1;
		return true;

	});

	var verifyCodeFlag =0;
	//验证码校验
	$("#messageCode").blur(function (){
		var code = $.trim($("#messageCode").val());
		if(code==null){
			verifyCodeFlag =0;
			showError("messageCode","验证码不能为空");
			return;
		}
		if(code==""){
			verifyCodeFlag =0;
			showError("messageCode","验证码不能为空");
			return;
		}
		if(code.length!=4){
			verifyCodeFlag =0;
			showError("messageCode","验证码长度错误");
			return;
		}
		if(!/^\d{4}$/.test(code)){
			verifyCodeFlag =0;
			showError("messageCode","验证码格式错误");
			return;
		}
		//隐藏错误信息
		hideError("messageCode");
		//显示成功
		showSuccess("messageCode");

		verifyCodeFlag = 1;


	});

	//点击按钮发送验证码
	$("#messageCodeBtn").click(function (){
		if(phoneFlag==1&&realNameFlag==1&&idCardFlag==1){
			var phone = $.trim($("#phone").val());
			var data = {phone:phone};
			var _this = $(this);
			if(!$(this).hasClass("on")){
				$.get("/_005-money-web/loan/page/verifyCode",data,function (message){
					alert(message.message);
					if(message.code==0){
						showError("message",message.message);
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
						showError("message","请稍后重试");
					}
				});
			}
		}

	});

	$("#btnRegist").click(function (){
		if(phoneFlag==1&&realNameFlag==1&&idCardFlag==1&&verifyCodeFlag==1){
			var phone = $.trim($("#phone").val());
			var realName = $.trim($("#realName").val());
			var idCard = $.trim($("#idCard").val());
			var code = $.trim($("#messageCode").val());
			var data = {phone:phone,realName:realName,idCard:idCard,code:code};
			$.post("/_005-money-web/loan/page/realNameSubmit",data,function (message){
				if(message.code==0){
					showError("message",message.message);
				}
				if(message.code==1){
					var ReturnUrl = $.trim($("#ReturnUrl").val());
					window.location.href = ReturnUrl;
				}
			});
		}
	});

	//跳过实名认证
	$("#jumpOver").click(function (){
		var ReturnUrl = $.trim($("#ReturnUrl").val());
		window.location.href = ReturnUrl;
	});
});