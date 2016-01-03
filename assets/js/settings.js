var DATA_CONFIGS = [
    {
        src: '#setting-form-template',
        dst: '#setting-form',
        data: ConfigCtrl.getSettings(),
        ajax: {
            url: 'mock/getSettings.json'
        }
    }
];

var LISTENER_CONFIGS = {
    '#ctrl-setting-form-save': function (e) {
        var data = {
            douban_email: $('input[name=douban_email]').val(),
            douban_password: $('input[name=douban_password]').val(),
            repeat: $('input[name=repeat]').val(),
            p_for_new: $('input[name=p_for_new]').val(),
            max_history: $('input[name=max_history]').val()
        };
        console.info(data);
        ConfigCtrl.setSettings(JSON.stringify(data));
    }
};

$(document).ready(function () {
    var publisher = Publisher(
        DATA_CONFIGS,
        LISTENER_CONFIGS
    );
    publisher.init();

});
