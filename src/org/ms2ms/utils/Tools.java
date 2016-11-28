package org.ms2ms.utils;

import com.google.common.collect.*;
import org.expasy.mzjava.core.ms.PpmTolerance;
import org.expasy.mzjava.proteomics.ms.ident.PeptideMatch;
import org.ms2ms.Disposable;
import org.ms2ms.data.collect.MultiTreeTable;
import toools.set.IntHashSet;
import toools.set.IntSet;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by wyu on 4/17/14.
 */
public class Tools
{
  static String ZEROES = "000000000000";
  static String BLANKS = "            ";

  public static <T> boolean isSet(Collection<T>  s) { return s!=null && s.size()>0; }
  public static <T> boolean isSet(Map            s) { return s!=null && !s.isEmpty(); }
  public static <T> boolean isSet(MultiTreeTable s) { return s!=null && s.keySet().size()>0; }
  public static <T> boolean isSet(Multimap       s) { return s!=null && !s.isEmpty(); }
  public static <T> boolean isSet(T[]            s) { return s!=null && s.length>0; }
  public static <T> boolean isSet(double[]       s) { return s!=null && s.length>0; }
  public static     boolean isSet(int[]          s) { return s!=null && s.length>0; }
  public static     boolean isSet(char[]         s) { return s!=null && s.length>0; }
  public static     boolean isSet(Table          s) { return s!=null && s.size()>0; }
  public static     boolean isSet(long[]         s) { return s!=null && s.length>0; }
  public static     boolean isSet(Range          s) { return s!=null && s.upperEndpoint().compareTo(s.lowerEndpoint())>=0; }
  public static     boolean isSet(IntSet         s) { return s!=null && s.size()>0; }
  public static     boolean isTrue( Boolean      s) { return s!=null &&  s; }
  public static     boolean isFalse(Boolean      s) { return s!=null && !s; }

  public static double[] cloneDoubleArray(double[] x)
  {
    return (x!=null?Arrays.copyOf(x, x.length):null);
  }
  public static float[] cloneFloatArray(float[] x)
  {
    return (x!=null?Arrays.copyOf(x, x.length):null);
  }
  public static <T> T front(Collection<T> s)
  {
    if (isSet(s))
      for (T t : s) return t;

    return null;
  }
  public static <T> T back(Collection<T> s)
  {
    T last = null;
    if (isSet(s))
      for (T t : s) last=t;

    return last;
  }
  public static <T> T  front(     T... s) { return s!=null?s[0]:null; }
  public static <T> T  back(      T... s) { return s!=null?s[s.length-1]:null; }
  public static double front(double... s) { return s!=null?s[0]:null; }
  public static double back( double... s) { return s!=null?s[s.length-1]:null; }

  //	Author:  Jean Vaucher
  //  Date:    March 2006
  // http://www.iro.umontreal.ca/~vaucher/Java/tutorials/Formatting.html
  /* -------------------------------------------------------------------------
     Meant to be used in a "print" statement to run data (text, int or real)
     in fields of W characters: on the right if W>0 and left if W<0.
     With real numbers, the parameter N is the number of decimals required.

         Util.format( String data, W )
         Util.format( int data, W )
         Util.format( double data, N, W )

     Usage:
         System.out.println("Name            age  result");
         System.out.println( Util.format(name,-15)
           + Util.format(age,5)
           + Util.format(mark,2,7));
     ------------------------------------------------------------------------

     - In my "formatting" page, "format" used to be called "toString"

     ------------------------------------------------------------------------ */
  public static String format( int val, int w) {	return format( Integer.toString(val), w); }
  public static String format( String s, int w)
  {
    int w1 = Math.abs(w);
    int n = w1-s.length();

    if ( n <= 0 ) return s;
    while (BLANKS.length()<n) BLANKS += "      ";
    if ( w<0 )
      return s + BLANKS.substring(0,n);
    else
      return BLANKS.substring(0,n) + s;
  }

