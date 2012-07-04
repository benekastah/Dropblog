(ns dropblog.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css html5 include-js]]))

(defpartial layout [& content]
	(html5
		[:head
			[:title "dropblog"]
			(include-css ;"/css/reset.css"
									 "/css/c/main.css")

			(include-js "//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"
									"/js/node_modules/underscore/underscore-min.js"
									"/js/c/main.js")]
		[:body
			[:div#wrapper content]]))
