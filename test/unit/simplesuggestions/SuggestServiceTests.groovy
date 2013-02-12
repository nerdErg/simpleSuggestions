package simplesuggestions

import com.nerderg.suggest.SuggestService

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class SuggestServiceTests {

    SuggestService suggestService

    void setUp() {
        def grailsApp = [config: [suggest: [data: [directory: './test/unit/simplesuggestions/suggestions']]]]
        suggestService = new SuggestService(grailsApplication: grailsApp)
    }

    void tearDown() {
        // Tear down logic here
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
