(ns dropblog.views.blog-stream
	(:require [dropblog.settings :as settings])
	(:use [noir.core :only [defpartial]]
				[hiccup.core :only [html]])
	(:import java.io.File))

(defn read-blog-post [file]
	(slurp (str settings/posts-directory-html file)))

(defn read-blog-post-list-item [file]
	[:li (read-blog-post file)])

(defn get-blog-posts []
	(let [posts-dir (File. settings/posts-directory-html)
				files (take settings/posts-per-page (or (.list posts-dir) []))]
		(if (not (empty? files))
			(conj [:ol] (map read-blog-post-list-item files)))))

(defpartial stream []
	(html (get-blog-posts)))
