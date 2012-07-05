(ns dropblog.settings)

(def check-for-posts-frequency 15000)

;; For now, do not change these two settings unless you intend to set things up manually
;; This is because the bin/setup does not look at any settings in this file yet
(def posts-directory-markdown "resources/posts/")
(def posts-directory-html "resources/posts-html/")

(def posts-per-page 50)

(def path-to-marked "resources/public/js/node_modules/marked/lib/marked.js")

(def default-author-name "Dropblog")