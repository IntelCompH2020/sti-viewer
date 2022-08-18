package gr.cite.intelcomp.stiviewer.eventscheduler.processing;

public interface ConsistencyHandler<T extends ConsistencyPredicates> {
	Boolean isConsistent(T consistencyPredicates);
}
