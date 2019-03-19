#!/bin/bash -l 
 
#*********************************************************************************************************************************************
#step1 Create phenomenet.owl ontology and fishies.txt
#*********************************************************************************************************************************************
# Required files 
curl -o phenoGeneCleanData_fish.txt https://zfin.org/downloads/phenoGeneCleanData_fish.txt
curl -o uberon_edit.obo http://purl.obolibrary.org/obo/uberon_edit.obo
curl -o mp.owl http://www.informatics.jax.org/downloads/reports/mp.owl	
curl -o mp.obo http://ontologies.berkeleybop.org/mp.obo
curl -o hp.obo http://ontologies.berkeleybop.org/hp.obo
curl -o hp.owl https://raw.githubusercontent.com/obophenotype/human-phenotype-ontology/master/hp.owl

#create the hp2mp.tsv file from the AgreementMakerLight.jar{Please don't move the AgreementMakerLight.jar from the folder or it will not work . 
#Make sure you increase the GVM memory when running the jar file depending on the ontologies sizes .You can run it using:
java -Xms5G  -jar AgreementMakerLight.jar 
#and then use the tool UI to save the file as .tsv tab seprated  format
# or by the following command
 java -Xms5G -jar AgreementMakerLight.jar -s store/anatomy/mp.obo -t store/anatomy/hp.obo -o /tmp/hp2mp.tsv -a
#then you will have a XML format file but to run the ReadEQsWithMappings.groovy script it needs to be in tab seprated format
#generate ontology
groovy ReadEQsWithMappings.groovy
groovy MakeInferredOntology.groovy a-with-mappings.owl phenomenet.owl

#*********************************************************************************************************************************************
#step 2 generate entities Annotation files
#*********************************************************************************************************************************************
#Required files 
#phenotype_annotation.tab-------- dieases
#MGI_PhenoGenoMP.rpt ------------mouse allel
#MGI_GenePheno.rpt --------------mouse gene  
#fishies.txt -------------------- fish gene ids, and fish geno ids
#genes_to_phenotype.txt----------human gene

#now we need to generate 12 files one>>2 files for each required file one contains the entities ID and the Phenotype ID and the other one contains the meta data for each entity



curl -o phenotype_annotation.tab http://compbio.charite.de/jenkins/job/hpo.annotations/lastStableBuild/artifact/misc/phenotype_annotation.tab
awk -F'\t' '{print ($1":"$2)"\t"("http://purl.obolibrary.org/obo/"$5)}' phenotype_annotation.tab > ph-ann.tab
perl -pi -w -e 's/HP:/HP_/g;'ph-ann.tab
curl -o MGI_PhenoGenoMP.rpt http://www.informatics.jax.org/downloads/reports/MGI_PhenoGenoMP.rpt
awk -F'\t' '{print ($1$3)"\t"$4}' MGI_PhenoGenoMP.rpt > MGI_PhenoGenoMP.txt
curl -o MGI_GenePheno.rpt http://www.informatics.jax.org/downloads/reports/MGI_GenePheno.rpt
awk -F'\t' '{print $7 "\t"("http://purl.obolibrary.org/obo/"$5)}' MGI_GenePheno.rpt > MGI_GenePhenoMP.txt
perl -pi -w -e 's/MP:/MP_/g;' MGI_GenePhenoMP.txt
curl -o genes_to_phenotype.tab http://compbio.charite.de/jenkins/job/hpo.annotations.monthly/lastSuccessfulBuild/artifact/annotation/ORPHA_ALL_FREQUENCIES_genes_to_phenotype.txt
awk -F'\t' '{print ("ENTRZ:"$1)"\t"("http://purl.obolibrary.org/obo/"$4)}' genes_to_phenotype.tab > genes_to_phenotype.txt
perl -pi -w -e 's/HP:/HP_/g;'genes_to_phenotype.txt
groovy Fish_MappingtoSimilartity.groovy
#generate one file that contain all the annotaion to run it against each file
cat  ph-ann.tab MGI_PhenoGenoMP.txt MGI_GenePhenoMP.txt genes_to_phenotype.txt fishgene.tsv fishgeno.tsv >allpheno.txt
#*********************************************************************************************************************************************
#step 3 generate Similarity Matrix for each file seprate as the generated files are very big
#*********************************************************************************************************************************************

groovy PNRevised.groovy -i URInewfishiegenes.txt -o fish-gene-similarity.txt

#*********************************************************************************************************************************************
#step 4 generate the meat data for the indixing, and create the csv files
#*************************************************************************************************************************************
# 1- human gene
 # https://www.genenames.org/cgi-bin/download/custom?col=gd_hgnc_id&col=gd_app_sym&col=gd_app_name&col=gd_status&col=gd_prev_sym&col=gd_aliases&col=gd_pub_chrom_map&col=gd_pub_acc_ids&col=gd_pub_refseq_ids&status=Approved&status=Entry%20Withdrawn&hgnc_dbtag=on&order_by=gd_app_sym_sort&format=text&submit=submit

# 2- mouse gene
#the meta data for all the genes in http://www.informatics.jax.org/downloads/reports/index.html#marker
curl -o MGI_MRK_Coord.rpt http://www.informatics.jax.org/downloads/reports/MGI_MRK_Coord.rpt
curl -o MGI_EntrezGene.rpt http://www.informatics.jax.org/downloads/reports/MGI_EntrezGene.rpt
awk -F'\t' '{print $1"\t"("ENTEZ:"$9)}' MGI_EntrezGene.rpt > HG_MG.txt

#3-disease data 
awk -F'\t' '{print ($1":"$2)"\t"$3}' phenotype_annotation.tab > ph-ann2.tab

#fish gene
# for the human_orthos
curl -o human_orthos.txt https://zfin.org/downloads/human_orthos.txt
#for the names, and the symbols
curl -o aliases.txt https://zfin.org/downloads/aliases.txt

groovy Create_entities_csv.groovy
#*********************************************************************************************************************************************
#step 5 prepare the csv files for the matrix
#*************************************************************************************************************************************
#generate a reduced matrix with top 100 similarity
groovy generate_final_matrix.groovy
#generate csv file to put it in mysql
groovy create_matrix_csv.groovy


#or index the matrix row by row
groovy insert_sql.groovy
#*********************************************************************************************************************************************
#alternative path for generating the matrix and index it in sql
#*************************************************************************************************************************************

#step1 generate entities csv files


groovy Create_entities_csv.groovy
#step2 map all the entity ids to the primary key in the sql database

groovy mapping_PK.groovy
#step3 run similarity script
groovy PNRevised.groovy -i URInewfishiegenes.txt -o fish-gene-similarity.txt

#step4 reduce the matrix to the top 100 match

groovy generate_final_matrix_withFK.groovy

#step5 put all the csv files in the sql database







