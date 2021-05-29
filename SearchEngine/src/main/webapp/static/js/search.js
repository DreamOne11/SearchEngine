//将输入光标移到form中的input上，login是form名，user是搜索框名
function giveFocus() {
    document.search.searchInfo.focus()
}
//提交表单
$(document).on('click','#searchBtn',function(){
var input = $("#searchInput").val();
    console.log(input);
    console.log(isEmpty(input));
    if(isEmpty(input)) {
        console.log("字符串为空!!!!");
        return;
    }
    console.log("字符串不为空!!!!");
    $("#Search").submit();
});

//重置表单
function resetForm() {
    document.search.reset()
    document.search.searchInfo.focus()
}

//判断字符串是否为空
function isEmpty(str) {
    if (str == null || str.trim() == "") {
        return true;
    }
    return false;
}

