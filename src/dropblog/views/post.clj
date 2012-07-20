(ns dropblog.views.post
	(:require [dropblog.settings :as settings]
						[dropblog.time :as dtime]
						[clojure.string :as string]
						[dropblog.post.io :as post-io])
	(:use [noir.core :only [defpage defpartial]]
				[hiccup.core :only [html]]
				[dropblog.post.metadata :only [read-metadata]]
				[clojure.java.io :only [file]]))

(defpartial byline [data]
	(let [author-info (data "author")
				author-info (if (not (map? author-info)) {"name" author-info} author-info)
				{:strs [name web email]} author-info
				name (or name @settings/default-author-name)
				web-link (fn [url display] [:a {:href url} display])
				email-link (fn [email display] [:a {:href (str "mailto:" email)} display])
				el [:span.author " by "]]
		(cond
			(and web email)
				(conj el name " " (web-link web "web") " | " (email-link email "email"))
			web
				(conj el (web-link web name))
			email
				(conj el (email-link email name))
			:else
				(conj el name))))

(defpartial post-date [data]
	(let [created (dtime/pretty-print (data "created"))
				el [:span.date created]]
			el))

(defpartial permalink [fname]
	(let [[y m d t] (string/split fname #"-" 4)
				t (string/replace t #"\.html$" "")
				href (str "/post/" y "/" m "/" d "/" t)]
		[:a.permalink {:href href} "[permalink]"]))

(defpartial blog-post [fname]
	(let [html (post-io/slurp fname)
				data (read-metadata fname)
				by (byline data)
				date (post-date data)
				plink (permalink fname)]
		[:div date by [:br] plink [:div html]]))

(defn read-blog-post [file]
	(blog-post file))

(defn get-post [year month day title]
	(let [fname (str year "-" month "-" day "-" title ".html")]
		(read-blog-post fname)))
