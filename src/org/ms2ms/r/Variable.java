package org.ms2ms.r;

import com.bigml.histogram.NumericTarget;
import org.ms2ms.math.Histogram;
import org.ms2ms.math.Transformer;
import org.ms2ms.utils.Tools;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wyu
 * Date: 7/13/14
 * Time: 1:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Variable implements Var
{
  private boolean      mIsNumeric;
  private int          mEntries;
  private String       mName;
  private Map<Object, Integer> mFactors;
  private VarType      eType = VarType.UNKNOWN;
  private Histogram mDist; // for the continuous type

  public Variable(String s)            { setName(s); }
  public Variable(String s, VarType t) { setName(s); eType=t; }

  public String getName()  { return mName; }
  public VarType getType() { return eType; }
  public int getNumFactors() { return mFactors!=null?mFactors.size():0; }
  public int getNumEntries() { return mEntries; }
  public Histogram getDistribution() { return mDist; }
  public Var setNumEntries(int s) { mEntries=s; return this; }
  public Var setDistribution(Histogram s) { mDist=s; return this; }

  public boolean isCategorical() { return isType(VarType.CATEGORICAL); }
  public boolean isContinuous()  { return isType(VarType.CONTINOUOUS); }
  public boolean isNumeric()     { return mIsNumeric; }
  public Var isNumeric(boolean s)     { mIsNumeric=s; return this; }

  public String getTitle()
  {
    // mention the transformation
    if (getDistribution()!=null && getDistribution().getTransformer()!=null &&
        !Tools.equals(getDistribution().getTransformer(), Transformer.processor.none))
      return getDistribution().getTransformer().name() + "("+getName()+")";

    return getName();
  }
  @Override
  public boolean isType(VarType s) { return eType.equals(s);}
  @Override
  public String toString() { return getNumEntries() + "\t" + getNumFactors() + "\t" + mName; }

  public Var setType(VarType s) { eType=s; return this; }
  public Var setFactors(Collection s)
  {
    // reset the factors
    mFactors=null;
    return addFactors(s);
  }
  public Var addFactor(Object s)
  {
    if (mFactors==null) mFactors = new HashMap<>();
    mFactors.put(s, mFactors.containsKey(s)?mFactors.get(s)+1:1);
    return this;
  }
  public Var renameFactor(Object from, Object to)
  {
    if (mFactors!=null && mFactors.containsKey(from))
    {
      mFactors.put(to, mFactors.get(from));
      mFactors.remove(from);
    }
    return this;
  }
  public Var addFactors(Collection s)
  {
    if (s==null) { mFactors=null; return this; }

    for (Object _s : s) addFactor(_s);
//    if (mFactors==null) mFactors = new ArrayList();
//    mFactors.addAll(s);
    return this;
  }
  public Var setName(String s) { mName=s; return this; }
  public Collection getFactors() { return mFactors!=null?mFactors.keySet():null; }

  @Override
  public boolean equals(Object s)
  {
    return Tools.equals(getName(), ((Var )s).getName()) && Tools.equals(getType(), ((Var )s).getType());
  }
  public Var reset()
  {
    mIsNumeric = false;
    mEntries   = 0;
    mName      = null;
    eType      = VarType.UNKNOWN;
    mDist      = null;
    Tools.dispose(mFactors);

    return this;
  }

  //**************  Utils  ***********************//
  public static Var[] toVars(String... vs)
  {
    Var[] vrows = new Var[vs.length];
    for (int i=0; i<vs.length; i++) vrows[i]=new Variable(vs[i]);

    return vrows;
  }
}
