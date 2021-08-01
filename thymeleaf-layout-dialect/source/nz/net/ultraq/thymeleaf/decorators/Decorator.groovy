/* 
 * Copyright 2013, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.decorators

import org.thymeleaf.model.IModel

/**
 * A decorator performs decoration of a target model, using a source model for
 * all the decorations to apply.  What exactly "decoration" means can vary per
 * implementation.
 * 
 * @author Emanuel Rabina
 */
interface Decorator {

	/**
	 * Decorate the target model with the contents of the source model, returning
	 * a new model that is the result of that decoration.
	 * 
	 * @param targetModel The target model to be decorated.
	 * @param sourceModel The source model to use for decorating.
	 * @return A new model that is the result of the decoration process.
	 */
	IModel decorate(IModel targetModel, IModel sourceModel)
}
