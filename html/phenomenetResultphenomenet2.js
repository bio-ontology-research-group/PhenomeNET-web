 var entityNO;
 var targetId;
 var entityinfohtml;
 $('#humanloading').hide();
 $('#fishloading').hide();
 $('#mouseloading').hide();


 $(document).ready(function() {
     entityNO = getUrlVars().entityNo;
     targetId = getUrlVars().id;
     showResult(targetId);


 });


 $(function() {

     $("#search").autocomplete({


         minLength: 0,
         source: function(request, response) {
             $.ajax({

                 url: 'http://phenomebrowser.net/backend/search.groovy?term=' + request.term,
                 dataType: "json",

                 success: function(data) {
                     response($.map(data, function(item) {
                         console.log(item)
                            var host="<b>" +(item[6]=="2")?(item[3]):('')+ "</b>"
                         return {
                             label: "<div style='padding-bottom: 20px'><b>" + item[0] + "</b>   -" + item[5] + " -ID:" + item[1] + "(Source:" + item[2] +")"+host+"</div>",
                             value: item[0],
                             _data: item[1],
                             _entityNo: item[6]
                         }

                     }));
                 }
             });
         },

         select: function(event, element) {

             //window.location.replace("phenomenetsearch.html?id=" + element.item._data);
             entityNO=element.item._entityNo;
             showResult(element.item._data);

         }

     }).autocomplete("instance")._renderItem = function(ul, item) {
         return $("<li>")
             .append(item.label)
             .appendTo(ul);
     };
 });


 function getUrlVars() {
     var vars = [],
         hash;
     var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
     for (var i = 0; i < hashes.length; i++) {
         hash = hashes[i].split('=');
         vars.push(hash[0]);
         vars[hash[0]] = hash[1];
     }

     return vars;
 }

 function showResult(j) {

     targetId = j;
     $('#fish').empty();
     $('#human').empty();
     $('#mouse').empty();
     gethostdata("fish");
     gethostdata("human");
     gethostdata("mouse");


 }

 function gethostdata(host) {
     //$('#entityinfo').empty;
     var container;
     if (host == "fish") {
         container = $('#fish');
     } else if (host == "human") {
         container = $('#human');
     } else if (host == "mouse") {
         container = $('#mouse');

     }

     $.ajax({

         url: 'http://phenomebrowser.net/backend/searchByID.groovy?term=' + targetId + "&no=" + entityNO + "&host=" + host,
         dataType: "json",

         success: function(data) {

             var resHtml = '<ul class="list-group list-group-divider list-group-full faq-list">'
             console.log("success");
             console.log(data);

             $.each(data, function() {
                 details = this;
                 if (entityNO == "1") {
                     if (details._source["entity1_id"] == targetId) {
                         entityinfohtml = '<div class="ibox-head"><h5 class="text-primary">' +details._source["entity1-name"][0]+ '</h5></div><div class="ibox-body " style="height: 200px; overflow-y: scroll;">\
                                            <b>Type:</b></br><small>'+details._source["entity1_type"]+'</small></br><b>Source:</b></br><small>'+details._source["entity1_source"]+'</small>\
                                            </br><b>ID:</br></b><small>'+details._source["entity1_id"]+'</small></br></div>';
                         resHtml = resHtml + '<li class="list-group-item py-3"><h5 class="m-0">' + details._source["entity2-name"] + '</h5><p class="text-light">' + parseFloat(details._source["similarity-score"]).toFixed(3)+'</p></li>'
                         console.log(details._source);
                     }
                 }
                 else if(entityNO =="2"){
                    if (details._source["entity2_id"] == targetId) {
                         entityinfohtml = '<div class="ibox-head"><h5 class="text-primary">' +details._source["entity2-name"]+ '</h5></div><div class="ibox-body " style="height: 200px; overflow-y: scroll;">\
                                            <b>Type:</b></br><small>'+details._source["entity2_type"]+'</small></br><b>Source:</b></br><small>'+details._source["entity2_source"]+'</small>\
                                            </br><b>ID:</b></br><small>'+details._source["entity2_id"]+'</small></br></div>';
                         resHtml = resHtml + '<li class="list-group-item py-3"><h5 class="m-0">' + details._source["entity1-name"][0] + '</h5><p class="text-light">' + parseFloat(details._source["similarity-score"]).toFixed(3) +'</p></li>'
                         console.log(details._source);
                     }
                 }


             });
             resHtml = resHtml + '</ul>';
             $('#entityinfo').empty();
             $('#entityinfo').append(entityinfohtml);


             container.append(resHtml);

         },
         error: function(data) {
             console.log("error");



         }
     });
 }
