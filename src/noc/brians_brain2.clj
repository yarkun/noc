(ns noc.brians-brain2
  "NoC extra - Brian's Brain implementation with macros"
  (:use (incanter core processing)
        (noc core)
        (clojure set)))

;; (def w (agent [#{19904 19906 20104 20105 20108 20109 20305}
;;                #{20110 20306 20307 20308}]))

(def board-size (int 200))
(def cell-size (int 1))

(defmacro pos-to-xy
  [pos]
  `[(mod  ~pos board-size) 
    (quot ~pos board-size)])

(defmacro xy-to-pos
  [x y]
  `(+ ~x (* ~y board-size)))

(defmacro render
  [sketch points]
  `(dorun
    (map (fn [~'pos] (let [[~'x ~'y] (pos-to-xy ~'pos)]
                      (point ~sketch ~'x ~'y)))
         ~points)))

(defn neighbors
  [pos]
  (let [[x y] (pos-to-xy pos)
        x-1    (mod (- x (int 1)) board-size)
        x+1    (mod (+ x (int 1)) board-size)
        y-1    (mod (- y (int 1)) board-size)
        y+1    (mod (+ y (int 1)) board-size)]
    
    #{(xy-to-pos x-1 y-1) (xy-to-pos x y-1) (xy-to-pos x+1 y-1)
      (xy-to-pos x-1 y)                     (xy-to-pos x+1 y)
      (xy-to-pos x-1 y+1) (xy-to-pos x y+1) (xy-to-pos x+1 y+1)}))

(defn tally-on-cells-neighbors
  [[on-1 on-2 more-on] next-bunch]
  (let [s1          (difference next-bunch more-on)
        new-more-on (intersection s1 on-2)
        s2          (difference s1 on-2)
        new-on-2    (intersection s2 on-1)]
    [(union (difference on-1 new-on-2) (difference s2 new-on-2))
     (union (difference on-2 new-more-on) new-on-2)
     (union more-on new-more-on)]))

(defn tick
  [[on-cells dying-cells]]
  [(difference ((reduce tally-on-cells-neighbors
                        [#{} #{} #{}]
                        (map neighbors on-cells)) (int 1))
               (union on-cells dying-cells))
   on-cells])


; state is stored in a vector of 2 sets, first one for on cells, 2nd
; for dying cells. The rest are implicitly the off cells.
(def w (agent [(apply conj #{} (for [i (range (int (/ (* board-size board-size) 20)))]
                                 (xy-to-pos (+ (int (/ board-size 4)) (rand-int (int (/ board-size 2))))
                                            (+ (int (/ board-size 4)) (rand-int (int (/ board-size 2)))))))
               #{}]))

(let [sktch (sketch
             (setup
              []
              (size this (* board-size cell-size) (* board-size cell-size))
              (background this 80)
              (framerate this 60))

             (draw
              []
              (background this (int 80))

              (stroke this 255)
              (render this (@w (int 0)))

              (stroke this 180)
              (render this (@w (int 1)))

              (send-off w tick)

              (when (> (frame-count this) (int 2000))
                (no-loop this)
                (println
                 (millis this) (frame-count this)
                 (/ (millis this) (frame-count this) 1.0)))))]
  
  (view sktch :size [(* board-size cell-size) (+ (* board-size cell-size) 22)]))
