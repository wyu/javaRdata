package org.ms2ms.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.ms2ms.utils.Tools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.SortedMap;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   12/9/14
 */
public class PropertyEdge extends DefaultWeightedEdge implements Cloneable
{
  protected Property mProperty;

  protected Double mScore;
  protected Long mID, mSource, mTarget;
  protected String mLabel = null, mDescription, mUrl;

  public PropertyEdge() { super(); }
  public PropertyEdge(double s) { super(); mScore = s; }

  public Double getScore() { return mScore; }
  public PropertyEdge setScore(Double s) { mScore = s; return this; }

  public SortedMap<String, String> getProperties() { return mProperty!=null?mProperty.getProperties():null; }

  public String getProperty(String s) { return mProperty!=null?mProperty.getProperty(s):null; }
  public String getId() { return "e" + hashCode(); }
  public String getLabel() { return mLabel; }
  public String getDescription() { return mDescription; }
  public String getUrl() { return mUrl; }
  public Long getID() { return mID; }
  public Long getSource() { return mSource; }
  public Long getTarget() { return mTarget; }

//  public Feature getFeature() { return mProperties; }

  public PropertyEdge setProperty(String name, String val)
  {
    if (mProperty==null) mProperty = new Property();
    mProperty.setProperty(name,val);

    return this;
  }

  public PropertyEdge setID(Long s) { mID=s; return this; }

  public PropertyEdge setDescription(String s) { mDescription = s; return this; }
  public PropertyEdge setUrl(String s) { mUrl = s; return this; }
  public PropertyEdge setLabel(String s) { mLabel = s; return this; }

  public PropertyEdge setSource(Long s) { mSource=s; return this; }
  public PropertyEdge setTarget(Long s) { mTarget=s; return this; }

  public static void writeCsvHeader(FileWriter w, String... keys) throws IOException
  {
    w.write("SRC_NAME,TARGET_NAME,id,LABEL,DESC,SCORE,ID,SRC_ID,TARGET_ID");

    if (Tools.isSet(keys))
      for (String key : keys) w.write(","+key);

    w.write("\n");
  }

  public void writeCsv(FileWriter w, PropertyNode src, PropertyNode tgt, String... keys) throws IOException
  {
    w.write(src.getName()+",");
    w.write(tgt.getName()+""+",");

    w.write(getId()+",");
    w.write(getLabel()+""+",");
    w.write(getDescription()+""+",");

    w.write(getScore()+""+",");

    w.write(getID()+",");
    w.write(getSource()+""+",");
    w.write(getTarget()+""+",");

    if (Tools.isSet(keys))
      for (String key : keys)
        w.write(","+getProperty(key));

    w.write("\n");
  }

  @Override
  public String toString()
  {
    return mLabel!=null?mLabel:
        (mSource!=null && mTarget!=null?(mSource+" --> "+mTarget):super.toString());
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

      if (mProperty   !=null) cloned.mProperty    = mProperty.clone();
      if (mLabel      !=null) cloned.mLabel       = new String(mLabel);
      if (mDescription!=null) cloned.mDescription = new String(mDescription);
      if (mUrl        !=null) cloned.mUrl         = new String(mUrl);
      if (mID         !=null) cloned.mID          = new Long(mID);
      if (mSource     !=null) cloned.mSource      = new Long(mSource);
      if (mTarget     !=null) cloned.mTarget      = new Long(mTarget);
      if (mScore      !=null) cloned.mScore       = new Double(mScore);
    }
    catch (CloneNotSupportedException e) {}

    return cloned;
  }
  @Override
  public int hashCode()
  {
    return super.hashCode() + Tools.hashCode(mScore,mID,mSource,mTarget,mLabel,mDescription,mUrl);
  }
}
