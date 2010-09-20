(ns noc.3dnoise
  "NoC - 3D Perlin Noise Program"
  (:use (incanter core processing)
        (noc core)))

(let [delta 0.01
      delta-time 0.02
            
      sktch (sketch

             (setup []
                    (size this (size-default 0) (size-default 1))
                    (framerate this 30))

             (draw []
                   (doseq [y (range (height this))
                           x (range (width this))]
                     (let [n (round (* 256 (noise this
                                                  (* x delta)
                                                  (* y delta)
                                                  (* delta-time (frame-count this)))))]
                       (set-pixel this x y (color n n n))))))]
  
  (view sktch :size [(size-adj-w) (size-adj-h)]))

