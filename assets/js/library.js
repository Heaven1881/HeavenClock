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

function drawClockDetail(str) {
    var clockItem = eval('(' + str + ')');
    $("input#time").val(clockItem.time);
    $("input#radio-" + clockItem.for.substr(4).toLowerCase()).attr("checked", true).checkboxradio("refresh");
    if (clockItem.for == "FOR_WEEK") {
        $("li#week-checkbox").show();
        var week = clockItem.week;
        for (i in week) {
            if (week[i] == "1") {
                $("#cb-" + i).attr("checked", true).checkboxradio("refresh");
            }
        }
    }
    $("input#desc").val(clockItem.name == null ? "" : clockItem.name);
}

function drawSettingView(str) {
    var config = eval('(' + str + ')');
    $("input#douban-email").val(config.email);
    $("input#douban-pwd").val(config.pwd);
    $("input#repeat-time").val(config.repeatTime).slider("refresh");
    $("input#p-for-new").val(config.pForNew).slider("refresh");
    $("div#clock-input-group").hide(100);
}

function saveDouban() {
    var email = $("input#douban-email").val();
    var pwd = $("input#douban-pwd").val();
    window.config.updateDouban(email, pwd);
}

function saveSetting() {
    var repeatTime = $("input#repeat-time").val();
    var pForNew = $("input#p-for-new").val();
    window.config.updateSetting(repeatTime, pForNew);
}

function saveDetail(id) {
    var time = $("input#time").val();
    var hour = time.split(":")[0];
    var minute = time.split(":")[1];
    var type = $("input[name=type]:checked").val();
    var week = ["0", "0", "0", "0", "0", "0", "0"];
    $("input[name='repeat-day']:checked").each(function () {
        var i = $(this).val();
        week[i] = "1";
    });
    var name = $("input#desc").val();
    window.clock.updateClockEntry(id, hour, minute, type, week.toString(), name);
}

function drawClockView(str) {
    $("div#clock-list").empty();
    var clockList = eval('(' + str + ')');
    for (i in clockList) {
        var id = clockList[i].id;
        var name = clockList[i].name;
        var type = clockList[i].for;
        var active = clockList[i].active;
        var time = clockList[i].time;

        var typeStr = "";
        if (type == "FOR_ONCE") typeStr = "只响一次";
        if (type == "FOR_DAY") typeStr = "每天";
        if (type == "FOR_WEEK") typeStr = "自定义";

        var soff = "";
        var son = "";
        if (active)
            son = "selected";
        else
            soff = "selected";

        var content = "<ul data-id=\"" + id + "\" data-role=\"listview\" data-split-icon=\"delete\" data-inset=\"true\"><li>" +
            "<a href=\"detailView.html?clock-id=" + id + "\" data-transition=\"slide\" rel=\"external\">" +
            "<div class=\"clock-li-time\">" + time + "</div>" +
            "<div class=\"clock-li-desc-group\">" +
            "<strong class=\"clock-li-desc\">" + typeStr + "</strong>" +
            "<p class=\"clock-li-desc\">" + name + "</p></div>" +
            "<div class=\"clock-li-slider\">" +
            "<select id=\"s" + id + "\" data-role=\"slider\" onchange=\"switchActive(" + id + ")\" data-mini=\"true\">" +
            "<option value=\"off\" " + soff + ">off</option>" +
            "<option value=\"on\" " + son + ">on</option></select></div>" +
            "<a class=\"delete-btn\" href=\"#\" onclick='confirmAndDelete(" + id + ")'>删除</a>" +
            "</a></li></ul>";
        $("div#clock-list").append(content)
    }
    $("div#clock-list").append("<a href=\"detailView.html\" class=\"ui-btn ui-shadow ui-btn-corner-all\" data-transition=\"slide\">添加闹钟</a>");
    $("div#clock-list").trigger("create");
    window.console.info("draw clock view done");
}

function switchActive(id) {
    var status = $("#s" + id).val();
    window.clock.activeClock(id, status);
}


function confirmAndDelete(id) {
    $("#delete-confirm").popup("open");
    $("#delete-confirm #yes").on("click", function () {
        window.clock.deleteClockById(id);
    })
}

function simpleUpdate(content) {
    $("div#" + content).trigger("pageinit");
}

function drawAlarm(str) {
    var currentData = eval('(' + str + ')');
    $("#current-time").text(currentData.time);
    $("#current-music").text(currentData.music);
    $("#current-music").attr("data-sid", currentData.sid);
    $("#current-artist").text(currentData.artist);
    if (currentData.like) {
        $("#btn-like").text("已标记");
        $("#btn-like").attr("onclick", "unmarkCurrent()");
    } else {
        $("#btn-like").text("喜欢");
        $("#btn-like").attr("onclick", "markCurrent()");
    }
}

function unlikeCurrent() {
    window.song.unlikeCurrentSong();
}

function markCurrent() {
    $("#btn-like").text("已标记");
    $("#btn-like").attr("onclick", "unmarkCurrent()");
    window.song.markCurrentSong();
}

function unmarkCurrent() {
    $("#btn-like").text("喜欢");
    $("#btn-like").attr("onclick", "markCurrent()");
    window.song.unmarkCurrentSong();
}

function skipCurrent() {
    window.song.skipCurrentSong();
}

function closeAlarm() {
    window.activity.stop()
}