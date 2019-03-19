// preparing the fish files
String [] entities=new File('./oldontologies/fishies.txt')

def genefile= new PrintWriter(new BufferedWriter(new FileWriter('fishgene.tsv')))
def genofile= new PrintWriter(new BufferedWriter(new FileWriter('fishgeno.tsv')))

entities.each{ line ->

	cols= line.split('\t',-1)
	def row=[]
	def geneID=""
	def genoID=""
	def phenoID=""
	def i=0
	println line
	geneID=cols[2]
	genoID=cols[18]
	if(cols[-1].contains("http://aber-owl.net/phenotype.owl#PHENO")){
		phenoID=cols[-1]
		println ("$geneID\t$phenoID")
		println ("$geneID/$genoID\t$phenoID")
		genefile.println ("$geneID\t$phenoID")
		genofile.println ("$geneID/$genoID\t$phenoID")
	}

	println "--------------------------------------------------------------------------------"
}
genefile.flush()
genofile.flush()
genefile.close()
genofile.close()