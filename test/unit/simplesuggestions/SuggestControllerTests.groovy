package simplesuggestions

import com.nerderg.suggest.SuggestController
import com.nerderg.suggest.SuggestService
import grails.test.mixin.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(SuggestController)
class SuggestControllerTests {

    void setUp() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void testSomething() {
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
