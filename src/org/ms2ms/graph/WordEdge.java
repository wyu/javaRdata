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
public class WordEdge implements Cloneable
{
  public static Rel ISA=Rel.isA, HASA=Rel.hasA;
  private enum Rel { isA, hasA };
  private Rel mRelationship;

  public WordEdge()         { super(); }
  public WordEdge(String s) { super(); mRelationship = Rel.valueOf(s); }
  public WordEdge(Rel s)    { super(); mRelationship = s; }

  public Rel getRelationship() { return mRelationship; }
  public WordEdge setRelationship(String s) { mRelationship=Rel.valueOf(s); return this; }
  public WordEdge setRelationship(Rel    s) { mRelationship=s; return this; }

  @Override
  public WordEdge clone()
  {
    WordEdge cloned = null;
    try
    {
      cloned = (WordEdge)super.clone();

      if (mRelationship!=null) cloned.mRelationship=mRelationship;
    }
    catch (CloneNotSupportedException e) {}

    return cloned;
  }
  @Override
  public boolean equals(Object s)
  {
    return s!=null && s instanceof WordEdge? Tools.equals(((WordEdge) s).getRelationship(), getRelationship()) : false;
  }
  @Override
  public int hashCode()
  {
    return mRelationship!=null?mRelationship.hashCode():0;
  }
}
