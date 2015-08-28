package org.eclipse.epsilon.evl.incremental;

import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.eol.dom.ExecutableBlock;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dom.ConstraintContext;
import org.eclipse.epsilon.evl.dom.Fix;
import org.eclipse.epsilon.evl.execute.EvlOperationFactory;
import org.eclipse.epsilon.evl.incremental.dom.TraceConstraint;
import org.eclipse.epsilon.evl.parse.EvlParser;


public class TraceEvlModule extends EvlModule {
	
	@Override
	public Object execute() throws EolRuntimeException {
		if (this.getContext().hasTrace()) {
			return null;
		}
		
		
		prepareContext(context);
		context.setOperationFactory(new EvlOperationFactory());
		context.getFrameStack().put(Variable.createReadOnlyVariable("thisModule", this));
		
		// Perform evaluation
//		((OrientTraceGraph)this.getContext().getTrace()).getBaseGraph().declareIntent(new OIntentMassiveInsert());
		execute(getPre(), context);
		
		for (ConstraintContext conCtx : getConstraintContexts()) { 
			conCtx.checkAll(context);	
		}		
		if (fixer != null) fixer.fix(this);
		
		execute(getPost(), context);
		
		this.getContext().getTrace().commit();
//		((OrientTraceGraph)this.getContext().getTrace()).getBaseGraph().declareIntent(null);
		this.getContext().setHasTrace(true);
		
		return null;
	} 
	
	@Override
	public AST adapt(AST cst, AST parentAst) {
		switch (cst.getType()) {
			case EvlParser.FIX: return new Fix();
			case EvlParser.DO: return new ExecutableBlock<Void>(Void.class);
			case EvlParser.TITLE: return new ExecutableBlock<String>(String.class);
			case EvlParser.MESSAGE: return new ExecutableBlock<String>(String.class);
			case EvlParser.CHECK: return new ExecutableBlock<Boolean>(Boolean.class);
			case EvlParser.GUARD: return new ExecutableBlock<Boolean>(Boolean.class);
			
			// Modified to return the appropriate subclasses of Constraint
			case EvlParser.CONSTRAINT: return new TraceConstraint();
			
			case EvlParser.CRITIQUE: return new TraceConstraint();
			case EvlParser.CONTEXT: return new ConstraintContext();
		}
		return super.adapt(cst, parentAst);
	}
	
	@Override
	public TraceEvlContext getContext() {
		return (TraceEvlContext) super.getContext();
	}
	
	public void setContext(TraceEvlContext context) {
		super.setContext(context);
	}
	
	@Override
	public void reset() {
		super.reset();
		context.dispose();
		context = new TraceEvlContext();
	}
	
}
