package org.ms2ms.data.collect;

import java.util.Collection;
import java.util.List;

/** A more compact and efficient version of ConcurrentNavigableMultimap.
 *
 * Created by yuw on 12/14/16.
 */
public class ImmutableNavigableMultimap<K extends Comparable, V> implements NavigableMultimap<K, V>
{
  private double   mPrecision=1d, mKeyMin=0d, mKeyMax=2000d, mKeySpan=2000d;
  private int[]    mIndex;
  private double[] mKeys;
  private V[]      mValues;

  public ImmutableNavigableMultimap()
  {
    super();
  }
  public ImmutableNavigableMultimap(double precesion, double min, double max)
  {
    super();
    mPrecision=precesion; mKeyMin=min; mKeyMax=max;mKeySpan=max-min;
  }

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
  public Collection<V> get(K k)
  {
    return null;
  }

  @Override
  public Collection<K> keySet()
  {
    return null;
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
}
