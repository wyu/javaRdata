package org.ms2ms.data;

import org.ms2ms.utils.Tools;

import java.util.HashMap;
import java.util.Map;

/** A generic class to model rows of data in a data table. Used in an alignment routine
 *
 * User: wyu
 * Date: 7/13/14
 */
public class Features implements Comparable<Features>
{
  private Map<String, Object> mProperties;

  public Features() { super(); }
  public Features(Map<String, Object> s) { mProperties=s; }

  public Features add(String key, Object val)
  {
    if (mProperties==null) mProperties = new HashMap<>();
    mProperties.put(key, val);

    return this;
  }
  public Object get(String key) { return mProperties!=null?mProperties.get(key):null; }
  public Double getDouble(String key) { return (Double )get(key); }

  public void invalidate() { if (mProperties!=null) mProperties.clear(); }
  public boolean isValid() { return Tools.isSet(mProperties); }
  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p>
   * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
   * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
   * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
   * <tt>y.compareTo(x)</tt> throws an exception.)
   * <p>
   * <p>The implementor must also ensure that the relation is transitive:
   * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
   * <tt>x.compareTo(z)&gt;0</tt>.
   * <p>
   * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
   * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
   * all <tt>z</tt>.
   * <p>
   * <p>It is strongly recommended, but <i>not</i> strictly required that
   * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
   * class that implements the <tt>Comparable</tt> interface and violates
   * this condition should clearly indicate this fact.  The recommended
   * language is "Note: this class has a natural ordering that is
   * inconsistent with equals."
   * <p>
   * <p>In the foregoing description, the notation
   * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
   * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
   * <tt>0</tt>, or <tt>1</tt> according to whether the value of
   * <i>expression</i> is negative, zero or positive.
   *
   * @param o the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object
   * is less than, equal to, or greater than the specified object.
   * @throws NullPointerException if the specified object is null
   * @throws ClassCastException   if the specified object's type prevents it
   *                              from being compared to this object.
   */
  @Override
  public int compareTo(Features o)
  {
    return 0;
  }
  @Override
  public Features clone()
  {
    return new Features(new HashMap<>(mProperties));
  }
//  @Override
//  public String toString()
//  {
//    return mProperties.toString();
//  }
/*
  private Dataframe mData;
  private Var[]            mVariable;
  private Tolerance[]      mTols;

  public Features() { super(); }
  public Features(Tolerance tol, Var[] vars)
  {
    super();
    mVariable   = vars;
    mTols       = Arrays.copyOf(new Tolerance[] {tol}, vars.length); // one relative tolerance for all variables
  }
  public Features(Tolerance[] tol, Var... vars)
  {
    super();
    if (tol.length!=vars.length) throw new RuntimeException("Equal number of tolerances and variables required!");

    mVariable  = vars;
    mTols      = tol; // one relative tolerance for all variables
  }
  public Features(Dataframe d, Tolerance[] tol, Var... vars)
  {
    super();

    if (tol.length!=vars.length) throw new RuntimeException("Equal number of tolerances and variables required!");

    mVariable  = vars;
    mTols      = tol; // one relative tolerance for all variables
    mData      = d;
  }

  public Var       var(int i) { return mVariable!=null?mVariable[i]:null; }
  public Tolerance tol(int i) { return mTols!=null?mTols[i]:null; }

  public String  getID()            { return null; }
  public Object  cells(int i,Var key) { return mData!=null?mData.row(i).cells(key):null; }
*/
/*
  public String  getString(Var key) { return (String )cells(key); }
  public Double  getDouble(Var key) { return (Double )cells(key); }
  public Float   getFloat( Var key) { return (Float  )cells(key); }
  public Long    getLong(  Var key) { return (Long   )cells(key); }
  public Integer getInt(   Var key) { return (Integer)cells(key); }
*//*


  public Features setVars(Var... vs) { mVariable = vs; return this; }
  public Features setTols(Tolerance... ts)
  {
    mTols = ts;
    return this;
  }

  public boolean match(Features s)
  {
    for (int i=0; i<mVariable.length; i++)
      if (!mTols[i].withinTolerance(Stats.toDouble(cells(var(i))), Stats.toDouble(s.cells(s.var(i))))) return false;

    return true;
  }
  public double score(Features s)
  {
    double score = 1d;
    for (int i=0; i<mVariable.length; i++)
    {
      double x0 = Stats.toDouble(cells(var(i))), delta = x0 - Stats.toDouble(s.cells(s.var(i)));
      NormalDistribution norm = new NormalDistribution(0, (tol(i).getMax(x0)-tol(i).getMin(x0))/1.77d);
      score *= norm.density(delta); norm=null;
    }
    return score;
  }
*/
}
