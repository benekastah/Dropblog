(ns dropblog.markdown
  (:require [dropblog.settings :as settings])
  (:import [com.petebevin.markdown MarkdownProcessor]))

(def md (MarkdownProcessor.))

(defn markdown-to-html [txt]
  (.markdown md txt))