var headerObj = {
    dom: $('.head'),
    init: function () {
        this.render();
        this.bindEvent();
        this.controller();
    },
    childs: function () {
        return this.dom.find('li');
    },
    height: function () {
        return this.dom.height();
    },
    render: function () {

    },
    bindEvent: function () {
        var _this = this;
        yshuai.event.addEventListener(window, 'scroll', function () {
            var scrollT = document.body.scrollTop || document.documentElement.scrollTop;
            var scrollTrue = scrollT + _this.height();
            if (scrollTrue >= playObj.ownHeight() && scrollTrue < haveFunObj.ownHeight()) {
                _this.changeStyle(_this.childs()[1]);
                haveFunObj.leave();
            } else if (scrollTrue >= playObj.ownHeight() && scrollTrue < downloadObj.ownHeight()) {
                _this.changeStyle(_this.childs()[2]);
                haveFunObj.render();
            } else if (scrollTrue >= downloadObj.ownHeight()) {
                _this.changeStyle(_this.childs()[3]);
                haveFunObj.leave();
            } else {
                _this.changeStyle(_this.childs()[0]);
            }
        })
    },
    controller: function () {
        var headerHeight = $('.head')[0].offsetHeight-0;
        if (location.search && location.search.indexOf('nav') > -1 && location.search.split('=')[1]) {
            var nav_index = location.search.split('=')[1];
            if (nav_index == 2) {
                playObj.init(headerHeight);
            } else if (nav_index == 3) {
                haveFunObj.init(headerHeight);
            } else if (nav_index == 4) {
                // downloadObj.init(headerHeight);
                $('html ,body').animate({scrollTop: 2552-headerHeight}, 1000);
            }
        }
        var oLi = this.childs();
        var num = this.height();
        var _this = this;
        for (var i = 0; i < oLi.length; i++) {
            switch (i) {
                case 1:
                    oLi[i].onclick = function () {
                        playObj.init(num-headerHeight);
                    };
                    break;
                case 2:             
                    oLi[i].onclick = function () {
                        haveFunObj.init(num-headerHeight);
                    };
                    break;
                case 3:
                    oLi[i].onclick = function () {
                        downloadObj.init(num-headerHeight);
                    };
                    break;
                default:
                    oLi[i].onclick = function () {
                        $('html, body').animate({scrollTop: 0}, 1000)
                    }
            }

        }
    },
    changeStyle: function (obj) {
        $('.head span').removeClass('spantext');
        $(obj).find('span').addClass('spantext');
    }

};
headerObj.init();






