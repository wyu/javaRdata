package org.ms2ms.test.d3;

import org.junit.Test;
import org.ms2ms.data.collect.TreeNode;
import org.ms2ms.math.Stats;
import org.ms2ms.r.Dataframe;
import org.ms2ms.r.Var;
import org.ms2ms.test.TestAbstract;
import org.ms2ms.utils.IOs;
import org.ms2ms.utils.Strs;

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
    Dataframe dat = new Dataframe("/media/data/test/data/clinical_i2b2trans_adult.txt", '\t', 0).setNAs("NA","null").init(true),
            abbrs = new Dataframe("/media/data/test/data/clinical.abbr", '\t', 0);

    Collection<String> columns = dat.cols();
    for (String col : columns)
    {
      String column = col;
      for (String abbr : abbrs.rows())
      {
        String snip = abbrs.cell(abbr, "snip").toString(), ab = abbrs.cell(abbr, "abbr").toString();
        if (snip.indexOf("x10^3_/uL")>0 && column.indexOf("x10^3_/uL")>0)
        {
          System.out.println();
        }
        column = column.replace(snip, ab);
      }
      column = column.replaceAll("\\s", "_");
      if (!Strs.equals(col, column)) dat.renameCol(col, column);
    }
    // {cohort_v=10, cohort_c=88, cohort_b=110, cohort_d=101, cohort_a=311}
    String cohort="\\Study Groups\\cohort", ctrl_level="cohort_a";

    // finding the useful and well-populated variables
    List<String> vars = dat.getColByPopulation(100);
//    vars.add(cohort);

    Collections.sort(vars);
    for (String col : vars)
    {
      if (col.indexOf('(')>0 && col.indexOf(')')>0) System.out.println(col);
    }

    // divide the rows into control and study populations
    Dataframe study=dat.subcol(vars.toArray(new String[] {}));
    // normalize them by the control population
