package org.ms2ms.utils;

import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import org.ms2ms.data.Binary;
import org.ms2ms.math.Stats;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: wyu
 * Date: 7/13/14
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class IOs
{
  public static boolean exists(String s)
  {
    if (!Strs.isSet(s)) return false;
    return new File(s).exists();
  }
  public static void write(String f, String data)
  {
    FileWriter w = null;
    try
    {
      try
      {
        w = new FileWriter(f);
        w.write(data);
      }
      catch (IOException ie)
      {
        System.out.println("Failed to write the output to " + f);
      }
      finally
      {
        if (w!=null) w.close();
      }
    }
    catch (IOException ie) {}
  }
  public static <T extends Binary> T read(DataInput ds, T t) throws IOException
  {
    // indicating null
    if (isnull(ds)) return null;

    try { if (t == null) t = (T )t.getClass().newInstance(); } catch (Exception e) {}

    t.read(ds);

    return t;
  }
  public static <T extends Binary> void write(DataOutput ds, T t) throws IOException
  {
    isnull(ds, t);
    if (t != null) t.write(ds);
  }
//  public static void write(DataOutput ds, Subject.eStatus t) throws IOException
//  {
//    write(ds, t != null ? t.toString() : (String )null);
////    isnull(ds, t);
////    if (t != null) write(ds, t.toString());
//  }

//  public static Subject.eStatus read(DataInput ds, Subject.eStatus t) throws IOException
//  {
//    return Hit.sNameStatus.get(read(ds, ""));
//    //String s = read(ds, "");
//    //try { t = Subject.eStatus.valueOf(s); } catch (Exception e) {}
//    //return t;
//  }
//  public static void write(DataOutput ds, Candidate.eVerdict t) throws IOException
//  {
//    write(ds, t != null ? t.toString() : (String )null);
////    isnull(ds, t);
////    if (t != null) write(ds, t.toString());
//  }
  public static void write(DataOutput ds, BitSet t) throws IOException
  {
    byte[] bs = t != null ? Tools.toByteArray(t) : null;
    // write it out as an integer array
    write(ds, bs != null ? bs.length : 0);
    if (bs != null && bs.length > 0) ds.write(bs);
  }
  public static BitSet read(DataInput ds, BitSet t) throws IOException
  {
    int       n = read(ds, 0);
    BitSet data = null;

    if (n > 0)
    {
      byte[] bs = new byte[n];
      ds.readFully(bs);
      data = Tools.fromByteArray(bs);
    }
    return data;
  }
//  public static Candidate.eVerdict read(DataInput ds, Candidate.eVerdict t) throws IOException
//  {
//    return Hit.sNameVerdict.get(read(ds, "")); // not handling the exception here, wyu 20111230
//    //String s = read(ds, "");
//    //try { t = Candidate.eVerdict.valueOf(s); } catch (Exception e) {}
//    //return t;
//  }
  public static void writeLongIntMap(DataOutput ds, Map<Long, Integer> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
      for (Long key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
  }
  public static <T extends Binary> void write(DataOutput ds, Collection<T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);
    if (Tools.isSet(data))
    {
      for (T t : data) t.write(ds);
    }
  }
  public static void write(DataOutput ds, double[] data) throws IOException
  {
    write(ds, data != null ? data.length : 0);
    if (data != null && data.length > 0)
    {
      for (double t : data) write(ds, t);
    }
  }
  public static <T extends Binary> Collection<T> read(DataInput ds, Collection<T> data, T template) throws Exception
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new ArrayList<T>();
      for (int i = 0; i < n; i++)
      {
        T new_t = (T )template.getClass().newInstance();
        new_t.read(ds);
        data.add(new_t);
      }
    }
    return data;
  }
  public static double[] read(DataInput ds, double[] data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new double[n];
      for (int i = 0; i < n; i++)
      {
        data[i] = read(ds, 0d);
      }
    }
    return data;
  }
  public static <T extends Binary> void write(DataOutput ds, List<T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);
    if (Tools.isSet(data))
    {
      for (T t : data) t.write(ds);
    }
  }
  public static void writeLongs(DataOutput ds, Collection<Long> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);
    if (Tools.isSet(data))
    {
      for (Long t : data) write(ds, t);
    }
  }
  public static void writeFloats(DataOutput ds, List<Float> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);
    if (Tools.isSet(data))
    {
      for (Float t : data) write(ds, t);
    }
  }
  public static <T extends Binary> List<T> read(DataInput ds, List<T> data, T template) throws Exception
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      //if (data == null) data = new ArrayList<T>();
      if      (data == null) data = new ArrayList<T>(n);
      else if (data instanceof ArrayList) ((ArrayList )data).ensureCapacity(n);
      for (int i = 0; i < n; i++)
      {
        T new_t = (T )template.getClass().newInstance();
        new_t.read(ds);
        data.add(new_t);
      }
    }
    return data;
  }
  public static <T extends Binary> List<T> skip(DataInput ds, List<T> data, T template) throws Exception
  {
    int n = read(ds, 0);
    if (n > 0)
      for (int i = 0; i < n; i++)
      {
        T new_t = (T )template.getClass().newInstance();
        new_t.read(ds);
      }

    return data;
  }
  public static List<Long> readLongs(DataInput ds, List<Long> data) throws Exception
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if      (data == null) data = new ArrayList<Long>(n);
      else if (data instanceof ArrayList) ((ArrayList )data).ensureCapacity(n);
      for (int i = 0; i < n; i++)
      {
        data.add(read(ds, 0L));
      }
    }
    return data;
  }
  public static List<Float> readFloats(DataInput ds, List<Float> data) throws Exception
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if      (data == null) data = new ArrayList<Float>(n);
      else if (data instanceof ArrayList) ((ArrayList )data).ensureCapacity(n);
      for (int i = 0; i < n; i++)
      {
        data.add(read(ds, 0F));
      }
    }
    return data;
  }
  public static void writeStrs(DataOutput ds, List<String> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);
    if (Tools.isSet(data))
    {
      for (String t : data) write(ds, t);
    }
  }
  public static List<String> readStrs(DataInput ds, List<String> data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new ArrayList<String>();
      for (int i = 0; i < n; i++)
      {
        data.add(read(ds, ""));
      }
    }
    return data;
  }
  public static <K extends Binary, T extends Binary> Map<K, T>
  read(DataInput ds, Map<K, T> data, K key, T template) throws Exception
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<K, T>();
      for (int i = 0; i < n; i++)
      {
        K new_k = (K )key.getClass().newInstance();
        T new_t = (T )template.getClass().newInstance();
        new_k.read(ds);
        new_t.read(ds);
        data.put(new_k, new_t);
      }
    }
    return data;
  }
  public static <T extends Binary> Map<Long, T>
  readLongMap(DataInput ds, Map<Long, T> data, T template) throws Exception
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<Long, T>();
      for (int i = 0; i < n; i++)
      {
        T new_t = (T )template.getClass().newInstance();
        Long new_k = read(ds, 0L);
        new_t.read(ds);
        data.put(new_k, new_t);
      }
    }
    return data;
  }
  public static Map<Long, Integer>
  readLongIntMap(DataInput ds, Map<Long, Integer> data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<Long, Integer>();
      for (int i = 0; i < n; i++)
        data.put(read(ds, 0L), read(ds, 0));
    }
    return data;
  }
  public static <T extends Binary> Map<Long, T>
  readLongMap(DataInput ds, Map<Long, T> data, T template, Collection<Long> ids) throws Exception
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<Long, T>();
      for (int i = 0; i < n; i++)
      {
        T new_t = (T )template.getClass().newInstance();
        Long new_k = read(ds, 0L);
        new_t.read(ds);
        if (ids == null || ids.contains(new_k)) data.put(new_k, new_t);
      }
    }
    return data;
  }
  public static Map<Long, Float>
  readLongFloat(DataInput ds, Map<Long, Float> data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<Long, Float>();
      for (int i = 0; i < n; i++)
      {
        data.put(read(ds, 0L), read(ds, 0F));
      }
    }
    return data;
  }
  public static SortedMap<Long, Long>
  readLongLong(DataInput ds, SortedMap<Long, Long> data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<Long, Long>();
      for (int i = 0; i < n; i++)
      {
        data.put(read(ds, 0L), read(ds, 0L));
      }
    }
    return data;
  }
  public static Map<Double, Long>
  readDoubleLong(DataInput ds, Map<Double, Long> data, Range<Double> key_range) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<Double, Long>();
      for (int i = 0; i < n; i++)
      {
        Double key = read(ds, 0D);
        Long   val = read(ds, 0L);
        if (key_range == null || key_range.contains(key)) data.put(key, val);
      }
    }
    return data;
  }
