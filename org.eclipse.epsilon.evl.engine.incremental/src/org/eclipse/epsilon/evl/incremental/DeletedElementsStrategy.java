package org.eclipse.epsilon.evl.incremental;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.epsilon.base.incremental.TraceReexecution;
import org.eclipse.epsilon.base.incremental.models.IIncrementalModel;
import org.eclipse.epsilon.base.incremental.trace.IModelElementTrace;
import org.eclipse.epsilon.base.incremental.trace.IModelTraceRepository;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.evl.incremental.execute.context.IIncrementalEvlContext;
import org.eclipse.epsilon.evl.incremental.trace.IEvlModuleTraceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IncrementalEvlExecutionStrategy to deal with deleted elements.
 * 
 * 
 * @author Goblin
 *
 */
public class DeletedElementsStrategy implements IncrementalEvlExecutionStrategy {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(SimpleStrategy.class);
	
	private final Collection<IModelElementTrace> deleted;
	private final IIncrementalModel im;

	public DeletedElementsStrategy(
		Collection<IModelElementTrace> deleted,
		IIncrementalModel im) {
		super();
		this.deleted = deleted;
		this.im = im;
	}

	@Override
	public void execute(IEvlModuleTraceRepository moduleRepo,
			IModelTraceRepository modelTraceRepo,
			IIncrementalEvlContext context,
			IncrementalEvlModule evlModule) throws EolRuntimeException {
		Set<TraceReexecution> traces = new HashSet<>();
		for (IModelElementTrace met : deleted) {
			traces.addAll(moduleRepo.findIndirectExecutionTraces(
					evlModule.getChksum(),
					met.getUri(),
					im.getAllTypeNamesOf(met)));
		}
		for (TraceReexecution t : traces) {
			try {
				t.reexecute(context, evlModule);
			} catch (EolRuntimeException e) {
				logger.error("Error reexecuting trace", e);
			}
		}

	}

}
