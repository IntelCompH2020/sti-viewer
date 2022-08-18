package gr.cite.user.model.builder.utils;

/**
 * Functional Interface for mapping individual fields of two Classes
 *
 *  <p>S - Source Class from where we get the value</p>
 *  <p>T - Target Class in which the value will be written to</p>
 * */
@FunctionalInterface
public interface MappingFunction<S, T> {

    void map(S source, T target);
}
