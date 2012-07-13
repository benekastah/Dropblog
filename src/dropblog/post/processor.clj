(ns dropblog.post.processor
	(:require [dropblog.settings :as settings]
						[clojure.string :as string]
						[clojure.stacktrace :as stacktrace]
						[dropblog.time :as dtime])
	(:use [dropblog.markdown :only [markdown-to-html]]
				[clojure.java.io :only [file delete-file]]
				[dropblog.post.metadata :only [add-metadata-to-post read-metadata]]))

(defn begins-with-date? [f]
	(not= nil (re-find #"^\d{4}\-\d{2}\-\d{2}" f)))

(defn prepend-date
  ([f]
    (let [metadata (read-metadata f)
          created (metadata "created")]
      (prepend-date (or created (dtime/now)))))
  ([f date]
  	(let [formatted (dtime/to-year-month-day-string date)]
  		(str formatted "-" f))))

(defn get-default-metadata []
	(let [isodate (dtime/to-date-time-string (dtime/now))]
		{"created" isodate}))

(defn render-post [f]
	(add-metadata-to-post f (get-default-metadata) :as-default)
	(let [md (slurp f)
				html (markdown-to-html md)
				prepend-dir #(str settings/posts-directory-html %1)
				new-file (-> f
										 .getName
										 (string/replace #"\.(md|markdown)$" ".html")
										 prepend-dir
										 file)
				new-file-contents (if (.exists new-file) (slurp new-file))]
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
  (Thread/sleep (or settings/check-for-posts-frequency 10000))
	(reduce merge
		(for [f file-list]
			(let [*file (get-markdown-file f)
						fname (.getName *file)
						file-old-mod-date (old-files fname)
						file-mod-date (.lastModified *file)]

				(cond
					(not (contains? old-files fname)) (create-post *file)
					(not= file-mod-date file-old-mod-date) (modify-post *file))
				{fname file-mod-date}))))

(def md-dir (file settings/posts-directory-markdown))
(def html-dir (file settings/posts-directory-html))
(def files (agent {}))
(defn process-posts []
	(try
		(let [current-files (.list md-dir)]
		 	(send files get-file-array current-files)
			(process-posts))
	(catch Exception e (do
		(if -debugging
			(throw e)
			(try (stacktrace/print-throwable e)))
		(process-posts)))))

;; Need to implement suggestion found here: http://stackoverflow.com/a/6408044/777929
(process-posts)