  public static String format( double val, int n, int w)
  {
    //	rounding
    double incr = 0.5;
    for( int j=n; j>0; j--) incr /= 10;
    val += incr;

    String s = Double.toString(val);
    int n1 = s.indexOf('.');
    int n2 = s.length() - n1 - 1;

    if (n>n2)  {
      int len = n-n2;
      while (ZEROES.length()<len) ZEROES += "000000";
      s = s+ZEROES.substring(0, len);
    }
    else if (n2>n) s = s.substring(0,n1+n+1);

    return format( s, w );
  }
  public static String d2s(double s, int i)
  {
//    return format(s, i, 0);
    return String.format("%."+i+"f", s);
  }
  public static String o2s(Object s, int i)
  {
    if (s==null) return "NULL";

    if      (s instanceof Integer || s instanceof Long) return s.toString();
    else if (s instanceof String) return (String )s;
    else if (s instanceof Float)  return d2s(((Float )s).doubleValue(), i);
    else if (s instanceof Double) return d2s(((Double )s), i);

    return s.toString();
  }

  public static <C extends Comparable> Collection<Range<C>> merge(Collection<Range<C>> r)
  {
    // merge the slices
    Collection<Range<C>> pool = new ArrayList<Range<C>>();
    Set<Range<C>>   discarded = new HashSet<Range<C>>();
    for (Range<C> r1 : r)
      if (!discarded.contains(r1))
      {
        Range<C> p = r1;
        for (Range<C> r2 : r)
          if (r2 != r1 && !discarded.contains(r2) && r1.isConnected(r2)) { p = p.span(r2); discarded.add(r2); }
        pool.add(p);
      }

    return pool;
  }
  public static <K, V> Multimap<K,V> putAll(Multimap<K,V> map, Map<K,V> in)
  {
    if (map!=null && in!=null)
      for (K key : in.keySet()) map.put(key, in.get(key));
    return map;
  }
  public static <K, V> Map<K,V> putAll(Map<K,V> map, Map<K,V> in)
  {
    if (map!=null && in!=null) map.putAll(in);
    return map;
  }
  public static <K extends Enum<K>, V> EnumMap<K,V> put(EnumMap<K, V> map, K key, V in)
  {
    if (map!=null && in!=null) map.put(key, in);
    return map;
  }
  public static boolean contains(int[] tt, int t)
  {
    if (isSet(tt))
      for (int i : tt)
        if (i==t) return true;

    return false;
  }
  public static <T> boolean contains(T[] vals, T obj, int end)
  {
    if (isSet(vals) && obj!=null)
    {
      int bound = (end>=0&&end<=vals.length?end:vals.length);
      for (int i=0; i<bound; i++)
        if (vals[i]!=null && vals[i].equals(obj)) return true;
    }

    return false;
  }
  public static <T> boolean contains(T[] vals, T obj)
  {
    if (isSet(vals) && obj!=null)
      for (int i=0; i<vals.length; i++)
        if (vals[i]!=null && vals[i].equals(obj)) return true;

    return false;
  }
  public static <T extends Object> boolean isA(T A, T... B)
  {
    if ((A == null && B != null) ||
        (A != null && B == null)) return false;
    if (A == null && B == null) return true;

    for (T b : B) if (A.equals(b)) return true;
    return false;
  }
  public static boolean isA(int A, int... B)
  {
    for (int b : B) if (A==b) return true;
    return false;
  }
  public static void dispose(Collection... ss)
  {
    if (isSet(ss))
      for (Collection s : ss)
        if (s!=null) { s.clear(); s=null; }
  }
  public static void dispose(StringBuffer s)
  {
    if (s!=null)
    {
      s.delete(0, s.length()); s=null;
    }
  }
  public static void dispose(Map... ss)
  {
    if (isSet(ss))
      for (Map s : ss)
        if (s!=null) { s.clear(); s=null; }
  }

