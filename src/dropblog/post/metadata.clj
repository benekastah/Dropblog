(ns dropblog.post.metadata
	(:require [cheshire.core :as json]
						[clojure.string :as string]
						[dropblog.time :as dtime]
						[dropblog.post.io :as post-io]))

(defn read-metadata [f & args]
	(let [no-additions (some #{:no-additions} args)
		  	f (post-io/file f)
		  	md (string/replace (post-io/slurp f) #"\n+" "")
		  	data (nth (re-find #"^<!--(.*)-->" md) 1)
		  	data (json/parse-string data)
		  	{:strs [created]} data
		  	modified (if no-additions (.lastModified f))
		  	created (if created (dtime/from-date-time-string created))
		  	data (merge data 
					  		(if modified {"modified" (dtime/from-long modified)})
								(if created  {"created" created}))]
		data))

(defn stringify [data]
	(str "<!--" (json/generate-string data {:pretty true}) "-->"))

(defn prepend-metadata [post data]
	(loop [txt (string/split post #"\n")
				 i 0]
		(let [line (first txt)]
			(if (or
						(re-find #".*-->" line)
						(and (= i 0) (not (re-find #"<!--" line))))
				(let [-line (string/replace line #".*-->" "")
							-txt (concat [-line] (rest txt))
							-txt (string/join "\n" -txt)
							-txt (str "\n\n" (string/replace -txt #"^\n+" ""))]
					(str (stringify data) -txt))
				(recur (rest txt) (inc i))))))

(defn add-metadata-to-post [f data & args]
	(let [contents (post-io/slurp f)
				prev-data (read-metadata f)
				as-default (some #{:as-default} args)
				more-data (if as-default
										(merge data prev-data)
										(merge prev-data data))
				contents (if (not= more-data prev-data) (prepend-metadata contents more-data))]
		(if contents
			(post-io/spit f contents))))