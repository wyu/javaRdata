package org.ms2ms.data;

public class AnnotatedPoint extends Point
{
  private Features mAnnotations;

  public AnnotatedPoint() { super(); }

  public Features       getAnnotations() { return mAnnotations; }

  public AnnotatedPoint addAnnotation(String tag, Object val)
  {
    if (mAnnotations==null) mAnnotations = new Features();
    mAnnotations.add(tag, val);

    return this;
  }
}
