package org.ms2ms.utils;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.Table;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: wyu
 * Date: 6/13/14
 * Time: 8:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Strs
{
  public static final String NULL              = "nUlL";
  public static final String INVALID_SELECTION = "iNvAlId sElEcTiOn";
  public static final String BLANK             = "  1lsd 00s *&**";
  public static final String ALPHA             = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  public static final Pattern PTN_SIGNS        = Pattern.compile("(?=[(+)(\\-)])");
  public static final Pattern PTN_SIGNS_DGT    = Pattern.compile("[+-.\\d]+");

  public static String concatenate(char t, Object... ss)
  {
    String out = null;
    if (Tools.isSet(ss))
      for (Object s : ss)
        if (s!=null)
        {
          if (s instanceof Optional) out = extend(out, ((Optional) s).get().toString(), t + "");
          else                       out = extend(out, s.toString(), t + "");
        }

    return out;
  }

  public static String extend(String s0, String s1, String delimiter)
  {
    return s0==null?s1:(s1!=null?s0+delimiter+s1:s0);
  }
  public static String extend(String s0, String s1, char delimiter)
  {
    return s0==null?s1:(s1!=null?s0+delimiter+s1:s0);
  }
  public static String dequotes(String s, char q)
  {
    if (s.length() > 0              &&
        s.charAt(0)            == q &&
        s.charAt(s.length()-1) == q)
      return s.length() - 1 > 1 ? s.substring(1, s.length() - 1) : s.substring(1);
    return s;
  }
  public static void dequotes(String[] ss, char q)
  {
    for (int i = 0; i != ss.length; i++)
      ss[i] = dequotes(ss[i], q);
  }
  public static String[] trim(String[] ss)
  {
    for (int i = 0; i != ss.length; i++)
      ss[i] = new String(ss[i].trim());

    return ss;
  }

  public static String trim(String s) { return s!=null?s.trim():s; }
  public static List<String> split(String s, String regx, boolean trim)
  {
    List<String> list  = new ArrayList<String>();
    String[]     items = s.split(regx);
    for (String ss : items)
      if (!trim || isSet(ss.trim()))
        list.add(trim ? ss.trim() : ss);
    // return the list
    return list;
  }
  public static List<String> split(String s, Pattern p, boolean trim)
  {
//    Pattern.compile(regex).split(this, limit)
    List<String> list  = new ArrayList<String>();
    String[]     items = p.split(s, 0);
    for (String ss : items)
      if (!trim || isSet(ss.trim()))
        list.add(trim ? ss.trim() : ss);
    // return the list
    return list;
  }
  public static String[] split(String s, char c, boolean trim)
  {
    if (s==null) return null;

    int i, b, e;
    int cnt;
    String res[];
    int ln = s.length();

    i = 0;
    cnt = 1;
    while ((i = s.indexOf(c, i)) != -1) {
      cnt++;
      i++;
    }
    res = new String[cnt];

    i = 0;
    b = 0;
    while (b <= ln) {
      e = s.indexOf(c, b);
      if (e == -1) {
        e = ln;
      }
      if (!trim) {
        res[i++] = s.substring(b, e);
      } else {
        int e2 = e - 1;
        while (e2 >= 0 && Character.isWhitespace(s.charAt(e2))) {
          e2--;
        }
        e2++;
        while (b < ln && Character.isWhitespace(s.charAt(b))) {
          b++;
        }
        if (b < e) {
          res[i++] = s.substring(b, e2);
        } else {
          res[i++] = "";
        }
      }
      b = e + 1;
    }
    return res;
  }
  public static String[] split(String s, char t)
  {
    if (s == null || t == 0) return null;

    return split(s, t, false);
  }
  public static String split2front(String s, char t)
  {
    if (s == null || t == 0) return "";

    return split(s, t, false)[0];
  }
  public static String split2back(String s, char t)
  {
    if (s == null || t == 0) return "";

    return Tools.back(split(s, t, false));
  }
  public static Integer[] split2Ints(String s, char t)
  {
    if (s == null || t == 0) return null;

    String[] ss = split(s, t, true);
    if (Tools.isSet(ss))
    {
      Integer[] out = new Integer[ss.length];
      for (int i=0; i<ss.length; i++) out[i] = Integer.parseInt(ss[i]);
      return out;
    }
    return null;
  }
  public static Range<Integer> split2IntRange(String s, char t)
  {
    if (s == null || t == 0) return null;

    String[] ss = split(s, t, true);
    if (Tools.isSet(ss))
    {
      Integer[] out = new Integer[ss.length];
      try
      {
        for (int i=0; i<ss.length; i++) out[i] = Integer.parseInt(ss[i]);
      }
      catch (Exception e) { return null; }
      return out.length>1?Range.closed(out[0],out[1]):null;
    }
    return null;
  }

  // Perl style split which retains the delimiter
  // http://stackoverflow.com/questions/2206378/how-to-split-a-string-but-also-keep-the-delimiters
  public static List<String> splits(String s, String pattern) {
    assert s != null;
    assert pattern != null;
    return splits(s, Pattern.compile(pattern));
  }
  public static List<String> splits(String s, Pattern pattern)
  {
    assert s != null;
    assert pattern != null;
    Matcher m = pattern.matcher(s);
    List<String> ret = new ArrayList<String>();
    int start = 0;
    while (m.find()) {
      ret.add(s.substring(start, m.start()));
      ret.add(m.group());
      start = m.end();
    }
    ret.add(start >= s.length() ? "" : s.substring(start));
    return ret;
  }

//  private static void testSplit(String s, String pattern) {
//    System.out.printf("Splitting '%s' with pattern '%s'%n", s, pattern);
//    List<String> tokens = split(s, pattern);
//    System.out.printf("Found %d matches%n", tokens.size());
//    int i = 0;
//    for (String token : tokens) {
//      System.out.printf("  %d/%d: '%s'%n", ++i, tokens.size(), token);
//    }
//    System.out.println();
//  }
//
  public static String toString(String[] ss, String dl)
  {
    if (!Tools.isSet(ss)) return "";
    return ss!=null&&ss.length==1?ss[0]:(toString(ss, dl, 0, ss != null ? ss.length : 0));
  }
  public static <K, T> String toString(Map<K, T> ss, String dl) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (Map.Entry E : ss.entrySet())
      made = extend(made, E.getKey().toString() + "--" + E.getValue().toString(), dl);
    // return the concatenated string
    return made;
  }
  public static <T> String toString(T[] ss, String dl, Integer start, Integer end) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (int i = start; i != Math.min(end, ss.length); i++)
      if (ss[i] != null) made = extend(made, ss[i].toString(), dl);
    // return the concatenated string
    return made;
  }
  public static <T> String toString(T[] ss, String dl) {
    // no point to go further
    if (ss == null) return null;
    if (ss.length==1) return ss[0].toString();

    String made = new String(ss[0].toString());
    for (int i = 1; i != ss.length; i++)
      if (ss[i] != null) made = extend(made, ss[i].toString(), dl);
    // return the concatenated string
    return made;
  }
  public static String toString(double[] ss, String dl, int deci) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (int i = 0; i != ss.length; i++)
      made = extend(made, Tools.d2s(ss[i],deci), dl);
    // return the concatenated string
    return made;
  }
  public static String toString(float[] ss, String dl) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (int i = 0; i != ss.length; i++)
      made = extend(made, ss[i] + "", dl);
    // return the concatenated string
    return made;
  }
  public static String toString(int[] ss, String dl) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (int i = 0; i != ss.length; i++)
      made = extend(made, ss[i] + "", dl);
    // return the concatenated string
    return made;
  }
  public static <T> String toString(Collection<T> ss, String dl) {
    // no point to go further
    if (ss!=null)
    {
      String made = null;
      for (T t : ss)
        if (t != null) made = extend(made, t.toString(), dl);
      // return the concatenated string
      return made;
    }
    return "";
  }
  public static String toString(Collection<Double> ss, int deci, String dl) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (Double t : ss)
      if (t != null) made = extend(made, Tools.d2s(t, deci), dl);
    // return the concatenated string
    return made;
  }
  public static String toString(Double[] ss, int deci, String dl) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (Double t : ss)
      if (t != null) made = extend(made, Tools.d2s(t, deci), dl);
    // return the concatenated string
    return made;
  }
  public static String toString(String[] ss, String dl, Integer start, Integer end) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (int i = start; i != end; i++) if (ss[i] != null) made = extend(made, ss[i], dl);
    // return the concatenated string
    return made;
  }
  public static <T> List<String> toStrings(List<T> ss) {
    // no point to go further
    if (ss == null) return null;
    List<String> made = new ArrayList<String>();
    for (T t : ss)
      if (t != null) made.add(t.toString());
    // return the concatenated string
    return made;
  }
