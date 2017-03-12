(ns teaspoon-demo.core
  (:require
   [clojure.string :as str]
   [reagent.core :as reagent]
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
  [:div.span8.offset2
   [:h2 "Traveling Salesperson, Twenty City Tour"]])

(defn city-pixel
  [{:keys [x y] :as city}]
  ^{:key (str x y)} [:rect {:x (dec x) :y (dec y)
                            :width 3 :height 3
                            :stroke "blue" :stroke-width 1}])

(defn tour-path
  [tour]
  [:polyline {:fill "none" :stroke "orange"
              :points (str/join " " (map
                                     (fn [{:keys [x y]}] (str x "," y))
                                     tour))}])

(defn render-svg
  []
  (if-let [r @(rf/subscribe [:tour])]
    [:svg {:style {:width 204 :height 204}}
     (map city-pixel (:l r))
     (tour-path (:l r))]
    [:svg {:style {:width 204 :height 204}}]))

(defn display
  []
  [:div
   [:div.row-fluid
    [:div
     [:div.span4.offset2 (render-svg)]
     [:div.span6
      [:p "Twenty cities arranged on a 204x204 px grid."]]]
    [:div.span4
     (let [r @(rf/subscribe [:spinner])]
       (if r
         [:div.span4 "Running simulation..."]
         [:div.span4 "Ready."]))
     [:input
      {:type "button"
       :value "GO!"
       :on-click  (fn [_]
                    (rf/dispatch-sync [:spinner-on])
                    (rf/dispatch [:run-sim])) }]]]
   [:div.span6.offset3
    (when-let [r @(rf/subscribe [:tour])]
      [:p "Final distance was "
       (m/get-distance r)
       " units."])]])

(defn branding
  []
  [:div.navbar.navbar-fixed-top.brand
   [:div.navbar-inner
    [:div.container
     [:div.brand {:id :headline}
      [:a {:href "https://ecik.youhavethewrong.info/blog"}
       "YouHaveTheWrong.info"]]]]])

(defn demo
  []
  [:div
   [branding]
   [:div {:id :content :class :container}
    [title]
    [display]]])

(defn mount-root
  []
  (rf/dispatch-sync [:load-db])
  (reagent/render [demo] (.getElementById js/document "app")))

(defn init!
  []
  (mount-root))
