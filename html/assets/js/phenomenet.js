$(function() {
    $("#search").autocomplete({


        minLength: 1,
        source: function(request, response) {

            $.ajax({

                url: 'http://localhost:19000/searchPhenomenet2.groovy?term=' + request.term,
                dataType: "json",

                success: function(data) {
                    console.log("succes");
                    response($.map(data, function(item) {
                        //console.log(item)
                        var label="<div style='padding-bottom: 20px'><b>" + item.name + "</b>   -" + item.type + " -ID:" + item.entity_id +  "</div>";
                        if(item.entity_id==item.name){
                            label="<div style='padding-bottom: 20px'><b>" + item.name + "</b>   -" + item.type +"</div>";
                        }
                        return {
                             label: label ,
                             value: item.name,
                             _data: item.id
                         }

                    }));
                }
            });
        },

        select: function(event, element) {

            window.location.replace("phenomenetresult2.html?id=" + element.item._data);

        }

    }).autocomplete("instance")._renderItem = function(ul, item) {
        return $("<li>")
            .append(item.label)
            .appendTo(ul);
    };
});