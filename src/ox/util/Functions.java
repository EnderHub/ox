package ox.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.size;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import ox.XList;

public final class Functions {

  public static <A, B> XList<B> map(A[] array, Function<A, B> function) {
    return map(Arrays.asList(array), function);
  }

  public static <A, B> XList<B> map(Iterable<A> input, Function<A, B> function) {
    checkNotNull(input, "input");
    checkNotNull(function, "function");

    XList<B> ret = XList.createWithCapacity(size(input));
    for (A element : input) {
      ret.add(function.apply(element));
    }
    return ret;
  }

  public static <A, B> Set<B> toSet(Iterable<A> input, Function<A, B> function) {
    checkNotNull(input, "input");
    checkNotNull(function, "function");

    Set<B> ret = new LinkedHashSet<>(size(input));
    for (A element : input) {
      ret.add(function.apply(element));
    }
    return ret;
  }

  public static <V> Iterable<V> reverse(Iterable<V> iter) {
    if (iter instanceof List) {
      return Lists.reverse((List<V>) iter);
    }
    return Lists.reverse(ImmutableList.copyOf(iter));
  }

  public static <K, V> Map<K, V> index(Iterable<V> input, Function<V, K> function) {
    return Maps.uniqueIndex(input, function::apply);
  }

  public static <K, V> Map<K, V> indexAllowNulls(Iterable<V> input, Function<V, K> function) {
    Map<K, V> ret = Maps.newHashMap();
    for (V v : input) {
      ret.put(function.apply(v), v);
    }
    return ret;
  }

  /**
   * Unlike Multimaps.index, this allows 'null' keys and values.
   */
  public static <K, V> ListMultimap<K, V> indexMultimap(Iterable<V> input, Function<? super V, K> function) {
    ListMultimap<K, V> ret = LinkedListMultimap.create();
    for (V v : input) {
      ret.put(function.apply(v), v);
    }
    return ret;
  }

  public static <K, V, T> Multimap<K, V> buildMultimap(Iterable<T> input, Function<? super T, K> keyFunction,
      Function<? super T, V> valueFunction) {
    Multimap<K, V> ret = LinkedListMultimap.create();
    for (T t : input) {
      ret.put(keyFunction.apply(t), valueFunction.apply(t));
    }
    return ret;
  }

  public static <K1, K2, V1, V2> Multimap<K2, V2> transformMultimap(Multimap<K1, V1> multimap,
      Function<? super K1, K2> keyFunction, Function<? super V1, V2> valueFunction) {
    Multimap<K2, V2> ret = LinkedListMultimap.create();
    multimap.forEach((k, v) -> {
      ret.put(keyFunction.apply(k), valueFunction.apply(v));
    });
    return ret;
  }

  public static <T> XList<T> filter(Iterable<T> input, Predicate<? super T> filter) {
    checkNotNull(input, "input");
    checkNotNull(filter, "filter");

    XList<T> ret = XList.createWithCapacity(size(input));
    for (T t : input) {
      if (filter.test(t)) {
        ret.add(t);
      }
    }
    return ret;
  }

  @SuppressWarnings("unchecked")
  public static <T, F extends T> XList<F> filter(Iterable<T> input, Class<F> classFilter) {
    checkNotNull(classFilter);
    return (XList<F>) filter(input, t -> t != null && classFilter.isAssignableFrom(t.getClass()));
  }

  public static double sum(Iterable<? extends Number> input) {
    double ret = 0;
    for (Number n : input) {
      ret += n.doubleValue();
    }
    return ret;
  }

  public static <T> double sum(Iterable<T> input, Function<T, Number> function) {
    double ret = 0;
    for (T t : input) {
      Number n = function.apply(t);
      if (n != null) {
        ret += n.doubleValue();
      }
    }
    return ret;
  }

  public static <T extends Comparable<? super T>> T min(Iterable<T> iter) {
    T ret = null;
    for (T t : iter) {
      if (ret == null || t.compareTo(ret) < 0) {
        ret = t;
      }
    }
    return ret;
  }

  public static <T> Consumer<T> emptyConsumer() {
    return t -> {
    };
  }

  public static Runnable emptyRunnable() {
    return () -> {
    };
  };

  public static <T> Predicate<T> not(Predicate<T> t) {
    return t.negate();
  }

  /**
   * Iterates through both iterables at the same time, calling the consumer for each pair of elements.
   * 
   * This will iterate until either one of the iterables runs out of elements.
   */
  public static <A, B> void splice(Iterable<A> iterA, Iterable<B> iterB, BiConsumer<A, B> consumer) {
    Iterator<A> a = iterA.iterator();
    Iterator<B> b = iterB.iterator();

    while (a.hasNext() && b.hasNext()) {
      consumer.accept(a.next(), b.next());
    }
  }

}
