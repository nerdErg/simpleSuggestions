package com.nerderg.simpleSuggestions

import org.apache.commons.io.FileUtils
import org.springframework.core.io.ClassPathResource

/**
 * @author Angel Ruiz (aruizca@gmail.com)
 */
class ClasspathSuggestionLoader implements SuggestionLoader {

    private String classpathRelativeDir

    ClasspathSuggestionLoader(String dir) {
        super()
          this.classpathRelativeDir = dir
    }

    @Override
    List<String> loadAllAvailableSuggestionOptions(String subject) {
        List<String> suggestionOptions = []

        File file = new File(subject)
        classpathRelativeDir = classpathRelativeDir.lastIndexOf("/") == classpathRelativeDir.length() - 1 ? classpathRelativeDir : classpathRelativeDir + "/"
        ClassPathResource classPathResource = new ClassPathResource(classpathRelativeDir + "${subject}.txt")
        if (classPathResource.exists()) {
            FileUtils.copyInputStreamToFile(classPathResource.inputStream, file)

            file.eachLine { line ->
                suggestionOptions.add(line)
            }
        }

        return suggestionOptions
    }
}
