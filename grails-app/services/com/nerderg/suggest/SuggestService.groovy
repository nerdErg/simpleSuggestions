package com.nerderg.suggest

import javax.naming.Context
import javax.naming.InitialContext

/**
 * A service to provide suggestions for things. Works with SuggestController
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

    void addSuggestionHandler(String subject, Closure handler) {
        suggestionHandlers.put(subject, handler)
    }

    List getSuggestions(String subject, String term) {
        if (suggestionHandlers[subject]) {
            suggestionHandlers[subject](term)
        } else {
            defaultSuggestionHandler(subject, term)
        }
    }

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
