package org.ms2ms.math.clustering;

import org.apache.commons.math3.ml.clustering.Clusterable;

import java.util.List;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   1/28/15
 */
public class ParCoodsClusterable implements Clusterable
{
  private double[] points;
  private String mCol;

  public ParCoodsClusterable(String col, double[] s) { this.points = s; mCol=col; }

  public String getCol() { return mCol; }
  public double[] getPoint() { return points;  }
}
