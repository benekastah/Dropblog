(ns dropblog.post.io
 (:require [clojure.java.io :as io]
           [dropblog.settings :as settings]))

(defn get-post-name [p]
 (if (string? p)
  p
  (.getName p)))

(defn get-post-path [pname]
 (let [is-html (re-find #"\.html$" pname)
       is-markdown (re-find #"\.(md|markdown)$" pname)
       prefix (cond
               is-html @settings/directory-html
               is-markdown @settings/directory-markdown
               :else "")]
  (str prefix pname)))

(defn file [f]
 (let [-f (io/file f)]
  (if (.exists -f)
   -f
   (io/file (get-post-path f)))))

(defmacro file-operation [post args & body]
 `(let [post-name# (get-post-name ~post)
        post-agent# (agent nil)
        post-path# (get-post-path post-name#)]
    (send post-agent# (fn [_#]
                        (let [~args [post-path#]]
                          ~@body)))
    (await post-agent#)
    (agent-error post-agent#)
    @post-agent#))

(defn slurp [post]
 (file-operation post [post-path]
  (clojure.core/slurp post-path)))

(defn spit [post c]
 (file-operation post [post-path]
  (clojure.core/spit post-path c)))