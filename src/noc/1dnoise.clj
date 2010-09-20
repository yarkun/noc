(ns noc.1dnoise
  "NoC - 1D Perlin Noise Program"
  (:use (incanter core processing)
        (noc core)))

(let [xinc 0.01
            
      sktch (sketch
             (setup []
                    (doto this
                      (size (size-default 0) (size-default 1))
                      (background 0)
                      smooth
                      (no-stroke)
                      (framerate 30)))

             (draw []
                   (let [n (* (width this) (noise this (* (frame-count this) xinc)))]
                     (fade-drawing this 10)
                     (fill this 200)
                     (ellipse this n (/ (height this) 2) 16 16))))]
  
  (view sktch :size [(size-adj-w) (size-adj-h)]))

