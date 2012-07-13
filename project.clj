(defproject dropblog "0.1.0-SNAPSHOT"
	:description "FIXME: write this!"
	:dependencies [	[org.clojure/clojure "1.4.0"]
									[noir "1.2.1"]
									;[rhino/js "1.7R2"]
                  [org.markdownj/markdownj "0.3.0-1.0.2b4"]
									[cheshire "4.0.0"]
									[clj-time "0.4.3"]
									[org.clojure/tools.cli "0.2.1"]]
	:main dropblog.server)