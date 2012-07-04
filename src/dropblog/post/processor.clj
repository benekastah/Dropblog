(ns dropblog.post.processor
	(:require [dropblog.settings :as settings]
						[clojure.string :as string]
						[clojure.stacktrace :as stacktrace]
						[clj-time.core :as clj-time]
						[clj-time.format :as time-format])
	(:use [dropblog.markdown :only [markdown-to-html]]
				[clojure.java.io :only [file delete-file]]
				[dropblog.post.metadata :only [add-metadata-to-post]]))

(defn begins-with-date? [f]
	(not= nil (re-find #"^\d{4}\-\d{2}\-\d{2}" f)))

(defn prepend-date [f]
	(let [formatted (time-format/unparse
										(time-format/formatters :year-month-day)
										(clj-time/now))]
		(str formatted "-" f)))

(defn get-default-metadata []
	(let [isodate (time-format/unparse
									(time-format/formatters :date-time)
									(clj-time/now))]
		{"created" isodate}))

(defn render-post [f]
	(add-metadata-to-post f (get-default-metadata) :as-default)
	(let [md (slurp f)
				html (markdown-to-html md)
				prepend-dir #(str settings/posts-directory-html %1)
				new-file (-> f
										 .getName
										 (string/replace #"\.(md|markdown)$" ".html")
										 prepend-dir)
				new-file-contents (slurp new-file)]
		(if (not= html new-file-contents)
			(spit new-file html))))

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
				(spit new-fname file-contents)
				(delete-file *file)
				(get-markdown-file date-name)))))

(defn get-file-array [old-files file-list]
	(reduce merge (for [f file-list]
		(let [*file (get-markdown-file f)
					fname (.getName *file)
					file-old-mod-date (old-files fname)
					file-mod-date (.lastModified *file)]

			(cond
				(not (contains? old-files fname)) (create-post *file)
				(not= file-mod-date file-old-mod-date) (modify-post *file))
			{fname file-mod-date}))))

(defmacro sleep []
	`(Thread/sleep (or settings/check-for-posts-frequency 10000)))

(def md-dir (file settings/posts-directory-markdown))
(def html-dir (file settings/posts-directory-html))
(defn process-posts 
	([] (process-posts {}))
	([files]
		(sleep)
		(prn :files files)
		(try
			(let [current-files (.list md-dir)
						new-files (get-file-array files current-files)]
				(process-posts new-files))
		(catch Exception e (do
			(try (stacktrace/print-throwable e))
			(process-posts))))))

;; Launch this process in another thread.
(.start (Thread. process-posts))

