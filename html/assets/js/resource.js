    $(document).ready(function() {
        $.ajax({

            url: 'http://10.254.145.77/backend/dataset.groovy?term=all',
            dataType: "json",

            success: function(data) {

            var datasetHtml = '<ul class="media-list media-list-divider">'
            $.each(data, function() {
                
                datasetHtml=datasetHtml+'<li class="media align-items-center"><div class="media-body d-flex align-items-center"><div class="flex-1"><div class="media-heading">'+this._source.dataset_name+'</div><small class="text-muted">'+this._source.description+'</small></div></div></li>'
                

                });
            datasetHtml=datasetHtml+'</ul>'
             $('#dataset').append(datasetHtml);

            },
            error: function(data) {
                console.log("Dataset error");



            }


        })

    });