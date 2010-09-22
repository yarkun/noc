(ns noc.walker
  "NoC - random walk functions"
  (:use (incanter core processing)
        (noc core)))

(defn new-agent
  "Returns a new walker agent. Defaults to a nil sketch, so reset it
  with a real one before use."
  [& {:keys [skt pos step-fn render-fn]
      :or {skt      nil
           pos      [(/ (size-default 0) 2)
                     (/ (size-default 1) 2)]
           step-fn (fn [skt [x y]]   ; in the default step-fn, sketch
                                     ; is ignored. In yours, you could
                                     ; make the step probabilites a
                                     ; function of the sketch
                                     ; parameters, e.g. distance to walls.
                     (let   [w (size-default 0)
                             h (size-default 1)]
                       [(constrain (+ x (dec (rand-int 3))) 0 w)
                        (constrain (+ y (dec (rand-int 3))) 0 h)]))
           render-fn (fn [skt [x y]]
                       (stroke skt 100)
                       (point skt x y))}}]
  (agent [skt pos step-fn render-fn]))

(defn reset
  "Resets the agent state to the given state. Necessary evil."
  [[old-sketch old-pos old-step-fn old-render-fn]
   & {:keys [sketch pos step-fn render-fn]
      :or {sketch old-sketch
           pos old-pos
           step-fn old-step-fn
           render-fn old-render-fn}}]
  [sketch pos step-fn render-fn])

(defn do-step
  "Makes a walker to take a step."
  [[sketch pos step-fn render-fn]]
  (let [new-pos (step-fn sketch pos)]
    (render-fn sketch new-pos)
    [sketch new-pos step-fn render-fn]))

(let [w (new-agent) ; sketch not defined here yet, so can't use it for
                    ; initialization. On the other hand, it needs to
                    ; be defined before sktch so that it is accessible
                    ; from all sktech methods. (?)
      
      sktch (sketch
             (setup []
                    (size this (size-default 0) (size-default 1))
                    (background this 0)
                    (framerate this 60)
                    (color-mode this RGB 255 255 255 255)
                    ; now we can reference it, reset w with the
                    ; proper sketch:
                    (send-off w reset :sketch this))

             (draw []
                   (send-off w do-step)))]
  
  (view sktch :size [(size-adj-w) (size-adj-h)]))

