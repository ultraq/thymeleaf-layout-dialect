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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.thymeleaf.engine.ElementDefinitions
import org.thymeleaf.engine.HTMLElementType
import org.thymeleaf.model.AttributeValueQuotes
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IModelFactory
import org.thymeleaf.templatemode.TemplateMode

/**
 * Create Thymeleaf 3.0 models using a simplified syntax.
 * 
 * @author Emanuel Rabina
 */
class ModelBuilder extends BuilderSupport {

	private static final Logger logger = LoggerFactory.getLogger(ModelBuilder)

	private final ElementDefinitions elementDefinitions
	private final IModelFactory modelFactory
	private final TemplateMode templateMode

	/**
	 * Constructor, create a new model builder using the given model factory.
	 * 
	 * @param modelFactory
	 * @param elementDefinitions
	 * @param templateMode
	 */
	ModelBuilder(IModelFactory modelFactory, ElementDefinitions elementDefinitions, TemplateMode templateMode) {

		this.modelFactory = modelFactory
		this.elementDefinitions = elementDefinitions
		this.templateMode = templateMode
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
		def elementDefinition = elementDefinitions."for${templateMode}Name"(name)

		// Standalone element
		if (elementDefinition.type == HTMLElementType.VOID) {
			if (attributes && attributes['standalone']) {
				attributes.remove('standalone')
				model.add(modelFactory.createStandaloneElementTag(name, attributes, AttributeValueQuotes.DOUBLE, false, true))
			}
			else if (attributes && attributes['void']) {
				attributes.remove('void')
				model.add(modelFactory.createStandaloneElementTag(name, attributes, AttributeValueQuotes.DOUBLE, false, false))
			}
			else {
				logger.warn("""
					Instructed to write a closing tag {0} for an HTML void element.  This
					might cause processing errors further down the track.  To avoid this,
					either self close the opening element, remove the closing tag, or
					process this template using the XML processing mode.  See
					https://html.spec.whatwg.org/multipage/syntax.html#void-elements
					for more information on HTML void elements.
				""".stripIndent().trim(), name)

				model.add(modelFactory.createStandaloneElementTag(name, attributes, AttributeValueQuotes.DOUBLE, false, false))
				model.add(modelFactory.createCloseElementTag(name));
			}
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
