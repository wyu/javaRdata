package org.ms2ms.graph;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanTransaction;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import org.jgrapht.Graph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.ms2ms.utils.Strs;
import org.ms2ms.utils.Tools;
import psidev.psi.mi.xml.model.*;

import java.util.*;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   12/9/14
 */
public class Graphs
{
  public static final String NAME  = "name";
  public static final String ID    = "id";
  public static final String UID   = "uid";
  public static final String LABEL = "label";
  public static final String TITLE = "title";
  public static final String TYPE  = "_TYPE_";

  public static final String DRUG     = "DRUG";
  public static final String SNP      = "SNP";
  public static final String STUDY    = "STUDY";
  public static final String TRAIT    = "TRAIT";
  public static final String GENE     = "GENE";
  public static final String INSTANCE = "INSTANCE";
  public static final String SM       = "SM";
  public static final String DISEASE  = "DISEASE";
  public static final String TISSUE   = "TISSUE";
  public static final String ASSAY    = "ASSAY";
  public static final String COMPLEX  = "COMPLEX";
  public static final String CHR      = "Chr";
  public static final String CHR_POS  = "ChrPos";

  //  public static String INTACT_ACTOR_ID = "IntAct_ActorID";
//  public static String INTACT_EXPT_ID = "IntAct_ExptID";
//  public static String INTACT_ACTION_ID = "IntAct_ActionID";