/*
  public static String[] toStringArray(Collection<String> ss)
  {
    if (!Tools.isSet(ss)) return null;

    String[] cols = new String[ss.size()];
    int order = 0;
    for (String s : ss) cols[order++] = s;

    return cols;
  }
*/
  public static <T> Collection<String> toStrings(Collection<T> ss) {
    // no point to go further
    if (ss == null) return null;
    Collection<String> made = new ArrayList<String>();
    for (T t : ss)
      if (t != null) made.add(t.toString());
    // return the concatenated string
    return made;
  }
  public static String toQuotedString(String[] ss, String dl, String quote) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (int i = 0; i != ss.length; i++)
      if (ss[i] != null) made = extend(made, quote + ss[i] + quote, dl);
    // return the concatenated string
    return made;
  }
  public static String toQuotedString(Collection<String> ss, String dl, String quote) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (String s : ss)
      if (s != null) made = extend(made, quote + s + quote, dl);
    // return the concatenated string
    return made;
  }
  public static String toQuotedString(Object[] ss, String dl, String quote) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (int i = 0; i != ss.length; i++)
      if (ss[i] != null) made = extend(made, quote + ss[i].toString() + quote, dl);
    // return the concatenated string
    return made;
  }
  public static String toString(List<String> ss, String dl) {
    // no point to go further
    if (ss == null) return null;
    String made = null;
    for (String s : ss)
    {
      made = extend(made, s.replaceAll(dl,"_"), dl);
    }
    // return the concatenated string
    return made;
  }
  public static String toStringFromLong(Collection<Long> ss, String dl) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (Long s : ss) made = extend(made, s.toString(), dl);
    // return the concatenated string
    return made;
  }
  public static String toStringFromLong(Long[] ss, String dl) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (Long s : ss) made = extend(made, s.toString(), dl);
    // return the concatenated string
    return made;
  }
  public static String toStringFromInt(int[] ss, String dl) {
    // no point to go further
    if (ss == null) return null;
    String made = new String();
    for (int s : ss) made = extend(made, s+"", dl);
    // return the concatenated string
    return made;
  }
  public static String[] toStringArray(Collection s)
  {
    String[] out = new String[s.size()];
    int i=0;
    for (Object v : s)  out[i++]=v.toString();

    return out;
  }
  // add the strs to the beginning of the collection
  public static String[] toStringArrayHead(Collection s, String... strs)
  {
    String[] out = new String[s.size()+(Tools.isSet(strs)?strs.length:0)];
    int i=0;
    if (Tools.isSet(strs))
    {
      i = strs.length;
      for (int j=0; j<strs.length; j++) out[j]=strs[j];
    }

    if (Tools.isSet(s))
      for (Object v : s)  out[i++]=v.toString();

    return out;
  }
  public static String[] toStringArray(Object... s)
  {
    String[] out = new String[s.length];
    int i=0;
    for (Object v : s)  out[i++]=v.toString();

    return out;
  }
  public static String[] merge(String[]... s)
  {
    if (!Tools.isSet(s)) return null;

    List<String> items = new ArrayList<>();
    for (String[] item : s)
      for (String itm : item) items.add(itm);

    return items.toArray(new String[] {});
  }
  public static String toString(String s) { return s!=null?s:""; }
  public static String toString(Long s) { return s!=null?s.toString():""; }
  public static String toString(Table t)
  {
    if (!Tools.isSet(t)) return null;

    StringBuffer buf = new StringBuffer();
    for (Object col : t.columnKeySet()) buf.append(col.toString() + "\t");
    buf.append("\n");
    for (Object row : t.rowKeySet())
    {
      for (Object col : t.columnKeySet())
      {
        Object val = t.get(row, col);
        buf.append((val!=null?val.toString():"--") + "\t");
      }
      buf.append("\n");
    }
    return buf.toString();
  }
  public static String toupper(String s) { return s!=null?s.toUpperCase():s; }
  // retain the last n chars of the string
  public static String rtruncate(String s, int n)
  {
    return s==null||s.length()<n?s:s.substring(s.length()-n);
  }
  // retain the last n chars of the string
  public static String truncate(String s, int n)
  {
    return s==null||s.length()<n?s:s.substring(0, n);
  }
  /** Converts time in milliseconds to a <code>String</code> in the format HH:mm:ss.SSS.
   *  lifted from http://www.uk-dave.com/bytes/java/long2time.php
   *
   * @param time the time in milliseconds.
   * @return a <code>String</code> representing the time in the format HH:mm:ss.SSS.
  */
  public static String msecToString(long time)
  {
    int milliseconds = (int)( time          % 1000);
    int      seconds = (int)((time/1000   ) % 60);
    int      minutes = (int)((time/60000  ) % 60);
    int        hours = (int)((time/3600000) % 24);
    String millisecondsStr = (milliseconds<10 ? "00" : (milliseconds<100 ? "0" : ""))+milliseconds;
    String      secondsStr = (seconds<10 ? "0" : "")+seconds;
    String      minutesStr = (minutes<10 ? "0" : "")+minutes;
    String        hoursStr = (hours<10 ? "0" : "")+hours;
    return new String(hoursStr+":"+minutesStr+":"+secondsStr+"."+millisecondsStr);
  }
  public static boolean isIsobaric(String seq1, String seq2)
  {
    return Tools.equals(seq1, seq2) ||
           isIsobaricTo(seq1, seq2) ||
           isIsobaricTo(seq2, seq1);
  }
  public static boolean isIsobaric(String seq1, Collection<String> seq2s)
  {
    if (Tools.isSet(seq2s) && Strs.isSet(seq1))
      for (String seq2 : seq2s)
        if (Tools.equals(seq1,seq2) || isIsobaricTo(seq1,seq2) || isIsobaricTo(seq2,seq1)) return true;

    return false;
  }
  public static boolean isIsobaricTo(String seq1, String seq2)
  {
    // consider only the single residue change
    boolean matched = false;
//    String regex = seq2.replaceAll("[IL]", "[IL]").replaceAll("[QK]", "[QK]");
    String regex = seq2.replaceAll("[IL]", "[IL]");
    matched = seq1.matches(regex);

    // let's consider the di-one residue mapping (Kinter, p83)
    /*if (!matched)
    {
      matched = (seq1.matches(seq2.replaceAll("(GG)", "[(GG)|N]"))           ||
                 seq1.matches(seq2.replaceAll("[(GA)|(AG)]", "[(GA)|(AG)|Q|K]")) ||
                 seq1.matches(seq2.replaceAll("[(GV)|(VG)]", "[(GV)|(VG)|R]"))   ||
                 seq1.matches(seq2.replaceAll("[(GE)|(AD)|(SV)|(EG)|(DA)|(VS)]", "[(GE)|(AD)|(SV)|(EG)|(DA)|(VS)|W]")));
    } */
    // the last check for 2-to-2 mapping
    if (!matched)
    {
      matched = (seq1.equals(seq2.replace("NQ", "LE")) ||
          seq1.equals(seq2.replace("QN", "LE")) ||
          seq1.equals(seq2.replace("NQ", "EL")) ||
          seq1.equals(seq2.replace("QN", "EL")));
    }
    return matched;
  }
  public static boolean equals(String A, String B)
  {
    return (A==null && B==null) || (A!=null && B!=null && A.equals(B));
  }
  public static boolean equals(Optional<String> A, String B)
  {
    return (A!=null && A.isPresent() && B!=null && B.equals(A.get()));
  }
  public static boolean equalsIgnoreCase(String A, String B)
  {
    return (A==null && B==null) || (A!=null && B!=null && A.equalsIgnoreCase(B));
  }
  public static boolean hasEqualsIgnoreCase(String A, Collection<String> Bs)
  {
    if (A==null && Bs==null) return true;
    if (A!=null && Bs!=null)
      for (String B : Bs)
        if (A.equalsIgnoreCase(B)) return true;

    return false;
  }
  public static boolean isSet(String         s) { return s!=null && s.length()>0; }
  public static boolean isSet(StringBuilder  s) { return s!=null && s.length()>0; }

  // return TRUE if s is a part of any item from vals
  public static boolean hasA(String s, int start, String... vals)
  {
    if (Tools.isSet(vals))
      for (String val : vals)
        if (indexOf(s, val)>=start) return true;

    return false;
  }
  public static boolean hasA(String s, Collection<String> vals)
  {
    if (!Tools.isSet(vals)) return false;
    for (String val : vals)
      if (indexOf(s, val)>=0) return true;

    return false;
  }
  public static String getEqualOrEmbedded(String s, Collection<String> vals)
  {
    if (!Tools.isSet(vals)) return null;
    for (String val : vals)
      if (s.equals(val)) return val;

    for (String val : vals)
      if (indexOf(val, s)>=0) return val;

    return null;
  }
  public static boolean hasApart(String s, Collection<String> vals)
  {
    if (!Tools.isSet(vals)) return false;
    for (String val : vals)
      if (indexOf(s,val)>=0 || indexOf(val,s)>=0) return true;

    return false;
  }
  public static boolean isA(String s, String... vals)
  {
    if (Tools.isSet(vals))
      for (String val : vals)
        if (equals(s, val)) return true;

    return false;
  }
  public static boolean isA(Optional<String> s, String... vals)
  {
    if (Tools.isSet(vals))
      for (String val : vals)
        if (equals(s, val)) return true;

    return false;
  }
  public static int left_overlap(String A, String B)
  {
    if (isSet(A) && isSet(B))
      for (int i=Math.min(A.length(), B.length()); i>0; i--)
        if (equals(A.substring(0,i), B.substring(0,i))) return i;

    return 0;
  }
  public static int right_overlap(String A, String B)
  {
    if (isSet(A) && isSet(B))
      for (int i=Math.min(A.length(), B.length()); i>0; i--)
        if (equals(A.substring(A.length()-i, A.length()), B.substring(B.length()-i,B.length()))) return i;

    return 0;
  }

  /** create a new string map from an array of strings
   *
   * @param t is the token char
   * @param items is one or more string in the format of "tag=val" where '=' as specified in t
   * @return a new string map
   */
  public static Map<String, String> newMap(char t, String... items)
  {
    if (!Tools.isSet(items)) return null;

    // to preserve the insertion order
    Map<String, String> pairs = new LinkedHashMap<>();
    for (String item : items)
    {
      if (t=='0') pairs.put(item,item);
      else
      {
        String[] ss = split(item, t);
        if (ss!=null && ss.length>1) pairs.put(ss[0], ss[1]);
      }
    }
    return pairs;
  }
  public static Multimap<String, String> newMultimap(char t, String... items)
  {
    if (!Tools.isSet(items)) return null;

    Multimap<String, String> pairs = HashMultimap.create();
    for (String item : items)
    {
      String[] ss = split(item, t);
      if (ss!=null && ss.length>1)
      {
        pairs.putAll(ss[0], split(ss[1], ";", true));
      }
    }
    return pairs;
  }

  public static Map<String, String> toStrMap(String... tagvals)
  {
    Map<String, String> out = new HashMap<>();
    for (int i=0; i<tagvals.length; i+=2)
    {
      if (i+1<tagvals.length)
        out.put(tagvals[i], tagvals[i + 1]);
    }
    return out;
  }
  public static Map<String, String> toStrMap1(String... tagvals)
  {
    Map<String, String> out = new HashMap<>();
    for (String t : tagvals)
    {
      out.put(t, t);
    }
    return out;
  }
  public static String stripLastOf(String s, char t)
  {
    if (isSet(s) && s.indexOf(t)>=0) return s.substring(0, s.lastIndexOf(t));
    return s;
  }
  public static int indexOf(String A, String B) { return A!=null&&B!=null?A.indexOf(B):-1; }

  public static char[] shuffle(char[] chars)
  {
    return shuffle(chars, new Random(System.nanoTime()));
  }
  public static char[] shuffle(char[] chars, Random rnd)
  {
    // Shuffle array
    for (int i=chars.length; i>1; i--)
    {
      int j=rnd.nextInt(i);
      char c=chars[i-1];
      chars[i-1]=chars[j]; chars[j]=c;
    }
    return chars;
  }
  public static char[] rnd(char[] chars)
  {
    return rnd(chars, new Random(System.nanoTime()));
  }
  public static char[] rnd(char[] chars, Random rnd)
  {
//    Random rnd = new Random(System.nanoTime());
    char[] out = new char[chars.length];
    // making the copy of the char at the random location
    for (int i=0; i<chars.length; i++)
    {
      out[i]=chars[rnd.nextInt(chars.length)];
    }
    return out;
  }
  public static String toString(char[] sequence, int left, int right)
  {
    if (sequence==null || right<left) return "";

    StringBuilder buf = new StringBuilder();
    buf.append(Arrays.copyOfRange(sequence, left, right));

    return buf.toString();
  }
  public static boolean hasSubStr(String A, String B)
  {
    if (A!=null && B!=null)
      if (A.indexOf(B)>=0 || B.indexOf(A)>=0) return true;

    return false;
  }
  public static boolean hasSubStr(String s, Collection<String> strs)
  {
    if (s!=null && strs!=null)
      for (String str : strs)
        if (s.indexOf(str)>=0 || str.indexOf(s)>=0) return true;

    return false;
  }
  public static String reverse(String s)
  {
    if (isSet(s))
    {
      StringBuffer buf = new StringBuffer(s);
      return buf.reverse().toString();
    }
    return s;
  }
  public static String guessString(Object obj)
  {
    String out=null;
    if (obj!=null)
    {
      if      (obj instanceof List)
      {
        for (Object ob : (List )obj)
          if (ob!=null) out=extend(out, ob.toString(), ",");
      }
      else if (obj instanceof Object[])
      {
        for (Object ob : (Object[] )obj)
          if (ob!=null) out=extend(out, ob.toString(), ",");
      }
      else out=obj.toString();
    }
    return out;
  }
  public static char last(String s)
  {
    return isSet(s)?s.charAt(s.length()-1):0;
  }
  public static String addWiki(Object... val)
  {
    String wiki = "";
    if (Tools.isSet(val))
      for (Object v : val)
        wiki+="|"+(v!=null && v.toString().length()>0 ?v.toString():" ");
    return wiki;
  }
  public static String addWiki(LinkedHashMap<String,String> val, String... cols)
  {
    if (!Tools.isSet(val)) return "";

    String wiki = "";
    if (Tools.isSet(cols))
    {
      for (String v : cols)
        wiki+="|"+(Strs.isSet(val.get(v))?val.get(v):" ");
    }
    else
    {
      for (String v : val.keySet())
        wiki+="|"+(val.get(v).length()>0 ?val.get(v):" ");
    }

    return wiki;
  }
  public static String replaceAll(String s, String fr, String to)
  {
    return (isSet(s) && s.indexOf(fr)>=0)?s.replaceAll(fr,to):s;
  }
//  public static String fromLast(List<String> s, int n)
//  {
//    return s!=null && s.size()>2 ? s.get(s.size()-n) : null;
//  }
}
