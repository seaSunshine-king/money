
/*点击显示具体余额*/
$(function (){
    $("#showBalance").click(function (){

        var prop = $("#showBalance").prop("value");
        if(prop==null || prop==""){
            var money = $("#balance").val();
            $("#showBalance").prop("value",money);
            $("#showBalance").html("¥ "+money+" 元");
            return;
        }
        $("#showBalance").prop("value","");
        $("#showBalance").html(" ***** ");


    });


    $("#uploadHeader").click(function (){
        $("#headerImg").click();
        return;
    });

    $("#headerImg").change(function (){
        $("#headerUploadSubmit").click();

    });

    var tx = $("#tx").val();
    /* var value = tx.value;*/
   /* alert(tx)*/
    $("#user_photo").attr("src","/_005-money-web/images/uploadHeader/"+tx);
    /*img.prop("src",rootPath+"/images/uploadHeader/"+value);*/
    /*img.href()="/_005-money-web/images/uploadHeader/"+value;*/
    /*rootPath+"/images/uploadHeader/"+value;*/

    $("#recharge").click(function (){
        var ReturnUrl = window.location.href;
        window.location.href=rootPath+"/loan/page/toRecharge?ReturnUrl="+ReturnUrl;
    });
});



