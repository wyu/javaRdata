package org.ms2ms.math;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.DiagonalMatrix;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by yuw on 11/28/16.
 */
public class EQ7921Fitter extends AbstractCurveFitter
{
  private static final PolynomialFunction.Parametric FUNCTION = new PolynomialFunction.Parametric();
  private final double[] initialGuess;
  private final int      maxIter;

  private EQ7921Fitter(double[] initialGuess, int maxIter)
  {
    this.initialGuess = initialGuess;
    this.maxIter = maxIter;
  }

  public static EQ7921Fitter create(int degree)
  {
    return new EQ7921Fitter(new double[degree + 1], 2147483647);
  }

  @Override
  protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> observations)
  {
    int len = observations.size();
    double[] target = new double[len];
    double[] weights = new double[len];
    int i = 0;

    for(Iterator model = observations.iterator(); model.hasNext(); ++i)
    {
      WeightedObservedPoint obs = (WeightedObservedPoint)model.next();
      target[ i] = obs.getY();
      weights[i] = obs.getWeight();
    }

    TheoreticalValuesFunction var8 = new TheoreticalValuesFunction(FUNCTION, observations);
    if(this.initialGuess == null)
    {
      throw new MathInternalError();
    }
    else
    {
      return (new LeastSquaresBuilder()).maxEvaluations(2147483647).maxIterations(this.maxIter).start(this.initialGuess).target(target).weight(new DiagonalMatrix(weights)).model(var8.getModelFunction(), var8.getModelFunctionJacobian()).build();
    }
  }
}
