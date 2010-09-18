(ns noc.core
  "NoC - common functions."
  (:use (incanter core stats processing)
        ;; (clojure.contrib.math)
        )
  )

(def dbg false)
(def size-default  '[200 200])
(def system-type :mac)
(def system-parameters {:pc  {:size-adj-w  8 :size-adj-h 30}
                        :mac {:size-adj-w  0 :size-adj-h 22}})

(defn size-adj-w []
  (let [[w h] size-default]
    (+ w ((system-parameters system-type) :size-adj-w))))

(defn size-adj-h []
  (let [[w h] size-default]
    (+ h ((system-parameters system-type) :size-adj-h))))
