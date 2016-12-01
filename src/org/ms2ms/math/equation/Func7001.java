package org.ms2ms.math.equation;

/** Implementation of eq# 7921 in TableCurve 2D. Created by yuw on 11/29/16. **
 *  modelled after PolynomialFunction from Apache Math3
 */

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.ms2ms.utils.Tools;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Func7001 implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction, Serializable
{
  private static final long serialVersionUID = -7726511984220295583L;
  private final double[] coefficients;
  // y = (a + cx) / (1 + bx), coeffs = {a, b, c}
  public Func7001(double[] c) throws NullArgumentException, NoDataException
  {
    MathUtils.checkNotNull(c);
    int n = c.length;
    this.coefficients = new double[n];
    System.arraycopy(c, 0, this.coefficients, 0, n);
  }

  public double value(double x) { return evaluate(this.coefficients, x); }

  public double[] getCoefficients() { return this.coefficients.clone(); }

  protected static double evaluate(double[] coeffs, double argument) throws NullArgumentException, NoDataException
  {
    if (coeffs!=null && coeffs.length>2)
      return (coeffs[0]+coeffs[2]*argument) / (1d+coeffs[1]*argument);

    throw new NoDataException();
  }

  public DerivativeStructure value(DerivativeStructure t) throws NullArgumentException, NoDataException
  {
//    MathUtils.checkNotNull(this.coefficients);
//    int n = this.coefficients.length;
//    if(n == 0) {
//      throw new NoDataException(LocalizedFormats.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
//    } else {
//      DerivativeStructure result = new DerivativeStructure(t.getFreeParameters(), t.getOrder(), this.coefficients[n - 1]);
//
//      for(int j = n - 2; j >= 0; --j) {
//        result = result.multiply(t).add(this.coefficients[j]);
//      }
//
//      return result;
//    }
    return null;
  }

  public Func7001 add(Func7001 p)
  {
    int lowLength  = FastMath.min(this.coefficients.length, p.coefficients.length);
    int highLength = FastMath.max(this.coefficients.length, p.coefficients.length);
    double[] newCoefficients = new double[highLength];

    for(int i = 0; i < lowLength; ++i) {
      newCoefficients[i] = this.coefficients[i] + p.coefficients[i];
    }

    System.arraycopy(this.coefficients.length < p.coefficients.length?p.coefficients:this.coefficients, lowLength, newCoefficients, lowLength, highLength - lowLength);
    return new Func7001(newCoefficients);
  }

//  public Func7401 subtract(Func7401 p) {
//    int lowLength = FastMath.min(this.coefficients.length, p.coefficients.length);
//    int highLength = FastMath.max(this.coefficients.length, p.coefficients.length);
//    double[] newCoefficients = new double[highLength];
//
//    int i;
//    for(i = 0; i < lowLength; ++i) {
//      newCoefficients[i] = this.coefficients[i] - p.coefficients[i];
//    }
//
//    if(this.coefficients.length < p.coefficients.length) {
//      for(i = lowLength; i < highLength; ++i) {
//        newCoefficients[i] = -p.coefficients[i];
//      }
//    } else {
//      System.arraycopy(this.coefficients, lowLength, newCoefficients, lowLength, highLength - lowLength);
//    }
//
//    return new Func7401(newCoefficients);
//  }
//
//  public Func7401 negate() {
//    double[] newCoefficients = new double[this.coefficients.length];
//
//    for(int i = 0; i < this.coefficients.length; ++i) {
//      newCoefficients[i] = -this.coefficients[i];
//    }
//
//    return new Func7401(newCoefficients);
//  }
//
//  public Func7401 multiply(Func7401 p) {
//    double[] newCoefficients = new double[this.coefficients.length + p.coefficients.length - 1];
//
//    for(int i = 0; i < newCoefficients.length; ++i) {
//      newCoefficients[i] = 0.0D;
//
//      for(int j = FastMath.max(0, i + 1 - p.coefficients.length); j < FastMath.min(this.coefficients.length, i + 1); ++j) {
//        newCoefficients[i] += this.coefficients[j] * p.coefficients[i - j];
//      }
//    }
//
//    return new Func7401(newCoefficients);
//  }

//  protected static double[] differentiate(double[] coefficients) throws NullArgumentException, NoDataException {
//    MathUtils.checkNotNull(coefficients);
//    int n = coefficients.length;
//    if(n == 0) {
//      throw new NoDataException(LocalizedFormats.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
//    } else if(n == 1) {
//      return new double[]{0.0D};
//    } else {
//      double[] result = new double[n - 1];
//
//      for(int i = n - 1; i > 0; --i) {
//        result[i - 1] = (double)i * coefficients[i];
//      }
//
//      return result;
//    }
//  }

//  public Func7401 EQ7921Derivative() {
//    return new Func7401(differentiate(this.coefficients));
//  }
//
  public UnivariateFunction derivative()
  {
//    return this.EQ7921Derivative();
    return null;
  }

  public String toString()
  {
    StringBuilder s = new StringBuilder();

    s.append("y = ("+ Tools.d2s(getCoefficients()[0], 3) + (getCoefficients()[2]<0?" - ":" + ") + Tools.d2s(Math.abs(getCoefficients()[2]), 3) + "*x)");
    s.append(" / (1" + (getCoefficients()[1]>0?" + ":" - ") + Tools.d2s(Math.abs(getCoefficients()[1]), 3) + "*x)");

    return s.toString();
  }

  public int hashCode()
  {
    boolean prime = true;
    byte result = 1;
    int result1 = 31 * result + Arrays.hashCode(this.coefficients);
    return result1;
  }

  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    } else if(!(obj instanceof Func7001)) {
      return false;
    } else {
      Func7001 other = (Func7001)obj;
      return Arrays.equals(this.coefficients, other.coefficients);
    }
  }

  public static class Parametric implements ParametricUnivariateFunction
  {
    public Parametric() { }

    /**
     * Computes the value of the gradient at {@code x}.
     * The components of the gradient vector are the partial derivatives of the function with respect to each of the
     * <em>parameters</em>.
     *
     * @param x Value at which the gradient must be computed.
     * @param parameters Values for lower asymptote and higher asymptote.
     * @return the gradient vector at {@code x}.
     * @throws NullArgumentException if {@code param} is {@code null}.
     * */
    public double[] gradient(double x, double... parameters)
    {
      double[] gradient = new double[parameters.length];

      // calculating the partial derivative with regard to the parameters
      double bx = (1+parameters[1]*x);
      gradient[0] = 1d / bx;
      gradient[1] = -1d * (parameters[0]+parameters[2]*x)*x/(bx*bx);
      gradient[2] = x / bx;

      return gradient;
    }

    public double value(double x, double... parameters) throws NoDataException { return Func7001.evaluate(parameters, x); }
  }

  public static double[] guessParameters(List<WeightedObservedPoint> data)
  {
    double[] params = new double[3];
    if (Tools.isSet(data))
    {
      double y0=Double.MAX_VALUE*-1d, x0=Double.MAX_VALUE*-1d;
      for (WeightedObservedPoint pt : data)
      {
        if (pt.getY()>y0) y0=pt.getY();
        if (pt.getX()>x0) x0=pt.getX();
      }
      params[0]=y0; params[1]=-1d/x0; params[2]=y0*-1d/x0;
      return params;
    }

    return null;
  }
}
