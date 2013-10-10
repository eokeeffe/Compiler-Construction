package src;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ImmutableSetBuilder<T> {

  private Set<T> baseSet;

  private ImmutableSetBuilder() {
    baseSet = new HashSet<T>();
  }

  public static <T> ImmutableSetBuilder<T> newInstance() {
    return new ImmutableSetBuilder<T>();
  }

  public ImmutableSetBuilder<T> add(T element) {
    baseSet.add(element);
    return this;
  }

  public ImmutableSetBuilder<T> addAll(Collection<? extends T> elements) {
    baseSet.addAll(elements);
    return this;
  }

  public Set<T> build() {
    return Collections.unmodifiableSet(baseSet);
  }
}
