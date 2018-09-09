package org.ms2ms.utils;

import com.compomics.util.io.FilenameExtensionFilter;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.ms2ms.Disposable;
import org.ms2ms.data.Binary;
import org.ms2ms.data.collect.MultiTreeTable;
import org.ms2ms.math.Stats;
import org.ms2ms.r.Dataframe;
import toools.set.IntHashSet;
import toools.set.IntSet;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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

    try { if (t == null) t = (T )t.getClass().newInstance(); } catch (Exception e) { e.printStackTrace(); }

    t.read(ds);

    return t;
  }
  public static <T extends Binary> void write(DataOutput ds, T t) throws IOException
  {
    isnull(ds, t);
    if (t != null) t.write(ds);
  }
  public static void writeOpStr(DataOutput ds, Optional<String> t) throws IOException
  {
    write(ds, t.isPresent());
    if (t.isPresent()) write(ds, t.get());
  }
  public static void writeOpDouble(DataOutput ds, Optional<Double> t) throws IOException
  {
    write(ds, t.isPresent());
    if (t.isPresent()) write(ds, t.get());
  }
  public static void writeOpFloat(DataOutput ds, Optional<Float> t) throws IOException
  {
    write(ds, t.isPresent());
    if (t.isPresent()) write(ds, t.get());
  }
  public static void writeOpInt(DataOutput ds, Optional<Integer> t) throws IOException
  {
    write(ds, t.isPresent());
    if (t.isPresent()) write(ds, t.get());
  }
  public static void writeOpLong(DataOutput ds, Optional<Long> t) throws IOException
  {
    write(ds, t.isPresent());
    if (t.isPresent()) write(ds, t.get());
  }
  public static void writeOpBoolean(DataOutput ds, Optional<Boolean> t) throws IOException
  {
    write(ds, t.isPresent());
    if (t.isPresent()) write(ds, t.get());
  }
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
  public static void writeIntLongMap(DataOutput ds, Map<Integer, Long> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
      for (Integer key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
  }
  public static void writeIntIntMap(DataOutput ds, Map<Integer, Integer> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
      for (Integer key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
  }
  public static void writeIntDoubleMap(DataOutput ds, Map<Integer, Double> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
      for (Integer key : data.keySet())
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
      for (T t : data) write(ds,t);
    }
  }
  public static void writeInts(DataOutput ds, Collection<Integer> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);
    if (Tools.isSet(data))
    {
      for (Integer t : data) write(ds,t);
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
  public static void write(DataOutput ds, float[] data) throws IOException
  {
    write(ds, data != null ? data.length : 0);
    if (data != null && data.length > 0)
    {
      for (float t : data) write(ds, t);
    }
  }
  public static void write(DataOutput ds, byte[] data) throws IOException
  {
    write(ds, data != null ? data.length : 0);
    if (data != null && data.length > 0)
    {
      write(ds,data);
    }
  }
  public static <T extends Binary> Collection<T> read(DataInput ds, Collection<T> data, T template) throws Exception
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new ArrayList<T>();
      for (int i = 0; i < n; i++)
        data.add(read(ds,(T )template.getClass().newInstance()));
    }
    return data;
  }
  public static Collection<Integer> readInts(DataInput ds) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      Collection<Integer> data = new ArrayList<Integer>();
      for (int i = 0; i < n; i++) data.add(ds.readInt());

      return data;
    }
    return null;
  }
  public static List<Double> readDoubles(DataInput ds) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      List<Double> data = new ArrayList<>(n);
      for (int i = 0; i < n; i++) data.add(ds.readDouble());

      return data;
    }
    return null;
  }
  public static double[] read(DataInput ds, double[] data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
//      if (data == null) data = new double[n];
      data = new double[n];
      for (int i = 0; i < n; i++)
      {
        data[i] = read(ds, 0d);
      }
    }
    return data;
  }
  public static float[] read(DataInput ds, float[] data) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      data = new float[n];
      for (int i = 0; i < n; i++)
      {
        data[i] = read(ds, 0f);
      }
    }
    return data;
  }
  public static byte[] readBytes(DataInput ds) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      byte[] data = new byte[n];
      ds.readFully(data);
      return data;
    }
    return null;
  }
  public static <T extends Binary> void write(DataOutput ds, List<T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);
    if (Tools.isSet(data))
    {
      for (T t : data) write(ds, t);
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
  public static void writeDoubles(DataOutput ds, Double... data) throws IOException
  {
    if (Tools.isSet(data))
      for (Double t : data) write(ds, t);
  }

  public static void writeDoubles(DataOutput ds, Collection<Double> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);
    if (Tools.isSet(data))
    {
      for (Double t : data) ds.writeDouble(t);
    }
  }
  public static void writeFloats(DataOutput ds, Collection<Float> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);
    if (Tools.isSet(data))
    {
      for (Float t : data) write(ds, t);
    }
  }
  public static <T extends Binary> List<T> read(DataInput ds, List<T> data, Class<T> template) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      try
      {
        //if (data == null) data = new ArrayList<T>();
        if      (data == null) data = new ArrayList<T>(n);
        else if (data instanceof ArrayList) ((ArrayList )data).ensureCapacity(n);
        for (int i = 0; i < n; i++)
          data = (List )Tools.addNotNull(data, read(ds, (T )template.newInstance()));
      }
      catch (IllegalAccessException|InstantiationException e)
      {
        throw new RuntimeException(e);
      }
    }
    return data;
  }
  public static <T extends Binary> List<T> readList(DataInput ds, Class<T> template) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      try
      {
        List<T> data = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
          data = (List )Tools.addNotNull(data, read(ds, (T )template.getDeclaredConstructor().newInstance()));

        return data;
      }
      catch (IllegalAccessException|NoSuchMethodException|InstantiationException|InvocationTargetException e)
      {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  public static <T extends Binary> ImmutableList<T> readImmutableList(DataInput ds, Class<T> template) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      try
      {
        ImmutableList.Builder<T> builder=ImmutableList.builder();
        for (int i=0; i<n; i++)
          builder = Tools.addNotNull(builder, read(ds, (T) template.getDeclaredConstructor().newInstance()));

        return builder.build();
      }
      catch (IllegalAccessException|NoSuchMethodException|InstantiationException|InvocationTargetException e)
      {
        throw new RuntimeException(e);
      }
    }
    return null;
  }
  public static <T extends Binary> List<T> skip(DataInput ds, List<T> data, T template) throws Exception
  {
    int n = read(ds, 0);
    if (n > 0)
      for (int i = 0; i < n; i++)
        read(ds, (T )template.getClass().newInstance());

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
  public static void writeStrs(DataOutput ds, Collection<String> data) throws IOException
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
  public static <T extends Binary> Map<T, float[]>
  readBinFloatsMap(DataInput ds, Map<T, float[]> data, Class<T> template) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      try
      {
        if (data == null) data = new TreeMap<T, float[]>();
        for (int i = 0; i < n; i++)
        {
          T K = IOs.read(ds, template.newInstance());
          data.put(K, read(ds, new float[1]));
        }
      }
      catch (IllegalAccessException | InstantiationException e2)
      {
        throw new RuntimeException(e2);
      }
    }
    return data;
  }
  public static IntSet readIntSet(DataInput ds) throws IOException
  {
    int n = read(ds, 1);
    if (n>0)
    {
      IntSet out = new IntHashSet();
      for (int i=0; i<n; i++) out.add(read(ds, 0));
      return out;
    }
    return null;
  }
  public static Optional<String> readOpStr(DataInput ds) throws IOException
  {
    Optional<String> r;
    if (Tools.isTrue(read(ds, false))) r=Optional.of(read(ds, "")); else r=Optional.absent();

    return r;
  }
  public static Optional<Double> readOpDouble(DataInput ds) throws IOException
  {
    Optional<Double> r;
    if (Tools.isTrue(read(ds, false))) r=Optional.of(read(ds, 0d)); else r=Optional.absent();

    return r;
  }
  public static Optional<Integer> readOpInt(DataInput ds) throws IOException
  {
    Optional<Integer> r;
    if (Tools.isTrue(read(ds, false))) r=Optional.of(read(ds, 0)); else r=Optional.absent();

    return r;
  }
  public static Optional<Float> readOpFloat(DataInput ds) throws IOException
  {
    Optional<Float> r;
    if (Tools.isTrue(read(ds, false))) r=Optional.of(read(ds, 0f)); else r=Optional.absent();

    return r;
  }
  public static Optional<Long> readOpLong(DataInput ds) throws IOException
  {
    Optional<Long> r;
    if (Tools.isTrue(read(ds, false))) r=Optional.of(read(ds, 0L)); else r=Optional.absent();

    return r;
  }
  public static Optional<Boolean> readOpBoolean(DataInput ds) throws IOException
  {
    Optional<Boolean> r;
    if (Tools.isTrue(read(ds, false))) r=Optional.of(read(ds, false)); else r=Optional.absent();

    return r;
  }

  public static Table<String,  String, IntSet> readStr2IntSet(DataInput ds) throws IOException
  {
    int n = read(ds, 1); // values.size()
    if (n>0)
    {
      Table<String,  String, IntSet> out = HashBasedTable.create();
      for (int i=0; i<n; i++)
      {
        out.put(read(ds, ""), read(ds, ""), readIntSet(ds));
      }
      return out;
    }
    return null;
  }
  public static <T extends Binary> TreeBasedTable<Double, String, T> readDoubleStrBin(DataInput ds, T t) throws IOException
  {
    int n = read(ds, 1); // values.size()
    if (n>0)
    {
      TreeBasedTable<Double,  String, T> out = TreeBasedTable.create();
      for (int i=0; i<n; i++)
      {
        out.put(read(ds, 0d), read(ds, ""), read(ds, t));
      }
      return out;
    }
    return null;
  }
  public static <T extends Binary> TreeBasedTable<Float, Float, T>
  readFloatFloatBin(DataInput ds, TreeBasedTable<Float, Float, T> out, Class<T> t) throws IOException
  {
    int n = read(ds, 1); // values.size()
    if (n>0)
    {
      try
      {
        out = TreeBasedTable.create();
        for (int i=0; i<n; i++)
        {
          Float k1 = read(ds, 0f);
          Float k2 = read(ds, 0f);
          out.put(k1, k2, read(ds, t.newInstance()));
        }
        return out;
      }
      catch (IllegalAccessException | InstantiationException e2)
      {
        throw new RuntimeException(e2);
      }
    }
    return null;
  }
  public static Table<Integer, String,  String> readIntStr2(DataInput ds) throws IOException
  {
    int n = read(ds, 1); // values.size()
    if (n>0)
    {
      Table<Integer, String,  String> out = HashBasedTable.create();
      for (int i=0; i<n; i++)
      {
        out.put(read(ds, 0), read(ds, ""), read(ds, ""));
      }
      return out;
    }
    return null;
  }
  public static <T extends Binary> Map<Long, T>
  readLongMap(DataInput ds, Map<Long, T> data, Class<T> template) throws Exception
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<>();
      for (int i = 0; i < n; i++)
      {
        Long K = read(ds, 0L);
        data.put(K, (T )read(ds, template.newInstance()));
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
      if (data == null) data = new TreeMap<>();
      for (int i = 0; i < n; i++)
      {
        Long     K =read(ds, 0L);
        data.put(K, read(ds, 0));
      }
    }
    return data;
  }
  public static TreeMap<Integer, Double>
  readIntDoubleMap(DataInput ds) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      TreeMap<Integer, Double> data = new TreeMap<>();
      for (int i = 0; i < n; i++)
      {
        Integer  K =read(ds, 0);
        data.put(K, read(ds, 0d));
      }

      return data;
    }
    return null;
  }
  public static TreeMap<Integer, Integer>
  readIntIntMap(DataInput ds) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      TreeMap<Integer, Integer> data = new TreeMap<>();
      for (int i = 0; i < n; i++)
      {
        Integer  K =read(ds, 0);
        data.put(K, read(ds, 0));
      }

      return data;
    }
    return null;
  }
  public static <T extends Binary> Map<Long, T>
  readLongMap(DataInput ds, Map<Long, T> data, Class<T> template, Collection<Long> ids) throws Exception
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<Long, T>();
      for (int i = 0; i < n; i++)
      {
        T new_t = (T )template.newInstance();
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
      if (data == null) data = new TreeMap<>();
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
      if (data == null) data = new TreeMap<>();
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
  public static Map<Double, Double>
  readDoubleDouble(DataInput ds) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      Map<Double, Double> data = new TreeMap<>();
      for (int i = 0; i < n; i++)
      {
        data.put(read(ds, 0d),read(ds, 0d));
      }
      return data;
    }
    return null;
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
  public static void writeObject(DataOutput ds, Object obj, boolean ignoreUnknown) throws IOException
  {
    if (obj instanceof Binary) write(ds, Binary.class.getName());
    else                       write(ds, obj.getClass().getName());

    if      (obj instanceof String)   write(ds, (String   )obj);
    else if (obj instanceof Integer)  write(ds, (Integer  )obj);
    else if (obj instanceof Long)     write(ds, (Long     )obj);
    else if (obj instanceof Boolean)  write(ds, (Boolean  )obj);
    else if (obj instanceof Float)    write(ds, (Float    )obj);
    else if (obj instanceof Double)   write(ds, (Double   )obj);
    else if (obj instanceof Binary) write(ds, (Binary )obj);
    else if (!ignoreUnknown) throw new RuntimeException("Unsupported type for binary property, " + obj.getClass().getName());
  }
  public static Object readObject(DataInput ds, boolean ignoreUnknown) throws IOException
  {
    String name = read(ds, Strs.NULL);
    //if      (name.equals(""))                       data.put(new_k, "");
    if      (name.equals(  String.class.getName())) return (Object )read(ds, "");
    else if (name.equals( Integer.class.getName())) return (Object )read(ds, 0);
    else if (name.equals(    Long.class.getName())) return (Object )read(ds, 0L);
    else if (name.equals( Boolean.class.getName())) return (Object )read(ds, (Boolean )null);
    else if (name.equals(   Float.class.getName())) return (Object )read(ds, 0f);
    else if (name.equals(  Double.class.getName())) return (Object )read(ds, 0d);
    else if (name.equals(Binary.class.getName()))   return (Object )read(ds, (Binary )null);
    else if (!ignoreUnknown) throw new RuntimeException("Unsupported type for binary property, " + name);

    return null;
  }
  public static void
  writeProperties(DataOutput ds, Map<String, Object> data, boolean ignoreUnknown) throws IOException
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
        writeObject(ds, data.get(key), ignoreUnknown);
