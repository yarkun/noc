(ns noc.brians-brain2
  "NoC extra - Brian's Brain implementation with macros"
  (:use (incanter core processing)
        (noc core)
        (clojure set)))

(def board-size (int 1200))
(def cell-size (int 1))


(defn pos-to-xy
"converts a postion index to a x-y coordinate."
  [pos]
  [(mod  pos board-size) 
   (quot pos board-size)])

(defn xy-to-pos
  "converts a x-y coordinate to a position index."
  [x y]
  (+ x (* y board-size)))

(defn render
  "renders a set of points."
  [sketch points]
  (dorun
    (map (fn [pos] (let [[x y] (pos-to-xy pos)]
                      (point sketch x y)))
         points)))

(defn neighbors
"returns a set of the 8 neighbors of a given cell. Edges are wrapped
around (torus topology.)"
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
  "given a vector of 3 sets containing the tally so far, and a set of
 cells that have at least one on neighbor, returns the updated tally
 of cells with 1, 2 and more than 2 on neighbors."
  [[on-1 on-2 on-more] next-bunch]
  (let [s1          (difference next-bunch on-more)
        new-on-more (intersection s1 on-2)
        s2          (difference s1 new-on-more)
        new-on-2    (intersection s2 on-1)]
    [(difference (union on-1 s2) new-on-2)
     (union (difference on-2 new-on-more) new-on-2)
     (union on-more new-on-more)]))

(defn tick
"builds a list of sets consisting of the neighbors of each one of the
currently on cells (excluding on or dying cells), then runs this
list through the tally-on-cells, building sets of the cells with 1,
2 and more than 2 on cells."
  [{:keys [tick-count on-cells dying-cells]}]
  {:tick-count (inc tick-count)
   :on-cells (let [on-and-dying-cells (union on-cells dying-cells)]
               ((reduce tally-on-cells-neighbors
                        [#{} #{} #{}]
                        (map #(difference (neighbors %)
                                          on-and-dying-cells)
                             on-cells))
                (int 1)))
   :dying-cells on-cells})

(def w
; State of the agent is stored in a map of a tick count and 2 sets,
; first one for on cells, 2nd for dying cells. The rest are implicitly
; the off cells.
  (agent
   {:tick-count 0
    :on-cells #{(+ -1 (/ board-size 2) (* board-size (/ board-size 2)))
                (+ (/ board-size 2) (* board-size (/ board-size 2)))}
    ;; (apply
    ;;            conj #{}
    ;;            (for [i (range (int (/ (* board-size board-size) 50)))]
    ;;              (xy-to-pos (+ (int (/ board-size 4))
    ;;                            (rand-int (int (/ board-size 2))))
    ;;                         (+ (int (/ board-size 4))
    ;;                            (rand-int (int (/ board-size 2)))))))
    :dying-cells #{}}))

; since the draw function has an implicit loop, unless we signal the
; availabilty of a new generation of cells, the old set will be
; redrawn unnecessarily almost every iteration.
(def new-state-available (ref true))

(defn flag-new-state
  [k a old-state new-state]
  (dosync (ref-set new-state-available true)))

(let [max-tick (int 1200)
      a 255
      sktch (sketch
             (setup
              []
              (size this
                    (* board-size cell-size)
                    (* board-size cell-size)
                    P2D)
              (background this 80)
              (framerate this 60)
              (add-watch w :flag flag-new-state))

             (draw
              []
              (when @new-state-available
                (dosync (ref-set new-state-available false))
                (fade-drawing this 2)
                ;(background this 80 80 80 1)
                (stroke this 255 255 255 a)
                (render this (:on-cells @w))
                (stroke this 180 180 180 a)
                (render this (:dying-cells @w)))

              (when (<= (frame-count this) max-tick) (send w tick))

              (when (>= (:tick-count @w) max-tick)
                (no-loop this)
                (save this "/Volumes/R250/graphics/generative/bb1.tif")
                (println
                 "Frames:" (frame-count this)
                 "Millisecs:" (millis this)
                 "Ticks:" (:tick-count @w)
                 "Millisecs/tick:" (float (/ (millis this)
                                             (:tick-count @w)))))))]
  
  (view sktch :size [(* board-size cell-size)
                     (+ (* board-size cell-size) 22)]))
