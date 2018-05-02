package org.ms2ms.math;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.ms2ms.utils.Strs;

public class DoubleBag extends Number
{
  private double   mLead;
  private Multimap<String, Double> mMembers;

//  public DoubleBag() { mLead=Double.NaN; }
  public DoubleBag()
  {
    mMembers= HashMultimap.create();
    mLead=Double.NaN;
  }

  public Double getLead()
  {
    return (mMembers!=null?Stats.mean(mMembers.values()):null);
  }
  public String getLeadTag()
  {
    return (mMembers!=null? Strs.toString(mMembers.keySet(), ";"):null);
  }

  public DoubleBag setLead()
  {
    if (mMembers!=null) mLead = Stats.mean(mMembers.values());
    return this;
  }
  public DoubleBag add(String k, double s)
  {
    if (mMembers==null) throw new RuntimeException("Members not initialized or full!");
    mMembers.put(k,s);
    return this;
  }
  @Override public int       intValue() { return (int   )mLead; }
  @Override public long     longValue() { return (long  )mLead; }
  @Override public float   floatValue() { return (float )mLead; }
  @Override public double doubleValue() { return         mLead; }
}
