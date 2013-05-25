;(function ( $, window, document, undefined ) {

    var pluginName = "modalBox",
        defaults = {
            complete    : function(){},
            fixed       : true,
            overlay     : true,
            button      : true,
            transparent : true,
            radius      : '5px',
            padding     : '10px',
            width       : 'auto',
            left        : 0,
            top         : 0
        };

    var methods = {
        close       : function() {
            $('.jQueryModalBox').fadeOut(function(){
                $('#modalBoxContainer').html('');
            });
        },
        reload      : function(data) {
            $('.jQueryModalBox').fadeOut(function(){
                setTimeout(function(instance, data){
                    instance.show(data);
                }, 500, instance, data)
            });
        }
    };

    var instance;

    function Plugin( element, options ) {
        this.element = element;

        this.options = $.extend( {}, defaults, options );

        this._defaults = defaults;
        this._name = pluginName;

        this.init();
    }

    Plugin.prototype = {

        init: function() {
            if(!($('#modalBoxContainer').length > 0)) {
                $('body').append('<div id="modalBoxContainer" class="jQueryModalBox container"></div>');
                $('body').append('<div id="modalBoxOverlay" class="jQueryModalBox overlay group"></div>');
                $('body').css({
                    'position':'relative'
                });
                if(typeof this.options.padding === 'object') {
                    $('#modalBoxContainer').css('padding-top', this.options.padding.verical);
                    $('#modalBoxContainer').css('padding-bottom', this.options.padding.verical);
                    $('#modalBoxContainer').css('padding-left', this.options.padding.horizontal);
                    $('#modalBoxContainer').css('padding-right', this.options.padding.horizontal);
                } else
                    $('#modalBoxContainer').css('padding', this.options.padding);
                $('#modalBoxContainer').css('border-radius', this.options.radius);
            }
            $('#modalBoxOverlay').hide();
            $('#modalBoxContainer').hide();
            if(!this.options.transparent) {
                $('#modalBoxContainer').addClass('solid');
            } else {
                $('#modalBoxContainer').removeClass('solid');
            }

            if($(this.element).is('a')) {
                $(this.element).bind('click', { plugin: this }, function(event){
                    event.preventDefault();
                    if($('#modalBoxContainer').is(':visible')) {
                        $('.jQueryModalBox').fadeOut(function(){
                            setTimeout(function(plugin){
                                plugin.showLink();
                            }, 500, event.data.plugin)
                        });
                    } else
                        event.data.plugin.showLink();
                });
            } else {
                this.show($(this.element).html());
            }
            instance = this;
        },

        showLink: function() {
            var href = $(this.element).attr('href');
            var $this = this;
            if(href.substr(0, 1) == '#' && href.length > 1) {
                $this.show($(href).html());
            } else {
                $.ajax({
                    url : href
                }).done(function(data) {
                    $this.show(data);
                });
            }
        },

        show: function(content) {
            var $this = this;
            $('#modalBoxContainer').html(content);
            if(this.options.button)
                $('#modalBoxContainer').prepend('<span class="close"></span>');
            var left = parseInt(($(window).width() - $('#modalBoxContainer').outerWidth(true)) / 2) + this.options.left;
            var top = parseInt(($(window).height() - $('#modalBoxContainer').outerHeight(true)) / 2) + this.options.top;
            $('#modalBoxContainer').css('left', left+'px');
            $('#modalBoxContainer').css('top', top+'px');
            $('#modalBoxContainer').fadeIn();
            if(this.options.overlay)
                $('#modalBoxOverlay').fadeIn(function(){
                    $this.options.complete();
                });
            if(this.options.fixed) {
                $('#modalBoxContainer').css('position', 'fixed');
            }
            $(window).resize(function() {
                var left = parseInt(($(window).width() - $('#modalBoxContainer').outerWidth(true)) / 2);
                var top = parseInt(($(window).height() - $('#modalBoxContainer').outerHeight(true)) / 2);
                $('#modalBoxContainer').css('left', left+'px');
                $('#modalBoxContainer').css('top', top+'px');
            });
            $('#modalBoxContainer .close, #modalBoxOverlay').bind('click', function(event){
                event.preventDefault();
                methods.close();
            });
        }
    };

    $.fn[pluginName] = function ( options ) {
        if(typeof options === 'object' || ! options) {
            return this.each(function () {
                if (!$.data(this, "plugin_" + pluginName)) {
                    $.data(this, "plugin_" + pluginName, new Plugin( this, options ));
                }
            });
        } else if(methods[options]) {
            return methods[ options ].apply( this, Array.prototype.slice.call( arguments, 1 ));
        } else {
            $.error('You suck!');
        }
    };

})( jQuery, window, document );