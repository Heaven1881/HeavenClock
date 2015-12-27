var DATA_CONFIGS = [
    {
        src: '#main-pad-template',
        dst: '#main-pad',
        data: PlayCtrl.getCurrentSongAndClock(),
        ajax: {
            url: "mock/getCurrentSongAndClock.json" // for debug
        }
    }
];

var LISTENER_CONFIGS = {
    '#ctrl-next': function (e) {
        PlayCtrl.skipCurrentSong();
    },
    '#ctrl-like': function (e) {
        var $like = $('#like-status');
        var bolLike = $like.hasClass('icon-star-filled');
        if (bolLike) {
            PlayCtrl.unrateCurrentSong();
            $like.removeClass('icon-star-filled');
            $like.addClass('icon-star');
        } else {
            PlayCtrl.rateCurrentSong();
            $like.addClass('icon-star-filled');
            $like.removeClass('icon-star');
        }
    },
    '#ctrl-bye': function (e) {
        PlayCtrl.byeCurrentSong()
    },
    '#ctrl-close': function (e) {
        Activity.closeActivity()
    }
};

$(document).ready(function () {
    var publisher = Publisher(
        DATA_CONFIGS,
        LISTENER_CONFIGS
    );
    publisher.init();

});

function doUpdate() {
    location = location;
}

Handlebars.registerHelper('mkstart', function (intLike) {
    if (intLike == 1) {
        return 'icon-star-filled';
    } else {
        return 'icon-star';
    }
});
