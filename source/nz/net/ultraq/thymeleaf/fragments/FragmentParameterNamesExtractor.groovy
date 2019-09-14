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

package nz.net.ultraq.thymeleaf.fragments

/**
 * Extracts just the parameter names from a fragment definition.  Used for when
 * unnamed fragment parameters need to be mapped to their respective names.
 * 
 * @author Emanuel Rabina
 */
class FragmentParameterNamesExtractor {

	/**
	 * Returns a list of parameter names for the given fragment definition.
	 * 
	 * @param fragmentDefinition
	 * @return A list of the named parameters, in the order they are defined.
	 */
	List<String> extract(String fragmentDefinition) {

		def matcher = fragmentDefinition =~ /.*?\((.*)\)/
		return matcher ?
			matcher[0][1].split(',').collect { parameter ->
				return (parameter =~ /([^=]+)=?.*/)[0][1].trim()
			} : []
	}
}
