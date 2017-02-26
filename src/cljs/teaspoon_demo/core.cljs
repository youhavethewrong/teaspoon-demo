(ns teaspoon-demo.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [cljsjs.react]
            [teaspoon-demo.model :refer [City TourManager] :as m]
            [teaspoon-demo.sa :as sa]))

(enable-console-print!)

(def tour-manager
  (let [c1 (City. 60 200)
        c2 (City. 180 200)
        c3 (City. 80 180)
        c4 (City. 140 180)
        c5 (City. 20 160)
        c6 (City. 100 160)
        c7 (City. 200 160)
        c8 (City. 140 140)
        c9 (City. 40 120)
        c10 (City. 100 120)
        c11 (City. 180 100)
        c12 (City. 60 80)
        c13 (City. 120 80)
        c14 (City. 180 60)
        c15 (City. 20 40)
        c16 (City. 100 40)
        c17 (City. 200 40)
        c18 (City. 20 20)
        c19 (City. 60 20)
        c20 (City. 160 20)]
    (TourManager. [c1  c2  c3  c4
                   c5  c6  c7  c8
                   c9  c10 c11 c12
                   c13 c14 c15 c16
                   c17 c18 c19 c20])))

(defn log-tour
  [tour]
  (cons
   (str "Final distance of " (m/get-distance tour)
        " units over "
        (m/get-tour-size tour)  " cities.\n")
   (map
    (fn [{:keys [x y]}]
      (str "City " x " " y ".\n"))
    (:l tour))))

;; (sa/find-solution tour-manager n)


(rf/reg-event-db
 :load-db
 (fn [_ [_ _]]
   {:tour-manager tour-manager
    :tour nil}))

(rf/reg-event-db
 :run-sim
 (fn [db [_ _]]
   (let [solution (sa/find-solution tour-manager 100000)]
     (assoc db :tour solution))))

(rf/reg-sub
  :tour
  (fn [db _]
    (:tour db)))

(defn title
  []
  [:div
   "Simulated annealing solution for 20 city tour."])

(defn canvas
  []
  (when-let [r @(rf/subscribe [:tour])]
    [:pre (log-tour r)]))

(defn control-panel
  []
  [:input
   {:type "button"
    :value "GO!"
    :on-click  #(rf/dispatch [:run-sim])}])

(defn demo
  []
  [:div
   [title]
   [canvas]
   [control-panel]])

(defn mount-root
  []
  (rf/dispatch-sync [:load-db])
  (reagent/render [demo] (.getElementById js/document "app")))

(defn init!
  []
  (mount-root))


(comment

  (defn init-canvas [canvas]
  (let [ctx (.getContext canvas "2d")
        width (.-width canvas)
        height (.-height canvas)]
    (do
      (.clearRect ctx 0 0 width height)
      (set! (. ctx -lineWidth) 5)
      (.beginPath ctx)
      (.moveTo ctx 0 0)
      (.lineTo ctx 0 height)
      (.lineTo ctx width height)
      (.lineTo ctx width 0)
      (.lineTo ctx 0 0)
      (.stroke ctx)))))
