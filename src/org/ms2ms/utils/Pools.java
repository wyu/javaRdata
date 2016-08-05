package org.ms2ms.utils;

import com.google.common.base.Supplier;
import com.google.common.collect.*;

import java.util.Collection;
import java.util.List;

/** Misc methods about the collection and related utilities
 * Created by yuw on 2/23/16.
 */
public class Pools
{
  // http://stackoverflow.com/questions/14975681/how-is-arraylistmultimap-different-from-linkedlistmultimap
  public static void newMinMaxQueueMultimap()
  {
//    ListMultimap<String, Integer> treeListMultimap =
//        MultimapBuilder.linkedHashKeys().arrayListValues().build();


  }
  /**
   * Creates {@link ListMultimap} preserving insertion order of keys and values
   * (it's backed by {@link LinkedHashMap} and {@link ArrayList}).
   */
  public static <K, V> ListMultimap<K, V> newLinkedArrayListMultimap() {
    return Multimaps.newListMultimap(
        Maps.<K, Collection<V>>newLinkedHashMap(),
        new Supplier<List<V>>() {
          @Override
          public List<V> get() {
            return Lists.newArrayList();
          }
        });
  }
}
