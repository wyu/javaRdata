package org.ms2ms.utils;

import org.ms2ms.math.Stats;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * User: wyu
 * Date: 6/12/14
 * Time: 11:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class TabFile
{
  private int                mLineSkip   = 0;
  private String[]           mHds        = null;
  private Map<String,String> mCol = null, mTransforms = null;
  private BufferedReader     mFileReader = null;
  private String             mDelimiter  = tabb;
  private char               mTokenChar  = 0;
  private Pattern mToken      = Pattern.compile(tabb);
  private String             mFilename   = null,mCurrentLine=null,mHeaderLine=null;
  private String             mSkip       = null, mRowHeader=null;

  public static String       comma       = ",(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))";
  public static String       tabb        = "\\t";
  public static String       dot         = "\\.";
  public static String       semicolumn  = ":";
  public static String       column      = ";";
  public static String       space       = "\\s";
  public static String       slash       = "/";
  public static String       at          = "@";
  public static String       pipe        = "\\|";
  public static String       pminus      = "?";

  public TabFile(String              infile) throws IOException
  {
    // no need to trap the exceptions since they are
    // delegated to the calling party
    mFilename   = infile;
  }

  public TabFile(String              infile,
                 String              delimiter) throws IOException
  {
    // no need to trap the exceptions since they are
    // delegated to the calling party
    mFilename   = infile;
    setDelimiter(delimiter);

    init();
  }
  public TabFile(InputStream         is,
                 String              delimiter) throws IOException
  {
    // no need to trap the exceptions since they are
    // delegated to the calling party
    setDelimiter(delimiter);
    init(is);
  }
  public TabFile(String              infile,
                 char                delimiter) throws IOException
  {
    // no need to trap the exceptions since they are
    // delegated to the calling party
    mFilename   = infile;
    setDelimiter(null);
    mTokenChar = delimiter;

    init();
  }
  public TabFile(String              infile,
                 String              delimiter,
                 int                 line_skip) throws IOException
  {
    // no need to trap the exceptions since they are
    // delegated to the calling party
    mFilename   = infile;
    setDelimiter(delimiter);
    mLineSkip = line_skip;

    init();
  }
  public TabFile(String              infile,
                 String              delimiter,
                 String              skip) throws IOException
  {
    // no need to trap the exceptions since they are
    // delegated to the calling party
    mFilename   = infile;
    setDelimiter(delimiter);
    mSkip       = skip;

    init();
  }
  public TabFile(String              infile,
                   String            delimiter,
                   String...         cols)
      throws IOException
  {
    // no need to trap the exceptions since they are
    // delegated to the calling party
    mFilename   = infile;
    setDelimiter(delimiter);
    mHds        = cols;

    init();
  }
  public TabFile init() throws IOException
  {
    InputStream is = new FileInputStream(mFilename);
    // Gracefully handle gzipped files.
    if (mFilename.endsWith(".gz")) {
      is = new GZIPInputStream(is);
    }
    init(is);
    return this;
  }
  protected void init(InputStream is) throws IOException
  {
    mCol        = new TreeMap<>();
    mFileReader = new BufferedReader(new InputStreamReader(is));

    // skip as many lines as indicated
    if (mLineSkip > 0)
      for (int i = 0; i < mLineSkip; i++)  mFileReader.readLine();

    if (mHds == null)
    {
      // skip line starting with 'skip' if asked
      if (Strs.isSet(mSkip))
      {
        while (mFileReader.ready())
        {
          mHeaderLine = mFileReader.readLine();
          if (mHeaderLine.trim().indexOf(mSkip) != 0) break;
        }
      }
      else mHeaderLine = mFileReader.readLine();

      if (Strs.isSet(mHeaderLine))
      {
        if (Strs.isSet(getRowHeader())) mHeaderLine = getRowHeader()+getDelimiter()+mHeaderLine;
        mHds = split(mHeaderLine);
        Strs.trim(     mHds);
        Strs.dequotes( mHds, '"');
      }
    }
  }
  public String   getFileName()  { return mFilename; }
  public String   getDelimiter() { return mDelimiter; }
  public String[] getHeaders()   { return mHds; }
  public String   getHeaderLine(){ return mHeaderLine; }
  public String   getRowHeader() { return mRowHeader; }

  //--------------------------------------------------------------------------
  public Map<String, String> getMappedRow()
  {
    return mCol;
  }

//  public Double number( String key) { return Double.parseDouble(cells(key)); }
//  public Long   integer(String key) { return Long.parseLong(cells(key)); }

  public TabFile setRowHeader(String s) { mRowHeader=s; return this; }
  public String getCurrentLine() { return mCurrentLine; }

  public TabFile setDelimiter(String s)
  {
    mDelimiter =  s;
    mToken     = (s != null ? Pattern.compile(s) : null);
    return this;
  }
  //--------------------------------------------------------------------------
  public boolean hasNext() throws IOException
  {
    // reset the current line
    mCurrentLine=null;

    // read-in the line in a separated step
    // skip line starting with 'skip' if asked
    if (Strs.isSet(mSkip))
    {
      while (mFileReader.ready())
      {
        mCurrentLine = mFileReader.readLine();
        if (mCurrentLine.trim().indexOf(mSkip) != 0) break;
      }
    }
    else mCurrentLine = mFileReader.readLine();

    // split the fields according to the delimiter
    String[] cols=mCurrentLine!=null?split(mCurrentLine):null;
    if (/*mCurrentLine!=null && */Tools.isSet(cols))
    {
      // forget the duplicated header
      while (Strs.isSet(cols[0]) && Strs.isSet(mHds[0]) && cols[0].equals(mHds[0]))
      {
        String hd = mFileReader.readLine();
        if (hd != null) cols = split(hd);
      }
      Strs.dequotes(cols, '"');
      Strs.trim(    cols);
      // populate the col map
      mCol.clear();
      for (int i = 0; i != cols.length; i++)
        // do we need to do the transformation?
        if (i < getHeaders().length) {
          String key = getHeaders()[i];
          mCol.put(key, cols[i]);
        }
    }

    return (mCurrentLine != null);
  }

  public Map<String, String> nextRow() { return mCol; }

  protected String[] split(String s)
  {
    return s != null ? (mTokenChar != 0 ? Strs.split(s, mTokenChar) : mToken.split(s)) : null;
  }

  //--------------------------------------------------------------------------
  public boolean ready() throws IOException { return mFileReader.ready(); }

  //--------------------------------------------------------------------------
  public void close() throws IOException
  {
    if (mFileReader != null) mFileReader.close();
  }
  protected void finalize() throws IOException { close(); }
  public Double  getDouble(String... keys)
  {
    if (Tools.isSet(keys))
      for (String key : keys)
        if (get(key)!=null && !"NaN".equals(get(key))) return Stats.toDouble(get(key));
    return null;
  }
  public Long  getLong(String... keys)
  {
    if (Tools.isSet(keys))
      for (String key : keys)
        if (get(key)!=null) return Stats.toLong(get(key));
    return null;
  }
  public Float  getFloat(String... keys)
  {
    if (Tools.isSet(keys))
      for (String key : keys)
        if (get(key)!=null) return Stats.toFloat(get(key));
    return null;
  }
  public Integer  getInt(String... keys)
  {
    if (Tools.isSet(keys))
      for (String key : keys)
        if (get(key)!=null) return Stats.toInt(get(key));
    return null;
  }
//  public Float   getFloat( String key) { return Stats.toFloat( get(key)); }
//  public Integer getInt(   String key) { return new Integer(   get(key)); }
  // return the first non-null value
  public String getNotNull(String... tags)
  {
    if (Tools.isSet(tags))
      for (String tag : tags)
        if (mCol!=null && tag!=null && mCol.get(tag)!=null) return mCol.get(tag);

    return null;
  }
  public String getStr(String... keys)
  {
    if (mCol != null && keys != null)
      for (String key : keys)
        if (get(key)!=null) return mCol.get(key);

    return null;
  }

  public String get(String key)
  {
    String value = null;
    if (mCol != null && key != null)
        value = mCol.get(key);

    return value;
  }
}
