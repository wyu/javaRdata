package org.ms2ms.data.collect;

import com.google.common.collect.Range;
import org.ms2ms.Disposable;
import org.ms2ms.data.Binary;
import org.ms2ms.utils.IOs;
import org.ms2ms.utils.Tools;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by yuw on 9/26/16.
 */
public class ConcurrentNavigableMultimap<K extends Comparable, V> implements NavigableMultimap<K, V>
{
  private int mListCapacity=500;
  private ConcurrentNavigableMap<K, ConcurrentLinkedQueue<V>> mData;

  public ConcurrentNavigableMultimap() { super(); }
  public ConcurrentNavigableMultimap(ConcurrentSkipListMap<K, ConcurrentLinkedQueue<V>> s)
  { super(); mData=s; }
  public ConcurrentNavigableMultimap(ConcurrentNavigableMap<K, ConcurrentLinkedQueue<V>> s)
  {
    super(); mData=s;
  }

  public                     V  put2(  K key, V data) { put(key,data); return data; }
  public ConcurrentNavigableMultimap<K, V> putAll(K key, Collection<V> data) { initByKey(key).addAll(data); return this; }

  public ConcurrentNavigableMultimap<K, V> subMap(K k1, K k2)
  {
    return mData!=null ? new ConcurrentNavigableMultimap<>(mData.subMap(k1, k2)) : null;
  }
  public boolean containsKey(K k1, K k2)
  {
    return mData!=null ? Tools.isSet(mData.subMap(k1, k2)) : false;
  }
  public long size(Range<K> r) { return size(r.lowerEndpoint(), r.upperEndpoint()); }
  //  public long keySize(Range<K> s) { return keySize(s.lowerEndpoint(), s.upperEndpoint()); }
  @Override
  public long keySize(K k1, K k2)
  {
    if (mData!=null)
    {
      SortedMap<K, ConcurrentLinkedQueue<V>> sub = mData.subMap(k1, k2);
      return (sub!=null?sub.keySet().size():0);
    }
    return 0;
  }
  public long size(K k1, K k2)
  {
    if (mData!=null)
    {
      SortedMap<K, ConcurrentLinkedQueue<V>> sub = mData.subMap(k1, k2);
      if (sub!=null)
      {
        long s=0;
        for (ConcurrentLinkedQueue<V> v : sub.values()) s+=v.size();

        return s;
      }
    }
    return 0;
  }
  @Override
  public long size()
  {
    if (mData!=null)
    {
      long s=0;
      for (ConcurrentLinkedQueue<V> v : mData.values()) s+=v.size();

      return s;
    }
    return 0;
  }
  public List<V> subList(Range<K> range) { return range!=null?subList(range.lowerEndpoint(), range.upperEndpoint()):null; }
  public List<V> subList(K k1, K k2)
  {
    if (mData!=null)
    {
      SortedMap<K, ConcurrentLinkedQueue<V>> sub = mData.subMap(k1, k2);
      if (sub!=null)
      {
        List<V> results = new ArrayList<>(mListCapacity);
        for (ConcurrentLinkedQueue<V> v : sub.values()) results.addAll(v);

        return results;
      }
    }
    return null;
  }
  public void clear() { if (mData!=null) mData.clear(); }

  private ConcurrentLinkedQueue<V> initByKey(K key)
  {
    if (mData == null) mData = new ConcurrentSkipListMap<>();

    ConcurrentLinkedQueue<V> D = mData.get(key);
    if (D==null)
    {
      D = new ConcurrentLinkedQueue<>(); mData.put(key, D);
    }
    return D;
  }
  public ConcurrentNavigableMultimap<K, V> put(K key, V data)
  {
//    initByKey(key).add(   data);
    if (mData == null) mData = new ConcurrentSkipListMap<>();

    ConcurrentLinkedQueue<V> D = mData.get(key);
    if (D==null)
    {
      D = new ConcurrentLinkedQueue<>();
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
    Tools.dispose(mData);
  }
}