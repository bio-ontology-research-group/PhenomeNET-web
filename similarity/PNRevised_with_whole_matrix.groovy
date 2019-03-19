@Grab(group='com.github.sharispe', module='slib-sml', version='0.9.1') 
@Grab(group='org.codehaus.gpars', module='gpars', version='1.2.0')

import java.net.*
import org.openrdf.model.vocabulary.*
import slib.sglib.io.loader.*
import slib.sml.sm.core.metrics.ic.utils.*
import slib.sml.sm.core.utils.*
import slib.sglib.io.loader.bio.obo.*
import org.openrdf.model.URI
import slib.graph.algo.extraction.rvf.instances.*
import slib.sglib.algo.graph.utils.*
import slib.utils.impl.Timer
import slib.graph.algo.extraction.utils.*
import slib.graph.model.graph.*
import slib.graph.model.repo.*
import slib.graph.model.impl.graph.memory.*
import slib.sml.sm.core.engine.*
import slib.graph.io.conf.*
import slib.graph.model.impl.graph.elements.*
import slib.graph.algo.extraction.rvf.instances.impl.*
import slib.graph.model.impl.repo.*
import slib.graph.io.util.*
import slib.graph.io.loader.*
import slib.graph.algo.extraction.rvf.DescendantEngine
import slib.graph.algo.accessor.GraphAccessor
import groovyx.gpars.ParallelEnhancer
import groovyx.gpars.GParsPool
import java.lang.*
import java.io.*
println "Starting........"
System.setProperty("jdk.xml.entityExpansionLimit", "0")
System.setProperty("jdk.xml.totalEntitySizeLimit", "0");

def cli = new CliBuilder()
cli.with {
usage: 'Self'
  h longOpt:'help', 'this information'
  i longOpt:'input-file', 'disease definitions input file', args:1, required:true
  o longOpt:'output', 'output file', args:1, required:true
  
}
println "Starting........"
def opt = cli.parse(args)
if( !opt ) {
 
  return
}
if( opt.h ) {
    cli.usage()
    return
}

def mpfile = new File("allpheno_PK.txt")
def disfile = new File(opt.i)
def log =  new File("log_exception.txt")
def graphfile = new File("graph.txt")
def fout = new PrintWriter(new BufferedWriter(new FileWriter(opt.o)))
//PrintStream ps = new PrintStream(log);

println "Starting........"
String uri = "http://phenomebrowser.net/smltest/"

URIFactory factory = URIFactoryMemory.getSingleton()

URI graph_uri = factory.getURI(uri)

G graph = new GraphMemory(graph_uri)

GDataConf graphconf = new GDataConf(GFormat.RDF_XML, "PN_inferred.owl") //use latest version of phenoment!
GraphLoaderGeneric.populate(graphconf, graph)

URI virtualRoot = factory.getURI("http://purl.obolibrary.org/obo/virtualRoot");
graph.addV(virtualRoot);
        
// We root the graphs using the virtual root as root
GAction rooting = new GAction(GActionType.REROOTING);
rooting.addParameter("root_uri", virtualRoot.stringValue());
GraphActionExecutor.applyAction(factory, rooting, graph);
// remove all instances
Set removeE = new LinkedHashSet()
graph.getE().each { it ->
  graphfile.append it
  graphfile.append '\n'
  String es = it.toString();
  if ( es.indexOf("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")>-1 ) {
    removeE.add( it );
  }
}               
removeE.each { graph.removeE(it) }

// adding instances
println "adding instances........"
mpfile.splitEachLine("\t") { line ->
  def id = line[0].replaceAll(":","_")

    def iduri = factory.getURI(uri+id)
    def mp = line[1]?.trim()
    //mp = mp?.replaceAll(":","_")
    def mpuri = factory.getURI(mp)
    if (mp && id && iduri && mpuri && mp.length()>0 && id.length()>0) {
      Edge e = new Edge(iduri, RDF.TYPE, mpuri);
      println e
      graph.addE(e)
    }
}

println "Reading disease file..."
def map = [:].withDefault { new LinkedHashSet() }
disfile.splitEachLine("\t") { line ->
  def doid = line[0].replaceAll(":","_")
    def pid = line[1]
    pid = pid.trim()
    if (pid && pid.length()>0) {
      def piduri = factory.getURI(pid)
      if (graph.containsVertex(piduri)) {
        map[doid].add(piduri)
      }
    }
  
}


ICconf icConf = new IC_Conf_Corpus("Resnik", SMConstants.FLAG_IC_ANNOT_RESNIK_1995)
SMconf smConfGroupwise = new SMconf("BMA", SMConstants.FLAG_SIM_GROUPWISE_BMA);
SMconf smConfPairwise = new SMconf("Resnik", SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_RESNIK_1995 );

smConfPairwise.setICconf(icConf)

SM_Engine engine = new SM_Engine(graph)

println "Computing similarity..."
InstancesAccessor ia = new InstanceAccessor_RDF_TYPE(graph)


GParsPool.withPool {
  map.eachParallel{ doid, phenos ->
    if (phenos.size() > 0) {
      println "Processing $doid..."
      def similarityMatrix=[]
      engine.getInstances().each { mgi ->
        Set mgiSet = ia.getDirectClass(mgi)

        if (mgiSet.size()>0){
          try {

            def sim = engine.compare(smConfGroupwise, smConfPairwise, phenos, mgiSet)
            fout.println("$doid\t$mgi\t$sim")
          } 
          catch (Exception e) {
            println "$doid------------$mgi"
            println '--------------------------------------------------------------------------------'
            println phenos
            println '--------------------------------------------------------------------------------'
            println mgiSet
            println '--------------------------------------------------------------------------------'

            //println (e.printStackTrace())
            //log.append("$doid\t$mgi\n")

          }        
        }
      }
  }
}
}
fout.flush()
fout.close()
