(ns dropblog.views.single-post
	(:require [dropblog.views [common :as common]])
	(:use [noir.core :only [defpage]]
				[hiccup.core :only [html]]
				[dropblog.views.blog-stream :only [read-blog-post]])
	(:import java.io.File))

(defn get-post [year month day title]
	(let [fname (str year "-" month "-" day "-" title ".html")]
		(read-blog-post fname)))

(defpage "/post/:year/:month/:day/:title" {:keys [year month day title]}
	(common/layout
		(get-post year month day title)))
