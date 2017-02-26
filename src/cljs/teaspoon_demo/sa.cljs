(ns teaspoon-demo.sa
  (:require [teaspoon-demo.model :as m]))

(def cooling-rate 0.003)

(defn acceptance-probability
  [e e' t]
  (if (< e' e)
    1.0
    (js/Math.exp (/ (- e e') t))))

(defn find-solution
  "Given an initial TourManager tm and initial temperature t0 find an optimal
   route for the tour."
  [tm t0]
  (loop [best-tour (m/generate-individual (m/Tour. []) tm (m/number-of-cities tm))
         current-tour best-tour
         temp t0]
    (if (< temp 1)
      best-tour
      (let [u (m/Tour. (:l current-tour))
            p0 (m/int-value (* (rand)
                             (m/get-tour-size u)))
            p1 (m/int-value (* (rand)
                             (m/get-tour-size u)))
            c0 (m/nth-city u p0)
            c1 (m/nth-city u p1)
            u (m/set-city u p0 c1)
            u (m/set-city u p1 c0)
            e (m/get-distance current-tour)
            e' (m/get-distance u)
            current-tour (if (> (acceptance-probability e e' temp)
                                (rand))
                           u
                           current-tour)
            best-tour (if (< (m/get-distance current-tour)
                             (m/get-distance best-tour))
                        current-tour
                        best-tour)]
        (recur best-tour current-tour (* temp (- 1 cooling-rate)))))))
