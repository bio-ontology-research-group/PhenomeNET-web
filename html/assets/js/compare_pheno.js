
$(document).ready(function() {
    e1 = getUrlVars().e1;
    e2 = getUrlVars().e2;
    $.ajax({
        url: 'http://localhost:19000/comparePhenotype.groovy?e1=' + e1+'&e2='+e2,
        dataType: "json",
        success: function(data) {
            console.log(data[0][0]);
            console.log(data[1][0]);
            console.log("**************************************");
            var metaData1=$.parseJSON(data[0][0].metaData);
            var metaData2=$.parseJSON(data[1][0].metaData);
            var pheno1;
            var pheno2;
            if(metaData1.pheno){
                pheno1 = metaData1.pheno;
                pheno1 = pheno1.replace('\t+', '').replace('[', '').replace(']', '').split(',');
            }
            else{
                pheno1 = metaData1.phenotypes;
                pheno1 = pheno1.replace('\t+', '').replace('[', '').replace(']', '').split(',');
            }
            if(metaData2.pheno){
                pheno2 = metaData2.pheno;
                pheno2 = pheno2.replace('\t+', '').replace('[', '').replace(']', '').split(',');
            }
            else{
                pheno2 = metaData2.phenotypes;
                pheno2 = pheno2.replace('\t+', '').replace('[', '').replace(']', '').split(',');
            }
                 $.each(pheno2, function() {
                    console.log(this);
                 }); 
            $.each(pheno1, function(idx, value) {
                console.log(value);
            if ($.inArray(value, pheno2) !== -1) {
                console.log('Match Prod: ' + value);
            } else {
                console.log('Not Match: ' + value);
            }
            });
                var e1infohtml = '<div class="ibox ibox-fullheight"><div class="ibox-head"><div class="ibox-title">' + data[0][0].name+ '</div></div><div class="ibox-body ">';
                var e2infohtml = '<div class="ibox ibox-fullheight"><div class="ibox-head"><div class="ibox-title">' + data[1][0].name+ '</div></div><div class="ibox-body ">';

            getphenoData("e1",e1infohtml,pheno1);
            getphenoData("e2",e2infohtml,pheno2);
        }
            ,
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

function getphenoData(div,einfohtml,pheno) {
    console.log(pheno);
    var url = 'http://aber-owl.net/api/classes/?';
    $.each(pheno, function() {
        if($.trim(this).includes("PHENO")){

            url=url+'iri='+encodeURIComponent('http://aber-owl.net/phenotype.owl#' + $.trim(this)) + '&';
        }
        else{
            url = url + 'iri=http://purl.obolibrary.org/obo/' + $.trim(this) + '&';
        }
    });
    url = url + 'ontology=PhenomeNET';
    console.log(url);
    var ephenotext = '<table class="table pricing-table"><tbody>';
    $.ajax({
        url: url,
        dataType: 'json',
        success: function(data) {
            $.each(data, function() {
                $.each(this, function() {
                    if(this.class!=null){
                    ephenotext=ephenotext + '<tr><td><div class="row align-items-center mb-3"><div class="col-4 text-light"><b>ID:</b></div><div class="col-8"><small><a href="'+ this.class+'" target="_blank">' + this.class+ '</a></small></div></div>\
                               <div class="row align-items-center mb-3"><div class="col-4 text-light"><b>Name:</b></div><div class="col-8"><small>' + ((this.label==null) ? '' : this.label)+ '</small></div></div>\
                                <div class="row align-items-center mb-3"><div class="col-4 text-light"><b>definition:</b></div><div class="col-8"><small>' + ((this.definition==null) ? '' : this.definition)+ '</small></div></td></tr>'
                    }
                });
            });
            ephenotext = ephenotext + '</tbody></table>';
                var phenoHtml = '<div class="row align-items-center "><a data-toggle="collapse" href="#'+div+'-link"><b>phenotypes<i class="fa fa-angle-down"></i></b></a></div><div class="collapse" id="'+div+'-link">' + ephenotext + '</div>'
                einfohtml += phenoHtml;
                einfohtml += '</div></div></div>';
                $('#'+div).append(einfohtml);
                    
        },
        error: function(data) {
            console.log("error");
        }
    });
}

function putsharedpheno(sharedpheno) {

    var url = 'http://aber-owl.net/api/classes/?';
    $.each(pheno1, function() {
        url = url + 'iri=http://purl.obolibrary.org/obo/' + $.trim(this) + '&';
    });
    url = url + 'ontology=PhenomeNET';
    var ephenotext = '<table class="table pricing-table"><tbody>';
    $.ajax({
        url: url,
        dataType: 'json',
        success: function(data) {
            $.each(data, function() {
                $.each(this, function() {
                    if(this.class!=null){
                    ephenotext=ephenotext + '<tr><td><div class="row align-items-center mb-3"><div class="col-4 text-light"><b>ID:</b></div><div class="col-8"><small><a href="'+ this.class+'" target="_blank">' + this.class+ '</a></small></div></div>\
                               <div class="row align-items-center mb-3"><div class="col-4 text-light"><b>Name:</b></div><div class="col-8"><small>' + ((this.label==null) ? '' : this.label)+ '</small></div></div>\
                                <div class="row align-items-center mb-3"><div class="col-4 text-light"><b>definition:</b></div><div class="col-8"><small>' + ((this.definition==null) ? '' : this.definition)+ '</small></div></td></tr>'
                    }
                });
            });
            ephenotext = ephenotext + '</tbody></table>';
                var phenoHtml = '<div class="row align-items-center "><a data-toggle="collapse" href="#'+div+'-link"><b>phenotypes<i class="fa fa-angle-down"></i></b></a></div><div class="collapse" id="'+div+'-link">' + ephenotext + '</div>'
                einfohtml += phenoHtml;
                einfohtml += '</div></div></div>';
                $('#'+div).append(einfohtml);
                    
        },
        error: function(data) {
            console.log("error");
        }
    });

}