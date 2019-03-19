@Grab('com.xlson.groovycsv:groovycsv:1.3')
import static com.xlson.groovycsv.CsvParser.parseCsv
import groovy.json.* 

def log= new File('./data/csv_data/log.txt')
def index=1
def data=[:]
/*prepare human gene meta data csv till 3738*/

String [] hgenes_metadata=new File('meta_data/hgeneMetaData.csv')
def csv_hg_data= new File('./data/csv_data/csv_humangene_data.csv')
def primarykey_hgene= new File('./data/csv_data/primarykey_hgene.txt')
def header=hgenes_metadata[0]
println "strating human gene"
new File('./files/genes_to_phenotype.txt').eachLine { line ->
    entity=line.split('\t')
    if(data.containsKey(entity[0])) { 
        data.get(entity[0]).add(entity[-1].toString().minus("http://purl.obolibrary.org/obo/"))
    }
    else{
        List <String> pheno = new ArrayList<>()
        pheno.add(entity[-1].toString().minus("http://purl.obolibrary.org/obo/"))
        data.put(entity[0],pheno) 
    }
}
println "writing human gene csv"
data.each{ entity_id, phenos ->
    def gene_metaData=hgenes_metadata.find {it.split(",",-1)[18] == entity_id.minus("ENTREZ:")}
    def name=''
    def meta_data_json='{'
    if(gene_metaData!=null){
        def i=0
        gene_metaData.split(",",-1).each{
            if (i!=2){
                meta_data_json+=/"${header.split(",",-1)[i]}":"${it.toString().replaceAll('"','').replaceAll("'","")}",/
            }
            i++
        }
        meta_data_json+=/"pheno":"$phenos"/
        meta_data_json+='}'
        name=gene_metaData.split(",",-1)[2]
    }
    else{
        log.append(entity_id)
        log.append '\n'
    }
    def row=[index,"'"+name+"'","'https://www.ncbi.nlm.nih.gov/gene/"+entity_id.minus("ENTREZ:")+"'",'human gene',"'"+meta_data_json+"'","'"+entity_id+"'"]
    csv_hg_data.append row.join(',')
    csv_hg_data.append '\n'
    primarykey_hgene.append(index+'\t'+entity_id+'\n')
    println index
    index++
}
log.append("human index:"+index)
hgenes_metadata=[]

println "human index:"+index
println"*******************************************************************"

/*prepare Fish Geno meta data csv 18478*/ 
println "strating Fish Geno gene"
def csv_fgeno_data= new File('./data/csv_data/csv_fishgeno_data.csv')
def primarykey_fgeno= new File('./data/csv_data/primarykey_fgeno.txt')
data=[:]
new File('./files/fishgeno.tsv').eachLine { line ->
    entity=line.split('\t')
    if(data.containsKey(entity[0])) { 
        data.get(entity[0]).add(entity[-1].toString().minus("http://aber-owl.net/phenotype.owl#"))
    }
    else{
        List <String> pheno = new ArrayList<>()
        pheno.add(entity[-1].toString().minus("http://aber-owl.net/phenotype.owl#"))
        data.put(entity[0],pheno)   
    }
}
println "writing Fish Geno csv"
data.each{ entity_id, phenos ->
    def metaData=/{"phenotypes":"$phenos","gene":"${entity_id.split("/")[0]}"}/     
    def row=[index,"'"+entity_id+"'","'"+''+"'","'"+'fish genotype'+"'","'"+metaData+"'","'"+entity_id+"'"]
    csv_fgeno_data.append row.join(',')
    csv_fgeno_data.append '\n'
    primarykey_fgeno.append(index+'\t'+entity_id+'\n')
    println index
    index++
} 
log.append("Fish Geno index:"+index)
println "Fish Geno index:"+index
println"*******************************************************************"

