package org.ms2ms.data.collect;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import org.ms2ms.Disposable;

import java.util.*;

/**
 * Created by yuw on 10/28/16.
 */
public class ListMultiTable<K, L, T> implements Disposable
{
  private Table<K, L, List<T>> mData;

  public ListMultiTable() { super(); mData = HashBasedTable.create(); }

  public long size()
  {
    long counts = 0;
    if (mData!=null)
      for (List<T> ts : mData.values()) counts += ts.size();

    return counts;
  }
  public void put(K key, L lable, T data)
  {
    if (mData==null) mData = HashBasedTable.create();
    List<T> D = mData.get(key, lable);
    if (D==null)
    {
      // to preseve multiple copies of the same objects. Oct 28, 2016
      D = new ArrayList<T>();
      mData.put(key, lable, D);
    }
    D.add(data);
  }

//  /** Overwrite the existing entry
//   *
//   * @param key
//   * @param data
//   */
//  public void set(K key, L lable, T data)
//  {
//    if (mData == null) mData = new HashMap<K, Multimap<L, T>>();
//    Multimap<L, T> D = mData.get(key);
//    if (D == null)
//    {
//      // to preseve multiple copies of the same objects. Oct 28, 2016
//      D = newMultimap();
////      D = HashMultimap.create();
//      mData.put(key, D);
//    }
//    else D.clear();
//    D.put(lable, data);
//  }
//  public void put(K key, Multimap<L, T> data)
//  {
//    if (mData == null) mData = new HashMap<>();
//    Multimap<L, T> D = mData.get(key);
//    if (D == null) {
//      // to preseve multiple copies of the same objects. Oct 28, 2016
//      D = newMultimap();
////      D = HashMultimap.create();
//      D.putAll(data);
//      mData.put(key, D);
//    }
//    else D.putAll(data);
//  }
//  public void add(K key, L lable, List<T> data)
//  {
//    if (mData == null) mData = new HashMap<>();
//    Multimap<L, T> D = mData.get(key);
//    if (D == null)
//    {
//      // to preseve multiple copies of the same objects. Oct 28, 2016
//      D = newMultimap();
////      D = HashMultimap.create();
//      D.putAll(lable, data);
//      mData.put(key, D);
//    }
//    else
//    {
//      D.putAll(lable, data);
//    }
//  }
  public Collection<K> rowSet() { return mData.rowKeySet(); }
  public Collection<L> columnSet() { return mData.columnKeySet(); }
  public Collection<T> values()
  {
    Collection<T> made = new ArrayList<T>();
    for (List<T> ts : mData.values()) made.addAll(ts);
    return made;
  }

  public Map<L,List<T>> row(K key)
  {
    return mData==null?null : mData.row(key);
  }
  public Map<K, List<T>> column(L s)
  {
    return mData!=null?mData.column(s):null;
  }
  public Collection<T> get(K key, L lable)
  {
    Collection<T> r = (mData==null?null:mData.get(key, lable));
    return r!=null ? r: new ArrayList<T>();
  }

  public Table<K, L, List<T>> getData() { return mData; }
  public void clear() { if (getData() != null) getData().clear(); }

  public static <R, C, V> ListMultiTable<R, C, V> create() { return new ListMultiTable<R, C, V>(); }

  @Override
  public void dispose()
  {
    if (mData!=null) mData.clear();
    mData=null;
  }
}
