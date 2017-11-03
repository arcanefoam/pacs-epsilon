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
package org.eclipse.epsilon.eol.incremental.trace;

import java.util.Queue;

public interface ExecutionTraceHasExecution {
    
    /**
     * Get the value(s) for the reference
     */
    Queue<Execution> get();

    /**
	 * Set a new value for the reference. Although public, this method should be only accessed by
	 * classes in the same package or extrange behavoir can be observed.
	 */
    void set(Execution target);

    /**
     * Remove a value for the reference. Although public, this method should be only accessed by
     * classes in the same package or extrange behavoir can be observed.
     */
    void remove(Execution target);
    
    /**
     * Determines if there is a conflict with a possible target.
     * Returns true if the opposite reference is already set, or if this refernce is single-valued
     * and is already set.
     */
    boolean conflict(Execution target);
    
    /**
     * Retruns true if the target is already related via this reference.
     */
    boolean related(Execution target);
    
    /**
     * Create a reference to the target element. Returns true if the relation was created.
     * Single-valued references can only be set if not set.
     * Unique Multi-valued references can only be set if not set before.
     * If the reference has an opposite, the refletive relation
     * is also created.
     */
    boolean create(Execution target);
    
    /**
     * Destroy a reference to the target element. Returns true, if the reference existed
     * and was properly destroyed. If the reference has an opposite, the refletive relation
     * is also destroyed.
     */    
    boolean destroy(Execution target);
    

}