  //  public static XgmmlReader readXGMMLs(File dir) throws Exception
//  {
////    File dir = new File("/bioinfo/scratch/wyu/staging/network/temp/");
//    String[] xgmmls = dir.list(new FilenameFilter() {
//      @Override
//      public boolean accept(File dir, String name)
//      {
//        return name.endsWith(".xgmml");
//      }
//    });
//    XgmmlReader totals = new XgmmlReader();
//    for (String xgmml : xgmmls)
//    {
//      totals.accumulate(new XgmmlReader(dir.getAbsolutePath(), xgmml));
//    }
//    return totals;
////    DirectedGraph<PropertyNode, PropertyEdge> graph = XgmmlReader.read(dir.getAbsolutePath(), xgmmls);
//  }
  // adapted from stackoverflow.com/question/58306/graph-algorithm-to-find-all-connections-between-two-arbitrary-vertices
  public static Collection<List<PropertyNode>> simplePaths(DefaultDirectedGraph<PropertyNode, PropertyEdge> graph,
                                                           PropertyNode START, PropertyNode END, int max)
  {
    Collection<List<PropertyNode>> paths = new ArrayList<>();
    LinkedList<PropertyNode> visited = new LinkedList<>();
    Set<PropertyNode> cache = new HashSet<>();
    visited.add(START); cache.add(START);
    max = breadthFirst(graph, visited, cache, END, paths, max);

    return paths;
  }
  public static int breadthFirst(DefaultDirectedGraph<PropertyNode, PropertyEdge> graph,
                                 LinkedList<PropertyNode> visited, Set<PropertyNode> cache,
                                 PropertyNode END, Collection<List<PropertyNode>> paths, int max)
  {
    // limit the depth of the search away from the starting node
//    if (max<=0) return max;

    LinkedList<PropertyNode> nodes = new LinkedList<>();
    nodes.addAll(getTargetNodes(graph.outgoingEdgesOf(visited.getLast()), graph));

    for (PropertyNode node : nodes)
    {
      if (cache.contains(node)) continue;
      if (node.equals(END))
      {
        visited.add(node); cache.add(node);
        paths.add(new ArrayList<PropertyNode>(visited));
        cache.remove(visited.removeLast()); break;
      }
    }
    // in breadth-first, recursion needs to come after visiting adjacent nodes
    for (PropertyNode node : nodes)
    {
      if (cache.contains(node) || node.equals(END)) continue;
      visited.addLast(node); cache.add(node);
      // terminate it if the path length hits the maximum
      if (visited.size()<max) breadthFirst(graph, visited, cache, END, paths, max);
//      breadthFirst(graph, visited, cache, END, paths, max);
      cache.remove(visited.removeLast());
    }
    return max--;
  }
  private static Collection<PropertyNode> getTargetNodes(Collection<PropertyEdge> edges, Graph<PropertyNode, PropertyEdge> graph)
  {
    if (edges==null || graph==null) return null;

    Collection<PropertyNode> nodes = new ArrayList<PropertyNode>();
    for (PropertyEdge edge : edges)
      nodes.add(graph.getEdgeTarget(edge));

    return nodes;
  }
  private static Collection<PropertyNode> getSourceNodes(Collection<PropertyEdge> edges, Graph<PropertyNode, PropertyEdge> graph)
  {
    if (edges==null || graph==null) return null;

    Collection<PropertyNode> nodes = new ArrayList<PropertyNode>();
    for (PropertyEdge edge : edges)
      nodes.add(graph.getEdgeSource(edge));

    return nodes;
  }
//  public static PropertyNode addNode(GraphDatabaseService graph, PropertyNode n, Map<Long, PropertyNode> id_node)
//  {
//    if (id_node!=null && id_node.containsKey(n.getID())) return id_node.get(n.getID());
//
//    // save the nodes and edge to the DB
//    PropertyNode N = graph.createNode();
//    setProperty(N, "NAME", n.getName());
//    setProperty(N, "ID", n.getID());
//
//    // save the rest of the properties
//    if (n.getFeature()!=null && n.getFeature().getProperties()!=null)
//      for (String key : n.getFeature().getProperties().keySet())
//        setProperty(N, key, n.getFeature().getProperty(key));
//
//    // setup the labels if necessary
//    if (n.hasFeature(XgmmlReader.XML_GENE_NAME))
//    {
//      N.addLabel(DynamicLabel.label("GENE"));
//      if (n.hasFeature(XgmmlReader.XML_LOCATION)) N.addLabel(DynamicLabel.label("LOCATION"));
//    }
//    if (n.hasFeature(XgmmlReader.XML_CLASS)) N.addLabel(DynamicLabel.label("ENTITY"));
//
//    id_node.put(n.getID(), N);
//    return N;
//  }
  public static Vertex addNode(TransactionalGraph graph, PropertyNode n, Map<Long, Vertex> id_node)
  {
    if (id_node!=null && id_node.containsKey(n.getID())) return id_node.get(n.getID());

    // save the nodes and edge to the DB
    Vertex N = graph.addVertex(null);
    N.setProperty(NAME, n.getName());
    N.setProperty(ID, n.getID());

    // save the rest of the properties
    if (n!=null && n.getProperties()!=null)
      for (String key : n.getProperties().keySet())
        N.setProperty(key, n.getProperty(key));

//    // setup the labels if necessary
//    if (n.hasFeature(XgmmlReader.XML_GENE_NAME))
//    {
//      N.addLabel(DynamicLabel.label("GENE"));
//      if (n.hasFeature(XgmmlReader.XML_LOCATION)) N.addLabel(DynamicLabel.label("LOCATION"));
//    }
//    if (n.hasFeature(XgmmlReader.XML_CLASS)) N.addLabel(DynamicLabel.label("ENTITY"));

    id_node.put(n.getID(), N);
    return N;
  }
//  public static Relationship setEdge(Relationship rel, PropertyEdge e)
//  {
//    // save the nodes and edge to the DB
//    setProperty(rel, "SOURCE_ID", e.getSource());
//    setProperty(rel, "TARGET_ID", e.getTarget());
//    setProperty(rel, "ID", e.getID());
//    setProperty(rel, "LABEL", e.getLabel());
//    setProperty(rel, "SCORE", e.getScore());
//
//    // save the rest of the properties
//    for (String key : e.getFeature().getProperties().keySet())
//      setProperty(rel, key, e.getFeature().getProperty(key));
//
//    return rel;
//  }
  public static Edge addEdge(TransactionalGraph graph, Vertex A, Vertex B, String relationship, PropertyEdge e)
  {
    Edge rel = graph.addEdge(null, A, B, relationship);
    // save the nodes and edge to the DB
    set(rel, "SOURCE_ID", e.getSource());
    set(rel, "TARGET_ID", e.getTarget());
    set(rel, ID,      e.getID());
    set(rel, LABEL,   e.getLabel());
    set(rel, "SCORE",     e.getScore());

    // save the rest of the properties
    for (String key : e.getProperties().keySet())
      set(rel,  key, e.getProperty(key));

    return rel;
  }
  private static Edge set(Edge E, String key, Object val)
  {
    if (E!=null && key!=null && val!=null) E.setProperty(key, val instanceof String ? (String )val : val.toString());
    return E;
  }
  private static PropertyNode setProperty(PropertyNode N, String key, Object val)
  {
    if (N!=null && key!=null && val!=null) N.setProperty(key, val instanceof String ? (String )val : val.toString());
    return N;
  }
//  private static Relationship setProperty(Relationship rel, String key, Object val)
//  {
//    if (rel!=null && key!=null && val!=null) rel.setProperty(key,  val instanceof String ? (String )val : val.toString());
//    return rel;
//  }
//  public static ExecutionResult query(GraphDatabaseService db, String statement)
//  {
//    ExecutionEngine engine = new ExecutionEngine(db, StringLogger.SYSTEM_DEBUG);
//    ExecutionResult result = null;
//    try (Transaction ignored = db.beginTx())
//    {
//      // match (n {name: 'my node'}) return n, n.name
//      result = engine.execute(statement);
//      ignored.success();
//    }
//    return result;
//  }
//  public static IndexDefinition setDynamicIndex(GraphDatabaseService graph, String... labels)
//  {
//    IndexDefinition definition = null;
//    try (Transaction tx = graph.beginTx())
//    {
//      Schema schema = graph.schema();
//      for (String label : labels)
//      {
//        String[] parts = label.split(":");
//        definition = schema.indexFor(DynamicLabel.label(parts[0].trim())).on(parts[1].trim()).create();
//      }
//      tx.success();
//    }
//    return definition;
//  }
//  public static void awaitIndex(GraphDatabaseService graph, int seconds)
//  {
//    try (Transaction tx = graph.beginTx())
//    {
//      graph.schema().awaitIndexesOnline(seconds, TimeUnit.SECONDS);
//      tx.success();
//    }
//  }
//  public static void save(GraphDatabaseService graph, DirectedGraph<PropertyNode, PropertyEdge> G)
//  {
//    IndexDefinition index = setDynamicIndex(graph, "GENE:gene_name", "LOCATION:localization", "ENTITY:class");
//
//    // save the network to the graph
//    try (Transaction tx = graph.beginTx())
//    {
//      Map<Long, PropertyNode> id_node = new HashMap<>();
//      for (PropertyEdge E : G.edgeSet())
//      {
//        PropertyNode src = G.getEdgeSource(E), tgt = G.getEdgeTarget(E);
//        // save the nodes and edge to the DB
//        PropertyNode source = addNode(graph, src, id_node),
//            target = addNode(graph, tgt, id_node);
//        // setup the edge (relationship)
//        setEdge(source.createRelationshipTo(target, RelTypes.METABASE), E);
//      }
//      // get some basic stats about the graph we just created
//      tx.success();
//    }
//    // let's make sure the index is ready
//    awaitIndex(graph, 10);
//  }
  public static void save(TitanGraph graph, DefaultDirectedGraph<PropertyNode, PropertyEdge> G)
  {
    // save the network to the graph
    Map<Long, Vertex> id_node = new HashMap<>();
    // create the index
    TitanManagement mgmt = graph.getManagementSystem();
    PropertyKey gene     = initPropertyKey(mgmt, "gene",         String.class);
    PropertyKey loc      = initPropertyKey(mgmt, "localization", String.class);
    PropertyKey entity   = initPropertyKey(mgmt, "class",        String.class);
    if (!mgmt.containsGraphIndex("byGene"))   mgmt.buildIndex("byGene",   Vertex.class).addKey(gene  ).buildCompositeIndex();
    if (!mgmt.containsGraphIndex("byLoc"))    mgmt.buildIndex("byLoc",    Vertex.class).addKey(loc   ).buildCompositeIndex();
    if (!mgmt.containsGraphIndex("byEntity")) mgmt.buildIndex("byEntity", Vertex.class).addKey(entity).buildCompositeIndex();
    mgmt.commit();

    // transfer the nodes and edges
    for (PropertyEdge E : G.edgeSet())
    {
      PropertyNode src = G.getEdgeSource(E), tgt = G.getEdgeTarget(E);
      // save the nodes and edge to the DB
      Vertex source = addNode(graph, src, id_node),
          target = addNode(graph, tgt, id_node);
      // setup the edge, replace 'metabase' with the field about the nature of interaction
      Edge edge = addEdge(graph, source, target, "metabase", E);
    }
    // get some basic stats about the graph we just created
    graph.commit();
  }
  public static PropertyKey initPropertyKey(TitanManagement m, String key, Class type)
  {
    return m.containsPropertyKey(key) ? m.getPropertyKey(key) : m.makePropertyKey(key).dataType(type).make();
  }

