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
package org.eclipse.epsilon.base.incremental.trace;


/**
 * The IdElement defines the access methods for the EClass features.
 * Additionally, the IIdElement acts as the root entity of the AGGREGATE of its
 * container references. That is, elements contained in the IdElement must be
 * created through this interface.
 */
public interface IIdElement {

    /**
     * Returns the value of the '<em><b>Id</b></em>' attribute.
     * <!-- protected region id-getter-doc on begin -->
     * <p>
     * If the meaning of the '<em>Id</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- protected region id-getter-doc end --> 
     * @return the value of the '<em>Id</em>' attribute.
     */
    Object getId();    

    /**
     * Sets the value of the '{@link IdElement#Id <em>Id</em>}' attribute.
     * <!-- protected region id-setter-doc on begin -->
     * <!-- protected region id-setter-doc end --> 
     * @param value the new value of the '<em>Id/em>' attribute.
     */
    void setId(Object value);
            

}
