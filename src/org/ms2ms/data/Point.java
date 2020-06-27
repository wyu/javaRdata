package org.ms2ms.data;

import org.ms2ms.utils.IOs;
import org.ms2ms.utils.Tools;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   1/23/15
 */
public class
Point implements Comparable<Point>, Binary
{
  private double mX, mY;

  public static class IntensityDesendComparator implements Comparator<Point> {
    public int compare(Point o1, Point o2) {
      return o1 != null && o2 != null ? Double.compare(o2.getY(), o1.getY()) : 0;
    }
  }

  public Point() { set(0d,0d); }
  public Point(double x, double y) { set(x,y); }
  public Point(Point s) { setX(s.getX()); setY(s.getY()); }

  public double getX() { return mX; }
  public double getY() { return mY; }
  public Point setX(double s) { mX=s; return this; }
  public Point setY(double s) { mY=s; return this; }
  public Point set(double x, double y) { mX=x; mY=y; return this; }
  public Point set(Point s) { mX=s.getX(); mY=s.getY(); return this; }

  @Override
  public int compareTo(Point o)
  {
    return Double.compare(mX, o.getX());
  }
  @Override
  public String toString()
  {
    return Tools.d2s(getX(), 2) + "," + Tools.d2s(getY(), 2);
  }

  @Override
  public void write(DataOutput ds) throws IOException
  {
    IOs.write(ds, mX); IOs.write(ds, mY);
  }

  @Override
  public void read(DataInput ds) throws IOException
  {
    mX = IOs.read(ds, mX);
    mY = IOs.read(ds, mY);
  }
}
