(ns noc.2dnoise
  "NoC - 2D Perlin Noise Program"
  (:use (incanter core processing)
        (noc core)))

(let [delta 0.01
            
      sktch (sketch

             (setup []
                    (size this (size-default 0) (size-default 1))
                    (no-loop this))

             (draw []
                   (doseq [y (range (height this))
                           x (range (width this))]
                     (let [n (round (* 256 (noise this
                                                  (* x delta)
                                                  (* y delta))))]
                       (set-pixel this x y (color n n n))))))]
  
  (view sktch :size [(size-adj-w) (size-adj-h)]))

