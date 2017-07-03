package org.ms2ms.math;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import org.apache.commons.math.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.ms2ms.Disposable;
import org.ms2ms.data.Point;
import org.ms2ms.utils.Strs;
import org.ms2ms.utils.Tools;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.*;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   1/23/15
 */
public class Histogram implements Disposable
{
  private String        mTitle;
  private Transformer.processor eTransform = Transformer.processor.none;
  private int           mHistogramSize = 12;
  private Double        mStep, mSumY = null, mMean, mMedian, mStdev, mKurtosisNormality, mSkewness, mCorr, mCenter, mTop, mSigma, mFWHH, mUpperModal;
  private Range<Double> mRange;
  private List<Point>   mCumulative;
  private List<Point>   mHistogram;
  private List<Double>  mData = null;

  private SortedSetMultimap<Double, Double> mPeaks;

  public Histogram() { super(); mData = new ArrayList<>(); }
  public Histogram(String title, Double step, Range<Double> range)
  {
    init(title, step, range);
  }
  public Histogram(int size)
  {
    setTitle("unTitled");
    mHistogramSize=size;
    mHistogram = null;
    mData      = new ArrayList<Double>();
  }
  public Histogram(String title)
  {
    setTitle(title);
    mHistogram = null;
    mData      = new ArrayList<Double>();
  }
  public Histogram(int size, double[] data)
  {
    setTitle("unTitled");
    mHistogramSize=size;
    mHistogram = null;
    mData      = new ArrayList<Double>();

    for (double d : data) add(d);
    survey();
  }
  public Histogram(String title, Double[] steps)
  {
    setTitle(title);
    if (steps != null)
    {
      mHistogram = new ArrayList<Point>();
      for (Double s : steps)
      {
        mHistogram.add(new Point(s, 0.0));
      }
      setRange(Range.closed(Tools.front(steps), Tools.back(steps)));
      setStepSize(null);
      mHistogramSize=mHistogram.size();
    }
  }
  protected void init(String title, Double step, Range<Double> range)
  {
    setStepSize(step); setRange(range); setTitle(title);
    mHistogram = new ArrayList<Point>();
    for (int i = 0; i <= (int )Math.round(((range.upperEndpoint() - range.lowerEndpoint()) / step)); i++)
    {
      mHistogram.add(new Point(i * mStep + mRange.lowerEndpoint(), 0.0));
    }
    mHistogramSize=mHistogram.size();
  }

  public String       getTitle()     { return mTitle; }
  public Double       getStepSize()  { return mStep; }
  public List<Point>  getHistogram() { return mHistogram; }
  public Double       getTotals()    { survey(); return mSumY; }
  public List<Double> getData()      { return mData; }
  public Double       getSkewness()  { return mSkewness; }
  public Double       getKurtosis()  { return mKurtosisNormality; }
  public Double       getMean()      { return mMean; }
  public Double       getMedian()    { return mMedian; }
  public Double       getStdev()     { return mStdev; }
  public Double       getCenter()    { return mCenter; }
  public Double       getTop()       { return mTop; }
  public Double       getSigma()     { return mSigma; }
  public Double       getFWHH()      { return mFWHH; }
  public Double       getCentroid()  { return Tools.isSet(mHistogram) ? Points.centroid(mHistogram) : null; }
  public Double       getUpperModal() { return mUpperModal; }

  public Double       getCentroid(int begin, int end)
  {
    if (Tools.isSet(mHistogram))
    {
      end = end>0?end:mHistogram.size();
      return Points.centroid(end>begin?mHistogram.subList(begin, end):mHistogram);
    }
    return null;
  }

  public Transformer.processor getTransformer() { return eTransform; }
  public Range<Double> getRange()
  {
    if (mRange == null && Tools.isSet(mData))
    {
      Collections.sort(mData);
      mRange = Range.closed(mData.get(0), mData.get(mData.size()-1));
    }
    return mRange;
  }

