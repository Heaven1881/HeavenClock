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

    $("a#save-douban").click(saveDouban());
    $("a#save-setting").click(saveSetting());

    window.config.getSetting("drawSettingView");
});

$(document).on("pageinit", "#detail", function () {

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

    //var clock_id = getUrlParam('clock-id');
    //if (clock_id != null) {
    //    window.clock.getClockDetail(clock_id, "callbackClockDetail");
    //} else {
    //    var current = new Date();
    //    $("input#time").val(current.getHours() + ":" + current.getMinutes());
    //}
});
