package simplesuggestions

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

import com.nerderg.suggest.SuggestService

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class SuggestServiceTests {

    private SuggestService suggestService

    void setUp() {
        def grailsApp = [config: [suggest: [data: [directory: './test/unit/simplesuggestions/suggestions']]]]
        suggestService = new SuggestService(grailsApplication: grailsApp)
    }

    void testSuggestion() {
        def handler = { String term ->
            return [term, "$term A", "$term B"]
        }
        suggestService.addSuggestionHandler('test', handler)
        assert suggestService.getSuggestions('test', 'wally') == ['wally', 'wally A', 'wally B']
        assert suggestService.getSuggestions('test', 'hello') == ['hello', 'hello A', 'hello B']
        assert suggestService.getSuggestions('toast', 'hello') == []
        assert suggestService.getSuggestions('titles', 'M') == ['Mr', 'Ms', 'Miss', 'Mrs', 'Master']
        assert suggestService.getSuggestions('titles', 'D') == ['Dr']
    }
}
