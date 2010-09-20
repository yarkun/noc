(ns noc.core
  "NoC - common definitions."
  (:use (incanter core stats charts processing)
        ;; (clojure.contrib.math)
        ))

(def size-default '[200 200])
(def system-type :pc)
(def system-parameters
     {:pc  {:size-adj-w  8 :size-adj-h 30}
      :mac {:size-adj-w  0 :size-adj-h 22}})

(defn size-adj-w []
  (let [[w h] size-default]
    (+ w ((system-parameters system-type) :size-adj-w))))

(defn size-adj-h []
  (let [[w h] size-default]
    (+ h ((system-parameters system-type) :size-adj-h))))

(defn fade-drawing
  "Fade drawing by alpha blending a black rectange."
  [sketch a]
  (stroke sketch 0 0 0 a)
  (fill sketch 0 0 0 a)
  (rect sketch 0 0 (width sketch) (height sketch)))

(defn flip-y
  "Returns the flipped y coordinate to move the origin from
  top left to bottom left of the sketch window."
  [sketch y]
  (- (height sketch) y))
