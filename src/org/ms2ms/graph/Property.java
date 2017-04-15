package org.ms2ms.graph;

import com.hfg.xml.XMLNode;
import org.ms2ms.utils.IOs;
import org.ms2ms.utils.Strs;
import org.ms2ms.utils.Tools;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   12/9/14
 */
public class Property  implements Cloneable
{
  private boolean mIsValid = true;
  private SortedMap<String, String> mProperties;

  public Property() { super(); }
  public Property(Map<String, String> props) { super(); setProperties(props); }

  public boolean hasProperty(String s) { return getProperties()!=null && getProperties().containsKey(s); }

  public SortedMap<String, String> getProperties()         { return mProperties; }
  public String              getProperty(String key) { return mProperties != null ? mProperties.get(key) : null; }
  public Integer             getProperty(String key, Integer _default)
  {
    Double val  = Double.valueOf(getProperty(key, (Double )null));
    return val != null ? val.intValue() : _default;
  }
  public Long                getProperty(String key, Long _default)
  {
    Double val  = getProperty(key, (Double )null);
    return val != null ? val.longValue() : _default;
  }
  public Float               getProperty(String key, Float _default)
  {
    Double val  = getProperty(key, (Double )null);
    return val != null ? val.floatValue() : _default;
  }
  public Double              getProperty(String key, Double _default)
  {
    String str = getProperty(key);
    if (Strs.isSet(str) && str.indexOf(",") >= 0)
      str = str.replaceAll(",", "");

    Double val  = (str!=null?Double.valueOf(str):null);
    return val != null ? val : _default;
  }
  public Double[]            getProperty(String key, Double _default, String delimiter)
  {
    if (getProperty(key) == null) return null;

    String[] strs = getProperty(key).split(delimiter);
    Double[] vals = new Double[strs.length];

    for (int i = 0; i < strs.length; i++)
    {
      if (Strs.isSet(strs[i]) && strs[i].indexOf(",") >= 0)
        strs[i] = strs[i].replaceAll(",", "");

      Double v = Double.valueOf(strs[i]);
      vals[i] = (v != null ? v : _default);
    }
    return vals;
  }

  public void setProperties(Map<String, String> s)
  {
    mProperties = new TreeMap<>(s);
  }
  public void setProperty(String name, StringBuilder val) { setProperty(name, val.toString());}
  public void setProperty(String name, String val)
  {
    if (!Strs.isSet(name) || !Strs.isSet(val)) return;
    if (mProperties == null) mProperties = new TreeMap<>();
    mProperties.put(name, val);
  }
  public void appendProperty(String name, String val)
  {
    if (!Strs.isSet(name) || !Strs.isSet(val)) return;
    if (mProperties == null) mProperties = new TreeMap<>();
    String v = mProperties.get(name);
    if (v==null) v=val; else if (!v.equals(val) && v.indexOf(";"+val)<=0) v=v+";"+val;
    mProperties.put(name, v);
  }
  public Property set(XMLNode tag, String name)
  {
    if (tag!=null && Strs.equals(tag.getTagName(), "att") && Strs.equals(tag.getAttributeValue("name"), name))
    {
      setProperty(name, tag.getAttributeValue("value"));
    }
    return this;
  }
  public void setPropertyNotNull(String name, String val)
  {
    if (val != null) setProperty(name, val);
  }
  public void mergeProperty(Property p)
  {
    if (p!=null && Tools.isSet(p.getProperties()))
      for (String k : p.getProperties().keySet())
        mergeProperty(k, p.getProperty(k));
  }
  public void mergeProperty(String name, String val)
  {
    if (!Strs.isSet(name) || !Strs.isSet(val)) return;

    String old = getProperty(name);
    if ((old == null || !Tools.contains(old.split(";"), val)))
      setProperty(name, Strs.extend(old, val, ";"));
  }
  public void removeProperty(String... tags)
  {
    if (!Tools.isSet(tags) || !Tools.isSet(getProperties())) return;
    for (String t : tags) getProperties().remove(t);
  }
  public void setProperty(String name, String new_name, Map<String, String> props)
  {
    if (!Tools.isSet(props) || !Strs.isSet(name) || props.get(name) == null) return;

    if (mProperties == null) mProperties = new TreeMap<>();
    mProperties.put(new_name != null ? new_name : name, props.get(name));
  }
  public void setProperty(String name, Map<String, String> props)
  {
    setProperty(name, name, props);
  }
  public void mergeProperty(String name, Property s)
  {
    mergeProperty(name, s.getProperty(name));
  }
  public void setProperty(String name, Property s)
  {
    setProperty(name, s.getProperty(name));
  }
  public boolean isValid()          { return mIsValid; }
  public void    isValid(boolean s) { mIsValid = s; }

  public Property clone() throws CloneNotSupportedException
  {
    Property clone = (Property )super.clone();

    clone.isValid(isValid());
    clone.setProperties(new HashMap<>(getProperties()));

    return clone;
  }

  public void write(FileWriter w, String delimiter) throws IOException
  {
    String line = null;
    for (String key : getProperties().keySet())
    {
      line = Strs.extend(line, getProperty(key), delimiter);
    }
    w.write(line);
  }
  public void writeHeaders(FileWriter w, String delimiter) throws IOException
  {
    String line = null;
    for (String key : getProperties().keySet())
    {
      line = Strs.extend(line, key, delimiter);
    }
    w.write(line);
  }
  public void write(DataOutput ds) throws IOException
  {
    IOs.write(ds, isValid());
    IOs.writeStringStringMap(ds, getProperties());
  }

  public void read(DataInput ds) throws IOException
  {
    isValid(IOs.read(ds, isValid()));
    setProperties(IOs.readStringStringMap(ds, getProperties()));
  }
  @Override
  public int hashCode()
  {
    int hcode = (mIsValid?1:0);
    if (Tools.isSet(mProperties))
      for (String key : mProperties.keySet())
        if (key!=null && mProperties.get(key)!=null)
          hcode += key.hashCode() + mProperties.get(key).hashCode();

    return hcode;
  }
  public Property rename(String from, String to)
  {
    if (hasProperty(from) && to!=null && !Strs.equals(to, from))
    {
      setProperty(to, getProperty(from));
      mProperties.remove(from);
    }
    return this;
  }

//  public Property setNameValue(XMLTag tag, String name)
//  {
//    // <att name="class" value="Receptor ligand"/>
//    if (tag!=null && Strs.equals(tag.getTagName(), "att") && Toolbox.equals(tag.getAttribute("name"), name))
//    {
//      setProperty(name, tag.getAttribute("value"));
//    }
//    return this;
//  }
}
