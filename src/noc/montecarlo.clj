(ns noc.montecarlo
  "NoC - Monte Carlo Program"
  (:use (incanter core stats processing)
        (clojure.contrib.math)
        (noc core))
  )

(defn montecarlo
  "Returns the coordinates of a random point satisfying a passed
  function. Defaults to points outside a quarter circle if no function
  is passed. Max. 1000 tries. "

  ([sketch func]

     (loop [rx (rand-int (width sketch))
            ry (rand-int (height sketch))
            count 1000]      ; we try only upto 1000 times, so there is a
                                        ; chance of an error
       (if (and (> count 0) (func sketch rx ry))
         (recur (rand-int (width sketch)) (rand-int (height sketch)) (dec count))
         [rx ry])))
  
  ([sketch]
     (montecarlo (fn [sketch x y] (> (+ (* x x) (* -2 (width sketch) y)
                                       (* y y))
                                    0)))))


(let [vals (ref (vec (repeat (sze 0) 0)))
      y-max (ref (sze 1))
      normalize true
            
      sktch (sketch
             (setup []
                    (doto this
                      (size (sze 0) (sze 1))
                      (fill 128 128 128 255)
                      (stroke 28 128 128 255)
                      (rect 0 0  (width this) (height this))
                      (background 128)
                      (color-mode RGB 255 255 255 255)
                      ;smooth
                      ;; (framerate 30)
                      ))
             (draw []
                   (let [[rx _] (montecarlo this)
                         old-y-max @y-max]

                     (dosync
                      (alter vals assoc rx (inc (@vals rx)))
                      (when (> (@vals rx) @y-max) (ref-set y-max (@vals rx))))

                     (stroke this 255 245 245 50)
                     (let [scale-y (* 1.0 (/ (sze 1) @y-max))]
                       (if (= old-y-max y-max)
                         (line rx 0 rx (* (@vals rx) (if normalize scale-y 1)))
                         (do
                           (background this 100 100 100 255)
                           (doseq [i (range (count @vals))]
                             (line this i 0 i (* (@vals i)
                                                 (if normalize scale-y 1)))))))
                     
                     (stroke this 0 0 0 180)
                     (no-fill this)
                     (ellipse this 0 0 (* 2  (width this)) (* 2 (height this))))))]
  
  (view sktch :size [(size-adj-w) (size-adj-h)]))
