package org.ms2ms.math;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import org.ms2ms.data.Point;
import org.ms2ms.utils.Tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   1/24/15
 */
public class Points
{
  //--------------------------------------------------------------------------
  /** Simple linear interpolation
   *
   * @param p1
   * @param p2
   * @param x
   * @return xy.getY() = the interpolated Y
   */
  public static <T extends Point> T interpolate(T p1, T p2, Double x)
  {
    T xy = (T )(new Point(x, 0d));

    if      (p1 != null && p2 == null)
    {
      //xy.setY(p1.getY());
      xy = null; // undefined situation, WYU 081209
    }
    else if (p1 == null && p2 != null)
    {
      //xy.setY(p2.getY());
      xy = null;
    }
    else if (p1 != null && p2 != null && p2.getX() - p1.getX() != 0)
    {
      Double k = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
      xy.setY(p1.getY() + (x - p1.getX()) * k);
    }
    else if (p1 != null && p2 != null && p2.getX() == p1.getX())
    {
      Double k = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
      xy.setY(0.5d * (p1.getY() + p2.getY()));
    }
    return xy;
  }
  public static <T extends Point> T interpolateByY(T p1, T p2, Double y)
  {
    T xy = (T )(new Point(0d, y));

    if      (p1 == null || p2 == null)
    {
      //xy.setY(p1.getY());
      xy = null; // undefined situation, WYU 081209
    }
    else if (p2.getX() - p1.getX() != 0)
    {
      Double k = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
      xy.setX(p1.getX() + (y-p1.getY()) / k);
    }
    else if (p2.getX() == p1.getX())
    {
      Double k = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
      xy.setY(0.5d * (p1.getY() + p2.getY()));
    }
    return xy;
  }

