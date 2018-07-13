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
package org.eclipse.epsilon.evl.incremental.trace.impl;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.epsilon.evl.incremental.trace.IEvlModuleTrace;
import org.eclipse.epsilon.evl.incremental.trace.IEvlModuleTraceRepository;
import org.eclipse.epsilon.base.incremental.trace.impl.ModuleExecutionTraceRepositoryImpl;

import java.util.ArrayList;
/** protected region EvlModuleTraceRepositoryImplImports on begin **/
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.epsilon.evl.incremental.trace.IInvariantTrace;
import org.eclipse.epsilon.base.incremental.models.IIncrementalModel;
import org.eclipse.epsilon.base.incremental.trace.IAccess;
import org.eclipse.epsilon.base.incremental.trace.IAllInstancesAccess;
import org.eclipse.epsilon.base.incremental.trace.IElementAccess;
import org.eclipse.epsilon.base.incremental.trace.IModelAccess;
import org.eclipse.epsilon.base.incremental.trace.IModelAccessHasModelTrace;
import org.eclipse.epsilon.base.incremental.trace.IModelElementTrace;
import org.eclipse.epsilon.base.incremental.trace.IModelTrace;
import org.eclipse.epsilon.base.incremental.trace.IModuleElementTrace;
import org.eclipse.epsilon.base.incremental.trace.IModuleExecutionTrace;
import org.eclipse.epsilon.base.incremental.trace.IPropertyAccess;
/** protected region EvlModuleTraceRepositoryImplImports end **/
import org.eclipse.epsilon.base.incremental.trace.IPropertyTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvlModuleTraceRepositoryImpl extends ModuleExecutionTraceRepositoryImpl<IEvlModuleTrace> implements IEvlModuleTraceRepository {

    private static final Logger logger = LoggerFactory.getLogger(EvlModuleTraceRepositoryImpl.class);
    
    private final Set<IEvlModuleTrace> extent;
    
    public EvlModuleTraceRepositoryImpl() {
        this.extent = new LinkedHashSet<>();
    }
    
    @Override
    public boolean add(IEvlModuleTrace item) {
        logger.info("Adding {} to repository", item);
        return extent.add(item);
    }

    @Override
    public boolean remove(IEvlModuleTrace item) {
        logger.info("Removing {} from repository", item);
        return extent.remove(item);
    }
    
    @Override
    public IEvlModuleTrace get(Object id) {
        
        logger.debug("Get EvlModuleTrace with id:{}", id);
        IEvlModuleTrace  result = null;
        try {
            result = extent.stream()
                    .filter(item -> item.getId().equals(id))
                    .findFirst()
                    .get();
        } catch (NoSuchElementException  e) {
            // No info about the ModelTrace
        }
        return result;
    }
    
    /** protected region IEvlModuleTraceRepositry on begin **/
    // Specialised search methods
	@Override
	public IEvlModuleTrace getEvlModuleTraceByIdentity(String source) {
		return extent.stream()
				.filter(mt -> mt.getUri() == source)
				.findFirst()
				.orElse(null);
	}

	@Override
	public IModuleExecutionTrace getModuleExecutionTraceByIdentity(String source) {
		return getEvlModuleTraceByIdentity(source);
	}
	
	
	@Override
	public Set<IEvlModuleTrace> findPropertyAccessExecutionTraces(
			String moduleSource,
			String modelUri,
			String elementId,
			String propertyName) {
		IEvlModuleTrace moduleTrace = getEvlModuleTraceByIdentity(moduleSource);
		
		IModelTrace modelTrace = null;
		Iterator<IModelAccess> it = moduleTrace.models().get();
		while (it.hasNext()) {
			IModelAccess next = it.next();
			IModelTrace mt = next.modelTrace().get();
			if (mt.getUri().equals(modelUri)) {
				modelTrace = mt;
			}
		}
		if (modelTrace == null) {
			// Model not involved in execution
			return Collections.emptySet();
		}
		IModelElementTrace elementTrace = null;
		Iterator<IModelElementTrace> eIt = modelTrace.elements().get();
		while (eIt.hasNext()) {
			IModelElementTrace next = eIt.next();
			if (next.getUri().equals(elementId)) {
				elementTrace = next;
			}
		}
		if (elementTrace == null) {
			// No info about element, there should be no trace associated to it
			return Collections.emptySet();
		}
		IPropertyTrace next = null;
		Iterator<IPropertyTrace> pIt = elementTrace.properties().get();
		while (pIt.hasNext()) {
			next = pIt.next();
			if (next.getName().equals(propertyName)) {
				break;
			}
		}
		if (next == null) {
			// Element does not have property by that name
			return Collections.emptySet();
		}
		IPropertyTrace pt = next;
		Iterable<IAccess> iterable = () -> moduleTrace.accesses().get();
		Set<IEvlModuleTrace> result = StreamSupport.stream(iterable.spliterator(), false)
				.filter(IPropertyAccess.class::isInstance)
				.map(IPropertyAccess.class::cast)
				.filter(pa -> pa.property().get().equals(pt))
				.map(pa -> pa.executionTrace().get())
				.map(IEvlModuleTrace.class::cast)
				.collect(Collectors.toSet());
		return result;
	}		
	
	@Override
	public Set<IEvlModuleTrace> findAllInstancesExecutionTraces(String moduleSource, String typeName) {
		IEvlModuleTrace moduleTrace = getEvlModuleTraceByIdentity(moduleSource);
		
		Iterable<IAccess> iterable = () -> moduleTrace.accesses().get();
		Set<IEvlModuleTrace> result = StreamSupport.stream(iterable.spliterator(), false)
				.filter(IAllInstancesAccess.class::isInstance)
				.map(IAllInstancesAccess.class::cast)
				.filter(aia -> aia.type().get().getName().equals(typeName))
				.map(aia -> aia.executionTrace().get())
				.map(IEvlModuleTrace.class::cast)
				.collect(Collectors.toSet());
		return result;
	}
	
	
	@Override
	public Set<IEvlModuleTrace> findSatisfiesExecutionTraces(IInvariantTrace invariantTrace) {
		// TODO Implement IEvlExecutionTraceRepository.findSatisfiesExecutionTraces
		throw new UnsupportedOperationException("Unimplemented Method    IEvlExecutionTraceRepository.findSatisfiesExecutionTraces invoked.");
	}

	@Override
	public Set<IEvlModuleTrace> getAllExecutionTraces() {
		return Collections.unmodifiableSet(extent);
	}

	@Override
	public Set<IEvlModuleTrace> findIndirectExecutionTraces(String moduleSource, IModelElementTrace modelElementTrace,
			Object modelElement, IIncrementalModel model) {
		
		IEvlModuleTrace moduleTrace = getEvlModuleTraceByIdentity(moduleSource);
		
		List<IEvlModuleTrace> elementTraces = getElementAccessesOfModelElement(modelElementTrace, moduleTrace);
		List<IEvlModuleTrace> propertyTraces = getPropertyAccessesOfModelElement(modelElementTrace, moduleTrace);
		List<IEvlModuleTrace> typeTraces = getAllInstancesAccessOfModelElement(modelElement, model, moduleTrace);
		Set<IEvlModuleTrace> result = new HashSet<IEvlModuleTrace>();
		result.addAll(elementTraces);
		result.addAll(propertyTraces);
		result.addAll(typeTraces);
		return result;
	}
	
	@Override
	public void removeTraceInformation(String moduleSource, IIncrementalModel model, IModelElementTrace modelElementTrace) {
		
		IEvlModuleTrace moduleTrace = getEvlModuleTraceByIdentity(moduleSource);
		Iterable<IAccess> iterable = () -> moduleTrace.accesses().get();
		List<IElementAccess> elementAccessTraces = StreamSupport.stream(iterable.spliterator(), false)
				.filter(IElementAccess.class::isInstance)
				.map(IElementAccess.class::cast)
				.filter(ea -> ea.element().get().equals(modelElementTrace))
				.collect(Collectors.toList());
		for (IElementAccess ea : elementAccessTraces) {
			IModuleElementTrace executionTrace = ea.executionTrace().get();
			ea.executionTrace().destroy(executionTrace);
			List<IAccess> list = new ArrayList<>();
			executionTrace.accesses().get().forEachRemaining(list::add);
			if (list.isEmpty()) {
				moduleTrace.moduleElements().destroy(executionTrace);
			}
			moduleTrace.accesses().destroy(ea);
		}
		List<IPropertyAccess> propertyAccessTraces = StreamSupport.stream(iterable.spliterator(), false)
				.filter(IPropertyAccess.class::isInstance)
				.map(IPropertyAccess.class::cast)
				.filter(pa -> pa.property().get().elementTrace().equals(modelElementTrace))
				.collect(Collectors.toList());
		for (IPropertyAccess pa : propertyAccessTraces) {
			IModuleElementTrace executionTrace = pa.executionTrace().get();
			pa.executionTrace().destroy(executionTrace);
			List<IAccess> list = new ArrayList<>();
			executionTrace.accesses().get().forEachRemaining(list::add);
			if (list.isEmpty()) {
				moduleTrace.moduleElements().destroy(executionTrace);
			}
			moduleTrace.accesses().destroy(pa);
		}
		
		//modelTrace.elements().destroy(modelElementTrace);
		// TODO We could delete allInstances access is no more elements of the type exist in the model?
	}
	
	/**
	 * @param modelElementUri
	 * @param moduleTrace
	 * @return
	 */
	private List<IEvlModuleTrace> getPropertyAccessesOfModelElement(IModelElementTrace modelElementTrace,
			IEvlModuleTrace moduleTrace) {
		// PropertyAccess to the element properties
		Iterable<IAccess> iterable = () -> moduleTrace.accesses().get();
		List<IEvlModuleTrace> propertyTraces = StreamSupport.stream(iterable.spliterator(), false)
				.filter(IPropertyAccess.class::isInstance)
				.map(IPropertyAccess.class::cast)
				.filter(pa -> pa.property().get().elementTrace().get().equals(modelElementTrace))
				.map(pa -> pa.executionTrace().get())
				.map(IEvlModuleTrace.class::cast)
				.collect(Collectors.toList());
		return propertyTraces;
	}
	
	/**
	 * AllInstancesAccess have to be done in the type, and in the super types
	 * @param modelElement
	 * @param model
	 * @param moduleTrace
	 * @return
	 */
	private List<IEvlModuleTrace> getAllInstancesAccessOfModelElement(Object modelElement,
			IIncrementalModel model, IEvlModuleTrace moduleTrace) {
		Set<String> elementTypes = model.getAllTypeNamesOf(modelElement);
		String elementType = model.getTypeNameOf(modelElement);
		Iterable<IAccess> iterable = () -> moduleTrace.accesses().get();
		List<IEvlModuleTrace> typeTraces = StreamSupport.stream(iterable.spliterator(), false)
				.filter(IAllInstancesAccess.class::isInstance)
				.map(IAllInstancesAccess.class::cast)
				.filter(aia -> {
					IModelTrace modelTrace = aia.type().get().modelTrace().get();
					if (modelTrace.getUri() == model.getModelUri()) {
						if (aia.getOfKind()) {
							return elementTypes.contains(aia.type().get().getName());
						}
						else {
							return aia.type().get().getName() == elementType;
						}
					}
					return false;
				})
				.map(pa -> pa.executionTrace().get())
				.map(IEvlModuleTrace.class::cast)
				.collect(Collectors.toList());
		return typeTraces;
	}

	/**
	 * @param modelElementUri
	 * @param moduleTrace
	 * @return
	 */
	private List<IEvlModuleTrace> getElementAccessesOfModelElement(IModelElementTrace modelElementTrace,
			IEvlModuleTrace moduleTrace) {
		Iterable<IAccess> iterable = () -> moduleTrace.accesses().get();
		List<IEvlModuleTrace> elementTraces = StreamSupport.stream(iterable.spliterator(), false)
				.filter(IElementAccess.class::isInstance)
				.map(IElementAccess.class::cast)
				.filter(ea -> ea.element().get().equals(modelElementTrace))
				.map(ea -> ea.executionTrace().get())
				.map(IEvlModuleTrace.class::cast)
				.collect(Collectors.toList());
		return elementTraces;
	}

    /** protected region IEvlModuleTraceRepositry end **/

}