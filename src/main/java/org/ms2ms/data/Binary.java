package org.ms2ms.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * ** Copyright 2014-2015 ms2ms.org
 * <p/>
 * Description:
 * <p/>
 * Author: wyu
 * Date:   12/9/14
 */
public interface Binary
{
  public void write(DataOutput ds) throws IOException;
  public void read( DataInput ds) throws IOException;
}