//        if (data.get(key) instanceof Binary) write(ds, Binary.class.getName());
//        else                                   write(ds, data.get(key).getClass().getName());
//
//        if      (data.get(key) instanceof String)   write(ds, (String   )data.get(key));
//        else if (data.get(key) instanceof Integer)  write(ds, (Integer  )data.get(key));
//        else if (data.get(key) instanceof Long)     write(ds, (Long     )data.get(key));
//        else if (data.get(key) instanceof Boolean)  write(ds, (Boolean  )data.get(key));
//        else if (data.get(key) instanceof Float)    write(ds, (Float    )data.get(key));
//        else if (data.get(key) instanceof Double)   write(ds, (Double   )data.get(key));
//        else if (data.get(key) instanceof Binary) write(ds, (Binary )data.get(key));
//        else throw new RuntimeException("Unsupported type for binary property, " + data.get(key).getClass().getName());
      }
    }
  }
  public static Map<String, Object>
  readProperties(DataInput ds, Map<String, Object> data, boolean ignoreUnknown) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      if (data == null) data = new HashMap<>();
      for (int i = 0; i < n; i++)
      {
        String new_k = read(ds, "");
        if (isnull(ds)) continue;

        data.put(new_k, readObject(ds, ignoreUnknown));
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
  public static void writeCharFloats(DataOutput ds, Map<Character, Float> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
      for (Character key : data.keySet())
      {
        write(ds,key);
        write(ds, data.get(key));
      }
  }
  public static void readCharFloats(DataInput ds, Map<Character, Float> data) throws IOException
  {
    int n=read(ds, 0);

    if (n>0)
      for (int i=0; i<n; i++)
        data.put(read(ds, 'c'), read(ds, 1f));
  }
  public static void write(DataOutput ds, IntSet data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
      for (int i : data.toIntArray()) write(ds, i);
  }
  public static void writeStr2IntSet(DataOutput ds, Table<String,  String, IntSet> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.values().size() : 0);
    if (Tools.isSet(data))
      for (String row : data.rowKeySet())
        for (String col : data.row(row).keySet())
        {
          write(ds, row);
          write(ds, col);
          write(ds, data.get(row, col));
        }
  }
  public static void writeIntStr2(DataOutput ds, Table<Integer, String,  String> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.rowKeySet().size() : 0);
    if (Tools.isSet(data))
      for (Integer row : data.rowKeySet())
        for (String col : data.row(row).keySet())
        {
          write(ds, row);
          write(ds, col);
          write(ds, data.get(row, col));
        }
  }
  public static <T extends Binary> void writeDoubleDoubleBin(DataOutput ds, Table<Double, Double, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.rowKeySet().size() : 0);
    if (Tools.isSet(data))
      for (Double row : data.rowKeySet())
        for (Double col : data.row(row).keySet())
        {
          write(ds, row);
          write(ds, col);
          write(ds, data.get(row, col));
        }
  }
  public static <T extends Binary> void writeDoubleStrBin(DataOutput ds, Table<Double, String, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.rowKeySet().size() : 0);
    if (Tools.isSet(data))
      for (Double row : data.rowKeySet())
        for (String col : data.row(row).keySet())
        {
          write(ds, row);
          write(ds, col);
          write(ds, data.get(row, col));
        }
  }
  public static <T extends Binary> void writeFloatFloatBin(
      DataOutput ds, Table<Float, Float, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.rowKeySet().size() : 0);
    if (Tools.isSet(data))
      for (Float row : data.rowKeySet())
        for (Float col : data.row(row).keySet())
        {
          write(ds, row);
          write(ds, col);
          write(ds, data.get(row, col));
        }
  }
