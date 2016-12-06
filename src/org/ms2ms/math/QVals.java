package org.ms2ms.math;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.ms2ms.data.Point;
import org.ms2ms.utils.Strs;
import org.ms2ms.utils.Tools;

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
  public Double thresholdByFDR(double fdr)
  {
    double D=0d, N=0, score0=Double.MAX_VALUE;
    for (Double score : mCandidates.keySet())
    {
      D += Collections.frequency(mCandidates.get(score), true); N++;
      //System.out.println(getName()+"\t"+Tools.d2s(score, 2)+"\t"+D+"\t"+N+"\t"+Strs.toString(mCandidates.get(score), ";"));
      if (2*D/N<=fdr && score<score0) score0=score;
    }
    return score0;
  }

  public QVals model()
  {
//    System.out.println("\n"+mName+"\trank\tdecoys");
    List<Point>  points = new ArrayList<>();
    List<Double> scores = new ArrayList<>();

    double N=0d, D=0d; boolean hasDecoy=false;
    for (Double score : mCandidates.keySet())
    {
//      if (!hasDecoy && mCandidates.get(score).contains(true)) hasDecoy=true;
//      if (hasDecoy)
//      {
//        // frequency of the decoys with the score
//        D += Collections.frequency(mCandidates.get(score), true) / (double) mCandidates.get(score).size();
//        if (N < 40) {
//          points.add(new Point(++N, D));
//          System.out.println(score + "\t" + N + "\t" + D);
//        }
//        scores.add(score);
//      }

//      // frequency of the decoys with the score
      D += Collections.frequency(mCandidates.get(score), true)/(double )mCandidates.get(score).size();
      points.add(new Point(++N, D));
//      System.out.println(score+"\t"+N+"\t"+D);
      scores.add(score);
    }
    mThreshold = index2score(linear(points, 40), scores);

    return this;
  }
  // http://pubs.acs.org/doi/pdf/10.1021/pr070492f
  // Nonlinear Fitting Method for Determining Local False Discovery Rates from Decoy Database Searches, 2007, JPR
