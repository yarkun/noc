(ns noc.multiple-probability
  "NoC - Multiple Probabilities Program"
  (:use (incanter core processing)
        (noc core)))

(let [p1 (ref 0.05)
      p2 (ref (+ 0.8 @p1))

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
                     (ref-set X 0)
                     (ref-set Y 0)))

             (draw []
                   (dosync
                    (ref-set X (+ (mod (* (frame-count this) 10)  (width this)) 0))
                    (ref-set Y (+ (mod (* (quot (* (frame-count this) 10) (width this)) 10) (height this)) 0)))
                     
                   (let [r (rand)]
                     (doto this
                       (fill 0 0 0 1)
                       (rect 0 0  (width this) (height this))
                       (stroke 200)
                       (fill (if (< r @p1 ) 255
                                 (if (< r @p2) 150
                                     0)))
                       (rect  @X @Y 10 10)))))]
  (view sktch :size [(size-adj-w) (size-adj-h)]))
