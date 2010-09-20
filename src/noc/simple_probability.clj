(ns noc.simple-probability
  "NoC - Multiple Probabilities Program"
  (:use (incanter core processing)
        (noc core)))

(let [prob (ref nil)
      mx (ref nil)
      X (ref nil)
      Y (ref nil)
      
      sktch (sketch
             (setup []
                    (doto this
                      (size (size-default 0) (size-default 1))
                      (fill 128 128 128 255)
                      (stroke 128 128 128 255)
                      (rect 0 0  (width this) (height this))
                      (background 128)
                      (color-mode RGB 255 255 255 255)
                      smooth
                      (framerate 30))
                    (dosync
                     (ref-set mx (/ (width this) 2))
                     (ref-set prob @mx)
                     (ref-set X 5)
                     (ref-set Y 5)))

             (draw []
                   (dosync
                    (ref-set prob (/ @mx  (width this)))
                    (ref-set X (+ (rem (* (frame-count this) 10)  (width this)) 5))
                    (ref-set Y (+ (rem (* (quot (* (frame-count this) 10) (width this)) 10) (height this)) 5)))
                   (fade-drawing this 1)
                   (when (< (rand) @prob)
                     (no-stroke this)
                     (fill this 255)
                     (ellipse this @X @Y 10 10)))
             (mouseMoved [mouse-event]
                         (dosync (ref-set mx (mouse-x mouse-event)))))]
   (view sktch :size [(size-adj-w) (size-adj-h)]))
