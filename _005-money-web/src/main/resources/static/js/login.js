/*var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}*/

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		$("#loginSubmit").click();
	}
});
//错误提示
function login_showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}

//错误隐藏
function login_hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function login_showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

//成功隐藏
function login_hideOk(id) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Ok").html("");
	$("#"+id).removeClass("ok");
}



$(function (){
	var login_phoneFlag = 0;
	$("#loginPhone").blur(function (){
		var loginPhone = $.trim($("#loginPhone").val());
		if(loginPhone==null){
			login_phoneFlag = 0;
			$("#loginPhone").focus();
			login_showError("loginPhone","电话号码不能为空");
			return;
		}
		if(loginPhone==""){
			login_phoneFlag = 0;
			$("#loginPhone").focus();
			login_showError("loginPhone","电话号码不能为空");
			return;
		}

		//错误隐藏
		login_hideError("loginPhone");
		//显示成功
		login_showSuccess("loginPhone");
		login_phoneFlag = 1;
		return;
	});

	var login_passwordFlag = 0;
	$("#login_password").blur(function (){
		var loginPassword=$.trim($("#login_password").val());
		if(loginPassword==null){
			login_passwordFlag = 0;
			$("#login_password").focus();
			login_showError("login_password","密码不能为空");
			return;
		}
		if(loginPassword==""){
			login_passwordFlag = 0;
			$("#login_password").focus();
			login_showError("login_password","密码不能为空");
			return;
		}

		//错误隐藏
		login_hideError("login_password");
		//显示成功
		login_showSuccess("login_password");

		login_passwordFlag = 1;
		return true;
	});

	$("#loginSubmit").click(function (){
		$("#loginPhone").blur();
		$("#login_password").blur();
		if(login_phoneFlag==1 && login_passwordFlag==1){
			var loginPhone = $.trim($("#loginPhone").val());
			var loginPassword=$.trim($("#login_password").val());
			var ReturnUrl = $.trim($("#ReturnUrl").val());
			var data = {phone:loginPhone,loginPassword:$.md5(loginPassword)};
			$.post("/_005-money-web/loan/page/loginSubmit",data,function (message){
				if(message.code==1){
					window.location.href =ReturnUrl;
				}else if(message.code==0){
					login_hideOk("loginPhone");
					login_hideOk("login_password");
					var $i = $("#error");
					$i.html(message.message);
					$("#loginPhone").prop("value","");
					$("#login_password").prop("value","");
				}
			});
		}

	});
});