  public Histogram setHistogram(List<Point> s) { mHistogram = s; return this; }
  public Histogram setTransform(Transformer.processor s) { eTransform=s; return this; }
  public void setTitle(String        s) { mTitle = s; }
  public void setStepSize(Double s)   { mStep  = s; }
  public void setRange(Range<Double> s) { mRange = s; }
  public void setRange(Double lower, Double upper) { mRange = Range.closed(lower, upper); }
  public void increment(int s) { increment(s, 1d); }
  public void increment(int s, Double counts)
  {
    if (s >= 0 && s < mHistogram.size())
      mHistogram.get(s).setY(mHistogram.get(s).getY() + counts);
  }
  public Histogram addToHistogram(Point s)
  {
    if (mHistogram == null) mHistogram = new ArrayList<>();
    mHistogram.add(s);

    return this;
  }
  public void add(Double x, Double counts)
  {
    if (x == null || x.isNaN() || x.isInfinite()) return;

    if (Tools.isSet(mHistogram))
    {
      if (getStepSize() != null && getStepSize() != 0)
      {
        int pos = (int )Math.round((x - mRange.lowerEndpoint()) / mStep);
        // any point out of the bound will just add to the edge, WYU 120707
        if (pos < 0) pos = 0; else if (pos >= mHistogram.size()) pos = mHistogram.size() - 1;
        mHistogram.get(pos).setY(mHistogram.get(pos).getY() + 1.0);
      }
      else
      {
        for (int i = 0; i < getHistogram().size() - 1; i++)
        {
          if (x >= getHistogram().get(i).getX() && x < getHistogram().get(i+1).getX()) increment(i, counts);
        }
      }
    }
    else if (mData != null) for (int i = 0; i < counts; i++) mData.add(x);
  }
  public Histogram add(Double x) { add(x, 1d); return this; }
  public void add(Histogram s)
  {
    if (Tools.isSet(s.getHistogram()))
      for (Point xy : s.getHistogram()) add(xy.getX(), xy.getY());

    if (Tools.isSet(s.getData()))
      for (Double x : s.getData()) add(x);
  }
  public void addAll(Collection<Double> x)
  {
    if (Tools.isSet(x)) for (Double X : x) add(X, 1d);
  }
  public void addAll(Double[] x)
  {
    if (Tools.isSet(x)) for (Double X : x) add(X, 1d);
  }
  public double getBin(int i) { return mHistogram.get(i).getX(); }
  public double getCounts(int i) { return mHistogram.get(i).getY(); }
  public int size() { return mHistogram != null ? mHistogram.size() : 0; }
  public boolean isSet() { return Tools.isSet(mHistogram); }

