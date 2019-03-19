 var targetId;
 var phenotext = '';
 var entityinfohtml = '';

 $(document).ready(function() {
     targetId = getUrlVars().id;
     showResult(targetId);
 });
 $(function() {

     $("#search").autocomplete({
         minLength: 1,
         source: function(request, response) {
             $.ajax({
                 url: 'http://localhost:19000/searchPhenomenet2.groovy?term=' + request.term,
                 dataType: "json",
                 success: function(data) {
                     response($.map(data, function(item) {
                         return {
                             label: "<div style='padding-bottom: 20px'><b>" + item.name + "</b>   -" + item.type +"</div>" ,
                             value: item.name,
                             _data: item.id
                         }

                     }));
                 }
             });
         },
         select: function(event, element) {
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
     getEntityData();
 }

 function getEntityData() {
     $('#entityinfo').empty();
     $.ajax({
         url: 'http://localhost:19000/searchByIDphenomenet2.groovy?term=' + targetId,
         dataType: "json",
         success: function(data) {
            var metaData=$.parseJSON(data[0].metaData);
            entityinfohtml = '<div class="ibox ibox-fullheight"><div class="ibox-head"><div class="ibox-title">' + data[0].name+ '</div>\
                    <div class="ibox-tools"><a class="font-strong text-pink" href="entity_info.html?id=' + data[0].id+ '" target="_blank">Open in new tab</a></div></div>\
                    <div class="ibox-body "><div class="row align-items-center mb-3"><div class="col-4 text-light">\
                    <b>ID:</b></div><div class="col-8"><small>' + data[0].entity_id+ '</small></div></div>\
                    <div class="row align-items-center mb-3"><div class="col-4 text-light"><b>Type:</b></div><div class="col-8"><small>' + data[0].type+ '</small></div></div>\
                    <div class="row align-items-center mb-3"><div class="col-4 text-light"><b>Link:</b></div><div class="col-8"><small><a href="' + data[0].link+ '"  target="_blank">' + data[0].link+ '</a></small></div></div>\
                    <div class="row align-items-center mb-3"><div class="col-4 text-light"><b>Name:</b></div><div class="col-8"><small>' + data[0].name+ '</small></div></div>\
                    ';  
            for (var key in metaData) {
                if (key != 'pheno') {
                    if(metaData[key] !=""){
                        entityinfohtml += '<div class="row align-items-center mb-3"><div class="col-4 text-light"><b>' + key + '</b></div><div class="col-8"><small>' + metaData[key] + '</small></div></div>';
                    }
                }
            }
            var pheno;
            if(metaData.pheno){
                pheno = metaData.pheno;
            }
            else{
                pheno = metaData.phenotypes;
            }
                 pheno = pheno.replace('\t+', '').replace('[', '').replace(']', '').split(',');
                 phenotext = '<table class="table pricing-table"><tbody>';
                 getphenoData(pheno);
             showSimilarity();
         },
         error: function(data) {
             console.log("error");

         }
     });
 }
 function getphenoData(pheno) {

     console.log(phenotext);
     var url = 'http://aber-owl.net/api/classes/?';
     $.each(pheno, function() {
         url = url + 'iri=http://purl.obolibrary.org/obo/' + $.trim(this) + '&';
     });
     url = url + 'ontology=PhenomeNET';

     $.ajax({
         url: url,
         dataType: 'json',
         success: function(data) {
             //console.log(data);
             $.each(data, function() {
                 $.each(this, function() {
                     //console.log(this.class+this.label+this.definition);
                     if (this.class != null) {
                         phenotext = phenotext + '<tr><td><div class="row align-items-center mb-3"><div class="col-4 text-light"><b>ID:</b></div><div class="col-8"><small><a href="' + this.class + '" target="_blank">' + this.class + '</a></small></div></div>\
                               <div class="row align-items-center mb-3"><div class="col-4 text-light"><b>Name:</b></div><div class="col-8"><small>' + ((this.label == null) ? '' : this.label) + '</small></div></div>\
                                <div class="row align-items-center mb-3"><div class="col-4 text-light"><b>definition:</b></div><div class="col-8"><small>' + ((this.definition == null) ? '' : this.definition) + '</small></div></td></tr>'
                     }
                 });
             });
             phenotext = phenotext + '</tbody></table>';
             var phenoHtml = '<div class="row align-items-center mb-3"><a data-toggle="collapse" href="#faq1-2"><b>phenotypes<i class="fa fa-angle-down"></i></b></a></div><div class="collapse" id="faq1-2">' + phenotext + '</div>'
             entityinfohtml += phenoHtml;
             entityinfohtml += '</div></div>';
             $('#entityinfo').append(entityinfohtml);

         },
         error: function(data) {
             console.log("error");
         }
     });
 }

 function showSimilarity() {

     $('#human_gene').empty();
     $('#disease').empty();
     $('#mouse_gene').empty();
     $('#Mouse_Allel').empty();
     $('#fish_genotype').empty();
     $('#fish_gene').empty();

     $('#human_gene').append('<ul class="list-group list-group-divider list-group-full faq-list">');
     $('#disease').append('<ul class="list-group list-group-divider list-group-full faq-list">');
     $('#mouse_gene').append('<ul class="list-group list-group-divider list-group-full faq-list">');
     $('#Mouse_Allel').append('<ul class="list-group list-group-divider list-group-full faq-list">');
     $('#fish_genotype').append('<ul class="list-group list-group-divider list-group-full faq-list">');
     $('#fish_gene').append('<ul class="list-group list-group-divider list-group-full faq-list">');
     $.ajax({
         url: 'http://localhost:19000/getSimilarityphenomenet2.groovy?term=' + targetId,
         dataType: "json",
         success: function(data) {
             console.log(data);
             $.each(data, function() {
                details = this;
                $('#' + details.type.replace(/ /g, '_')).append('<li class="list-group-item py-3"><div class="row"><div class="col-lg-9"><a class="fancybox fancybox.iframe" href="entity_info.html?id=' + details.id + '"  title="' + details.name+ '">' + details.name+'</a></div><div class="col-lg-3"><a class="fancybox fancybox.iframe" href="compare_pheno.html?e1='+targetId+'&e2='+details.id+'">compare Phenotypes</a></div></div><p class="text-light">' + parseFloat(details.score).toFixed(3) + '</p></li>');
             });
             $('#human_gene').append('</ul>');
             $('#disease').append('</ul>');
             $('#mouse_gene').append('</ul>');
             $('#Mouse_Allel').append('</ul>');
             $('#fish_genotype').append('</ul>');
             $('#fish_gene').append('</ul>');
         },
         error: function(data) {
             console.log("error");

         }
     });

 }