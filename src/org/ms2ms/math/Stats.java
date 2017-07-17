package org.ms2ms.math;

import com.google.common.collect.*;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.ms2ms.data.Point;
import org.ms2ms.utils.Strs;
import org.ms2ms.utils.Tools;
import org.uncommons.maths.combinatorics.PermutationGenerator;

import java.util.*;

/**
 * Created by wyu on 4/26/14.
 */
public class Stats
{
  public enum Aggregator { MEAN, MEDIAN, STDEV, COUNT }

  static private Map<Long,   Double> sLnFactorials = new HashMap<>();
  static private Map<Double, Double> sLnDblFactorials = new HashMap<>();
  static private Table<Integer, Integer, Collection<int[]>> sPermutationCache = HashBasedTable.create();

  static
  {
    for (long i=0L; i<18L; i++) sLnFactorials.put(i, Math.log(factorial(i)));
    for (Double i=0d; i<18d; i++) sLnDblFactorials.put(i, Math.log(factorial(i)));
  }
  public static double geomean(Collection<Double> s)
  {
    if (!Tools.isSet(s)) return 0;

    double avg = 0d, counts=0;
    for (Double v : s) if (v!=null && v!=0) { avg += Math.log(v); counts++; }
    return Math.exp(avg/counts);
  }
  public static double products(Collection<Double> s)
  {
    if (!Tools.isSet(s)) return 0;

    double avg = 1d;
    for (Double v : s) avg*=v;
    return avg;
  }
  public static double ratio_products(Collection<Double> s)
  {
    if (!Tools.isSet(s)) return 0;

    double avg = 1d;
    for (Double v : s) avg*=(1-v);
    return 1-avg;
  }
  // no bound checking
  public static double mean0(double[] s)
  {
    double avg = 0d;
    for (double v : s) avg+=v;
    return avg/(double )s.length;
  }
  public static double mean(Collection<Double> s)
  {
    if (!Tools.isSet(s)) return 0;

    double avg = 0d;
    for (Double v : s) avg+=v;
    return avg/(double )s.size();
  }
  public static double mean(double[] s, int up_to)
  {
    if (!Tools.isSet(s)) return 0;

    double avg = 0d;
    for (int i=0; i<up_to; i++) avg+=s[i];
    return avg/(double )s.length;
  }
  public static double stdev(Collection<Double> s) { return Math.sqrt(variance(s)); }
  public static double stdev(double[] s, int up_to) { return Math.sqrt(variance(s, up_to)); }
  public static double variance(Collection<Double> s)
  {
    if (!Tools.isSet(s) || s.size()==1) return 0;

    double avg=mean(s), var=0d;
    for (Double v : s) { var+= (v-avg)*(v-avg); }

    return var/((double )s.size()-1d);
  }
  public static double variance(double[] s, int up_to)
  {
    if (!Tools.isSet(s) || s.length==1) return 0;

    double avg=mean(s, up_to), var=0d;
    for (int i=0; i<up_to; i++) { var+= (s[i]-avg)*(s[i]-avg); }

    return var/((double )s.length-1d);
  }
  public static long factorial(long n)
  {
    long prod = 1L;
    for (long k=1; k<=n; ++k)
      prod *= k;
    return prod;
  }
  public static double ln_combination(long n, long k) { return ln_factorial(n)-ln_factorial(k)-ln_factorial(n-k); }
  public static double ln_factorial(long n)
  {
    if (n>17) return 0.5d*Math.log(2d*(double )n*3.14) + (double )n*Math.log((double )n) - (double )n;
//    if (n<=0)
//      System.out.print("");
    return sLnFactorials.get(n);
  }
  public static double ln_factorial(double n)
  {
    if (n>17) return 0.5d*Math.log(2d*n*3.14) + n*Math.log(n) - n;
    return sLnDblFactorials.get(n);
  }
  public static double hypergeometricPval1(long success, long trials, long success_population, long population)
  {
    long min_trial_pop = trials<success_population?trials:success_population;
    double t1 = (double )trials-(double )population+(double )success_population;

    if (success>min_trial_pop || (double )success<t1 || trials>population || success_population>population) return 1;

    double ln_pop_trials   =ln_combination(population, trials),
           lnfac_suc_pop   =ln_factorial(success_population),
           lnfac_pop_sucpop=ln_factorial(population-success_population), prob=0d;
    for (long suc=success; suc<=min_trial_pop; suc++)
    {
      double p=lnfac_suc_pop - ln_factorial(success) - ln_factorial(success_population-success) +
               lnfac_pop_sucpop - ln_factorial(trials-success) - ln_factorial(population-success_population-trials+success) - ln_pop_trials;
      prob += Math.exp(p);
    }
    return Math.log10(prob);
  }
  // calc the probability density
  public static double hypergeom(long success, long trials, long success_population, long population)
  {
    return (ln_combination(success_population,success)+ln_combination(population-success_population,trials-success)-ln_combination(population,trials))/2.30258509;
  }
  public static Number aggregate(Collection data, Aggregator func)
  {
    if (!Tools.isSet(data)) return 0;
    if (func.equals(Aggregator.COUNT)) return data.size();

    Collection<Double> ns = new ArrayList<Double>();
    for (Object d : data) ns.add(toDouble(d));

    if (func.equals(Aggregator.MEAN)) return mean(ns);
    //else if (func.equals(Dataframes.Func.MEAN)) return mean(ns);

    return 0;
  }
  public static boolean isNumeric(Object s)
  {
    if (s==null) return false;
    if (s instanceof Double || s instanceof Float || s instanceof Integer || s instanceof Long) return true;
    return NumberUtils.isNumber(s.toString());
  }
  // convert the Object to Number if possible
  public static Object toNumber(Object s)
  {
    if (s==null) return null;
    try
    {
      // quotes? must remain a string if so
      if (s instanceof String)
      {
        String val = Strs.trim((String) s);
        if ((val.charAt(0)=='"'  && val.charAt(val.length()-1)=='"') ||
            (val.charAt(0)=='\'' && val.charAt(val.length()-1)=='\'')) return val.substring(1, val.length()-1);

        boolean isNum = (val.charAt(0)>='0' && val.charAt(0)<='9');
        return isNum && val.indexOf('.')>=0 ? NumberUtils.createDouble(val) : (isNum?NumberUtils.createLong(val):val);
      }
    }
    catch (Exception e) {}
    return s;
  }
  public static Double toDouble(Object s)
  {
    if (s==null) return null;
    try
    {
      if      (s instanceof String)  return NumberUtils.createDouble((String )s);
      else if (s instanceof Double)  return (Double  )s;
      else if (s instanceof Float )  return ((Float  )s).doubleValue();
      else if (s instanceof Long  )  return ((Long   )s).doubleValue();
      else if (s instanceof Integer) return ((Integer)s).doubleValue();
    }
    catch (NumberFormatException e) {}

    return null;
  }
  public static Float toFloat(Object s)
  {
    if (s==null) return null;
    try
    {
      if      (s instanceof String)  return NumberUtils.createFloat((String )s);
      else if (s instanceof Double)  return ((Double  )s).floatValue();
      else if (s instanceof Float )  return ((Float  )s);
      else if (s instanceof Long  )  return ((Long   )s).floatValue();
      else if (s instanceof Integer) return ((Integer)s).floatValue();
    }
    catch (NumberFormatException e) {}

    return null;
  }
  public static Long[] toLongArray(Object s, char delim)
  {
    if (s==null) return null;
    if (s instanceof String) return Stats.toLongArray(Strs.split((String) s, ';'));

    Long i = Stats.toLong(s);
    return i!=null?new Long[] {i}:null;
  }
  public static Long[] toLongArray(Object[] s)
  {
    try
    {
      Long[] out = new Long[s.length];
      for (int i=0; i<s.length; i++) out[i]=toLong(s[i]);
      return out;
    }
    catch (NumberFormatException e) {}
    return null;
  }

