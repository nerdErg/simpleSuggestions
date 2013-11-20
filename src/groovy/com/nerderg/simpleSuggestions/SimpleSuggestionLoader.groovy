package com.nerderg.simpleSuggestions

/**
 * @author Angel Ruiz (aruizca@gmail.com)
 */
class SimpleSuggestionLoader implements SuggestionLoader {

    private String suggestionFileDir

    SimpleSuggestionLoader(String dir) {
        super()
        this.suggestionFileDir = dir ? dir : './suggestions'
    }

    @Override
    List<String> loadAllAvailableSuggestionOptions(String subject) {
        List<String> suggestionOptions = []

        File file = new File(suggestionFileDir, "${subject}.txt")
        if (file.exists()) {
            file.eachLine { line ->
                suggestionOptions.add(line)
            }
        }

        return suggestionOptions
    }
}
