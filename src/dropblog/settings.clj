(ns dropblog.settings)

(def path-to-marked "resources/public/js/node_modules/marked/lib/marked.js")

(def post-processor-frequency (atom 10000))
(def directory-markdown (atom "resources/posts/"))
(def directory-html (atom "resources/posts-html/"))
(def default-author-name (atom "Dropblog"))