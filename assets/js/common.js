/**
 * Created by Heaven on 15/7/19.
 */

$(document).on("pageinit", "#setting", function () {

    $("div#douban-input-group").hide();
    $("input#douban-email").on("input", function () {
        $("div#douban-input-group").show(100);
    });
    $("input#douban-pwd").on("input", function () {
        $("div#douban-input-group").show(100);
    });
    $("input#douban-reset").click(function () {
        $("div#douban-input-group").hide(100);
    });

    $("div#clock-input-group").hide();
    $("input#repeat-time").change(function () {
        $("div#clock-input-group").show(100);
    });
    $("input#p-for-new").change(function () {
        $("div#clock-input-group").show(100);
    });
    $("input#clock-reset").click(function () {
        $("div#clock-input-group").hide(100);
    });
});

$(document).on("pageinit", "#detail", function () {
    //更新当前时间
    var current = new Date();
    $("input#time").val(current.getHours() + ":" + current.getMinutes());

    // 仅当选择自定义时出现星期复选框
    $("li#week-checkbox").hide();
    $("input[name=type]").click(function () {
        var id = $("input[name=type]:checked").attr("id");
        if (id == "radio-every-week") {
            $("li#week-checkbox").show(100);
        } else {
            $("li#week-checkbox").hide(100);
        }
    });

});

