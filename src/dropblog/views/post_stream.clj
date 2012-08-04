(ns dropblog.views.post-stream
 	(:require [dropblog.settings :as settings])
	(:use [noir.core :only [defpartial]]
				[hiccup.core :only [html]]
				[clojure.java.io :only [file]]
				[dropblog.post.metadata :only [read-metadata]]
				[dropblog.views.post :only [blog-post]]))

(defn read-blog-post-list-item [file]
	[:li.post (blog-post (.getName file))])

(defn get-post-date [f]
	(let [data (read-metadata f)]
		(data "created")))

(defn get-post-files [posts max-number]
	(let [-posts (take max-number (.listFiles posts))
				-posts (sort #(compare (get-post-date %2) (get-post-date %1)) -posts)]
		-posts))

(defn get-blog-posts [max-number]
	(let [posts-dir (file @settings/directory-html)
				files (get-post-files posts-dir max-number)]
		(if (not (empty? files))
			(conj [:ul#blog-stream] (map read-blog-post-list-item files)))))

(defpartial stream [max-number]
	(html (get-blog-posts max-number)))
