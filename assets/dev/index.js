/**
 * Status保存当前页面状态
 */
var Status = {
    currentPageId: null,    // 当前的pageId
    openCid: null,          // 当前活跃的clock id
    popupWeekInput: null,   // popup-select-week选择的星期
    activeSongId: null,
};


var DEBUG = false;

/**
 * 每个page的数据渲染函数
 */
var PAGE_INIT = {
    // page init 的渲染功能函数
    '_render': function (pageId, data) {
        // 寻找pageId下的.content和.content-template
        var source = $('#' + pageId + ' ' + '.content-template').html();
        var template = Handlebars.compile(source);
        var htmlStr = template(data);
        $('#' + pageId + ' ' + '.content').html(htmlStr);
    },

    // page的渲染函数定义位置
    'page-clockList': function (e, pageId, $page) {
        var initJsForPage = function (data) {
            // 编辑闹钟
            $('.clock-edit').on('click', function (e) {
                Status.openCid = $(e.target).parents('.clock-content').attr('data-cid');

                var oprationBtns = [
                    {
                        text: '编辑',
                        onClick: function(e) {
                            $.router.load('#page-clockDetail');
                        }
                    },
                    {
                        text: '删除',
                        color: 'danger',
                        onClick: function() {
                            $.showIndicator();
                            ClockCtrl.deleteClockEntry(JSON.stringify({
                                cid: Status.openCid
                            }));
                            $.hideIndicator();
                            $.router.load('#page-clockList')
                        }
                    }
                ];
                var cancelBtn = [
                    {
                        text: '取消'
                    }
                ];
                $.actions([oprationBtns, cancelBtn]);
            });

            // 新建闹钟
            $('.clock-create').on('click', function (e) {
                Status.openCid = null;
            });

            // 激活和取消闹钟
            $('.clock-switch').on('change', function (e) {
                var cid = $(e.target).parents('.clock-content').attr('data-cid');
                var active = $(e.target).is(':checked');
                ClockCtrl.activeClock(JSON.stringify({
                    cid: cid,
                    active: active
                }));
            });
        };

        if (DEBUG) {
            $.getJSON('../mock/getClockEntries.json', function (data) {
                PAGE_INIT._render(pageId, data);
                initJsForPage(data);
            });
        } else {
            var data = JSON.parse(ClockCtrl.getClockEntries());
            PAGE_INIT._render(pageId, data);
            initJsForPage(data);
        }

    },

    'page-clockDetail': function (e, pageId, $page) {
        var openCid = Status.openCid;
        var initJsForPage = function (data) {
            //渲染popup页面
            var template = Handlebars.compile($('.popup-select-week-template').html());
            $('.popup-select-week').html(template(data));

            // 渲染时间选择器
            var timeArray = data.time.split(':');
            $('#input-time').val((timeArray[0] < 10 ? '0' + parseInt(timeArray[0]) : '' + timeArray[0]) + ' : '
                + (timeArray[1] < 10 ? '0' + parseInt(timeArray[1]) : '' + timeArray[1]));
            $('#input-time').timePicker({value: [timeArray[0], ':', timeArray[1]]});

            // 渲染星期选择器
            var weekStrArray = [];
            Status.popupWeekInput = [];
            for (var i in data.week.split(',')) {
                weekStrArray.push(WEEK_STR[data.week.split(',')[i]]);
                Status.popupWeekInput.push(data.week.split(',')[i]);
            }
            $('#input-week').val(weekStrArray.join(','));
            $('.popup-select-week .close-popup').on('click', function (e) {
                var selectedWeek = [];
                $('input[name="week"]:checked').each(function () {
                    selectedWeek.push($(this).val());
                });
                Status.popupWeekInput = selectedWeek;
                var weekStrArray = [];
                for (var i in selectedWeek) {
                    weekStrArray.push(WEEK_STR[selectedWeek[i]]);
                }
                $('#input-week').val(weekStrArray.join(','));
            });

            // 仅当选择类型为自定义时才出现星期选择窗口
            if (data.type != 'FOR_WEEK') {
                $('.input-week-item').hide(100);
            }
            $('#input-type').on('change', function (e) {
                if ($(e.target).val() == 'FOR_WEEK') {
                    $('.input-week-item').show(100);
                } else {
                    $('.input-week-item').hide(100);
                }
            });

            // 保存按钮
            $('#save-clock-btn').on('click', function (e) {
                $.showIndicator();
                var data = {
                    cid: Status.openCid == null ? 0 : Status.openCid,
                    active: true,
                    time: $('#input-time').val().replace(/ /g, ''),
                    type: $('#input-type').val(),
                    week: Status.popupWeekInput == null ? '' : Status.popupWeekInput.join(','),
                    desc: $('#input-desc').val()
                };
                //console.info(data);
                ClockCtrl.setClockEntry(JSON.stringify(data));
                $.hideIndicator();
                $.router.load('#page-clockList');
            });
        };
        if (openCid != null) {      // 若指定cid则从后台获取数据
            if (DEBUG) {
                $.getJSON('../mock/getClockEntry.json', function (data) {
                    PAGE_INIT._render(pageId, data);
                    initJsForPage(data);
                });
            } else {
                var data = JSON.parse(ClockCtrl.getClockEntry(JSON.stringify({cid: openCid})));
                PAGE_INIT._render(pageId, data);
                initJsForPage(data);
            }
        } else {                    //否则使用默认数据
            var now = new Date();
            var data = {
                time: now.getHours() + ':' + now.getMinutes(),
                type: 'FOR_ONCE',
                week: '',
                desc: ''
            };
            PAGE_INIT._render(pageId, data);
            initJsForPage(data);
        }
    },

    'page-songHistory': function (e, pageId, $page) {
        var initJsForPage = function (data) {
            // 打开操作表
            $('.song-operation').on('click', function (e) {
                console.info($(e.target))
                var oprationBtns = [
                    {
                        text: '播放',
                        bold: true,
                        onClick: function(btn) {
                            var playedTime = $(e.target).parents('li').attr('data-playedTime');
                            var retStr = Activity.simplePlayByPlayedTime(JSON.stringify({
                                'played_time' : playedTime
                            }));
                            var currentSong = JSON.parse(retStr);
                            $('#left-title').text(currentSong.title);

                            $.openPanel('.player-panel')
                        }
                    },
                    {
                        text: '删除',
                        color: 'danger',
                        onClick: function() {
                            $.alert("好好的一首歌,你删它干嘛呀~");
                        }
                    }
                ];
                var cancelBtn = [
                    {
                        text: '取消'
                    }
                ];
                $.actions([oprationBtns, cancelBtn]);
            });

            // 查看图片
            $('.pb-popup').on('click', function (e) {
                var browser = $.photoBrowser({
                    photos: [$(this).find('img').attr('src')],
                    type: 'popup'
                });
                browser.open();
            });

            //右侧播放器 停止 按钮
            $('#left-stop').on('click', function (e) {
                Activity.simpleStop();
            });
        };

        if (DEBUG) {
            $.getJSON('../mock/getSongHistory.json', function (data) {
                PAGE_INIT._render(pageId, data);
                initJsForPage(data);
            });
        } else {
            var data = JSON.parse(ClockCtrl.getHistory());
            PAGE_INIT._render(pageId, data);
            initJsForPage(data);
        }

    },

    'page-settings': function (e, pageId, $page) {
        var initJsForPage = function (data) {
            $('.number-picker').each(function () {
                $(this).numberPicker({
                    min: $(this).attr('data-min'),
                    max: $(this).attr('data-max'),
                    step: $(this).attr('data-step'),
                    unit: $(this).attr('data-unit')
                })
            });

            // 保存按钮
            $('#save-settings-btn').on('click', function (e) {
                var data = {
                    douban_email: $('#input-email').val(),
                    douban_password: $('#input-password').val(),
                    repeat: $('#input-repeat').val(),
                    p_for_new: $('#input-pForNew').val().split('%')[0] / 100.0,
                    max_history: $('#input-maxHistory').val()
                };
                ConfigCtrl.setSettings(JSON.stringify(data));
            })
        };

        if (DEBUG) {
            $.getJSON('../mock/getSettings.json', function (data) {
                PAGE_INIT._render(pageId, data);
                initJsForPage(data);
            });
        } else {
            var data = JSON.parse(ConfigCtrl.getSettings());
            PAGE_INIT._render(pageId, data);
            initJsForPage(data);
        }

    }
};

var PANEL_INTI = {
    'panel-setting': function (e, panelId) {

    }
};

/**
 * pageinit调用
 */
$(document).on("pageInit", function (e, pageId, $page) {
    Status.currentPageId = pageId;
    var initFunc = PAGE_INIT[pageId];
    if (initFunc == null || typeof initFunc != 'function') {
        console.warn('init function not found, pageId: ' + pageId);
        return;
    }
    initFunc(e, pageId, $page);
    console.info(JSON.stringify(Status));
});

$(document).on('open', function (e) {
    panelId = e.target.id;
    var initFunc = PANEL_INIT[panelId];
    if (initFunc == null || typeof initFunc != 'function') {
        console.warn('init function not found, panelId: ' + panelId);
        return;
    }
    initFunc(e, panelId);
});


/**
 * 页面初始化
 */
$.init();