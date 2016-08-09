package org.ms2ms.data.collect;

import org.ms2ms.utils.Tools;

import java.util.*;

/**
 * Created by yuw on 8/8/16.
 */
public class TreeListMultimap<K extends Comparable, V>
{
  private int mListCapacity=500;
  private SortedMap<K, List<V>> mData;

  public TreeListMultimap() { super(); }
  public TreeListMultimap(SortedMap<K, List<V>> s)
  { super(); mData=s; }

  public                     V  put2(  K key, V data) { initByKey(key).add(   data); return data; }
  public TreeListMultimap<K, V> put(   K key, V             data) { initByKey(key).add(   data); return this; }
  public TreeListMultimap<K, V> putAll(K key, Collection<V> data) { initByKey(key).addAll(data); return this; }

  public TreeListMultimap<K, V> subMap(K k1, K k2)
  {
    return mData!=null ? new TreeListMultimap<>(mData.subMap(k1, k2)) : null;
  }
  public boolean containsKey(K k1, K k2)
  {
    return mData!=null ? Tools.isSet(mData.subMap(k1, k2)) : false;
  }
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
  public static <K extends Comparable, V> TreeListMultimap<K,V>
  create() { return new TreeListMultimap<>(); }

}
