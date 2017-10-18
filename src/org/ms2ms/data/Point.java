package org.ms2ms.data;

import org.ms2ms.utils.IOs;
import org.ms2ms.utils.Tools;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   1/23/15
 */
public class Point implements Comparable<Point>, Binary
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
  @Override
  public String toString()
  {
    return Tools.d2s(getX(), 2) + "\t" + Tools.d2s(getY(), 2);
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
