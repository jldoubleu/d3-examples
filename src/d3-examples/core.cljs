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
      (.attr "width" 300)
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
    (-> rects
        (.enter)
        (.append "rect")
          (.attr "width" 20)
          (.attr "height" 20)
        (.merge rects)
        (.attr "x" scale-58)
        (.attr "y" 50)
        (.attr "fill" color))))

(def svg-63 (create-svg! "div#ex-63"))

(render-right! svg-63 #js [1 2 2.5] "red")
(render-right! svg-63 #js [1 2 3 4 5] "blue")

; Example 64
; Use append for all the stuff that doesn't change
(def svg-64 (create-svg! "div#ex-64"))

(defn render-righter!
  [svg data color]
  (let [rects (-> svg
                  (.selectAll "rect") 
                  (.data data))]
    (-> rects
        (.enter)
        (.append "rect")
          (.attr "y" 50)
          (.attr "width" 20)
          (.attr "height" 20)
        (.merge rects)
        (.attr "x" scale-58)
        (.attr "fill" color))))
  
(render-righter! svg-64 #js [1 2 2.5] "red")
(render-righter! svg-64 #js [1 2 3 4 5] "blue")

; Example 65
; Actually it still isn't right.  Elements are not deleted.
(def svg-65 (create-svg! "div#ex-65"))
  
(render-righter! svg-65 #js [1 2 2.5] "red")
(render-righter! svg-65 #js [1 2 3 4 5] "blue")
(render-righter! svg-65 #js [1 2] "green")

; Example 66
; Finally the complete add, update, remove cycle
(defn render-rightest!
  [svg data color]
  (let [rects (-> svg
                  (.selectAll "rect") 
                  (.data data))]
    (do 
      (-> rects
          (.enter)
            (.append "rect")
            (.attr "y" 50)
            (.attr "width" 20)
            (.attr "height" 20) 
          (.merge rects)
            (.attr "x" scale-58)
            (.attr "fill" color))
      (-> rects
          (.exit)
          (.remove)))))

(def svg-66 (create-svg! "div#ex-66"))
  
(render-rightest! svg-66 #js [1 2 2.5] "red")
(render-rightest! svg-66 #js [1 2 3 4 5] "blue")
(render-rightest! svg-66 #js [1 2] "green")

; Example 67
; Using delay to show the steps

(def svg-67 (create-svg! "div#ex-67"))

(js/setTimeout #(render-rightest! svg-67 #js [1 2 2.5] "red") 1000)
(js/setTimeout #(render-rightest! svg-67 #js [1 2 3 4 5] "blue") 2000)
(js/setTimeout #(render-rightest! svg-67 #js [1 2] "green") 3000)
(js/setTimeout #(render-rightest! svg-67 #js [3 4 5] "cyan") 4000)
(js/setTimeout #(render-rightest! svg-67 #js [3 4] "magenta") 5000)

; Example 68
; Circles have different properties than rects

(defn render-circle
  [svg data]
  (let [circles (-> svg
                    (.selectAll "circle")
                    (.data data))]
    (do
      (-> circles
          (.enter)
            (.append "circle")
            (.attr "r" 10)
          (.merge circles)
            (.attr "cx" #(.-x %1))  ; javascript interop property access
            (.attr "cy" #(.-y %1)))
      (-> circles
          (.exit)
            (.remove)))))

(def data-68 [
  {:x 100 :y 100}
  {:x 130 :y 120}
  {:x 80  :y 180}
  {:x 180 :y 80}
  {:x 180 :y 40}])

(def svg-68 (create-svg! "div#ex-68"))

(render-circle svg-68 (clj->js data-68))

; Example 69
; Loading circles from a file

(def svg-69 (create-svg! "div#ex-69"))
(-> js/d3
    (.csv "data.csv" type-func #(render-circle svg-69 %1)))

; Example 70-81
; Skipped a few boring items. Scatter plot of iris information
; The lesson here for me is to remember that the js stuff is not
; necessarily immutable I think. I had a problem for a while 
; that seemed to have been caused by reusing the same linear
; scale for the x and y range/domains. 

(def iris-outer-width 300)
(def iris-outer-height 250)
(def iris-radius-min 1)
(def iris-radius-max 6)
(def iris-column-prop "petal_length")
(def iris-row-prop    "sepal_length")
(def iris-radius-prop "sepal_width")
(def iris-color-prop  "species")

(def iris-range-x (-> js/d3
                      (.scaleLinear)
                      (.range #js [0 iris-outer-width])))
(def iris-range-y (-> js/d3
                      (.scaleLinear)
                      (.range #js [iris-outer-height 0])))

(def iris-range-r (-> js/d3
                      (.scaleLinear)
                      (.range #js [iris-radius-min iris-radius-max])))

(def iris-range-c (-> js/d3
                      (.scaleOrdinal)
                      (.range (.-schemeCategory10 js/d3))))

(def iris-svg (create-svg! "div#ex-70"))

(defn iris-type-func 
  [datum]
  (let [clj-datum (js->clj datum)]
    (-> clj-datum
        (update iris-row-prop js/parseFloat)
        (update iris-radius-prop js/parseFloat)
        (update iris-column-prop js/parseFloat)
        (update "petal_width" js/parseFloat)
        (clj->js))))

(defn render-70
  [svg data]
  (let [x-domain (-> iris-range-x 
                     (.domain (.extent js/d3 data #(aget %1 iris-row-prop))))
        y-domain (-> iris-range-y 
                     (.domain (.extent js/d3 data #(aget %1 iris-column-prop))))
        r-domain (-> iris-range-r 
                     (.domain (.extent js/d3 data #(aget %1 iris-radius-prop))))
        circles  (-> svg
                    (.selectAll "circle")
                    (.data data))]
        (-> circles
            (.enter)
              (.append "circle")
              (.attr "class" "testing")
            (.merge circles)
              (.attr "fill" #(iris-range-c (aget %1 iris-color-prop)))
              (.attr "cx" #(x-domain (aget %1 iris-row-prop)))
              (.attr "cy" #(y-domain (aget %1 iris-column-prop)))
              (.attr "r" #(r-domain (aget %1 iris-radius-prop))))
        (-> circles
            (.exit)
            (.remove))))

(-> js/d3
    (.csv "iris.csv" iris-type-func #(render-70 iris-svg %1)))


; Example 82
; Graphing data. Doesn't look great as linear scale what to do?
; The trend in the data is easier to see if we swap to a log
; scale for both x and y.
; Example 84
; Showing population as radius of circle scaled linearly results
; in lots of tiny circles and a few large circles.
; Example 85
; Using a log scale for the radius results in a clearer trend.
; it is still a little odd that the pixels in different circles
; count for a different number of people
; Example 86
; Using sqrt scale get something that looks close to pixel 
; equality. Why? Area of circle = pi*r*r

(def gdp-margin  30)
(def gdp-width   (- 300 gdp-margin gdp-margin))
(def gdp-height  (- 250 gdp-margin gdp-margin))
(def gdp-x-col   "population")
(def gdp-y-col   "gdp")
(def gdp-r-max   20)
(def gdp-r-col   "population")
(def ppl-per-pxl 1000000)

(def gdp-svg (create-svg! "div#ex-82"))
(def gdp-g   (-> gdp-svg
                 (.append "g")
                 (.attr "transform" (str "translate(" gdp-margin "," gdp-margin ")"))))

(def gdp-x-scale (-> js/d3
                     (.scaleLog)
                     (.range #js [0 gdp-width])))
(def gdp-y-scale (-> js/d3
                     (.scaleLog)
                     (.range #js [gdp-height 0])))
(def gdp-r-scale (-> js/d3
                     (.scaleSqrt)))

(defn gdp-type-func
  [datum]
  (let [clj-datum (js->clj datum)]
    (-> clj-datum
        (update gdp-x-col js/parseFloat)
        (update gdp-y-col js/parseFloat)
        (clj->js))))
(def PI (.-PI js/Math))
(defn sqrt
  [n]
  (.sqrt js/Math n))

(defn render-gdp
  [data]
  (let [gdp-x-domain (-> gdp-x-scale
                         (.domain (.extent js/d3 data #(aget %1 gdp-x-col))))
        gdp-y-domain (-> gdp-y-scale
                         (.domain (.extent js/d3 data #(aget %1 gdp-y-col))))
        gdp-r-domain (-> gdp-r-scale
                         (.domain (.extent js/d3 data #(aget %1 gdp-r-col))))
        max-pop      (aget (-> gdp-r-scale (.domain)) 1)
        gdp-r-min    0
        gdp-r-max    (sqrt (/ max-pop (* PI ppl-per-pxl)))
        circles  (-> gdp-g
                    (.selectAll "circle")
                    (.data data))]
    (.range gdp-r-scale #js [gdp-r-min gdp-r-max])
    (-> circles
        (.enter)
          (.append "circle")
          (.attr "fill" "black")
        (.merge circles)
          (.attr "r" #(gdp-r-domain (aget %1 gdp-r-col)))
          (.attr "cx" #(gdp-x-domain (aget %1 gdp-x-col)))
          (.attr "cy" #(gdp-y-domain (aget %1 gdp-y-col))))
    (-> circles
        (.exit)
        (.remove))))

(-> js/d3
    (.csv "countries_population_GDP.csv" gdp-type-func render-gdp))

; Examples 94
; Rendering population and location

(def pop-outer-width  500)
(def pop-outer-height 250)
(def pop-margins {:left -50 :top 0 :right -50 :bottom 0})

(def pop-x-column "longitude")
(def pop-y-column "latitude")
(def pop-r-column "population")
(def pop-ppl-per-pxl 100000)

(def pop-inner-width  (- pop-outer-width (:left pop-margins) (:right pop-margins)))
(def pop-inner-height (- pop-outer-height (:top pop-margins) (:bottom pop-margins)))

(def pop-svg (.. js/d3
                 (select "div#ex-94")
                 (append "svg")
                 (attr "width" pop-outer-width)
                 (attr "height" pop-outer-height)))

(def pop-g (.. pop-svg
               (append "g")
               (attr "transform" (str "translate(" (:left pop-margins) "," (:top pop-margins) ")"))))

(def pop-x-scale (.. js/d3
                     (scaleLinear)
                     (range #js [0 pop-inner-width])))

(def pop-y-scale (.. js/d3
                     (scaleLinear)
                     (range #js [pop-inner-height 0])))

(def pop-r-scale (.. js/d3
                     (scaleSqrt)))

(defn pop-type-func
  [datum]
  (let [clj-datum (js->clj datum)]
    (-> clj-datum
        (update pop-x-column js/parseFloat)
        (update pop-y-column js/parseFloat)
        (update pop-r-column js/parseFloat)
        (clj->js))))

(defn pop-renderer
  [data]
  (let [pop-x-domain (-> pop-x-scale
                         (.domain (.extent js/d3 data #(aget %1 pop-x-column))))
        pop-y-domain (-> pop-y-scale
                         (.domain (.extent js/d3 data #(aget %1 pop-y-column))))
        pop-r-domain (-> pop-r-scale
                         (.domain #js [0 (.max js/d3 data #(aget %1 pop-r-column))]))
        max-pop      (aget (-> pop-r-scale (.domain)) 1)
        pop-r-min    0
        pop-r-max    (sqrt (/ max-pop (* PI pop-ppl-per-pxl)))
        circles  (-> pop-g
                    (.selectAll "circle")
                    (.data data))]
(prn max-pop)
    (.range pop-r-scale #js [pop-r-min pop-r-max])
    (-> circles
        (.enter)
          (.append "circle")
          (.attr "fill" "black")
        (.merge circles)
          (.attr "r" #(pop-r-domain (aget %1 pop-r-column)))
          (.attr "cx" #(pop-x-domain (aget %1 pop-x-column)))
          (.attr "cy" #(pop-y-domain (aget %1 pop-y-column))))
    (-> circles
        (.exit)
        (.remove))))

(.. js/d3
    (csv "geonames_cities100000.csv" pop-type-func pop-renderer))
