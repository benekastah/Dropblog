(ns dropblog.views.blog-stream
	(:require [dropblog.settings :as settings])
	(:use [noir.core :only [defpartial]]
				[hiccup.core :only [html]]
				[clojure.java.io :only [file]]
				[dropblog.post.metadata :only [read-metadata]]
				[dropblog.views.single-post :only [blog-post]])
	(:import java.io.File))

(defn read-blog-post-list-item [file]
	[:li.post (blog-post file) [:hr]])

(defn get-post-date [f]
	(let [data (read-metadata f)]
		(data "created")))

(defn get-post-files [posts]
	(let [-posts (take settings/posts-per-page (.listFiles posts))
				-posts (sort #(compare (get-post-date %1) (get-post-date %2)) -posts)]
		-posts))

(defn get-blog-posts []
	(let [posts-dir (file settings/posts-directory-html)
				files (get-post-files posts-dir)]
		(if (not (empty? files))
			(conj [:ol#blog-stream] (map read-blog-post-list-item files)))))

(defpartial stream []
	(html (get-blog-posts)))
