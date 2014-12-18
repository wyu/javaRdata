package org.ms2ms.graph;

import org.ms2ms.utils.Tools;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   12/9/14
 */
public class PropertyEdge extends Property
{
  protected Double mScore;
  protected Long mID, mSource, mTarget;
  protected String mLabel = null, mDescription, mUrl;
//  protected Feature mProperties = new Feature();

  public PropertyEdge() { super(); }
  public PropertyEdge(double s) { super(); mScore = s; }

  public Double getScore() { return mScore; }
  public PropertyEdge setScore(Double s) { mScore = s; return this; }

  public String getId() { return "e" + hashCode(); }
  public String getLabel() { return mLabel != null ? mLabel : Tools.d2s(getScore(), 1); }
  public String getDescription() { return mDescription; }
  public String getUrl() { return mUrl; }
  public Long getID() { return mID; }
  public Long getSource() { return mSource; }
  public Long getTarget() { return mTarget; }

//  public Feature getFeature() { return mProperties; }

  public PropertyEdge setID(Long s) { mID=s; return this; }

  public PropertyEdge setDescription(String s) { mDescription = s; return this; }
  public PropertyEdge setUrl(String s) { mUrl = s; return this; }
  public PropertyEdge setLabel(String s) { mLabel = s; return this; }

  public PropertyEdge setSource(Long s) { mSource=s; return this; }
  public PropertyEdge setTarget(Long s) { mTarget=s; return this; }

  @Override
  public String toString()
  {
    return mLabel!=null?mLabel:(mSource+" --> "+mTarget);
  }
  @Override
  public boolean equals(Object s)
  {
    if (s instanceof PropertyEdge)
    {
      PropertyEdge edge = (PropertyEdge)s;
      return (mID==null || mID.equals(edge.getID())) &&
          (mSource==null || mSource.equals(edge.getSource()) &&
              (mTarget==null || mTarget.equals(edge.getTarget())));
    }
    return false;
  }
  @Override
  public PropertyEdge clone()
  {
    PropertyEdge cloned = null;
    try
    {
      cloned = (PropertyEdge)super.clone();

//      if (mProperties!=null) cloned.mProperties = mProperties.clone();
      if (mLabel     !=null) cloned.mLabel = new String(mLabel);
      if (mDescription!=null) cloned.mDescription = new String(mDescription);
      if (mUrl       !=null) cloned.mUrl = new String(mUrl);
      if (mID        !=null) cloned.mID   = new Long(mID);
      if (mSource    !=null) cloned.mSource = new Long(mSource);
      if (mTarget    !=null) cloned.mTarget   = new Long(mTarget);
      if (mScore!=null) cloned.mScore = new Double(mScore);
    }
    catch (CloneNotSupportedException e) {}

    return cloned;
  }
}
