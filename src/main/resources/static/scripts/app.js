var app = angular.module("app", [ 'pascalprecht.translate' ]);

app.config(function($locationProvider) {
	$locationProvider.html5Mode({
		enabled : true,
		requireBase : false
	})
});


app.config(function($translateProvider) {
	$translateProvider
    .useStaticFilesLoader({ // load our locales
        prefix: 'strings/',
        suffix: '.json'
    })
    .useSanitizeValueStrategy('escape')
    .registerAvailableLanguageKeys(['ja', 'en'])
    .determinePreferredLanguage(function () { // choose the best language based on browser languages
        var translationKeys = $translateProvider.registerAvailableLanguageKeys(),
            browserKeys = navigator.languages,
            preferredLanguage;

        label: for (var i = 0; i < browserKeys.length; i++) {
            for (var j = 0; j < translationKeys.length; j++) {
                if (browserKeys[i] == translationKeys[j]) {
                    preferredLanguage = browserKeys[i];
                    break label;
                }
            }
        }
        return preferredLanguage;
	});
});


app.factory('$req', function($http, $location) {
	var url = $location.absUrl();
	return {
		searchInvoices : function(predicates) {
			return $http.get(url + "/invoice/search", { params : predicates } );
		},
		downloadInvoice: function(docId) {
			return $http.get(url + "/invoice/download", { params : {id : docId} });
		},
		test: function() {
			return $http.get(url + "test");
		}
	}
});
