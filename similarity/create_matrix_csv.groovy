@Grab('com.xlson.groovycsv:groovycsv:1.3')
import static com.xlson.groovycsv.CsvParser.parseCsv
import groovy.json.* 
def i=1
def log= new File('./files/reduced_log_annotaion.txt')
def file_PK= new File('./data/csv_data/reduced_fishgeno_PK.txt')

PK=[:]
println "Putting all PK"
new File('./files/allPK.txt').eachLine { line ->
    entity=line.split('\t')
    log.append (entity[1].toString().replaceAll('>','').replaceAll('<',''))
    log.append '\n'
    PK.put(entity[1].toString().replaceAll('>','').replaceAll('<',''),entity[0])   
    i++
    println i
} 
println'****************************************************************************'
println'****************************************************************************'
println'****************************************************************************'
println'****************************************************************************'
println'****************************************************************************'
println'****************************************************************************'
i=0
println "mapping allpheno.txt"
new File('/Users/abdems0a/myfolder/Phenomenet/similariry/reduced_fishgene_similarity.txt').eachLine { line ->
    entity=line.split('\t')
    println i
    log.append entity[1].toString().minus('http://phenomebrowser.net/smltest/').replaceAll('_',':')+'------------------------'+PK[entity[1].toString().minus('http://phenomebrowser.net/smltest/').replaceAll('_',':')]
    log.append '\n'
    if(PK.containsKey(entity[0].toString().replaceAll('_',':')) &&PK.containsKey(entity[1]).toString().minus('http://phenomebrowser.net/smltest/').replaceAll('_',':')) {
    	file_PK.append(PK[entity[0].toString().replaceAll('_',':')]+','+PK[entity[1].toString().minus('http://phenomebrowser.net/smltest/').replaceAll('_',':')]+','+entity[2]+'\n')

    } 
    else{
    	log.append(line+'\n') 
    }
    i++
    println i
}
