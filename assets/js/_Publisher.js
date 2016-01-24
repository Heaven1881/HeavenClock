/**
 * Created by Heaven on 15/12/23.
 */

function Publisher(dataConfigs, listenerConfigs) {
    return {
        _dataConfigs: dataConfigs,
        _listenerConfigs: listenerConfigs,

        init: function () {
            // 渲染数据以及相关的事件监听器
            this._initData(this);
            // 渲染额外的监听器
            this._initListener(this, this._listenerConfigs);
        },

        // 批量渲染数据
        _initData: function (self) {
            for (var i in self._dataConfigs) {
                var config = self._dataConfigs[i];
                if (config.data != null) {  // 若data已指定,则直接使用data
                    var jsonData = JSON.parse(config.data );
                    self._renderData(self, jsonData, config.src, config.dst, config.innerListener);
                } else {  // 否则使用ajax获取数据
                    config.ajax.dataType = 'json';
                    config.ajax.success = function (param) {
                        self._renderData(self, param, config.src, config.dst, config.innerListener);
                    };

                    if (config.interval != null && config.interval != 0) {  //如果指定了interval,则设置定时任务
                        console.info(config.interval);
                        config.ajax.async = true;
                        setInterval('$.ajax(' + JSON.stringify(config.ajax) + ')', config.interval);
                    } else {  // 否则按照普通的配置
                        $.ajax(config.ajax);
                    }
                }
            }
        },

        _renderData: function (self, param, src, dst, listenerCfgs) {
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

            // 设置相关eventlistener
            self._initListener(self, listenerCfgs);
        },

        // 批量初始化事件监听
        _initListener: function (self, listenerCfgs) {
            for (var sel in listenerCfgs) {
                var config = listenerCfgs[sel];
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


