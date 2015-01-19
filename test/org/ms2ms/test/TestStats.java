package org.ms2ms.test;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.junit.Test;
import org.ms2ms.math.Stats;

import java.util.Arrays;

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
    EmpiricalDistribution dist = Stats.newDistribution(data);

    System.out.println(dist.getNumericalMean());
  }
}
