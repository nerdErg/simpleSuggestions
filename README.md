#Grails Simple Suggestions Plugin

The Simple suggestions plugin provides a very simple service for providing suggestions to autocomplete widgets in your grails application.

It provides a suggest service that you can access at suggest/[subject]?term=blah, Where [subject] is the topic that needs a suggestion
 and the term "blah" is what has been entered by the user.

So if you have the jQuery and jQuery-UI plugins installed you could add Javascript code like this:

    $(".suggest").each(function () {
        var action = $(this).attr("class").split(" ")[1];
        var actionurl = baseContextPath + '/suggest/' + action;
        $(this).autocomplete({
            minLength:1,
            source:actionurl,
            select:function (event, ui) {
                $(this).val(ui.item.value);
            }
        });
    });

then just add a class to your input element like this:

    <input type="text" class="suggest title" size="40"/>

to get suggestions for a title.

The plugin provides a suggestService to provide the suggestions, all you need to do is place a directory in your project called
"suggestions" which contains text files named after the subject with a .txt extension [subject].txt.

So for the title input above we'd put a title.txt file in the suggestions directory. that file would look something like this:

    Mr
    Ms
    Miss
    Mrs
    Master
    Dr
    Professor
    Sir

And that's it, your input box will have suggestions served up.

You can configure where the suggestion files live by setting it in config.groovy like so:

    suggest.data.directory = './mySuggestions'

The directory is relative to the root directory of your app unless you make it an absolute path.

You can put the suggestion files in you Classpath if you like by setting the directory relative to your Classpath, and
setting the classpathResource option like this:

    suggest.data.directory = 'mySuggestions'
    suggest.data.classpathResource = true

*NOTE* suggestion files are loaded into memory once requested, and stay there for the life of the application, so bear that
in mind with large data sets, you may want to provide a handler that accesses a database.

If you need to provide more complex suggestions, perhaps using a database then just add a suggestion handler to the suggestService.
For example:

       def handler = { String term ->
            //do something interesting here, perhaps call another service
            //the handler has to return a list of things, normally Strings
           return [term, "$term A", "$term B"]
       }
       suggestService.addSuggestionHandler('test', handler)

OR you can take both the subject and term in your handler like this:

       def handler = { String subject, String term ->
            //do something interesting here, perhaps call another service
            //the handler has to return a list of things, normally Strings
           return [term, "$term A", "$term B", subject]
       }
       suggestService.addSuggestionHandler('test', handler)

Note you still have to add the handler for all the subjects you expect... unless you replace the defaultSuggestionHandler
on the suggestService. To do that just set it in you bootstrap (or anywhere you like really) like this:

       def suggestService

       ...

       MyUBeautSuggestionHandler mubsh = MyUBeautSuggestionHandlerFactory.getMeOneOfThoseLittleBeauties(...)

       suggestService.defaultSuggestionHandler =  { String subject, String term ->
           mubsh(subject, term)
       }

The resultant list of things will be sent back as JSON, so if you send back a list of Maps or Objects they'll be JSONified
and sent to the caller, so you can do what you like with it.

If you are going to replace the defaultSuggestionHandler it's best to log an error if you get a subject you don't know about.
Just sayin' :-)

That simple.