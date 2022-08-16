$(function (){
    /*去登陆*/
    $("#toLogin").click(function (){
        window.location.href = rootPath+"/loan/page/login?ReturnUrl="+window.location.href;
    });

    /*超过一万查看余额*/
    $("#balance").hover(function (){
        var money = $("#s").val();
        $("#balance").html(" "+money+" ");
       /* $.get(rootPath+"/loan/page/balance",function (message) {
            if (message.code == 0) {
                $("#balance").html(message.message);
            }
            if (message.code == 1) {
                $("#balance").html(message.message);
            }
        })*/
    });

    /*鼠标离开后隐藏*/
    $("#balance").mouseleave(function (){
        var money = $("#s").val();
        if(money>=10000){
            $("#balance").html(" ***** ");
        }
    });
});
