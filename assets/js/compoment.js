Handlebars.registerHelper("WeekCheckbox", function (name, value, text, week) {
    // 检查当前value的值是否在week之中
    var isWeek = false;
    var weeks = week.split(',');
    for (var i in weeks) {
        if (value == weeks[i]) {
            isWeek = true;
            break;
        }
    }

    var checked = 'checked="checked"';
    if (!isWeek) checked = '';

    // 渲染数据
    var src = '<input type="checkbox" name="{name}" value="{value}" {checked}><span>{text}</span>';
    return new Handlebars.SafeString(
        src.replace('{name}', name)
            .replace('{value}', value)
            .replace('{text}', text)
            .replace('{checked}', checked)
    );

});

Handlebars.registerHelper("RepeatInput", function (value, type, text) {
    var checked = '';
    if (value == type) checked = 'checked="checked"';

    var src = '<input type="radio" name="repeat" value="{value}" {checked}><span>{text}</span>';
    return new Handlebars.SafeString(
        src.replace('{value}', value)
            .replace('{text}', text)
            .replace('{checked}', checked)
    );
});

Handlebars.registerHelper('clockRepeat', function (strFor) {
    switch (strFor) {
        case 'FOR_ONCE':
            return '一次';
        case 'FOR_DAY':
            return '每天';
        case 'FOR_WEEK':
            return '每周';
    }

});

Handlebars.registerHelper('clockActive', function (bolActive) {
    if (bolActive) {
        return 'active';
    } else {
        return '';
    }
});