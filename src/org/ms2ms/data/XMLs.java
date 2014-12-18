package org.ms2ms.data;

import com.hfg.xml.XMLException;
import com.hfg.xml.XMLNode;
import com.hfg.xml.XMLTag;
import org.ms2ms.utils.Strs;
import org.ms2ms.utils.Tools;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hliu
 * Date: 8/19/14
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMLs
{
  public static final String CLASS_NAME = "cLaSsNaMe0000";
  public static final String SUPER_NAME = "sUpErNaMe1111";

  public static XMLStreamReader newReader(String fileName) throws FileNotFoundException, XMLStreamException
  {
    return XMLInputFactory.newInstance().createXMLStreamReader(fileName, new FileInputStream(fileName));
  }
  public static boolean isA(XMLStreamReader parser, int a, String name)
  {
    return parser.getAttributeLocalName(a).equalsIgnoreCase(name);
  }
  public static Integer getInt(XMLStreamReader parser, int a)
  {
    return Integer.parseInt(parser.getAttributeValue(a));
  }

  public static XMLTag newXMLTag(String tag_name, Class cls)
  {
    XMLTag tag = new XMLTag(tag_name);
    tag.setAttribute(CLASS_NAME, cls.getSimpleName());

    return tag;
  }
  //--------------------------------------------------------------------------
  /** We're really getting the subtag of a subtag from 'tag'
   *
   * @param tag
   * @param name
   * @param required_N
   */
  public static XMLTag getSubTag(XMLTag tag, String name, Integer required_N) {
    // send back the null if the parent is null
    if (tag == null) return null;
    // couldn't find the method: getRequiredSubTagByName
    XMLTag subtag = tag.getRequiredSubtagByName(name);
    // There should only be one tag (an RDBRow) in this tag.
    if (required_N != null && subtag.getSubtags().size() != required_N) return null;
    // send back the tag
    return (XMLTag) subtag.getSubtags().get(0);
  }
  public static XMLNode getSubTagByAttr(XMLTag tag, String name, String val) {
    // send back the null if the parent is null
    if (tag == null) return null;
    // couldn't find the method: getRequiredSubTagByName
    for (XMLNode sub : tag.getXMLNodeSubtags())
    {
      if (Strs.equals(sub.getAttributeValue("text"), val)) return sub;
    }
    return null;
  }

  //--------------------------------------------------------------------------
  public static String getSubTagAttribute(XMLTag tag,
                                          String name,
                                          int    required_N,
                                          String attr_name) {
    // grab the subtag first
    XMLTag t_new = getSubTag(tag, name, required_N);
    if (t_new != null)
      for (XMLNode t : t_new.getXMLNodeSubtags())
        //XMLTag tag = (XMLTag) iter.next();
        if (t.getTagName().equalsIgnoreCase(attr_name))
          return t.getContent();
    // send back the null as the last resort
    return null;
  }

  //--------------------------------------------------------------------------
  public static XMLNode getSubTagIgnoreCase(XMLTag tag,
                                           String name) {
    for (XMLNode t : tag.getXMLNodeSubtags())
      if (t.getTagName().equalsIgnoreCase(name)) return t;
    // nothing found
    return null;
  }

  //--------------------------------------------------------------------------
  public static XMLNode getSubTagIgnoreCase(XMLTag tag,
                                           String name,
                                           String attrib_name,
                                           String attrib_val) {
    // grab the tag by the name first
    XMLNode subtag = getSubTagIgnoreCase(tag, name);
    // quit if nothing was found
    if (subtag == null) return null;
    // varify the presence of the attribute
    String inputName = subtag.getAttributeValue(attrib_name);
//    if (inputName == null)
//       throw new WorkflowException("Tag has no attribute " + attrib_name + "!");
    // further verify that the attribute is of the right value
    if (inputName.equals(attrib_val)) return subtag;
    // not verified
    return null;
  }

  public static void assertTagByName(XMLTag tag, String tag_name)
  {
    if (!tag.getTagName().equals(tag_name))
    {
      throw new XMLException("Received a '" + tag.getTagName()
          + "' tag! Requires a '" + tag_name + "' tag!");
    }
  }
  public static void assertTagByClass(XMLTag tag, Class cls)
  {
    if (tag != null && !tag.getAttribute(CLASS_NAME).equals(cls.getSimpleName()))
    {
      throw new XMLException("Class Name Mismatch: Received a '" + tag.getAttribute(CLASS_NAME)
          + "' tag! Requires a '" + cls.getSimpleName() + "'!");
    }
  }
  //----------- Methods to save to the XMLTag for individual data type --------------

  public static void saveAsSuper(XMLTag tag, XMLTag supertag)
  {
    supertag.setTagName(SUPER_NAME);
    tag.addSubtag(supertag);
  }
  public static void saveToXMLTag(XMLTag tag,
                                  String name,
                                  double value,
                                  String _format)
  {
    if (tag != null && name != null)
      tag.setAttribute(name,  String.format(_format, value));
  }
  /*
  public static void saveToXMLTag(XMLTag tag,
                                  String name,
                                  Double value)
  {
    if (tag != null && name != null && value != null)
        tag.setAttribute(name,  value.toString());
  }
  */
  public static void saveToXMLTag(XMLTag tag,
                                  String name,
                                  int    value)
  {
    if (tag != null && name != null)
      tag.setAttribute(name,  value + "");
  }
  /*
  public static void saveToXMLTag(XMLTag tag,
                                  String name,
                                  Long value)
  {
    if (tag != null && name != null && value != null)
        tag.setAttribute(name,  value.toString());
  }
  */

  public static void saveToXMLTag(XMLTag  tag,
                                  String  name,
                                  Boolean value)
  {
    if (tag != null && name != null && value != null)
      tag.setAttribute(name,  value ? "1" : "0");
  }

  /** add the string value to the attributes of the tag, if it's not null
   *
   * @param tag is a XMLTag
   * @param name is the name of the variable
   * @param value is the value of the string
   */
  public static void saveToXMLTag(XMLTag tag,
                                  String name,
                                  String value) {
    if (tag != null && name != null && value != null)
      tag.setAttribute(name,  value);
  }

  //--------------------------------------------------------------------------
  /** add the string value to the attributes of the tag, if it's not null
   *
   * @param tag is a XMLTag
   * @param name is the name of the variable
   * @param value is the value of the string
   */
  public static void saveToXMLTag(XMLTag tag,
                                  String name,
                                  Object value)
  {
    if (tag != null && name != null && value != null)
    {
      tag.setAttribute(name,  value.toString());
    }
  }

  public static XMLTag newXMLTag(String name, String val)
  {
    XMLTag t = new XMLTag(name);
    t.setContent(val);

    return t;
  }
  public static void
  saveToXMLTagAsSubTag(XMLTag tag,
                       String name,
                       String value)
  {
    if (tag != null && name != null && value != null) tag.addSubtag(newXMLTag(name, value));
  }

//  public static <T extends XMLTaggable> void
//  saveXMLTaggedToXMLTag(XMLTag  tag,
//                        String  name,
//                        T       value)
//  {
//    if (value == null) return;
//
//    XMLTag subtag = value.toXMLTag();
//    subtag.setTagName(name);
//
//    tag.addSubtag(subtag);
//  }
  public static void saveToXMLTag(XMLTag tag,
                                  String name,
                                  String elem_name,
                                  Double[] values)
  {
    if (!Tools.isSet(values)) return;

    XMLTag subtag = new XMLTag(name);
    for (Double s : values)
      subtag.addSubtag(newXMLTag(elem_name, String.format("%.2f", s)));
    tag.addSubtag(subtag);
  }
  public static void saveToXMLTag(XMLTag tag,
                                  String name,
                                  List<String> values)
  {
    if (!Tools.isSet(values)) return;

    XMLTag subtag = new XMLTag(name);
    for (String s : values)
      subtag.addSubtag(newXMLTag("value", s));
    tag.addSubtag(subtag);
  }
  public static void saveToXMLTag(XMLTag             tag,
                                  String             name,
                                  Collection<String> values)
  {
    if (!Tools.isSet(values)) return;

    XMLTag subtag = new XMLTag(name);
    for (String s : values) subtag.addSubtag(newXMLTag("value", s));
    tag.addSubtag(subtag);
  }
  //--------------------------------------------------------------------------
  public static void saveToXMLTag(XMLTag              tag,
                                  String              name,
                                  Map<String, String> values)
  {
    // abort if there is nothing to do
    if (!Tools.isSet(values)) return;

    // deposite the content of the MAP
    XMLTag subtag = new XMLTag(name);
    for (Map.Entry e : values.entrySet()) 
    {
      XMLTag t = newXMLTag("value", (String )e.getValue());
      t.setAttribute("key", (String )e.getKey());
      subtag.addSubtag(t);
    }
    tag.addSubtag(subtag);
  }
  public static <T extends Object> void
  savePropertiesToXMLTag(XMLTag              tag,
                         String              name,
                         Map<String, T> values)
  {
    // abort if there is nothing to do
    if (!Tools.isSet(values)) return;

    // deposite the content of the MAP
    XMLTag subtag = new XMLTag(name);
    for (Map.Entry e : values.entrySet())
    {
      if (e.getValue() != null)
      {
        XMLTag t = newXMLTag("value", e.getValue().toString());

        if (! (e.getValue() instanceof String))
        {
          String type = null;
          if (e.getValue() instanceof Integer)     type = "int";
          else if (e.getValue() instanceof Float)  type = "float";
          else if (e.getValue() instanceof Double) type = "double";
          else if (e.getValue() instanceof Long)   type = "long";

          if (type != null) t.setAttribute("type", type);
        }

        t.setAttribute("key", (String )e.getKey());
        subtag.addSubtag(t);
      }
    }
    tag.addSubtag(subtag);
  }
/*
  public static <T extends XMLTaggable> void
  saveXMLTaggedsToXMLTag(XMLTag              tag,
                         String              name,
                         Map<String, T> values)
  {
    // abort if there is nothing to do
    if (!Tools.isSet(values)) return;

    // deposite the content of the MAP
    XMLTag subtag = new XMLTag(name);
    for (String s : values.keySet()) {
      if (isSet(s) && values.get(s) != null)
      {
        XMLTag t = values.get(s).toXMLTag();
        saveToXMLTag(t, "MAP_KEY", s);
        subtag.addSubtag(t);
      }
    }
    tag.addSubtag(subtag);
  }
  public static <T extends XMLTaggable> void
  saveXMLTaggedsToXMLTag(XMLTag  tag,
                         String  name,
                         List<T> values)
  {
    if (!Tools.isSet(values)) return;

    XMLTag subtag = new XMLTag(name);
    for (T s : values)
      subtag.addSubtag(s.toXMLTag());
    tag.addSubtag(subtag);
  }
  public static <T extends XMLTaggable> void
  saveXMLTaggedsToXMLTag(XMLTag        tag,
                         String        name,
                         Collection<T> values)
  {
    if (!Tools.isSet(values)) return;

    XMLTag subtag = new XMLTag(name);
    for (T s : values)
      subtag.addSubtag(s.toXMLTag());
    tag.addSubtag(subtag);
  }
  public static <T extends XMLTaggable> void
  saveMapCollectionToXMLTag(XMLTag                     tag,
                            String                     name,
                            Map<String, Collection<T>> values)
  {
    if (!Tools.isSet(values)) return;

    XMLTag subtag = new XMLTag(name);
    // save the keys
    saveToXMLTag(subtag, name, values.keySet());

    for (String s : values.keySet())
    {
      saveXMLTaggedsToXMLTag(subtag, s, values.get(s));
    }
    tag.addSubtag(subtag);
  }
  public static <T extends XMLTaggable> void
  saveMapListToXMLTag(XMLTag               tag,
                      String               name,
                      Map<String, List<T>> values)
  {
    if (!Tools.isSet(values)) return;

    XMLTag subtag = new XMLTag(name);
    for (String s : values.keySet())
    {
      XMLTag tt = new XMLTag("KEY_VALUE");
      saveToXMLTag(tt, "KEY", s);
      saveXMLTaggedsToXMLTag(tt, "VALUES", values.get(s));
      subtag.addSubtag(tt);
    }
    tag.addSubtag(subtag);
  }
  public static <K extends XMLTaggable, T extends XMLTaggable> void
  saveXMLTaggableMapList(XMLTag          tag,
                         String          name,
                         Map<K, List<T>> values)
  {
    if (!Tools.isSet(values)) return;

    XMLTag subtag = new XMLTag(name);

    for (K k : values.keySet())
    {
      XMLTag tt = k.toXMLTag();
      saveXMLTaggedsToXMLTag(tt, "VALUES", values.get(k));
      subtag.addSubtag(tt);
    }
    tag.addSubtag(subtag);
  }
  public static <T extends XMLTaggable> void
  saveMapSetToXMLTag(XMLTag              tag,
                     String              name,
                     Map<String, Set<T>> values)
  {
    if (!Tools.isSet(values)) return;

    XMLTag subtag = new XMLTag(name);
    // save the keys
    saveToXMLTag(subtag, name, values.keySet());

    for (String s : values.keySet())
    {
      saveXMLTaggedsToXMLTag(subtag, s, values.get(s));
    }
    tag.addSubtag(subtag);
  }
*/
  //----------- Methods to update from the XMLTag for individual data type --------------

  public static Double getDouble(XMLTag tag, String name)
  {
    try
    {
      String str = tag.getAttributeValue(name);
      if (Tools.isSet(str)) return new Double(str);
    }
    catch (Exception e) {}
    // send the null out since we have nothing here
    return null;
  }
  public static Float getFloat(XMLTag tag, String name)
  {
    try
    {
      String str = tag.getAttributeValue(name);
      if (Tools.isSet(str)) return new Double(str).floatValue();
    }
    catch (Exception e) {}
    // send the null out since we have nothing here
    return null;
  }
  public static Integer getInteger(XMLTag tag, String name)
  {
    try
    {
      String str = tag.getAttributeValue(name);
      if (Tools.isSet(str)) return new Integer(str);
    }
    catch (Exception e) {}
    // send the null out since we have nothing here
    return null;
  }
  public static Boolean getBoolean(XMLTag tag, String name)
  {
    try
    {
      String str = tag.getAttributeValue(name);
      if (Tools.isSet(str)) return str.equals("1") ? true : false;
    }
    catch (Exception e) {}
    // always send the variable back out
    return null;
  }
  public static Long
  getLong(XMLTag tag, String name)
  {
    try
    {
      String str = tag.getAttributeValue(name);
      if (Tools.isSet(str)) return new Long(str);
    }
    catch (Exception e) {}
    // always send the variable back out
    return null;
  }

  public static Double getDouble(XMLTag tag, String name, Double def)
  {
    try
    {
      String str = tag.getAttributeValue(name);
      if (Tools.isSet(str)) return new Double(str);
    }
    catch (Exception e) {}

    // if something goes wrong, set the variable to the default
    return def;
  }
  //--------------------------------------------------------------------------
  public static String getString(XMLTag tag, String name)
  {
    try { return tag.getAttributeValue(name); }
    catch (Exception e) { }

    return null;
  }
  public static String getStringFromSubTag(XMLTag tag, String name)
  {
    try
    {
      // expect exactly one subtag
      XMLTag sub = tag.getRequiredSubtagByName(name);
      return sub.getContent();
    }
    catch (Exception e) { }

    return null;
  }
//  public static XMLTaggable
//  getXMLTagged(XMLTag    tag,
//               String    name,
//               XMLTaggable var)
//  {
//    try
//    {
//      // expect exactly one subtag
//      XMLTag sub = tag.getRequiredSubTagByName(name);
//      if (sub != null) var.update(sub);
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace();
//      return null;
//    }
//
//    return var;
//  }
  //--------------------------------------------------------------------------
  public static Map<String, String> updateByXMLTag(XMLTag tag, String name, Map<String, String> values)
  {
    try
    {
      XMLTag subtag = tag.getRequiredSubtagByName(name);
      if (subtag == null) return null;
      // clear the map
      values.clear();
      for (XMLNode t : subtag.getXMLNodeSubtags())
        values.put(t.getAttributeValue("key"), t.getContent());
    }
    catch (Exception e) {}

    return values;
  }
  public static Map<String, Object> updatePropertiesByXMLTag(XMLTag tag, String name, Map<String, Object> values)
  {
    try
    {
      XMLTag subtag = tag.getRequiredSubtagByName(name);
      if (subtag == null) return null;
      // clear the map
      values.clear();
      for (XMLNode t : subtag.getXMLNodeSubtags())
      {
        String key         = t.getAttributeValue("key");
        String valueString = t.getContent();
        String type        = t.getAttributeValue("type");

        if (Tools.isSet(type))
        {
          if      (type.equals("int"))    values.put(key, new Integer(valueString));
          else if (type.equals("float"))  values.put(key, new Float(  valueString));
          else if (type.equals("double")) values.put(key, new Double( valueString));
          else if (type.equals("long"))   values.put(key, new Long(   valueString));
          else                            values.put(key, new String( valueString));
        }
        else values.put(key, new String(valueString));

        valueString = null; key = null; type = null;
      }
    }
    catch (Exception e) {}

    return values;
  }

  public static Double[] getDoubles(XMLTag tag, String name)
  {
    try
    {
      XMLTag subtag = tag.getRequiredSubtagByName(name);
      Double[] values = new Double[subtag.getSubtags().size()];
      int count = 0;
      for (XMLNode t : subtag.getXMLNodeSubtags())
        values[count++] = new Double(t.getContent());

      return values;
    }
    catch (Exception e) {}

    return null;
  }

  //--------------------------------------------------------------------------
  public static List<String>
  updateByXMLTag(XMLTag       tag,
                 String       name,
                 List<String> values)
  {
    if (!Tools.isSet(name) || tag == null || values == null) return values;

    try
    {
      XMLTag subtag = tag.getRequiredSubtagByName(name);
      // clear the list before the addition
      values.clear();
      for (XMLNode t : subtag.getXMLNodeSubtags()) values.add(t.getContent());

      return values;
    }
    catch (Exception e) {}

    return null;
  }
//  public static <T extends XMLTaggable> List<T>
//  updateXMLTaggedsByXMLTag(XMLTag   tag,
//                           String   name,
//                           List<T>  ts,
//                           T        template)
//  {
//    if (!Tools.isSet(name) || tag  == null || ts == null || template == null) return ts;
//
//    try {
//      XMLTag subtag = tag.getRequiredSubtag(name);
//      // clear the list prior to addition
//      ts.clear();
//      for (Object t : subtag.getSubTags())
//      {
//        XMLTaggable new_t = template.getClass().newInstance();
//        new_t.update((XMLTag )t);
//        ts.add((T) new_t);
//      }
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace();
//      return null;
//    }
//
//    return ts;
//  }
/*
  public static <T extends XMLTaggable> Set<T>
  updateXMLTaggedSetByXMLTag(XMLTag   tag,
                             String   name,
                             Set<T>   ts,
                             T        template)
  {
    if (!Tools.isSet(name) || tag      == null ||
        ts == null   || template == null) return ts;

    try {
      XMLTag subtag = tag.getRequiredSubtag(name);
      // clear the list prior to addition
      ts.clear();
      for (Object t : subtag.getSubTags())
      {
        XMLTaggable new_t = template.getClass().newInstance();
        new_t.update((XMLTag )t);
        ts.add((T) new_t);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }

    return ts;
  }
  public static <T extends XMLTaggable> Map<String, T>
  updateXMLTaggedsByXMLTag(XMLTag   tag,
                           String   name,
                           Map<String, T>  map,
                           T        template)
  {
    if (!Tools.isSet(name) || tag  == null || map == null || template == null) return map;

    try {
      XMLTag subtag = tag.getRequiredSubTagByName(name);
      // clear the list prior to addition
      map.clear();
      for (XMLTag t : subtag.getSubTags())
      {
        String key = getString(t, "MAP_KEY");
        XMLTaggable new_t = template.getClass().newInstance();
        new_t.update((XMLTag )t);

        map.put(key, (T) new_t);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }

    return map;
  }
  public static <T extends XMLTaggable> Collection<T>
  updateXMLTaggedsByXMLTag(XMLTag         tag,
                           String         name,
                           Collection<T>  ts,
                           T              template)
  {
    if (!Tools.isSet(name) || tag      == null ||
        ts == null  || template == null) return ts;

    try {
      XMLTag subtag = tag.getRequiredSubtag(name);
      // clear the list prior to addition
      ts.clear();
      for (Object t : subtag.getSubTags())
      {
        XMLTaggable new_t = template.getClass().newInstance();
        new_t.update((XMLTag )t);
        ts.add((T) new_t);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }

    return ts;
  }
  public static <T extends XMLTaggable> Map<String, Collection<T>>
  updateMapCollectionByXMLTag(XMLTag                     tag,
                              String                     name,
                              Map<String, Collection<T>> values,
                              T                          template)
  {
    if (!Tools.isSet(name)    || tag      == null ||
        values == null || template == null) return values;

    try {
      XMLTag subtag = tag.getRequiredSubtag(name);
      // clear the list prior to addition
      values.clear();
      // retrieve the keys
      List<String> keys = updateByXMLTag(subtag, name, new ArrayList<String>());
      for (String k : keys)
      {
        Collection<T> ts = updateXMLTaggedsByXMLTag(subtag, k, new ArrayList<T>(), template);
        values.put(k, ts);
      }
    }
    catch (Exception e) { return null; }

    return values;
  }
  public static <K extends XMLTaggable, T extends XMLTaggable> SortedMap<K, List<T>>
  updateXMLTaggableMapList(XMLTag       tag,
                           String          name,
                           SortedMap<K, List<T>> values,
                           K               key,
                           T               val)
  {
    if (!Tools.isSet(name)    || tag == null ||
        values == null || key == null || val == null) return values;

    try {
      XMLTag subtag = tag.getRequiredSubtag(name);
      // clear the list prior to addition
      values.clear();
      for (XMLTag t : subtag.getSubTags())
      {
        XMLTaggable new_k = key.getClass().newInstance();
        new_k.update(t);
        List<T> ts = updateXMLTaggedsByXMLTag(t, "VALUES", new ArrayList<T>(), val);
        values.put((K )new_k, ts);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }

    return values;
  }
  public static <T extends XMLTaggable> SortedMap<String, List<T>>
  updateMapListByXMLTag(XMLTag               tag,
                        String               name,
                        SortedMap<String, List<T>> values,
                        XMLTaggable            template)
  {
    if (!Tools.isSet(name)    || tag      == null ||
        values == null || template == null) return values;

    try {
      XMLTag subtag = tag.getRequiredSubtag(name);
      // clear the list prior to addition
      values.clear();
      // retrieve the keys
      for (XMLTag t : subtag.getSubTags())
      {
        String key = getString(t, "KEY");
        List<T> ts = updateXMLTaggedsByXMLTag(t, "VALUES", new ArrayList<T>(), (T) template);
        values.put(key, ts);
      }
    }
    catch (Exception e) { return null; }

    return values;
  }
  public static <T extends XMLTaggable> Map<String, Set<T>>
  updateMapSetByXMLTag(XMLTag               tag,
                       String               name,
                       Map<String, Set<T>> values,
                       XMLTaggable            template)
  {
    if (!Tools.isSet(name)    || tag      == null ||
        values == null || template == null) return values;

    try {
      XMLTag subtag = tag.getRequiredSubtag(name);
      // clear the list prior to addition
      values.clear();
      // retrieve the keys
      List<String> keys = updateByXMLTag(subtag, name, new ArrayList<String>());
      for (String k : keys)
      {
        Set<T> ts = updateXMLTaggedSetByXMLTag(subtag, k, new TreeSet<T>(), (T )template);
        values.put(k, ts);
      }
    }
    catch (Exception e) { return null; }

    return values;
  }
*/
}

