/*
 * Copyright 2019, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.layoutdialect.models

import org.thymeleaf.model.IModel
import org.thymeleaf.model.ITemplateEvent

/**
 * An iterator that treats a model as a queue of events.
 *
 * @author Emanuel Rabina
 */
class EventIterator implements Iterator<ITemplateEvent> {

	private final IModel model
	private int currentIndex = 0

	EventIterator(IModel model) {
		this.model = model
	}

	@Override
	boolean hasNext() {
		return currentIndex < model.size()
	}

	@Override
	ITemplateEvent next() {
		return model.get(currentIndex++)
	}
}
