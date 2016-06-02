/* 
 * Copyright 2016, Emanuel Rabina (http://www.ultraq.net.nz/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.net.ultraq.thymeleaf.models

import org.thymeleaf.model.AttributeValueQuotes
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IModelFactory

/**
 * Create Thymeleaf 3.0 models using a simplified syntax.
 * 
 * @author Emanuel Rabina
 */
class ModelBuilder extends BuilderSupport {

	private final IModelFactory modelFactory

	/**
	 * Constructor, create a new model builder using the given model factory.
	 * 
	 * @param modelFactory
	 */
	ModelBuilder(IModelFactory modelFactory) {

		this.modelFactory = modelFactory
	}

	/**
	 * Appends an existing model to the model being built.
	 * 
	 * @param model
	 */
	public void add(IModel model) {

		current.insertModel(current.size() - 1, model)
	}

	/**
	 * Captures the top `build` call so that it doesn't end up as a node in the
	 * final model.
	 * 
	 * @param definition
	 * @return The model built using the closure definition.
	 */
	IModel build(Closure definition) {

		setClosureDelegate(definition, null)
		return definition.call()
	}

	/**
	 * Create a model for the given HTML element.
	 * 
	 * @param name HTML element name.
	 * @return New model with the given name.
	 */
	@Override
	protected Object createNode(Object name) {

		return createNode(name, null, null)
	}

	/**
	 * Create a model for the given HTML element and inner text content.
	 * 
	 * @param name  HTML element name.
	 * @param value Text content.
	 * @return New model with the given name and content.
	 */
	@Override
	protected Object createNode(Object name, Object value) {

		return createNode(name, null, value)
	}

	/**
	 * Create a model for the given HTML element and attributes.
	 * 
	 * @param name       HTML element name.
	 * @param attributes Element attributes.
	 * @return New model with the given name and attributes.
	 */
	@Override
	protected Object createNode(Object name, Map attributes) {

		return createNode(name, attributes, null)
	}

	/**
	 * Create a model for the given HTML element, attributes, and inner text
	 * content.
	 * 
	 * @param name       HTML element name.
	 * @param attributes Element attributes.
	 * @param value      Text content.
	 * @return New model with the given name, attributes, and content.
	 */
	@Override
	protected IModel createNode(Object name, Map attributes, Object value) {

		def model = modelFactory.createModel()

		// Standalone element
		if (attributes && attributes['standalone']) {
			attributes.remove('standalone')
			model.add(modelFactory.createStandaloneElementTag(name, attributes, AttributeValueQuotes.DOUBLE, false, true))
		}

		// Open/close element and potential text content
		else {
			model.add(modelFactory.createOpenElementTag(name, attributes, AttributeValueQuotes.DOUBLE, false));
			if (value) {
				model.add(modelFactory.createText(value))
			}
			model.add(modelFactory.createCloseElementTag(name));
		}

		return model
	}

	/**
	 * Link a parent and child node.  A child node is appended to a parent by
	 * being the last sub-model before the parent close tag.
	 * 
	 * @param parent
	 * @param child
	 */
	@Override
	protected void nodeCompleted(Object parent, Object child) {

		if (parent != null) {

			// TODO: Insert w/ whitespace?
			parent.insertModel(parent.size() - 1, child)
		}
	}

	/**
	 * Does nothing.  Because models only copy events when added to one another,
	 * we can't just add child events at this point - we need to wait until that
	 * child has had it's children added, and so on.  So the parent/child link is
	 * made in the {@link ModelBuilder#nodeCompleted} method instead.
	 */
	@Override
	protected void setParent(Object parent, Object child) {
	}
}
