package org.ms2ms.r;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.solr.util.stats.Histogram;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wyu
 * Date: 7/13/14
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Var
{
  public enum VarType { CONTINOUOUS, CATEGORICAL, NUMERICAL, UNKNOWN }

  public Var setName(String s);
  public String getName();
  public VarType getType();
  public int getNumEntries();
  public int getNumFactors();
  public Var setNumEntries(int s);

  public boolean isCategorical();
  public boolean isContinuous();
  public boolean isNumeric();
  public Var isNumeric(boolean s);
  public Var setType(VarType s);
  public Var addFactor(Object s);
  public Var setFactors(Collection s);
  public Collection getFactors();
  public boolean isType(VarType s);
  public EmpiricalDistribution getDistribution();
  public Var setDistribution(EmpiricalDistribution s);
}