//  private Double nonlinearABI(Collection<Point> pts)
//  {
//    if (pts==null || pts.size()<5) return null;
//
//    QuadraticProblem problem = new QuadraticProblem();
//
//    problem.addPoint(1, 34.234064369);
//    problem.addPoint(2, 68.2681162306);
//    problem.addPoint(3, 118.6158990846);
//    problem.addPoint(4, 184.1381972386);
//    problem.addPoint(5, 266.5998779163);
//    problem.addPoint(6, 364.1477352516);
//    problem.addPoint(7, 478.0192260919);
//    problem.addPoint(8, 608.1409492707);
//    problem.addPoint(9, 754.5988686671);
//    problem.addPoint(10, 916.1288180859);
//
//    LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
//
//    final double[] weights = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
//
//    final double[] initialSolution = {1, 1, 1};
//
//    PointVectorValuePair optimum = optimizer.optimize(100,
//        problem,
//        problem.calculateTarget(),
//        weights,
//        initialSolution);
//
//    final double[] optimalValues = optimum.getPoint();
//
//    System.out.println("A: " + optimalValues[0]);
//    System.out.println("B: " + optimalValues[1]);
//    System.out.println("C: " + optimalValues[2]);
//
//    LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
//    CurveFitter fitter = new CurveFitter(optimizer);
//    fitter.addObservedPoint(2.805d, 0.6934785852953367d);
//    fitter.addObservedPoint(2.74333333333333d, 0.6306772025518496d);
//    fitter.addObservedPoint(1.655d, 0.9474675497289684);
//    fitter.addObservedPoint(1.725d, 0.9013594835804194d);
//
//    ParametricRealFunction sif = new SimpleInverseFunction();
//
//    double[] initialguess1 = new double[1];
//    initialguess1[0] = 1.0d;
//    Assert.assertEquals(1, fitter.fit(sif, initialguess1).length);
//
//    double[] initialguess2 = new double[2];
//    initialguess2[0] = 1.0d;
//    initialguess2[1] = .5d;
//    Assert.assertEquals(2, fitter.fit(sif, initialguess2).length);
//
//  }

  private Double quadratic(Collection<Point> pts)
  {
    if (pts!=null && pts.size()>4)
    {
      List<WeightedObservedPoint> points = new ArrayList<>(pts.size());
      for (Point pt : pts) points.add(new WeightedObservedPoint(1,pt.getX(), pt.getY()));

      // fit a polynomial curve
      PolynomialCurveFitter quad = PolynomialCurveFitter.create(2);
      mCoeffs = quad.fit(points);

      double d = (mCoeffs[1]*mCoeffs[1]-4*mCoeffs[0]*mCoeffs[2]),
            x1 = (-1*mCoeffs[1]-Math.sqrt(d))/(2*mCoeffs[2]), x2 = (-1*mCoeffs[1]+Math.sqrt(d))/(2*mCoeffs[2]);

      // picking the lowest of the roots
      return Math.min(x1, x2);
//      // inter/ex-polating for the critical score
//      int left=(int )Math.floor(mThreshold), right=(int )Math.ceil(mThreshold);
//      Point x = Points.interpolate(new Point(left, scores.get(left)), new Point(right, scores.get(right)), mThreshold);
//      mThreshold = x.getY();
    }
    return null;
  }
  private Double extremeCum(Collection<Point> pts)
  {
    if (pts!=null && pts.size()>4)
    {

      List<WeightedObservedPoint> points = new ArrayList<>(pts.size());
      for (Point pt : pts) points.add(new WeightedObservedPoint(1,pt.getX(), pt.getY()));

      // fit a polynomial curve
      PolynomialCurveFitter quad = PolynomialCurveFitter.create(2);
      mCoeffs = quad.fit(points);

      double d = (mCoeffs[1]*mCoeffs[1]-4*mCoeffs[0]*mCoeffs[2]),
          x1 = (-1*mCoeffs[1]-Math.sqrt(d))/(2*mCoeffs[2]), x2 = (-1*mCoeffs[1]+Math.sqrt(d))/(2*mCoeffs[2]);

      // picking the lowest of the roots
      return Math.min(x1, x2);
//      // inter/ex-polating for the critical score
//      int left=(int )Math.floor(mThreshold), right=(int )Math.ceil(mThreshold);
//      Point x = Points.interpolate(new Point(left, scores.get(left)), new Point(right, scores.get(right)), mThreshold);
//      mThreshold = x.getY();
    }
    return null;
  }
  private Double linear(Collection<Point> pts, int limit)
  {
    if (pts!=null && pts.size()>2)
    {
      // settle for a linear fit
      SimpleRegression linear = new SimpleRegression(true);
      for (Point pt : pts)
        if (linear.getN()<limit) linear.addData(pt.getX(), pt.getY());

      return -1*linear.getIntercept()/linear.getSlope();
    }
    return null;
  }

  private Double index2score(Double pos, List<Double> scores)
  {
    if (pos!=null)
    {
      int left=(int )Math.floor(pos), right=(int )Math.ceil(pos);
      if (left<0) { left=0; right=1; }

      Point  x = Points.interpolate(new Point(left, scores.get(left)), new Point(right, scores.get(right)), pos);
      return x.getY();
    }
    return null;
  }

//  private static class ABIqValProblem
//      implements DifferentiableMultivariateVectorFunction, Serializable {
//
//    private static final long serialVersionUID = 7072187082052755854L;
//    private List<Double> x;
//    private List<Double> y;
//
//    public ABIqValProblem()
//    {
//      x = new ArrayList<>();
//      y = new ArrayList<>();
//    }
//
//    public void addPoint(double x, double y)
//    {
//      this.x.add(x); this.y.add(y);
//    }
//
//    public double[] calculateTarget()
//    {
//      double[] target = new double[y.size()];
//      for (int i = 0; i < y.size(); i++) target[i] = y.get(i).doubleValue();
//
//      return target;
//    }
//
//    private double[][] jacobian(double[] variables)
//    {
//      double[][] jacobian = new double[x.size()][3];
//      for (int i = 0; i < jacobian.length; ++i)
//      {
//        jacobian[i][0] = x.get(i) * x.get(i);
//        jacobian[i][1] = x.get(i);
//        jacobian[i][2] = 1.0;
//      }
//      return jacobian;
//    }
//
//    public double[] value(double[] variables) {
//      double[] values = new double[x.size()];
//      for (int i = 0; i < values.length; ++i) {
//        values[i] = (variables[0] * x.get(i) + variables[1]) * x.get(i) + variables[2];
//      }
//      return values;
//    }
//
//    public MultivariateMatrixFunction jacobian() {
//      return new MultivariateMatrixFunction() {
//        private static final long serialVersionUID = -8673650298627399464L;
//        public double[][] value(double[] point) {
//          return jacobian(point);
//        }
//      };
//    }
//  }
}
