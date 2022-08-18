package gr.cite.notification.model.mapper;

import gr.cite.tools.fieldset.FieldSet;

public interface Mapper<S, T> {

    T map(S source, FieldSet fieldSet);
}
