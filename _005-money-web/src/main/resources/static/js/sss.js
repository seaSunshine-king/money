var file; // 定义一个全局变量，为一个文本选择器。
var chooseImage; // 用于上传的文件
file = $('<input type="file" />'); // 这样file就是jquery创建的一个文本选择器，但是因为我们并没有把它加载到页面上，所以是不可见的。

// button的单击事件
$('#chooseImg').click(function(){
    // 启动文件选择
    file.click();
});

// 选择好文件后，获取选择的内容
file.change(function(e){
    var select_file = file[0].files[0];

    // 校验文件名称
    var file_path = file.val();
    var extStart = file_path.lastIndexOf("."); // 按.分隔文件名称
    var ext = file_path.substring(extStart, file_path.length).toUpperCase(); // 取出后缀并转为大写
    if (ext != ".BMP" && ext != ".PNG" && ext != ".JPG" && ext != ".JPEG") {
        bootbox.alert({
            size: "small",
            title: "提示",
            message: "图片仅限于bmp，png，jpg，jpeg的格式！",
            buttons: {
                ok: {
                    label: '确认',
                    className: 'btn-primary'
                }
            },
            callback: function(){ /* your callback code */ }
        })
        return false;
    }

    // 校验文件大小
    if (select_file.size > 1048576) {
        bootbox.alert({
            size: "small",
            title: "提示",
            message: "图片大小不能超过1M！！",
            buttons: {
                ok: {
                    label: '确认',
                    className: 'btn-primary'
                }
            },
            callback: function(){ /* your callback code */ }
        })
        return false;
    }

    chooseImage = select_file; // 赋值给全局变量 --> 用来做上传的操作

    // 展示到页面
    var reader = new FileReader();// 读取文件URL
    reader.readAsDataURL(chooseImage);
    reader.onload = function() {
        // 读取的URL结果：this.result
        $("#re_headImg").attr("src", this.result).show();
    }

    $("#uploadImg").removeClass("update-head-msg-bt-dis");
    $("#uploadImg").removeAttr("disabled");
    $("#uploadImg").attr("title","选择提交即可上传该图片文件作为头像.");
});

// 头像上传提交事件
$("#uploadHeadImg_form").submit(function () {
    event.preventDefault();
    var formData = new FormData(); // 要提交的内容封装进formdata
    formData.append("file", chooseImage);
    formData.append('userId', [[${userInfo.id}]])
    axios({
        method: 'post',
        url: '/uploadImage/uploadHeadImg',
        data: formData
    })
        .then(function (response) {
            if (response.data.state == 0) { // 说明上传失败
                bootbox.alert({
                    size: "small",
                    title: "修改提示",
                    message: "上传头像失败",
                    buttons: {
                        ok: {
                            label: '确认',
                            className: 'btn-primary'
                        }
                    },
                    callback: function(){ /* your callback code */ }
                })
            } else if (response.data.state == 1) { // 说明添加成功

                // 更新页面数据
                var reader = new FileReader();// 读取文件URL
                reader.readAsDataURL(chooseImage);
                reader.onload = function() {
                    // 读取的URL结果：this.result
                    $("#avatar_img").attr("src", this.result).show(); // 异步更新页面上的头像图片
                    $("#local_headImg").attr("src", this.result).show(); // 异步更新页面上的头像图片
                    $("#uploadImg").addClass("update-head-msg-bt-dis");
                    $("#uploadImg").attr("disabled","disabled");
                    $("#uploadImg").attr("title","请先选择要上传的图片文件.");
                }

                bootbox.alert({
                    size: "small",
                    title: "修改提示",
                    message: "上传头像成功",
                    buttons: {
                        ok: {
                            label: '确认',
                            className: 'btn-primary'
                        }
                    },
                    callback: function(){ /* your callback code */ }
                })
            }

        })
        .catch(function (error) {
            console.log(error);
        });
});
