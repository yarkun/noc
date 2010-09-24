(ns noc.core
  "NoC - common definitions."
  (:use (incanter core stats charts processing)
;        (clojure.contrib.math) ;abs, ceil, exact-integer-sqrt, expt,
                               ;floor, gcd, lcm, round, sqrt
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

(defn constrain-coordinates
  "Limits the coordinates to the width and height of sketch."
  [sketch [x y]]
  [(constrain x 0 (width sketch))
   (constrain y 0 (height sketch))])

;;; Processing PVector to Incanter Matrix mapping

;; x 	x component of the vector
;; y 	y component of the vector
;; z 	z component of the vector

;; Methods 	

;; set() 	Sets the x, y, z component of the vector
;; get() 	Gets the x, y, z component of the vector
;; mag() 	Calculate the magnitude (length) of the vector
(defn mag
  "Calculates the magnitude of a vector."
  [v]
  (sqrt (sum-of-squares v)))

;; add() 	Adds one vector to another
;; sub() 	Subtracts one vector from another
;; mult() 	Multiplies the vector by a scalar
;; div() 	Divides the vector by a scalar
;; dist() 	Calculate the Euclidean distance between two points
;; dot() 	Calculates the dot product
;; cross() 	Calculates the cross product
;; normalize() 	Normalizes the vector
;; limit() 	Limits the magnitude of the vector
;; angleBetween() 	Calculates the angle between two vectors
;; array() 	Return a representation of the vector as an array

(defn heading2D
  "Calculates the angle from (negative) y-axis."
  [[vx vy]]
  (+ (atan2 vy vx)
     ;; (/ PI 2)
     ))
