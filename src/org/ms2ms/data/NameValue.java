package org.ms2ms.data;

import com.google.common.collect.Range;
import org.ms2ms.math.Stats;
import org.ms2ms.utils.Strs;
import org.ms2ms.utils.Tools;

/**
 * Created by IntelliJ IDEA.
 * User: wyu
 * Date: Aug 8, 2010
 * Time: 9:06:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class NameValue
{
  public String name = null, val = null, token=null;
  private Double number = null;

  public NameValue() { super(); }
  public NameValue(String n, String v) { super(); name = n; val = v; }

  public boolean is(String n, String v)
  {
    return Tools.equals(n, name) && Tools.equals(val, v);
  }
  public boolean IS(String n, String v)
  {
    return Tools.equalsCaseless(n, name) && Tools.equalsCaseless(val, v);
  }
  public boolean is(String n)
  {
    return Tools.equals(n, name);
  }
  public boolean IS(String n)
  {
    return Tools.equalsCaseless(n, name);
  }
  public boolean isNum(String n)
  {
    if (!is(n)) return false;
    return getNumber() != null;
  }
  public Double getNumber()
  {
    if (number == null)
    {
      try
      {
        number = Double.parseDouble(val);
      }
      catch (Exception e) { }
    }
    return number;
  }
  public Integer getInt()
  {
    if (number == null)
    {
      try
      {
        return Integer.parseInt(val);
      }
      catch (Exception e) { }
    }
    return number.intValue();
  }
  public Boolean getBoolean(String... s)
  {
    if (number == null)
    {
      try
      {
        return Tools.isA(val, s);
      }
      catch (Exception e) { }
    }
    return number!=0;
  }
  public Range<Integer> getIntRange()
  {
    if (number == null)
    {
      try
      {
        if      (val.indexOf('~')>0)
        {
          String[] strs =Strs.split(val, '~', true);
          return Range.closed(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]));
        }
        else if (val.indexOf(">=")==0)
        {
          return Range.atLeast(Integer.parseInt(val.substring(1)));
        }
        else if (val.indexOf("<=")==0)
        {
          return Range.atMost(Integer.parseInt(val.substring(1)));
        }
        else if (val.indexOf('<')==0)
        {
          return Range.atMost(Integer.parseInt(val.substring(1))-1);
        }
        else if (val.indexOf(">")==0)
        {
          return Range.atLeast(Integer.parseInt(val.substring(1))+1);
        }
      }
      catch (Exception e) { }
    }
    return null;
  }
  public Range<Double> getDoubleRange()
  {
    if (number == null)
    {
      try
      {
        if      (val.indexOf('~')>0)
        {
          String[] strs =Strs.split(val, '~', true);
          return Range.closed(Double.parseDouble(strs[0]), Double.parseDouble(strs[1]));
        }
        else if (val.indexOf(">=")==0)
        {
          return Range.atLeast(Double.parseDouble(val.substring(1)));
        }
        else if (val.indexOf("<=")==0)
        {
          return Range.atMost(Double.parseDouble(val.substring(1)));
        }
        else if (val.indexOf('<')==0)
        {
          return Range.atMost(Double.parseDouble(val.substring(1))-1);
        }
        else if (val.indexOf(">")==0)
        {
          return Range.atLeast(Double.parseDouble(val.substring(1))+1);
        }
      }
      catch (Exception e) { }
    }
    return null;
  }
  public void clear() { name=null; val=null; number=null; }
  public boolean parse(String line, String... tokens)
  {
    if (line==null) { clear(); return false; }
    int idx = -1;
    // go thro each token until we hit one
    for (String t : tokens)
    {
      idx=line.indexOf(t);
      if (idx>0)
      {
        name=line.substring(0, idx);
        val =idx+t.length()<line.length()?line.substring(idx+t.length()):null;
        number = Stats.toDouble(val);
//        if      (v!=null)  number=v;
//        else if (v!=null && v instanceof Integer) number=(Integer )v; else val=(String )v;
        token=t; return true;
      }
    }
    return false;
  }
  public static NameValue create(String line, String... tokens)
  {
    NameValue nv = new NameValue();
    nv.parse(line, tokens);

    return nv;
  }
}
