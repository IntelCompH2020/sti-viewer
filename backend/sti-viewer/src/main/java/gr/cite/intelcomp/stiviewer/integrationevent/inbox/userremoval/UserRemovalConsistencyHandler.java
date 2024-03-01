package gr.cite.intelcomp.stiviewer.integrationevent.inbox.userremoval;

import gr.cite.intelcomp.stiviewer.integrationevent.inbox.ConsistencyHandler;
import gr.cite.intelcomp.stiviewer.query.UserQuery;
import gr.cite.tools.data.query.QueryFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserRemovalConsistencyHandler implements ConsistencyHandler<UserRemovalConsistencyPredicates> {

	private final QueryFactory queryFactory;

	public UserRemovalConsistencyHandler(QueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Boolean isConsistent(UserRemovalConsistencyPredicates consistencyPredicates) {
		long count = this.queryFactory.query(UserQuery.class).ids(consistencyPredicates.getUserId()).count();
		if (count == 0) return false;
		return true;
	}
}
