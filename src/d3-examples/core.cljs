(ns min-figwheel.core
  (:require [cljsjs/d3]))

; Example 47
; Print out x and y coordinates. Notice they are strings.
(defn prn-concat
  [data]
  (doseq [{:strs [x y]} (js->clj data)]
    (prn x y)))

(.csv js/d3 "data.csv" prn-concat)

; Example 48
; Print out sum of x and y coordinates. Use type function to process datum.
(defn type-func
  [datum]
  (-> (js->clj datum)
      (update "x" js/parseFloat)
      (update "y" js/parseFloat)
      (clj->js)))

(defn prn-addition
  [data]
  (doseq [{:strs [x y]} (js->clj data)]
    (prn (+ x y))))

(.csv js/d3 "data.csv" type-func prn-addition)

; Example 49
; Skipped +datum.x syntax not applicable

; Example 50
; Example introducing scale, domain, and range
(def scale-50 (-> js/d3
                  (.scaleLinear)
                  (.domain #js [0 1])
                  (.range #js [0 100])))
(prn "Ex 50 " (scale-50 0))
(prn "Ex 50 " (scale-50 0.5))
(prn "Ex 50 " (scale-50 1))

; Example 51
; Skipped already covered with threading macro above

; Example 52
; I guess showing that .domain and .range are both setters and getters
(prn "Ex 52 " (.domain scale-50))
(prn "Ex 52 " (.range scale-50))

; Example 53
; Example of ordinal scale
(def domain-letters ["A" "B" "C" "D"])
(def range-fruits ["Apples" "Bananas" "Coconuts" "Durian"])
(def scale-53 (-> js/d3
                  (.scaleOrdinal)
                  (.domain (clj->js domain-letters))
                  (.range (clj->js range-fruits))))
(prn "Ex 53 " (scale-53 "A"))
(prn "Ex 53 " (scale-53 "B"))
(prn "Ex 53 " (scale-53 "C"))

; Example 54
; Example of ordinals to range
(def scale-54 (-> js/d3
                  (.scalePoint)
                  (.domain (clj->js domain-letters))
                  (.range #js [0 100])))
(prn "Ex 54 " (scale-54 "A"))
(prn "Ex 54 " (scale-54 "B"))
(prn "Ex 54 " (scale-54 "C"))

; Example 55
; Uses rounding
(def scale-55 (.round scale-54 true))
(prn "Ex 55 " (scale-55 "A"))
(prn "Ex 55 " (scale-55 "B"))
(prn "Ex 55 " (scale-55 "C"))

; Example 56
; First dom manipulation. Ensure that div is above script in html. 
(-> js/d3
    (.select "div#ex-56")
    (.append "svg")
    (.append "rect")
    (.attr "x" 50)
    (.attr "y" 50)
    (.attr "width" 20)
    (.attr "height" 20))

; Example 57
; Skipped. Covered with threading macro

; Example 58
; First instance of selectAll/Enter paradigm
(def data-58 #js [1, 2, 3, 4, 5])
(def scale-58 (-> js/d3
                  (.scaleLinear)
                  (.domain #js [1 5])
                  (.range #js [0 200])))

(-> js/d3
    (.select "div#ex-58")
    (.append "svg")
    (.attr "width" 250)
    (.attr "height" 250)
    (.selectAll "rect")
    (.data data-58)
    (.enter)
    (.append "rect")
    (.attr "x" #(scale-58 %1))
    (.attr "y" 50)
    (.attr "width" 20)
    (.attr "height" 20))
    
; Example 59
; ok... this was obvious the anonymous function wrapping scale is unnecessary
(-> js/d3
    (.select "div#ex-59")
    (.append "svg")
    (.attr "width" 250)
    (.attr "height" 250)
    (.selectAll "rect")
    (.data data-58)
    (.enter)
    (.append "rect")
    (.attr "x" scale-58)
    (.attr "y" 50)
    (.attr "width" 20)
    (.attr "height" 20))

; Example 60
; Example 60 is doing the same thing as before but keeping a reference to the 
; rectangles.
(defn create-svg!
  [parent-selctor]
  (-> js/d3
      (.select parent-selctor)
      (.append "svg")
      (.attr "width" 250)
      (.attr "height" 250)))

(def svg-60 (create-svg! "div#ex-60"))

(def rects-60 (-> svg-60
                  (.selectAll "rect")))
(-> rects-60
    (.data data-58)
    (.enter)
    (.append "rect")
    (.attr "x" scale-58)
    (.attr "y" 50)
    (.attr "width" 20)
    (.attr "height" 20))

; Example 61
; How updating does not work.
; Range and Domain are still the same
(def svg-61 (create-svg! "div#ex-61"))

(def rects-61 (-> svg-61
                  (.selectAll "rect")))
(defn render!
  [svg data color]
  (-> svg
      (.selectAll "rect")
      (.data data)
      (.enter)
      (.append "rect")
      (.attr "x" scale-58)
      (.attr "y" 50)
      (.attr "width" 20)
      (.attr "height" 20)
      (.attr "fill" color)))

(render! svg-61 #js [1 2 3] "red")
(render! svg-61 #js [1 2 3 4 5] "blue")

; Example 62
; changing the data isn't reflected either
(def svg-62 (create-svg! "div#ex-62"))

(def rects-62 (-> svg-62
                  (.selectAll "rect")))

(render! svg-62 #js [1 2 2.5] "red")
(render! svg-62 #js [1 2 3 4 5] "blue")

; Example 63
; The right way, finally
(defn render-right!
  [svg data color]
  (let [rects (-> svg
                  (.selectAll "rect") 
                  (.data data))]
    (do
      (-> rects
          (.enter)
          (.append "rect"))
      (-> rects
          (.attr "x" scale-58)
          (.attr "y" 50)
          (.attr "width" 20)
          (.attr "height" 20)
          (.attr "fill" color)))))

(def svg-63 (create-svg! "div#ex-63"))

(render-right! svg-63 #js [1 2 3 4 5] "blue")
(render-right! svg-63 #js [1 2 3] "red")

