/**
 * Created by Heaven on 16/1/26.
 */
var WEEK_STR = {
    0: '周日',
    1: '周一',
    2: '周二',
    3: '周三',
    4: '周四',
    5: '周五',
    6: '周六'
};

// 为string对象添加替换函数
String.prototype.replaceInFormat = function (repl) {
    return this.replace(/\$([0-9])/g, function (match, capture) {
        // TODO 这里和函数会调用多次,我也不知道为什么
        //console.info(match, capture);
        return repl[capture];
    });
};

Handlebars.registerHelper('textType', function () {
    var out = '';
    if (this.type == 'FOR_WEEK') {
        var weeks = this.week.split(',');
        for (var i in weeks) {
            out += WEEK_STR[weeks[i]] + ' ';
        }
    } else if (this.type == 'FOR_DAY') {
        out = '每天';
    } else {
        out = '不重复';
    }
    return out;
});

Handlebars.registerHelper('textPercentage', function (pForNew) {
    return (parseFloat(pForNew) * 100) + ' ' + '%';
});

Handlebars.registerHelper('textClassLike', function (like) {
    if (like == 1)
        return 'button-danger';
    else
        return 'button-light';
});

Handlebars.registerHelper('textDate', function (unixTimestamp) {
    var time = new Date(unixTimestamp * 1000);
    return (time.getUTCMonth() + 1) + '月' + time.getUTCDate() + '日';
});

Handlebars.registerHelper('LikeStar', function (like) {
    var out = '';
    if (like == 1) {
        out += '<span class="icon icon-star"></span>';
    }
    return new Handlebars.SafeString(out);
});

Handlebars.registerHelper('ClockSwitch', function () {
    var repl = {
        0: this.active ? 'checked="checked"' : ''
    };

    var out = '';
    out += '<label class="label-switch">';
    out += '    <input type="checkbox" class="clock-switch" $0>'.replaceInFormat(repl);
    out += '    <div class="checkbox"></div>';
    out += '</label>';
    return new Handlebars.SafeString(out);
});

Handlebars.registerHelper('InputWeekLabel', function (text, value) {
    var repl = {
        0: text,
        1: value,
        2: this.week.split(',').indexOf(value) != -1 ? 'checked="checked"' : ''
    };

    var out = '';
    out += '<label class="label-checkbox item-content">';
    out += '    <input type="checkbox" name="week" value="$1" $2>'.replaceInFormat(repl);
    out += '    <div class="item-media"><i class="icon icon-form-checkbox"></i></div>';
    out += '    <div class="item-inner">';
    out += '        <div class="item-title-row">';
    out += '            <div class="item-title">$0</div>'.replaceInFormat(repl);
    out += '        </div>';
    out += '    </div>';
    out += '</label>';
    return new Handlebars.SafeString(out);
});

Handlebars.registerHelper('InputTypeOption', function (text, value) {
    var repl = {
        0: value,
        1: value == this.type ? 'selected="selected"' : '',
        2: text
    };
    var out = '';
    out += '<option value="$0" $1>$2</option>'.replaceInFormat(repl);
    return new Handlebars.SafeString(out);
});

$.fn.timePicker = function (data) {
    var array24 = [];
    for (var i = 0; i < 24; i++) {
        array24.push(i < 10 ? '0' + i : '' + i)
    }
    var array60 = [];
    for (var i = 0; i < 60; i++) {
        array60.push(i < 10 ? '0' + i : '' + i)
    }

    var now = new Date();

    return $(this).picker({
        toolbarTemplate: '  <header class="bar bar-nav">\
                                <button class="button button-link pull-right close-picker">确定</button>\
                                <h1 class="title">请选择时间</h1>\
                            </header>',
        cols: [
            {
                textAlign: 'center',
                values: array24
            },
            {
                textAlign: 'center',
                values: [':']
            },
            {
                textAlign: 'center',
                values: array60
            }
        ],
        value: data.value == null ? [array24[now.getHours()], ':', array60[now.getMinutes()]] : data.value,
        inputReadOnly: false
    });
};

$.fn.numberPicker = function (data) {
    var array = [];
    var min = parseFloat(data.min);
    var max = parseFloat(data.max);
    var step = parseFloat(data.step);
    for (var i = 0; min + i * step <= max; i++) {
        array.push(min + i * step);
    }

    if (data.unit == null) {
        return $(this).picker({
            cols: [
                {
                    textAlign: 'center',
                    values: array
                }
            ],
            inputReadOnly: false
        });
    } else {
        return $(this).picker({
            cols: [
                {
                    textAlign: 'center',
                    values: array
                },
                {
                    textAlign: 'center',
                    values: [data.unit]
                }
            ],
            inputReadOnly: false
        });
    }

}