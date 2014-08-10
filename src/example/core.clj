(ns example.core
  (:use [overtone.live :exclude [clear]])
  (:use [overtone.sc.machinery.synthdef])
  (:use [clojure.pprint])
  (:use [clojure.core.match :only [match]])
  (:use [polynome.core :as poly])
  (:use [overtone.inst.drum :only [quick-kick haziti-clap soft-hat open-hat]])
  )

; ------------------------------------------------
; SYNTHS -----------------------------------------
; ------------------------------------------------
(definst high-hat [amp 1 decay 0.3 freq 2000 peak 4 attack 0]
  (let [
        high-hat (pink-noise)
        high-hat (+ high-hat (bpf (* 4 high-hat) 20000))
        high-hat (+ high-hat (* 0.2 (g-verb high-hat 9 0.7 0.7)))
        env      (env-gen (perc attack decay peak) 1 1 0 1 FREE)]
    (out 0 (pan2 (* high-hat env) 0))))

(definst kick-drum [amp 1 decay 0.8 freq 45 peak 1 attack 0.01]
  (let [env (env-gen (perc attack decay peak) 1 1 0 1 FREE)
        snd (sin-osc freq (* Math/PI 0.5))]
    (out 0 (pan2 (* snd env) 0))))

(definst snare-drum [amp 1 decay 0.1 freq 600 peak 6 attack 0]
  (let [
        snare0 (pink-noise)
        snare0 (+ snare0 (/ (sin-osc freq) 1))
        snare0 (+ snare0 (bpf (* 4 snare0) 10000))

        snare1 (pink-noise)
        snare1 (+ snare1 (bpf (* 4 snare1) 1000))

        ; Percussion Envelope
        env (env-gen (perc attack decay peak) 1 1 0 1 FREE)]
    (out 0 (pan2 (g-verb (* snare0 snare1 env)
                         5 1 1)
                 0))))

;(definst snare-drum [bpm 120]
  ;(let [snare (* (pink-noise) (apply + (* (decay (impulse (/ bpm 240) 0.5) [0.4 2]) [1 0.05])))

        ;;; Send through band pass filter with peak @ 2kHz
        ;snare (+ snare (bpf (* 4 snare) 2000))

        ;;; Also clip at max vol to distort
        ;snare (clip2 snare 1)]
    ;(out 0 (pan2 snare 0))))

(definst beep [note 60 amp 1 decay 0.8 freq 220 peak 1 attack 0.01]
  (let [env (env-gen (perc attack decay peak) 1 1 0 1 FREE)
        snd (sin-osc (midicps note) (* Math/PI 0.5))]
    (out 0 (pan2 (* snd env) 0))))

; ------------------------------------------------
; MAIN->MONOME -----------------------------------
; ------------------------------------------------
(def m (poly/init "/dev/tty.usbserial-m64-1113"))

(poly/remove-all-callbacks m)

(poly/toggle-led m 0 0)

(poly/on-press m ::foo (fn [x y s]
                         (match [x y]
                           [0 0] (kick-drum)
                           [0 1] (snare-drum)
                           [0 2] (high-hat)
                           [7 7] (beep 80)
                           [7 6] (beep 82)
                           [7 5] (beep 84)
                           [7 4] (beep 85)
                           [7 3] (beep 87)
                           [7 2] (beep 89)
                           [7 1] (beep 91)
                           [7 0] (beep 92)
                           )))


