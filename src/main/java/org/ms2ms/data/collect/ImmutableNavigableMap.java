package org.ms2ms.data.collect;

import com.google.common.collect.Range;
import org.ms2ms.utils.Disposable;
import org.ms2ms.utils.Tools;

import java.lang.reflect.Array;
import java.util.*;

/** A simple collection to optimize for range query by a double key
 * Created by yuw on 10/8/16.
 */
public class ImmutableNavigableMap<V> implements Disposable
{
  private double   mPrecision=1d, mKeyMin=0d, mKeyMax=2000d, mKeySpan=2000d;
  private int[]    mIndex;
  private double[] mKeys;
  private V[]      mValues;

  public ImmutableNavigableMap()
  {
    super();
  }
  public ImmutableNavigableMap(double precesion, double min, double max)
  {
    super();
    mPrecision=precesion; mKeyMin=min; mKeyMax=max;mKeySpan=max-min;
  }

  private void init()
  {
    int lens =(int )(mKeySpan/mPrecision)+1;
    // setup the arrays
    mIndex = new int[   lens]; Arrays.fill(mIndex, -1);
  }
  public    int[] getIndex() { return mIndex; }
  public double[] getKeys()  { return mKeys; }
  public      V[] getVals()  { return mValues; }

  public ImmutableNavigableMap<V> of(SortedMap<Double, V> map, double precision)
  {
    if (Tools.isSet(map))
    {
      mKeyMin=Collections.min(map.keySet());
      mKeyMax=Collections.max(map.keySet());
      mPrecision=precision; mKeySpan=mKeyMax-mKeyMin;

      init();

      mKeys   = new double[map.size()]; Arrays.fill(mKeys,  -1);
      mValues = (V[] )Array.newInstance(Tools.front(map.values()).getClass(), map.size());

      int k=0;
      for (Map.Entry<Double, V> E : map.entrySet())
      {
        int idx = index(E.getKey());
        // only update the index if not set already or larger than this one.
        // When multiple entries share the same key, keep the smallest one
        if (mIndex[idx]==-1 || mKeys[mIndex[idx]]>E.getKey()) mIndex[idx]=k;
        mKeys[  k]=E.getKey();
        mValues[k]=E.getValue();
        k++;
      }
    }

    return this;
  }
  public int size() { return mKeys!=null?mKeys.length:0; }
  public double[] fetchKeys(int[] pos) { return                 Arrays.copyOfRange(mKeys,   pos[0], pos[1]); }
  public      V[] fetchVals(int[] pos) { return pos!=null?(V[] )Arrays.copyOfRange(mValues, pos[0], pos[1]):null; }

  public int[] query(Range<Double> R) { return query(R.lowerEndpoint(), R.upperEndpoint()); }
  public int[] query(double[] R) { return query(R[0], R[1]); }
  public int[] query(double k0, double k1)
  {
    int start=-1, i0=Math.max(0,index(k0));

    // look for the valid start
    for (int i=i0; i<mIndex.length; i++)
      if (mIndex[i]>=0) { start=mIndex[i]; break; }

    if (start>=0)
    {
      int j0=-1, j1=-1;
      for (int k=start; k<mKeys.length; k++)
      {
        if (j0==-1 && mKeys[k]>=k0)   j0=k;
        if (          mKeys[k]> k1) { j1=k; break; }
      }
      if (j0>=0 && j1>j0) return new int[] {j0, j1};
    }

    return null;
  }
  public int query4counts(double k0, double k1)
  {
    int start=-1, i0=Math.max(0,index(k0));

    // look for the valid start
    for (int i=i0; i<mIndex.length; i++)
      if (mIndex[i]>=0) { start=mIndex[i]; break; }

    if (start>=0)
    {
      int j0=-1, j1=-1;
      for (int k=start; k<mKeys.length; k++)
      {
        if (j0==-1 && mKeys[k]>=k0)   j0=k;
        if (          mKeys[k]> k1) { j1=k; break; }
      }
      if (j0>=0 && j1>j0) return j1-j0;
    }

    return 0;
  }

  public int index(double k) { return (int )((k-mKeyMin)*(mIndex.length-1)/mKeySpan); }
  public int start(int i0)
  {
    // look for the valid start
    for (int i=i0; i<mIndex.length; i++)
      if (mIndex[i]>=0) return mIndex[i];

    return -1;
  }
  public static <V> ImmutableNavigableMap<V> build(SortedMap<Double, V> map, double precision)
  {
    if (!Tools.isSet(map)) return null;

    ImmutableNavigableMap made = new ImmutableNavigableMap();
    return made.of(map, precision);
  }

  @Override
  public void dispose()
  {
    mIndex=null; mKeys=null; mValues=null;
  }
}
