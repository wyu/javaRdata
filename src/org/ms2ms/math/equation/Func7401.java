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
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.ms2ms.utils.Tools;

import java.io.Serializable;
import java.util.Arrays;

public class Func7401 implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction, Serializable
{
  private static final long serialVersionUID = -7726511984220295583L;
  private final double[] coefficients;
  // y = (a + cx^2) / (1 + bx^2), coeffs = {a, b, c}
  public Func7401(double[] c) throws NullArgumentException, NoDataException
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
      return (coeffs[0]+coeffs[2]*argument*argument) / (1d+coeffs[1]*argument*argument);

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

  public Func7401 add(Func7401 p)
  {
    int lowLength  = FastMath.min(this.coefficients.length, p.coefficients.length);
    int highLength = FastMath.max(this.coefficients.length, p.coefficients.length);
    double[] newCoefficients = new double[highLength];

    for(int i = 0; i < lowLength; ++i) {
      newCoefficients[i] = this.coefficients[i] + p.coefficients[i];
    }

    System.arraycopy(this.coefficients.length < p.coefficients.length?p.coefficients:this.coefficients, lowLength, newCoefficients, lowLength, highLength - lowLength);
    return new Func7401(newCoefficients);
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

    s.append("y = ("+ Tools.d2s(getCoefficients()[0], 3) + (getCoefficients()[2]<0?" - ":" + ") + Tools.d2s(Math.abs(getCoefficients()[2]), 3) + "*x^2)");
    s.append(" / (1" + (getCoefficients()[1]>0?" + ":" - ") + Tools.d2s(Math.abs(getCoefficients()[1]), 3) + "*x^2)");

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
    } else if(!(obj instanceof Func7401)) {
      return false;
    } else {
      Func7401 other = (Func7401)obj;
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
      double x2 = x*x, bx = (1+parameters[1]*x2);
      gradient[0] = 1d / bx;
      gradient[1] = -1d * (parameters[0]+parameters[2]*x2)*x2/(bx*bx);
      gradient[2] = x2 / bx;

      return gradient;
    }

    public double value(double x, double... parameters) throws NoDataException { return Func7401.evaluate(parameters, x); }
  }
}
