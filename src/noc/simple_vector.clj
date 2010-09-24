(ns noc.simple-vector
  "NoC - simple vector"
  (:use (incanter core processing)
        (noc core)))

(defn draw-vector
  "Draws a vector v at loc using scale scayl."
  [sketch v loc scayl]
  (let [[vx vy]     v
        [locx locy] loc
        arrow-size  4
        len         (* (mag v) scayl)]
    (push-matrix sketch)
    (translate sketch locx locy)
    (stroke sketch 255)
    (rotate sketch (heading2D v))
    (line sketch 0 0 len 0)
    (line sketch len 0 (- len arrow-size) (/ arrow-size 2))
    (line sketch len 0 (- len arrow-size) (/ arrow-size -2))
    (pop-matrix sketch)))

(let [centerLoc (ref (matrix [(/ (size-default 0) 2) (/ (size-default 1) 2)] 1))
      mouseLoc  (ref @centerLoc)
      
      sktch (sketch
             (setup
              []
              (size this (size-default 0) (size-default 1))
              (background this 255)
              (smooth this))

             (draw
              []
              (dosync
               (ref-set centerLoc (matrix [(/ (width this) 2)
                                           (/ (height this) 2)] 1)))
              (background this 100)
              (draw-vector this (minus @mouseLoc @centerLoc) @centerLoc 1))

             (mouseMoved
              [mouse-event]
              (dosync
               (ref-set mouseLoc (matrix [(mouse-x mouse-event)
                                          (mouse-y mouse-event)] 1)))))]
  
  (view sktch :size [(size-adj-w) (size-adj-h)]))

