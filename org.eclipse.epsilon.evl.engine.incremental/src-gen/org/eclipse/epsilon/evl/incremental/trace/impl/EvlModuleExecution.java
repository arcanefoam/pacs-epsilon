 /*******************************************************************************
 * This file was automatically generated on: 2017-12-08.
 * Only modify protected regions indicated by "<!-- -->"
 *
 * Copyright (c) 2017 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 ******************************************************************************/
package org.eclipse.epsilon.evl.incremental.trace.impl;

import org.eclipse.epsilon.evl.incremental.trace.IEvlModuleExecution;
import java.util.NoSuchElementException;

import org.eclipse.epsilon.eol.incremental.EolIncrementalExecutionException;
import org.eclipse.epsilon.eol.incremental.trace.impl.TraceModelDuplicateRelation;
import org.eclipse.epsilon.eol.incremental.trace.IModelElementTrace;
import org.eclipse.epsilon.eol.incremental.trace.IModelTrace;
import org.eclipse.epsilon.eol.incremental.trace.IModuleExecutionHasExecutions;
import org.eclipse.epsilon.eol.incremental.trace.IModuleExecutionHasModel;
import org.eclipse.epsilon.eol.incremental.trace.IModuleExecutionHasModule;
import org.eclipse.epsilon.eol.incremental.trace.impl.ModelTrace;
import org.eclipse.epsilon.eol.incremental.trace.impl.ModuleExecutionHasExecutions;
import org.eclipse.epsilon.eol.incremental.trace.impl.ModuleExecutionHasModel;
import org.eclipse.epsilon.eol.incremental.trace.impl.ModuleExecutionHasModule;
import org.eclipse.epsilon.evl.incremental.trace.ICheckTrace;
import org.eclipse.epsilon.evl.incremental.trace.IContextTrace;
import org.eclipse.epsilon.evl.incremental.trace.IEvlModuleTrace;
import org.eclipse.epsilon.evl.incremental.trace.IGuardTrace;
import org.eclipse.epsilon.evl.incremental.trace.IGuardedElementTrace;
import org.eclipse.epsilon.evl.incremental.trace.IInvariantTrace;
import org.eclipse.epsilon.evl.incremental.trace.IMessageTrace;
import org.eclipse.epsilon.evl.incremental.trace.ISatisfiesTrace;
import org.eclipse.epsilon.evl.incremental.trace.impl.CheckTrace;
import org.eclipse.epsilon.evl.incremental.trace.impl.ContextTrace;
import org.eclipse.epsilon.evl.incremental.trace.impl.EvlModuleTrace;
import org.eclipse.epsilon.evl.incremental.trace.impl.GuardTrace;
import org.eclipse.epsilon.evl.incremental.trace.impl.InvariantTrace;
import org.eclipse.epsilon.evl.incremental.trace.impl.MessageTrace;
import org.eclipse.epsilon.evl.incremental.trace.impl.SatisfiesTrace;

/**
 * Implementation of IEvlModuleExecution. 
 */
public class EvlModuleExecution implements IEvlModuleExecution {

    /** The id */
    private Object id;

    /** The module relation */
    private final IModuleExecutionHasModule module;

    /** The model relation */
    private final IModuleExecutionHasModel model;

    /** The executions relation */
    private final IModuleExecutionHasExecutions executions;

    /**
     * Instantiates a new EvlModuleExecution. The EvlModuleExecution is uniquely identified by its
     * container and any attributes identified as indexes.
     */    
    public EvlModuleExecution() throws TraceModelDuplicateRelation {
        this.module = new ModuleExecutionHasModule(this);
        this.model = new ModuleExecutionHasModel(this);
        this.executions = new ModuleExecutionHasExecutions(this);
    }
    
    @Override
    public Object getId() {
        return id;
    }
    
    
    @Override
    public void setId(Object value) {
        this.id = value;
    }   
     
    @Override
    public IModuleExecutionHasModule module() {
        return module;
    }

    @Override
    public IModuleExecutionHasModel model() {
        return model;
    }

    @Override
    public IModuleExecutionHasExecutions executions() {
        return executions;
    }

    @Override
    public IEvlModuleTrace createEvlModuleTrace(String source) throws EolIncrementalExecutionException {
        IEvlModuleTrace evlModuleTrace = null;
        try {
            evlModuleTrace = new EvlModuleTrace(source, this);
        } catch (TraceModelDuplicateRelation e) {
            // Pass
        } finally {
    	    if (evlModuleTrace != null) {
    	        return evlModuleTrace;
    	    }
            evlModuleTrace = (EvlModuleTrace) this.module.get();
            if (evlModuleTrace  == null) {
                throw new EolIncrementalExecutionException("Error creating trace model element. Requested EvlModuleTrace was "
                        + "duplicate but previous one was not found.");
            }
        }
        return evlModuleTrace;
    }      
            
                  
    @Override
    public IModelTrace createModelTrace(String name) throws EolIncrementalExecutionException {
        IModelTrace modelTrace = null;
        try {
            modelTrace = new ModelTrace(name, this);
        } catch (TraceModelDuplicateRelation e) {
            // Pass
        } finally {
    	    if (modelTrace != null) {
    	        return modelTrace;
    	    }
            try {
                modelTrace = this.model.get().stream()
                    .filter(item -> item.getName().equals(name))
                    .findFirst()
                    .get();
            } catch (NoSuchElementException ex) {
                throw new EolIncrementalExecutionException("Error creating trace model element. Requested ModelTrace was "
                        + "duplicate but previous one was not found.");
            }
        }
        return modelTrace;
    }      
                  
