 /*******************************************************************************
 * This file was automatically generated on: 2017-12-07.
 * Only modify protected regions indicated by "<!-- -->"
 *
 * Copyright (c) 2017 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 ******************************************************************************/
package org.eclipse.epsilon.eol.incremental.trace.impl;

import org.eclipse.epsilon.eol.incremental.trace.IElementAccess;
import java.util.NoSuchElementException;

import org.eclipse.epsilon.eol.incremental.EolIncrementalExecutionException;
import org.eclipse.epsilon.eol.incremental.trace.impl.TraceModelDuplicateRelation;
import org.eclipse.epsilon.eol.incremental.trace.IAccessHasExecution;
import org.eclipse.epsilon.eol.incremental.trace.IElementAccessHasModelElement;
import org.eclipse.epsilon.eol.incremental.trace.IExecutionTrace;
import org.eclipse.epsilon.eol.incremental.trace.IModelElementTrace;
import org.eclipse.epsilon.eol.incremental.trace.impl.AccessHasExecution;
import org.eclipse.epsilon.eol.incremental.trace.impl.ElementAccessHasModelElement;

/**
 * Implementation of IElementAccess. 
 */
public class ElementAccess implements IElementAccess {

    /** The id */
    private Object id;

    /** The execution relation */
    private final IAccessHasExecution execution;

    /** The modelElement relation */
    private final IElementAccessHasModelElement modelElement;

    /**
     * Instantiates a new ElementAccess. The ElementAccess is uniquely identified by its
     * container and any attributes identified as indexes.
     */    
    public ElementAccess(IModelElementTrace modelElement, IExecutionTrace container) throws TraceModelDuplicateRelation {
        this.execution = new AccessHasExecution(this);
        this.modelElement = new ElementAccessHasModelElement(this);
        this.modelElement.create(modelElement);
        if (!container.accesses().create(this)) {
            throw new TraceModelDuplicateRelation();
        };
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
    public IAccessHasExecution execution() {
        return execution;
    }

    @Override
    public IElementAccessHasModelElement modelElement() {
        return modelElement;
    }

    @Override
    public boolean sameIdentityAs(final IElementAccess other) {
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
        if (!(obj instanceof ElementAccess))
            return false;
        ElementAccess other = (ElementAccess) obj;
        if (!sameIdentityAs(other))
            return false;
        // Will use modelElement for equals
        if (modelElement.get() == null) {
            if (other.modelElement.get() != null)
                return false;
        }        else if (!modelElement.get().equals(other.modelElement.get())) {
            return false;
        }
        // Will use execution for equals
        if (execution.get() == null) {
            if (other.execution.get() != null)
                return false;
        }        else if (!execution.get().equals(other.execution.get())) {
            return false;
        }
        return true; 
  }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((modelElement == null) ? 0 : modelElement.hashCode());
        result = prime * result + ((execution == null) ? 0 : execution.hashCode());
        return result;
    }

}
