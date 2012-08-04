(ns dropblog.core
  (:require [noir.server :as server]
            [dropblog.settings :as settings]
            [dropblog.post.io :as post-io])
  (:use [dropblog.post.processor :only [process-posts]]))

;(server/load-views "src/dropblog/views/")

; (defn -main [& m]
;   (let [mode (keyword (or (first m) :dev))
;         port (Integer. (get (System/getenv) "PORT" "8080"))]
;     (server/start port {:mode mode
;                         :ns 'dropblog})))

(defn -main [& m]
  (require 'dropblog.post.processor))

(defn initialize [& args]
  (let [{:keys [post-processor-frequency directory-markdown directory-html default-author-name]} (apply hash-map args)]
    (if post-processor-frequency
      (reset! settings/post-processor-frequency post-processor-frequency))
    (if directory-markdown
      (reset! settings/directory-markdown directory-markdown))
    (if directory-html
      (reset! settings/directory-html directory-html))
    (if default-author-name
      (reset! settings/default-author-name default-author-name)))
  (println "Watching for new posts...")
  (process-posts))