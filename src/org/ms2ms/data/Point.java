package org.ms2ms.data;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   1/23/15
 */
public class Point implements Comparable<Point>
{
  private double mX, mY;

  public Point(double x, double y) { set(x,y); }

  public double getX() { return mX; }
  public double getY() { return mY; }
  public Point setX(double s) { mX=s; return this; }
  public Point setY(double s) { mY=s; return this; }
  public Point set(double x, double y) { mX=x; mY=y; return this; }

  @Override
  public int compareTo(Point o)
  {
    return Double.compare(mX, o.getX());
  }
}
