(ns dropblog.post.processor
	(:require [dropblog.settings :as settings]
						[clojure.string :as string]
						[clojure.stacktrace :as stacktrace]
						[dropblog.time :as dtime]
						[dropblog.post.io :as post-io])
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
 	(prn :render-post f)
	(add-metadata-to-post f (get-default-metadata) :as-default)
	(let [md (post-io/slurp f)
				html (markdown-to-html md)
				prepend-dir #(str @settings/directory-html %1)
				new-file (-> f
										 .getName
										 (string/replace #"\.(md|markdown)$" ".html")
										 prepend-dir
										 file)
				new-file-contents (if (.exists new-file) (post-io/slurp new-file))]
		(if (not= html new-file-contents)
			(post-io/spit new-file html))))

(defn get-markdown-file [f]
	(let [*file (post-io/file f)]
		(if (begins-with-date? f)
			*file
			(let [file-contents (post-io/slurp *file)
						date-name (prepend-date f)
						new-fname (str @settings/directory-markdown date-name)]
				(post-io/spit new-fname file-contents)
				(delete-file *file)
				(get-markdown-file date-name)))))

(defn get-file-array [old-files file-list]
	(reduce merge
		(for [f file-list]
			(let [*file (get-markdown-file f)
						fname (.getName *file)
						file-old-mod-date (old-files fname)
						file-mod-date (.lastModified *file)]

				(if (or (not (contains? old-files fname)) (not= file-mod-date file-old-mod-date))
				 	(render-post *file))
				{fname file-mod-date}))))

(def md-dir (file @settings/directory-markdown))
(def html-dir (file @settings/directory-html))
(def files (atom {}))
(defn- process-posts-loop []
  (Thread/sleep @settings/post-processor-frequency)
	(swap! files get-file-array (.list md-dir))
  (recur))

(defn process-posts [& args]
 	(.start (Thread. process-posts-loop)))

