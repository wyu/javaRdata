package org.ms2ms.utils;

import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import toools.set.IntSet;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   2/21/15
 */
public class Reporters
{
  public static <K, L, V> StringBuffer inventory(StringBuffer buf, Table<K,L,V> data)
  {
    if (Tools.isSet(data))
    {
      buf.append("Counts\t\tProperty\n");
      for (L label : data.columnKeySet())
        buf.append(label + "\t\t" + Sets.newHashSet(data.column(label).values()).size() + "\n");
    }
    buf.append("\n");
    return buf;
  }
}