    @Override
    public IContextTrace createContextTrace(String kind, IModelElementTrace context) throws EolIncrementalExecutionException {
        IContextTrace contextTrace = null;
        try {
            contextTrace = new ContextTrace(kind, context, this);
        } catch (TraceModelDuplicateRelation e) {
            // Pass
        } finally {
    	    if (contextTrace != null) {
    	        return contextTrace;
    	    }
            try {
                contextTrace = this.executions.get().stream()
                    .map(ContextTrace.class::cast)
                    .filter(item -> item.getKind().equals(kind))
                    .filter(item -> item.context().get().equals(context))
                    .findFirst()
                    .get();
            } catch (NoSuchElementException ex) {
                throw new EolIncrementalExecutionException("Error creating trace model element. Requested ContextTrace was "
                        + "duplicate but previous one was not found.");
            }
        }
        return contextTrace;
    }      
            
    @Override
    public IInvariantTrace createInvariantTrace(String name) throws EolIncrementalExecutionException {
        IInvariantTrace invariantTrace = null;
        try {
            invariantTrace = new InvariantTrace(name, this);
            
            this.constraints().create(invariantTrace);
        } catch (TraceModelDuplicateRelation e) {
            // Pass
        } finally {
    	    if (invariantTrace != null) {
    	        return invariantTrace;
    	    }
            try {
                invariantTrace = this.executions.get().stream()
                    .map(InvariantTrace.class::cast)
                    .filter(item -> item.getName().equals(name))
                    .findFirst()
                    .get();
            } catch (NoSuchElementException ex) {
                throw new EolIncrementalExecutionException("Error creating trace model element. Requested InvariantTrace was "
                        + "duplicate but previous one was not found.");
            }
        }
        return invariantTrace;
    }      
            
    @Override
    public IGuardTrace createGuardTrace() throws EolIncrementalExecutionException {
        IGuardTrace guardTrace = null;
        try {
            guardTrace = new GuardTrace(this);
            
            this.guard().create(guardTrace);
        } catch (TraceModelDuplicateRelation e) {
            // Pass
        } finally {
    	    if (guardTrace != null) {
    	        return guardTrace;
    	    }
            try {
                guardTrace = this.executions.get().stream()
                    .map(GuardTrace.class::cast)
                    .findFirst()
                    .get();
            } catch (NoSuchElementException ex) {
                throw new EolIncrementalExecutionException("Error creating trace model element. Requested GuardTrace was "
                        + "duplicate but previous one was not found.");
            }
        }
        return guardTrace;
    }      
            
    @Override
    public ICheckTrace createCheckTrace() throws EolIncrementalExecutionException {
        ICheckTrace checkTrace = null;
        try {
            checkTrace = new CheckTrace(this);
            
            this.check().create(checkTrace);
        } catch (TraceModelDuplicateRelation e) {
            // Pass
        } finally {
    	    if (checkTrace != null) {
    	        return checkTrace;
    	    }
            try {
                checkTrace = this.executions.get().stream()
                    .map(CheckTrace.class::cast)
                    .findFirst()
                    .get();
            } catch (NoSuchElementException ex) {
                throw new EolIncrementalExecutionException("Error creating trace model element. Requested CheckTrace was "
                        + "duplicate but previous one was not found.");
            }
        }
        return checkTrace;
    }      
            
    @Override
    public IMessageTrace createMessageTrace() throws EolIncrementalExecutionException {
        IMessageTrace messageTrace = null;
        try {
            messageTrace = new MessageTrace(this);
            
            this.message().create(messageTrace);
        } catch (TraceModelDuplicateRelation e) {
            // Pass
        } finally {
    	    if (messageTrace != null) {
    	        return messageTrace;
    	    }
            try {
                messageTrace = this.executions.get().stream()
                    .map(MessageTrace.class::cast)
                    .findFirst()
                    .get();
            } catch (NoSuchElementException ex) {
                throw new EolIncrementalExecutionException("Error creating trace model element. Requested MessageTrace was "
                        + "duplicate but previous one was not found.");
            }
        }
        return messageTrace;
    }      
            
    @Override
    public ISatisfiesTrace createSatisfiesTrace() throws EolIncrementalExecutionException {
        ISatisfiesTrace satisfiesTrace = null;
        try {
            satisfiesTrace = new SatisfiesTrace(this);
            
            this.satisfies().create(satisfiesTrace);
        } catch (TraceModelDuplicateRelation e) {
            // Pass
        } finally {
    	    if (satisfiesTrace != null) {
    	        return satisfiesTrace;
    	    }
            try {
                satisfiesTrace = this.executions.get().stream()
                    .map(SatisfiesTrace.class::cast)
                    .findFirst()
                    .get();
            } catch (NoSuchElementException ex) {
                throw new EolIncrementalExecutionException("Error creating trace model element. Requested SatisfiesTrace was "
                        + "duplicate but previous one was not found.");
            }
        }
        return satisfiesTrace;
    }      
            
                  
    @Override
    public boolean sameIdentityAs(final IEvlModuleExecution other) {
        if (other == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof EvlModuleExecution))
            return false;
        EvlModuleExecution other = (EvlModuleExecution) obj;
        if (!sameIdentityAs(other))
            return false;
        return true; 
  }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        return result;
    }

}
