(ns brians-brain3
  "bug reproduction"
  (:use (incanter core processing)))

(let [board-size 400
      a          254 ; no artifact when a = 255
      sktch (sketch
             (setup
              []
              (size this board-size board-size)
              (background this 80)
              (framerate this 60))

             (draw
              []

              (stroke this 255 255 255 a)
              
              (dorun
               (for [i (range 100)]
                 (point this
                        (rand-int board-size)
                        (rand-int board-size))))
              
              (when (>= (frame-count this) 500)
                (no-loop this))))]
  
  (view sktch :size [board-size (+ board-size 22)]))
