 /*******************************************************************************
 * This file was automatically generated on: 2018-04-20.
 * Only modify protected regions indicated by "/** **&#47;"
 *
 * Copyright (c) 2017 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 ******************************************************************************/
package org.eclipse.epsilon.base.incremental.trace;


import org.eclipse.epsilon.base.incremental.trace.IModuleTrace;

public interface IRuleTraceHasModule {

    // PUBLIC API
    
    boolean isUnique();
    
    /**
     * Get the value(s) for the reference
     */
    public IModuleTrace get();

    /**
     * Create a reference to the target element. Returns true if the relation was created or if the
     * relation already existed. 
     * The relation is created if there are no conflicts (see {@link ExecutionHasAccesses#conflict(IModuleTrace)}).
     * If the reference has an opposite, that relation is also created.
     *
     * @see ExecutionHasAccesses#conflict(IModuleTrace)
     * @see ExecutionHasAccesses#related(IModuleTrace)
     */
    boolean create(IModuleTrace target);
    
    /**
     * Destroy a reference to the target element. Returns true, if the reference existed
     * and was properly destroyed. If the reference has an opposite, that relation
     * is also destroyed.
     *
     * @see ExecutionHasAccesses#related(IModuleTrace)
     */    
    boolean destroy(IModuleTrace target);
    
    /**
     * Determines if there is a conflict with a possible target. Conflicts can only arise for if
     * the reference has an opposite and for unique multi-valued references. Conflicts enforce
     * that the application destroys relations before creating new ones. This helps maintain data
     * integrity. Returns true if there is a conflict. Conflicts are determined by:
     * <ul>
     *  <li>If the relation is many-to-many, there is no conflict.<li>
     *  <li>If the relation is one-to-one there is a conflict if the target is already part of
     *      another relation, i.e. target.oppoiste != null.</li>
     *  <li>If the relation is one-to-many:
     *      <ul>
     *          <li>If the relation is not containment and non-unique, there is no conflict.<li>
     *          <li>If the relation is not-containment and unique, there is conflict if the target
     *              is already related to the source.<li>
     *          <li>If the relation is containment and non-unique, there is a conflict if the target
     *              is contained elsewhere, i.e. target.oppoiste != null</li>
     *          <li>If the relation is containment and unique, there is a conflict if the target is
     *              contained elsewhere, i.e. target.oppoiste != null, or if the target is already
     *              related to the source.</li>
     *      </ul>
     *  </li>
     */
    boolean conflict(IModuleTrace target);
    
    /**
     * Returns true if the target is already related via this reference.
     */
    boolean related(IModuleTrace target);

    // PRIVATE API

    /**
     * Set a new value for the reference. This method should be only accessed by classes in the
     * relation.
     *
     * @see ExecutionHasAccesses#create(IModuleTrace)
     */
    void set(IModuleTrace target);

    /**
     * Remove a value for the reference. This method should be only accessed by classes in the
     * relation.
     *
     * @see ExecutionHasAccesses#destroy(IModuleTrace)
     */
    void remove(IModuleTrace target);
}