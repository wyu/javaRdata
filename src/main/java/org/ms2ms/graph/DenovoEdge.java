package org.ms2ms.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.ms2ms.utils.Tools;

/**
 * Created by yuw on 9/14/16.
 */
public class DenovoEdge extends DefaultWeightedEdge
{
  private Double mSource, mTarget;
  private String mLabel;
  private static final long serialVersionUID = 3258408452177934555L;

  public DenovoEdge(Double src, Double tgt, String label)
  {
    mSource=src; mTarget=tgt; mLabel=label;
  }
  public String getLabel() { return mLabel; }
  /**
   * Retrieves the source of this edge. This is protected, for use by
   * subclasses only (e.g. for implementing toString).
   *
   * @return source of this edge
   */
  protected Double getSource()
  {
    return mSource;
  }

  /**
   * Retrieves the target of this edge. This is protected, for use by
   * subclasses only (e.g. for implementing toString).
   *
   * @return target of this edge
   */
  protected Double getTarget()
  {
    return mTarget;
  }

  @Override public String toString() { return "(" + Tools.d2s(mSource, 3) + " : " + Tools.d2s(mTarget,3) + ")"+mLabel; }

}
