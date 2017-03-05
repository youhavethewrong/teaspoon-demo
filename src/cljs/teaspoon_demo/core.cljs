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

(def t0 1000000)

(defn log-tour
  [tour]
  (cons
   (str "Final distance of " (m/get-distance tour)
        " units over "
        (m/get-tour-size tour)  " cities.\n"
        "Tour order is:\n")
   (map
    (fn [{:keys [x y]}]
      (str "City at (" x ", " y ").\n"))
    (:l tour))))

(rf/reg-event-db
 :load-db
 (fn [_ [_ _]]
   {:tour-manager tour-manager
    :spinner false
    :tour nil}))

(rf/reg-event-db
 :run-sim
 (fn [db [_ _]]
   (let [solution (sa/find-solution tour-manager t0)]
     (assoc db :tour solution :spinner false))))

(rf/reg-event-db
 :spinner-on
 (fn [db [_ _]]
   (assoc db :spinner true)))

(rf/reg-sub
  :spinner
  (fn [db _]
    (:spinner db)))

(rf/reg-sub
  :tour
  (fn [db _]
    (:tour db)))

(defn title
  []
  [:h2 "Simulated annealing solution for 20 city tour."])

(defn city-pixel
  [{:keys [x y] :as city}]
  ^{:key (str x y)} [:rect {:x x :y y :width 2 :height 2 :stroke "red" :stroke-width 1}])

(defn canvas
  []
  (when-let [r @(rf/subscribe [:tour])]
    [:div
     [:p "Cities arranged on a 204x204 px grid."]
     [:div
      [:svg {:style {:width 204 :height 204}}
       (map
        city-pixel
        (:l r))]
      [:h4 "Tour log"]
      [:pre (log-tour r)]]]))

(defn spinner
  []
  (when-let [r @(rf/subscribe [:spinner])]
    (when r
      [:h3 "Running simulation..."])))

(defn control-panel
  []
  [:input
   {:type "button"
    :value "GO!"
    :on-click  (fn [_]
                 (rf/dispatch-sync [:spinner-on])
                 (rf/dispatch [:run-sim])) }])

(defn demo
  []
  [:div
   [title]
   [control-panel]
   [canvas]
   [spinner]
   ])

(defn mount-root
  []
  (rf/dispatch-sync [:load-db])
  (reagent/render [demo] (.getElementById js/document "app")))

(defn init!
  []
  (mount-root))