  public static Long toLong(Object s)
  {
    if (s==null) return null;
    if      (s instanceof String)  return NumberUtils.createLong((String) s);
    else if (s instanceof Long  )  return ((Long   )s);
    else if (s instanceof Integer) return ((Integer )s).longValue();
    else if (s instanceof Double)  return ((Double  )s).longValue();
    else if (s instanceof Float)   return ((Float   )s).longValue();

    return null;
  }
  public static Integer toInt(Object s)
  {
    try
    {
      if      (s instanceof String)  return NumberUtils.createInteger((String) s);
      else if (s instanceof Long  )  return ((Long    )s).intValue();
      else if (s instanceof Integer) return ((Integer )s);
      else if (s instanceof Double)  return ((Double  )s).intValue();
      else if (s instanceof Float)   return ((Float   )s).intValue();
    }
    catch (Exception e) {}

    return null;
  }
  public static Range<Double> closed(double[] s)
  {
    if (s==null) return null;
    double lower=Double.MAX_VALUE, upper = Double.MAX_VALUE*-1;
    for (double x : s)
    {
      if (x<lower) lower=x;
      if (x>upper) upper=x;
    }
    return Range.closed(lower, upper);
  }
  /**
   *
   * @param Xs is the variable
   * @param xs and ys represent the X-Y curve on which the interpolation will be based
   * @param bandwidth is the fraction of source points closest to the current point is taken into account for computing
   *                  a least-squares regression when computing the loess fit at a particular point. A sensible value is
   *                  usually 0.25 to 0.5, the default value is 0.3.
   * @return
   */
  public static double[] interpolate(double[] xs, double[] ys, double bandwidth, double... Xs)
  {
    if (!Tools.isSet(xs) || !Tools.isSet(ys) || xs.length!=ys.length) return null;

    try
    {
      // average the values with duplicated x
      Multimap<Double, Double> xy = TreeMultimap.create();
      for (int i=0; i<xs.length; i++) xy.put(xs[i], ys[i]);
      int i=0; xs=new double[xy.keySet().size()]; ys=new double[xy.keySet().size()];
      for (Double x : xy.keySet()) { xs[i]=x; ys[i]= mean(xy.get(x)); i++; }
      Tools.dispose(xy);

      double[]                   Ys = new double[Xs.length];
      Range<Double>           bound = closed(xs);
      PolynomialSplineFunction poly = new LoessInterpolator(bandwidth, 2).interpolate(xs, ys);
      // compute the interpolated value
      for (i=0; i<Xs.length; i++)
      {
        double x=Xs[i];
        if      (x<bound.lowerEndpoint()) x=bound.lowerEndpoint();
        else if (x>bound.upperEndpoint()) x=bound.upperEndpoint();
        Ys[i] = poly.value(x);
      }

      return Ys;
    }
    catch (MathException me)
    {
      throw new RuntimeException("Not able to interpolate: ", me);
    }
  }
  public static double[] matrix_sum(double[]... ys)
  {
    if (!Tools.isSet(ys)) return null;

    double[] out = new double[ys[0].length];
    for (int i=0; i<ys.length; i++)
    {
      for (int j=0; j<out.length; j++)
      {
        out[j] += ys[i][j];
      }
    }
    return out;
  }
  public static Double sum(Collection<Double> ys)
  {
    if (!Tools.isSet(ys)) return null;

    Double sum=0d;
    for (Double y : ys) sum+=y;
    return sum;
  }
  public static Float sumFloats(Collection<Float> ys)
  {
    if (!Tools.isSet(ys)) return null;

    Float sum=0f;
    for (Float y : ys) sum+=y;
    return sum;
  }
  public static Float sumFloats(Float... ys)
  {
    if (!Tools.isSet(ys)) return null;

    Float sum=0f;
    for (Float y : ys)
      if (y!=null) sum+=y;
    return sum;
  }
  public static double sum(double[] ys)
  {
    if (!Tools.isSet(ys)) return 0;

    double sum=0d;
    for (double y : ys) sum+=y;
    return sum;
  }
  public static int sum(int[] ys)
  {
    if (!Tools.isSet(ys)) return 0;

    int sum=0;
    for (double y : ys) sum+=y;
    return sum;
  }
  public static Integer[] newIntArray(int start, int end)
  {
    Integer[] out = new Integer[end-start];
    for (int i=start; i<end; i++) out[i-start]=i;

    return out;
  }
  /** Estimates the mean and stdev of intensities after 'rounds' of outlier rejections.
   *  The outliers are defined as those outside of the 'stdevs' multiples of the calculated stdev
   *
   * @param intensities are the intensities of the points to be considered
   * @param stdevs is the multiple of stdev that define the boundary for the outliers
   * @param rounds is the number of times the outlier rejections will be attempted.
   * @return a double array where mean is followed by stdev of the intensities excluding the outliers
   */
  public static double[] outliers_rejected(Collection<Double> intensities, double stdevs, int rounds)
  {
    // deal with null or singleton first
    if (intensities==null)    return null;
    if (intensities.size()<2) return new double[] { Tools.front(intensities), 0d};

    double avg=0, bound=Double.MAX_VALUE;

    for (int i = 0; i < rounds; i++)
    {
      Iterator<Double> itr = intensities.iterator();
      while (itr.hasNext())
        if (Math.abs(itr.next()-avg)>bound) itr.remove();

      avg   = mean(intensities);
      bound = stdev(intensities) * stdevs;
    }

    return new double[] {avg, bound};
  }
  public static double median(double[] ys)
  {
    if (ys == null || ys.length == 0) return Double.NaN;
    if (ys.length == 1) return ys[0];
    Arrays.sort(ys);
    if (ys.length % 2 == 0) return (ys[(int )(ys.length * 0.5)    ] +
        ys[(int )(ys.length * 0.5) - 1]) * 0.5;
    return ys[(int )(ys.length * 0.5)];
  }
  public static double median(List<Double> ys)
  {
    if (ys.size() == 1) return Tools.front(ys);
    Collections.sort(ys);
    if (ys.size() % 2 == 0) return (ys.get((int )(ys.size() * 0.5)    ) +
        ys.get((int )(ys.size() * 0.5) - 1)) * 0.5;
    return ys.get((int )(ys.size() * 0.5));
  }
  public static double filter(List<Double> A, int index_begin, double[] filters)
  {
    double Y = 0d;
    for (int i = 0; i < filters.length; i++)
      Y += A.get(i + index_begin) * filters[i];

    return Y;
  }
  public static List<Double> smoothBySG5(List<Double> A)
  {
    // Do nothing if the set isn't big enough to smooth.
    if (null==A || A.size()<6) return A;

    // store the smoothed data separately
    List<Double> smoothed = new ArrayList<>(A.size());

    // special handling for the edge points
    smoothed.add(filter(A, 0, new double[] {0.886,0.257,-0.086,-0.143,0.086}));
    smoothed.add(filter(A, 0, new double[] {0.257,0.371,0.343,0.171,-0.143}));

    // the mid section
    for (int i=2; i < A.size()-2; i++)
      smoothed.add(filter(A, i-2, new double[] {-0.086,0.343,0.486,0.343,-0.086}));

    // special handling for the edge points
    smoothed.add(filter(A, A.size()-6, new double[] {-0.143,0.171,0.343,0.371,0.257}));
    smoothed.add(filter(A, A.size()-6, new double[] {0.086,-0.143,-0.086,0.257,0.886}));

    return smoothed;
  }
  public static Histogram newHistogram(int steps, Collection<Double> data)
  {
    return newHistogram(steps, Tools.toDoubleArray(data));
  }
  public static Histogram newHistogram(int steps, double... data)
  {
    if (!Tools.isSet(data)) return null;

    return Histogram.bestTransform(new Histogram(steps, data),
        1, 6, Transformer.processor.log,Transformer.processor.sqrt);
  }
/* Following Huber et al. Bioinformatics, 18, S96-S104, 2003 we apply the arcsinh transformation t,
   t: intensity -> gamma*arcsinh(a + b*intensity), where arcsinh(x) = log(x + sqrt(x^2 + 1)),
   which stabilizes the variance of the peak intensities, i.e. after this transformation all peak intensities
   have the same variance independent of their height. This transformation is equal to the log transformation
   for large intensities. According to Huber et al, this transformation is valid for error models of the form:
   var(I) = (c1*I+c2)^2 + c3 i.e. the variance of the intensity I depend quadratically on the intensity itself.
   The parameters gamma, a and b are related to the ci's as follows: gamma = 1/c1, a=c2/sqrt(c3), b=c1/sqrt(c3),
   or may be estimated otherwise.
*/

