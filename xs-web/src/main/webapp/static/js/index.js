window.onload = function () {
    $('.to_about').click(function () {
        location.href = '/about.html';
        var e = window.event || e;
        if (document.all) {  //只有ie识别
            e.cancelBubble = true;
        } else {
            e.stopPropagation();
        }
    });

    //监听pc导航条跟随屏幕滚动样式的变化
    function wactchScroll() {
        var defaultTop = document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop;
        var height;
        if (location.href.indexOf("index") > -1 || location.href == 'http://dev.xiangshuispace.com/' || location.href == 'https://www.xiangshuispace.com/') {
            height = $('.banner_img').height();
        } else if (location.href.indexOf("about") > -1) {
            height = $('.about_banner_img').height() - 70;
        }

        if (defaultTop >= height - 78) {
            $(".logo").attr('src', '/static/image/logochange_black.png');
            $(".head").addClass("head_white");
            $(".head_nav").addClass("head_nav_black");
            $(".head_nav_a").addClass("head_nav_a_black");
        } else {
            $(".logo").attr('src', '/static/image/logochange.png');
            $(".head").removeClass("head_white");
            $(".head_nav").removeClass("head_nav_black");
            $(".head_nav_a").removeClass("head_nav_a_black");
        }
    }

    wactchScroll();
    $(window).scroll(function () {
        wactchScroll();
    });

    //监听phone导航条跟随屏幕滚动样式的变化
    function wactchPhoneScroll() {
        var defaultTop = $(document).scrollTop();
        $(".headMenu").removeClass('headMenuClose');
        if (defaultTop >= 10) {
            $(".phoneLogo").attr('src', '/static/image/phone/phone_logo_black.png');
            $(".phoneMenu").attr('src', '/static/image/phone/menulogo_black.png');
            $(".phoneHeader").addClass('phoneHeader_white');
        } else {
            $(".phoneLogo").attr('src', '/static/image/phone/phone_logo.png');
            $(".phoneMenu").attr('src', '/static/image/phone/menulogo.png');
            $(".phoneHeader").removeClass('phoneHeader_white');
        }
    }

    wactchPhoneScroll();
    $(document).scroll(function () {
        $('.menuList').removeClass('active01');
        wactchPhoneScroll();
    });
    //底部公众号的悬停
    $('.dlwx_dlow').get(0).onmouseenter = function (e) {
        downloadObj.downmashow();
        this.style.backgroundImage = 'url(/static/image/linkwxLight.png)';
        this.style.backgroundSize = '43px 43px';
        var e = window.event || e;
        if (document.all) {  //只有ie识别
            e.cancelBubble = true;
        } else {
            e.stopPropagation();
        }
    };
    $('.dlwx_dlow').get(0).onmouseleave = function (e) {
        downloadObj.downmahide();
        this.style.backgroundImage = 'url(/static/image/linkwx2.png)';
        this.style.backgroundSize = '43px 43px';
        var e = window.event || e;
        if (document.all) {  //只有ie识别
            e.cancelBubble = true;
        } else {
            e.stopPropagation();
        }
    };

    //底部小程序的悬停
    $('.dlwx_dlow').get(1).onmouseenter = function () {
        downloadObj.miniAppShow();
        this.style.backgroundImage = 'url(/static/image/linkminiLight.png)';
        this.style.backgroundSize = '43px 43px';
    };
    $('.dlwx_dlow').get(1).onmouseleave = function (e) {
        downloadObj.miniAppHide();
        this.style.backgroundImage = 'url(/static/image/linkmini2.png)';
        this.style.backgroundSize = '43px 43px';
        var e = window.event || e;
        if (document.all) {  //只有ie识别
            e.cancelBubble = true;
        } else {
            e.stopPropagation();
        }
    };

    //phone菜单下拉
    yshuai.event.addEventListener($('.headMenu').get(0), 'touchend', function (e) {
        var e = window.event || e;
        e.preventDefault();
        $('.menuList').toggleClass('active01');
        if ($('.menuList').hasClass('active01')) {
            $(".phoneLogo").attr('src', '/static/image/phone/phone_logo_black.png');
            $(".phoneMenu").attr('src', '/static/image/phone/menu_close.png');
            $(".headMenu").addClass('headMenuClose');
            $(".phoneHeader").addClass('phoneHeader_white');
            yshuai.event.addEventListener($('body').get(0), 'touchend', function (e) {
                $('.menuList').removeClass('active01');
                wactchPhoneScroll();
            });
        } else {
            wactchPhoneScroll();
            $(".headMenu").removeClass('headMenuClose');
        }
        var e = window.event || e;
        if (document.all) {  //只有ie识别
            e.cancelBubble = true;
        } else {
            e.stopPropagation();
        }
    });

    //还原phone微信及小程序点击弹出对应二维码
    function restore() {
        $('.phone_link_miniApp_code').removeClass('phone_link_active');
        $('.phone_linkmini2').attr('src', '/static/image/linkmini2.png');
        $('.phone_link_wx_code').removeClass('phone_link_active');
        $('.phone_linkwx2').attr('src', '/static/image/linkwx2.png');
    }

    //phone微信及小程序点击弹出对应二维码
    yshuai.event.addEventListener($('.phone_link_wx').get(0), 'touchend', function (e) {
        e.stopPropagation();
        $('.phone_link_wx_code').toggleClass('phone_link_active');
        if ($('.phone_link_wx_code').hasClass('phone_link_active')) {
            $('.phone_linkwx2').attr('src', '/static/image/linkwxLight.png');
            yshuai.event.addEventListener($('.phone_footer').get(0), 'touchend', function (e) {
                restore()
            });
        } else {
            $('.phone_linkwx2').attr('src', '/static/image/linkwx2.png');
        }

        $('.phone_link_miniApp_code').removeClass('phone_link_active');
        $('.phone_linkmini2').attr('src', '/static/image/linkmini2.png');
    });
    yshuai.event.addEventListener($('.phone_link_miniApp').get(0), 'touchend', function (e) {
        e.stopPropagation();
        $('.phone_link_miniApp_code').toggleClass('phone_link_active');
        if ($('.phone_link_miniApp_code').hasClass('phone_link_active')) {
            $('.phone_linkmini2').attr('src', '/static/image/linkminiLight.png');
            yshuai.event.addEventListener($('.phone_footer').get(0), 'touchend', function (e) {
                restore()
            });
        } else {
            $('.phone_linkmini2').attr('src', '/static/image/linkmini2.png');
        }

        $('.phone_link_wx_code').removeClass('phone_link_active');
        $('.phone_linkwx2').attr('src', '/static/image/linkwx2.png');
    });
    $(document).scroll(function () {
        restore()
    });

    //ie8不支持:last-child
    if (window.isIE8) {
        $('.link_list ul:nth-child(2)').css({border: 'none'});
    }

    //首页
    if (location.href.indexOf("index") > -1 || location.href == 'http://dev.xiangshuispace.com/' || location.href == 'https://www.xiangshuispace.com/') {
        //swiper初始化；
        var swiper1 = new Swiper('.swiper-container01', {
            pagination: '.swiper-pagination01',
            paginationClickable: true,
            paginationType: 'custom',
            autoplay: 3000,
            loop: true,
            paginationCustomRender: function (swiper1, current, total) {
                var _html = "";
                if (current == 1) {
                    _html = '<div class="swiper-pagination-temp">' +
                        '<div class="swiper-pagination-temp-head">' +
                        '<div class="swiper-pagination-temp-head-wrap display_flex align-items_center justify-content_flex-between">' +
                        '<img src="/static/image/phone/phone_line.png" alt="背景线" class="phone_line">' +
                        '<div class="swiper-pagination-temp-imgs">' +
                        '<div class="swiper-pagination-temp-imgs-wrap display_flex align-items_center justify-content_flex-between">' +
                        '<img src="/static/image/cliockicon2.png"  data-id="1" alt=""/>' +
                        '<img src="/static/image/shandian1.png"  data-id="2" alt=""/>' +
                        '<img src="/static/image/saomaicon1.png"  data-id="3" alt=""/>' +
                        '<img src="/static/image/bikeicon1.png"  data-id="4" alt=""/>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '<div class="swiper-pagination-temp-text">' +
                        '<h2>注册／登录</h2>' +
                        '<p>打开享+APP／小程序，首次使用需注</br>册认证，自动手机定位，可查看离你最</br>近的共享头等舱位置</p>' +
                        '</div>' +
                        '</div>';
                } else if (current == 2) {
                    _html = '<div class="swiper-pagination-temp">' +
                        '<div class="swiper-pagination-temp-head">' +
                        '<div class="swiper-pagination-temp-head-wrap display_flex align-items_center justify-content_flex-between">' +
                        '<img src="/static/image/phone/phone_line.png" alt="背景线" class="phone_line">' +
                        '<div class="swiper-pagination-temp-imgs">' +
                        '<div class="swiper-pagination-temp-imgs-wrap display_flex align-items_center justify-content_flex-between">' +
                        '<img src="/static/image/cliockicon1.png"  data-id="1" alt=""/>' +
                        '<img src="/static/image/shandian2.png"  data-id="2" alt=""/>' +
                        '<img src="/static/image/saomaicon1.png"  data-id="3" alt=""/>' +
                        '<img src="/static/image/bikeicon1.png"  data-id="4" alt=""/>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '<div class="swiper-pagination-temp-text">' +
                        '<h2>扫码使用</h2>' +
                        '<p>扫描头等舱外的二维码，<br>确认后自动开启使用</p>' +
                        '</div>' +
                        '</div>';
                } else if (current == 3) {
                    _html = '<div class="swiper-pagination-temp">' +
                        '<div class="swiper-pagination-temp-head">' +
                        '<div class="swiper-pagination-temp-head-wrap display_flex align-items_center justify-content_flex-between">' +
                        '<img src="/static/image/phone/phone_line.png" alt="背景线" class="phone_line">' +
                        '<div class="swiper-pagination-temp-imgs">' +
                        '<div class="swiper-pagination-temp-imgs-wrap display_flex align-items_center justify-content_flex-between">' +
                        '<img src="/static/image/cliockicon1.png"  data-id="1" alt=""/>' +
                        '<img src="/static/image/shandian1.png"  data-id="2" alt=""/>' +
                        '<img src="/static/image/saomaicon2.png"  data-id="3" alt=""/>' +
                        '<img src="/static/image/bikeicon1.png"  data-id="4" alt=""/>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '<div class="swiper-pagination-temp-text">' +
                        '<h2>开始体验</h2>' +
                        '<p>自由调整头等舱座椅角度，休息，<br>办公，任意切换</p>' +
                        '</div>' +
                        '</div>';
                } else if (current == 4) {
                    _html = '<div class="swiper-pagination-temp">' +
                        '<div class="swiper-pagination-temp-head">' +
                        '<div class="swiper-pagination-temp-head-wrap display_flex align-items_center justify-content_flex-between">' +
                        '<img src="/static/image/phone/phone_line.png" alt="背景线" class="phone_line">' +
                        '<div class="swiper-pagination-temp-imgs">' +
                        '<div class="swiper-pagination-temp-imgs-wrap display_flex align-items_center justify-content_flex-between">' +
                        '<img src="/static/image/cliockicon1.png"  data-id="1" alt=""/>' +
                        '<img src="/static/image/shandian1.png"  data-id="2" alt=""/>' +
                        '<img src="/static/image/saomaicon1.png"  data-id="3" alt=""/>' +
                        '<img src="/static/image/bikeicon2.png"  data-id="4" alt=""/>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '<div class="swiper-pagination-temp-text">' +
                        '<h2>结束体验</h2>' +
                        '<p>体验完毕后，APP／小程序<br>结束订单</p>' +
                        '</div>' +
                        '</div>';
                }
                return _html;//返回所有的页码html;
            },
            onSlideChangeEnd: function (swiper) {
                swiper.startAutoplay();
            }
        });

        //给每个页码绑定跳转的事件   
        $('.swiper-pagination01').on('click', 'img', function () {
            swiper1.slideTo(this.dataset.id, 3000, true);
        })

        function pcSwiper3Slide() {
            var swiper_pc = new Swiper('.swiper-container-pc', {
                paginationClickable: false,
                nextButton: '.swiper-pc-next',
                prevButton: '.swiper-pc-prev',
                slidesPerView: 3,
                freeMode: true
            });
        }

        function pcSwiper2Slide() {
            var mySwiper = new Swiper('.swiper-container-pc', {
                paginationClickable: false,
                slidesPerView: 3,
                freeMode: true
            });
            $('.swiper-pc-prev').on('click', function (e) {
                e.preventDefault();
                mySwiper.swipePrev()
            });
            $('.swiper-pc-next').on('click', function (e) {
                e.preventDefault();
                mySwiper.swipeNext();
            });
        }

        if (isIe()) {
            pcSwiper2Slide();
        } else {
            pcSwiper3Slide();
        }
        var swiper_pc = new Swiper('.swiper-container-pc', {
            paginationClickable: false,
            nextButton: '.swiper-pc-next',
            prevButton: '.swiper-pc-prev',
            slidesPerView: 3,
            freeMode: true
        });
        var swiper2 = new Swiper('.swiper-container02', {
            pagination: '.swiper-pagination',
            paginationClickable: true,
            autoplay: 3000,
            loop: true,
            onSlideChangeEnd: function (swiper) {
                swiper.startAutoplay();
            }
        });

        //播放视频
        function playVideo() {
            var defaultTop = document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop;
            $('.product_video_wrap').css({display: 'block', top: defaultTop + 'px'});
            if (window.isIE8) {
                flowplayer(0).play()
            } else {
                var player = videojs('my-player');
                player.ready(function () {
                    player.play();
                });
            }
            document.documentElement.style.overflow = 'hidden';//禁止屏幕滚动
        }

        //关闭视频
        $('.close_video').click(function () {
            if (window.isIE8) {
                flowplayer(0).pause()
            } else {
                var player = videojs('my-player');
                player.ready(function () {
                    player.pause();
                });
            }
            $('.product_video_wrap').css({display: 'none'});
            document.documentElement.style.overflow = 'visible';//允许屏幕滚动
        });

        //pc观看视频介绍
        $('.show_movie').click(function () {
            playVideo();
        });

        //phone观看视频介绍
        $('.phone_watch_vedio').click(function () {
            playVideo();
        });

        //手机菜单按钮
        var headHeight = $('.headContain').height() / 2 - 1;
        var playHeight = $('.phone_direction').get(0).offsetTop - headHeight;
        var fanHeight = $('.phone_experience').get(0).offsetTop - headHeight;
        var downloadHeight = $('.phone_app_store').get(0).offsetTop - headHeight;
        if (location.search && location.search.indexOf('phoneNav') > -1 && location.search.split('=')[1]) {
            var navIndex = location.search.split('=')[1] - 0;
            switch (navIndex) {
                case 2:
                    $('html body').animate({scrollTop: playHeight}, 1000);
                    break;
                case 3:
                    $('html body').animate({scrollTop: fanHeight}, 1000);
                    break;
                case 4:
                    $('html body').animate({scrollTop: downloadHeight}, 1000);
                    break;
            }
        }
        yshuai.event.addEventListener($('.menuList').get(0), 'touchend', function (e) {
            switch (e.target.innerHTML) {
                case '首页':
                    $('html body').animate({scrollTop: 0}, 1000);
                    $('.menuList').removeClass('active01');
                    break;
                case '使用说明':
                    $('html body').animate({scrollTop: playHeight}, 1000);
                    $('.menuList').removeClass('active01');
                    break;
                case '立即体验':
                    $('html body').animate({scrollTop: fanHeight}, 1000);
                    $('.menuList').removeClass('active01');
                    break;
                case '下载APP':
                    $('html body').animate({scrollTop: downloadHeight}, 1000);
                    $('.menuList').removeClass('active01');
                    break;
                case '关于我们':
                    location.href = '/about.html';
                    break;

            }
        });
        $(".swiper-container-pc a").hover(function () {
            $(this).addClass('active')
        }, function () {
            $(this).removeClass('active')
        });
    }
    //关于我们
    if (location.href.indexOf("about") > -1) {
        yshuai.event.addEventListener($('.about_menuList').get(0), 'touchend', function (e) {
            var e = window.event || e;
            e.preventDefault();
            var navText = e.target.innerText;
            switch (navText) {
                case '首页':
                    location.href = "index.html";
                    break;
                case '使用说明':
                    location.href = "index.html?phoneNav=2";
                    break;
                case '立即体验':
                    location.href = "index.html?phoneNav=3";
                    break;
                case '下载APP':
                    location.href = "index.html?phoneNav=4";
                    break;
                case '关于我们':
                    location.href = "/about.html";
                    break;
            }

            if (document.all) {  //只有ie识别
                e.cancelBubble = true;
            } else {
                e.stopPropagation();
            }
        });
        $('.about_content').css('height', $('.about_content_wrap').height() - $('.about_content_nav').height() + 'px');
        $('.phone_about_content').css('height', $('.phone_about_content_wrap').height() - $('.phone_about_content_nav').height() + 'px');

        function newsSwiper3Slide() {
            var swiper_news = new Swiper('.swiper-container-news', {
                paginationClickable: false,
                prevButton: '.swiper-news-prev',
                nextButton: '.swiper-news-next',
                slidesPerView: 3,
                freeMode: true,
                spaceBetween: 20
            });
        }

        function newsSwiper2Slide() {
            var swiper_news = new Swiper('.swiper-container-news', {
                paginationClickable: false,
                slidesPerView: 3,
                freeMode: true,
                spaceBetween: 20
            });

            $('.swiper-news-prev').on('click', function (e) {
                e.preventDefault();
                swiper_news.swipePrev()
            });
            $('.swiper-news-next').on('click', function (e) {
                e.preventDefault();
                swiper_news.swipeNext();
            });
        }

        if (isIe()) {
            newsSwiper2Slide();
        } else {
            newsSwiper3Slide();
        }


        var swiper_about = new Swiper('.swiper-container-phone-news', {
            paginationClickable: false,
            prevButton: '.swiper-phone-news-prev',
            nextButton: '.swiper-phone-news-next'
        });
        $('.about_xiangshui_nav').click(function () {
            $('.contact_us').removeClass('about_nav_active');
            $('.about_xiangshui').addClass('about_nav_active');
            $(this).addClass('active').siblings('div').removeClass('active');
            $('.about_content').css('height', $('.about_content_wrap').height() - $('.about_content_nav').height() + 'px');
        });
        $('.contact_us_nav').click(function () {
            $('.about_xiangshui').removeClass('about_nav_active');
            $('.contact_us').addClass('about_nav_active');
            $(this).addClass('active').siblings('div').removeClass('active');
            $('.about_content').css('height', $('.about_content_wrap').height() - $('.about_content_nav').height() + 'px');
        });
        if (location.search && location.search.split('=')[1]) {
            var paramsNum = location.search.split('=')[1];
            if (paramsNum == 1) {
                $('html ,body').animate({scrollTop: $('.about_contact_wrap')[0].offsetTop + 78}, 1000);
            } else if (paramsNum == 2) {
                $('html ,body').animate({scrollTop: $('.about_xiangshui_news')[0].offsetTop + 78}, 1000);
            } else if (paramsNum == 3) {
                $('html ,body').animate({scrollTop: $('.about_contact_wrap')[0].offsetTop + 78}, 1000);
                $('.about_xiangshui').removeClass('about_nav_active');
                $('.contact_us').addClass('about_nav_active');
                $('.contact_us_nav').addClass('active').siblings('div').removeClass('active');
                $('.about_content').css('height', $('.about_content_wrap').height() + 'px');
            } else if (paramsNum == 4) {
                $('html ,body').animate({scrollTop: $('.phone_about_xiangshui_text_news')[0].offsetTop}, 1000);
            } else if (paramsNum == 5) {
                $('.phone_about_xiangshui').removeClass('phone_about_nav_active');
                $('.phone_contact_us').addClass('phone_about_nav_active');
                $('.phone_contact_us_nav').addClass('active').siblings('div').removeClass('active');
                $('.phone_about_content').css('height', $('.phone_about_content_wrap').height() - $('.phone_about_content_nav').height() + 'px');
            }
        }

        $('.news_title').each(function () {
            var maxwidth = 11;
            if ($(this).text().length > maxwidth) {
                $(this).text($(this).text().substring(0, maxwidth));
                $(this).html($(this).text() + '...');
            }
        });

        $('.phone_news_title').each(function () {
            var maxwidth = 16;
            if ($(this).text().length > maxwidth) {
                $(this).text($(this).text().substring(0, maxwidth));
                $(this).html($(this).text() + '...');
            }
        });

        yshuai.event.addEventListener($('.phone_about_xiangshui_nav').get(0), 'touchend', function (e) {
            var e = window.event || e;
            e.preventDefault();
            $('.phone_contact_us').removeClass('phone_about_nav_active');
            $('.phone_about_xiangshui').addClass('phone_about_nav_active');
            $(this).addClass('active').siblings('div').removeClass('active');
            $('.phone_about_content').css('height', $('.phone_about_content_wrap').height() - $('.phone_about_content_nav').height() + 'px');
            if (document.all) {  //只有ie识别
                e.cancelBubble = true;
            } else {
                e.stopPropagation();
            }
        });
        yshuai.event.addEventListener($('.phone_contact_us_nav').get(0), 'touchend', function (e) {
            var e = window.event || e;
            e.preventDefault();
            $('.phone_about_xiangshui').removeClass('phone_about_nav_active');
            $('.phone_contact_us').addClass('phone_about_nav_active');
            $(this).addClass('active').siblings('div').removeClass('active');
            $('.phone_about_content').css('height', $('.phone_about_content_wrap').height() - $('.phone_about_content_nav').height() + 'px');
            if (document.all) {  //只有ie识别
                e.cancelBubble = true;
            } else {
                e.stopPropagation();
            }
        });
    }
};