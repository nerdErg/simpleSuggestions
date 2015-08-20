package simplesuggestions

import grails.test.mixin.TestFor

import com.nerderg.simpleSuggestions.SuggestController
import com.nerderg.simpleSuggestions.SuggestService

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(SuggestController)
class SuggestControllerTests {

    void testSuggest() {
        def mockControl = mockFor(SuggestService)
        mockControl.demand.getSuggestions(1..1) { String subject, String term, Map params ->
            ['one', 'two']
        }
        controller.suggestService = mockControl.createMock()
        controller.suggest('test', 'blah')
        assert '["one","two"]' == response.text
        assert "two" == response.json[1]
    }
}
