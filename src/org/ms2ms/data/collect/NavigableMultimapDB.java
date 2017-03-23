package org.ms2ms.data.collect;

import java.util.List;

/** A MapDB backed store to address the problem of excessively large heap and associated GC delay.
 *  Implementation is postponed until MapDB4 is ready so we could have multimap and better performance.
 *
 * Created by yuw on 3/22/17.
 */
public class NavigableMultimapDB<K extends Comparable, V> implements NavigableMultimap<K, V>
{
  @Override
  public V put2(K key, V data)
  {
    return null;
  }

  @Override
  public long keySize(K k1, K k2)
  {
    return 0;
  }

  @Override
  public long size()
  {
    return 0;
  }

  @Override
  public List<V> subList(K k1, K k2)
  {
    return null;
  }

  @Override
  public void clear()
  {

  }

  @Override
  public void dispose()
  {

  }
//  private int mListCapacity=500;
//  DB db = DBMaker
//      .memoryDirectDB()
//      .make();
//  private ConcurrentNavigableMap<K, ConcurrentLinkedQueue<V>> mData;
//
//  public NavigableMultimapDB() { super(); }
//  public NavigableMultimapDB(ConcurrentSkipListMap<K, ConcurrentLinkedQueue<V>> s)
//  { super(); mData=s; }
//  public NavigableMultimapDB(ConcurrentNavigableMap<K, ConcurrentLinkedQueue<V>> s)
//  {
//    super(); mData=s;
//  }
//
//  public                     V  put2(  K key, V data) { put(key,data); return data; }
//  public NavigableMultimapDB<K, V> putAll(K key, Collection<V> data) { initByKey(key).addAll(data); return this; }
//
//  public NavigableMultimapDB<K, V> subMap(K k1, K k2)
//  {
//    return mData!=null ? new NavigableMultimapDB<>(mData.subMap(k1, k2)) : null;
//  }
//  public boolean containsKey(K k1, K k2)
//  {
//    return mData!=null ? Tools.isSet(mData.subMap(k1, k2)) : false;
//  }
//  public long size(Range<K> r) { return size(r.lowerEndpoint(), r.upperEndpoint()); }
//  //  public long keySize(Range<K> s) { return keySize(s.lowerEndpoint(), s.upperEndpoint()); }
//  @Override
//  public long keySize(K k1, K k2)
//  {
//    if (mData!=null)
//    {
//      SortedMap<K, ConcurrentLinkedQueue<V>> sub = mData.subMap(k1, k2);
//      return (sub!=null?sub.keySet().size():0);
//    }
//    return 0;
//  }
//  public long size(K k1, K k2)
//  {
//    if (mData!=null)
//    {
//      SortedMap<K, ConcurrentLinkedQueue<V>> sub = mData.subMap(k1, k2);
//      if (sub!=null)
//      {
//        long s=0;
//        for (ConcurrentLinkedQueue<V> v : sub.values()) s+=v.size();
//
//        return s;
//      }
//    }
//    return 0;
//  }
//  @Override
//  public long size()
//  {
//    if (mData!=null)
//    {
//      long s=0;
//      for (ConcurrentLinkedQueue<V> v : mData.values()) s+=v.size();
//
//      return s;
//    }
//    return 0;
//  }
//  public List<V> subList(Range<K> range) { return range!=null?subList(range.lowerEndpoint(), range.upperEndpoint()):null; }
//  public List<V> subList(K k1, K k2)
//  {
//    if (mData!=null)
//    {
//      SortedMap<K, ConcurrentLinkedQueue<V>> sub = mData.subMap(k1, k2);
//      if (sub!=null)
//      {
//        List<V> results = new ArrayList<>(mListCapacity);
//        for (ConcurrentLinkedQueue<V> v : sub.values()) results.addAll(v);
//
//        return results;
//      }
//    }
//    return null;
//  }
//  public void clear() { if (mData!=null) mData.clear(); }
//
//  private ConcurrentLinkedQueue<V> initByKey(K key)
//  {
//    if (mData == null) mData = new ConcurrentSkipListMap<>();
//
//    ConcurrentLinkedQueue<V> D = mData.get(key);
//    if (D==null)
//    {
//      D = new ConcurrentLinkedQueue<>(); mData.put(key, D);
//    }
//    return D;
//  }
//  public NavigableMultimapDB<K, V> put(K key, V data)
//  {
////    initByKey(key).add(   data);
//    if (mData == null) mData = new ConcurrentSkipListMap<>();
//
//    ConcurrentLinkedQueue<V> D = mData.get(key);
//    if (D==null)
//    {
//      D = new ConcurrentLinkedQueue<>();
//      mData.put(key, D);
//    }
//    D.add(data);
//
//    return this;
//  }
//
//  public static <K extends Comparable, V> TreeListMultimap<K,V>
//  create() { return new TreeListMultimap<>(); }
//
//  @Override
//  public void dispose()
//  {
//    Tools.dispose(mData);
//  }
}