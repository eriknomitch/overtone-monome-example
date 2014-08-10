(ns example.core
  (:use [overtone.live :exclude [clear]])
  (:use [overtone.sc.machinery.synthdef])
  (:use [clojure.pprint])
  (:use [clojure.core.match :only [match]])
  (:use [polynome.core :as poly])
  )

; ------------------------------------------------
; SYNTHS -----------------------------------------
; ------------------------------------------------
(definst kick-drum [amp 1 decay 0.8 freq 45 attack 10]
  (let [env (env-gen (perc 0 decay) 1 1 0 1 FREE)
        snd (sin-osc freq (* Math/PI 0.5))]
    (out 0 (pan2 (* snd env) 0))))

;(kick-drum)

; ------------------------------------------------
; METRONOMES -------------------------------------
; ------------------------------------------------
(def one-twenty-bpm (metronome 120))

; ------------------------------------------------
; LOOPER -----------------------------------------
; ------------------------------------------------
(defn looper [nome sound]
    (let [beat (nome)]
        (at (nome beat) (sound))
        (apply-by (nome (inc beat)) looper nome sound [])))

; ------------------------------------------------
; MAIN -------------------------------------------
; ------------------------------------------------
(def m (poly/init "/dev/tty.usbserial-m64-1113"))

(poly/remove-all-callbacks m)

(poly/toggle-led m 0 0)

(poly/on-press m ::foo (fn [x y s]
                         (match [x y]
                           [0 0] (kick-drum))))


;; LOOPERS
(kick-drum)
;(looper one-twenty-bpm kick-drum)
;;(looper one-twenty-bpm snare-drum)

;(stop)

