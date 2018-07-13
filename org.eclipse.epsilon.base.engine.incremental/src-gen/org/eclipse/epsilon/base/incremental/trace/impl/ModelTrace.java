 /*******************************************************************************
 * This file was automatically generated on: 2018-07-13.
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

import org.eclipse.epsilon.base.incremental.trace.IModelTrace;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** protected region ModelTraceImports on begin **/
/** protected region ModelTraceImports end **/

import org.eclipse.epsilon.base.incremental.exceptions.EolIncrementalExecutionException;
import org.eclipse.epsilon.base.incremental.exceptions.TraceModelDuplicateRelation;
import org.eclipse.epsilon.base.incremental.trace.IModelElementTrace;
import org.eclipse.epsilon.base.incremental.trace.IModelTraceHasElements;
import org.eclipse.epsilon.base.incremental.trace.IModelTraceHasTypes;
import org.eclipse.epsilon.base.incremental.trace.IModelTypeTrace;
import org.eclipse.epsilon.base.incremental.trace.impl.ModelElementTrace;
import org.eclipse.epsilon.base.incremental.trace.impl.ModelTraceHasElements;
import org.eclipse.epsilon.base.incremental.trace.impl.ModelTraceHasTypes;
import org.eclipse.epsilon.base.incremental.trace.impl.ModelTypeTrace;

/**
 * Implementation of IModelTrace. 
 */
public class ModelTrace implements IModelTrace {

    /**
	 * The id.
	 */
    private Object id;

    /**
	 * The uri.
	 */
    private String uri;

    /**
     * The elements.
     */
    private final IModelTraceHasElements elements;

    /**
     * The types.
     */
    private final IModelTraceHasTypes types;

    /**
     * Instantiates a new ModelTrace. The ModelTrace is uniquely identified by its
     * container and any attributes identified as indexes.
     */    
    public ModelTrace(String uri) throws TraceModelDuplicateRelation {
        this.uri = uri;
        this.elements = new ModelTraceHasElements(this);
        this.types = new ModelTraceHasTypes(this);

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
    public String getUri() {
        return uri;
    }
    
    @Override
    public IModelTraceHasElements elements() {
        return elements;
    }

    @Override
    public IModelTraceHasTypes types() {
        return types;
    }

    @Override
    public IModelElementTrace createModelElementTrace(String uri) throws EolIncrementalExecutionException {
        IModelElementTrace modelElementTrace = null;
        try {
            modelElementTrace = new ModelElementTrace(uri, this);
            
            this.elements().create(modelElementTrace);
        } catch (TraceModelDuplicateRelation e) {
            // Pass
        } finally {
    	    if (modelElementTrace != null) {
    	        return modelElementTrace;
    	    }
            Iterator<IModelElementTrace> it = this.elements.get();
            while (it.hasNext()) {
            	IModelElementTrace item = it.next();
                    //.filter(item -> item.getUri() == uri)
    			if (item.getUri() == uri) {
    				modelElementTrace = item;
    			}
    		}
    		if (modelElementTrace == null) {
               	throw new EolIncrementalExecutionException("Error creating trace model element. Requested ModelElementTrace was "
                		+ "duplicate but previous one was not found.");
            }
        }
        return modelElementTrace;
    }      
                  
    @Override
    public IModelTypeTrace createModelTypeTrace(String name) throws EolIncrementalExecutionException {
        IModelTypeTrace modelTypeTrace = null;
        try {
            modelTypeTrace = new ModelTypeTrace(name, this);
            
            this.types().create(modelTypeTrace);
        } catch (TraceModelDuplicateRelation e) {
            // Pass
        } finally {
    	    if (modelTypeTrace != null) {
    	        return modelTypeTrace;
    	    }
            Iterator<IModelTypeTrace> it = this.types.get();
            while (it.hasNext()) {
            	IModelTypeTrace item = it.next();
                    //.filter(item -> item.getName() == name)
    			if (item.getName() == name) {
    				modelTypeTrace = item;
    			}
    		}
    		if (modelTypeTrace == null) {
               	throw new EolIncrementalExecutionException("Error creating trace model element. Requested ModelTypeTrace was "
                		+ "duplicate but previous one was not found.");
            }
        }
        return modelTypeTrace;
    }      
                  
    @Override
    public boolean sameIdentityAs(final IModelTrace other) {
        if (other == null) {
            return false;
        }
        String uri = getUri();
        String otherUri = other.getUri();
        if (uri == null) {
            if (otherUri != null)
                return false;
        } else if (!uri.equals(otherUri)) {
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
        if (!(obj instanceof ModelTrace))
            return false;
        ModelTrace other = (ModelTrace) obj;
        if (!sameIdentityAs(other))
            return false;
        return true; 
  }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }
}
