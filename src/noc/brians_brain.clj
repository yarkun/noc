(ns noc.brians-brain
  "NoC extra - Brian's Brain implementation"
  (:use (incanter core processing)
        (noc core)
        (clojure set)))

;; (def w (agent [#{19904 19906 20104 20105 20108 20109 20305}
;;                #{20110 20306 20307 20308}]))

(def board-size 90)
(def cell-size 6)

(defn pos-to-xy
  [pos]
  [(mod  pos ca-size)
   (quot pos ca-size)])

(defn xy-to-pos
  [x y]
  (+ x (* y ca-size)))

(defn render-cell
  [sketch pos]
  (let [[x y] (pos-to-xy pos)]
    (rect sketch (* x cell-size) (* y cell-size) cell-size cell-size)))

(defn neighbors
  [pos]
  (let [w     ca-size
        h     ca-size
        [x y] (pos-to-xy pos)
        x-1    (mod (- x 1) w)
        x+1    (mod (+ x 1) w)
        y-1    (mod (- y 1) h)
        y+1    (mod (+ y 1) h)]
    
    #{(xy-to-pos x-1 y-1) (xy-to-pos x y-1) (xy-to-pos x+1 y-1)
      (xy-to-pos x-1 y)                     (xy-to-pos x+1 y)
      (xy-to-pos x-1 y+1) (xy-to-pos x y+1) (xy-to-pos x+1 y+1)}))

(defn tally-on-cells-neighbors
  [[on-1 on-2 more-on] next-bunch]
  (let [s1          (difference next-bunch more-on)
        new-more-on (intersection s1 on-2)
        s2          (difference s1 on-2)
        new-on-2    (intersection s2 on-1)
        new-on-1    (difference s2 new-on-2)]
    [(union (difference on-1 new-on-2) new-on-1)
     (union (difference on-2 new-more-on) new-on-2)
     (union more-on new-more-on)]))

(defn tick
  [[on-cells dying-cells]]
  [(difference ((reduce tally-on-cells-neighbors
                        [#{} #{} #{}]
                        (map neighbors on-cells)) 1)
               (union on-cells dying-cells))
   on-cells])

(def w (agent [(apply conj #{} (for [i (range 100)]
                                 (xy-to-pos (+ 50 (rand-int 100))
                                            (+ 50 (rand-int 100)))))
               #{}]))

(let [sktch (sketch
             (setup
              []
              (size this (* board-size cell-size) (* board-size cell-size))
              (background this 80)
              (framerate this 120)
              )

             (draw
              []
              (background this 80)
              (stroke this 20)
              (fill this 255)
              (dorun (map #(render-cell this %) (@w 0)))
              (stroke this 20)
              (fill this 180)
              (dorun (map #(render-cell this %) (@w 1)))
              (send-off w tick)
              (when (> (frame-count this) 1000)
                (no-loop this)
                (println (/ (millis this) (frame-count this) 1.0)))))]
  
  (view sktch :size [(* board-size cell-size) (+ (* board-size cell-size) 22)]))

