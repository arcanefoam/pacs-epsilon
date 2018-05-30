/*******************************************************************************
 * Copyright (c) 2016 University of York
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	   Jonathan Co   - Initial API and implementation
 *     Horacio Hoyos - Decoupling and abstraction
 *******************************************************************************/
package org.eclipse.epsilon.evl.incremental.dom;

import java.util.Iterator;

import org.eclipse.epsilon.base.incremental.dom.TracedExecutableBlock;
import org.eclipse.epsilon.base.incremental.dom.TracedModuleElement;
import org.eclipse.epsilon.base.incremental.exceptions.EolIncrementalExecutionException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.evl.IncrementalEvlModule;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;
import org.eclipse.epsilon.evl.execute.context.IEvlContext;
import org.eclipse.epsilon.evl.incremental.execute.context.TracedEvlContext;
import org.eclipse.epsilon.evl.incremental.trace.ICheckTrace;
import org.eclipse.epsilon.evl.incremental.trace.IGuardTrace;
import org.eclipse.epsilon.evl.incremental.trace.IInvariantTrace;
import org.eclipse.epsilon.evl.incremental.trace.IMessageTrace;
import org.eclipse.epsilon.evl.trace.ConstraintTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Subclass of {@link Constraint} for use with incremental evaluation and
 * traces. This bypasses the default checking of the {@link ConstraintTrace}'s.
 * 
 * @author Jonathan Co
 *
 */
