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
public class PropertyNode extends Property
{
//  protected Feature mProperties = new Feature();
  protected String mName;
  protected Long mID;

  public PropertyNode() { super(); }
  public PropertyNode(String n) { super(); setName(n); }

  public String       getName()         { return mName; }
  public Long getID() { return mID; }
//  public Feature getFeature() { return mProperties; }

  public PropertyNode setName(String s) { mName=s; return this; }
  public PropertyNode setID(Long s) { mID=s; return this; }

//  public PropertyNode setProperty(String n, String v)
//  {
////    if (mProperties==null) mProperties=new Feature();
//    if (n!=null && v!=null) setProperty(n, v);
//    return this;
//  }
//  public String  getProperty(String s) { return getProperties()!=null ? getProperties().get(s) : null; }

  @Override
  public PropertyNode clone()
  {
    PropertyNode cloned = null;
    try
    {
      cloned = (PropertyNode)super.clone();

      if (mName      !=null) cloned.mName = new String(mName);
      if (mID        !=null) cloned.mID   = new Long(mID);
    }
    catch (CloneNotSupportedException e) {}

    return cloned;
  }
  @Override
  public boolean equals(Object s)
  {
    if (s instanceof PropertyNode)
    {
      PropertyNode node = (PropertyNode)s;
      return (mID==null || mID.equals(node.getID())) && (mName==null || mName.equals(node.getName()));
    }
    return false;
  }
  @Override
  public int hashCode()
  {
    return super.hashCode() + Tools.hashCode(mID,mName);
  }
}
