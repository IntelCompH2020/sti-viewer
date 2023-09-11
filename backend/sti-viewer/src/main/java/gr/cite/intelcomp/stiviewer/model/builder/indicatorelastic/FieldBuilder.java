package gr.cite.intelcomp.stiviewer.model.builder.indicatorelastic;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.FieldEntity;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.Field;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.DataLogEntry;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FieldBuilder extends BaseBuilder<Field, FieldEntity> {

    private final BuilderFactory builderFactory;

    private EnumSet<AuthorizationFlags> authorize = EnumSet.of(AuthorizationFlags.None);

    @Autowired
    public FieldBuilder(ConventionService conventionService, BuilderFactory builderFactory) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(FieldBuilder.class)));
        this.builderFactory = builderFactory;
    }

    public FieldBuilder authorize(EnumSet<AuthorizationFlags> values) {
        this.authorize = values;
        return this;
    }

    @Override
    public List<Field> build(FieldSet fields, List<FieldEntity> data) throws MyApplicationException {
        logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || fields.isEmpty())
            return new ArrayList<>();

        FieldSet valueRangeFields = fields.extractPrefixed(this.asPrefix(Field._valueRange));
        FieldSet operationsFields = fields.extractPrefixed(this.asPrefix(Field._operations));
        FieldSet altLabelsFields = fields.extractPrefixed(this.asPrefix(Field._altLabels));
        FieldSet altDescriptionsFields = fields.extractPrefixed(this.asPrefix(Field._altDescriptions));

        List<Field> fieldModels = new ArrayList<>(100);

        if (data == null)
            return fieldModels;
        for (FieldEntity d : data) {
            Field m = new Field();
            if (fields.hasField(this.asIndexer(Field._id)))
                m.setId(d.getId());
            if (fields.hasField(this.asIndexer(Field._code)))
                m.setCode(d.getCode());
            if (fields.hasField(this.asIndexer(Field._description)))
                m.setDescription(d.getDescription());
            if (fields.hasField(this.asIndexer(Field._label)))
                m.setLabel(d.getLabel());
            if (fields.hasField(this.asIndexer(Field._name)))
                m.setName(d.getName());
            if (fields.hasField(this.asIndexer(Field._subfieldOf)))
                m.setSubfieldOf(d.getSubfieldOf());
            if (fields.hasField(this.asIndexer(Field._typeId)))
                m.setTypeId(d.getTypeId());
            if (fields.hasField(this.asIndexer(Field._typeSemantics)))
                m.setTypeSemantics(d.getTypeSemantics());
            if (fields.hasField(this.asIndexer(Field._baseType)))
                m.setBaseType(d.getBaseType());
            if (fields.hasField(this.asIndexer(Field._useAs)))
                m.setUseAs(d.getUseAs());
            if (fields.hasField(this.asIndexer(Field._validation)))
                m.setValidation(d.getValidation().toString());
            if (fields.hasField(this.asIndexer(Field._valueField)))
                m.setValueField(d.getValueField());
            if (!valueRangeFields.isEmpty() && d.getValueRange() != null)
                m.setValueRange(this.builderFactory.builder(ValueRangeBuilder.class).authorize(this.authorize).build(valueRangeFields, d.getValueRange()));
            if (!operationsFields.isEmpty() && d.getOperations() != null)
                m.setOperations(this.builderFactory.builder(OperatorBuilder.class).authorize(this.authorize).build(operationsFields, d.getOperations()));
            if (!altLabelsFields.isEmpty() && d.getAltLabels() != null)
                m.setAltLabels(this.builderFactory.builder(AltTextBuilder.class).authorize(this.authorize).build(altLabelsFields, d.getAltLabels()));
            if (!altDescriptionsFields.isEmpty() && d.getAltDescriptions() != null)
                m.setAltDescriptions(this.builderFactory.builder(AltTextBuilder.class).authorize(this.authorize).build(altDescriptionsFields, d.getAltDescriptions()));

            fieldModels.add(m);
        }

        return fieldModels;
    }
}
