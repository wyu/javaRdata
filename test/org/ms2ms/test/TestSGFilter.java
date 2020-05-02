package org.ms2ms.test;

import org.junit.Test;
import org.ms2ms.math.contrib.ContinuousPadder;
import org.ms2ms.math.contrib.SGFilter;

import static org.junit.Assert.assertEquals;

public class TestSGFilter extends TestAbstract
{
  @Test
  public final void testComputeSGCoefficients() {
    double[] coeffs = SGFilter.computeSGCoefficients(5, 5, 2);
    double[] tabularCoeffs = new double[]{-0.084, 0.021, 0.103, 0.161, 0.196, 0.207, 0.196, 0.161, 0.103, 0.021, -0.084
    };
    assertEquals(11,
        coeffs.length);
    assertCoeffsEqual(coeffs,
        tabularCoeffs);
    coeffs = SGFilter.computeSGCoefficients(5,
        5, 4);
    tabularCoeffs = new double[]{0.042, -0.105, -0.023, 0.140, 0.280, 0.333, 0.280, 0.140, -0.023, -0.105, 0.042};
    assertEquals(11, coeffs.length);
    assertCoeffsEqual(coeffs,
        tabularCoeffs);
    coeffs = SGFilter.computeSGCoefficients(4,
        0,
        2);
    tabularCoeffs = new double[]{0.086,
        -0.143,
        -0.086,
        0.257,
        0.886};
    assertEquals(5,
        coeffs.length);
    assertCoeffsEqual(coeffs,
        tabularCoeffs);
  }
  @Test
  public final void testSmooth() {
    float[] data = new float[]{8.91f,
        8.84f,
        9.06f,
        8.94f,
        8.78f};
    float[] leftPad = new float[]{8.91f,
        8.93f,
        9.02f,
        9.16f,
        7.50f};
    double[] realResult = new double[]{8.56394, 8.740239999999998, 8.962772,
        9.077350000000001, 8.80455};

    double[] coeffs = SGFilter.computeSGCoefficients(5,
        5,
        4);
    ContinuousPadder padder1 = new ContinuousPadder(false,
        true);
    SGFilter sgFilter = new SGFilter(5,
        5);
    sgFilter.appendPreprocessor(padder1);
    float[] smooth = sgFilter.smooth(data,
        leftPad,
        new float[0],
        coeffs);
    assertResultsEqual(smooth,
        realResult);
  }
  private void assertCoeffsEqual(double[] coeffs, double[] tabularCoeffs)
  {
    for (int i = 0; i < tabularCoeffs.length; i++) {
      assertEquals(tabularCoeffs[i],
          coeffs[i],
          0.001);
    }
  }
  private void assertResultsEqual(double[] results, double[] real) {
    for (int i = 0; i < real.length; i++) {
      assertEquals(real[i],
          results[i],
          0.001);
    }
  }

  private void assertResultsEqual(float[] results, double[] real) {
    for (int i = 0; i < real.length; i++) {
      assertEquals(real[i],
          results[i],
          0.1);
    }
  }
}
