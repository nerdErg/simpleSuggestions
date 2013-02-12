/*
Copyright 2013 nerdErg Pty Ltd

This file is part of Simple Suggestions plugin.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy
of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.nerderg.suggest

/**
 * A service to provide suggestions for things. Works with SuggestController.
 *
 * This service lets you provide lists of options to match a users input against as a file or provide a handler closure
 * see <a href="http://nerderg.com/Simple+Suggestions+plugin">nerderg.com</a> for details.
 *
 * @see SuggestController
 */
class SuggestService {

    def grailsApplication

    static transactional = false

    protected Map<String, Closure> suggestionHandlers = [:]
    protected Map<String, List<String>> dataMap = [:]

    private List<String> loadData(String name) {
        if (!dataMap[name]) {
            List<String> data = []
            String dataDir = grailsApplication.config.suggest.data.directory ?: './suggestions'
            File file = new File(dataDir, "${name}.txt")
            if (file.exists()) {
                file.eachLine { line ->
                    data.add(line)
                }
                dataMap[name] = Collections.unmodifiableList(data)
            }
        }
        return dataMap[name]
    }

    /**
     * Add a suggestion hander closure to the suggestion service to handle a particular subject. Your handler should
     * return an empty list if it finds no results.
     *
     * @param subject - the subject that this handler will respond to
     * @param handler - closure that returns a list of results that get encoded as JSON and sent back to the caller, normally
     * just Strings
     */
    void addSuggestionHandler(String subject, Closure handler) {
        suggestionHandlers.put(subject, handler)
    }

    /**
     * Get a list of suggestions on the provided subject. If there is no handler found for the subject we log an error,
     * but return no results (empty list)
     *
     * @param subject - the subject to search on, e.g. titles
     * @param term - the query term typed in by the user
     * @return List of Objects, normally Strings
     */
    List getSuggestions(String subject, String term) {
        if (suggestionHandlers[subject]) {
            suggestionHandlers[subject](term)
        } else {
            defaultSuggestionHandler(subject, term)
        }
    }

    /**
     * This default suggestion handler looks to see if there is a text file that matches the subject and loads it to look
     * through the list of items for the term. The term can be anywhere within the string.
     *
     * @param subject - the subject to search on, e.g. titles
     * @param term - the query term typed in by the user
     * @return List of Strings
     */
    List<String> defaultSuggestionHandler(String subject, String term) {
        List<String> data = loadData(subject)
        if (data) {
            simpleSearchList(data, term)
        } else {
            log.error("Suggestion search subject $subject not found. Do you need to configure a handler?")
            return []
        }
    }

    private static List<String> simpleSearchList(List<String> list, String term) {
        String q = term.toUpperCase()
        list.findAll { String item -> item.toUpperCase().startsWith(q) }
    }

}
