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

package com.nerderg.simpleSuggestions

import org.springframework.core.io.ClassPathResource

/**
 * A service to provide suggestions for things. Works with SuggestController.
 *
 * This service lets you provide lists of options to match a users input against as a file or provide a handler closure
 * see <a href="http://nerderg.com/Simple+Suggestions+plugin">nerderg.com</a> for details.
 *
 * You can also set a defaultSuggestionHandler closure that takes a subject and a term string.
 *
 * There are two configuration options you can set in Config.groovy
 *
 * suggest.data.directory = 'where/we/keep/suggestion/files' //relative path from install or in classpath OR and absolute path you choose
 * suggest.data.classpathResource = true/false //set true if you want to put the suggestion files in the classpath
 *
 * @see SuggestController
 */
class SuggestService {

    def grailsApplication

    static transactional = false

    protected final Map<String, Closure> suggestionHandlers = [:]
    protected final Map<String, List<String>> dataMap = [:]

    /**
     * A defaultSuggestionHandler closure is provided but can be replaced by a custom one. The closure must take a subject
     * and term argument and return a List. If there are no suggestions it should return and empty list.
     *
     * It's a good idea to *log* an error if you handler doesn't handle a particular subject and return an empty list.
     */
    Closure defaultSuggestionHandler = {String subject, String term ->
        _defaultSuggestionHandler(subject, term)
    }

    private List<String> loadAllSuggestions(String subject) {

        if (!dataMap[subject]) {
            File file = new File(grailsApplication?.config?.suggest?.data?.directory ?: './suggestions', "${subject}.txt")

            if (grailsApplication?.config?.suggest?.data?.classpathResource) {
                dataMap[subject] = Collections.unmodifiableList(loadSuggestionsFromClasspath(file))
            } else {
                dataMap[subject] = Collections.unmodifiableList(loadSuggestionsFromFile(file))
            }
        }
        return dataMap[subject]
    }

    private List<String> loadSuggestionsFromFile(File file) {
        List<String> suggestionOptions = []

        if (file.exists()) {
            file.eachLine { line ->
                suggestionOptions.add(line)
            }
        }

        return suggestionOptions
    }

    private List<String> loadSuggestionsFromClasspath(File file) {
        List<String> suggestionOptions = []

        ClassPathResource classPathResource = new ClassPathResource(file.path)
        if (classPathResource.exists()) {
            BufferedInputStream bis = new BufferedInputStream(classPathResource.inputStream)

            bis.eachLine { line ->
                suggestionOptions.add(line)
            }
        }

        return suggestionOptions
    }

    /**
     * Add a suggestion handler closure to the suggestion service to handle a particular subject. Your handler should
     * return an empty list if it finds no results.
     *
     * You can supply a handler closure that takes both Subject and Term or just term e.g.
     * <code>
     *     { String subject, String term -> return lookUpMyDB(subject, term) }
     * </code>
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
    List getSuggestions(String subject, String term, Map params) {
        Closure handler = suggestionHandlers[subject]
        if (handler) {
            if(handler.maximumNumberOfParameters == 3) {
                handler(subject, term, params)
            } else if(handler.maximumNumberOfParameters == 2) {
                handler(subject, term)
            } else {
                handler(term)
            }
        } else {
            defaultSuggestionHandler(subject, term)
        }
    }

    /**
     * This default suggestion handler looks to see if there is a text file that matches the subject and loads it to look
     * through the list of items for the term. The term can be anywhere within the string.
     *
     * By design this is simple, and is not an attempt to cover all possible scenarios of how we handle suggestions. (for
     * those we have handlers)
     *
     * This loads all the suggestions into memory, so don't use it for suggesting, say, all the URLs on the Internet.
     *
     * @param subject - the subject to search on, e.g. titles
     * @param term - the query term typed in by the user
     * @return List of Strings
     */
    private List<String> _defaultSuggestionHandler(String subject, String term) {
        List<String> data = loadAllSuggestions(subject)
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