  public void print(PrintStream os)
  {
    print(os, "%.1f");
  }
  public void print(PrintStream os, String y_format)
  {
    if (Strs.isSet(getTitle())) os.println(getTitle());
    for (int i = 0; i < size(); i++)
    {
      os.println(getBin(i) + "," + String.format(y_format, getCounts(i)));
    }
  }
  public void print(PrintStream os, String x_format, String y_format)
  {
    if (Strs.isSet(getTitle())) os.println(getTitle());
    for (int i = 0; i < size(); i++)
    {
      os.println(String.format(x_format, getBin(i)) + "," + String.format(y_format, getCounts(i)));
    }
  }
  public void print(Writer writer, String x_format, String y_format) throws IOException
  {
    if (Strs.isSet(getTitle())) writer.write(getTitle() + "\n");
    for (int i = 0; i < size(); i++)
    {
      writer.write(String.format(x_format, getBin(i)) + "," + String.format(y_format, getCounts(i)) + "\n");
    }
  }
  @Override
  public String toString()
  {
    String made = null;
    if (Strs.isSet(getTitle())) made = getTitle();
    for (int i = 0; i < size(); i++)
    {
      made = Strs.extend(made, getBin(i) + "," + String.format("%.1f", getCounts(i)), "\n");
    }
    return made;
  }
  public Histogram survey()
  {
    // generate the histogram from the data if necessary
    if (!Tools.isSet(mHistogram) && Tools.isSet(mData)) generate(mHistogramSize);

    if (Tools.isSet(mData))
    {
      mMean   = Stats.mean(mData);
      mMedian = Stats.median(mData);
      mStdev  = Stats.stdev( mData);
    }
    if (Tools.isSet(mHistogram))
    {
      List<Double> ys = Points.toYs(getHistogram());
      mSumY   = Stats.sum(   ys);
/*
      Skewness quantifies how symmetrical the distribution is.
          A symmetrical distribution has a skewness of zero.
          An asymmetrical distribution with a long tail to the right (higher values) has a positive skew.
          An asymmetrical distribution with a long tail to the left (lower values) has a negative skew.

          Any threshold or rule of thumb is arbitrary, but here is one:
              If the skewness is greater than 1.0 (or less than -1.0),
              the skewness is substantial and the distribution is far from symmetrical.

      Kurtosis quantifies whether the shape of the data distribution matches the Gaussian distribution.
          A Gaussian distribution has a kurtosis of 0.
          A flatter distribution has a negative kurtosis,
          A distribution more peaked than a Gaussian distribution has a positive kurtosis.
*/
      mKurtosisNormality = new Kurtosis().evaluate(Tools.toDoubleArray(ys));
      mSkewness          = new Skewness().evaluate(Tools.toDoubleArray(ys));
      // check whether this is a uniform dist
      SimpleRegression R = new SimpleRegression(true);
      for (Point xy : getHistogram())
        R.addData(xy.getX(), xy.getY());
      mCorr              = R.getRSquare();
    }
    return this;
  }
//  public Double getProbability(Double x)
//  {
//    if (mSumY == null || mSumY == 0) survey();
//    Point xy = Point_Util.interpolate(getHistogram(), x, true); // ignore the nero
//    return xy != null ? xy.getY() / mSumY : Double.NaN;
//  }
  public void reset()
  {
    if (Tools.isSet(getHistogram()))
      for (Point xy : getHistogram()) xy.setY(0f);
  }
  public Histogram generate() { return generate(mHistogramSize); }
  public Histogram generate(int step_num)
  {
    if (!Tools.isSet(mData) || step_num == 0) return this;

    Collections.sort(mData);
    Range<Double> range = Range.closed(mData.get(0), mData.get(mData.size()-1));

    generate(step_num, range);
    return this;
  }
  public Histogram peak_detection(double affinity, int clump)
  {
    if (!Tools.isSet(mData)) return this;

    Collections.sort(mData);
    // peak detection
    mPeaks = TreeMultimap.create();
    Collection<Double> pool = new ArrayList<>(); pool.add(mData.get(0));
    for (int i=1; i<mData.size(); i++)
    {
      if (mData.get(i)-mData.get(i-1)>affinity)
      {
        // got a peak if we have enough points in the pool
        if (pool.size()>=clump)
          Tools.putAll(mPeaks, Stats.mean(pool), pool);
        pool.clear();
      }
      pool.add(mData.get(i));
    }
    // trim away peaks with too few points
    Iterator<Double> itr = mPeaks.keySet().iterator();
    while (itr.hasNext())
      if (mPeaks.get(itr.next()).size()<clump) itr.remove();

    return this;
  }

