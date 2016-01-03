var DATA_CONFIGS = [
    {
        src: '#clock-list-template',
        dst: '#div-clock-list',
        data: ClockCtrl.getClockEntries(),
        ajax: {
            url: 'mock/getClockEntries.json',
        }
    }
];

var LISTENER_CONFIGS = {
    // 控制显示和隐藏星期选择的区域
    'input[name=repeat]': {
        change: function (e) {
            var curRepeat = $(e.target).val();
            if (curRepeat == 'FOR_WEEK') {
                $('#input-week-group').show(100);
            } else {
                $('#input-week-group').hide(100);
            }
        }
    },
    // 创建新闹钟的提交页面
    '#ctrl-create-confirm': function (e) {
        var data = {
            cid: 0,
            time: $('input[name=time]').val(),
            type: $('input[name=repeat]:checked').val(),
            week: "",
            desc: $('input[name=desc]').val(),
            active: true
        };

        if (data.repeat == 'FOR_WEEK') {
            var week = [];
            $('input[name=week]:checked').each(function () {
                week.push($(this).val());
            });
            data.week = week.join(',');
        }
        ClockCtrl.setClockEntry(JSON.stringify(data));
        window.location = 'clocks.html';
    },
    // 打开新建闹钟页面时 更新time字段为当前时间
    '#create-clock': function (e) {
        var d = new Date(),
            h = (d.getHours() < 10 ? '0' : '') + d.getHours(),
            m = (d.getMinutes() < 10 ? '0' : '') + d.getMinutes();
        $('input[name=time]').val(h + ':' + m);
    },
    // 改变闹钟右边开关时，修改闹钟的激活状态
    '.ctrl-clock-toggle': function (e) {
        var cid = $(e.target).parents('li.table-view-cell').attr('data-cid');
        var active = $('#toggle-' + cid).hasClass('active');
        ClockCtrl.activeClock(JSON.stringify({
            cid: cid,
            active: active
        }));
    }
};


$(document).ready(function () {
    var publisher = Publisher(
        DATA_CONFIGS,
        LISTENER_CONFIGS
    );
    publisher.init();

    $('#input-week-group').hide();

});

Handlebars.registerHelper('clockRepeat', function (strFor) {
    switch (strFor) {
        case 'FOR_ONCE':
            return '一次';
        case 'FOR_DAY':
            return '每天'
    }

});

Handlebars.registerHelper('clockActive', function (bolActive) {
    if (bolActive) {
        return 'active';
    } else {
        return '';
    }
});