(ns noc.bell-curve
  "NoC - Bell Curve Program"
  (:use (incanter core stats processing)
        (noc core)))

(let [m   0.0
      sd  1.0
      min-x -5.0
      max-x  5.0

      sktch (sketch

             (setup []
                    (doto this
                      (size (size-default 0) (size-default 1))
                      (color-mode RGB 255 255 255 100)
                      smooth
                      (framerate 5)))

             (draw []
                   (background this 128)
                   (no-fill this)
                   (stroke this 255 255 255 255)
                   (begin-shape this)
                   (let [x-scale  (/ (- max-x min-x) (width this))]
                     (dorun
                      (map (fn [x]
                             (vertex this
                                     x
                                     (flip-y this
                                             (* (pdf-normal
                                                 (+ (* x x-scale) min-x)
                                                 :mean m :sd sd)
                                                (height this)))))
                           (range 0 (width this) 5))))
                   (end-shape this)))]
  
   (view sktch :size [(size-adj-w) (size-adj-h)]))
