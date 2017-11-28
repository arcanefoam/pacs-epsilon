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
package org.eclipse.epsilon.evl.dom;

import java.util.Iterator;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.incremental.EolIncrementalExecutionException;
import org.eclipse.epsilon.eol.incremental.dom.TracedExecutableBlock;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;
import org.eclipse.epsilon.evl.execute.context.IEvlContext;
import org.eclipse.epsilon.evl.execute.context.TracedEvlContext;
import org.eclipse.epsilon.evl.incremental.trace.ICheckTrace;
import org.eclipse.epsilon.evl.incremental.trace.IGuardTrace;
import org.eclipse.epsilon.evl.incremental.trace.IInvariantTrace;
import org.eclipse.epsilon.evl.incremental.trace.IMessageTrace;
import org.eclipse.epsilon.evl.trace.ConstraintTrace;

/**
 * Subclass of {@link Constraint} for use with incremental evaluation and
 * traces. This bypasses the default checking of the {@link ConstraintTrace}'s.
 * 
 * @author Jonathan Co
 *
 */
// FIXME this could be merged with the Constraint and use a flag
public class TracedConstraint extends Constraint {
	
	private IInvariantTrace trace;

	public IInvariantTrace getTrace() {
		return trace;
	}

	public void setTrace(IInvariantTrace trace) {
		this.trace = trace;
	}

	public boolean appliesTo(Object object, IEvlContext context, final boolean checkType) throws EolRuntimeException{
		if (checkType && !constraintContext.getAllOfSourceKind(context).contains(object)) return false;
		((TracedEvlContext)context).getTraceManager().getSatisfiesListener().aboutToExecute(this, context);
		Boolean result = true;
		if (guardBlock != null) {
			result = guardBlock.execute(context, Variable.createReadOnlyVariable("self", object));
		}
		((TracedEvlContext)context).getTraceManager().getSatisfiesListener().finishedExecuting(this, result, context);		
		return result;
	}
	

	@Override
	public boolean check(Object self, IEvlContext context, boolean checkType) throws EolRuntimeException {
		
		// First look in the cache
		if (context.getConstraintTrace().isChecked(this,self)){
			return context.getConstraintTrace().isSatisfied(this,self);
		}
		// Return immediately if constraint does not apply
		if (!appliesTo(self, context, checkType)) {
			trace.setResult(false);
			return false;
		}
		
		// FIXME Why remove?
		//removeOldUnsatisfiedConstraint(self, context);
		
		final UnsatisfiedConstraint unsatisfiedConstraint = preprocessCheck(self, context);
		//((TracedExecutableBlock<?>)checkBlock).startRecording();
		Boolean result = checkBlock.execute(context, false);
		//((TracedExecutableBlock<?>)checkBlock).stopRecording();
		boolean postResult = postprocessCheck(self, context, unsatisfiedConstraint, result);
		trace.setResult(postResult);
		return postResult;
		
	}
	
	/**
	 * Create a new guard trace for the constraint
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
				((TracedExecutableBlock<?>) guardBlock).setTrace(guard);
				return true;
			} catch (EolIncrementalExecutionException e) {
				throw new EolIncrementalExecutionException("Can't create GuardTrace for Invariant " + getName() + ".");	
			}
		}
		return false;
	}
	
	/**
	 * Create a new check trace for the constraint
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
				((TracedExecutableBlock<?>) checkBlock).setTrace(check);
				return true;
			} catch (EolIncrementalExecutionException e) {
				throw new EolIncrementalExecutionException("Can't create GuardTrace for Invariant " + getName() + ".");	
			}
		}
		return false;
	}
	
	/**
	 * Create a new message trace for the constraint
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
				((TracedExecutableBlock<?>) messageBlock).setTrace(message);
				return true;
			} catch (EolIncrementalExecutionException e) {
				throw new EolIncrementalExecutionException("Can't create MessageTrace for Invariant " + getName() + ".");	
			}
		}
		return false;
	}
	
	
	private void removeOldUnsatisfiedConstraint(Object self, IEvlContext context) {
		Iterator<UnsatisfiedConstraint> it = context.getUnsatisfiedConstraints().iterator();
		while(it.hasNext()) {
			UnsatisfiedConstraint current = it.next();
			if(current.getConstraint().equals(this) && current.getInstance().equals(self)) {
				it.remove();
			}
		}
	}
}
