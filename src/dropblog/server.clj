(ns dropblog.server
  (:require [noir.server :as server]
  					dropblog.post.processor))

(server/load-views "src/dropblog/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'dropblog})))

