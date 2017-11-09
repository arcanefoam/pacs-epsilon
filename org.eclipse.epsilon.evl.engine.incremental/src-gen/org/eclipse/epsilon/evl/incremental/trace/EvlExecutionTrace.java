 /*******************************************************************************
 * This file was automatically generated on: 2017-11-09.
 * Only modify protected regions indicated by "<!-- -->"
 *
 * Copyright (c) 2017 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 ******************************************************************************/
package org.eclipse.epsilon.evl.incremental.trace;

import org.eclipse.epsilon.eol.incremental.EolIncrementalExecutionException;
import org.eclipse.epsilon.eol.incremental.trace.ExecutionTrace;    
import org.eclipse.epsilon.eol.incremental.trace.Model;    
import org.eclipse.epsilon.evl.incremental.trace.EvlModule;    

/**
 * The EvlExecutionTrace defines the access methods for the EClass features.
 * Additionally, the EvlExecutionTrace extends ExecutionTrace acts as the root entity of the AGGREGATE of its
 * container references. That is, elements contained in the EvlExecutionTrace must be
 * created through this interface.
 */
public interface EvlExecutionTrace extends ExecutionTrace {

 
    /**
     * EvlExecutionTrace has same identity in the aggregate.
     */
    public boolean sameIdentityAs(final EvlExecutionTrace other);
    
    /** The Model Factory. */
    Model createModel(String name) throws EolIncrementalExecutionException;       
   
    /** The EvlModule Factory. */
    EvlModule createEvlModule(String source) throws EolIncrementalExecutionException;       
            
   
}
