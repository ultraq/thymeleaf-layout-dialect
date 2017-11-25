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

package nz.net.ultraq.thymeleaf.models

import org.thymeleaf.model.IModel

/**
 * Merges template models by applying the source model to the target model, with
 * the result being implementation-dependant.
 * 
 * @author Emanuel Rabina
 */
interface ModelMerger {

	/**
	 * Merge the source model into the target model.
	 * 
	 * @param targetModel
	 * @param sourceModel
	 * @return The result of the merge.
	 */
	IModel merge(IModel targetModel, IModel sourceModel)
}
