(ns noc.brians-brain4
  "NoC extra - Brian's Brain implementation"
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
  "renders a set of lines between some points between 2 agents."
  [sketch points1 points2]
  (dorun
   (map (fn [pos1 pos2] (let [[x1 y1] (pos-to-xy pos1)
                             [x2 y2] (pos-to-xy pos2)]
                         ;(point sketch x1 y1)
                         (when (> (rand-int 100) 95)
                           (line sketch x2 y2 x1 y1))))
        points1 points2)))

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

(defn chords
  "returns the position indices of the intersections of n evenly
  spaced horizontal and vertical lines"
  [n]
  (let [step (quot board-size (+ n 2))]
    (into #{} (for [x (range step (- board-size step -1) step)
                    y (range step (- board-size step -1) step)]
                (xy-to-pos x y)))))


(def w1
; State of the agent is stored in a map of a tick count and 2 sets,
; first one for on cells, 2nd for dying cells. The rest are implicitly
; the off cells.
  (agent
   {:tick-count 0
    :on-cells (union (map #(xy-to-pos (inc ((pos-to-xy %) 0))
                                      ((pos-to-xy %) 1))
                          (chords 3))
                     (chords 3 ))
    :dying-cells #{}}))

(def w2
  (agent
   {:tick-count 0
    :on-cells (union (map #(xy-to-pos (inc ((pos-to-xy %) 0))
                                      ((pos-to-xy %) 1))
                          (chords 3))
                     (chords 3))
    :dying-cells #{}}))

; since the draw function has an implicit loop, unless we signal the
; availabilty of a new generation of cells, the old set will be
; redrawn unnecessarily almost every iteration.
(def new-state-available (ref true))

(defn flag-new-state
  [k a old-state new-state]
  (dosync (ref-set new-state-available true)))

(let [max-tick (int 50)
      a 200
      sktch (sketch
             (setup
              []
              (size this
                    (* board-size cell-size)
                    (* board-size cell-size)
                    P2D)
              (background this 80)
              (framerate this 60)
              (smooth this)
              (add-watch w1 :flag flag-new-state)
              (add-watch w2 :flag flag-new-state)
              (dorun (for [i (range 10)] (send w2 tick))))

             (draw
              []
              (when @new-state-available
                (dosync (ref-set new-state-available false))
                (fade-drawing this 50)
                ;(background this 80 80 80 1)
                (stroke this 255 255 255 a)
                (render this (:on-cells @w1) (:on-cells @w2)))

              (when (<= (frame-count this) max-tick)
                (send w1 tick)
                (send w2 tick))

              (when (>= (:tick-count @w1) max-tick)
                (no-loop this)
                (save this "/Volumes/R250/graphics/generative/bb2.tif")
                (println
                 "Frames:" (frame-count this)
                 "Millisecs:" (millis this)
                 "Ticks:" (:tick-count @w1) (:tick-count @w2)
                 "Millisecs/tick:" (float (/ (millis this)
                                             (:tick-count @w1)))))))]
  
  (view sktch :size [(* board-size cell-size)
                     (+ (* board-size cell-size) 22)]))