// FIXME this could be merged with the Constraint and use a flag
public class TracedConstraint extends Constraint 
		implements TracedModuleElement<IInvariantTrace> {
	
	private static final Logger logger = LoggerFactory.getLogger(TracedConstraint.class);
	
	private IInvariantTrace trace;
	
	/**
	 * Flag to indicate that we are on live mode, i.e. listening to model changes
	 */
	private boolean listeningToChagnes = false;

	@Override
	public void setCurrentTrace(IInvariantTrace trace) {
		this.trace = trace;
	}

	@Override
	public IInvariantTrace getCurrentTrace() {
		return trace;
	}


	public boolean appliesTo(Object object, IEvlContext context, final boolean checkType) throws EolRuntimeException {
		if (checkType && !constraintContext.getAllOfSourceKind(context).contains(object)) return false;
		Boolean result = true;
		if (guardBlock != null) {
			TracedEvlContext tracedEvlContext = (TracedEvlContext)context;
			tracedEvlContext.getTraceManager().getSatisfiesListener().aboutToExecute(this, context);
			tracedEvlContext.getTraceManager().getPropertyAccessListener().aboutToExecute(guardBlock, context);
			tracedEvlContext.getTraceManager().getAllInstancesAccessListener().aboutToExecute(guardBlock, context);
			result = guardBlock.execute(context, Variable.createReadOnlyVariable("self", object));
			trace.guard().get().setResult(result);
			tracedEvlContext.getTraceManager().getSatisfiesListener().finishedExecuting(this, result, context);		
			tracedEvlContext.getTraceManager().getPropertyAccessListener().finishedExecuting(guardBlock, result, context);
			tracedEvlContext.getTraceManager().getAllInstancesAccessListener().finishedExecuting(guardBlock, result, context);
			return result;
		}
		return result;
	}
	

	@Override
	public boolean check(Object self, IEvlContext context, boolean checkType) throws EolRuntimeException {
		
		logger.info("Check {} for {}", getName(), self);
		// First look in the cache
		if (context.getConstraintTrace().isChecked(this,self)) {
			logger.debug("Result found in cache");
			return context.getConstraintTrace().isSatisfied(this,self);
		}
		// Return immediately if constraint does not apply
		if (!appliesTo(self, context, checkType)) {
			logger.debug("Does not apply");
			trace.setResult(false);
			return false;
		}
		
		final UnsatisfiedConstraint unsatisfiedConstraint = preprocessCheck(self, context);
		TracedEvlContext tracedEvlContext = (TracedEvlContext)context;
		tracedEvlContext.getTraceManager().getPropertyAccessListener().aboutToExecute(checkBlock, context);
		tracedEvlContext.getTraceManager().getAllInstancesAccessListener().aboutToExecute(checkBlock, context);
		Boolean result = checkBlock.execute(context, false);
		logger.info("Result: {}", result);
		tracedEvlContext.getTraceManager().getPropertyAccessListener().finishedExecuting(checkBlock, result, context);
		tracedEvlContext.getTraceManager().getAllInstancesAccessListener().finishedExecuting(checkBlock, result, context);
		
		if (messageBlock != null) {
			tracedEvlContext.getTraceManager().getPropertyAccessListener().aboutToExecute(messageBlock, context);
			tracedEvlContext.getTraceManager().getAllInstancesAccessListener().aboutToExecute(messageBlock, context);
		}
		boolean postResult = postprocessCheck(self, context, unsatisfiedConstraint, result);
		if (messageBlock != null) {
			tracedEvlContext.getTraceManager().getPropertyAccessListener().finishedExecuting(messageBlock, postResult, context);
			tracedEvlContext.getTraceManager().getAllInstancesAccessListener().finishedExecuting(messageBlock, postResult, context);
		}
		boolean oldResult = trace.getResult();
		if (!oldResult && postResult) {
			logger.debug("Removing unsatisfied constraint");
			removeUnsatisfiedConstraint(context, self);
		}
		trace.setResult(postResult);
		return postResult;
		
	}
	
	/**
	 * @param self
	 * @param context
	 */
	@Override
	protected void addToCache(Object self, IEvlContext context) {
		if (!listeningToChagnes) {
			logger.debug("Adding result to cache");
			context.getConstraintTrace().addChecked(this, self, false);
		}
	}
	
	/**
	 * Create a new guard trace for the constraint
	 * @param evlExecution 
	 * @param tracedGuard
	 * @throws EolIncrementalExecutionException
	 */
	public boolean createGuardTrace() throws EolIncrementalExecutionException {
		if (guardBlock == null) {
			return false;
		}
		IGuardTrace guard = trace.guard().get();
		if (guard == null) {
			try {
				guard = trace.createGuardTrace();
				((TracedExecutableBlock<IGuardTrace, ?>) guardBlock).setCurrentTrace(guard);
				return true;
			} catch (EolIncrementalExecutionException e) {
				throw new EolIncrementalExecutionException("Can't create GuardTrace for Invariant " + getName() + ".");	
			}
		}
		return false;
	}
	
	/**
	 * Create a new check trace for the constraint
	 * @param evlExecution 
	 * @param tracedCheck
	 * @throws EolIncrementalExecutionException
	 */
	public boolean createCheckTrace() throws EolIncrementalExecutionException {
		if (checkBlock == null) {
			return false;
		}
		ICheckTrace check = trace.check().get();
		if (check == null) {
			try {
				check = trace.createCheckTrace();
				((TracedExecutableBlock<ICheckTrace, ?>) checkBlock).setCurrentTrace(check);
				return true;
			} catch (EolIncrementalExecutionException e) {
				throw new EolIncrementalExecutionException("Can't create GuardTrace for Invariant " + getName() + ".");	
			}
		}
		return false;
	}
	
	/**
	 * Create a new message trace for the constraint
	 * @param evlExecution 
	 * @param tracedMessage
	 * @throws EolIncrementalExecutionException
	 */
	public boolean createMessageTrace() throws EolIncrementalExecutionException {
		if (messageBlock == null) {
			return false;
		}
		IMessageTrace message = trace.message().get();
		if (message == null) {
			try {
				message = trace.createMessageTrace();
				((TracedExecutableBlock<IMessageTrace, ?>) messageBlock).setCurrentTrace(message);
				return true;
			} catch (EolIncrementalExecutionException e) {
				throw new EolIncrementalExecutionException("Can't create MessageTrace for Invariant " + getName() + ".");	
			}
		}
		return false;
	}
	
	public boolean isListeningToChagnes() {
		return listeningToChagnes;
	}

	public void setListeningToChagnes(boolean listeningToChagnes) {
		this.listeningToChagnes = listeningToChagnes;
	}
	
	private void removeUnsatisfiedConstraint(IEvlContext context, Object self) {
		Iterator<UnsatisfiedConstraint> it = context.getUnsatisfiedConstraints().iterator();
		while(it.hasNext()) {
			UnsatisfiedConstraint current = it.next();
			if(current.getConstraint().equals(this) && current.getInstance().equals(self)) {
				it.remove();
			}
		}
	}

}