  public Histogram generate(double step_size)
  {
    if (!Tools.isSet(mData) || step_size == 0) return this;

    Collections.sort(mData);
    Range<Double> range = Range.closed(mData.get(0), mData.get(mData.size()-1));

    generate((int )((range.upperEndpoint()-range.lowerEndpoint())/step_size), range);
    return this;
  }
  public Histogram generateTruncated(int step_num, int skips)
  {
    if (!Tools.isSet(mData) || step_num == 0) return this;

    Collections.sort(mData);
    Range<Double> range = Range.open(mData.get(0), mData.get(mData.size() - 1));

    if (skips>0)
    {
      double step = (range.upperEndpoint()-range.lowerEndpoint())/(double )step_num;
      range = Range.closedOpen(mData.get(0)+step*skips, range.upperEndpoint());
      step_num-=skips;
    }
    generate(step_num, range);
    return this;
  }
  public Histogram generate2pts(int step_num, double min_step)
  {
    if (!Tools.isSet(mData) || step_num == 0) return this;

    Collections.sort(mData);

    // setup the quatiles
    if (Math.round(mData.size()*0.5 )<mData.size()) mCenter = mData.get((int )Math.round(mData.size()*0.5));
    if (Math.round(mData.size()*0.75)<mData.size()) mSigma  = mData.get((int )Math.round(mData.size()*0.75)) - mCenter;

    mHistogram = new ArrayList<>();

    if (step_num>mData.size()*0.5) step_num = (int )Math.round(mData.size()*0.5);

    int   step = (int )Math.round((double )mData.size()/(double )step_num), i=0, start=i;
    while (i<mData.size())
    {
      int stop = (i+step<mData.size()?i+step:(mData.size()-1)); i+=step;
      if (Math.abs(mData.get(stop)-mData.get(start))>=min_step)
      {
        mHistogram.add(new Point(mData.get(start), 100d * ((stop-start) / (double) mData.size()) / (mData.get(stop)-mData.get(start))));
        start=i;
      }
    }
    mHistogramSize=mHistogram.size();

    return this;
  }
  public static void generate(int step_num, Histogram... histos)
  {
    generate(step_num, Arrays.asList(histos));
  }
  public static void generate(int step_num, Collection<Histogram> histos)
  {
    if (!Tools.isSet(histos) || step_num == 0) return;

    Range<Double> range = Range.closed(Double.MAX_VALUE, Double.MAX_VALUE * -1d);
    for (Histogram H : histos)
    {
      Collections.sort(H.getData());
      range = Tools.extendLower(range, H.getData().get(0));
      range = Tools.extendUpper(range, H.getData().get(H.getData().size() - 1));
    }

    for (Histogram H : histos) H.generate(step_num, range);
  }
/*
  public void generate()
  {
    if (!Tools.isSet(mData)) return;

    TreeMultimap<Double, Double> slot_val = TreeMultimap.create();
    for (Double v : getData()) slot_val.put(v, v);

    if (mHistogram == null) mHistogram = new ArrayList<>(); else getHistogram().clear();
    for (Double slot : slot_val.keySet())
      mHistogram.add(new Point(slot, slot_val.get(slot).size()));

  }
*/
  public void generate(int step_num, Range<Double> initial_range)
  {
    if (!Tools.isSet(mData)) return;

    Range<Double> range = Range.closed(initial_range.lowerEndpoint(), initial_range.upperEndpoint());

    if (step_num <= 0) step_num = (int )(range.upperEndpoint()-range.lowerEndpoint());

    init(getTitle(), (range.upperEndpoint()-range.lowerEndpoint())/(float )step_num, range);

    if (Tools.isSet(mHistogram)) for (Double x : mData)
      if (x != null && (!initial_range.lowerBoundType().equals(BoundType.CLOSED) || x>=initial_range.lowerEndpoint())) add(x);

    survey();
  }
  public static void generate(Collection<Histogram> grams, int step_num)
  {
    if (!Tools.isSet(grams)) return;

    Range<Double> range = Range.closed(Double.MAX_VALUE, Double.MIN_VALUE);
    for (Histogram H : grams)
    {
      if (!Tools.isSet(H.getData())) continue;
      Collections.sort(H.getData());
      range = Tools.extendLower(range, H.getData().get(0));
      range = Tools.extendUpper(range, H.getData().get(H.getData().size() - 1));
    }

    for (Histogram H : grams)
    {
      if (!Tools.isSet(H.getData())) continue;
      H.generate(step_num, range);
    }
  }
  public static void generate(Histogram A, Histogram B, int step_num)
  {
    Collection<Histogram> gs = new ArrayList<Histogram>();
    gs.add(A); gs.add(B);
    generate(gs, step_num);
  }
  public Histogram calcProb(Histogram positives, Histogram negatives)
  {
    if (positives == null || negatives == null  ||
        !Tools.isSet(positives.getHistogram()) ||
        !Tools.isSet(negatives.getHistogram()) ||
        positives.getHistogram().size() != negatives.getHistogram().size()) return null;

    if (mHistogram == null) mHistogram = new ArrayList<Point>(); else getHistogram().clear();
    for (int i = 0; i < positives.getHistogram().size(); i++)
    {
      Point pt = new Point(positives.getHistogram().get(i).getX(),
          positives.getHistogram().get(i).getY() /
              (positives.getHistogram().get(i).getY() + negatives.getHistogram().get(i).getY()));
      mHistogram.add(pt);
    }
    return this;
  }
  public Double seekXbyY(double y)
  {
    if (Tools.isSet(mHistogram))
      for (Point pt : mHistogram)
        if (pt.getY() >= y) return pt.getX();

    return null;
  }

