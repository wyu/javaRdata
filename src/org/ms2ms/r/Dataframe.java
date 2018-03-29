package org.ms2ms.r;

//import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
//import com.apporiented.algorithm.clustering.Cluster;
//import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
//import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.google.common.collect.*;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.ms2ms.Disposable;
import org.ms2ms.data.NameValue;
import org.ms2ms.data.collect.MapOfMultimap;
import org.ms2ms.data.collect.MultiTreeTable;
import org.ms2ms.math.Stats;
import org.ms2ms.math.clustering.ParCoodsClusterable;
import org.ms2ms.math.clustering.SlopeConvergenceDistance;
import org.ms2ms.utils.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wyu
 * Date: 7/13/14
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Dataframe implements Disposable
{
  private String                     mTitle, mCurrentRow="unTitled";
  private boolean                    mKeepData = true;
  private List<String>               mRowIDs, mColIDs;
  private Map<String, Var>           mNameVar;
  private Table<String, String, Object> mData;
//  private String[]                   mNAs;

  public Dataframe()                                         { super(); }
  public Dataframe(String s)                                 { super(); setTitle(s); }
  public Dataframe(String cvs, char delim, String... idcols) { super(); readTable(cvs, null, delim, idcols); setTitle(cvs); }
  public Dataframe(String cvs, String[] selected_cols, char delim, String... idcols)
  { super(); readTable(cvs, selected_cols, delim, idcols); setTitle(cvs); }

  public Dataframe setRowIds(List<String> s) { mRowIDs=s; return this; }
  public Dataframe setColIds(List<String> s) { mColIDs=s; return this; }

  //** factory method **//
//  public static Dataframe csv(String csv, char delim, String... idcols)
//  {
//    Dataframe data = new Dataframe();
//    data.readTable(csv, null, delim, idcols); data.setTitle(csv);
//    return data;
//  }
//  public static Dataframe csv(String csv, String[] selected_cols, char delim, String... idcols)
//  {
//    Dataframe data = new Dataframe();
//    data.readTable(csv, selected_cols, delim, idcols); data.setTitle(csv);
//    return data;
//  }

  //** Getters the Setters **//
  public Table<String, String, Object> data() { return mData; }
  public int size() { return mData!=null?mData.rowKeySet().size():0; }
  public String       getTitle()      { return mTitle; }
  public String       getRowId(int i) { return mRowIDs!=null?mRowIDs.get(i):null; }
  public Var[]        asVars(String... s)
  {
    if (!hasVars(s)) return null;
    Var[] out = new Var[s.length];
    for (int i=0; i<s.length; i++) out[i] = asVar(s[i]);

    return out;
  }
  // set the String that shall be mapped to NA
  public Dataframe setNAs(String... s)
  {
    if (Tools.isSet(s) && Tools.isSet(mData))
      for (String col : cols())
      {
        for (String row : rows())
        {
          Object val = cell(row, col);
          // remove the cell if the value is null
          if (val!=null && val instanceof String && Strs.isA((String )val, s)) mData.remove(row, col);
        }
        Var C = asVar(col);
        if (C!=null)
          for (String n : s) if (C.getFactors()!=null) C.getFactors().remove(n);
      }

    return this;
  }
  // recognize the situations where boolean values are appropriate
  public Dataframe setBooleans(Object TRUE, Object FALSE)
  {
    if (TRUE!=null && FALSE!=null && Tools.isSet(mData))
    {
      for (String col : cols())
      {
        Var C = asVar(col);
        if (C!=null && C.getNumFactors()==2 && C.getFactors().contains(TRUE) && C.getFactors().contains(FALSE))
        {
          replaceValue(col, Var.VarType.BOOLEAN, TRUE,  true);
          replaceValue(col, Var.VarType.BOOLEAN, FALSE, false);
//          for (String row : rows())
//          {
//            Object val = cell(row, col);
//            if (val!=null && val.getClass().equals(TRUE.getClass()))
//            {
//              // remove the cell and replace it with 'true' or false
//              if      (Tools.equals(TRUE,  val)) { mData.remove(row, col); mData.put(row, col, true); }
//              else if (Tools.equals(FALSE, val)) { mData.remove(row, col); mData.put(row, col, false); }
//            }
//          }
//          // update the Var
//          C.setType(Var.VarType.BOOLEAN);
//          C.renameFactor(TRUE, true).renameFactor(FALSE, false);
        }
      }
    }

    return this;
  }
  public Dataframe replaceValue(String col, Var.VarType toType, Object from, Object to)
  {
    Collection<String> cols = col==null?cols():Arrays.asList(col);
    for (String c : cols)
    {
      Var C = asVar(c);
      if (C!=null && C.getFactors().contains(from))
      {
        for (String row : rows())
        {
          Object val = cell(row, c);
          if (val!=null && val.getClass().equals(from.getClass()))
          {
            // remove the cell and replace it with 'true' or false
            if      (Tools.equals(from,  val)) { mData.remove(row, c); mData.put(row, c, to); }
          }
        }
        // update the Var
        C.setType(toType);
        C.renameFactor(from, to);
      }
    }
    return this;
  }
  public Dataframe setTitle(String s) { mTitle=s; return this; }

  public Map<String, Object> row(int    i) { return mData!=null?mData.row(getRowId(i)):null; }
  public Map<String, Object> row(String s) { return mData!=null?mData.row(s):null; }
  public Map<String, Object> col(String s) { return mData!=null?mData.column(s):null; }
  public Var asVar(String s)
  {
    if (mNameVar==null)
    {
      mNameVar= new HashMap<>();
      if (Tools.isSet(mColIDs))
        for (String v : mColIDs) mNameVar.put(v, new Variable(v));
    }
    return mNameVar.get(s);
  }
  public Dataframe put(String col, Object val) { return put(mCurrentRow, col, val); }
  public Dataframe put(int row, String col, Object val) { return put(row+"", col, val); }
  public Dataframe put(String row, String col, Object val)
  {
    if (row!=null && col!=null && val!=null)
    {
      mCurrentRow=row;
      if (mData  ==null) mData = HashBasedTable.create();
      mData.put(row, col, val);
    }

    return this;
  }
  public Dataframe put(int row, TabFile tabs, String... cols)
  {
    if (Tools.isSet(cols))
      for (String col : cols)
        put(row, col, tabs.get(col));

    return this;
  }
  public Dataframe put(String row, String col, Double val, int decimal)
  {
    if (val!=null && !val.isNaN() && !val.isInfinite() && Strs.isSet(col)) put(row, col, Tools.d2s(val, decimal));
    return this;
  }
  public Dataframe addRowId(String row)
  {
    if (mRowIDs==null) mRowIDs = new ArrayList<>();
    mRowIDs.add(row); return this;
  }
  // adding a new row without explicit row id.
  public Dataframe addRow(Object... items)
  {
    if (Tools.isSet(items) && items.length>0 && (items.length/2)==Math.round(items.length/2))
    {
      if (mData==null) mData = HashBasedTable.create();
      // creat a new row
      String row = mData.rowKeySet().size()+""; addRowId(row);
      for (int i=0; i<items.length; i+=2)
      {
        mData.put(row, items[i].toString(), items[i+1]);
      }
    }

    return this;
  }
  public Dataframe addRowWithAttr(String id, Map<String, String> row)
  {
    if (mData==null) mData = HashBasedTable.create();
    for (String v : row.keySet())
      mData.put(id, v, row.get(v));

    return this;
  }

  public Dataframe addRow(String id, Map<String, Object> row)
  {
    if (mData==null) mData = HashBasedTable.create();
    for (String v : row.keySet())
      mData.put(id, v, row.get(v));

    return this;
  }
  // set the whole column for the var to a specific value
  // same as: df$col = val
  public Dataframe setVar(String col, String val)
  {
    if (!hasVars(col)) setVar(new Variable(col));

    if (Strs.isSet(val))
      for (String r : rows()) put(r, col, val);

    return this;
  }
  // create the variable if necessary and set the new column to ys[]
  public Dataframe setVar(String v, double[] ys)
  {
    if (!hasVars(v)) setVar(new Variable(v));

    if (ys!=null && ys.length==rows().size())
      for (int i=0; i<ys.length; i++)
      {
        put(getRowId(i), v, ys[i]);
      }

    return this;
  }
  public Var setVar(Var v)
  {
    if (mNameVar==null) mNameVar = new HashMap<>();
    if (mNameVar.put(v.getName(), v)==null)
    {
      // add to the var list if this is a new one
      if ( mColIDs==null) mColIDs = new ArrayList<>();
      if (!mColIDs.contains(v.getName())) mColIDs.add(v.getName());
    }
    return v;
  }
  public boolean hasVars(String... vs)
  {
    if (!Tools.isSet(vs)) return false;
    for (String v : vs) if (asVar(v)==null) return false;

    return true;
  }
  public boolean hasVar(String s, boolean isCategorical)
  {
    if (Strs.isSet(s) && asVar(s)!=null && asVar(s).isCategorical()==isCategorical) return true;
    return false;
  }
  public boolean hasVar(Var s)
  {
    return (s!=null && mNameVar!=null && mNameVar.values().contains(s));
  }
  public Object[] cells(String rowid, String... vs)
  {
    Object[] lead = new Object[vs.length];
    for (int i=0; i<vs.length; i++) lead[i]=row(rowid).get(vs[i]);

    return lead;
  }
  public String  getStr(    String rowid, String s) { return mData!=null && mData.get(rowid,s)!=null?mData.get(rowid,s).toString():null; }
  public Boolean getBool(   String rowid, String s) { return mData!=null && mData.get(rowid,s)!=null?(Boolean )mData.get(rowid,s):null; }
  public Double  getDouble( String rowid, String s) { return mData!=null && mData.get(rowid,s)!=null?Stats.toDouble(mData.get(rowid,s)):null; }
  public Integer getInteger(String rowid, String s) { return mData!=null && mData.get(rowid,s)!=null?Stats.toInt(mData.get(rowid,s)):null; }

  public Object cell(String rowid, String s) { return mData!=null?mData.get(rowid,s):null; }
  public List<String> cols() { return mColIDs; }
  public List<String> rows() { return mRowIDs; }

  public Dataframe reorder(String... s)
  {
    if (!Tools.isSet(s) || mData==null || !Tools.isSet(mColIDs) || !Tools.isSet(mNameVar)) return this;

    mColIDs = new ArrayList<String>(s.length);
    for (String v : s) if (mNameVar.containsKey(v)) mColIDs.add(v);
    return this;
  }
  // simulate optional var so it's OK to call display(). Only the first element of the array is used
  public StringBuffer display() { return display("\t", ""); }
  public StringBuffer display(String delim, String empty)
  {
    StringBuffer buf = new StringBuffer();
    buf.append("rowid" + delim + Strs.toString(cols(), delim) + "\n");
    for (String id : rows())
    {
      buf.append(id);
      for (String v : cols())
        buf.append(delim + (cells(id, v)!=null&& cells(id, v)[0]!=null? cells(id, v)[0]:empty));

      buf.append("\n");
    }
    return buf;
  }
  public StringBuffer wiki(int deci)
  {
    StringBuffer buf = new StringBuffer();

    for (String col : cols()) buf.append("||"+col);
    buf.append("||\n");
    for (String id : rows())
    {
      for (String v : cols())
      {
        Object val = (cells(id, v) != null && cells(id, v)[0] != null ? cells(id, v)[0] : null);
        buf.append("|" + (val!=null?(val instanceof Double?Stats.d2d((double)val, deci):val):" "));
      }
      buf.append("|\n");
    }
    return buf;
  }
  public StringBuffer csv(int decimal) { return csv(decimal, ","); }
  public StringBuffer tsv(int decimal) { return csv(decimal, "\t"); }
  public StringBuffer csv(int decimal, String delimiter)
  {
    StringBuffer buf = new StringBuffer();

    if (size()>0)
    {
      buf.append(Strs.toString(cols(), delimiter) + "\n");
      for (String id : rows())
      {
        String line=null;
        for (String v : cols())
        {
          Object val = cell(id, v);
//          if (val==null)
//          {
//            System.out.println();
//          }
          line = Strs.extend(line, val==null?"":Tools.o2s(val, decimal), delimiter);
        }
        buf.append(line + "\n");
      }
    }
    return buf;
  }
  public void csv(String out, int decimal, String delimiter)
  {
    try
    {
      System.out.println("\nWriting the data frame to: " + out);
      FileWriter writer = null;
      try
      {
        writer = new FileWriter(out);
        writer.write(csv(decimal, delimiter).toString());
      }
      finally {
        if (writer!=null) writer.close();
      }
    }
    catch (IOException ie) {}
  }
  @Override
  public String toString()
  {
    return getTitle();
  }
  public void write(Writer writer, String delim, boolean rowid, String blank)
  {
    try
    {
      try
      {
        if (rowid) writer.write("rowid" + delim);
        writer.write(Strs.toString(cols(), delim) + "\n");
        for (String id : rows())
        {
          if (rowid) writer.write(id+delim);
          for (int i=0; i<cols().size(); i++)
            // not worry about the vector in the cell!
            writer.write((cell(id, cols().get(i))!=null ? cell(id, cols().get(i)) : blank)+(i<cols().size()-1?delim:""));

          writer.write("\n"); // terminate the line
        }
      }
      finally { writer.close(); }
    }
    catch (IOException io)
    {
      throw new RuntimeException("Failed to write the data frame to the output.", io);
    }
  }
  public SortedMap<Double, Double> getXY(String x, String y)
  {
    if (!hasVar(x, false) || hasVar(y,false)) return null;

    SortedMap<Double, Double> line = new TreeMap<Double, Double>();
//    Var vx=asVar(x), vy=getVar(y);
    for (String id : rows()) {
      Tools.putNotNull(line, cell(id, x), cell(id, y));
    }
    return line;
  }
  public double[] getDoubleCol(String y, boolean keep_na)
  {
    if (!Tools.isSet(mData) || !hasVars(y)) return null;

    List<Double> data =new ArrayList<>();
    for (int i=0; i<rows().size(); i++)
    {
      Double val = Stats.toDouble(cell(rows().get(i), y));
      if (val!=null) data.add(val); else if (keep_na) data.add(Double.NaN);
//      ys[i] = val!=null?Stats.toDouble(val):Double.NaN;
    }
    double[] ys = new double[data.size()];
    for (int i=0; i<data.size(); i++) ys[i]=data.get(i);

    return ys;
  }
  public long[] getLongCol(String y)
  {
    if (!Tools.isSet(mData) || !hasVars(y)) return null;

    long[] ys = new long[rows().size()];
    for (int i=0; i<rows().size(); i++)
    {
      ys[i] = Stats.toLong(cell(rows().get(i), y));
    }
    return ys;
  }
  public long[] getLongCol(String y, Collection<String> rs)
  {
    if (mData==null || !hasVars(y)) return null;

    long[] ys = new long[rs.size()]; int i=0;
    for (String r : rs)
    {
      ys[i++] = Stats.toLong(cell(r, y));
    }
    return ys;
  }

  /** Grab the long col while keeping the correspondence with the row_id in rs
   *
   * @param y is the col name where the LONG value will be taken from
   * @param rs contains the row ids
   * @return
   */
  public Map<Long, String> getLongColRow(String y, Collection<String> rs)
  {
    if (mData==null || !hasVars(y)) return null;

    Map<Long, String> xy = new HashMap<Long, String>(); int i=0;
    for (String r : rs)
    {
      xy.put(Stats.toLong(cell(r, y)), r);
    }
    return xy;
  }
  public String[] getStrCol(String y)
  {
    if (mData==null || !hasVars(y)) return null;

    String[] ys = new String[rows().size()];
    for (int i=0; i<rows().size(); i++)
    {
      ys[i] = cell(rows().get(i), y).toString();
    }
    return ys;
  }
  public Dataframe renameCol(String from, String to)
  {
    if (!Tools.isSet(cols())) return this;
    // look
    int i = cols().indexOf(from);
    if (i>=0)
    {
      mColIDs.set(i, to);
      Var v = mNameVar.get(from);
      v.setName(to); mNameVar.remove(from); mNameVar.put(to, v);
      // move the actual column
      Map<String, Object> col = mData.column(from);
      if (Tools.isSet(col))
        for (String r : col.keySet())
        {
          mData.put(r, to, col.get(r));
          // remove the old column
          mData.remove(r, from);
        }
    }
    return this;
  }
  public Dataframe removeCols(String... cols)
  {
    if (Tools.isSet(cols) && mData!=null)
      for (String col : cols)
      {
        mColIDs.remove(col); mNameVar.remove(col);
        if (mData.column(col)!=null) mData.column(col).clear();
      }

    return this;
  }
  public Dataframe removeRows(String... rows)
  {
    if (Tools.isSet(rows) && mData!=null)
      for (String row : rows)
      {
//        System.out.println("removing " + row);
        mRowIDs.remove(row);
        if (mData.row(row)!=null)
        {
          mData.row(row).clear();
//          mData.rowKeySet().remove(row);
        }
      }
    // re-init the columns since we removed some of the rows
    for (String col : cols()) init(asVar(col), true);

    return this;
  }
  //** builders, no variable init here **//
  public void readTable(String src, String[] selected_cols, char delimiter, String... idcols)
  {
    if (!IOs.exists(src)) return;

    System.out.println("Reading the data table from " + src);
    TabFile csv=null;
    try
    {
      csv = new TabFile(src, delimiter);
      // convert the header to variables
      mColIDs = new ArrayList<>();
      mData    = HashBasedTable.create();

      // keep only the selected cols if specified
      for (String col : (Tools.isSet(selected_cols)?selected_cols:csv.getHeaders()))
        if (Tools.contains(csv.getHeaders(), col)) setVar(new Variable(col));

      // going thro the rows
      long row_counts = 0;
      while (csv.hasNext())
      {
        if (++row_counts % 10000  ==0) System.out.print(".");
//        if (  row_counts % 1000000==0) System.out.println();
        String id=null;
        if (Tools.isSet(idcols))
        {
          for (String col : idcols)
            id= Strs.extend(id, csv.get(col), "_");
        }
        else id = row_counts+"";

        addRowId(id);
        // deposit the cells
        for (String v : cols()) if (csv.get(v)!=null) mData.put(id, v, csv.get(v));
      }
      csv.close();
      setupVars();
    }
    catch (IOException ioe)
    {
      throw new RuntimeException("Unable to access file: " + src, ioe);
    }
  }
  public Dataframe setupVars()
  {
    if (!Tools.isSet(mData)) return this;
    if (mRowIDs ==null) { mRowIDs  = new ArrayList<>(mData.rowKeySet()); Collections.sort(mRowIDs); }
    if (mColIDs ==null)
    {
      mColIDs  = new ArrayList<>(mData.columnKeySet());
      mNameVar = new HashMap<>(mColIDs.size());
      for (String v : mColIDs) mNameVar.put(v, new Variable(v));
    }
    return this;
  }
  // go through the table to determine the type of the variables. Convert them to number if necessary
  public Dataframe init(boolean toNum)
  {
    if (!Tools.isSet(mData)) return this;

    mColIDs=null; mRowIDs=null; setupVars();
    if (Tools.isSet(mColIDs))
      for (String v : mColIDs)
      {
        Var V = asVar(v);
        if (V!=null) { V.setFactors(null); init(V, toNum);}
      }
    return this;
  }
  // re-init the columns after some of the rows were removed
  public Dataframe updateVars()
  {
    for (String col : cols()) init(asVar(col).getType(), col);
    return this;
  }
  private Dataframe init(Var v, boolean toNum)
  {
    if (!Tools.isSet(mData)) return this;

    int    counts=0;
    boolean isNum=true;
    for (String row : mRowIDs)
    {
      Object val = cell(row, v.getName());

      if (toNum) val = Stats.toNumber(val);
      if (val!=null)
      {
        counts++;
        if (!(val instanceof String) || ((String )val).length()>0) v.addFactor(val);
        // put the cell back
        if (row!=null && v!=null) mData.put(row, v.getName(), val);
        if (val instanceof String) isNum=false;
      }
    }
    v.setNumEntries(counts).isNumeric(isNum);
    if (v.isType(Var.VarType.UNKNOWN))
    {
      if (v.getNumFactors()>0 && (!isNum || v.getNumFactors()<Math.min(250, v.getNumEntries()*0.25)))
        v.setType(Var.VarType.CATEGORICAL);
      else
      {
        v.setType(Var.VarType.CONTINOUOUS);
        v.setDistribution(Stats.newHistogram(16, getDoubleCol(v.getName(), false)));
      }
    }
    return this;
  }
  public Dataframe init(Var.VarType type, String... vs)
  {
    if (!Tools.isSet(mData)) return this;

    for (String s : vs)
    {
      // add the var if necessary
      if (!hasVars(s)) setVar(new Variable(s));
      Var v = asVar(s);
      if (v==null) continue;

      if (!Tools.isSet(rows()))
      {
        v.reset(); continue;
      }
      if (Tools.equals(type, Var.VarType.CATEGORICAL))
      {
        int counts = 0;
        for (String row : mRowIDs)
        {
          Object val = cell(row, s);
//          if (val!=null && val instanceof String && Strs.isA((String )val, mNAs))
//          {
//            val=null;
//            // remove the cell if the value is null
//            mData.remove(row, s);
//          }
          if (val!=null)
          {
            counts++; v.addFactor(val);
          }
        }
        v.setNumEntries(counts);
      }
      else if (Tools.equals(type, Var.VarType.CONTINOUOUS))
      {
        for (String row : mRowIDs)
        {
          if (row==null) continue;

          Object val = Stats.toNumber(cell(row, v.getName()));
          if (val instanceof String) break;
          // put the cell back
          if (val!=null) mData.put(row, v.getName(), val);
        }
        v.setDistribution(Stats.newHistogram(16, getDoubleCol(s, false)));
      }
      v.setType(type);
    }
    return this;
  }

  /** Test whether the columns are identical
   *
   * @param A
   * @param B
   * @return
   */
  public boolean isEqualCols(String A, String B)
  {
    if (mColIDs==null || !mColIDs.contains(A) || !mColIDs.contains(B)) return false;

    MapDifference<String, Object> diff = Maps.difference(mData.column(A), mData.column(B));
    return diff.areEqual();
  }

  /** Produce a shallow copy of the self */
  @Override
  public Dataframe clone()
  {
    Dataframe out = new Dataframe();
    if (Tools.isSet(mData))
    {
      out.mData = HashBasedTable.create(); out.mData.putAll(mData);
    }
    out.setTitle(getTitle());
    out.mKeepData = mKeepData;
    if (mRowIDs !=null) out.mRowIDs =new ArrayList<>(mRowIDs);
    if (mColIDs !=null) out.mColIDs =new ArrayList<>(mColIDs);
    if (mNameVar!=null) out.mNameVar=new HashMap<>(  mNameVar);

    return out;
  }

  /** Produce a view of the self. Any change to the Data object of the 'view' will alter the self, and vice versus
   *  Row and col IDs are free to change on their own
   *
   * @return
   */
  public Dataframe view()
  {
    Dataframe out = new Dataframe();

    out.setTitle(getTitle());
    out.mKeepData = mKeepData;
    if (mRowIDs !=null) out.mRowIDs =new ArrayList<>(mRowIDs);
    if (mColIDs !=null) out.mColIDs =new ArrayList<>(mColIDs);
    if (mNameVar!=null) out.mNameVar=new HashMap<>(  mNameVar);

    out.mData = mData;

    return out;
  }

  //********** factory methods ***************//
  // obs2 <- read.table(header=T, text='number  size type\n1   big  cat\n2 small  dog\n3 small  dog\n4   big  dog\n5   big  dog\n6   big  dog')
  public static Dataframe readtable(boolean header, String text)
  {
    Dataframe    f = new Dataframe();
    String[] lines = Strs.split(text, '\n');

    // test for the field delimiter. prefer tab if exist
    String token = lines[0].split("\t").length>1?"\t":"\\s+";
    // make the headers
    String[] headers = header&&lines.length>0?lines[0].split(token):Strs.toStringArray(Stats.newIntArray(0,lines[0].split(token).length));

    // fill out the data frame
    for (int i=(header?1:0); i<lines.length; i++)
    {
      f.addRowId(i+"");
      String[] fields = lines[i].split(token);
      for (int j=0; j<fields.length; j++)
        f.put(i+"", headers[j], fields[j]);
    }
    return f.setupVars();
  }
  public static Dataframe readtable(String src, char delimiter, String... idcols)
  {
    return readtable(src, delimiter, true, idcols);
//    Dataframe f = new Dataframe(src, delimiter, idcols);
//    return f;
  }
  public static Dataframe readtable(String src, char delimiter, boolean init, String... idcols)
  {
    Dataframe f = new Dataframe(src, delimiter, idcols);
    return init?f.init(true):f;
  }
  public static Dataframe readtable(String src, String[] selected_cols, char delimiter, boolean init, String... idcols)
  {
    Dataframe f = new Dataframe(src, selected_cols, delimiter, idcols);
    return init?f.init(true):f;
  }

  //********** R or Matlab style algorithms ***************//

  /** Split the data frame by the factors in variable 'v'
   *
   * @param v
   * @return
   */
  public Map<Object, Dataframe> split(String v)
  {
    if (v==null || !hasVar(v,true)) return null;

    Map<Object, Dataframe> outs = new HashMap<Object, Dataframe>();
    for (Object f : asVar(v).getFactors())
      outs.put(f, subset(v+"=="+(f instanceof String ? ("'"+f+"'"):f.toString())));

    return outs;
  }

  /** clone the data frame that has only the requested columns
   *
   * @param cols
   * @return
   */
  public Dataframe subcol(String... cols)
  {
    Dataframe out = new Dataframe();
    if (Tools.isSet(mData))
    {
      out.mData    = HashBasedTable.create();
      out.mColIDs  = new ArrayList<>(cols.length);
      out.mNameVar = new HashMap<>();
      for (String col : cols)
      {
        if (!mColIDs.contains(col)) continue;

        out.mColIDs.add(col);
        out.mNameVar.put(col, mNameVar.get(col));
        for (String row : rows())
          if (mData.get(row, col)!=null)
            out.mData.put(row, col, mData.get(row, col));
      }
    }
    out.setTitle(getTitle());
    out.mKeepData = mKeepData;
    if (mRowIDs !=null) out.mRowIDs =new ArrayList<>(mRowIDs);

    return out;
  }
  /** subset the rows according to the test conditions specified, similar to 'subset' function from R
   *
   * subset(animals, type=="cat")
   *
      size    type    name
   1  small   cat     lynx
   2  big     cat     tiger
   *
   * @return
   */
  public Dataframe subset(String test)
  {
    // taking
    Dataframe out = view();
    NameValue nv  = new NameValue();
    // parse the test and prepare the row subset
    List<String> rows = new ArrayList<>();
    String[]      ors = Strs.split(test, '|', true);
    if (Tools.isSet(ors))
      for (String or : ors)
      {
        String[]     ands = Strs.split(or, '&', true);
        List<String> subs = new ArrayList<>(rows());
        if (Tools.isSet(ands))
          for (String and : ands)
          {
            if (nv.parse(and, "==",">=","<=",">","<","!=","%in%") && out.hasVars(nv.name))
            {
              Iterator<String> itr = subs.iterator();
              while (itr.hasNext())
              {
                // remove the row if not meeting the test
                Object v=Stats.toNumber(cell(itr.next(), nv.name));
                if (v instanceof String)
                {
                  if ((Tools.equals(nv.token, "==") && !Tools.equals((String )v, nv.val)) ||
                      (Tools.equals(nv.token, "!=") &&  Tools.equals((String )v, nv.val))) itr.remove();
                }
                else if (v instanceof Double)
                {
                  if ((Tools.equals(nv.token, "==") && !Tools.equals((Double )v, nv.getNumber())) ||
                      (Tools.equals(nv.token, "!=") &&  Tools.equals((Double )v, nv.getNumber())) ||
                      (Tools.equals(nv.token, "<=") &&  (Double )v>nv.getNumber()) ||
                      (Tools.equals(nv.token, ">=") &&  (Double )v<nv.getNumber()) ||
                      (Tools.equals(nv.token, "<")  &&  (Double )v>=nv.getNumber()) ||
                      (Tools.equals(nv.token, ">")  &&  (Double )v<=nv.getNumber())) itr.remove();
                }
              }
            }
          }
        // combine the rows
        for (String r : subs)
          if (rows.size()==0 || !rows.contains(r)) rows.add(r);
      }

    if (!Tools.isSet(rows)) { out.clear(); return out; }
    // refresh the factors and update the rows
    return out.setRowIds(rows).updateVars();
  }
  /** Partial implementation of R-aggregate
   *
   * @param by is a list of grouping categorical variables,
   * @return
   */
  public Dataframe aggregate(String... by)
  {
    Dataframe stats = new Dataframe();

    return stats;
  }
  public Dataframe melt(String... idvars)
  {
    return null;
  }

  /** generic transformation of a data frame in the style of Matlab
   *
   * pivot( [dose sbj], visit_name ) produces the following table

   []               []    'visit_name'    'visit_name'
   'dose'      'sbj'            'D0'            'D22'
   'dosed'    '1003'    [         1]    [         1]
   'dosed'    '1015'    [         1]    [         1]
   'dosed'    '1025'    [         1]    [         1]
   *
   * @param col is a categorical column whose factors will be used as the column header in the outgoing data frame
   * @param val is a numberic column whose values will be the cell in the outgoing data frame
   * @param func is the aggregate function if multiple values are found in a cell
   * @param rows are the columns that will transferred to the outgoing data frame
   * @return the outgoing data frame
   */
  public Dataframe pivot(String col, String val, Stats.Aggregator func, String... rows)
  {
    // make sure the column types are OK
    if ((Tools.isSet(rows) && !hasVars(rows)) || !hasVar(col, true) || !hasVar(val, true)) return null;
    // build the inventory
    ListMultimap<ArrayKey, Object> body = ArrayListMultimap.create();
    for (String rowid : rows())
    {
      body.put(new ArrayKey(ObjectArrays.concat(cell(rowid, col), cells(rowid, rows))), cell(rowid, val));
    }
    // construct the outgoing data frame
    Dataframe out = new Dataframe();
    for (ArrayKey keys : body.keySet())
    {
      String id = Strs.toString(Arrays.copyOfRange(keys.key, 1, keys.key.length), "");
      for (int i=1; i< keys.key.length; i++)
      {
        out.put(id, rows[i-1], keys.key[i]);
      }
      out.put(id, keys.key[0].toString(), Stats.aggregate(body.get(keys), func));
    }
    out.init(true);
    body=(ListMultimap )Tools.dispose(body);
    out.reorder(ObjectArrays.concat(rows, Strs.toStringArray(asVar(col).getFactors()), String.class));

    return out;
  }
  public Multimap<String, String> factorize(String row)
  {
    if (!hasVars(row)) return null;

    Multimap<String, String> indice = TreeMultimap.create();
    for (String rowid : rows())
    {
      Object v = cell(rowid, row);
      if (v instanceof String && Strs.isSet((String)v)) indice.put((String )v, rowid);
    }

    return indice;
  }
  public Multimap<String, String> IndexByCol(String col)
  {
//    if (!hasVar(col,true)) return null;

    Multimap<String, String> indice = HashMultimap.create();
    for (String rowid : rows())
      if (cell(rowid, col)!=null)
        indice.put(cell(rowid, col).toString(), rowid);

    return indice;
  }
  // construct a sorted index by two cols of the data frame. For example, mz vs RT
  public MultiTreeTable<Double, Double, String> index(String row, String col)
  {
    if (!hasVar(row,false) || !hasVar(col,false)) return null;

    MultiTreeTable<Double, Double, String> indice = MultiTreeTable.create();
//    Var vrow=getVar(row), vcol=getVar(col);
    for (String rowid : rows())
      indice.put(Stats.toDouble(cell(rowid, row)), Stats.toDouble(cell(rowid, col)), rowid);

    return indice;
  }
  // construct a sorted index by two cols of the data frame. For example, mz vs RT
  public Table<String, String, Double> IndexAB(String k1, String k2, String val)
  {
    Table<String, String, Double> indice = HashBasedTable.create();
    for (String rowid : rows())
      indice.put(cell(rowid, k1).toString(), cell(rowid, k2).toString(), getDouble(rowid, val));

    return indice;
  }

  public SortedSetMultimap<Double, String> index(String row)
  {
    if (!hasVar(row,false)) return null;

    TreeMultimap<Double, String> indice = TreeMultimap.create();
//    Var vrow=getVar(row), vcol=getVar(col);
    for (String rowid : rows())
      indice.put(Stats.toDouble(cell(rowid, row)), rowid);

    return indice;
  }
  public Map toMap(String from, String to)
  {
    if (mData!=null && mData.column(from)!=null && mData.column(to)!=null)
    {
      Map mapping = new HashMap();
      for (String F : mData.column(from).keySet())
        mapping.put(mData.get(F, from), mData.get(F, to));

      return mapping;
    }
    return null;
  }
  public static MultiTreeTable<Double, Double, String>[] indice(String row, String col, Dataframe... frames)
  {
    if (!Tools.isSet(frames) || !Strs.isSet(row) || !Strs.isSet(col)) return null;

    MultiTreeTable<Double, Double, String>[] indices = new MultiTreeTable[frames.length];
    for (int i=0; i<frames.length; i++)
    {
      indices[i] = frames[i].index(row, col);
    }
    return indices;
  }

  // replicate the row if multiple values exist in the given col
  public Dataframe unroll(char delimiter, String... cols)
  {
    if (!Tools.isSet(mData) || !Tools.isSet(cols)) return this;

    int last_row = rows().size();
    for (String col : cols)
      for (String row : rows())
        if (mData.get(row, col)!=null)
        {
          String[] items = Strs.split(mData.get(row, col).toString(), delimiter);
          if (items.length>1)
          {
            put(row, col, items[0]);
            for (int i=1; i<items.length; i++)
            {
              last_row++;
              addRow(last_row+"", row(row)).put(last_row+"", col, items[i]);
            }
          }
        }

    return init(true);
  }
  /** ftable(animals), same as ftable(animals[,c("size","type","name")])
   *
              name   chihuahua   greatdane   lynx  tiger
   size   type
   big    cat               0          0      0     1
          dog               0          1      0     0
   small  cat               0          0      1     0
          dog               1          0      0     0
   *
   > ftable(animals[,c("size","type")])
          type cat dog
   size
   big          1   1
   small        1   1
   *
   * @param cols
   * @return
   */
  public Dataframe ftable(String... cols)
  {
    Dataframe out = new Dataframe();

    return out;
  }
  public Dataframe removeRowsWithMissingValue(int max)
  {
    // remove the row with missing value
    int ncols = cols().size();
    List<String> missing = new ArrayList<>();
    for (String row : rows())
      if (row(row).values().size()+max<ncols) missing.add(row);

    if (Tools.isSet(missing)) removeRows(missing.toArray(new String[] {}));
    return this;
  }
  public List<String> getColByPopulation(int min)
  {
    List<String> vars = new ArrayList<>();
    for (String col : cols())
    {
      Var v = asVar(col);
      if (v!=null && v.isContinuous() && v.getNumEntries()>min) vars.add(col);
    }
    return vars;
  }
  //** algorithms **//

  /**The returned data frame will contain:

   columns: all columns present in any provided data frame
   rows:    a set of rows from each provided data frame, with values in columns not present in the given data frame
            filled with missing (NA) values.

   The data type of columns will be preserved, as long as all data frames with a given column name agree on the
   data type of that column. If the data frames disagree, the column will be converted into a character strings.
   The user will need to coerce such character columns into an appropriate type.
   *
   * @param frames
   * @return
   */
  public static Dataframe smartbind(Dataframe... frames)
  {
    // prepare the merged columns
    Set<String> cols = new TreeSet<String>(); int order=0;
    for (Dataframe F : frames)
    {
      if (!Strs.isSet(F.getTitle())) F.setTitle(""+order++);
      cols.addAll(F.cols());
    }
    // the resulting dataframe
    Dataframe output = new Dataframe();
    for (Dataframe frame : frames)
    {
      for (String v : cols)
        for (String r : frame.rows())
          if (frame.cols().contains(v)) output.put(frame.getTitle()+"::"+r, v, frame.cell(r,v));
          // no value set if col didn;t exist for this dataframe. In R-routine, NA would the be the default
    }
    return output;
  }

  /** Merge two data frames by common columns or row names, or do other versions of database join operations.

   *
   * @param x and y : the data frames to be merged
//   * @param allx and ally : TRUE if the rows from x/y will be added to the output that contains no matching in the other.
   * @param all is true is the unmatched rows are to be placed in the merged data frame
   * @return the dataframe with the merge data
   */
  public static Dataframe merge(Dataframe x, Dataframe y, boolean combineSharedCol, boolean all, String... by)
  {
    if (x==null || y==null) return null;
    // cells the shared cols
    String[] shared=Strs.toStringArray(Tools.overlap(x.cols(), y.cols()));
    // set the by cols to the common if not specified
    if (!Tools.isSet(by)) by=shared;
    if (!Tools.isSet(by)) return null;
    // pool the matching rows
    MapOfMultimap<String, Integer, String> id_x_y = MapOfMultimap.create();
    for (String r : x.rows())
      id_x_y.put(Strs.toString(x.cells(r, by),"^"), 1, r);
    for (String r : y.rows())
      id_x_y.put(Strs.toString(y.cells(r, by),"^"), 2, r);

    // create the merged cols
    Table<Integer, String, String> xy_var_col = HashBasedTable.create();
    for (String v : x.cols())
      xy_var_col.put(1, v, !Tools.contains(shared, v) || Tools.contains(by, v) ? v : v + (Strs.isSet(x.getTitle())?"."+x.getTitle():".x"));
    for (String v : y.cols())
      xy_var_col.put(2, v, !Tools.contains(shared, v) || Tools.contains(by, v) ? v : v + (Strs.isSet(y.getTitle())?"."+y.getTitle():".y"));

    // create the merged results
    Dataframe out = new Dataframe();
    // deposite the columns
    for (String v : xy_var_col.values()) out.setVar(new Variable(v));

    for (String id : id_x_y.keySet())
    {
      if (id_x_y.get(id).keySet().size()>1)
        for (String xrow : id_x_y.get(id, 1))
          for (String yrow : id_x_y.get(id, 2))
          {
            String row = xrow+"."+yrow;
            out.addRowId(row);
            // make up the unique row id
            // deposit the A first
            for (String v : x.cols())
              out.put(row, xy_var_col.get(1, v), x.cell(xrow, v));
            for (String v : y.cols())
              out.put(row, xy_var_col.get(2, v), y.cell(yrow, v));
          }
      else if (all)
      {
        // singleton, not matched between x and y
        for (Integer xy : id_x_y.get(id).keySet())
        {
          for (String xyrow : id_x_y.get(id, xy))
          {
            String row = (xy==1?(xyrow+"."):("."+xyrow));
            out.addRowId(row);
            if (xy==1)
            {
              for (String v : x.cols())
                out.put(row, xy_var_col.get(xy, v), x.cell(xyrow, v));
            }
            else if (xy==2)
            {
              for (String v : y.cols())
                out.put(row, xy_var_col.get(xy, v), y.cell(xyrow, v));
            }
          }
        }

      }
    }
    // combined the shared cols bot in 'by' if asked
    if (combineSharedCol && shared!=null && shared.length>by.length)
    {
      for (String s : shared)
      {
        // skip the column if already in 'by'
        if (Tools.contains(by, s)) continue;
        if (out.isEqualCols(xy_var_col.get(1, s), xy_var_col.get(2, s)))
        {
          out.removeCols(xy_var_col.get(1, s));
          out.renameCol(xy_var_col.get(2, s), s);
        }
      }
    }
    Collections.sort(out.cols());
    return out;
  }
  public static Dataframe bundling(Dataframe data, int n)
  {
    List<String> cats=new ArrayList<>(), conts=new ArrayList<>(), clusters=new ArrayList<>();
    for (String col : data.cols())
      if (data.asVar(col).isContinuous()) conts.add(col); else cats.add(col);

    // re-arrange the continuous variables so they are grouped by their similarities
    // we have a list of our locations we want to cluster. create a
    List<Clusterable> clusterInput = new ArrayList<>(conts.size());
    for (String col : conts)
      clusterInput.add(new ParCoodsClusterable(col, data.getDoubleCol(col, true)));

    // initialize a new clustering algorithm.
    // we use KMeans++ with 10 clusters and 10000 iterations maximum.
    // we did not specify a distance measure; the default (euclidean distance) is used.
    KMeansPlusPlusClusterer<Clusterable> clusterer = new KMeansPlusPlusClusterer<>(n, 10000, new SlopeConvergenceDistance());
    List<CentroidCluster<Clusterable>> clusterResults = clusterer.cluster(clusterInput);

    // output the clusters
    for (int i=0; i<clusterResults.size(); i++)
    {
      System.out.println("Cluster " + i);
      for (Clusterable node : clusterResults.get(i).getPoints())
      {
        System.out.println(((ParCoodsClusterable )node).getCol());
        clusters.add(((ParCoodsClusterable )node).getCol());
      }
      System.out.println();
    }
    // push them back into the dataframe
    data.setColIds(cats).cols().addAll(clusters);
    return data;
  }
//  public static Dataframe bundling(Dataframe data)
//  {
//    List<String> cats=new ArrayList<>(), conts=new ArrayList<>(), clusters=new ArrayList<>();
//    for (String col : data.cols())
//      if (data.asVar(col).isContinuous()) conts.add(col); else cats.add(col);
//
//    // https://github.com/lbehnke/hierarchical-clustering-java
//    double[][] distances = new double[conts.size()][conts.size()];
//    String[]   names     = new String[conts.size()];
//    List<double[]>    ds = new ArrayList<>(conts.size());
//
//    for (int i=0; i<conts.size(); i++)
//    {
//      ds.add(data.getDoubleCol(conts.get(i), true));
//      names[i] = conts.get(i);
//    }
//
//    // re-arrange the continuous variables so they are grouped by their similarities compute the similarity matrix
//    SlopeConvergenceDistance score = new SlopeConvergenceDistance();
//    for (int i=0; i<conts.size(); i++)
//      for (int j=0; j<conts.size(); j++)
//        distances[i][j]=(i==j?0d:score.compute(ds.get(i), ds.get(j)));
//
//    ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
//    Cluster c = alg.performClustering(distances, names, new AverageLinkageStrategy());
//
//    List<Cluster> leafs = new ArrayList<Cluster>();
//    Clusters.gatherLeafs(c, leafs, null);
//
//    // create the col mapping
//    data.setColIds(cats); conts.clear();
//    for (Cluster leaf : leafs)
//    {
//      String[] n = leaf.getName().split("\\~\\^\\~");
//      data.cols().add(n[1]);
//    }
//    // push them back into the dataframe
//    for (Cluster leaf : leafs)
//    {
//      String[] n = leaf.getName().split("\\~\\^\\~");
//      data.renameCol(n[1], n[0]+": "+n[1]);
//    }
//    return data;
//  }
//  public static Dataframe sort(Dataframe d, String... cols)
//  {
//
//  }

  @Override
  public void dispose()
  {
    mTitle=null;
    mRowIDs=(List )Tools.dispose(mRowIDs);
    mColIDs=(List )Tools.dispose(mColIDs);
    mNameVar=Tools.dispose(mNameVar);
    mData=Tools.dispose(mData);
  }
  public void clear()
  {
    dispose();
    mKeepData = true;
  }
}

