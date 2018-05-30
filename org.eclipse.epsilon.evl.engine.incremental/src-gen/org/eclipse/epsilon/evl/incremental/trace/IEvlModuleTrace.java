 /*******************************************************************************
 * This file was automatically generated on: 2018-05-30.
 * Only modify protected regions indicated by "/** **&#47;"
 *
 * Copyright (c) 2017 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 ******************************************************************************/
package org.eclipse.epsilon.evl.incremental.trace;

import org.eclipse.epsilon.base.incremental.exceptions.EolIncrementalExecutionException;
import org.eclipse.epsilon.base.incremental.trace.IExecutionContext;    
import org.eclipse.epsilon.base.incremental.trace.IModuleTrace;    
import org.eclipse.epsilon.evl.incremental.trace.IContextTrace;    

/**
 * The EvlModuleTrace defines the access methods for the EClass features.
 * Additionally, the IEvlModuleTrace acts as the root entity of the AGGREGATE of its
 * container references. That is, elements contained in the EvlModuleTrace must be
 * created through this interface.
 */
public interface IEvlModuleTrace extends IModuleTrace {


 
    /**
     * EvlModuleTrace has same identity in the aggregate.
     */
    public boolean sameIdentityAs(final IEvlModuleTrace other);
    
    /** The ContextTrace Factory. */
    IContextTrace createContextTrace(String kind, Integer index, IExecutionContext executionContext) throws EolIncrementalExecutionException;       
            
   
}
