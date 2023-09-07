package gr.cite.intelcomp.stiviewer.model.builder.indicatorelastic;

import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.convention.ConventionService;
import gr.cite.intelcomp.stiviewer.elastic.data.indicator.AltTextEntity;
import gr.cite.intelcomp.stiviewer.model.builder.BaseBuilder;
import gr.cite.intelcomp.stiviewer.model.elasticindicator.AltText;
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
public class AltTextBuilder extends BaseBuilder<AltText, AltTextEntity> {

    @Autowired
    public AltTextBuilder(ConventionService conventionService) {
        super(conventionService, new LoggerService(LoggerFactory.getLogger(AltTextBuilder.class)));
    }

    public AltTextBuilder authorize(EnumSet<AuthorizationFlags> values) {
        return this;
    }

    @Override
    public List<AltText> build(FieldSet fields, List<AltTextEntity> data) throws MyApplicationException {
        this.logger.debug("building for {} items requesting {} fields", Optional.ofNullable(data).map(List::size).orElse(0), Optional.ofNullable(fields).map(FieldSet::getFields).map(Set::size).orElse(0));
        this.logger.trace(new DataLogEntry("requested fields", fields));
        if (fields == null || fields.isEmpty())
            return new ArrayList<>();

        List<AltText> altTexts = new LinkedList<>();

        if (data == null)
            return altTexts;
        for (AltTextEntity d : data) {
            AltText m = new AltText();
            if (fields.hasField(this.asIndexer(AltText._text)))
                m.setText(d.getText());
            if (fields.hasField(this.asIndexer(AltText._lang)))
                m.setLang(d.getLang());
            altTexts.add(m);
        }

        return altTexts;
    }
}