/*prepare mouse allel meta data csv 78597*/
println "strating mouse allel gene"
String [] mouseAllel_metaData=new File('./meta_data/MGI_PhenoGenoMPmetadata.txt')
def csv_mAllel_data= new File('./data/csv_data/csv_mouseAllel_data.csv')
def primarykey_mAllel= new File('./data/csv_data/primarykey_MAllel.txt')
data=[:]
new File('./files/URIMGI_PhenoGenoMP.txt').eachLine { line ->
    entity=line.split('\t')
    if(data.containsKey(entity[0])) { 
        data.get(entity[0]).add(entity[-1].toString().minus("http://purl.obolibrary.org/obo/"))
    }
    else{
        List <String> pheno = new ArrayList<>()
        pheno.add(entity[-1].toString().minus("http://purl.obolibrary.org/obo/"))
        data.put(entity[0],pheno) 
    }
}
println "writing mouse allel csv"
data.each{ entity_id, phenos ->
    def entity_metaData=mouseAllel_metaData.find {it.split('\t',-1)[0] == entity_id}
    def name=''
    if(entity_metaData!=null){
        name=entity_metaData.split('\t',-1)
    }
    else{
        log.append(entity_id)
        log.append '\n'
    }
    def metaData=/{"phenotypes":"$phenos","mouse gene":"${name[-1]}"}/     
    def row=[index,"'"+entity_id+"'","'"+''+"'","'"+'Mouse Allel'+"'","'"+metaData+"'","'"+entity_id+"'"]
    csv_mAllel_data.append row.join(',')
    csv_mAllel_data.append '\n'
    primarykey_mAllel.append(index+'\t'+entity_id+'\n')
    println index
    index++
}
log.append("mouse allel index:"+index)
mouseAllel_metaData=[]
println "mouse allel index:"+index
println"*******************************************************************"

/*prepare Fish Gene meta data csv 84448*/ 
println "strating Fish gene"
def csv_fgene_data= new File('./data/csv_data/csv_fishgene_data.csv')
def primarykey_fgene= new File('./data/csv_data/primarykey_fgene.txt')
String [] fgenes_metaData=new File('./meta_data/HG-FG.txt')
data=[:]
new File('./files/fishgene.tsv').eachLine { line ->
    entity=line.split('\t')
    if(data.containsKey(entity[0])) { 
        data.get(entity[0]).add(entity[-1].toString().minus("http://aber-owl.net/phenotype.owl#"))
    }
    else{
        List <String> pheno = new ArrayList<>()
        pheno.add(entity[-1].toString().minus("http://aber-owl.net/phenotype.owl#"))
        data.put(entity[0],pheno)   
    }
}
println "writing Fish Gene csv"
data.each{ entity_id, phenos ->

    def gene_metaData=fgenes_metaData.find {it.split()[0] == entity_id}
    def name=''
    def metaData=''
    if(gene_metaData!=null){
        metaData=/{"human gene":"${gene_metaData.split()[-1]}","symbol":"${gene_metaData.split()[1]}","phenotypes":"$phenos"}/ 
        name=gene_metaData.split()[2] 
    }
    else{
        metaData=/{"phenotypes":"$phenos"}/ 
        log.append(entity_id)
        log.append '\n'
    }
    def row=[index,"'"+entity_id+"'","'https://zfin.org/"+entity_id+"'","'"+'fish gene'+"'","'"+metaData+"'","'"+name+"'"]
    csv_fgene_data.append row.join(',')
    csv_fgene_data.append '\n'
    primarykey_fgene.append(index+'\t'+entity_id+'\n')
    println index
    index++
} 
log.append("Fish Gene index:"+index)
fgenes_metaData=[]
println "Fish Gene index:"+index
println"*******************************************************************"


