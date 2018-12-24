package org.ms2ms.data.collect;

import com.google.common.collect.Range;
import org.ms2ms.utils.Tools;

import java.util.*;

/**
 * Created by yuw on 8/8/16.
 */
public class TreeListMultimap<K extends Comparable, V> implements NavigableMultimap<K, V>
{
  private int mListCapacity=500;
  private SortedMap<K, List<V>> mData;

  public TreeListMultimap() { super(); }
//  public TreeListMultimap(int s) { super(); mListCapacity=s; }
  public TreeListMultimap(SortedMap<K, List<V>> s)
  { super(); mData=s; }

  @Override
  public                     V  put2(K key, V data) { put(key,data); return data; }
  public TreeListMultimap<K, V> putAll(K key, Collection<V> data) { initByKey(key).addAll(data); return this; }

  public TreeListMultimap<K, V> subMap(K k1, K k2)
  {
    return mData!=null ? new TreeListMultimap<>(mData.subMap(k1, k2)) : null;
  }
  public Collection<V> get(K s) { return mData!=null?mData.get(s):null; }
  public Set<K> keySet() { return mData!=null?mData.keySet():null; }
  public boolean containsKey(K k1, K k2)
  {
    return mData!=null ? Tools.isSet(mData.subMap(k1, k2)) : false;
  }
  public long size(Range<K> r) { return size(r.lowerEndpoint(), r.upperEndpoint()); }
//  public long keySize(Range<K> s) { return keySize(s.lowerEndpoint(), s.upperEndpoint()); }
  public long keySize(K k1, K k2)
  {
    if (mData!=null)
    {
      SortedMap<K, List<V>> sub = mData.subMap(k1, k2);
      return (sub!=null?sub.keySet().size():0);
    }
    return 0;
  }
  public long size(K k1, K k2)
  {
    if (mData!=null)
    {
      SortedMap<K, List<V>> sub = mData.subMap(k1, k2);
      if (sub!=null)
      {
        long s=0;
        for (List<V> v : sub.values()) s+=v.size();

        return s;
      }
    }
    return 0;
  }
  public long size()
  {
    if (mData!=null)
    {
      long s=0;
      for (List<V> v : mData.values()) s+=v.size();
      return s;
    }
    return 0;
  }
  @Override
  public List<V> subList(K k1, K k2)
  {
    if (mData!=null)
    {
      SortedMap<K, List<V>> sub = mData.subMap(k1, k2);
      if (sub!=null)
      {
        List<V> results = new ArrayList<>(mListCapacity);
        for (List<V> v : sub.values()) results.addAll(v);

        return results;
      }
    }
    return null;
  }
  @Override
  public void clear() { if (mData!=null) mData.clear(); }

  private List<V> initByKey(K key)
  {
    if (mData == null) mData = new TreeMap<>();

    List<V> D = mData.get(key);
    if (D==null)
    {
      D = new ArrayList<>(); mData.put(key, D);
    }
    return D;
  }
  public TreeListMultimap<K, V> put(K key, V data)
  {
//    initByKey(key).add(   data);
    if (mData == null) mData = new TreeMap<>();

    List<V> D = mData.get(key);
    if (D==null)
    {
      D = new ArrayList<>();
      mData.put(key, D);
    }
    D.add(data);

    return this;
  }

  public static <K extends Comparable, V> TreeListMultimap<K,V>
  create() { return new TreeListMultimap<>(); }

  @Override
  public void dispose()
  {
    mData=(SortedMap )Tools.dispose(mData);
  }
}
