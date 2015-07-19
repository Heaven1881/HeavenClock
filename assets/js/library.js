/**
 * Created by Heaven on 15/7/19.
 */

function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)
        return r[2];
    return null;
}

function callbackClockDetail(str) {
    var clockItem = eval('(' + str + ')');
    $("input#time").val(clockItem.time);
    $("input#radio-" + clockItem.for).attr("checked", true).checkboxradio("refresh");
    if (clockItem.for == "every-week") {
        $("li#week-checkbox").show();
        var week = clockItem.week;
        for (i in week) {
            if (week[i]) {
                $("#cb-" + i).attr("checked", true).checkboxradio("refresh");
            }
        }
    }
}

function drawSettingView(str) {
    var config = eval('(' + str + ')');
    $("input#douban-email").val(config.email);
    $("input#douban-pwd").val(config.pwd);
    $("input#repeat-time").val(config.repeatTime);
    $("input#p-for-new").val(config.pForNew);
}

function saveDouban() {
    var email = $("input#douban-email").val();
    var pwd = $("input#douban-pwd").val();
    window.config.updateDouban(email, pwd);
}

function saveSetting() {
    var repeatTime = $("input#repeat-time");
    var pForNew = $("input#p-for-new");
    window.config.updateSetting(repeatTime, pForNew);
}