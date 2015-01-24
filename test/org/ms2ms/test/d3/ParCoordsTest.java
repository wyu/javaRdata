package org.ms2ms.test.d3;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.Test;
import org.ms2ms.data.collect.TreeNode;
import org.ms2ms.graph.PropertyEdge;
import org.ms2ms.graph.PropertyNode;
import org.ms2ms.r.Dataframe;
import org.ms2ms.r.Var;
import org.ms2ms.test.TestAbstract;
import org.ms2ms.utils.IOs;
import org.ms2ms.utils.Strs;
import org.ms2ms.utils.Tools;

import java.util.*;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   1/13/15
 */
public class ParCoordsTest extends TestAbstract
{
  @Test
  public void prepareTSNECSV() throws Exception
  {
    Dataframe dat = new Dataframe("/media/data/test/data/clinical_i2b2trans_adult.txt", '\t').setNAs("NA","null").init();

    // {cohort_v=10, cohort_c=88, cohort_b=110, cohort_d=101, cohort_a=311}
    String cohort="\\Study Groups\\cohort", ctrl_level="cohort_a";

    // finding the useful and well-populated variables
    List<String> vars = dat.getColByPopulation(600);
    vars.add(cohort);

    // divide the rows into control and study populations
    Dataframe study=dat.subcol(vars.toArray(new String[] {}));
    // normalize them by the control population
    for (String col : study.cols())
    {
      String[] items = Strs.split(col, '\\'); String c=items[items.length-1];
      study.renameCol(col, items[items.length-1]);
    }

    IOs.write("/tmp/adult6.csv", study.removeRowsWithMissingValue().csv(2));
  }

  @Test
  public void prepareParCoordsCSV() throws Exception
  {
    // {cohort_v=10, cohort_c=88, cohort_b=110, cohort_d=101, cohort_a=311}
    String cohort="\\Study Groups\\cohort", ctrl_level="cohort_a";

    Dataframe dat = new Dataframe("/media/data/test/data/clinical_i2b2trans_adult.txt", '\t').setNAs("NA","null").init();
    String[] header5 = {
        "\\Study Groups\\cohort",
        "\\Clinical Data\\Lung Biopsy Immunopathology\\Broncoscopy Visit\\Submucosa\\CD4 T cells (cells/mm^2)",
        "\\Clinical Data\\Haematology and biochemistry tests\\Screening\\eosinophils (x10^3/uL)",
        "\\Biomarker Data\\Baseline Visit\\Serum\\Genentech Periostin Assay\\Periostin (ng/mL)",
        "\\Clinical Data\\Haematology and biochemistry tests\\Screening\\Blood Urea Nitrogen (mg/dL)",
        "\\Clinical Data\\Haematology and biochemistry tests\\Screening\\Eosinophils Pct"};

    // finding the useful and well-populated variables
    List<String> vars = dat.getColByPopulation(600);
    TreeNode top = new TreeNode("root");
    for (String col : dat.cols())
    {
//      Var v = dat.asVar(col);
//      if (v!=null && v.isContinuous() && v.getNumEntries()>600) vars.add(col);
      top = parseTransmartHeader(top, col, '\\', dat.asVar(col));
    }
    vars.add(cohort);
//    String[] headers = vars.toArray(new String[] {});
    String[] headers = header5;

    // divide the rows into control and study populations
    Dataframe ctrl=dat.subset(cohort+"=="+ctrl_level), study=dat.subset(cohort + "!=" + ctrl_level), output=new Dataframe();
    // normalize them by the control population
    for (String col : headers)
    {
      Var C = ctrl.asVar(col);
      String[] items = Strs.split(col, '\\'); String c=items[items.length-1];
      for (String row : study.rows())
      {
        if (study.cell(row, col) instanceof Double)
        {
          Double val = (Double )study.cell(row, col);
          if (val!=null)
          {
            //val = (val-C.getDistribution().getNumericalMean()) / Math.sqrt(C.getDistribution().getNumericalVariance());
            output.put(row, c, val);
          }
        }
        else output.put(row, c, study.cell(row, col));
      }
    }
    output.init().removeRowsWithMissingValue();
/*
    // remove the row with missing value
    List<String> missing = new ArrayList<>();
    for (String row : output.rows())
    {
      if (output.row(row).values().size()<output.cols().size()) missing.add(row);
    }
    if (Tools.isSet(missing)) output.removeRows(missing.toArray(new String[] {}));

*/
    IOs.write("/tmp/adult4.csv", output.csv(2));
  }

  @Test
  public void examineAdultCSV() throws Exception
  {
    Dataframe dat = new Dataframe("/media/data/test/data/clinical_i2b2trans_adult.txt", '\t').setNAs("NA","null").init();

    TreeNode top = new TreeNode("root");
    System.out.println("entries\tcat\tfactor\tcolumn header");
    for (String col : dat.cols())
    {
      Var v = dat.asVar(col);
      if (v!=null && v.isContinuous() && v.getNumEntries()>500)
      {
        System.out.println(v.getNumEntries() + "\t" + v.isCategorical() + "\t" + v.getNumFactors() + "\t" + col);
        top = parseTransmartHeader(top, col, '\\', v);
      }
    }
    System.out.println(dat.rows().size()+"rows.");
  }
  /* \Exacerbations\Screening\Event 6\Addition of LABA or other long acting bronchodilators=\Exacerbations\Screening\Event 6\Addition of LABA or other long acting bronchodilators */
  private static DirectedGraph<PropertyNode, PropertyEdge> parseTransmartHeader(
                 DirectedGraph<PropertyNode, PropertyEdge> g, Map<String, PropertyNode> nodes, String col, char delimiter)
  {
    if (g==null || !Strs.isSet(col)) return g;

    String[]       cols = Strs.split(col, delimiter);
    PropertyNode parent = null;
    // everything up to the last one are the scope
    for (int i=0; i<cols.length-1; i++)
    {
      if (!nodes.containsKey(cols[i]))
      {
        nodes.put(cols[i], new PropertyNode(cols[i]));
        g.addVertex(nodes.get(cols[i]));
      }
      // setup the relationship if parent exist
      if (parent!=null && !g.containsEdge(nodes.get(cols[i]), parent)) g.addEdge(nodes.get(cols[i]), parent);
      // pass it down the chain
      parent = nodes.get(cols[i]);
    }
    return g;
  }

  private static TreeNode parseTransmartHeader(TreeNode root, String col, char delimiter, Object data)
  {
    if (root==null || !Strs.isSet(col)) return root;

    String[] cols = Strs.split(col, delimiter);
    TreeNode top = root;
    for (int i=0; i<cols.length; i++)
    {
      if (!Strs.isSet(cols[i])) continue;
      // add the child
      top.addChild(new TreeNode(cols[i]));
      top=top.getChild(cols[i]);
      if (i==cols.length-1) top.setData(data);
    }
    return root;
  }
}
