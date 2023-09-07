package gr.cite.intelcomp.stiviewer.eventscheduler.processing.datagroupbuild;

import gr.cite.intelcomp.stiviewer.common.enums.DataGroupRequestStatus;
import gr.cite.intelcomp.stiviewer.data.DataGroupRequestEntity;
import gr.cite.intelcomp.stiviewer.integrationevent.inbox.ConsistencyHandler;
import gr.cite.intelcomp.stiviewer.query.DataGroupRequestQuery;
import gr.cite.tools.data.query.QueryFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateDataGroupConsistencyHandler implements ConsistencyHandler<CreateDataGroupConsistencyPredicates> {

    private final QueryFactory queryFactory;

    public CreateDataGroupConsistencyHandler(QueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean isConsistent(CreateDataGroupConsistencyPredicates consistencyPredicates) {
        if (consistencyPredicates.getGroupHash() == null)
            return Boolean.FALSE;
        long count = this.queryFactory.query(DataGroupRequestQuery.class).groupHashes(consistencyPredicates.getGroupHash()).ids(consistencyPredicates.getRequestId()).count();
        if (count == 0)
            return Boolean.FALSE;

        DataGroupRequestEntity item = this.queryFactory.query(DataGroupRequestQuery.class).groupHashes(consistencyPredicates.getGroupHash()).ids(consistencyPredicates.getRequestId()).first();

        return item.getStatus() == DataGroupRequestStatus.PENDING;
    }
}
