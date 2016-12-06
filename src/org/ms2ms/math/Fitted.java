package org.ms2ms.math;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import java.util.List;
import java.util.Random;

/**
 * Created by yuw on 11/29/16.
 */
public class Fitted
{
  private double r2;
  private SimpleCurveFitter fitter;
  private double[] params;

  public Fitted()                                { super(); }
  public Fitted(SimpleCurveFitter s)             { super(); fitter=s; }
  public Fitted(SimpleCurveFitter s, double[] p) { super(); fitter=s; params=p; }

  public Fitted   setR2(double s) { r2=s; return this; }
  public Fitted   setParams(double... s) { params=s; return this; }
  public Fitted   setFitter(SimpleCurveFitter s) { fitter=s; return this; }

  public double   getR2() { return r2; }
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
  public Fitted fit(int degree, List<WeightedObservedPoint> data)
  {
    PolynomialCurveFitter fitter=PolynomialCurveFitter.create(degree);

    if (data.size()>2) {
      setParams(fitter.fit(data));
      // compute the R2 coeff. get the mean-Y first
      double sum=0, sumTOT=0, sumRES=0, n=0;
      for (WeightedObservedPoint pt : data) {
        n++;
        sum+=pt.getY();
      }
      sum/=n;

      for (WeightedObservedPoint pt : data) {
        double fx=getParams()[1]*pt.getX()+getParams()[0];
        sumTOT+=(pt.getY()-sum)*(pt.getY()-sum);
        sumRES+=(pt.getY()-fx)*(pt.getY()-fx);
      }
      return setR2(1-sumRES/sumTOT);
    }
    return null;
  }
}
