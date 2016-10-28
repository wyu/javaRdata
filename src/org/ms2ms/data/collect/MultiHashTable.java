package org.ms2ms.data.collect;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
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
public class MultiHashTable<K, L, T>
{
  private Map<K, Multimap<L, T>> mData;

  public MultiHashTable() { super(); mData = new HashMap<>(); }

  public long size()
  {
    long counts = 0;
    for (Multimap<L,T> ts : getData().values()) counts += ts.size();

    return counts;
  }
  private Multimap<L,T> newMultimap()
  {
    return HashMultimap.create();
  }
  public void put(K key, L lable, T data)
  {
    if (mData == null) mData = new TreeMap<>();
    Multimap<L, T> D = mData.get(key);
    if (D == null)
    {
      // to preseve multiple copies of the same objects. Oct 28, 2016
      D = newMultimap();
      mData.put(key, D);
    }
    D.put(lable, data);
//    System.out.println(D.size());
  }

  /** Overwrite the existing entry
   *
   * @param key
   * @param data
   */
  public void set(K key, L lable, T data)
  {
    if (mData == null) mData = new HashMap<K, Multimap<L, T>>();
    Multimap<L, T> D = mData.get(key);
    if (D == null)
    {
      // to preseve multiple copies of the same objects. Oct 28, 2016
      D = newMultimap();
//      D = HashMultimap.create();
      mData.put(key, D);
    }
    else D.clear();
    D.put(lable, data);
  }
  public void put(K key, Multimap<L, T> data)
  {
    if (mData == null) mData = new HashMap<>();
    Multimap<L, T> D = mData.get(key);
    if (D == null) {
      // to preseve multiple copies of the same objects. Oct 28, 2016
      D = newMultimap();
//      D = HashMultimap.create();
      D.putAll(data);
      mData.put(key, D);
    }
    else D.putAll(data);
  }
  public void add(K key, L lable, List<T> data)
  {
    if (mData == null) mData = new HashMap<>();
    Multimap<L, T> D = mData.get(key);
    if (D == null)
    {
      // to preseve multiple copies of the same objects. Oct 28, 2016
      D = newMultimap();
//      D = HashMultimap.create();
      D.putAll(lable, data);
      mData.put(key, D);
    }
    else
    {
      D.putAll(lable, data);
    }
  }
  public Collection<K> keySet() { return mData.keySet(); }
  public Collection<T> values()
  {
    Collection<T> made = new ArrayList<T>();
    for (Multimap<L,T> ts : getData().values())
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

  public Multimap<L,T> row(K key)
  {
    return mData == null ? null : mData.get(key);
  }
  public Map<K, Multimap<L, T>> column(L s)
  {
    Map<K, Multimap<L, T>> cols = new HashMap<>();
    for (K k : keySet())
      if (get(k, s) != null)
      {
        // to preseve multiple copies of the same objects. Oct 28, 2016
        Multimap<L, T> d = newMultimap();
//        Multimap<L, T> d = HashMultimap.create();
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

  public Map<K, Multimap<L, T>> getData() { return mData; }
  public void clear() { if (getData() != null) getData().clear(); }

  public static <R, C, V> MultiHashTable<R, C, V> create() { return new MultiHashTable<R, C, V>(); }
}
