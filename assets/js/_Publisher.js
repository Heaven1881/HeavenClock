/**
 * Created by Heaven on 15/12/23.
 */

function Publisher(dataConfigs, listenerConfigs) {
    return {
        _dataConfigs: dataConfigs,
        _listenerConfigs: listenerConfigs,

        init: function () {
            this._initData(this);
            this._initListener(this);
        },

        // 批量渲染数据
        _initData: function (self) {
            for (var i in self._dataConfigs) {
                var config = self._dataConfigs[i];
                if (config.data != null) {
                    var jsonData = JSON.parse(config.data );
                    self._renderData(self, jsonData, config.src, config.dst);
                } else {
                    config.ajax.dataType = 'json';
                    config.ajax.success = function (param) {
                        self._renderData(self, param, config.src, config.dst);
                    };

                    $.ajax(config.ajax);
                    if (config.interval != null && config.interval != 0) {
                        console.info(config.interval);
                        setInterval('$.ajax(' + JSON.stringify(config.ajax) + ')', config.interval);
                    }
                }
            }
        },

        _renderData: function (self, param, src, dst) {
            if ($(src).size() < 1) {
                console.warn('Cannot find element [' + src + '], please check your dataConfigs');
                return;
            }
            if ($(dst).size() < 1) {
                console.warn('Cannot find element [' + dst + '], please check your dataConfigs');
                return;
            }
            var template = Handlebars.compile($(src).html());
            $(dst).html(template(param));
        },

        // 批量初始化事件监听
        _initListener: function (self) {
            for (var sel in self._listenerConfigs) {
                var config = self._listenerConfigs[sel];
                if (typeof config == 'function') {
                    config = {click: config};
                }
                for (var event in config) {
                    $(sel).on(event, config[event]);
                }
            }
        }
    };
}


