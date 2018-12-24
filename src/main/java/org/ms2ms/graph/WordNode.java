package org.ms2ms.graph;

import org.ms2ms.utils.Strs;
import org.ms2ms.utils.Tools;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   1/15/15
 */
public class WordNode implements Cloneable
{
  private String mWord, mLabel="word";

  public WordNode()                  { super(); }
  public WordNode(String s)          { super(); mWord=s; }
  public WordNode(String l, String s){ super(); mWord=s; mLabel=l; }

  public String   getWord()          { return mWord; }
  public String   getLabel()         { return mLabel; }
  public WordNode setWord( String s) { mWord =s; return this; }
  public WordNode setLabel(String s) { mLabel=s; return this; }

  @Override
  public WordNode clone()
  {
    WordNode cloned = null;
    try
    {
      cloned = (WordNode)super.clone();

      if (mWord !=null) cloned.mWord = new String(mWord);
    }
    catch (CloneNotSupportedException e) {}

    return cloned;
  }
  @Override
  public boolean equals(Object s)
  {
    return s!=null && s instanceof WordNode?
        Strs.equals(((WordNode )s).getWord(), getWord()) && Strs.equals(((WordNode )s).getLabel(), getLabel()) : false;
  }
  @Override
  public int hashCode()
  {
    return mWord!=null?mWord.hashCode():0;
  }
}
