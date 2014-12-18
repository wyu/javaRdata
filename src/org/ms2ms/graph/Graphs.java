package org.ms2ms.graph;

import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

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
  public static final String NAME  = "_NAME_";
  public static final String ID    = "_ID_";
  public static final String LABEL = "_LABEL_";

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
  public static Collection<List<PropertyNode>> simplePaths(DirectedGraph<PropertyNode, PropertyEdge> graph,
                                                           PropertyNode START, PropertyNode END, int max)
  {
    Collection<List<PropertyNode>> paths = new ArrayList<List<PropertyNode>>();
    LinkedList<PropertyNode> visited = new LinkedList<PropertyNode>();
    Set<PropertyNode> cache = new HashSet<PropertyNode>();
    visited.add(START); cache.add(START);
    max = breadthFirst(graph, visited, cache, END, paths, max);

    return paths;
  }
  public static int breadthFirst(DirectedGraph<PropertyNode, PropertyEdge> graph,
                                 LinkedList<PropertyNode> visited, Set<PropertyNode> cache,
                                 PropertyNode END, Collection<List<PropertyNode>> paths, int max)
  {
    // limit the depth of the search away from the starting node
//    if (max<=0) return max;

    LinkedList<PropertyNode> nodes = new LinkedList<PropertyNode>();
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
  public static void save(TitanGraph graph, DirectedGraph<PropertyNode, PropertyEdge> G)
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
  public static void clear(TitanGraph graph)
  {
    for (Vertex n : graph.getVertices()) n.remove();
    for (Edge e : graph.getEdges())    e.remove();

    graph.commit();
  }
}
