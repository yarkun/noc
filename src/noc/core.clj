(ns noc.core
  "NoC - common definitions."
  (:use (incanter core stats charts processing)
        (clojure.contrib.math) ;abs, ceil, exact-integer-sqrt, expt,
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

;;; PVector 'implementation'
;;; Since the vectors are implemented as clojure vectors, the methods
;;; usually take one more argument than the processing version.

;; [x y] or [x y z]
;; x 	x component of the vector
;; y 	y component of the vector
;; z 	z component of the vector

;; Methods 	

;; set() 	Sets the x, y, z component of the vector
;; get() 	Gets the x, y, z component of the vector

;; mag() 	Calculate the magnitude (length) of the vector
(defn mag
  "Calculate the magnitude (length) of the vector."
  [v]
  (apply (comp sqrt +) (map (fn [x] (* x x)) v)))

;; add() 	Adds one vector to another
(defn add
  "Adds one vector to another. If they have different lengths, shorter
  one is padded with zeros."
  [v1 v2]
  (by-element + v1 v2))

;; sub() 	Subtracts one vector from another
(defn sub
  "Subtracts 2nd vector from first. If they have different lengths,
  shorter one is padded."
  [v1 v2]
  (by-element - v1 v2))

;; mult() 	Multiplies the vector by a scalar
(defn mult
  "Multiplies the vector by a scalar, or two vectors' elements."
  [v1 v2]
  (let [v1 (if (number? v1)
             (vec (repeat (if (coll? v2) (count v2) 1) v1))
             v1)
        v2 (if (number? v2)
             (vec (repeat (if (coll? v1) (count v1) 1) v2))
             v2)
        (by-element * v1 v2)]))

;; div() 	Divides the vector by a scalar
;; dist() 	Calculate the Euclidean distance between two points
;; dot() 	Calculates the dot product
;; cross() 	Calculates the cross product
;; normalize() 	Normalizes the vector
;; limit() 	Limits the magnitude of the vector
;; angleBetween() 	Calculates the angle between two vectors
;; array() 	Return a representation of the vector as an array

;; added by YA
(defn pad-if
  "Makes two vectors match in count by padding the shorter one, if
  needed. Returns a vector of 2 two vectors."
  [v1 v2]
  (let [c1 (count v1)
        c2 (count v2)]
    [(vec (concat v1 (repeat (- c2 c1) 0)))
     (vec (concat v2 (repeat (- c1 c2) 0)))]))

(defn by-element
  "Applies the given function to two vectors by element (after padding
  if necessary), returning a vector."
  [func v1 v2]
    (vec (apply map func (pad-if v1 v2))))