  //--------------------------------------------------------------------------
  public static <T extends Point> T interpolate(List<T> ps, Double x, boolean ignore_zero)
  {
    Range<T> range = boundry((T )(new Point(x, 0d)), ps, ignore_zero);
    return range != null ? interpolate(range.lowerEndpoint(), range.upperEndpoint(), x) : null;
  }
/*
  // extend a few points beyond the boundry and estimate the uncertainty of the interpolation accordingly
  public static <T extends Point> MsReporter interpolate(List<T> ps, Double x, boolean ignore_zero, int extension)
  {
    //Range<T> range = findBoundry((T )(new Point(x, 0d)), ps, ignore_zero);
    Multimap<Integer, T> range = Point_Util.findBoundry((T )(new Point(x, 0d)), ps, ignore_zero, extension);

    if (range == null) return null;

    Collection<Double> estimates = new ArrayList<Double>();
    for (T left : range.get(-1))
    {
      for (T right : range.get(1))
      {
        T estimated = interpolate(left, right, x);
        estimates.add(estimated.getY());
      }
    }

    return new MsReporterIon(x, Toolbox.mean(estimates), 1.96d * Toolbox.stdev(estimates));
  }
*/
  /**
   Returns the base peak (the peak with the highest relative intensity) of the spectrum.
   */
  public static <T extends Point> T basePoint(Collection<T> data)
  {
    if (!Tools.isSet(data)) return null;

    T base = null;
    for (T datum : data)
      if (base == null || (datum.getY() > base.getY())) base = datum;

    // send the base peak back
    return base;
  }
  public static <T extends Point> T basePoint(List<T> data, int pad)
  {
    if (!Tools.isSet(data)) return null;

    int top = -1; T best = null;
    for (int i = 0; i < data.size(); i++)
      if (top == -1 || (data.get(i).getY() > best.getY())) { best = data.get(i); top = i; }

    if (top == -1) return null;

    Collection<T> profile = new ArrayList<T>();
    for (int i = top;     i <= top + pad; i++) if (i < data.size()) profile.add(data.get(i));
    for (int i = top - 1; i >= top - pad; i--) if (i >= 0)          profile.add(data.get(i));

    // send the base peak back
    return (T)(new Point(centroid(profile), best.getY()));
  }
  /** Assuming asending order in X
   *
   * @param m
   * @param ms
   * @param ignore_zero
   * @return lower, upper
   */
  public static <T extends Point> Range<T> boundry(T m, List<T> ms, boolean ignore_zero)
  {
    // locate the point that's the cloest to 'm'
    int index  = Collections.binarySearch(ms, m), left, right;
    if (index >= 0)
    {
      left = index; right = index;
    }
    else  // (-(insertion point) - 1)
    {
      index = -1 * index - 1;
      left = (index > 0 ? index-1 : -1);
      right = (index < ms.size() ? index : -1);
    }
    if (ignore_zero && left >= 0 && ms.get(left).getY() == 0)
      for (int i = left; i >= 0; i--) if (ms.get(i).getY() != 0) { left = i; break; }

    if (ignore_zero && right >= 0 && ms.get(right).getY() == 0)
      for (int i = right; i < ms.size(); i++) if (ms.get(i).getY() != 0) { right = i; break; }

    return (left >= 0 && right >= left ? Range.closed(ms.get(left), ms.get(right)) : null);
  }
  public static <T extends Point> Multimap<Integer, T> boundry(T m, List<T> ms, boolean ignore_zero, int extension)
  {
    // locate the point that's the cloest to 'm'
    int index  = Collections.binarySearch(ms, m), left, right;
    if (index >= 0)
    {
      left = index; right = index;
    }
    else  // (-(insertion point) - 1)
    {
      index = -1 * index - 1;
      left = (index > 0 ? index-1 : -1);
      right = (index < ms.size() ? index : -1);
    }
    Multimap<Integer, T> boundary = HashMultimap.create();

    for (int i = left; i >= 0; i--)
      if (!ignore_zero || ms.get(i).getY() != 0)
      {
        boundary.put(-1, ms.get(i));
        // quite if we have enough point already
        if (boundary.get(-1).size() >= extension) break;
      }

    for (int i = right; i < ms.size(); i++)
      if (!ignore_zero || (i >= 0 && ms.get(i).getY() != 0))
      {
        boundary.put(1, ms.get(i));
        // quite if we have enough point already
        if (boundary.get(1).size() >= extension) break;
      }

    return boundary;
  }
  public static Range<Point> boundry(Double x0, Double x1, int start, List<? extends Point> points, Range<Point> range)
  {
    // doing nothing
    if (x0 == null || x1 == null || x1 < x0) return range;

    Point lower=range.lowerEndpoint(), upper=range.upperEndpoint();
    for (int k = start; k < points.size(); k++)
    {
      if (points.get(k).getX() >= x0)
      {
        start = k; lower=points.get(k);
        for (int i = start; i < points.size(); i++)
        {
          if (points.get(i).getX() >= x1)
          {
            // let's interpolate the numbers
            upper=points.get(i); return Range.closed(lower, upper);
          }
        }
      }
    }
    return range;
  }
  public static <T extends Point> Double centroid(Collection<T> points)
  {
    return centroid(points, null, null);
  }
  public static <T extends Point> Double centroid(Collection<T> points, Double x0, Double x1)
  {
    if (! Tools.isSet(points)) return null;

    double sumXY = 0, sumY = 0;
    for (Point xy : points)
    {
      if ((x0 == null || xy.getX() >= x0) &&
          (x1 == null || xy.getX() <= x1))
      {
        sumXY += xy.getX() * xy.getY();
        sumY  += xy.getY();
      }
    }
    return sumY != 0 ? sumXY / sumY : null;
  }
  public static <T extends Point> Double sumY(Collection<T> data)
  {
    if (data==null) return null;

    double sum = 0d;
    for (T xy : data) sum += xy.getY();

    return sum;
  }
  public static <T extends Point> Double sumY(List<T> data, int i0)
  {
    if (data==null) return null;

    double sum = 0d;
    for (int i=i0; i<data.size(); i++) sum += data.get(i).getY();

    return sum;
  }
  public static <T extends Point> List<Double> toYs(List<T> data)
  {
    if (Tools.isSet(data))
    {
      List<Double> ys = new ArrayList<>(data.size());
      for (T xy : data) ys.add(xy.getY());
      return ys;
    }
    return null;
  }
  public static <T extends Point> int findClosest(List<T> pts, double x)
  {
    if (pts==null) return -1;

    int best=-1;
    for (int i=0; i<pts.size(); i++)
      if (best==-1 || Math.abs(pts.get(i).getX()-x) < Math.abs(pts.get(i).getX()-pts.get(best).getX())) { best=i; }

    return best;
  }
}
