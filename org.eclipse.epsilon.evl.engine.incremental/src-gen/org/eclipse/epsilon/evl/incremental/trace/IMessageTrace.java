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
package org.eclipse.epsilon.evl.incremental.trace;

import org.eclipse.epsilon.eol.incremental.EolIncrementalExecutionException;
import org.eclipse.epsilon.eol.incremental.trace.IAllInstancesAccess;    
import org.eclipse.epsilon.eol.incremental.trace.IExecutionTrace;    
import org.eclipse.epsilon.eol.incremental.trace.IModelTypeTrace;    
import org.eclipse.epsilon.eol.incremental.trace.IPropertyAccess;    
import org.eclipse.epsilon.eol.incremental.trace.IPropertyTrace;    
import org.eclipse.epsilon.evl.incremental.trace.IInvariantTrace;    

/**
 * The MessageTrace defines the access methods for the EClass features.
 * Additionally, the IMessageTrace acts as the root entity of the AGGREGATE of its
 * container references. That is, elements contained in the MessageTrace must be
 * created through this interface.
 */
public interface IMessageTrace extends IExecutionTrace {

    /** The invariant reference. */
    IMessageTraceHasInvariant invariant();
                
 
    /**
     * MessageTrace has same identity in the aggregate.
     */
    public boolean sameIdentityAs(final IMessageTrace other);
    
   
}
