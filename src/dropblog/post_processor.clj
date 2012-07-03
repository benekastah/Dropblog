(ns dropblog.post-processor
	(:require [dropblog.settings :as settings]
						[clojure.string :as string]
						[clojure.stacktrace :as stacktrace])
	(:use [dropblog.markdown :only [markdown-to-html]]
				[clojure.java.io :only [file delete-file]])
	(:import [java.io File]
					 [java.util Date]
					 [java.text SimpleDateFormat]))

(defn begins-with-date? [f]
	(not= nil (re-find #"^\d{4}\-\d{2}\-\d{2}T\d{2}:\d{2}:\d{2}\-\d+" f)))

(defn prepend-date [f]
	(let [now (Date.)
		  	fmt (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ssZ")
        formatted (.format fmt now)]
		(str formatted "-" f)))

(defn render-post [f]
	(let [md (slurp f)
				html (markdown-to-html md)
				prepend-dir #(str settings/posts-directory-html %1)
				new-file (-> f
										 .getName
										 (string/replace #"\.(md|markdown)$" ".html")
										 prepend-dir)]
		(spit new-file html)))

(defn create-post [f]
	(prn :create-post)
	(render-post f))

(defn modify-post [f]
	(prn :modify-post)
	(render-post f))

(defn get-markdown-file [f]
	(let [*file (file (str settings/posts-directory-markdown f))]
		(if (begins-with-date? f)
			*file
			(let [file-contents (slurp *file)
						date-name (prepend-date f)
						new-fname (str settings/posts-directory-markdown date-name)]
				(prn :new-fname new-fname)
				(delete-file *file)
				(spit new-fname file-contents)
				(get-markdown-file date-name)))))

(defn get-file-array [old-files file-list]
	(for [f file-list]
		(let [*file (get-markdown-file f)
					file-info (first
											(filter
												#(= (first %1) f)
												old-files))
					file-old-mod-date (or (nth file-info 1) 0)
					file-mod-date (.lastModified *file)]

			(cond
				(not file-info) (create-post *file)
				(not= file-mod-date file-old-mod-date) (modify-post *file))
			[f file-mod-date])))

(def files (atom []))
(defn process-posts []
	(try
		(let [md-dir (file settings/posts-directory-markdown)
						html-dir (file settings/posts-directory-html)
						old-file-list @files]
				(swap! files get-file-array (.list md-dir)))
			(Thread/sleep settings/check-for-posts-frequency)
			(process-posts)
		(catch Exception e (stacktrace/e))

(pcalls process-posts)