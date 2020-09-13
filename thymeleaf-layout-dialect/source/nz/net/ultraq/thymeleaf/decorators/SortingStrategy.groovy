/* 
 * Copyright 2015, Emanuel Rabina (http://www.ultraq.net.nz/)
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
 * Interface for controlling the sort order in which {@code <head>} elements
 * from one source are placed into another.
 * 
 * @author Emanuel Rabina
 */
interface SortingStrategy {

	/**
	 * Returns the position in a {@code <head>} element model to insert a child
	 * model.
	 * 
	 * @param headModel  Model of a {@code <head>} element.
	 * @param childModel A model that can be found in a {@code <head>} element.
	 * @return Position to insert the child model into.
	 */
	int findPositionForModel(IModel headModel, IModel childModel)
}
