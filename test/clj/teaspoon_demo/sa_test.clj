(ns teaspoon-demo.sa-test
  (:require [clojure.test :refer [is deftest testing]]
            [teaspoon.core :as tsp]
            [teaspoon.sa :as sa])
  (:import [teaspoon.core City Tour TourManager]))

(deftest sa-test
  (testing "Tests the ability of the simulated annealing algorithm to find a
            solution."
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
          c20 (City. 160 20)
          tm (TourManager. [c1  c2  c3  c4
                            c5  c6  c7  c8
                            c9  c10 c11 c12
                            c13 c14 c15 c16
                            c17 c18 c19 c20])
          random-distance (tsp/get-distance
                           (tsp/generate-individual (Tour. [])
                                                    tm
                                                    (tsp/number-of-cities tm)))
          t (sa/find-solution tm 10000)]
      (println "Found solution" (tsp/get-distance t))
      (is (> random-distance
             (tsp/get-distance t)))
      )))
