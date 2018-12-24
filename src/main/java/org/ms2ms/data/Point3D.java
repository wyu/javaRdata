package org.ms2ms.data;

import org.ms2ms.utils.Tools;

/**
 * Created by yuw on 1/3/17.
 */
public class Point3D implements Comparable<Point3D>
{
  private double mX, mY, mZ;

  public Point3D(double x, double y, double z) { set(x,y,z); }

  public double getX() { return mX; }
  public double getY() { return mY; }
  public double getZ() { return mZ; }
  public Point3D setX(double s) { mX=s; return this; }
  public Point3D setY(double s) { mY=s; return this; }
  public Point3D setZ(double s) { mZ=s; return this; }
  public Point3D set(double x, double y, double z) { mX=x; mY=y; mZ=z; return this; }

  @Override
  public int compareTo(Point3D o)
  {
    return Double.compare(mX, o.getX());
  }
  @Override
  public String toString()
  {
    return Tools.d2s(getX(), 2) + "\t" + Tools.d2s(getY(), 2) + "\t" + Tools.d2s(getZ(), 2);
  }
}
