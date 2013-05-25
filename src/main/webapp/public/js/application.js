$(document).ready(function(){
    $('#search').typeahead({
        source: function(query, process) {
            $('.typeahead').each( function(index, element){
                $(this).css("min-width", ($(this).prev('.input').outerWidth()-2) + "px");
            });
            $.ajax({
                url : '/movies/find/'+encodeURIComponent(query),
                dataType : 'json',
                type : 'GET'
            }).done(function(data){
                process(data);
            });
        },
        updater: function(item) {
            $.ajax({
                url : '/movies/pick/'+encodeURIComponent(item),
                dataType : 'json',
                type : 'GET'
            }).done(function(data){
                window.location.href = '/movies/show/'+data.id;
            });
        },
        minLength: 2,
        items: 20
    });
    $('.close').live('click', function(e){
        $(this).parent().remove();
    });
});