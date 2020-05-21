(ns core
  (:require ["net" :as net]
            [mayu.attach :refer [attach]]
            [mayu.dom :as dom]
            [mayu.frp.event :as e]
            [mayu.frp.signal :as s]
            [mayu.macros :refer [defui]]))

(def e-key-down (e/on! (e/Event)))

(defn onKeyDown [e]
  (e/push! e-key-down e))

(defui ui []
  {:keys [js-on js-off e-tcp]} <- dom/env
  let [e-message (->> e-tcp
                      (e/map js/JSON.parse)
                      (e/map #(js->clj % :keywordize-keys true)))
       reducer #(assoc-in %1 (butlast %2) (last %2))]
  <[dom/collect-and-reduce ::state reducer {:open? false} $=
    (->> e-message
         (e/filter #(= "open" (:action %)))
         (e/map #(-> [:open? true]))
         (dom/emit ::state))
    (->> e-key-down
         (e/map #(aget % "which"))
         (e/filter #(= % 27))
         (e/map #(-> [:open? false]))
         (dom/emit ::state))
    s-state <- (dom/envs ::state)
    s-open? <- (s/map :open? s-state)
    <[dom/bind s-open? $[open?]=
      [((if open? js-on js-off))]]
    <[dom/bind s-state $[{:keys [open?]}]=
      <[when open?
        <[div {:style {:font-size "50px"}}
          "Hello From Mayu!"]]]])

(defn init [el js-on js-off]
  (let [e-tcp (e/on! (e/Event))
        ^js server (net/createServer (fn [^js socket]
                                       (.setEncoding socket "utf8")
                                       (.on socket "data" #(e/push! e-tcp %))))]
    (.listen server 8292 "127.0.0.1")
    (:off (attach el {:js-on js-on
                      :js-off js-off
                      :e-tcp e-tcp} ui))))

