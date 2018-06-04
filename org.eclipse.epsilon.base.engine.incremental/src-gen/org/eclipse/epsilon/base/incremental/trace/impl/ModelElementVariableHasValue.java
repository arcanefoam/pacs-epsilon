 /*******************************************************************************
 * This file was automatically generated on: 2018-05-31.
 * Only modify protected regions indicated by "/** **&#47;"
 *
 * Copyright (c) 2017 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 ******************************************************************************/
package org.eclipse.epsilon.base.incremental.trace.impl;

import org.eclipse.epsilon.base.incremental.trace.IModelElementVariable;
import org.eclipse.epsilon.base.incremental.trace.IModelElementTrace;
import org.eclipse.epsilon.base.incremental.trace.IModelElementVariableHasValue;
import org.eclipse.epsilon.base.incremental.trace.impl.Feature;


/**
 * Implementation of IModelElementVariableHasValue reference. 
 */
public class ModelElementVariableHasValue extends Feature implements IModelElementVariableHasValue {
    
    /** The source(s) of the reference */
    protected IModelElementVariable source;
    
    /** The target(s) of the reference */
    protected IModelElementTrace target;
    
    /**
     * Instantiates a new IModelElementVariableHasValue.
     *
     * @param source the source of the reference
     */
    public ModelElementVariableHasValue (IModelElementVariable source) {
        super(true);
        this.source = source;
    }
    
    // PUBLIC API
        
    @Override
    public IModelElementTrace get() {
        return target;
    }
    
    @Override
    public boolean create(IModelElementTrace target) {
        if (conflict(target)) {
            return false;
        }
        set(target);
        return true;
    }

    @Override
    public boolean destroy(IModelElementTrace target) {
        if (!related(target)) {
            return false;
        }
        remove(target);
        return true;
    }
    
    @Override
    public boolean conflict(IModelElementTrace target) {
        boolean result = false;
        result |= get() != null;
        return result;
    }
    
    @Override
    public boolean related(IModelElementTrace target) {
  
        return target.equals(this.target) ;
    }
    
    // PRIVATE API
    
    @Override
    public void set(IModelElementTrace target) {
        this.target = target;
    }
    
    @Override
    public void remove(IModelElementTrace target) {
        this.target = null;
    }

}