package org.ms2ms.math.clustering;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.ms2ms.math.Stats;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   1/28/15
 */
public class SlopeConvergenceDistance implements DistanceMeasure
{
  /** Serializable version identifier. */
  private static final long serialVersionUID = -6972277381587032228L;

  /** {@inheritDoc} */
  public double compute(double[] a, double[] b)
  {
    // looking for slopes (b-a) that are close together
    double[] ab = new double[a.length];
    for (int i = 0; i < a.length; i++)
      if (Stats.isSet(a[i]) && Stats.isSet(b[i])) ab[i] = a[i]-b[i];

    return Stats.stdev(ab, ab.length);
  }
}
