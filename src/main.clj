(ns main
  "NoC - Main entry point."
  (:gen-class)
  ;; (:use (incanter core stats processing)
  ;;       noc.core)
  (:require [noc.bell-curve :as bell-curve]))

;;; main entry point
(defn -main [& args]
  "Runs NoC sample programs."
  (bell-curve/run-program))
