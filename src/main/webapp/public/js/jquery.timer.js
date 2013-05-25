(function($){
    var glob = {
        start    : 0,
        curr     : 0,
        last     : 0,
        fix      : 0,
        timer    : false,
        running  : false
    }
    var methods = {
        init : function( options ) {
            var defaults = { autostart: false, start: 0 };
            var options = $.extend(defaults, options);

            glob.start = options.start;

            if(options.autostart) {
                timer();
            }
            return this.each(function() {
                $(this).text("00:00:00").show();
                formatTime(this);
            });
        },
        start : function( ) {
            timer();
            return this.each(function() {
                setTimeout(function(obj) {
                    formatTime(obj);
                }, 200, this);
            });
        },
        stop : function( ) {
            glob.last = (new Date()).getTime();
            glob.running = false;
        }
    };

    function timer() {
        if(glob.running)
            return;
        var time = 200;
        if(!glob.timer) {
            glob.start = (new Date()).getTime();
        } else {
            glob.fix += (new Date()).getTime() - glob.last;
            time = 100;
        }
        glob.running = true;
        glob.timer = true;
        setTimeout(timeout, time);
    }
    function timeout() {
        calculateTime();
        if(glob.running)
            setTimeout(timeout, 200);
    }
    function calculateTime() {
        if(glob.running) {
            glob.curr = (new Date()).getTime() - glob.start;
        }
    }
    function formatTime(obj) {
        if(!(glob.timer && glob.running))
            return;
        var total = parseInt((glob.curr - glob.fix) / 1000);
        var hours = extractHours(total);
        var minutes = extractMinutes(total, hours);
        var seconds = extractSecundes(total, hours, minutes);
        var time = strpad(hours) + ":" + strpad(minutes) + ":" + strpad(seconds);
        $(obj).text(time);
        setTimeout(formatTime, 200, obj);
    }
    function extractHours(secs) {
        return parseInt(secs / 3600);
    }
    function extractMinutes(secs, hours) {
        return parseInt((secs - hours * 3600) / 60);
    }
    function extractSecundes(secs, hours, mins) {
        return parseInt(secs - hours * 3600 - mins * 60);
    }
    function strpad(num) {
        var str = "" + num;
        var pad = "00";
        return pad.substring(0, pad.length - str.length) + str;
    }

    $.fn.timer = function(method) {
        if ( methods[method] && typeof methods[method] === 'function' ) {
            return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
        } else if ( typeof method === 'object' || ! method ) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' +  method + ' does not exist on jQuery.tooltip' );
        }
    };
})(jQuery);