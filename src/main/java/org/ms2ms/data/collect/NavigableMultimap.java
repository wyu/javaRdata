package org.ms2ms.data.collect;

import org.ms2ms.Disposable;

import java.util.Collection;
import java.util.List;

/**
 * Created by yuw on 9/27/16.
 */
public interface NavigableMultimap<K, V> extends Disposable
{
  V put2(K key, V data);

  long keySize(K k1, K k2);

  long size();
  Collection<V> get(K k);
  Collection<K> keySet();


  List<V> subList(K k1, K k2);

  void clear();
}
