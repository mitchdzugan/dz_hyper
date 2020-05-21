(ns core
  (:require [mayu.macros :refer [defui]]
            [mayu.dom :as dom]
            [mayu.attach :refer [attach]]))

(defui ui []
  <[div {:style {:font-size "50px"}}
    "Hello From Mayu!"])

(defn init [el]
  (:off (attach el {} ui)))

