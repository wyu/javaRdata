package org.ms2ms.test.graphdb;

import ai.grakn.Grakn;
import ai.grakn.GraknTx;
import ai.grakn.GraknTxType;
import ai.grakn.graql.InsertQuery;
import org.junit.Before;
import org.junit.Test;
import org.ms2ms.test.TestAbstract;

import ai.grakn.client.BatchExecutorClient;

import java.io.IOException;

public class TestGrakn extends TestAbstract
{
  @Before
  public void setUp() throws IOException
  {
  }

  @Test
  public void simpleSchema() throws Exception
  {
//    GraknTx tx = Grakn.session(Grakn.IN_MEMORY, "myknowlegdebase").open(GraknTxType.WRITE);

  }
  @Test
  public void hello() throws IOException
  {
//    BatchExecutorClient loader = BatchExecutorClient.newBuilderforURI(uri).build();
//    InsertQuery insert = insert(var().isa("person"));
//
//    for(int i = 0; i < 100; i++){
//      loader.add(insert, keyspace).subscribe({System.out.println(it)});
//    }
//
//    loader.close();
  }
}
