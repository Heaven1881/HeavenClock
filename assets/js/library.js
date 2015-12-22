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
        for (var i in week) {
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
    $("input#history-song").val(config.historySong).slider("refresh");
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
    var historySong = $("input#history-song").val();
    window.config.updateSetting(repeatTime, pForNew, historySong);
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

function drawClockView() {
    var str = window.clock.getAllClockEntry();
    var clocks = eval('(' + str + ')');
    console.info(str);
    var $divClockList = $("div#clock-list").empty();

    var typeStr = {
        "FOR_ONCE": "只响一次",
        "FOR_DAY": "每天",
        "FOR_WEEK": "自定义"
    };

    for (var i in clocks) {
        var $ul = $('<ul data-role="listview" data-split-icon="delete" data-inset="true"><li><a class="clock-detail" rel="external"></a><a class="delete-btn"></a></li></ul>')
            .attr("data-id", clocks[i].id)
            .find("a.clock-detail")
            .attr("href", "detailView.html?clock-id=" + clocks[i].id)
            .append($('<div class="clock-li-time"></div>').text(clocks[i].time))
            .append($('<div class="clock-li-desc-group"></div>')
                .append('<strong>' + typeStr[clocks[i].for] + '</strong>')
                .append('<p>' + clocks[i].name + '</p>'))
            .append($('<div class="clock-li-slider"></div>').append('<select data-role="slider" data-mini="true"></select>'))
            .end().find("a.delete-btn")
            .attr("onclick", "confirmAndDelete(this)")
            .end().appendTo($divClockList);
        var $select = $ul.find("select").attr("onchange", "switchActive(this)");
        var $offSel = $('<option value="off"></option>').appendTo($select);
        var $onSel = $('<option value="on"></option>').appendTo($select);
        if (clocks[i].active) {
            $onSel.attr("selected", true);
        } else {
            $offSel.attr("selected", true);
        }

    }

    $('<a href="detailView.html" class="ui-btn ui-shadow ui-btn-corner-all" data-transition="slide"></a>').text("添加闹钟").appendTo($divClockList);
    $divClockList.trigger("create");
}

function switchActive(s) {
    var id = $(s).parents("ul").attr("data-id");
    var status = $(s).val();
    window.clock.activeClock(id, status);
}

function drawMusicList() {
    var str = window.clock.getHistory();
    var musicList = eval('(' + str + ')');
    musicList = musicList["songHistory"];
    var $musicList = $("#music-list").append("<a id='stop-btn' href=\"#\" class=\"ui-btn ui-shadow ui-btn-corner-all\" onclick='simpleStop()'>停止播放</a>");
    console.info("start draw music");
    for (var i in musicList) {
        console.info(musicList[i].name);
        var $el = $('<li><a><h2></h2><p></p></a></li>');
        $el.find('a').attr('data-ad', musicList[i].playedTime)
            .attr('onclick', 'simplePlay("' + musicList[i].url + '")');
        $el.find('h2').text(musicList[i].name);
        $el.find('p').text(musicList[i].artist);
        $("#music-list").append($el);
    }

    $musicList.listview({
        autodividers: true,
        autodividersSelector: function (li) {
            return $(li).find('a').attr('data-ad');
        }
    }).listview("refresh");
    $("#stop-btn").hide();
}

function simplePlay(url) {
    $("#stop-btn").show();
    window.simplePlayer.simplePlay(url);
}

function simpleStop() {
    $("#stop-btn").hide();
    window.simplePlayer.simpleStop();
}

function confirmAndDelete(btn) {
    var id = $(btn).parents("ul").attr("data-id");
    $("#delete-confirm").popup("open").find("#yes").on("click", function () {
        window.clock.deleteClockById(id);
    })
}

function simpleUpdate(content) {
    //window.clock.getAllClockEntry("drawClockView");
    $("#" + content).trigger("create");

}

function drawAlarm(str) {
    var currentData = eval('(' + str + ')');
    $("#current-time").text(currentData.time);
    $("#current-music").text(currentData.music).attr("data-sid", currentData.sid);
    $("#current-artist").text(currentData.artist);
    if (currentData.like) {
        $("#btn-like").text("已标记").attr("onclick", "unmarkCurrent()");
    } else {
        $("#btn-like").text("喜欢").attr("onclick", "markCurrent()");
    }
}

function unlikeCurrent() {
    window.song.unlikeCurrentSong();
}

function markCurrent() {
    $("#btn-like").text("已标记").attr("onclick", "unmarkCurrent()");
    window.song.markCurrentSong();
}

function unmarkCurrent() {
    $("#btn-like").text("喜欢").attr("onclick", "markCurrent()");
    window.song.unmarkCurrentSong();
}

function skipCurrent() {
    window.song.skipCurrentSong();
}

function closeAlarm() {
    window.activity.stop()
}