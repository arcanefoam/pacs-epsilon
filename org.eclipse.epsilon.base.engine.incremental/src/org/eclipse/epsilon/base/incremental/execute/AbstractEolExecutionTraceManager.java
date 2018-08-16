package org.eclipse.epsilon.base.incremental.execute;

import java.time.LocalDate;

import org.eclipse.epsilon.base.incremental.IBaseFactory;
import org.eclipse.epsilon.base.incremental.trace.IModelTraceRepository;
import org.eclipse.epsilon.base.incremental.trace.IModuleExecutionTrace;
import org.eclipse.epsilon.base.incremental.trace.IModuleExecutionTraceRepository;

/**
 * A base implementation of the IEolExecutionTraceManager.
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
public abstract class AbstractEolExecutionTraceManager<T extends IModuleExecutionTrace, R extends IModuleExecutionTraceRepository<T>, F extends IBaseFactory>
		implements IExecutionTraceManager<T, R, F> {

	/** A flag to signal parallel persistence */
	protected boolean inParallel;

	/**
	 * The maximum number of elements in the repositories before a
	 * {@link #persistTraceInformation()} is triggered.
	 */
	protected int flushSize;

	/**
	 * The periodic time (in milliseconds) for a {@link #persistTraceInformation()}
	 * to be triggered.
	 */
	protected float timeOut;

	/** Time of last flush */
	protected LocalDate lastFlush;

	protected final R executionTraceRepository;

	protected final IModelTraceRepository modelTraceRepository;

	protected AbstractEolExecutionTraceManager(R executionTraceRepository, IModelTraceRepository modelTraceRepository) {
		this.lastFlush = LocalDate.now();
		this.executionTraceRepository = executionTraceRepository;
		this.modelTraceRepository = modelTraceRepository;
	}

	@Override
	public void setParallelPersist(boolean inParallel) {
		this.inParallel = inParallel;
	}

	@Override
	public void setFlushQueueSize(int size) {
		this.flushSize = size;
	}

	@Override
	public void setFlushTimeout(float period) {
		this.timeOut = period;
	}

	@Override
	public R getExecutionTraceRepository() {
		return this.executionTraceRepository;
	}

	@Override
	public IModelTraceRepository getModelTraceRepository() {
		return modelTraceRepository;
	}

}
