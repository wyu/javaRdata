package org.ms2ms.test;

import org.junit.Test;
import org.ms2ms.math.Histogram;
import org.ms2ms.math.Stats;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   1/18/15
 */
public class TestStats extends TestAbstract
{
  @Test
  public void newDist()
  {
    double[] data = new double[5];
    data[0]=1.1; data[1]=1.5; data[2]=3.2; data[3]=2.7; data[4]=1.75;
    Histogram dist = Stats.newHistogram(64, data);

    System.out.println(dist.getTotals());
  }
}
