package org.ms2ms.math;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.ms2ms.data.Point;

import java.util.*;

/**
 * Created by yuw on 11/10/16.
 */
public class QVals
{
  private String mName;
  private Double mRoot, mThreshold;
  private Map<Double, Collection<Boolean>> mCandidates = new TreeMap<>(Collections.reverseOrder());
  private double[] mCoeffs=null;

  public QVals()         { super(); }
  public QVals(String s) { super(); mName=s; }

  public String getName() { return mName; }
  public Double getThreshold() { return mThreshold; }

  public QVals put(Double score, Boolean decoy)
  {
    Collection<Boolean> vals = mCandidates.get(score);
    if (vals==null)
    {
      vals = new ArrayList<>(); mCandidates.put(score, vals);
    }
    vals.add(decoy); return this;
  }

  // http://pubs.acs.org/doi/pdf/10.1021/pr070492f
  // Nonlinear Fitting Method for Determining Local False Discovery Rates from Decoy Database Searches, 2007, JPR
  public QVals model()
  {
//    System.out.println("\n"+mName+"\trank\tdecoys");
    List<WeightedObservedPoint> points = new ArrayList<>();
    List<Double>                scores = new ArrayList<>();
    double N=0d, D=0d; boolean hasDecoy=false;
    for (Double score : mCandidates.keySet())
    {
      if (!hasDecoy && mCandidates.get(score).contains(true)) hasDecoy=true;
      if (hasDecoy)
      {
        // frequency of the decoys with the score
        D += Collections.frequency(mCandidates.get(score), true)/(double )mCandidates.get(score).size();
        if (N<40)
        {
          points.add(new WeightedObservedPoint(1,++N, D));
        }
        scores.add(score);
//      System.out.println(score+"\t"+N+"\t"+D);
      }
    }
    if (points.size()>5)
    {
      // fit a polynomial curve
      PolynomialCurveFitter quad = PolynomialCurveFitter.create(2);
      mCoeffs = quad.fit(points);

      double d = (mCoeffs[1]*mCoeffs[1]-4*mCoeffs[0]*mCoeffs[2]),
          x1 = (-1*mCoeffs[1]-Math.sqrt(d))/(2*mCoeffs[2]), x2 = (-1*mCoeffs[1]+Math.sqrt(d))/(2*mCoeffs[2]);

      mThreshold = Math.max(0, Math.min(x1, x2));

//    if (     mThreshold<=0)             mThreshold=scores.get(0);
      if (mThreshold>=scores.size()) mThreshold=scores.get(scores.size()-1);
      else
      {
        int left=(int )Math.floor(mThreshold), right=(int )Math.ceil(mThreshold);
        Point x = Points.interpolate(new Point(left, scores.get(left)), new Point(right, scores.get(right)), mThreshold);
        mThreshold = x.getY();
      }
    }
    else if (points.size()>2)
    {
      // settle for a linear fit
      SimpleRegression linear = new SimpleRegression(true);
      for (WeightedObservedPoint pt : points) linear.addData(pt.getX(), pt.getY());

      mThreshold = -1*linear.getIntercept()/linear.getSlope();
    }

    return this;
  }
}
