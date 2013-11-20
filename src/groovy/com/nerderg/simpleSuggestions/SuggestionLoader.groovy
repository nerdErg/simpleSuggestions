package com.nerderg.simpleSuggestions

interface SuggestionLoader {

    /**
     * Search all the available options for a particular subject
     * @param subject
     * @return List with
     */
    List<String> loadAllAvailableSuggestionOptions(String subject)
}

