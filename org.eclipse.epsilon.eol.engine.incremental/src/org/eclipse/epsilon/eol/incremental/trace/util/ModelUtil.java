package org.eclipse.epsilon.eol.incremental.trace.util;

import java.util.NoSuchElementException;

import org.eclipse.epsilon.eol.incremental.trace.IModelTrace;
import org.eclipse.epsilon.eol.incremental.trace.IModelElementTrace;
import org.eclipse.epsilon.eol.incremental.trace.IModelTypeTrace;
import org.eclipse.epsilon.eol.incremental.trace.IPropertyTrace;


public class ModelUtil {

	public static IModelElementTrace findElement(IModelTrace model, String elementUri) {
		
		IModelElementTrace result = null;
		try {
			model.elements().get().stream()
				.filter(me -> me.getUri().equals(elementUri))
				.findFirst()
				.get();
		} catch (NoSuchElementException ex) {
			// No matching element found
		}
		return result;
	}

	public static IPropertyTrace findProperty(IModelElementTrace modelElement, String name) {
		
		IPropertyTrace result = null;
		try {
			result = modelElement.properties().get().stream()
					.filter(p -> p.getName().equals(name))
					.findFirst()
					.get();
		} catch (NoSuchElementException ex) {
			// No matching element found
		}
		return result;
	}

	public static IModelTypeTrace findModelType(IModelTrace model, String modelTypeName) {
		IModelTypeTrace result;
		result = model.types().get().stream()
					.filter(mt -> mt.getName().equals(modelTypeName))
					.findFirst()
					.orElse(null);
		return result;
	}

}
