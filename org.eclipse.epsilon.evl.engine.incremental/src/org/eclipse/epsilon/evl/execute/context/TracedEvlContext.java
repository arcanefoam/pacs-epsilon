/*******************************************************************************
 * Copyright (c) 2017 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.evl.execute.context;


import org.eclipse.epsilon.evl.execute.IEvlExecutionTraceManager;
import org.eclipse.epsilon.evl.incremental.trace.IEvlModuleExecution;

/**
 * An EVL Context that keeps a reference to the traceManager and current EVL Execution Trace.
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
public class TracedEvlContext extends EvlContext {
	
	/** The trace manager. */
	private IEvlExecutionTraceManager<IEvlModuleExecution> traceManager;
	
	/** The evl execution. */
	private IEvlModuleExecution evlExecution;
	
	/**
	 * Gets the trace manager.
	 *
	 * @return the trace manager
	 */
	public IEvlExecutionTraceManager<IEvlModuleExecution> getTraceManager() {
		return traceManager;
	}
	
	/**
	 * Sets the trace Manager.
	 *
	 * @param traceManager the new unit of work
	 */
	public void setTraceManager(IEvlExecutionTraceManager<IEvlModuleExecution> traceManager) {
		this.traceManager = traceManager;
	}
	
	/**
	 * Gets the evl execution.
	 *
	 * @return the evl execution
	 */
	public IEvlModuleExecution getEvlExecution() {
		return evlExecution;
	}
	
	/**
	 * Sets the evl execution.
	 *
	 * @param evlExecution the new evl execution
	 */
	public void setEvlExecution(IEvlModuleExecution evlExecution) {
		this.evlExecution = evlExecution;
	}

}