//    for (String col : study.cols())
//    {
//      String[] items = Strs.split(col, '\\'); String c=items[items.length-1];
//      study.renameCol(col, items[items.length-1]);
//    }

    IOs.write("/tmp/adult100.tsv", study.tsv(2));
  }
  @Test
  public void prepareParCoordsCSV() throws Exception
  {
    // {cohort_v=10, cohort_c=88, cohort_b=110, cohort_d=101, cohort_a=311}
    String cohort="\\Study Groups\\cohort", ctrl_level="D";

    Dataframe dat = new Dataframe("/media/data/test/data/clinical_i2b2trans_adult.txt", '\t', 0).setNAs("NA","null").init(true);

    dat.replaceValue(cohort, Var.VarType.CATEGORICAL, "cohort_a", "A");
    dat.replaceValue(cohort, Var.VarType.CATEGORICAL, "cohort_b", "B");
    dat.replaceValue(cohort, Var.VarType.CATEGORICAL, "cohort_c", "C");
    dat.replaceValue(cohort, Var.VarType.CATEGORICAL, "cohort_d", "D");
    dat.replaceValue(cohort, Var.VarType.CATEGORICAL, "cohort_v", "V");

    // finding the useful and well-populated variables
    List<String> vars = dat.getColByPopulation(600);
    vars.add(cohort);
    String[] headers = vars.toArray(new String[] {});

    // divide the rows into control and study populations
    Dataframe ctrl=dat.subset(cohort+"=="+ctrl_level), study=dat.subset(cohort + "!=" + ctrl_level), output=new Dataframe();
    // normalize them by the control population
    for (String col : headers)
    {
      Var C = ctrl.asVar(col);
      String[] items = Strs.split(col, '\\'); C.setName(items[items.length-1]);
//      // mention the transformation
//      if (C.getDistribution()!=null && C.getDistribution().getTransformer()!=null &&
//         !Tools.equals(C.getDistribution().getTransformer(), Transformer.processor.none))
//        c = C.getDistribution().getTransformer().name() + "("+c+")";

      for (String row :dat.rows())
      {
        if (dat.cell(row, col) instanceof Double)
        {
          Double val = (Double )dat.cell(row, col);
          if (val!=null)
          {
            // transform the value if suggested by the Var
            val = Stats.transform(val, C.getDistribution().getTransformer());
            val = (val-C.getDistribution().getMean()) / C.getDistribution().getStdev();
            output.put(row, C.getTitle(), val);
          }
        }
        else output.put(row, C.getTitle(), dat.cell(row, col));
      }
    }
    output.init(true).removeRowsWithMissingValue(0);
/*
    // remove the row with missing value
    List<String> missing = new ArrayList<>();
    for (String row : output.rows())
    {
      if (output.row(row).values().size()<output.cols().size()) missing.add(row);
    }
    if (Tools.isSet(missing)) output.removeRows(missing.toArray(new String[] {}));

*/
//    output = Dataframe.bundling(output);
    IOs.write("/tmp/adult4.csv", output.csv(2));
  }

  @Test
  public void prepareSelectedParCoordsCSV() throws Exception
  {
    // {cohort_v=10, cohort_c=88, cohort_b=110, cohort_d=101, cohort_a=311}
    String cohort="\\Study Groups\\cohort", ctrl_level="cohort_a";

    Dataframe dat = new Dataframe("/media/data/test/data/clinical_i2b2trans_adult.txt", '\t', 0).setNAs("NA","null").init(true);
    String[] header5 = {
        "\\Study Groups\\cohort",
        "\\Clinical Data\\Lung Biopsy Immunopathology\\Broncoscopy Visit\\Submucosa\\CD4 T cells (cells/mm^2)",
        "\\Clinical Data\\Haematology and biochemistry tests\\Screening\\eosinophils (x10^3/uL)",
        "\\Biomarker Data\\Baseline Visit\\Serum\\Genentech Periostin Assay\\Periostin (ng/mL)",
        "\\Clinical Data\\Haematology and biochemistry tests\\Screening\\Blood Urea Nitrogen (mg/dL)",
        "\\Clinical Data\\Haematology and biochemistry tests\\Screening\\Eosinophils Pct"};

    dat.replaceValue(header5[0], Var.VarType.CATEGORICAL, "cohort_a", "A");
    dat.replaceValue(header5[0], Var.VarType.CATEGORICAL, "cohort_b", "B");
    dat.replaceValue(header5[0], Var.VarType.CATEGORICAL, "cohort_c", "C");
    dat.replaceValue(header5[0], Var.VarType.CATEGORICAL, "cohort_d", "D");
    dat.replaceValue(header5[0], Var.VarType.CATEGORICAL, "cohort_v", "V");

    // finding the useful and well-populated variables
    List<String> vars = dat.getColByPopulation(600);
    TreeNode top = new TreeNode("root");
//    for (String col : dat.cols())
    for (String col : header5)
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
      String[] items = Strs.split(col, '\\'); C.setName(items[items.length-1]);
//      // mention the transformation
//      if (C.getDistribution()!=null && C.getDistribution().getTransformer()!=null &&
//         !Tools.equals(C.getDistribution().getTransformer(), Transformer.processor.none))
//        c = C.getDistribution().getTransformer().name() + "("+c+")";

      for (String row : study.rows())
      {
        if (study.cell(row, col) instanceof Double)
        {
          Double val = (Double )study.cell(row, col);
          if (val!=null)
          {
            // transform the value if suggested by the Var
            val = Stats.transform(val, C.getDistribution().getTransformer());
            val = (val-C.getDistribution().getMean()) / C.getDistribution().getStdev();
            output.put(row, C.getTitle(), val);
          }
        }
        else output.put(row, C.getTitle(), study.cell(row, col));
      }
    }
    output.init(true).removeRowsWithMissingValue(0);
/*
    // remove the row with missing value
    List<String> missing = new ArrayList<>();
    for (String row : output.rows())
    {
      if (output.row(row).values().size()<output.cols().size()) missing.add(row);
    }
    if (Tools.isSet(missing)) output.removeRows(missing.toArray(new String[] {}));

*/
//    output = Dataframe.bundling(output);
    IOs.write("/tmp/adult4.csv", output.csv(2));
  }

  @Test
  public void examineAdultCSV() throws Exception
  {
    Dataframe dat = new Dataframe("/media/data/test/data/clinical_i2b2trans_adult.txt", '\t', 0).setNAs("NA","null").init(true);

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
/*
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
*/

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