//  public static MapOfMap<Double, Long, Long>
//  readDoubleLongLong(DataInput ds, MapOfMap<Double, Long, Long> data, Range<Double> key_range) throws IOException
//  {
//    int n = read(ds, 0);
//
//    if (n > 0)
//    {
//      if (data == null) data = new MapOfMap<Double, Long, Long>();
//      for (int i = 0; i < n; i++)
//      {
//        Double                key = read(ds, 0D);
//        SortedMap<Long, Long> val = readLongLong(ds, new TreeMap<Long, Long>());
//        if (key_range == null || key_range.isEnclosed(key)) data.add(key, val);
//      }
//    }
//    return data;
//  }
  public static void
  writeProperties(DataOutput ds, Map<String, Object> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);
    // abort if there is nothing to do
    if (!Tools.isSet(data)) return;

    // deposite the content of the MAP
    for (String key : data.keySet())
    {
      write(ds, key);
      isnull(ds, data.get(key));
      if (data.get(key) != null)
      {
        if (data.get(key) instanceof Binary) write(ds, Binary.class.getName());
        else                                   write(ds, data.get(key).getClass().getName());

        if      (data.get(key) instanceof String)   write(ds, (String   )data.get(key));
        else if (data.get(key) instanceof Integer)  write(ds, (Integer  )data.get(key));
        else if (data.get(key) instanceof Long)     write(ds, (Long     )data.get(key));
        else if (data.get(key) instanceof Boolean)  write(ds, (Boolean  )data.get(key));
        else if (data.get(key) instanceof Float)    write(ds, (Float    )data.get(key));
        else if (data.get(key) instanceof Double)   write(ds, (Double   )data.get(key));
        else if (data.get(key) instanceof Binary) write(ds, (Binary )data.get(key));
        else throw new RuntimeException("Unsupported type for binary property, " + data.get(key).getClass().getName());
      }
    }
  }
  public static Map<String, Object>
  readProperties(DataInput ds, Map<String, Object> data) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      if (data == null) data = new HashMap<String, Object>();
      for (int i = 0; i < n; i++)
      {
        String new_k = read(ds, "");
        if (isnull(ds)) continue;
        String name = read(ds, Strs.NULL);
        //if      (name.equals(""))                       data.put(new_k, "");
        if      (name.equals(  String.class.getName())) data.put(new_k, (Object )read(ds, ""));
        else if (name.equals( Integer.class.getName())) data.put(new_k, (Object )read(ds, 0));
        else if (name.equals(    Long.class.getName())) data.put(new_k, (Object )read(ds, 0L));
        else if (name.equals( Boolean.class.getName())) data.put(new_k, (Object )read(ds, (Boolean )null));
        else if (name.equals(   Float.class.getName())) data.put(new_k, (Object )read(ds, 0f));
        else if (name.equals(  Double.class.getName())) data.put(new_k, (Object )read(ds, 0d));
        else if (name.equals(Binary.class.getName())) data.put(new_k, (Object )read(ds, (Binary )null));
        else throw new RuntimeException("Unsupported type for binary property, " + name);
        name = null;
      }
    }
    return data;
  }

  public static <T extends Comparable> void
  write(DataOutput ds, Range<T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? 1 : 0);

    if (Tools.isSet(data))
    {
      write(ds, data.lowerEndpoint() != null ? data.lowerEndpoint().toString() : null);
      write(ds, data.upperEndpoint() != null ? data.upperEndpoint().toString() : null);
    }
  }
  public static <K extends Binary, T extends Binary> void
  write(DataOutput ds, Map<K, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
    {
      for (K key : data.keySet())
      {
        key.write(ds);
        data.get(key).write(ds);
      }
    }
  }
  public static <T extends Binary> void
  writeLongMap(DataOutput ds, Map<Long, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
    {
      for (Long key : data.keySet())
      {
        write(ds, key);
        data.get(key).write(ds);
      }
    }
  }
  public static void
  writeLongFloat(DataOutput ds, Map<Long, Float> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
    {
      for (Long key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
    }
  }
  public static void
  writeLongLong(DataOutput ds, Map<Long, Long> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
    {
      for (Long key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
    }
  }
  public static void
  writeDoubleLong(DataOutput ds, Map<Double, Long> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
    {
      for (Double key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
    }
  }
//  public static void
//  writeDoubleLongLong(DataOutput ds, MapOfMap<Double, Long, Long> data) throws IOException
//  {
//    write(ds, Tools.isSet(data) ? data.getData().keySet().size() : 0);
//
//    if (Tools.isSet(data))
//    {
//      for (Double key : data.getData().keySet())
//      {
//        write(ds, key);
//        writeLongLong(ds, data.get(key));
//      }
//    }
//  }
//  public static <K extends Binary, T extends Binary> Multimap<K, T>
//  read(DataInput ds, Multimap<K, T> data, K key, T template) throws Exception
//  {
//    int n = read(ds, 0);
//
//    // new behave 20120515, null data to indicate reading only with no data accumulation
//    if (n > 0)
//    {
//      //if (data == null) data = new Multimap<K, T>();
//      for (int i = 0; i < n; i++)
//      {
//        K new_k = (K )key.getClass().newInstance();
//        new_k.read(ds);
//        List<T> new_ks = read(ds, new ArrayList<T>(), template);
//        if (data != null) data.put(new_k, new_ks);
////        if (i > 0 && i % 500  == 0) log.info(i + " of " + n + " read.");
//      }
////      if (data != null) data.trimToSize(); // to reduce the storage requirement
//    }
//    return data;
//  }
//  public static <K extends Binary, T extends Binary> void
//  write(DataOutput ds, Multimap<K, T> data) throws IOException
//  {
//    write(ds, isSet(data) ? data.keySet().size() : 0);
//    if (Tools.isSet(data))
//      for (K key : data.keySet())
//      {
//        key.write(ds);
//        write(ds, data.get(key));
//      }
//  }
//  public static <T extends Binary> Multimap<String, T>
//  readStringMap(DataInput ds, Multimap<String, T> data, T template) throws Exception
//  {
//    int n = read(ds, 0);
//
//    if (n > 0)
//    {
//      //if (data == null) data = new Multimap<String, T>();
//      // WYU 20120515, read only if data == null
//      for (int i = 0; i < n; i++)
//      {
//        if (data != null) data.put(read(ds, ""), read(ds, new ArrayList<T>(), template));
//        else
//        {
//          read(ds, "");
//          read(ds, new ArrayList<T>(), template);
//        }
//      }
////      if (data != null) data.trimToSize(); // to minize the storage requirement
//    }
//
//    return data;
//  }
//  public static <T extends Binary> Multimap<String, T>
//  skipStringMap(DataInput ds, Multimap<String, T> data, T template) throws Exception
//  {
//    int n = read(ds, 0);
//
//    if (n > 0)
//      for (int i = 0; i < n; i++)
//      {
//        read(ds, "");
//        skip(ds, new ArrayList<T>(), template);
//      }
//
//    return data;
//  }
//  public static <T extends Binary> Multimap<Integer, T>
//  readIntegerMap(DataInput ds, Multimap<Integer, T> data, T template) throws Exception
//  {
//    int n = read(ds, 0);
//
//    if (n > 0)
//    {
//      if (data == null) data = new Multimap<Integer, T>();
//      for (int i = 0; i < n; i++)
//      {
//        data.add(read(ds, 0), read(ds, new ArrayList<T>(), template));
//      }
//      if (data != null) data.trimToSize(); // to minize the storage requirement
//    }
//
//    return data;
//  }
//  public static Multimap<String, Long>
//  readStringLongs(DataInput ds, Multimap<String, Long> data) throws Exception
//  {
//    int n = read(ds, 0);
//
//    if (n > 0)
//    {
//      if (data == null) data = new Multimap<String, Long>();
//      for (int i = 0; i < n; i++)
//      {
//        data.add(read(ds, ""), readLongs(ds, new ArrayList<Long>()));
//      }
//      if (data != null) data.trimToSize(); // to minize the storage requirement
//    }
//
//    return data;
//  }
  public static Range<Long>
  readLongRange(DataInput ds, Range<Long> data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
//      if (data == null) data = Range.all();
      for (int i = 0; i < n; i++)
      {
        Double lower = Stats.toDouble(read(ds, ""));
        Double upper = Stats.toDouble(read(ds, ""));

        data = Range.closed(lower != null ? lower.longValue() : null, upper != null ? upper.longValue() : null);
      }
    }

    return data;
  }
  public static Range<Integer>
  readIntegerRange(DataInput ds, Range<Integer> data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
//      if (data == null) data = new Range<Integer>();
      for (int i = 0; i < n; i++)
      {
        Double lower = Stats.toDouble(read(ds, ""));
        Double upper = Stats.toDouble(read(ds, ""));

        data = Range.closed(lower != null ? lower.intValue() : null, upper != null ? upper.intValue() : null);
      }
    }

    return data;
  }
  public static Range<Float>
  readFloatRange(DataInput ds, Range<Float> data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
//      if (data == null) data = new Range<Float>();
      for (int i = 0; i < n; i++)
      {
        Double lower = Stats.toDouble(read(ds, ""));
        Double upper = Stats.toDouble(read(ds, ""));

        data = Range.closed(lower != null ? lower.floatValue() : null, upper != null ? upper.floatValue() : null);
      }
    }

    return data;
  }
  public static Range<Double>
  readDoubleRange(DataInput ds, Range<Double> data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
//      if (data == null) data = new Range<Double>();
      for (int i = 0; i < n; i++)
      {
        Double lower = Stats.toDouble(read(ds, ""));
        Double upper = Stats.toDouble(read(ds, ""));

        data = Range.closed(lower != null ? lower : null, upper != null ? upper : null);
      }
    }

    return data;
  }
  public static Range<String>
  readStringRange(DataInput ds, Range<String> data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
//      if (data == null) data = new Range<String>();
      for (int i = 0; i < n; i++)
      {
        data = Range.closed(read(ds, ""), read(ds, ""));
      }
    }

    return data;
  }
  public static <T extends Binary> void
  writeStringMap(DataOutput ds, Multimap<String, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
    {
      for (String key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
    }
  }
  public static <T extends Binary> void
  writeIntegerMap(DataOutput ds, Multimap<Integer, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
    {
      for (Integer key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
    }
  }
  public static void
  writeStringLongs(DataOutput ds, Multimap<String, Long> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
    {
      for (String key : data.keySet())
      {
        write(ds, key);
        writeLongs(ds, data.get(key));
      }
    }
  }
  public static <T extends Binary> Map<String, T>
  readStringMap(DataInput ds, Map<String, T> data, T template) throws Exception
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<String, T>();
      for (int i = 0; i < n; i++)
      {
        String new_k = read(ds, "");
        T      new_t = read(ds, template);
        data.put(new_k, new_t);
      }
    }
    return data;
  }
  public static Map<String, String>
  readStringStringMap(DataInput ds, Map<String, String> data)  throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<String, String>();
      for (int i = 0; i < n; i++)
      {
        String new_k = read(ds, "");
        String new_t = read(ds, "");
        data.put(new_k, new_t);
      }
    }
    return data;
  }
  public static <T extends Binary> void
  writeStringMap(DataOutput ds, Map<String, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
    {
      for (String key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
    }
  }
  public static void
  writeStringStringMap(DataOutput ds, Map<String, String> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
    {
      for (String key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
    }
  }
  public static void write(DataOutput ds,
                           Boolean    value) throws IOException
  {
    isnull(ds, value);
    if (value != null) ds.writeBoolean(value);
    //write(ds, (value != null ? (value ? "Y" : "N") : (String )null));
/*    if (value == null)
    {
      writer.writeInt(0);
    }
    else
    {
      writer.writeInt(1);
      writer.writeUTF(value ? "Y" : "N");
    } */
  }
  public static void write(DataOutput writer,
                           String     value) throws IOException {
    if (value == null)
    {
      writer.writeInt(-1);
    }
    else
    {
      writer.writeInt(value.length());
      //if (writer instanceof BufferedRandomAccessFile)
      //{
      //  ((BufferedRandomAccessFile )writer).writeUTFx(value);
      //}
      //else
      //{
      writer.writeUTF(value);
      //}
      //writer.writeUTF(value);
    }
  }
  public static void write(DataOutput writer,
                           long       value) throws IOException
  {
    writer.writeInt(1);
    writer.writeLong(value);
  }
  public static void write(DataOutput writer,
                           Long       value) throws IOException
  {
    if (value == null)
    {
      writer.writeInt(0);
    }
    else
    {
      writer.writeInt(1);
      writer.writeLong(value);
    }
  }
  public static void write(DataOutput writer,
                           int        value) throws IOException
  {
    writer.writeInt(1);
    writer.writeInt(value);
  }
  public static void writeAsFloat(DataOutput writer,
                                  Double value, float default_) throws IOException
  {
    writer.writeFloat(value != null ? value.floatValue() : default_);
  }
  public static void write(DataOutput writer,
                           char       value) throws IOException
  {
    writer.writeInt(1);
    writer.writeChar(value);
  }
  public static void write(DataOutput writer,
                           Integer    value) throws IOException
  {
    if (value == null)
    {
      writer.writeInt(0);
    }
    else
    {
      writer.writeInt(1);
      writer.writeInt(value);
    }
  }
  public static void write(DataOutput writer,
                           Float      value) throws IOException
  {
    if (value == null)
    {
      writer.writeInt(0);
    }
    else
    {
      writer.writeInt(1);
      writer.writeFloat(value);
    }
  }
  public static void write(DataOutput writer,
                           double     value) throws IOException
  {
    writer.writeInt(1);
    writer.writeDouble(value);
  }
  public static void write(DataOutput writer,
                           float     value) throws IOException
  {
    writer.writeInt(1);
    writer.writeFloat(value);
  }
  public static void write(DataOutput writer,
                           Double     value) throws IOException
  {
    if (value == null)
    {
      writer.writeInt(0);
    }
    else
    {
      writer.writeInt(1);
      writer.writeDouble(value);
    }
  }
  public static boolean isnull(DataInput is) throws IOException
  {
    int isnull = read(is, 0);
    // indicating null
    return (isnull == 0);
  }
  public static Integer read(DataInput is, Integer value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    return (value = is.readInt());
  }
  public static Integer read(DataInput is, int value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    return (value = is.readInt());
  }
  public static char read(DataInput is, char value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    return (value = is.readChar());
  }
  public static Boolean read(DataInput is, Boolean value) throws IOException
  {
    if (isnull(is)) return null;
    return is.readBoolean();
  }
  public static Long read(DataInput is,
                          Long      value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    return (value = is.readLong());
  }
  public static long read(DataInput is,
                          long      value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    Long val = is.readLong();
    return (val != null ? val : value);
  }
  public static Double read(DataInput is,
                            Double    value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    return (value = is.readDouble());
  }
  public static Float read(DataInput is,
                           Float     value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    return (value = is.readFloat());
  }
  public static double read(DataInput is,
                            double    value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    return is.readDouble();
    //double val = is.readDouble();
    //return (val != null ? val : value);
  }
  public static float read(DataInput is,
                           float     value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    Float val = is.readFloat();
    return (val != null ? val : value);
  }
  public static String read(DataInput is,
                            String    value) throws IOException
  {
    if (is.readInt() == -1)
    {
      return value;
    }
    //value = is.readUTF();

    //return value;
    return is.readUTF();
  }
  public static void isnull(DataOutput writer, Object obj) throws IOException
  {
    write(writer, obj != null ? 1 : 0);
  }
  public static void write(String dest, StringBuffer buf)
  {
    FileWriter writer = null;
    try
    {
      writer = new FileWriter(dest);
      writer.write(buf.toString());
      writer.close();
    }
    catch (IOException e) {}
  }
}
