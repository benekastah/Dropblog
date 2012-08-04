(ns dropblog.time
  (:require [clj-time.core :as clj-time]
            [clj-time.format :as time-format]
            [clj-time.coerce :as time-coerce]))

(def from-long time-coerce/from-long)
(def to-long time-coerce/to-long)
(def now clj-time/now)
(def date-time clj-time/date-time)

(defmacro build-date-formatter [formatter]
  (let [to-string-sym (symbol (str "to-" (name formatter) "-string"))
        from-string-sym (symbol (str "from-" (name formatter) "-string"))]
    `(let [formatter# (time-format/formatters ~formatter)]
      (defn ~to-string-sym [date#]
          (time-format/unparse formatter# date#))

      (defn ~from-string-sym [date#]
        (time-format/parse formatter# date#)))))

(defmacro build-date-formatters [& formatters]
  (let [stmts (for [f formatters] `(build-date-formatter ~f))]
    `(do ~@stmts)))

(build-date-formatters :date-time :year-month-day)

(def min-in-ms (* 60 1000))
(def hr-in-ms (* 60 min-in-ms))
(def day-in-ms (* 24 hr-in-ms))
(def pretty-date (time-format/formatter "d MMMM, yyyy"))
(defn pretty-print [date]
  (let [l-now (to-long (now))
        l-date (to-long date)
        diff (- l-now l-date)
        pluralize #(if (not= %1 1) "s" "")]
    (if (< diff day-in-ms)
      (let [hrs-since-date (Math/round (float (/ diff hr-in-ms)))
            mins-since-date (Math/round (float (/ diff min-in-ms)))]
        (if (< hrs-since-date 1)
          (if (< mins-since-date 1)
            (str "Less than a minute ago")
            (str mins-since-date " minute" (pluralize mins-since-date) " ago"))
          (str hrs-since-date " hour" (pluralize hrs-since-date) " ago")))
      (time-format/unparse pretty-date date))))

;(time-format/show-formatters)