/*prepare diseases meta data csv 94832*/ 
println "strating diseases"
def csv_disease_data= new File('./data/csv_data/csv_disease_data.csv')
def primarykey_disease= new File('./data/csv_data/primarykey_disease.txt')
String [] disease_metaData=new File('./meta_data/ph-ann2.tab')
data=[:]
new File('./files/URIphenotype_annotation.txt').eachLine { line ->
    entity=line.split('\t')
    if(data.containsKey(entity[0])) { 
        data.get(entity[0]).add(entity[-1].toString().minus("http://purl.obolibrary.org/obo/"))
    }
    else{
        List <String> pheno = new ArrayList<>()
        pheno.add(entity[-1].toString().minus("http://purl.obolibrary.org/obo/"))
        data.put(entity[0],pheno)   
    }
}
println "writing disease csv"
data.each{ entity_id, phenos ->
    def name=''
    def link=''
    def entity_metaData=disease_metaData.find {it.split('\t')[0] == entity_id}
    if(entity_metaData!=null){
        name=entity_metaData.split('\t')[-1].replaceAll('"','').replaceAll(',',';')
    }
    else{
        log.append(entity_id)
        log.append '\n'
    }
    if(entity_id.contains("OMIM")){
        link="http://omim.org/entry/"+entity_id.minus("OMIM:")
    }
    if(entity_id.contains("ORPHA")){
        link="https://www.orpha.net/consor/cgi-bin/OC_Exp.php?Lng=GB&Expert="+entity_id.minus("ORPHA:")
    }
    def metaData=/{"phenotypes":"$phenos"}/
    def row=[index,"'"+entity_id+"'","'"+link+"'","'"+'disease'+"'","'"+metaData+"'","'"+name+"'"]
    csv_disease_data.append row.join(',')
    csv_disease_data.append '\n'
    primarykey_disease.append(index+'\t'+entity_id+'\n')
    println index
    index++
}
log.append("disease index:"+index) 
disease_metaData=[]
println "disease index:"+index
println"*******************************************************************"

/*prepare mouse gene meta data csv 106857*/ 
def data=[:]
println "strating mouse gene"
def csv_mouse_gene_data= new File('./data/csv_data/csv_mouse_gene_data21-2-2019.csv')
def primarykey_mouse_gene= new File('./data/csv_data/primarykey_mouse_gene.txt')
String [] mouse_gene_metaData=new File('./meta_data/MGI_MRK_Coord.rpt')
String [] names=new File('./meta_data/MGI_EntrezGene.rpt')
String [] hg_mg=new File('./meta_data/HG_MG.txt')
header=mouse_gene_metaData[0]
data=[:]
new File('./files/URIMGI_GenePheno.txt').eachLine { line ->
    entity=line.split('\t')
    if(data.containsKey(entity[0])) { 
        data.get(entity[0]).add(entity[-1].toString().minus("http://purl.obolibrary.org/obo/"))
    }
    else{
        List <String> pheno = new ArrayList<>()
        pheno.add(entity[-1].toString().minus("http://purl.obolibrary.org/obo/"))
        data.put(entity[0],pheno)   
    }
}
println "writing mouse gene csv"
data.each{ entity_id, phenos ->
    def gene_metaData=mouse_gene_metaData.find {it.split("\\t",-1)[0] == entity_id}
    def gene_hid=hg_mg.find {it.split("\\t",-1)[0].trim() == entity_id}
    def name=''
    def link=''
    def meta_data_json='{'
    if(gene_metaData!=null){
        for (i = 0; i <12 ; i++) {
            if(1!=4){
                meta_data_json+=/"${header.split("\\t",-1)[i]}":"${gene_metaData.split("\\t",-1)[i].replaceAll('"','').replaceAll("'","")}",/   
            }
        }
        if(gene_hid!=null){meta_data_json+=/"human_gene_id":"${gene_hid.split("\\t",-1)[-1]}",/}
        meta_data_json+=/"pheno":"$phenos"/
        meta_data_json+='}'
        name=gene_metaData.split("\\t",-1)[4]
    }
    else{
        if(gene_hid!=null){meta_data_json+=/"human_gene_id":"${gene_hid.split("\\t",-1)[-1]}",/}
        meta_data_json+=/"pheno":"$phenos"/
        meta_data_json+='}'
        def gene_name=names.find {it.split("\\t",-1)[0] == entity_id}
        if(gene_name!=null){
            name=gene_name.split("\\t",-1)[3]
        }
        else{
            log.append(entity_id)
            log.append '\n'
        }
    }
    link="http://www.informatics.jax.org/marker/"+entity_id
    def row=[index,"'"+entity_id+"'","'"+link+"'","'"+'mouse gene'+"'","'"+meta_data_json+"'","'"+name+"'"]
    csv_mouse_gene_data.append row.join(',')
    csv_mouse_gene_data.append '\n'
    primarykey_mouse_gene.append(index+'\t'+entity_id+'\n')
    println index
    index++
}
log.append("mouse gene index:"+index) 
println "mouse gene index:"+index
println"*******************************************************************"



