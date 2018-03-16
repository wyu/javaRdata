package org.ms2ms.data.collect;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Range;
import com.google.common.collect.TreeMultiset;
import org.ms2ms.Disposable;
import org.ms2ms.utils.Tools;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by yuw on 8/8/16.
 */
public class HashListMultimap<K extends Comparable, V> implements Multimap<K, V>, Disposable
{
  private int mListCapacity=500;
  private Map<K, List<V>> mData;

  public HashListMultimap() { super(); }
  //  public TreeListMultimap(int s) { super(); mListCapacity=s; }
  public HashListMultimap(SortedMap<K, List<V>> s)
  { super(); mData=s; }

  public                     V  put2(K key, V data) { put(key,data); return data; }
  public HashListMultimap<K, V> putAll(K key, Collection<V> data) { initByKey(key).addAll(data); return this; }

  public Collection<V> get(K s) { return mData!=null?mData.get(s):null; }
  public Set<K> keySet() { return mData!=null?mData.keySet():null; }

  @Override
  public Multiset<K> keys() { return null; }

  @Override
  public Collection<V> values() { return null; }

  @Override
  public Collection<Map.Entry<K, V>> entries() { return null; }

  @Override
  public Map<K, Collection<V>> asMap() { return null; }

  public int size()
  {
    if (mData!=null)
    {
      int s=0;
      for (List<V> v : mData.values()) s+=v.size();
      return s;
    }
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean containsKey(@Nullable Object o) {
    return false;
  }

  @Override
  public boolean containsValue(@Nullable Object o) {
    return false;
  }

  @Override
  public boolean containsEntry(@Nullable Object o, @Nullable Object o1) {
    return false;
  }

  @Override
  public void clear() { if (mData!=null) mData.clear(); }

  private List<V> initByKey(K key)
  {
    if (mData == null) mData = new HashMap<>();

    List<V> D = mData.get(key);
    if (D==null)
    {
      D = new ArrayList<>(); mData.put(key, D);
    }
    return D;
  }
  public boolean put(K key, V data)
  {
//    initByKey(key).add(   data);
    if (mData == null) mData = new HashMap<>();

    List<V> D = mData.get(key);
    if (D==null)
    {
      D = new ArrayList<>();
      mData.put(key, D);
    }
    D.add(data);

    return true;
  }

  @Override
  public boolean remove(@Nullable Object o, @Nullable Object o1) {
    return false;
  }

  @Override
  public boolean putAll(@Nullable K k, Iterable<? extends V> iterable) {
    return false;
  }

  @Override
  public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
    return false;
  }

  @Override
  public Collection<V> replaceValues(@Nullable K k, Iterable<? extends V> iterable) {
    return null;
  }

  @Override
  public Collection<V> removeAll(@Nullable Object o) {
    return null;
  }

  public static <K extends Comparable, V> HashListMultimap<K,V>
  create() { return new HashListMultimap<>(); }

  @Override
  public void dispose()
  {
    mData=Tools.dispose(mData);
  }
}
