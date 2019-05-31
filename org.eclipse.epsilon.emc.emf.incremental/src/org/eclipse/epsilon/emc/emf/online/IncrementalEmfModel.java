/*******************************************************************************
 * Copyright (c) 2017 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Horacio Hoyos Rodriguez - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.emc.emf.online;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.base.incremental.exceptions.models.NotInstantiableModelElementValueException;
import org.eclipse.epsilon.base.incremental.exceptions.models.NotSerializableModelException;
import org.eclipse.epsilon.base.incremental.execute.IIncrementalModule;
import org.eclipse.epsilon.base.incremental.models.IIncrementalModel;
import org.eclipse.epsilon.base.incremental.models.ModuleNotifications;
import org.eclipse.epsilon.emc.emf.EmfModel;

public class IncrementalEmfModel extends EmfModel implements IIncrementalModel {
	
	private boolean deliver;
	private ModuleNotifications moduleNotifications;
	
	public IncrementalEmfModel() {
		super();
		moduleNotifications = new ModuleNotifications(this);
	}

	@Override
	public Object serializePropertyValue(Object instance) throws NotSerializableModelException {
		if (!owns(instance)) {
			throw new NotSerializableModelException("Can not convert objects that dont belong to this model.");
		}
		// We assume that once the model is loaded, we can get the correspoing value from its id.
		return getElementId(instance);
	}

	@Override
	public Object deserializePropertyValue(String value) throws NotInstantiableModelElementValueException {
		return getElementById(value);
	}

	@Override
	public String getModelUri() {
		return getResource().getURI().toString();
	}

	@Override
	public boolean supportsNotifications() {
		return true;
	}

	@Override
	public void setDeliver(boolean deliver) {
		if (deliver != this.deliver) {
			if (deliver) {
//				for (IIncrementalModule module : getModules()) {
//					Set<String> elementIds = getModelTraceFactory().getAllModelElementIds();
//					EmfPropertyChangeListener emfPCL = new EmfPropertyChangeListener(this, module, elementIds);
//					getResource().eAdapters().add(emfPCL);
//				}
			}
			else {
				unRegister();
			}
		}
		this.deliver = deliver;
	}

	@Override
	public boolean isDelivering() {
		return deliver;
	}

	
	/**
	 * 
	 */
	private void unRegister() {
		Iterator<Adapter> it = getResource().eAdapters().iterator();
		while (it.hasNext()) {
			Adapter d = it.next();
			if (d instanceof EmfPropertyChangeListener) {
				it.remove();
			}
		};
	}	@Override
	public void notifyChange(Object element, String propertyName) {
		moduleNotifications.notifyChange(element, propertyName);
	}

	@Override
	public void notifyDeletion(Object element) {
		moduleNotifications.notifyDeletion(element);
	}

	@Override
	public void notifyCreation(Object element) {
		moduleNotifications.notifyCreation(element);
	}
	
	@Override
	public boolean registerModule(IIncrementalModule module) {
		return moduleNotifications.registerModule(module);
	}

	@Override
	public boolean isRegistered(IIncrementalModule module) {
		return moduleNotifications.isRegistered(module);
	}

	@Override
	public boolean unregisterModule(IIncrementalModule module) {
		return moduleNotifications.unregisterModule(module);
	}

	@Override
	public Iterator<Object> getAllElements() {
		return new ResourceIterator(getResources());
	}
	
	private class ResourceIterator implements Iterator<Object> {
	
		private final Queue<Iterator<EObject>> iterators;
		private Iterator<EObject> current = null;
		
		public ResourceIterator(List<Resource> resources) {
			iterators = new ArrayDeque<>();
			for (Resource r : resources) {
				iterators.add(r.getAllContents());
			}
		}

		@Override
		public boolean hasNext() {
			if (current == null) {
				if (iterators.isEmpty()) {
					return false;
				}
				current = iterators.poll();
			}
			if (current.hasNext()) {
				return true;
			}
			current = iterators.poll();
			if (current == null) {
				return false;
			}
			return current.hasNext();
			
		}

		@Override
		public EObject next() {
			if (current == null) {
				if (iterators.isEmpty()) {
					throw new NoSuchElementException();
				}
				current = iterators.poll();
			}
			return current.next();
		}
	}
	
}
