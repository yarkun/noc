(ns noc.brians-brain2
  "NoC extra - Brian's Brain implementation with macros"
  (:use (incanter core processing)
        (noc core)
        (clojure set)))

(def board-size (int 400))
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
  [[on-1 on-2 on-more] next-bunch]
  (let [s1          (difference next-bunch on-more)
        new-on-more (intersection s1 on-2)
        s2          (difference s1 on-2)
        new-on-2    (intersection s2 on-1)]
    [(union (difference on-1 new-on-2) (difference s2 new-on-2))
     (union (difference on-2 new-on-more) new-on-2)
     (union on-more new-on-more)]))

; builds a list of sets consisting of the neighbors of each one of the
; currently on cells, then runs this list through the tally-on-cells,
; building sets of the cells with 1, 2 and more on cells. The new on
; cells are those with 2 on neighbors, minus the currently on and
; dying cells.

(defn tick
  [{:keys [tick-count on-cells dying-cells]}]
  {:tick-count (inc tick-count)
   :on-cells (difference ((reduce tally-on-cells-neighbors
                                  [#{} #{} #{}]
                                  (map neighbors on-cells)) (int 1))
                         (union on-cells dying-cells))
   :dying-cells on-cells})

; State of the agent is stored in a map of a tick count and 2 sets,
; first one for on cells, 2nd for dying cells. The rest are implicitly
; the off cells.
(def w
  (agent
   {:tick-count 0
    :on-cells (apply
               conj #{}
               (for [i (range (int (/ (* board-size board-size) 50)))]
                 (xy-to-pos (+ (int (/ board-size 4))
                               (rand-int (int (/ board-size 2))))
                            (+ (int (/ board-size 4))
                               (rand-int (int (/ board-size 2)))))))
    :dying-cells #{}}))

(def new-state-available (ref true))

(defn flag-new-state
  [k a old-state new-state]
  (dosync (ref-set new-state-available true)))

(let [max-tick (int 500)
      a 255
      sktch (sketch
             (setup
              []
              (size this
                    (* board-size cell-size)
                    (* board-size cell-size))
              (background this 80)
              (framerate this 60)
              (add-watch w :flag flag-new-state))

             (draw
              []
              (when @new-state-available
                (dosync (ref-set new-state-available false))
                (background this (int 80))
                (stroke this 255 255 255 a)
                (render this (:on-cells @w))
                (stroke this 180 180 180 a)
                (render this (:dying-cells @w)))

              (when (<= (frame-count this) max-tick) (send w tick))

              (when (>= (:tick-count @w) max-tick)
                (no-loop this)
                (println
                 "Frames:" (frame-count this)
                 "Millisecs:" (millis this)
                 "Ticks:" (:tick-count @w)
                 "Millisecs/tick:" (float (/ (millis this)
                                             (:tick-count @w)))))))]
  
  (view sktch :size [(* board-size cell-size)
                     (+ (* board-size cell-size) 22)]))
