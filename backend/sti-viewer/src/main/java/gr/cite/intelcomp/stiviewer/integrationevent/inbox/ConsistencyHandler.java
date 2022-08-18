package gr.cite.intelcomp.stiviewer.integrationevent.inbox;

public interface ConsistencyHandler<T extends ConsistencyPredicates> {
	Boolean isConsistent(T consistencyPredicates);
}
