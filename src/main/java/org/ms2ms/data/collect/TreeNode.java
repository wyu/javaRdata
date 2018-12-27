package org.ms2ms.data.collect;

import java.util.HashMap;
import java.util.Map;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   1/17/15
 */
public class TreeNode
{
  private String mName;
  private TreeNode mParent;
  private Object mData;
  private Map<String, TreeNode> mChildren;

  public TreeNode(String s) { mName=s; }

  public String getName() { return mName; }
  public TreeNode getChild(String s) { return mChildren!=null?mChildren.get(s):null; }
  public TreeNode getParent() { return mParent; }
  public Object getData() { return mData; }

  public TreeNode setName(  String   s) { mName=s;   return this; }
  public TreeNode setParent(TreeNode s) { mParent=s; return this; }
  public TreeNode setData(  Object   s) { mData=s;   return this; }

  public TreeNode addChild(TreeNode s)
  {
    if (s!=null)
    {
      if (mChildren==null) mChildren = new HashMap<>();
      if (mChildren.get(s.getName())==null) mChildren.put(s.getName(), s.setParent(this));
    }

    return this;
  }
}
