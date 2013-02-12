class SimpleSuggestionsGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.1 > *"

    def title = "nerdErg Simple Suggest Plugin"
    def author = "Peter McNeil"
    def authorEmail = "peter@nerderg.com"
    def description = '''\
This is a simple, by convention, suggestion service to provide suggestions to auto complete controls.

Just point the auto complete JS URL at suggest/[subject]?term=bla and you get a JSON list of suggestion strings back.

You can add suggestion handlers to the service as a closure, or just add a text file named [subject].txt with an item\
per line to be searched for matches. The simple search returns a result if an item string contains the term anywhere.

See docs for details.
'''

    def documentation = "http://nerderg.com/Simple+Suggestions+plugin"

    def license = "APACHE"
    def organization = [ name: "nerdErg Pty Ltd", url: "http://www.nerderg.com/" ]
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]
    def scm = [ url: "https://github.com/nerdErg/simpleSuggestions" ]
}
