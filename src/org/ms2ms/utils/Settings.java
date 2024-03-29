package org.ms2ms.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: wyu
 * Date: 5/20/14
 * Time: 10:36 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class Settings implements Serializable, Cloneable
{
  protected Map<String, Object> properties;

  public Settings() { super(); properties = new HashMap<>();  }
  public Settings(Settings s)
  {
    super(); properties = new HashMap<>(s.properties);
  }

  protected Double    getDouble( String s, Double _def) { return getDouble(s)==null?_def:getDouble(s); }
  protected Float     getFloat(  String s, Float  _def) { return getFloat( s)==null?_def:getFloat( s); }

  protected Character getChar(   String s) { return properties!=null?(Character )properties.get(s):0; }
  protected Double    getDouble( String s) { return properties!=null?(Double    )properties.get(s):null; }
  protected Float     getFloat(  String s) { return properties!=null?(Float     )properties.get(s):null; }
  protected String    getString( String s) { return properties!=null?(String    )properties.get(s):null; }
  protected Long      getLong(   String s) { return properties!=null?(Long      )properties.get(s):null; }
  protected Integer   getInteger(String s) { return properties!=null?(Integer   )properties.get(s):null; }
  protected byte[]    getBytes(  String s) { return properties!=null?(byte[]    )properties.get(s):null; }
  protected Boolean   getBoolean(String s) { return properties!=null?(Boolean   )properties.get(s):null; }

  protected Settings set(String k, Object v) { properties.put(k,v); return this; }
  protected Settings update(String k, Object v)
  {
    if (v==null) properties.remove(k); else properties.put(k,v);
    return this;
  }
  public StringBuffer show()
  {
    StringBuffer buf = new StringBuffer();

    if (Tools.isSet(properties))
      for (String key : properties.keySet())
        buf.append(key+"\t"+properties.get(key)+"\n");

    return buf;
  }
  @Override
  protected Settings clone() throws CloneNotSupportedException
  {
    Settings cloned = (Settings )super.clone();
    cloned.properties = new HashMap<>(properties);
    return cloned;
  }
}
