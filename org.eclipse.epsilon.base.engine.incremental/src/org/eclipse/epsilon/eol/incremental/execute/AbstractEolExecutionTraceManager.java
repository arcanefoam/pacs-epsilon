package org.eclipse.epsilon.eol.incremental.execute;

import java.time.LocalDate;

/**
 * A base implementation of the IEolExecutionTraceManager.
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
public abstract class AbstractEolExecutionTraceManager<T> implements IEolExecutionTraceManager<T> {

	/** A flag to signal parallel persistence */
	protected boolean inParallel;

	/** The maximum number of elements in the repositories before a {@link #persistTraceInformation()} is triggered. */
	protected int flushSize;

	/** The periodic time (in milliseconds) for a {@link #persistTraceInformation()} to be triggered. */
	protected float timeOut;

	/** Time of last flush */
	protected LocalDate lastFlush;
	
	protected AbstractEolExecutionTraceManager() {
		this.lastFlush = LocalDate.now();
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

}
