package org.ms2ms.nosql;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: wyu
 * Date: 6/1/14
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
abstract public class Titans
{
  public static TitanGraph openHBaseGraph()
  {
    // start the HBase first: /usr/local/hbase/4titan/bin$ ./start-hbase.sh
    Configuration conf = new BaseConfiguration();
    conf.setProperty("storage.backend","hbase");
    conf.setProperty("storage.directory","/media/data/titan");
    conf.setProperty("cache.db-cache",true);
    conf.setProperty("cache.db-cache-clean-wait", 20);
    conf.setProperty("cache.db-cache-time", 180000);
    conf.setProperty("cache.db-cache-size", 0.5);
    conf.setProperty("index.search.backend", "elasticsearch");
    conf.setProperty("index.search.hostname", "127.0.0.1");
    conf.setProperty("index.search.client-only", true);

//  # Activate the interface track with ES's transport client
    conf.setProperty("index.search.elasticsearch.interface", "TRANSPORT_CLIENT");


//  The Elasticsearch node.client option is set to this boolean value, and the Elasticsearch node.data option is set
//  to the negation of this value. True creates a thin client which holds no data.  False creates a regular
//  Elasticsearch cluster node that may store data.
    conf.setProperty("index.search.elasticsearch.client-only", false);
    conf.setProperty("storage.index.search.local-mode", true);

//    conf.setProperty("schema.default","none"); // to ensure data integrity during batch loading
    conf.setProperty("ids.block-size",100000);
    conf.setProperty("storage.buffer-size", 10240); // need careful experimentation during batch loading
    conf.setProperty("batch-loading", true);

/*  Settings from bIO4J-Titan
    Configuration conf = new BaseConfiguration();
    conf.setProperty("storage.directory", dbFolder);
    conf.setProperty("storage.backend", "berkeleyje"); // why BerkeleyDB?
    conf.setProperty("query.force-index", "true");
    conf.setProperty("autotype", "none");
*/
/*
    Q. Titan 0.5 does not work with storage.batch-loading=true ?
    https://groups.google.com/forum/#!msg/aureliusgraphs/CXym3kCGjUE/KJhHaqlxShsJ

    We won't allow schema changes (which includes creating edge labels, etc) during batch-loading transactions because
    those disable locking which can cause schema inconsistencies. That's been the case in Titan 0.4.x as well but is
    more strictly enforced now.

    Hence, either
    1) create your schema up front
    2) use separate transaction that don't have batch-loading enabled to create the schema on the fly (use the
       transaction builder with buildTransaction() on graph)

    In general, give us one more week to wrap up the M1 release of 0.5 - there are still some loose ends we are patching up.

    Matthias
*/

    return TitanFactory.open(conf);
  }
  public static TitanGraph openGraph(String cfg)
  {
    // graph.properties. possibility for multiple graphs
    return TitanFactory.open(cfg);
  }
  public static void addPeptides()
  {
//    GeneNode geneNode = new GeneNode(manager.createNode(GeneNode.NODE_TYPE));
//    geneNode.setPositions(genePositionsSt);
//    graph.addEdge(null, genomeElementNode.getNode(), geneNode.getNode(), GenomeElementGeneRel.NAME);

  }
}
