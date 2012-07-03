(ns dropblog.markdown
	(:require [dropblog.settings :as settings])
	(:import [org.mozilla.javascript Context ScriptableObject]))

(defn markdown-to-html [txt]
	(let [cx (Context/enter)
				scope (.initStandardObjects cx)
				input (Context/javaToJS txt scope)
				script (str (slurp 	settings/path-to-marked)
														";marked(input);")]
		(try
			(ScriptableObject/putProperty scope "input" input)
			(let [result (.evaluateString cx scope script "<cmd>" 1 nil)]
				(Context/toString result))
			(finally (Context/exit)))))