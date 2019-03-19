var targetId;
var phenotext='';
var entityinfohtml='';
$(document).ready(function() {
    targetId = getUrlVars().id;
    $.ajax({
        url: 'http://localhost:19000/searchByIDphenomenet2.groovy?term=' + targetId,
        dataType: "json",
        success: function(data) {
            var metaData=$.parseJSON(data[0].metaData);
                entityinfohtml = '<div class="ibox ibox-fullheight"><div class="ibox-head"><div class="ibox-title">' + data[0].name+ '</div></div>\
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
        },
        error: function(data) {
            console.log("error");

        }
    });
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
            $.each(data, function() {
                $.each(this, function() {
                    if(this.class!=null){
                    phenotext=phenotext + '<tr><td><div class="row align-items-center mb-3"><div class="col-4 text-light"><b>ID:</b></div><div class="col-8"><small><a href="'+ this.class+'" target="_blank">' + this.class+ '</a></small></div></div>\
                               <div class="row align-items-center mb-3"><div class="col-4 text-light"><b>Name:</b></div><div class="col-8"><small>' + ((this.label==null) ? '' : this.label)+ '</small></div></div>\
                                <div class="row align-items-center mb-3"><div class="col-4 text-light"><b>definition:</b></div><div class="col-8"><small>' + ((this.definition==null) ? '' : this.definition)+ '</small></div></td></tr>'
                            }
                });
            });
            console.log(phenotext);
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