 /*******************************************************************************
 * This file was automatically generated on: 2019-04-30.
 * Only modify protected regions indicated by "/** **&#47;"
 *
 * Copyright (c) 2017 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 ******************************************************************************/
package org.eclipse.epsilon.evl.incremental.trace.impl;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.*;
import org.eclipse.epsilon.base.incremental.trace.util.ActiveTraversal;
import org.eclipse.epsilon.base.incremental.trace.util.GremlinUtils;
import org.eclipse.epsilon.base.incremental.trace.util.TraceFactory;
import org.eclipse.epsilon.base.incremental.trace.util.TinkerpopDelegate;
import org.eclipse.epsilon.base.incremental.exceptions.TraceModelConflictRelation;
import org.eclipse.epsilon.evl.incremental.trace.IContextTrace;
import org.eclipse.epsilon.evl.incremental.trace.IInvariantTrace;
import org.eclipse.epsilon.evl.incremental.trace.IContextTraceHasConstraints;
import org.eclipse.epsilon.base.incremental.trace.impl.Feature;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;


/**
 * Implementation of IContextTraceHasConstraints reference. 
 */
@SuppressWarnings("unused") 
public class ContextTraceHasConstraintsGremlin extends Feature
        implements IContextTraceHasConstraints, TinkerpopDelegate<Edge> {
    
    /** The graph traversal source for all navigations */
    private GraphTraversalSource gts;
    
    /** The source(s) of the reference */
    protected IContextTrace source;
    
    /** Factory used to wrap referenced elements */
    protected final TraceFactory factory;
    
    
    /**
     * Instantiates a new IContextTraceHasConstraints.
     *
     * @param source                the source element of the reference
     * @param delegate              the delegate edge
     * @param gts                   the graph taversal source   
     * @param factory               the factory used to instantiante the target
     */
    public ContextTraceHasConstraintsGremlin (
        IContextTrace source,
        GraphTraversalSource gts, 
        TraceFactory factory) {
        super(true);
        this.source = source;
        this.gts = gts;
        this.factory = factory;
    }
    
    
    
    // PUBLIC API
        
    @Override
    public Iterator<IInvariantTrace> get() {
        return new GremlinUtils.IncrementalFactoryIterator<IInvariantTrace>(getRaw(),
                gts, factory);
    }
    
    /**
     * Get the Tinkerpop GraphTraversal iterator of the vertices that are part of the relation.
     */
    public GraphTraversal<Vertex, Vertex> getRaw() {
        GraphTraversal<Vertex, Vertex> result = null;
        try (ActiveTraversal agts = new ActiveTraversal(gts)) {
            result = agts.V(source.getId()).out("constraints");
        } catch (Exception e) {
            throw new IllegalStateException("There was an error during graph traversal.", e);
        }
        return result;
    }
    

    @Override
    public boolean create(IInvariantTrace target) throws TraceModelConflictRelation {
        if (conflict(target)) {
            if (related(target)) {
                return true;
            }
            throw new TraceModelConflictRelation("Relation to previous IInvariantTrace exists");
        }
        target.invariantContext().set(source);
        set(target);
        return true;
    }

    @Override
    public boolean destroy(IInvariantTrace target) {
        if (!related(target)) {
            return false;
        }
        target.invariantContext().remove(source);
        remove(target);
        return true;
    }
    
    @Override
    public boolean conflict(IInvariantTrace target) {
        boolean result = false;
        try (ActiveTraversal agts = new ActiveTraversal(gts)) {
	        if (isUnique()) {
	            GraphTraversal<Vertex, Vertex> gt =  agts.V(source.getId()).out("constraints");
                for (Entry<String, Object> id : target.getIdProperties().entrySet()) {
                    gt.has(id.getKey(), id.getValue());
                }
                result |= gt.hasNext();
            }
            result |= target.invariantContext().get() != null;
        } catch (Exception e) {
            throw new IllegalStateException("There was an error during graph traversal.", e);
        }
        return result;
    }
    
    @Override
    public boolean related(IInvariantTrace target) {
    	if (target == null) {
			return false;
		}
        boolean result = false;
        try (ActiveTraversal agts = new ActiveTraversal(gts)) {
		  result = agts.V(source.getId()).out("constraints").hasId(target.getId()).hasNext() && source.equals(target.invariantContext().get());
		} catch (Exception e) {
            throw new IllegalStateException("There was an error during graph traversal.", e);
        }
        return result;
	}
	
	@Override
    public Edge delegate() {
        return null;
    }
    
    @Override
    public GraphTraversalSource graphTraversalSource() {
        return gts;
    }
        
    // PRIVATE API
    
    @Override
    public void set(IInvariantTrace target) {
        try (ActiveTraversal agts = new ActiveTraversal(gts)) {
            agts.V(source.getId()).addE("constraints")
                    .to(agts.V(target.getId())).iterate();
        } catch (Exception e) {
            throw new IllegalStateException("There was an error during graph traversal.", e);
        }
        
    }
    
    @Override
    public void remove(IInvariantTrace target) {
        try (ActiveTraversal agts = new ActiveTraversal(gts)) {
            agts.V(source.getId())
                    .outE("constraints")
                    .as("e").inV()
                    .hasId(target.getId())
                    .select("e").drop().iterate();
        } catch (Exception e) {
            throw new IllegalStateException("There was an error during graph traversal.", e);
        }
    }
}