  public static double arcsinh(double s) { return Math.log(s + Math.sqrt(Math.pow(s, 2d) + 1d)); }
  public static double transform(double s, Transformer.processor proc)
  {
    switch (proc)
    {
      case log:     return Math.log(s);
      case log2:    return Math.log(s)/Math.log(2);
      case inverse: return 1d/s;
      case sqrt:    return Math.sqrt(s);
      case arcsinh: return arcsinh(s);
    }
    return s;
  }
  public static boolean isSet(double s) { return !Double.isNaN(s) && !Double.isInfinite(s); }
  public static int compareTo(Comparable A, Comparable B)
  {
    if (A==null && B==null) return 0;
    if (A==null) return -1;
    if (B==null) return  1;

    return A.compareTo(B);
  }
  public static double d2d(double x, int n)
  {
    double y=Math.round(x*Math.pow(10d,n))/Math.pow(10d,n);
    return y;
  }
  public static Double max(Double A, Double B)
  {
    if (A==null && B!=null) return B;
    if (A!=null && B==null) return A;
    if (A==null && B==null) return null;

    return Math.max(A,B);
  }
  public static Double percentile(Collection<Double> data, double pct)
  {
    if (data==null) return null;

    List<Double> d = new ArrayList<>(data);
    Collections.sort(d);
    return d.get((int )Math.round(data.size()*pct*0.01));
  }
  public static Collection<int[]> permutations(int n, int r)
  {
    if (sPermutationCache.contains(n, r)) return sPermutationCache.get(n,r);

    Multimap<String, List<Integer>> outcomes = HashMultimap.create();
    Collection<int[]> permuted = new ArrayList<>();

    // initiate a blank array of 0's
    List<Integer> mods = new ArrayList<>(); for (int i=0; i<n; i++) mods.add(0);
    // go thro the scenario
    for (int i=1; i<=r; i++)
    {
      mods.set(i-1, 1); outcomes.clear();
      PermutationGenerator perm = new PermutationGenerator(mods);
      while (perm.hasMore())
      {
        List<Integer> row = perm.nextPermutationAsList();
//        System.out.println(Strs.toString(row, ","));
        outcomes.put(Strs.toString(row, ","), row);
      }
      for (List<Integer> row : outcomes.values())
      {
        int[] rr = new int[row.size()];
        for (int j=0; j<row.size(); j++) rr[j]=row.get(j);
        permuted.add(rr);
      }
    }
    // deposit it in the cache
    sPermutationCache.put(n, r, permuted);

    return permuted;
  }
  public static boolean hasPositive(Collection<Double> s)
  {
    if (Tools.isSet(s))
      for (Double d : s) if (d>0) return true;
    return false;
  }
  public static double absSum(Collection<Double> s)
  {
    double sum=0;
    if (Tools.isSet(s))
      for (Double d : s) sum+=Math.abs(d);
    return sum;
  }
  public static double ppm(double obs, double calc) { return 1E6*(obs-calc)/calc; }
  public static double kai2(double[] pts)
  {
    if (pts==null) return 0d;

    double kai=0d;
    for (double s : pts) kai+=(s*s);

    return kai;
  }
  public static double kai2(List<Double>... pts)
  {
    if (pts==null) return 0d;

    double kai=0d;
    for (List<Double> pt : pts)
      if (pt!=null)
        for (double s : pt) kai+=(s*s);

    return kai;
  }
  public static Map<Double, Double> scaleQVal(SortedMap<Double, Boolean> scale)
  {
    if (scale==null) return null;

    Map<Double, Double> qval = new HashMap<>(); double counts=0d, decoys=0d;
    for (Double scr : scale.keySet())
    {
      counts++;
      if (scale.get(scr)) decoys++;
      qval.put(scr, 2d*decoys/counts);
    }
    return qval;
  }
  public static double factorial(double n)
  {
    if (n<2) return 1;
    // compute the exact factorial. Could be costly!
    double f=1d;
    for (double i=1d; i<=n; i++) f*=i;

    return f;
  }
// http://stackoverflow.com/questions/1095650/how-can-i-efficiently-calculate-the-binomial-cumulative-distribution-function
  public static double binomial_exact(double s, double n, double p)
  {
//  >>> prob(20, 0.3, 100)
//  0.016462853241869437
//
//      >>> 1-prob(40-1, 0.3, 100)
//  0.020988576003924564

    double prob=0d, x=1.0-p, a=n-s, b=s+1, c=a+b-1;

    for (double j=a; j<c+1; j++)
    {
//      double y1=factorial(c)/(factorial(j)*factorial(c-j));
//      double y2=Math.pow(x,j);
//      double y3=Math.pow(1-x,c-j);
//      prob += y1*y2*y3;
      prob += factorial(c)/(factorial(j)*factorial(c-j)) * Math.pow(x,j) * Math.pow(1-x,c-j);
    }

    return prob;
  }
  // return cummulative binomial prob using log-transformed aprox of factorial fn.
  public static double binomial(double s, double n, double p)
  {
    double prob=0d, x=1.0-p, a=n-s, b=s+1, c=a+b-1;
    for (double j=a; j<c+1; j++)
      prob += Math.exp(ln_factorial(c)-(ln_factorial(j)+ln_factorial(c-j)) + j*Math.log(x) + (c-j)*Math.log(1 - x));

    return prob;
  }
  public static double erf_Horner(double z)
  {
    double t = 1d/(1d + 0.5 * Math.abs(z));
    // use Horner's method
    double ans = 1 - t * Math.exp( -z*z - 1.26551223 +
        t * ( 1.00002368 +
        t * ( 0.37409196 +
        t * ( 0.09678418 +
        t * (-0.18628806 +
        t * ( 0.27886807 +
        t * (-1.13520398 +
        t * ( 1.48851587 +
        t * (-0.82215223 +
        t * ( 0.17087277))))))))));

    return z>=0d?ans:(ans*-1d);
  }
  // Normal Estimate, good for large n
  public static double binomial_normal_estimate(int s, int n, double p)
  {
    double u = n*p, o = Math.sqrt(u*(1-p));

    return 0.5*(1 + erf_Horner((s-u)/(o*Math.sqrt(2))));
  }

  public static double binomial_poisson_estimate(double s, double n, double p)
  {
    double L = n*p, sum = 0d;

    for (double i=0d; i<s+1d; i++)
      sum += Math.exp(i*Math.log(L)-ln_factorial(i));

    return sum*Math.exp(L*-1d);
  }
  public static int greaterEqualThan(Collection<Integer> vals, Double val)
  {
    int n=0;
    if (Tools.isSet(vals))
      for (Integer v : vals) if (v>=val) n++;

    return n;
  }
  public static Double thresholdByFdr(SortedMap<Double, Boolean> scores, double maxFDR, int repeat)
  {
    double counts=0d, decoys=0d, last=0;
    for (Double scr : scores.keySet())
    {
      counts++;
      if (scores.get(scr)) decoys++;
      if (decoys/counts>maxFDR) last=counts;
      if (counts-last>=repeat) return scr;
    }
    return null;
  }
  public static Double thresholdByNorms(SortedMap<Double, Boolean> scores, int repeat)
  {
    double counts=0d, norms=0d;
    for (Double scr : scores.keySet())
    {
      counts++;
      if (!scores.get(scr)) norms++;
      if (norms>repeat) return scr;
    }
    return null;
  }
}
