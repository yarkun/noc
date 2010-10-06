(ns brians-brain3
  "bug reproduction"
  (:use (incanter core processing)))

(let [board-size (int 400)
      a          (int 254)               ; no artifact when a = 255
      sktch (sketch
             (setup
              []
              (size this board-size board-size)
              (background this 80)
              (framerate this 60)
              (color-mode this RGB 255 255 255 255)
              (no-loop this))

             (draw
              []

              (stroke this 255 255 255 a)
              
              (dorun
               (for [x (range (int 400)) y (range (int 400))]
                 (point this (float x) (float y))))))]
  
  (view sktch :size [board-size (+ board-size 22)]))
