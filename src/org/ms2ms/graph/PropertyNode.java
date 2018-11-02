package org.ms2ms.graph;

import org.ms2ms.utils.Strs;
import org.ms2ms.utils.Tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

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
  public static final String CATEGORY = "category";
  public static final String TYPE     = "type";
  public static final String UID      = "uid";

//  protected Feature mProperties = new Feature();
  protected String mName;
  protected Long mID;

  public PropertyNode() { super(); }
  public PropertyNode(String n) { super(); setName(n); }
  public PropertyNode(String n, String t) { super(); setName(n); setType(t); }

  public String       getName()     { return mName; }
  public String       getType()     { return getProperty(TYPE); }
  public String       getCategory() { return getProperty(CATEGORY); }
  public String       getUID()      { return getProperty(UID); }

  public Long getID() { return mID; }
//  public Feature getFeature() { return mProperties; }

  public PropertyNode setName(    String s) { mName=s;                  return this; }
  public PropertyNode setID(        Long s) { mID=s;                    return this; }
  public PropertyNode setCategory(String s) { setProperty(CATEGORY, s); return this; }
  public PropertyNode setUID(     String s) { setProperty(UID, s);      return this; }
  public PropertyNode setType(    String s) { setProperty(TYPE, s);     return this; }

  public boolean isType(String s) { return Strs.equals(getType(), s); }
//  public PropertyNode setProperty(String n, String v)
//  {
////    if (mProperties==null) mProperties=new Feature();
//    if (n!=null && v!=null) setProperty(n, v);
//    return this;
//  }
//  public String  getProperty(String s) { return getProperties()!=null ? getProperties().get(s) : null; }

  public static void writeCsvHeader(FileWriter w, String... keys) throws IOException
  {
    w.write("NAME,ID,TYPE");

    if (Tools.isSet(keys))
      for (String key : keys)
        if (!key.equals(TYPE)) w.write(","+key);

    w.write("\n");
  }

  public void writeCsv(FileWriter w, String... keys) throws IOException
  {
    w.write(wrap(getUID())+",");
    w.write(wrap(getName())+",");
    w.write(wrap(getID())+",");
    w.write(wrap(getType()));

    if (Tools.isSet(keys))
      for (String key : keys)
        if (!key.equals(TYPE) && !TYPE.equals(key))
          w.write(","+wrap(getProperty(key)));

    w.write("\n");
  }
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
      return (mID==null || mID.equals(node.getID())) &&
       (getType()==null || getType().equals(node.getType())) &&
           (mName==null || mName.equals(node.getName()));
    }
    return false;
  }
  @Override
  public int hashCode()
  {
    return super.hashCode() + Tools.hashCode(mID,mName);
  }

  @Override
  public String toString()
  {
    return mID!=null?mID.toString():(mName!=null?mName:super.toString());
  }
}