  public static void clear(TitanTransaction graph)
  {
    long vertices=0, edges=0;
    for (Vertex n : graph.getVertices()) { n.remove(); vertices++; }
    for (Edge   e : graph.getEdges())    { e.remove(); edges++; }

    System.out.println("Graph cleared (vertices/edges): " + vertices+"/"+edges);
//
//    graph.commit();
  }
  public static void clear(TitanGraph graph)
  {
    for (Vertex n : graph.getVertices()) n.remove();
    for (Edge e : graph.getEdges())    e.remove();

    graph.commit();
  }
  public static String addCompositeIndex(TitanManagement m, String tag, String idx)
  {
    if (!m.containsGraphIndex(idx))
    {
      PropertyKey key = m.containsPropertyKey(tag) ? m.getPropertyKey(tag) : m.makePropertyKey(tag).dataType(String.class).make();
      m.buildIndex(idx, Vertex.class).addKey(key).buildCompositeIndex();
      m.commit();
    }

    return idx;
  }
  public static TitanVertex set(TitanVertex v, PropertyNode n, String... tags)
  {
    if (v!=null && n!=null)
      for (String tag : tags)
        if (n.hasProperty(tag))
          v.setProperty(tag, n.getProperty(tag));

    return v;
  }
  public static TitanVertex addNewProperty(TitanVertex v, String tag, String val)
  {
    if (v!=null && tag!=null && v.getProperties(tag)==null) v.setProperty(tag, val);
    return v;
  }
/*
  private static void test()
  {
    if (commentTypeSt.equals(COMMENT_TYPE_INTERACTION)) {

      List<Element> interactants = commentElem.getChildren("interactant");
      Element interactant1 = interactants.get(0);
      Element interactant2 = interactants.get(1);
      Element organismsDiffer = commentElem.getChild("organismsDiffer");
      Element experiments = commentElem.getChild("experiments");
      String intactId1St = interactant1.getAttributeValue("intactId");
      String intactId2St = interactant2.getAttributeValue("intactId");
      String organismsDifferSt = "";
      String experimentsSt = "";
      if (intactId1St == null) {
        intactId1St = "";
      }
      if (intactId2St == null) {
        intactId2St = "";
      }
      if (organismsDiffer != null) {
        organismsDifferSt = organismsDiffer.getText();
      }
      if (experiments != null) {
        experimentsSt = experiments.getText();
      }

      //----now we try to retrieve the interactant 2 accession--
      String interactant2AccessionSt = interactant2.getChildText("id");
      long protein2Id = -1;
      if (interactant2AccessionSt != null) {

        Optional<Protein<I,RV,RVT,RE,RET>> protein2Optional = graph.proteinAccessionIndex().getVertex(interactant2AccessionSt);

        if(!protein2Optional.isPresent()){

          Optional<Isoform<I,RV,RVT,RE,RET>> isoformOptional = graph.isoformIdIndex().getVertex(interactant2AccessionSt);

          if(isoformOptional.isPresent()){

            ProteinIsoformInteraction<I,RV,RVT,RE,RET> proteinIsoformInteraction = protein.addOutEdge(graph.ProteinIsoformInteraction(), isoformOptional.get());
            proteinIsoformInteraction.set(graph.ProteinIsoformInteraction().experiments, experimentsSt);
            proteinIsoformInteraction.set(graph.ProteinIsoformInteraction().organismsDiffer, organismsDifferSt);
            proteinIsoformInteraction.set(graph.ProteinIsoformInteraction().intActId1, intactId1St);
            proteinIsoformInteraction.set(graph.ProteinIsoformInteraction().intActId2, intactId2St);
          }

        }else{

          ProteinProteinInteraction<I,RV,RVT,RE,RET> proteinProteinInteraction = protein.addOutEdge(graph.ProteinProteinInteraction(), protein2Optional.get());
          proteinProteinInteraction.set(graph.ProteinProteinInteraction().experiments, experimentsSt);
          proteinProteinInteraction.set(graph.ProteinProteinInteraction().organismsDiffer, organismsDifferSt);
          proteinProteinInteraction.set(graph.ProteinProteinInteraction().intActId1, intactId1St);
          proteinProteinInteraction.set(graph.ProteinProteinInteraction().intActId2, intactId2St);
        }

      }

    }

  }

  proteinCounter++;
  if ((proteinCounter % limitForPrintingOut) == 0) {
  logger.log(Level.INFO, (proteinCounter + " proteins updated with interactions!!"));
}

}

  }
*/
//  #ID(s) interactor A	ID(s) interactor B	Alt. ID(s) interactor A	Alt. ID(s) interactor B	Alias(es) interactor A	Alias(es) interactor B	Interaction detection method(s)	Publication 1st author(s)	Publication Identifier(s)	Taxid interactor A	Taxid interactor B	Interaction type(s)	Source database(s)	Interaction identifier(s)	Confidence value(s)	Expansion method(s)	Biological role(s) interactor A	Biological role(s) interactor B	Experimental role(s) interactor A	Experimental role(s) interactor B	Type(s) interactor A	Type(s) interactor B	Xref(s) interactor A	Xref(s) interactor B	Interaction Xref(s)	Annotation(s) interactor A	Annotation(s) interactor B	Interaction annotation(s)	Host organism(s)	Interaction parameter(s)	Creation date	Update date	Checksum(s) interactor A	Checksum(s) interactor B	Interaction Checksum(s)	Negative	Feature(s) interactor A	Feature(s) interactor B	Stoichiometry(s) interactor A	Stoichiometry(s) interactor B	Identification method participant A	Identification method participant B
//  uniprotkb:P49418	uniprotkb:O43426	intact:EBI-7121510|intact:MINT-109264|uniprotkb:Q8N4G0|uniprotkb:Q75MJ8|uniprotkb:A4D1X8|uniprotkb:O43538|uniprotkb:A4D1X9|uniprotkb:Q75MM3|uniprotkb:Q75MK5	intact:EBI-2821539|uniprotkb:Q4KMR1|uniprotkb:O94984|uniprotkb:O43425	psi-mi:amph_human(display_long)|uniprotkb:AMPH(gene name)|psi-mi:AMPH(display_short)|uniprotkb:AMPH1(gene name synonym)	psi-mi:synj1_human(display_long)|uniprotkb:SYNJ1(gene name)|psi-mi:SYNJ1(display_short)|uniprotkb:KIAA0910(gene name synonym)|uniprotkb:Synaptic inositol 1,4,5-trisphosphate 5-phosphatase 1(gene name synonym)	psi-mi:"MI:0084"(phage display)	Cestra et al. (1999)	pubmed:10542231|mint:MINT-5211933	taxid:9606(human)|taxid:9606(Homo sapiens)	taxid:9606(human)|taxid:9606(Homo sapiens)	psi-mi:"MI:0407"(direct interaction)	psi-mi:"MI:0471"(MINT)	intact:EBI-7121552|mint:MINT-16056	intact-miscore:0.56	-	psi-mi:"MI:0499"(unspecified role)	psi-mi:"MI:0499"(unspecified role)	psi-mi:"MI:0498"(prey)	psi-mi:"MI:0496"(bait)	psi-mi:"MI:0326"(protein)	psi-mi:"MI:0326"(protein)	interpro:IPR027267|interpro:IPR004148(BAR)|interpro:IPR001452(Src homology-3)|interpro:IPR003017(Amphiphysin, isoform 1)|interpro:IPR003005(Amphiphysin)|rcsb pdb:1KY7|rcsb pdb:1UTC|rcsb pdb:3SOG|rcsb pdb:4ATM|go:"GO:0031256"(leading edge membrane)|go:"GO:0005543"(phospholipid binding)|go:"GO:0007612"(learning)|go:"GO:0030054"(cell junction)|go:"GO:0006897"(endocytosis)|go:"GO:0008021"(synaptic vesicle)|go:"GO:0007268"(synaptic transmission)|go:"GO:0015629"(actin cytoskeleton)|go:"GO:0048488"(synaptic vesicle endocytosis)|ensembl:ENSP00000348602|ensembl:ENSP00000317441|ensembl:ENSG00000078053|ensembl:ENST00000356264|ensembl:ENST00000325590|refseq:NP_647477.1|refseq:NP_001626.1|mint:MINT-376294(identity)	interpro:IPR002013(Synaptojanin, N-terminal)|interpro:IPR000504(RNA recognition motif, RNP-1)|interpro:IPR012677(Nucleotide-binding, alpha-beta plait)|interpro:IPR000300(Inositol polyphosphate related phosphatase)|interpro:IPR005135(Endonuclease/exonuclease/phosphatase)|interpro:IPR015047(Region of unknown function DUF1866)|rcsb pdb:2VJ0|rcsb pdb:2DNR|rcsb pdb:1W80|go:"GO:0008219"(cell death)|go:"GO:0044281"(small molecule metabolic process)|go:"GO:0006796"(phosphate-containing compound metabolic process)|go:"GO:0006644"(phospholipid metabolic process)|go:"GO:0046856"(phosphatidylinositol dephosphorylation)|go:"GO:0043647"(inositol phosphate metabolic process)|go:"GO:0006661"(phosphatidylinositol biosynthetic process)|go:"GO:0005829"(cytosol)|go:"GO:0048488"(synaptic vesicle endocytosis)|go:"GO:1903390"(positive regulation of synaptic vesicle uncoating)|go:"GO:1901632"(regulation of synaptic vesicle membrane organization)|go:"GO:0003723"(RNA binding)|go:"GO:0004439"(phosphatidylinositol-4,5-bisphosphate 5-phosphatase activity)|go:"GO:0000166"(nucleotide binding)|go:"GO:0004445"(inositol-polyphosphate 5-phosphatase activity)|ensembl:ENST00000357345|ensembl:ENSP00000349903|ensembl:ENSG00000159082|refseq:NP_982271.2|refseq:NP_003886.3|refseq:NP_001153778.1|refseq:NP_001153774.1|reactome:REACT_150352|reactome:REACT_150312|reactome:REACT_121025|mint:MINT-376287(identity)	-	function:May participate in mechanisms of regulated exocytosis in synapses and certain endocrine cell types. May control the properties of the membrane associated cytoskeleton|comment:mint|comment:homomint|function:Antibodies against AMPH are detected in patients with stiff-man syndrome, a rare disease of the central nervous system characterized by progressive rigidity of the body musculature with superimposed painful spasms|comment:"Stoichiometry: 1.0"	comment:"Stoichiometry: 1.0"	comment:homomint|comment:domino|comment:mint	taxid:-1(in vitro)|taxid:-1(In vitro)	-	2001/01/10	2014/10/16	rogid:vrgVrVoYr45cUe4X6L/zBAE1RtU9606	rogid:RA73eMbCn6F7MD0ItxF/V7QbjqM9606	intact-crc:F4234557A3B54840|rigid:n+UcEH4PPLkFnIyvBiXrLefK/xU	false	binding-associated region:626-695(MINT-376295)	binding-associated region:1063-1070(MINT-376288)	-	-	psi-mi:"MI:0078"(nucleotide sequence identification)	psi-mi:"MI:0078"(nucleotide sequence identification)
//  uniprotkb:P49418	intact:EBI-7121639	intact:EBI-7121510|intact:MINT-109264|uniprotkb:Q8N4G0|uniprotkb:Q75MJ8|uniprotkb:A4D1X8|uniprotkb:O43538|uniprotkb:A4D1X9|uniprotkb:Q75MM3|uniprotkb:Q75MK5	intact:MINT-8094608	psi-mi:amph_human(display_long)|uniprotkb:AMPH(gene name)|psi-mi:AMPH(display_short)|uniprotkb:AMPH1(gene name synonym)	psi-mi:vrparrvlw(display_short)|psi-mi:EBI-7121639(display_long)	psi-mi:"MI:0084"(phage display)	Cestra et al. (1999)	pubmed:10542231|mint:MINT-5211933	taxid:9606(human)|taxid:9606(Homo sapiens)	taxid:-2(chemical synthesis)|taxid:-2("Chemical synthesis (Chemical synthesis)")	psi-mi:"MI:0407"(direct interaction)	psi-mi:"MI:0471"(MINT)	intact:EBI-7121634|mint:MINT-8094596	intact-miscore:0.44	-	psi-mi:"MI:0499"(unspecified role)	psi-mi:"MI:0499"(unspecified role)	psi-mi:"MI:0498"(prey)	psi-mi:"MI:0496"(bait)	psi-mi:"MI:0326"(protein)	psi-mi:"MI:0327"(peptide)	interpro:IPR027267|interpro:IPR004148(BAR)|interpro:IPR001452(Src homology-3)|interpro:IPR003017(Amphiphysin, isoform 1)|interpro:IPR003005(Amphiphysin)|rcsb pdb:1KY7|rcsb pdb:1UTC|rcsb pdb:3SOG|rcsb pdb:4ATM|go:"GO:0031256"(leading edge membrane)|go:"GO:0005543"(phospholipid binding)|go:"GO:0007612"(learning)|go:"GO:0030054"(cell junction)|go:"GO:0006897"(endocytosis)|go:"GO:0008021"(synaptic vesicle)|go:"GO:0007268"(synaptic transmission)|go:"GO:0015629"(actin cytoskeleton)|go:"GO:0048488"(synaptic vesicle endocytosis)|ensembl:ENSP00000348602|ensembl:ENSP00000317441|ensembl:ENSG00000078053|ensembl:ENST00000356264|ensembl:ENST00000325590|refseq:NP_647477.1|refseq:NP_001626.1|mint:MINT-8094601(identity)	mint:MINT-8094610(identity)	-	function:May participate in mechanisms of regulated exocytosis in synapses and certain endocrine cell types. May control the properties of the membrane associated cytoskeleton|comment:mint|comment:homomint|function:Antibodies against AMPH are detected in patients with stiff-man syndrome, a rare disease of the central nervous system characterized by progressive rigidity of the body musculature with superimposed painful spasms	comment:mint|no-uniprot-update:	figure legend:F1 F2|comment:domino|comment:mint	taxid:-1(in vitro)|taxid:-1(In vitro)	-	2001/01/10	2014/10/16	rogid:vrgVrVoYr45cUe4X6L/zBAE1RtU9606	rogid:lPy6gBhpgvyGSYgOqeHbjcqBtMQ-2	intact-crc:880913D9000BF26E|rigid:iRObbQsaUeX0PxuKeQwZ73s8iKU	false	binding-associated region:626-695(MINT-8094602)	-	-	-	psi-mi:"MI:0078"(nucleotide sequence identification)	psi-mi:"MI:0083"(peptide synthesis)

/*
   1. Unique identifier for interactor A, represented as databaseName:ac, where databaseName is the name of the corresponding
      database as defined in the PSI-MI controlled vocabulary, and ac is the unique primary identifier of the molecule in the
      database. Identifiers from multiple databases can be separated by "|". It is recommended that proteins be identified by
      stable identifiers such as their UniProtKB or RefSeq accession number.
   2. Unique identifier for interactor B.
   3. Alternative identifier for interactor A, for example the official gene symbol as defined by a recognised nomenclature
      committee. Representation as databaseName:identifier. Multiple identifiers separated by "|".
   4. Alternative identifier for interactor B.
   5. Aliases for A, separated by "|". Representation as databaseName:identifier. Multiple identifiers separated by "|".
   6. Aliases for B.
   7. Interaction detection methods, taken from the corresponding PSI-MI controlled Vocabulary, and represented as
      darabaseName:identifier(methodName), separated by "|".
   8. First author surname(s) of the publication(s) in which this interaction has been shown, optionally followed by
      additional indicators, e.g. "Doe-2005-a". Separated by "|".
   9. Identifier of the publication in which this interaction has been shown. Database name taken from the PSI-MI controlled
      vocabulary, represented as databaseName:identifier. Multiple identifiers separated by "|".
  10. NCBI Taxonomy identifier for interactor A. Database name for NCBI taxid taken from the PSI-MI controlled vocabulary,
      represented as databaseName:identifier (typicaly databaseName is set to 'taxid'). Multiple identifiers separated by "|".
      Note: In this column, the databaseName:identifier(speciesName) notation is only there for consistency. Currently no
      taxonomy identifiers other than NCBI taxid are anticipated, apart from the use of -1 to indicate "in vitro", -2 to
      indicate "chemical synthesis", -3 indicates "unknown", -4 indicates "in vivo" and -5 indicates "in silico".
  11. NCBI Taxonomy identifier for interactor B.
  12. Interaction types, taken from the corresponding PSI-MI controlled vocabulary, and represented as dataBaseName:identifier
      (interactionType), separated by "|".
  13. Source databases and identifiers, taken from the corresponding PSI-MI controlled vocabulary, and represented as
      databaseName:identifier(sourceName). Multiple source databases can be separated by "|".
  14. Interaction identifier(s) in the corresponding source database, represented by databaseName:identifier
  15. Confidence score. Denoted as scoreType:value. There are many different types of confidence score, but so far no
      controlled vocabulary. Thus the only current recommendation is to use score types consistently within one source.
      Multiple scores separated by "|".
*/
  public static DefaultDirectedGraph<PropertyNode, PropertyEdge> readPsiMI(
                DefaultDirectedGraph<PropertyNode, PropertyEdge> graph,
                Map<String, PropertyNode> tag_node, EntrySet es)
  {
    for (Entry E : es.getEntries())
    {
      // locate the existing node
      Multimap<Integer, PropertyNode> id_nodes = HashMultimap.create();
      for (Interactor R : E.getInteractors())
        id_nodes.putAll(R.getId(), getNodes(tag_node, R));
      // check the interactions
      for (Interaction R : E.getInteractions())
      {
        if (R.getParticipants().size()!=2) throw new RuntimeException("Participants!=2: " + R.getParticipants().size());
        // build the edge
        PropertyEdge edge = new PropertyEdge();

        edge.setProperty("experiment", toString(R.getExperiments()));
        edge.setProperty("name", R.getNames().getShortLabel()+": " + R.getNames().getFullName());
        edge.setProperty("type", toString(R.getInteractionTypes()));

        Integer id1=Tools.front(R.getParticipants()).getId(), id2=Tools.back(R.getParticipants()).getId();
        //
        for (  PropertyNode n1 : id_nodes.get(id1))
          for (PropertyNode n2 : id_nodes.get(id2))
          {
            graph.addEdge(n1, n2, edge);
          }
      }
    }
    return graph;
  }
/*
  public static String toString(Collection<ExperimentDescription> expts)
  {
    String line = null;
    for (ExperimentDescription E : expts)
      line = Strs.extend(line, E.getNames().getShortLabel(), "|");

    return line;
  }
*/
  public static <N extends NamesContainer> String toString(Collection<N> types)
  {
    String line = null;
    for (N E : types)
      line = Strs.extend(line, E.getNames().getShortLabel(), "|");

    return line;
  }
  public static Collection<PropertyNode> getNodes(Map<String, PropertyNode> tag_node, Interactor A)
  {
    if (tag_node!=null && A!=null)
    {
      Collection<PropertyNode> nodes = new HashSet<>();
      // check the primary IDs
      for (Alias x : A.getNames().getAliases())
        if (Strs.isA(x.getType(), "gene name","gene name synonym"))
          Tools.add(nodes, tag_node.get(x.getValue()));
      // check the other IDs as well
      for (DbReference x : A.getXref().getAllDbReferences())
      {
        if (Strs.isA(x.getDb(), "uniprotkb","refseq"))
          Tools.add(nodes, tag_node.get(x.getId()));
      }
      return nodes;
    }
    return null;
  }
  public static Map<String, PropertyNode> getVertices(TitanTransaction tx, String label)
  {
    Map<String, PropertyNode> tag_accession = new HashMap<>();
    for (Vertex v : tx.getVertices("_label_", "protein"))
    {
      PropertyNode node = new PropertyNode();
      node.setProperty("accession", "");
    }
    return tag_accession;
  }
//  public static <V, E, G extends UndirectedGraph<V, E>> Collection<Subgraph<V,E,G>> decompose(G graph, int min)
//  {
//    ConnectivityInspector<V, E> inspector = new ConnectivityInspector<V, E>(graph);
//    List<Set<V>>                connected = inspector.connectedSets();
//
//    Collection<Subgraph<V,E,G>> components = new ArrayList<>();
//    for (Set<V> set : connected)
//      if (set.size()>=min) components.add(new Subgraph(graph, set));
//
//    return components;
//  }
//  public static <V, E, G extends DirectedGraph<V, E>> Collection<Subgraph<V,E,G>>
//  decompose_directed(G graph, int min)
//  {
//    ConnectivityInspector<V, E> inspector = new ConnectivityInspector<V, E>(graph);
//    List<Set<V>>                connected = inspector.connectedSets();
//
//    Collection<Subgraph<V,E,G>> components = new ArrayList<>();
//    for (Set<V> set : connected)
//      if (set.size()>=min) components.add(new Subgraph(graph, set));
//
//    return components;
//  }
  public static void dispose(DefaultDirectedGraph g)
  {
    if (g!=null)
    {
//      g.vertexSet().clear();
//      g.edgeSet(  ).clear();
      g=null;
    }
  }
}