//  public static void writeIntStrDouble(DataOutput ds, Table<Integer, String,  Double> data) throws IOException
//  {
//    write(ds, Tools.isSet(data) ? data.rowKeySet().size() : 0);
//    if (Tools.isSet(data))
//      for (Integer row : data.rowKeySet())
//        for (String col : data.row(row).keySet())
//        {
//          write(ds, row);
//          write(ds, col);
//          write(ds, data.get(row, col));
//        }
//  }
  public static <T extends Binary> void
  writeIntMap(DataOutput ds, Map<Integer, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
      for (Integer key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
  }
  public static <T extends Binary> void
  writeBinFloatsMap(DataOutput ds, Map<T, float[]> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
      for (T key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
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
        write(ds, data.get(key));
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
  public static void
  writeDoubleDouble(DataOutput ds, Map<Double, Double> data) throws IOException
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
  public static void
  writeDoubleObject(DataOutput ds, Map<Double, Object> data, boolean ignoreUnknown) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
    {
      for (Double key : data.keySet())
      {
        write(ds, key);
        writeObject(ds, data.get(key), ignoreUnknown);
      }
    }
  }
  public static void
  writeStrObject(DataOutput ds, Map<String, Object> data, boolean ignoreUnknown) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.size() : 0);

    if (Tools.isSet(data))
    {
      for (String key : data.keySet())
      {
        write(ds, key);
        writeObject(ds, data.get(key), ignoreUnknown);
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
  readStrRange(DataInput ds, Range<String> data) throws IOException
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
  writeStrMaps(DataOutput ds, Multimap<String, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
      for (String key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
  }
  public static <T extends Binary> void
  writeIntMaps(DataOutput ds, Multimap<Integer, T> data) throws IOException
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
  public static <T extends Binary> void
  writeFloatMaps(DataOutput ds, Multimap<Float, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);
    if (Tools.isSet(data))
    {
      for (Float key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
    }
  }
  public static <T extends Binary> void
  writeDoubleMultimap(DataOutput ds, Multimap<Double, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
    {
      for (Double key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
    }
  }
  public static <K extends Binary, T extends Binary> void
  writeMultimap(DataOutput ds, Multimap<K, T> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
    {
      for (K key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
    }
  }
  public static <T extends Binary> void
  writeDoubleListMap(DataOutput ds, Map<Double, List<T>> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
    {
      for (Double key : data.keySet())
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
  public static void
  writeStringDoubles(DataOutput ds, Multimap<String, Double> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
    {
      for (String key : data.keySet())
      {
        write(ds, key);
        writeDoubles(ds, data.get(key));
      }
    }
  }
  public static void
  writeStrInts(DataOutput ds, Multimap<String, Integer> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
      for (String key : data.keySet())
      {
        write(ds, key);
        writeInts(ds, data.get(key));
      }
  }
  public static void
  writeStrDouble(DataOutput ds, Map<String, Double> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
      for (String key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
  }
  public static void
  writeStrInt(DataOutput ds, Map<String, Integer> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
      for (String key : data.keySet())
      {
        write(ds, key);
        write(ds, data.get(key));
      }
  }
  public static <T extends Binary> Map<String, T>
  readStrMap(DataInput ds, Map<String, T> data, Class<T> template) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      try
      {
        if (data == null) data = new TreeMap<>();
        for (int i = 0; i < n; i++)
        {
          String   K =read(ds, "");
          data.put(K, read(ds, (T )template.getDeclaredConstructor().newInstance()));
        }
      }
      catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e2)
      {
        throw new RuntimeException(e2);
      }
    }
    return data;
  }
  public static Map<String, Object>
  readStrObject(DataInput ds, Map<String, Object> data, boolean ignoreUnknown) throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      try
      {
        if (data == null) data = new TreeMap<>();
        for (int i = 0; i < n; i++)
        {
          String   K =read(ds, "");
          data.put(K, readObject(ds,ignoreUnknown));
        }
      }
      catch (IOException e2)
      {
        e2.printStackTrace();
      }
    }
    return data;
  }

  public static <T extends Binary> Multimap<String, T>
  readStrMaps(DataInput ds, Multimap<String, T> data, Class<T> template) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      for (int i = 0; i < n; i++)
      {
        String      K =read(ds, "");
        data.putAll(K, readList(ds, template));
      }
    }
    return data;
  }
  public static <T extends Binary> Multimap<Integer, T>
  readIntMaps(DataInput ds, Multimap<Integer, T> data, Class<T> template) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      for (int i = 0; i < n; i++)
      {
        Integer     K =read(ds, 0);
        data.putAll(K, readList(ds, template));
      }
    }
    return data;
  }
  public static <K extends Binary, T extends Binary> Multimap<K, T>
  readMultimaps(DataInput ds, Multimap<K, T> data, K key, Class<T> val_template) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      for (int i = 0; i < n; i++)
      {
        data.putAll(read(ds, key), readList(ds, val_template));
      }
    }
    return data;
  }
  // only read a random samples of the objects
  public static <T extends Binary> Map<Integer, T>
  sampleIntMap(DataInput ds, Map<Integer, T> data, Class<T> template, int samples) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      // figure out the sampling mechanism
      Set<Integer> sampled = new TreeSet<>();
      if (samples<=0 || samples>n) samples=0;
      else
      {
        Random RND = new Random(System.nanoTime());
        while (sampled.size()<samples) sampled.add(RND.nextInt(n));
      }
      try
      {
        for (int i = 0; i < n; i++)
        {
          Integer  K =read(ds, 0);
          T V = read(ds, (T )template.getDeclaredConstructor().newInstance());
          if (samples==0 || sampled.contains(i)) data.put(K, V);
          else if (V instanceof Disposable) V = (T )Tools.dispose((Disposable )V);
        }
      }
      catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e2)
      {
        throw new RuntimeException("Error during the sampling of the matches.", e2);
      }
      sampled=(Set )Tools.dispose(sampled);
    }
    return data;
  }

  public static <T extends Binary> Map<Integer, T>
  readIntMap(DataInput ds, Map<Integer, T> data, Class<T> template) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      try
      {
        for (int i = 0; i < n; i++)
        {
          if ((i+1)%1000 ==0) System.out.print(".");
          if ((i+1)%50001==0) System.out.println(i);
          Integer  K =read(ds, 0);
          T V = read(ds, (T )template.getDeclaredConstructor().newInstance());
          data.put(K, V);
        }
      }
      catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e2)
      {
        e2.printStackTrace();
      }
      System.out.println();
    }
    return data;
  }
  public static <T extends Binary> Multimap<Double, T>
  readDoubleMaps(DataInput ds, Multimap<Double, T> data, Class<T> template) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      for (int i = 0; i < n; i++)
      {
        Double      K =read(ds, 0d);
        data.putAll(K, readList(ds, template));
      }
    }
    return data;
  }
  public static <T extends Binary> Multimap<Float, T>
  readFloatMaps(DataInput ds, Multimap<Float, T> data, Class<T> template) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      for (int i = 0; i < n; i++)
      {
        Float      K =read(ds, 0f);
        data.putAll(K, readList(ds, template));
      }
    }
    return data;
  }
  public static void readStrInts(DataInput ds, Map<String, Integer> data) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
      for (int i=0; i<n; i++) data.put(read(ds, ""), read(ds, 0));
  }
  public static Map<String, String>
  readStringStringMap(DataInput ds, Map<String, String> data)  throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      if (data == null) data = new TreeMap<>();
      for (int i = 0; i < n; i++)
      {
        String new_k = read(ds, "");
        String new_t = read(ds, "");
        data.put(new_k, new_t);
      }
    }
    return data;
  }
  public static TreeMap<String, Double>
  readStrDouble(DataInput ds)  throws IOException
  {
    int n = read(ds, 0);

    if (n > 0)
    {
      TreeMap<String, Double> data = new TreeMap<>();
      for (int i = 0; i < n; i++)
      {
        String new_k = read(ds, "");
        Double new_t = read(ds, 0d);
        data.put(new_k, new_t);
      }
      return data;
    }
    return null;
  }
  public static Multimap<String, Double>
  readStringDoubles(DataInput ds, Multimap<String, Double> data) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      for (int i = 0; i < n; i++)
      {
        String      K = read(ds, "");
        data.putAll(K, readDoubles(ds));
      }
      return data;
    }
    return null;
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
      writer.writeUTF(value);
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
  public static void write(Writer writer, Integer value) throws IOException
  {
    if (value == null) writer.write(value);
  }
  public static void write(Writer writer, Double value) throws IOException
  {
    if (value == null) writer.write(value.toString());
  }
  public static void write(Writer writer, String value) throws IOException
  {
    if (value == null) writer.write(value);
  }
  public static void write(Writer writer, String blank, char t, Object... values) throws IOException
  {
    int counts=0;
    if (Tools.isSet(values))
      for (Object val : values)
      {
        if (++counts>1) writer.write(t);
        writer.write(val!=null?val.toString():blank);
      }
  }
  public static void write(Writer writer, String blank, char t, Map<String, Object> data, String... keys) throws IOException
  {
    int counts=0;
    if (data!=null && Tools.isSet(keys))
      for (Object key : keys)
      {
        if (++counts>1) writer.write(t);
        writer.write(key!=null&&data.containsKey(key)?data.get(key).toString():blank);
      }
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
//  public static Integer read(DataInput is, Integer value) throws IOException
//  {
//    if (is.readInt() == 0)
//    {
//      return value;
//    }
//    return (value = is.readInt());
//  }
//  public static Integer read(DataInput is, Integer value) throws IOException
//  {
//    if (is.readInt() == 0)
//    {
//      return value;
//    }
//    return (value = is.readInt());
//  }
  public static Integer read(DataInput is, Integer value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    return is.readInt();
  }
  public static int read(DataInput is, int value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    return is.readInt();
  }
  public static char read(DataInput is, char value) throws IOException
  {
    if (is.readInt() == 0)
    {
      return value;
    }
    return is.readChar();
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
  public static Double read(DataInput is, Double value) throws IOException
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
    int n=is.readInt();
    if (n==0)
    {
      return value;
    }
    return is.readDouble();
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
    //return value;
    return is.readUTF();
  }
  public static void write(String out, byte[] data)
  {
    RandomAccessFile bin = null;
    try
    {
      // save the native graph data to a separate file
      bin = new RandomAccessFile(out, "rw");
      bin.write(data.length);
      write(bin, data);
      bin.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  public static byte[] readBytes(String in)
  {
    RandomAccessFile bin = null;
    try
    {
      // save the native graph data to a separate file
      bin = new RandomAccessFile(in, "rw");
      byte[] data = new byte[bin.readInt()];
      bin.readFully(data);
      bin.close();
      return data;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return null;
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
  public static Writer write(Writer w, char t, String... fields) throws IOException
  {
    if (Tools.isSet(fields))
      for (int i=0; i<fields.length; i++)
      {
        if (fields[i]!=null) w.write(fields[i].replaceAll("\n", "_").replaceAll("\r", "_"));
        if (i<fields.length-1) w.write(t+"");
      }

    return w;
  }
  public static Writer writeLine(Writer w, char t, String... fields) throws IOException
  {
    write(w,t,fields); w.write("\n");
    return w;
  }

  public static String[] listFilesByExt(String root, String ext, boolean full)
  {
    String[] files = new File(root).list(new FilenameExtensionFilter(ext));
    if (full && files!=null)
      for (int i=0; i<files.length; i++) files[i] = root+"/"+files[i];

    return files;
  }
  public static String[] listFiles(String root, String name)
  {
    String[] files = new File(root).list(new WildcardFileFilter(name));
    if (files!=null)
      for (int i=0; i<files.length; i++) files[i] = root+"/"+files[i];

    return files;
  }
  public static List<String> listFiles(String root, FileFilter filter, int depth)
  {
    FileVisitor<Path> fileProcessor = new ProcessFile(filter);
    try
    {
      ProcessFile.files.clear(); ProcessFile.dir_file.clear();

      Files.walkFileTree(Paths.get(root), EnumSet.noneOf(FileVisitOption.class), depth,fileProcessor);
      // clone it so will not change on next call
      return new ArrayList<>(ProcessFile.files);
    }
    catch (IOException io)
    {
      System.out.println(io);
    }

    return null;
  }
  // http://www.mkyong.com/java/how-to-execute-shell-command-from-java/
  public static String executeCommand(String command)
  {
    StringBuffer output = new StringBuffer();

    Process p;
    try {
      p = Runtime.getRuntime().exec(command);
      p.waitFor();
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(p.getInputStream()));

      String line = "";
      while ((line = reader.readLine())!= null) {
        output.append(line + "\n");
      }

    } catch (Exception e)
    {
      e.printStackTrace();
    }

    return output.toString();
  }
  // not recursive. Having lots of trouble reproducing the behavious of "ls" using the other calls
//  public static List<String> listFiles(String tsv)
//  {
//    File dir = new File(tsv);
//    return listFiles(dir.getPath(), new WildcardFWildcardFileFilter("sample*.java"));
//  }
  public static Multimap<String, String> listDirFiles(String root, FileFilter filter)
  {
    FileVisitor<Path> fileProcessor = new ProcessFile(filter);
    try
    {
      Files.walkFileTree(Paths.get(root), fileProcessor);
      return ProcessFile.dir_file;
    }
    catch (IOException io)
    {
      System.out.println(io);
    }

    return null;
  }
  public static List<String> readLines(String file) throws IOException
  {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    List<String> lines = new ArrayList<>();
    while (reader.ready())
    {
      lines.add(reader.readLine());
    }
    reader.close();

    return lines;
  }
  private static final class ProcessFile extends SimpleFileVisitor<Path>
  {
    FileFilter filter;
    public static List<String> files = new ArrayList<>();
    public static Multimap<String, String> dir_file = HashMultimap.create();

    public ProcessFile(FileFilter f) { super(); filter=f; files.clear();}
    @Override
    public FileVisitResult visitFile(Path aFile, BasicFileAttributes aAttrs) throws IOException
    {
//      System.out.println("Processing file:" + aFile);
      if (filter.accept(aFile.toFile()))
      {
        files.add(aFile.toString());
        dir_file.put(aFile.getParent().toString(), aFile.toString());
      }
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path aDir, BasicFileAttributes aAttrs) throws IOException
    {
//      System.out.println("Processing directory:" + aDir);
      return FileVisitResult.CONTINUE;
    }
  }
  public static void writeTo(String f, String contents)
  {
    try
    {
      FileWriter w = new FileWriter(f);
      w.write(contents);
      w.close();
    }
    catch (IOException io)
    {
      System.out.println(io);
    }
  }
  public static String row(char t, Map<String, Object> R, String _default, String... cols)
  {
    String line=null;
    for (String col : cols)
      line = Strs.extend(line, R.containsKey(col) ? R.get(col).toString() : _default, t);

    return line;
  }
  public static Writer writeLine(Writer w, String line) throws IOException
  {
    if (line!=null) w.write(line+"\n");
    return w;
  }
  public static void writeIntMultimap(DataOutput ds, Multimap<Integer, Integer> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);

    if (Tools.isSet(data))
      for (Integer key : data.keySet())
      {
        write(ds, key);
        writeInts(ds, data.get(key));
      }
  }
  public static void readIntMultimap(DataInput ds, Multimap<Integer, Integer> data) throws IOException
  {
    int n = read(ds, 0);

    if (n>0)
      for (int i=0; i<n; i++)
      {
        data.putAll(read(ds, 0), readInts(ds));
      }
  }
  public static <T extends Binary> SortedMap<Double, List<T>>
  readDoubleListMap(DataInput ds, Class<T> template) throws IOException
  {
    int n = read(ds, 0);
    if (n > 0)
    {
      SortedMap<Double, List<T>> data = new TreeMap<>();
      for (int i=0; i<n; i++)
      {
        data.put(read(ds, 0d),readList(ds, template));
      }
      return data;
    }
    return null;
  }

  public static void readDoubleListMap(DataInput ds, Multimap<Integer, Integer> data) throws IOException
  {
    int n = read(ds, 0);

    if (n>0)
      for (int i=0; i<n; i++)
      {
        data.putAll(read(ds, 0), readInts(ds));
      }
  }
  public static <T extends Binary & Comparable> void writeStr2StrMultiTable(DataOutput ds, MultiTreeTable<String, String, T> data) throws IOException
  {
    write(ds, data.keySet().size());
    for (String row : data.keySet())
    {
      write(ds, row);
      writeStrMaps(ds, data.getData().get(row));
    }
  }
  public static <T extends Binary & Comparable> MultiTreeTable<String, String, T>
    readStr2StrMultiTable(DataInput ds, Class<T> template) throws IOException
  {
    int R = read(ds,0);
    // create the local table
    MultiTreeTable<String, String, T> D = MultiTreeTable.create();
    // read the rows and cols
    for (int r=0; r<R; r++)
    {
//      String key=read(ds, "");
//      Multimap<String, T> mmap = readStrMaps(ds, TreeMultimap.create(), template);
//      D.put(key, mmap);
      D.put(read(ds, ""), readStrMaps(ds, TreeMultimap.create(), template));
    }

    return D;
  }

  public static <T extends Binary & Comparable> void
    writeStr3MultiTable(DataOutput ds, Map<String, MultiTreeTable<String, String, T>> data) throws IOException
  {
    write(ds, Tools.isSet(data) ? data.keySet().size() : 0);
    if (Tools.isSet(data))
      for (String key : data.keySet())
      {
        write(ds, key);
        writeStr2StrMultiTable(ds, data.get(key));
      }
  }
  public static <T extends Binary & Comparable> Map<String, MultiTreeTable<String, String, T>>
    readStr3MultiTable(DataInput ds, Class<T> template) throws IOException
  {
    Map<String, MultiTreeTable<String, String, T>> data = new HashMap<>();

    int n = read(ds, 0);
    if (n>0)
      for (int i=0; i<n; i++)
      {
        data.put(read(ds,""), readStr2StrMultiTable(ds, template));
      }

    return data;
  }

  public static void save(String file, StringBuffer buf)
  {
    try
    {
      if (buf!=null)
      {
        FileWriter w = new FileWriter(file);
        w.write(buf.toString());
        w.close();
      }
    }
    catch (IOException io)
    {
      io.printStackTrace();
    }
  }
  public static <T extends Binary> void persist(String file, T data)
  {
    try
    {
      BufferedRandomAccessFile mm=null;
      try
      {
        // parse the run title from the file name
        mm = new BufferedRandomAccessFile(file, BufferedRandomAccessFile.WRITE);
        write(mm, data);
      }
      finally
      {
        if (mm!=null) { mm.flush(); mm.close(); mm=null; }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  public static <T extends Binary> T retrieve(String file, T data)
  {
    try
    {
      BufferedRandomAccessFile mm=null;
      try
      {
        System.out.println("Retrieving "+file);
        // parse the run title from the file name
        mm = new BufferedRandomAccessFile(file, BufferedRandomAccessFile.READ);

        data = read(mm, data);
      }
      finally
      {
        if (mm!=null) { mm.close(); mm=null; }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return data;
  }

}
