package simplesuggestions

import grails.test.mixin.TestFor

import com.nerderg.suggest.SuggestController
import com.nerderg.suggest.SuggestService

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(SuggestController)
class SuggestControllerTests {

    void testSuggest() {
        def mockControl = mockFor(SuggestService)
        mockControl.demand.getSuggestions(1..1) { String subject, String term ->
            ['one', 'two']
        }
        controller.suggestService = mockControl.createMock()
        controller.suggest('test', 'blah')
        assert '["one","two"]' == response.text
        assert "two" == response.json[1]
    }
}