  /** Given a critical value, said 0.95, what is the x-bound that covers the required percentage of the area?
   *
   * @param t
   * @return
   */
  public Range<Double> calcConfidenceInterval(double t)
  {
    if (!Tools.isSet(mHistogram)) return null;

    Point base = Points.basePoint(mHistogram);
    double area_total = 0d;
    for (Point pt : mHistogram) area_total += pt.getY();

    double step = base.getY() / Math.min(1000d, mHistogram.size());
    int lower=0, upper=0;
    for (double cutoff = 0; cutoff <= base.getY(); cutoff += step)
    {
//      Range<Integer> bound = new Range<Integer>();
      for (int i = 0; i < mHistogram.size(); i++)
        if (mHistogram.get(i).getY() > cutoff) { lower=i; break; }

      for (int i = mHistogram.size() - 1; i >= 0; i--)
        if (mHistogram.get(i).getY() > cutoff) { upper=i; break; }

      if (upper > lower)
      {
        double area = 0d;
        for (int i = lower; i <= upper; i++) area += mHistogram.get(i).getY();

        if (area <= area_total * t)
        {
          return Range.closed(mHistogram.get(lower).getX(), mHistogram.get(upper).getX());
        }
      }
    }
    return null;
  }
  public void normalize()
  {
    double t = getTotals();
    for (Point p : getHistogram()) p.setY(100d * p.getY() / t);
  }
  public static Histogram bestTransform(Histogram orig, double max_skewness, double max_kurtosis,
                                        Transformer.processor... processors)
  {
    // loop thro some transformation to achieve better normality
    // http://imaging.mrc-cbu.cam.ac.uk/statswiki/FAQ/Simon
    if (Math.abs(orig.getSkewness())>max_skewness || orig.getKurtosis()>max_kurtosis)
    {
      Histogram good = null;
      for (Transformer.processor proc : processors)
      {
        try
        {
          Histogram processed = new Histogram(orig.mHistogramSize);
          for (Double d : orig.getData())
          {
            processed.add(Stats.transform(d, proc));
          }
          processed.setTransform(proc).survey();
          if (Math.abs(processed.getSkewness())<=max_skewness && processed.getKurtosis()<=max_kurtosis) return processed;
          if (Math.abs(processed.getSkewness())<=max_skewness) good = processed;
        }
        catch (Exception e) { }
      }
      if (good!=null) return good;
    }
    return orig;
  }
//  public Map<String, Double> fitNormDist()
//  {
//    double A=0; // the amplitude of the dist
//    SimpleRegression R = new SimpleRegression(true);
//    for (Point xy : getHistogram())
//      if (xy.getY()>0)
//      {
//        R.addData(Math.log(xy.getY()), xy.getX());
//        if (xy.getY()>A) A=xy.getY();
//      }
//
//    // compute the definition of a norm dist
//    Map<String, Double> params = new HashMap<>();
//    params.put("amplitude",A);
//    params.put("sigma",    R.getSlope()/-2d);
//    params.put("mean",     R.getIntercept()+2*Math.log(A)*(params.get("sigma")+1d));
//
//    return params;
//  }
//  public SimpleRegression getRegression(boolean logT)
//  {
//    if (!Tools.isSet(getHistogram())) return null;
//
//    double A=0, sum=0; int apex=0; // the amplitude of the dist
//    for (int i=0; i<getHistogram().size(); i++)
//    {
//      sum+=getHistogram().get(i).getY();
//      if (getHistogram().get(i).getY() > A) { A = getHistogram().get(i).getY(); apex = i; }
//    }
//
//    SimpleRegression R = new SimpleRegression(true); int i=apex; double counts=0d, steps=0;
//    while (i<getHistogram().size())
//    {
//      Point xy = getHistogram().get(i);
//      counts+=xy.getY(); steps+=mStep;
//      if (counts>1)
//      {
////        System.out.println(Tools.d2s(xy.getX(), 2) + "\t" + Math.log(counts/(steps*sum)) + "\t" + steps);
//        R.addData(xy.getX(), logT?Math.log(counts/(steps*sum)):counts/(steps*sum));
//        counts=steps=0d;
//      }
//      i++;
//    }
////    System.out.println("r2="+R.getRSquare());
//
//    return R;
//  }
//  public Histogram fitGaussian(boolean upper)
//  {
//    if (Tools.isSet(getHistogram()))
//    {
//      WeightedObservedPoints obs = new WeightedObservedPoints();
//      // watch out for truncation
//      int start=0;
//      if (upper)
//      {
//        int totals=getData().size(), remainder=(int )(totals*0.9);
//        // let's find out where the start is to avoid truncation
//        for (int i=getHistogram().size()-1; i>=0; i--)
//          if (totals>remainder) totals-=getHistogram().get(i).getY(); else break;
//      }
//      for (int i=start; i<getHistogram().size(); i++)
//        if (getHistogram().get(i).getY()>0)
//          obs.add(getHistogram().get(i).getX(), getHistogram().get(i).getY());
//
//      Point top = Points.basePoint(getHistogram());
//      // let's make the best guess
//      mTop=top.getY(); if (mCenter==null) mCenter=getCentroid(); if (mSigma==null) mSigma=mCenter*0.25d;
//      try
//      {
//        // fit the model --> Normalization, Mean, Sigma
//        double[] initials = new double[] {mTop, mCenter, mSigma},
//            parameters = GaussianCurveFitter.create().withStartPoint(initials).withMaxIterations(1000).fit(obs.toList());
//
//        if (parameters!=null && parameters.length>2)
//        {
//          mTop=parameters[0]; mCenter=parameters[1]; mSigma=parameters[2];
//        }
//      }
//      catch (Exception e)
//      {
//        // try our best guess, not worry about the spread
//        mCenter=getCentroid(); mSigma=null;
////        e.printStackTrace();
//      }
////      System.out.println("Score\tOccurances");
////      for (Point pt : getHistogram()) System.out.println(pt.getX()+"\t"+pt.getY());
////      System.out.println("\nTop="+mTop+", Center="+mCenter+", Sigma="+mSigma);
//    }
//    return this;
//  }
  // assess the center and upper quatile of a distribution truncated at the lower end
  // assume that the histogram is already sorted from low to high
  public Histogram assessTruncated(int low_bound)
  {
    if (!Tools.isSet(getHistogram())) return this;

    if (getHistogram().size()==1)
    {
      mCenter = Tools.front(getHistogram()).getX(); mSigma=null;
    }
    // remove the zero
    Iterator<Point> itr = getHistogram().iterator();
    while (itr.hasNext())
      if (itr.next().getY()==0) itr.remove();

    mCenter = getCentroid(low_bound,-1);
    if (getHistogram().size()<=3) { mSigma=null; mFWHH=null; return this; }

    // locate the upper quartile
    int apex    = Points.findClosest(getHistogram(), mCenter);
    double half = Points.sumY(getHistogram(), apex);
//        upperQ = (int )Math.round((getHistogram().size()+apex)*0.5d),
    mUpperModal = getCentroid(apex,-1);

    // locate the point above the apex where Y is 1/2 of the apex
    double hw = Math.sqrt(getHistogram().get(apex).getY()), sum=0d, q4=half*0.6827d; mSigma=null;
    for (int i=apex; i<getHistogram().size()-1; i++)
    {
      sum+=getHistogram().get(i).getY();
      if (mSigma==null && sum>=q4 && i>0)
      {
        Point x1 = new Point(getHistogram().get(i- 1).getX(), sum-getHistogram().get(i).getY()),
              x2 = new Point(getHistogram().get(i).getX(), sum);
        //x1.setY(sum); x2.setY(sum+x2.getY());
        Point mid = Points.interpolateByY(x1, x2, q4);
        if (mid!=null) mSigma=mid.getX()-mCenter;
//        if (Double.isInfinite(mSigma))
//          System.out.print("");
      }
      if (getHistogram().get(i).getY()>hw && getHistogram().get(i+1).getY()<=hw)
      {
        Point xy = Points.interpolateByY(getHistogram().get(i), getHistogram().get(i+1), hw);
        if (xy!=null) mFWHH=xy.getX()-mCenter;
      }
    }

    return this;
  }
//  public Histogram trimFromUpper()
//  {
//    if (!Tools.isSet(getHistogram())) return this;
//
//    int start=0;
//    for (int i=getHistogram().size()-2; i>0; i--)
//    {
//      if (getHistogram().get(i).getY()>0 &&
//          getHistogram().get(i-1).getY()>getHistogram().get(i).getY() &&
//          getHistogram().get(i+1).getY()>getHistogram().get(i).getY()) { start=i; break; }
//    }
//    if (start>0)
//      for (int i=0; i<start; i++) getHistogram().remove(0);
//
//    return this;
//  }
//  public Histogram generateCumulative(int size)
//  {
//    if (!Tools.isSet(mData)) return this;
//
//    Collections.sort(mData, Ordering.natural().reverse());
//
//    int step = (int )Math.round((double )mData.size()/(double )size);
//    mCumulative = new ArrayList<>();
//    for (int i=0; i<mData.size(); i+=step)
//    {
//      mCumulative.add(new Point(mData.get(i), (i+1)));
//    }
//
//    return this;
//  }
  public void printHistogram()
  {
    double base = (double )mData.size();
    System.out.println("Score\tOccurances-"+getTitle());
    for (Point pt : getHistogram()) if (pt.getY()!=0) System.out.println(pt.getX()+"\t"+(pt.getY()/base));
    System.out.println("\nTop="+mTop+", Center="+mCenter+", Sigma="+mSigma);
  }
  public StringBuffer wikiHistogram(StringBuffer buf)
  {
    double base = (double )mData.size();
    buf.append("Score\tOccurances-"+getTitle()+"\n");
    for (Point pt : getHistogram()) if (pt.getY()!=0) buf.append(pt.getX()+"\t"+(pt.getY()/base)+"\n");
    buf.append("\nTop="+mTop+", Center="+mCenter+", Sigma="+mSigma+"\n");

    return buf;
  }
  public void printCumulatives()
  {
    double base = (double )mData.size();
    System.out.println("Score\tCumulatives-"+getTitle());
    for (Point pt : mCumulative) if (pt.getY()!=0) System.out.println(pt.getX()+"\t"+(pt.getY()/base));
    System.out.println();
  }
  public String wikiHistogram()
  {
    if (Tools.isSet(getHistogram()))
    {
      StringBuilder buf = new StringBuilder("{chart:type=xyLine|title="+getTitle()+"}\n");
      buf.append("||X||Occurrence||\n");
      for (Point pt : getHistogram()) buf.append("|"+pt.getX()+"|"+pt.getY()+"|\n");
      buf.append("{chart}\n");

      return buf.toString();
    }
    return null;
  }

  @Override
  public void dispose()
  {
    Tools.dispose(mCumulative,mHistogram,mData);
  }
}
