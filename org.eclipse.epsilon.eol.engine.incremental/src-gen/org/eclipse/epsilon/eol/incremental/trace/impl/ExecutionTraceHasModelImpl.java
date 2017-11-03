 /*******************************************************************************
 * This file was automatically generated on: 2017-11-03.
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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.eclipse.epsilon.eol.incremental.trace.ExecutionTrace;
import org.eclipse.epsilon.eol.incremental.trace.Model;
import org.eclipse.epsilon.eol.incremental.trace.ExecutionTraceHasModel;
import org.eclipse.epsilon.eol.incremental.trace.impl.Feature;


/**
 * Implementation of model reference. 
 */
public class ExecutionTraceHasModelImpl extends Feature implements ExecutionTraceHasModel {
    
    protected ExecutionTrace source;
    protected Queue<Model> target =  new ConcurrentLinkedQueue<Model>();
    
    /**
     * Instantiates a new ExecutionTraceHasModel.
     *
     * @param source the source of the reference
     */
    public ExecutionTraceHasModelImpl (ExecutionTrace source) {
        super(true);
        this.source = source;
    }
    
    @Override
    public Queue<Model> get() {
        return target;
    }
    
    @Override
    public void set(Model target) {
        this.target.add(target);
    }
    
    @Override
    public void remove(Model target) {
        this.target.remove(target);
    }
    
    @Override
    public boolean conflict(Model  target) {
        boolean result = false;
        result |= get().contains(target);
        return result;
    }
    
    @Override
    public boolean related(Model target) {
  
        return get().contains(target) ;
    }
    
    @Override
    public boolean create(Model target) {
        if (conflict(target)) {
            return false;
        }
        if (related(target)) {
            return true;
        }
        set(target);
        return true;
    }

    @Override
    public boolean destroy(Model target) {
        if (!related(target)) {
            return false;
        }
        remove(target);
        return true;
    }

}