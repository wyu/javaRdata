package org.ms2ms.data.collect;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.TreeMultimap;
import org.ms2ms.utils.Tools;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wyu
 * Date: 10/2/14
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MultiTreeTable<K extends Comparable, L extends Comparable, T extends Comparable> implements Cloneable
{
  private SortedMap<K, TreeMultimap<L, T>> mData;

  public MultiTreeTable() { super(); mData = new TreeMap<>(); }
  public MultiTreeTable(Comparator<? super K> keyC) { super(); mData = new TreeMap<>(keyC); }

  public long size()
  {
    long counts = 0;
    for (TreeMultimap<L,T> ts : getData().values()) counts += ts.size();

    return counts;
  }
  public void put(K key, L lable, T data)
  {
    if (mData == null) mData = new TreeMap<>();
    TreeMultimap<L, T> D = mData.get(key);
    if (D == null)
    {
      D = TreeMultimap.create();
      mData.put(key, D);
    }
    D.put(lable, data);
  }
  public void putAll(K key, L lable, Collection<T> data)
  {
    if (mData == null) mData = new TreeMap<>();
    TreeMultimap<L, T> D = mData.get(key);
    if (D == null)
    {
      D = TreeMultimap.create();
      mData.put(key, D);
    }
    D.putAll(lable, data);
  }

  /** Overwrite the existing entry
   *
   * @param key
   * @param data
   */
  public void set(K key, L lable, T data)
  {
    if (mData == null) mData = new TreeMap<K, TreeMultimap<L, T>>();
    TreeMultimap<L, T> D = mData.get(key);
    if (D == null)
    {
      D = TreeMultimap.create();
      mData.put(key, D);
    }
    else D.clear();
    D.put(lable, data);
  }
  public void put(K key, Multimap<L, T> data)
  {
    if (mData == null) mData = new TreeMap<K, TreeMultimap<L, T>>();
    TreeMultimap<L, T> D = mData.get(key);
    if (D == null) {
      D = TreeMultimap.create();
      D.putAll(data);
      mData.put(key, D);
    }
    else D.putAll(data);
  }
  public void add(K key, L lable, List<T> data)
  {
    if (mData == null) mData = new TreeMap<K, TreeMultimap<L, T>>();
    TreeMultimap<L, T> D = mData.get(key);
    if (D == null)
    {
      D = TreeMultimap.create();
      D.putAll(lable, data);
      mData.put(key, D);
    }
    else
    {
      D.putAll(lable, data);
    }
  }
  public Collection<K> rowSet() { return mData.keySet(); }
  public Collection<K> keySet() { return mData.keySet(); }
  public Collection<T> values()
  {
    Collection<T> made = new ArrayList<T>();
    for (TreeMultimap<L,T> ts : getData().values())
      for (T t : ts.values()) made.add(t);
    return made;
  }
  public Collection<L> columnSet()
  {
    Collection<L> labels = new HashSet<L>();
    for (K k : keySet())
      labels.addAll(row(k).keySet());
    return labels;
  }

  public TreeMultimap<L,T> row(K key)
  {
    return mData==null && key!=null ? null : mData.get(key);
  }
  public SortedMap<K, TreeMultimap<L, T>> column(L s)
  {
    SortedMap<K, TreeMultimap<L, T>> cols = new TreeMap<>();
    for (K k : keySet())
      if (get(k, s) != null)
      {
        TreeMultimap<L, T> d = TreeMultimap.create();
        d.putAll(s, get(k, s));
        cols.put(k, d);
      }

    return cols;
  }
  public Collection<T> get(K key, L lable)
  {
    return mData == null ||
        mData.get(key) == null ? null : mData.get(key).get(lable);
  }

  public SortedMap<K, TreeMultimap<L, T>> getData() { return mData; }
  public void clear() { if (getData() != null) getData().clear(); }

  public boolean remove(K key1, K key2)
  {
    Set<K> removed = new HashSet<K>(getData().subMap(key1, key2).keySet());

    if (Tools.isSet(removed))
    {
      for (K key : removed) getData().keySet().remove(key);
      return true;
    }
    removed = null;

    return false;
  }
  public Collection<T> subset(Range<K> k, Range<L> l) { return subset(k.lowerEndpoint(), k.upperEndpoint(), l.lowerEndpoint(), l.upperEndpoint()); }
  public Collection<T> subset(K k0, K k1, L l0, L l1)
  {
    Collection<TreeMultimap<L, T>> slice = getData().subMap(k0, k1).values();
    Collection<T> values = new HashSet<T>();
    if (Tools.isSet(slice))
      for (TreeMultimap<L, T> sub : slice)
      {
        for (Collection<T> ts : sub.asMap().subMap(l0, l1).values()) values.addAll(ts);
      }

    return values;
  }

  @Override
  public MultiTreeTable<K,L,T> clone()
  {
    MultiTreeTable<K,L,T> cloned = MultiTreeTable.create(); // do we need to clone the indices?

    for (K key : mData.keySet())
      for (L lab : mData.get(key).keySet())
        cloned.putAll(key, lab, get(key,lab));

    return cloned;
  }

  public static <R extends Comparable, C extends Comparable, V extends Comparable> MultiTreeTable<R, C, V>
  create() { return new MultiTreeTable<R, C, V>(); }

  public static<R extends Comparable, C extends Comparable, V extends Comparable> MultiTreeTable<R, C, V>
  create(Comparator<? super R> keyComparator) {
    return new MultiTreeTable((Comparator) Preconditions.checkNotNull(keyComparator));
  }

}
