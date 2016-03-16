/**
 * Created by Heaven on 16/1/27.
 */

var DEBUG = false;
var PAGE_INIT = {
    // page init 的渲染功能函数
    '_render': function (pageId, data) {
        // 寻找pageId下的.content和.content-template
        var source = $('#' + pageId + ' ' + '.content-template').html();
        var template = Handlebars.compile(source);
        var htmlStr = template(data);
        $('#' + pageId + ' ' + '.content').html(htmlStr);
    },

    // page的init函数
    'page-alarm': function (e, pageId, $page) {
        var initJsForPage = function (data) {
            $('#like-btn').on('click', function (e) {
                if ($(this).hasClass('button-danger')) {
                    // 取消标记
                    PlayCtrl.unrateCurrentSong();
                    $(this).removeClass('button-danger').addClass('button-light');
                } else {
                    // 标记为喜欢
                    PlayCtrl.rateCurrentSong();
                    $(this).removeClass('button-light').addClass('button-danger');
                }
            });

            $('#bye-btn').on('click', function (e) {
                // 标记为不再播放
                PlayCtrl.byeCurrentSong();
            });

            $('#skip-btn').on('click', function (e) {
                // 跳过当前歌曲
                PlayCtrl.skipCurrentSong();
            });

            $('#close-btn').on('click', function (e) {
                // 关闭闹钟
                Activity.closeActivity()
            });

        };

        if (DEBUG) {
            $.getJSON('../mock/getCurrentSongAndClock.json', function (data) {
                PAGE_INIT._render(pageId, data);
                initJsForPage(data);
            });
        } else {
            var data = JSON.parse(PlayCtrl.getCurrentSongAndClock());
            PAGE_INIT._render(pageId, data);
            initJsForPage(data);
        }

    }
};

/**
 * pageinit调用
 */
$(document).on("pageInit", function (e, pageId, $page) {
    var initFunc = PAGE_INIT[pageId];
    if (initFunc == null || typeof initFunc != 'function') {
        console.warn('init function not found, pageId: ' + pageId);
        return;
    }
    initFunc(e, pageId, $page);
});


/**
 * 页面初始化
 */
$.init();