var DATA_CONFIGS = [
    {
        src: '#main-pad-template',
        dst: '#main-pad',
        data: SongCtrl.getCurrentSongAndClock()
    }
];

var LISTENER_CONFIGS = {
    '#ctrl-next': function (e) {
        SongCtrl.skipCurrentSong();
    },
    '#ctrl-like': function (e) {
        var $like = $('#like-status');
        var bolLike = $like.hasClass('icon-star-filled');
        if (bolLike) {
            SongCtrl.unmarkCurrentSong();
            $like.removeClass('icon-star-filled');
            $like.addClass('icon-star');
        } else {
            SongCtrl.markCurrentSong();
            $like.addClass('icon-star-filled');
            $like.removeClass('icon-star');
        }
    },
    '#ctrl-bye': function (e) {
        SongCtrl.unlikeCurrentSong()
    },
    '#ctrl-close': function (e) {
        Activity.stop()
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

Handlebars.registerHelper('mkstart', function (bolLike) {
    if (bolLike) {
        return 'icon-star-filled';
    } else {
        return 'icon-star';
    }
});
