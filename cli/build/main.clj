
(ns build.main
  (:require [shadow.cljs.devtools.api :as shadow]))

(defn build []
  (sh! "rm -rf dist/*")
  (shadow/release :module))
