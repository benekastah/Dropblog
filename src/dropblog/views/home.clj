(ns dropblog.views.home
	(:require [dropblog.views [common :as common]
														[blog-stream :as blog-stream]])
	(:use [noir.core :only [defpage]]
				[hiccup.core :only [html]]))

(defpage "/" []
	(common/layout
		[:h1 "Blog"]
		(blog-stream/stream)))
