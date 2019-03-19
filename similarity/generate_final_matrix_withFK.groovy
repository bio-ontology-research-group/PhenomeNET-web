/*Prepare mouse allel  matrix*/
def i=0
entity_id=''
human_gene=[:]
mouse_gene=[:]
fish_gene=[:]
disease=[:]
fish_geno=[:]
mouse_allel=[:]
reduced_matrix= new File('reduced_mouse_allel.txt')
log= new File ('sorted_mouse_allel_log.txt')


new File('mouse_example.txt').eachLine { line ->
     entity=line.split('\t')
    if(entity_id !=entity[0]){
        println entity_id+'----------'+ entity[0]
        if(i!=0){
            insert_into_arrays(entity)
            human_gene=human_gene.sort {a, b -> b.value <=> a.value}
            mouse_gene=mouse_gene.sort {a, b -> b.value <=> a.value}
            fish_gene=fish_gene.sort {a, b -> b.value <=> a.value}
            println disease.size()
            disease=disease.sort {a, b -> b.value <=> a.value}
            println disease.size()
            fish_geno=fish_geno.sort {a, b -> b.value <=> a.value}
            mouse_allel=mouse_allel.sort {a, b -> b.value <=> a.value}
            store_entity_infile()
            entity_id=entity[0]
            human_gene=[:]
            mouse_gene=[:]
            fish_gene=[:]
            disease=[:]
            fish_geno=[:]
            mouse_allel=[:]
        }
        else{
            entity_id=entity[0]
            insert_into_arrays(entity)
        }
        i++
        println i
    }
    else{
        insert_into_arrays(entity)
    }
}
store_entity_infile()
println i
def insert_into_arrays(String [] entity){
    def entity2_id=entity[1].minus('http://phenomebrowser.net/smltest/').toInteger()
    if(entity2_id>0&&entity2_id<3739){
        human_gene.put(entity[1].minus('http://phenomebrowser.net/smltest/'),Float.parseFloat(entity[2]))
    }
    else if(entity2_id>84448&&entity2_id<94833){
        disease.put(entity[1].minus('http://phenomebrowser.net/smltest/'),Float.parseFloat(entity[2]))
    }
    else if(entity2_id>94832&&entity2_id<106858){
        mouse_gene.put(entity[1].minus('http://phenomebrowser.net/smltest/'),Float.parseFloat(entity[2]))
    }
    else if(entity2_id>78597&&entity2_id<84449){
        fish_gene.put(entity[1].minus('http://phenomebrowser.net/smltest/'),Float.parseFloat(entity[2]))
    }
    else if(entity2_id>3738&&entity2_id<18479){
        fish_geno.put(entity[1].minus('http://phenomebrowser.net/smltest/'),Float.parseFloat(entity[2]))
    }
    else if(entity2_id>18478&&entity2_id<78598) {
        mouse_allel.put(entity[1].minus('http://phenomebrowser.net/smltest/'),Float.parseFloat(entity[2]))
    }
}

def store_entity_infile(){
    log.append entity_id+'\n'
    log.append ('human_gene----'+human_gene.size()+'\n')
    log.append ('disease--'+disease.size()+'\n')
    log.append ('mouse_gene---'+mouse_gene.size()+'\n')
    log.append ('fish_geno--'+fish_geno.size()+'\n')
    log.append ('fish_gene--'+fish_gene.size()+'\n')
    log.append ('mouse_allel--'+mouse_allel.size()+'\n')
    log.append ('*********************************************************************************'+'\n')
    def map_sorting={a,b ->
        int j=0
        a.each{ k, v ->
            if(j<=100){
                def row=[entity_id,k,v]
                reduced_matrix.append row.join(',')
                reduced_matrix.append '\n'
            }
            j++
}
    }
    map_sorting.call(human_gene,"human_gene")
    map_sorting.call(disease,"disease")
    map_sorting.call(mouse_gene,"mouse gene")
    map_sorting.call(fish_geno,"fish genotype")
    map_sorting.call(fish_gene,"fish gene")
    map_sorting.call(mouse_allel,"Mouse Allel")

}
