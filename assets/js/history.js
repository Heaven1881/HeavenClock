var DATA_CONFIGS = [
    {
        src: '#song-list-template',
        dst: '#song-list',
        data: ClockCtrl.getHistory()
    //    ajax: {
    //        url: 'mock/getSongHistory.json'
    //    }
    }
];

var LISTENER_CONFIGS = {
    'a.ctrl-song-list-item': function (e) {
        $('#ctrl-stop-play').show(100);
        Activity.simplePlay(JSON.stringify({
            surl: $(e.target).attr('data-surl')
        }));
    },
    '#ctrl-stop-play-btn': function (e) {
        $('#ctrl-stop-play').hide(100);
        Activity.simpleStop();
    }

};

$(document).ready(function () {
    var publisher = Publisher(
        DATA_CONFIGS,
        LISTENER_CONFIGS
    );
    publisher.init();

    if (Activity.isPlaying()) {
        $('#ctrl-stop-play').show();
    } else {
        $('#ctrl-stop-play').hide();
    }
});