  public static void dispose(MultiTreeTable... ss)
  {
    if (isSet(ss))
      for (MultiTreeTable s : ss)
        if (s!=null) { s.clear(); s=null; }
  }
  public static void dispose(Multimap... ss)
  {
    if (isSet(ss))
      for (Multimap s : ss)
        if (s!=null) { s.clear(); s=null; }
  }
  public static void dispose(Table... ss)
  {
    if (isSet(ss))
      for (Table s : ss)
        if (s!=null) { s.clear(); s=null; }
  }
  public static void dispose(Disposable... ss)
  {
    if (isSet(ss))
      for (Disposable s : ss)
        if (s!=null) { s.dispose(); s=null; }
  }
  public static Map putKeysVal(Object v, Map m, Object... keys)
  {
    if (m!=null && keys!=null && v!=null)
      for (Object k : keys) m.put(k, v);
    return m;
  }
  public static Map putNotNull(Map m, Object k, Object v)
  {
    if (m!=null && k!=null && v!=null) m.put(k, v);
    return m;
  }
  public static double[][] sort(double[]... xs)
  {
    // bubble sort from
    // http://thilinasameera.wordpress.com/2011/06/01/sorting-algorithms-sample-codes-on-java-c-and-matlab/
    int lenD = xs[0].length; double tmp = 0;
    for(int i = 0;i<lenD;i++)
      for(int j = (lenD-1);j>=(i+1);j--)
        if(xs[0][j]<xs[0][j-1])
          for (int k=0; k<xs.length; k++)
          {
            tmp = xs[k][j];
            xs[k][j]=xs[k][j-1];
            xs[k][j-1]=tmp;
          }

    return xs;
  }
  public static <T extends Object> Collection<T> slice(TreeBasedTable<Double, Double, T> data, Double r1, Double r2, Double c1, Double c2)
  {
    Collection<T> results = new ArrayList<T>();

    if (isSet(data) && (r2>r1) && (c2>c1))
    {
      SortedMap<Double,Map<Double, T>> s1 = data.rowMap().subMap(r1, r2);
      if (isSet(s1))
      {
        SortedMap<Double,Map<Double, T>> s2 = s1.subMap(c1, c2);
        if (isSet(s2))
          for (Map<Double, T> s3 : s2.values())
            results.addAll(s3.values());
        s2=null;
      }
      s1=null;
    }
    return results;
  }
  public static <K, V> BiMap<K, V> putNew(BiMap<K, V> m, K key, V val)
  {
    if (m==null || key==null || val==null) return m;
    // Check if the row or the aligned row is already filled
    if (!m.containsKey(key) && !m.inverse().containsKey(val)) m.put(key, val);

    return m;
  }
  public static <T> Collection<T> common(Collection<T> A, Collection<T> B)
  {
    if (A==null || B==null) return A;

    Iterator<T> itr = A.iterator();
    while (itr.hasNext())
      if (!B.contains(itr.next())) itr.remove();

    return A;
  }
  public static boolean equals(Map A, Map B)
  {
    if (A==null && B==null) return true;
    if (A==null || B==null || A.size()!=B.size() || A.keySet().size()!=B.keySet().size()) return false;

    for (Object k1 : A.keySet())
      if (!B.containsKey(k1) || !equals(A.get(k1), B.get(k1))) return false;

    return true;
  }
  public static boolean equals(Collection A, Collection B)
  {
    if (A==null && B==null) return true;
    if (A==null || B==null || A.size()!=B.size()) return false;

    for (Object a : A)
      if (!B.contains(a)) return false;

    return true;
  }
  public static boolean equals(Object A, Object B)
  {
    if (A==null && B==null) return true;
    return A!=null&&B!=null?A.equals(B):false;
  }
  public static boolean equalsCaseless(String A, String B)
  {
    if (A==null && B==null) return true;
    return A!=null&&B!=null?A.equalsIgnoreCase(B):false;
  }
  public static <T> boolean contains(Collection<T> vals, T s)
  {
    if (vals==null || s==null) return false;
    for (T t : vals)
      if (equals(t, s)) return true;

    return false;
  }
  public static <T> Collection<T> overlap(T[] A, T[] B)
  {
    return overlap(Arrays.asList(A), Arrays.asList(B));
  }
  public static <T> Collection<T> onlyA(T[] A, T[] B)
  {
    return onlyA(Arrays.asList(A), Arrays.asList(B));
  }
  public static <T> Collection<T> overlap(Collection<T> A, Collection<T> B)
  {
    if (A==null || B==null) return null;

    Collection<T> shared = new ArrayList<T>();
    for (T t1 : A)
      if (contains(B, t1)) shared.add(t1);

    return shared;
  }
  public static <T> int intersect_counts(Set<T> A, Set<T> B)
  {
    if (A==null || B==null) return 0;

    int shared=0;;
    for (T t1 : A)
      if (B.contains(t1)) shared++;

    return shared;
  }
  public static <T> Collection<T> onlyA(Collection<T> A, Collection<T> B)
  {
    if (A==null || B==null) return null;

    Set<T> only = new HashSet<T>(), bb = new HashSet<>(B);
    for (T t1 : A)
      if (!bb.contains(t1)) only.add(t1);

    return only;
  }
  public static Number negates(Number s)
  {
    if (s!=null)
    {
      if      (s instanceof Double)  return -((Double )s);
      else if (s instanceof Float )  return -((Float  )s);
      else if (s instanceof Integer) return -((Integer)s);
      else if (s instanceof Long   ) return -((Long   )s);
    }

    return s;
  }
  public static <V extends Comparable> SortedSetMultimap<Double, V> reverse(SortedSetMultimap<Double, V> s)
  {
    if (s==null) return s;

    SortedSetMultimap<Double, V> reversed = TreeMultimap.create();
    for (Double k : s.keySet()) reversed.putAll(-k, s.get(k));

    return reversed;
  }
  public static Range<Double> window(Range<Double> slice,
                                     double center, double dx,
                                     double lower, double upper)
  {
    if      (center - dx < lower) return Range.closed(lower, lower + dx);
    else if (center + dx > upper) return Range.closed(upper - dx,  upper);
    else Range.closed(center - dx, center + dx);

    return slice;
  }
  public static IntSet newIntSet(int... ns)
  {
    if (isSet(ns))
    {
      IntSet n = new IntHashSet();
      for (int i : ns) n.add(i);
      return n;
    }
    return null;
  }
  public static byte[] toByteArray(BitSet bits)
  {
    //byte[] bytes = new byte[bits.length()/8+1];
    byte[] bytes = new byte[(bits.length() + 7) / 8]; // Also this can end up with an array which is longer than it needs to be.
    for (int i=0; i<bits.length(); i++) {
      if (bits.get(i)) {
        bytes[bytes.length-i/8-1] |= 1<<(i%8);
      }
    }
    return bytes;
  }
  public static double[] toDoubleArray(Collection<Double> s)
  {
    if (s==null) return null;
    double[] data = new double[s.size()];
    int order=0;
    for (Double d : s)
    {
      data[order++] = d;
    }
    return data;
  }
  public static int[] toIntArray(Collection<Integer> s)
  {
    if (s==null) return null;
    int[] data = new int[s.size()];
    int order=0;
    for (Integer d : s)
    {
      data[order++] = d;
    }
    return data;
  }
  public static BitSet fromByteArray(byte[] bytes)
  {
    BitSet bits = new BitSet();
    for (int i=0; i<bytes.length*8; i++) {
      //if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) > 0) {
      if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) != 0) {
        bits.set(i);
      }
    }
    return bits;
  }
  public static <T> Collection<T> add(Collection<T> s, T... x)
  {
    if (s!=null && x!=null)
      for (T t : x) s.add(t);
    return s;
  }
  public static <T> Collection<T> addAll(Collection<T> s, Collection<T> x)
  {
    if (s!=null && x!=null)
      for (T t : x) s.add(t);
    return s;
  }
  public static <T> Collection<Integer> add(Collection<Integer> s, IntSet x)
  {
    if (s!=null && x!=null)
      for (Integer i : x.toIntegerArrayList()) s.add(i);
    return s;
  }
  public static IntSet addAll(IntSet A, IntSet x)
  {
    if (A!=null && x!=null) A.addAll(x);
    return A;
  }

  public static int hashCode(Map s)
  {
    int hcode=0;
    if (isSet(s))
      for (Map.Entry E : (Collection<Map.Entry> )s.entrySet())
        hcode += E.getKey().hashCode()+E.getValue().hashCode();

    return hcode;
  }
  public static int hashCode(Object... s)
  {
    int hcode=0;
    if (isSet(s))
      for (Object o : s) hcode+=(o!=null?o.hashCode():0);
    return hcode;
  }
  public static <T> T fromLast(List<T> s, int n)
  {
    return s!=null && s.size()>2 ? s.get(s.size()-n) : null;
  }
  public static boolean hasNULL(Collection s)
  {
    if (s==null) return true;
    for (Object v : s) if (v==null) return true;

    return false;
  }
  public static Range<Double> extendLower(Range<Double> s, Double x)
  {
    return s!=null && s.lowerEndpoint()>x ? Range.closed(x, s.upperEndpoint()) : s;
  }
  public static Range<Double> extendUpper(Range<Double> s, Double x)
  {
    return s!=null && s.upperEndpoint()<x ? Range.closed(s.lowerEndpoint(), x) : s;
  }
  public static Table<String, String, IntSet> put(Table<String, String, IntSet> tbl, String lbl, String val, int n)
  {
    IntSet s = tbl.get(lbl, val);
    if (s==null)
    {
      s=new IntHashSet();
      s.add(n);
      tbl.put(lbl, val, s);
    }
    else s.add(n);
    return tbl;
  }
  public static IntSet intersect(IntSet A, IntSet B)
  {
    if (A==null && B==null) return null;
    if (A==null) return B;
    if (B==null) return A;

    IntSet out = new IntHashSet();
    for (Integer a : A.toIntegerArrayList())
      if (B.contains(a)) out.add(a);

    return out;
  }
  public static String[] toCols(Map<String, String> row, List<String> cols)
  {
    String[] outs = new String[cols.size()];
    for (int i=0; i<cols.size(); i++)
      outs[i] = cols.get(i)!=null?row.get(cols.get(i)):null;

    return outs;
  }
  public static String[] toColsHdr(Map<String, String> row, List<String> cols)
  {
    String[] outs = new String[cols.size()*2];
    for (int i=0; i<cols.size(); i+=2)
      if (cols.get(i)!=null)
      {
        outs[i] = cols.get(i); outs[i+1] = row.get(cols.get(i));
      }

    return outs;
  }
  public static Set asSet(Collection s)
  {
    if (s!=null)
    {
      Set ss = new HashSet();
      ss.addAll(s);
      return ss;
    }
    return null;
  }
  public static String[] toColsHdr(Map<String, String> row, Map<String, String> cols)
  {
    String[] outs = new String[cols.size()*2];
    int i=0;
    for (String col : cols.keySet())
      if (row.get(col)!=null)
      {
        outs[i++] = cols.get(col); outs[i++] = row.get(col);
      }

    return outs;
  }
  public static Map<String, String> slice(Map<String, String> props, String... keys)
  {
    if (!isSet(keys) || !isSet(props)) return props;

    Map<String, String> out = new HashMap<>();
    for (String key : keys)
      if (props.get(key)!=null) out.put(key, props.get(key));

    return out;
  }
  public static long[] reverse(long[] array) {
    int sz = array.length;
    int t = sz / 2;

    for(int i = 0; i < t; ++i) {
      long tmp = array[i];
      array[i] = array[sz - i - 1];
      array[sz - i - 1] = tmp;
    }
    return array;
  }
  public static double[] reverse(double[] array) {
    int sz = array.length;
    int t = sz / 2;

    for(int i = 0; i < t; ++i) {
      double tmp = array[i];
      array[i] = array[sz - i - 1];
      array[sz - i - 1] = tmp;
    }
    return array;
  }

  /* @param ordered is an ordered map of key-obj
   * @param x is the desired location of the key
   * @return a subtable where each column has at least an object below and above the x
   */
  public static <R extends Comparable, C extends Comparable, T extends Object> RowSortedTable<R, C, T>
      interpolate(@Nonnull RowSortedTable<R, C, T> ordered, R x)
  {
    SortedSet<R>    tail=ordered.rowKeySet().tailSet(x);
    NavigableSet<R> head=new TreeSet<R>(ordered.rowKeySet().headSet(x)).descendingSet();

    // step through the upper and lower ranks
    RowSortedTable<R, C, T> sub = TreeBasedTable.create();
    for (C col : ordered.columnKeySet())
      for (R row : head)
        if (ordered.contains(row, col))
        {
          sub.put(row, col, ordered.get(row, col)); break;
        }
    for (C col : ordered.columnKeySet())
      for (R row : tail)
        if (ordered.contains(row, col))
        {
          sub.put(row, col, ordered.get(row, col)); break;
        }

/*
    // create an ordered list of row keys
    R[] rows = (R[] )Array.newInstance(x.getClass(), ordered.rowKeySet().size());
    int counts=0;
    for (R r : ordered.rowKeySet()) rows[counts++]=r;

    // sort the numbers
    Arrays.sort(rows);

    int pos = Arrays.binarySearch(rows, x);
    // convert it to the upper bound of the interval if not matched
    if (pos<0) pos = -1*(pos+1);

    // expand upward if permitted
    int lower=rows.length, upper=0;
    for (C col : ordered.columnKeySet())
      for (int i=pos; i<rows.length; i++)
        if (ordered.contains(rows[i], col))
        {
          if (i>upper) upper=i;
          break;
        }
    // check the other direction
    for (C col : ordered.columnKeySet())
      for (int i=pos-1; i>=0; i--)
        if (ordered.contains(rows[i], col))
        {
          if (i<lower) lower=i;
          break;
        }

    // create the sub-table
    RowSortedTable<R, C, T> sub = TreeBasedTable.create();
    for (int i=lower; i<upper; i++)
      for (C col : ordered.row(rows[i]).keySet())
        if (ordered.contains(rows[i], col)) sub.put(rows[i], col, ordered.get(rows[i], col));
*/

    return sub;
  }
  public static int lastIndexBelow(@Nonnull List<Double> s, int start, double value)
  {
    if (start<s.size())
      for (int i=start; i<s.size(); i++)
        if (s.get(i)>value) return i-1;

    // signal invalid start
    return -1;
  }

  public static int firstIndexAbove(@Nonnull List<Double> s, int start, double value)
  {
    if (start<s.size())
      for (int i=start; i<s.size(); i++)
        if (s.get(i)>value) return i;

    // signal invalid start
    return -1;
  }
  public static <T> Collection<T> flatten(Collection<Collection<T>> s)
  {
    if (s==null) return null;

    Collection<T> ss = new ArrayList<T>();
    for (Collection<T> s1 : s) ss.addAll(s1);

    return ss;
  }
  public static PpmTolerance scalePpmTolerance(PpmTolerance ppm, double scale)
  {
    if (ppm==null) return ppm;

    return new PpmTolerance(scale*1E6*(ppm.getMax(500d)-500d)/500d);
  }
  public static <T> List<T> copyOf(List<T> s, int left, int right)
  {
    if (s==null || left<0 || right>s.size()) return s;

    List<T> ss = new ArrayList<>();
    for (int i=left; i<right; i++) ss.add(s.get(i));

    return ss;
  }
  public static <T> T[] c(T[]... ss)
  {
    if (!isSet(ss)) return null;

    int counts=0;
    for (T[] s : ss) counts+=s.length;

    List<T> out = new ArrayList<T>(counts);
    for (T[] s : ss)
      for (T t : s) out.add(t);

    return out.toArray(ss[0]);
  }
  public static int[] c(int[]... ss)
  {
    if (!isSet(ss)) return null;

    int counts=0;
    for (int[] s : ss) counts+=s.length;

    int[] out = new int[counts]; int i=0;
    for (int[] s : ss)
      for (int t : s) out[i++]=t;

    return out;
  }
  public static <T> List<List<T>> partition(Collection<T> s, int n)
  {
    if (!isSet(s)) return null;
    return Lists.partition(new ArrayList<T>(s), n);
  }
  public static <K, V> boolean hasKeys(Map<K,V> map, K... keys)
  {
    if (map==null || keys==null) return false;
    for (K k : keys)
      if (!map.containsKey(k)) return false;

    return true;
  }
  public static <K, V> boolean hasKeys(Map<K,V> map, Collection<K> keys)
  {
    if (map!=null && keys!=null)
      for (K k : keys)
        if (map.containsKey(k)) return true;

    return false;
  }
  public static Collection<Float> find(float[] AAs, float left, float right)
  {
    Collection<Float> found = new HashSet<>();
    if (AAs!=null && right>left)
      for (float AA : AAs)
        if (AA>=left && AA<=right) found.add(AA);

    return found;
  }
  public static Float findClosest(float[] AAs, float target, float tol)
  {
    Float found = null, delta=tol;
    if (AAs!=null)
      for (float AA : AAs)
        if (Math.abs(AA-target)<=delta) { found=AA; delta=Math.abs(AA-target); }

    return found;
  }
  public static int hashCodes(boolean... s)
  {
    int h=0;
    if (s!=null)
      for (boolean x : s) h+=Boolean.hashCode(x);

    return h;
  }
  public static int hashCodes(double... s)
  {
    int h=0;
    if (s!=null)
      for (double x : s) h+=Double.hashCode(x);

    return h;
  }
  public static Collection sampling(Collection data, int start, int spacing)
  {
    if (!isSet(data) || data.size()<spacing*2) return data;

    Collection out = new ArrayList(); int counts=0;
    for (Object s : data)
    {
      if ((++counts+start)%spacing==0) out.add(s);
    }
    return out;
  }
  public static <K,V> Multimap<K,V> remove(Multimap<K,V> map, K key, V val)
  {
    if (map!=null) map.remove(key, val);
    return map;
  }
  public static int bound(int val, int low, int high)
  {
    if (low>=high)
      if (val<low) return low; else if (val>high) return high; else return val;

    if (val<high) return high; else if (val>low) return low; else return val;
  }
  public static boolean isLarger(Collection A, Collection B)
  {
    return (A!=null && (B==null || A.size()>B.size()));
  }
  public static boolean lessThan(Double A, Double B)
  {
    return (A!=null && B==null && A<B);
  }
  public static double growth(double A, double B) { return (B-A)/(A!=0?A:B); }
}
