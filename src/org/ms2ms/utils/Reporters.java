package org.ms2ms.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import toools.set.IntSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
  public static <K, L, V> StringBuffer inventory_col(StringBuffer buf, Table<K,L,V> data, int pivot_max)
  {
    if (Tools.isSet(data))
    {
      Multimap<L, V> pivots = HashMultimap.create();
      buf.append("Totals\tUnique\tProperty\n");
      for (L label : data.columnKeySet())
      {
//        int combined = 0;
//        if (Tools.front(data.cellSet()).getValue() instanceof IntSet)
//          for (K key : data.column(label).keySet())
//            combined += ((IntSet )data.get(key, label)).size();
//
        Set<V> uniques = Sets.newHashSet(data.column(label).values());
        buf.append(data.column(label).values().size() + "\t" + uniques.size() + "\t" + label + "\n");

        if (uniques.size()<pivot_max)
          pivots.putAll(label, data.column(label).values());
      }
      buf.append("\n");
/*
      Multimap<L,K> rows = HashMultimap.create();
      if (Tools.isSet(pivots))
        for (L pivot : pivots.keys())
        {
          buf.append(pivot+"\t");
          for (L label : data.columnKeySet()) if (!label.equals(pivot)) buf.append(label+"\t");
          buf.append("\n");

          // for each levels of the pivot
          for (V val : pivots.get(pivot))
          {
            buf.append(val+"\t"); rows.clear();
            for (K key : data.rowKeySet())
            {
              // skip if the row does not have this level
              if (Tools.equals(data.get(key, pivot), val))
                for (L lab : data.columnKeySet())
                  if (!lab.equals(pivot)) rows.put(lab, key);
            }
            for (L label : data.columnKeySet()) if (!label.equals(pivot)) buf.append(rows.get(label).size()+"\t");
            buf.append("\n");
          }
          buf.append("\n");
        }
*/
    }
    buf.append("\n");
    return buf;
  }
}
