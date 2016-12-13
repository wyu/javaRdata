package org.ms2ms.math;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.stat.inference.TestUtils;

import java.util.List;
import java.util.Random;

/**
 * Created by yuw on 11/29/16.
 */
public class Fitted
{
  private int N;
  private double r2, kai2, ftest;
  private SimpleCurveFitter fitter;
  private double[] params;

  public Fitted()                                { super(); }
  public Fitted(SimpleCurveFitter s)             { super(); fitter=s; }
  public Fitted(SimpleCurveFitter s, double[] p) { super(); fitter=s; params=p; }

  public Fitted   setN(                   int s) { N     =s; return this; }
  public Fitted   setR2(               double s) { r2    =s; return this; }
  public Fitted   setKai2(             double s) { kai2  =s; return this; }
  public Fitted   setFtest(            double s) { ftest =s; return this; }
  public Fitted   setParams(        double... s) { params=s; return this; }
  public Fitted   setFitter(SimpleCurveFitter s) { fitter=s; return this; }

  public int      getN()      { return N; }
  public double   getR2()     { return r2; }
  public double   getKai2()   { return kai2; }
  public double   getFtest()  { return ftest; }
  public double[] getParams() { return params; }

  public Fitted fit(List<WeightedObservedPoint> data)
  {
    params = fitter.fit(data);
    return this;
  }

  public Fitted fit(ParametricUnivariateFunction function, List<WeightedObservedPoint> data, double multiplier, int max_itr, double... params)
  {
    // start the fitting
    setFitter(SimpleCurveFitter.create(function, params));

    Random rnd = new Random(System.nanoTime());
    double[] best=null; double best_kai=Double.MAX_VALUE, sumRES=0, fx=0;
    for (int itr=0; itr<max_itr; itr++)
    {
      try
      {
        fit(data); sumRES=0;
        for (WeightedObservedPoint pt : data)
        {
          fx= function.value(pt.getX(), getParams());
          sumRES += (pt.getY()-fx)*(pt.getY()-fx);
        }
      }
      catch (Exception e)
      {
        sumRES=Double.MAX_VALUE;
      }
      // check the exit condition
      if (sumRES<best_kai) { best=getParams(); best_kai=sumRES; }
      // come up with the next starting values
      double[] param2 = params.clone();
      for (int i=0; i<params.length; i++)
      {
        double factor = rnd.nextDouble()*multiplier*(rnd.nextBoolean()?1d:-1d);
        param2[i] = params[i]*factor;
      }
      setFitter(SimpleCurveFitter.create(function, param2));
    }

    // get the mean-Y first
    double sum=0, sumTOT=0, n=0;
    for (WeightedObservedPoint pt : data) { n++; sum+=pt.getY(); }
    sum/=n;

    for (WeightedObservedPoint pt : data)
    {
      fx= function.value(pt.getX(), getParams());
      sumTOT += (pt.getY()-sum) * (pt.getY()-sum);
      sumRES += (pt.getY()-fx)*(pt.getY()-fx);
    }

    return setParams(best).setR2(1-sumRES/sumTOT);
  }
  public Fitted shrink_fit(int degree, List<WeightedObservedPoint> data, int min_len, double min_increment)
  {
    if (data==null || data.size()<degree+1) return null;

    double[] best_params=null; double best=0, best_r2=0; int best_N=0;
    do
    {
      fit(degree, data);
      if (best_params==null || (best-getKai2())/best>min_increment)
      {
        best_params=getParams(); best=getKai2(); best_N=data.size(); best_r2=getR2();
      }
      data.remove(data.size()-1);
    }
    while(data.size()>=min_len);

    return (best_params!=null ? setParams(best_params).setR2(best_r2).setN(best_N) : this);
  }
  // compute the y-value given a polynomial fit
  public double polynomial(double x)
  {
    double y=0d;
    for (int i=0; i<getParams().length; i++) y += getParams()[i]*Math.pow(x, i);

    return y;
  }
  public Fitted fit(int degree, List<WeightedObservedPoint> data)
  {
    PolynomialCurveFitter fitter=PolynomialCurveFitter.create(degree);

    if (data.size()>2) {
      setParams(fitter.fit(data));
      // compute the R2 coeff. get the mean-Y first
      double sum=0, sumTOT=0, sumRES=0, n=0, kai2=0;
      for (WeightedObservedPoint pt : data) {
        n++;
        sum+=pt.getY();
      }
      sum/=n;

      double[] observed = new double[data.size()], expected = new double[data.size()];
      // http://facweb.cs.depaul.edu/sjost/csc423/documents/f-test-reg.htm
      for (int i=0; i<data.size(); i++)
      {
        WeightedObservedPoint pt = data.get(i);
//        double fx=getParams()[1]*pt.getX()+getParams()[0];
        double fx=polynomial(pt.getX());
        observed[i]=pt.getY(); expected[i]=fx;

        sumTOT+=(pt.getY()-sum)*(pt.getY()-sum); // Corrected Sum of Squares for Model (SSM)
        sumRES+=(pt.getY()-fx)*(pt.getY()-fx);   // Sum of Squares for Error (SSE)
        kai2  +=(pt.getY()-fx)*(pt.getY()-fx)/Math.abs(fx);
        // n is the number of observations, p is the number of regression parameters.
        // F = MSM / MSE = (explained variance) / (unexplained variance) = (SSM/(p-1)) / (SSE/(n-p))
      }
      // not the classical definition of Kai2!!
      return setR2(1-sumRES/sumTOT).setN(data.size()).setKai2(kai2/(double )data.size()).setFtest((sumTOT/(getParams().length-1))/(sumRES/(data.size()-getParams().length)));
    }
    return null;
  }
}
