package com.nerderg.suggest

import grails.converters.JSON

class SuggestController {

    def suggestService

    def suggest(String subject, String term) {
        List suggestions = suggestService.getSuggestions(subject, term)
        render suggestions as JSON
    }

}
