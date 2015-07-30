package org.eclipse.epsilon.evl.incremental.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintSeverity;
import org.eclipse.emf.validation.model.EvaluationMode;
import org.eclipse.emf.validation.model.IModelConstraint;
import org.eclipse.emf.validation.service.AbstractConstraintDescriptor;
import org.eclipse.emf.validation.service.IConstraintDescriptor;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.evl.dom.Constraint;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.incremental.TraceEvlContext;

public class EvlEmfConstraint extends AbstractConstraintDescriptor implements
		IConstraintDescriptor, IModelConstraint {
	
	private final Constraint constraint;
	private final ConstraintContext constraintContext;
	private final int statusCode;
	
	private TraceEvlContext evlContext;
	
	public EvlEmfConstraint(Constraint constraint, int statusCode) {
		super();
		this.constraint = constraint;
		this.constraintContext = constraint.getConstraintContext();
		this.statusCode = statusCode;
	}

	@Override
	public IStatus validate(IValidationContext ctx) {
		if (this.evlContext == null) {
			return ctx.createFailureStatus("");
		}
		try {
			boolean check = this.constraint.check(ctx.getTarget(), this.evlContext);
			if (check) {
				return ctx.createSuccessStatus();
			} else {
				return ctx.createFailureStatus("");
			}
 		} catch (EolRuntimeException e) {
			return ctx.createFailureStatus("");
		}
	}
	
	public void setCurrentEvlContext(final TraceEvlContext context) {
		this.evlContext = context;
	}
	
	public Constraint getConstraint() {
		return this.constraint;
	}

	public ConstraintContext getConstraintContext() {
		return this.constraintContext;
	}

	@Override
	public String getName() {
		return this.constraint.getName();
	}

	@Override
	public String getId() {
		// TODO: add category to this
		return this.constraintContext.getName() + "." + this.constraint.getName();
	}

	@Override
	public String getPluginId() {
		// FIXME
		return "";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public ConstraintSeverity getSeverity() {
		// FIXME
		return ConstraintSeverity.ERROR;
	}

	@Override
	public int getStatusCode() {
		return this.statusCode;
	}

	@Override
	public EvaluationMode<?> getEvaluationMode() {
		return EvaluationMode.LIVE;
	}

	@Override
	public boolean targetsTypeOf(EObject eObject) {
		if (this.evlContext == null) {
			return false;
		}
		try {
			return this.constraint.appliesTo(eObject, this.evlContext);
		} catch (EolRuntimeException e) {
			return false;
		}
	}

	@Override
	public boolean targetsEvent(Notification notification) {
		return true;
	}

	@Override
	public String getMessagePattern() {
		return String.format("Constraint %s violated on {0}", getName()); //$NON-NLS-1$
	}

	@Override
	public String getBody() {
		return null;
	}

}
