(ns flintstones.core
  (:require
    [devtools.core :as devtools]
    [oops.core :as oops]
    [reagent.core :as r]
    ))

(enable-console-print!)
(devtools/install!)

(println
"This text is printed from src/flintstones/core.cljs.
Go ahead and edit it and see reloading in action. Again, or not.")

(println "*** Hello World! 1.10.520 ***  ")

(def states-all
  ["Alabama" "Alaska" "Arizona" "Arkansas" "California"
   "Colorado" "Connecticut" "Delaware" "Florida" "Georgia" "Hawaii"
   "Idaho" "Illinois" "Indiana" "Iowa" "Kansas" "Kentucky" "Louisiana"
   "Maine" "Maryland" "Massachusetts" "Michigan" "Minnesota"
   "Mississippi" "Missouri" "Montana" "Nebraska" "Nevada" "New Hampshire"
   "New Jersey" "New Mexico" "New York" "North Carolina" "North Dakota"
   "Ohio" "Oklahoma" "Oregon" "Pennsylvania" "Rhode Island"
   "South Carolina" "South Dakota" "Tennessee" "Texas" "Utah" "Vermont"
   "Virginia" "Washington" "West Virginia" "Wisconsin" "Wyoming" ])

(def states-curr-max-display 10)

(def states-curr
  "The current list of (autocomplete) states to display"
  (r/atom []))

(defn states-autocomplete-list []
  [:ul {:id :states-keep}
   (let [list-items (vec (for [state (take states-curr-max-display @states-curr)]
                           ^{:key state} [:li {:on-click #(let [elem (js/document.getElementById "myInput")]
                                                            (oops/oset! elem :value state)
                                                            ; or (goog.object/set! #js {:foo 1} :bar 2)  ; #todo test this
                                                            (reset! states-curr []))
                                               :class    [:states-list] ; <= replace with anything
                                               :style    {; :color :cyan
                                                          :list-style-position :outside
                                                          :list-style-type     :none
                                                          :border              "1px solid black"
                                                          ; :border-bottom       :none
                                                          ; need CSS ' ul li:last-child { border-bottom: "1px solid black" '
                                                          }}
                                          state]))
         list-items (if (and (< states-curr-max-display (count @states-curr))
                             (pos? (count list-items)))
                      (conj list-items
                            ^{:key "..."} [:li {:class [:states-list] ; <= replace with anything
                                                :style {:list-style-position :outside
                                                        :list-style-type     :none
                                                        :border              "1px solid black"}}
                                           "..."])
                      list-items)]
     (seq list-items)  ; reagent needs a seq here; will fail if return a vector
   )])

(defn simple-component []
  [:div
   [:p "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red"] " text."]
   [:form {:autoComplete "off" }
    [:div {:class     "autocomplete" :style {:width "300px"}
           :on-change (fn [arg]
                        (println)
                        (let [curr-text   (-> arg .-target .-value)
                              repat       (re-pattern (str "(?i)\\b\\w*" curr-text "\\w*\\b"))
                              keep?       (fn [state] (re-find repat state))
                              states-keep (if (pos? (count curr-text))
                                            (vec (filter keep? states-all))
                                            []) ]
                          (println "states-keep = " states-keep)
                          (reset! states-curr states-keep))) }

     [:input {:id "myInput" :type "text" :name "myState" :placeholder "State"} ]]
     [states-autocomplete-list]
     [:input {:type "submit"}] ] ])

(defonce reload-counter (atom 0))

(defn figwheel-reload []
  (comment ; Called via this line in `project.clj`:
    :on-jsload "flintstones.core/figwheel-reload" )

  ; optionally touch your app-state to force rerendering depending on your application
  ; (swap! app-state update-in [:__figwheel_counter] inc)
  (println "figwheel-reload:  count =" (swap! reload-counter inc)))

(defn app-start
  "Initiates the cljs application. Called upon page load/reload."
  []
  (println "***** app-start - enter *****")
  (r/render [simple-component] (js/document.getElementById "tgt-div"))
  (println "***** app-start - leave *****"))

;***************************************************************************************************
; kick off the app when this file is loaded
(app-start)


(comment
  ; was working on refining this, but looks like timbre is better
  (ns logging-demo
    (:require
      [goog.log :as glog]
      [tupelo.core :as t])
    (:import goog.debug.Console))

  (enable-console-print!)

  (println :awt-minerva-log--top)
  (t/spy :awt-minerva-log--top-spy)

  (def logger (glog/getLogger "minerva.config"))

  (def goog-log-levels {:off     goog.debug.Logger.Level.OFF ; never used, so supresses all log msgs

                        :shout   goog.debug.Logger.Level.SHOUT ; highest value for log msgs
                        :severe  goog.debug.Logger.Level.SEVERE
                        :warning goog.debug.Logger.Level.WARNING
                        :info    goog.debug.Logger.Level.INFO
                        :config  goog.debug.Logger.Level.CONFIG
                        :fine    goog.debug.Logger.Level.FINE
                        :finer   goog.debug.Logger.Level.FINER
                        :finest  goog.debug.Logger.Level.FINEST ;lowest value for log msgs

                        :all     goog.debug.Logger.Level.ALL ; never used, so allows all log msgs
                        })

  (defn log-to-console! []
    (.setCapturing (goog.debug.Console.) true))

  (defn set-level! [level]
    (.setLevel logger (get goog-log-levels level (:info goog-log-levels))))

  (log-to-console!)
  (set-level! :info)

  (defn error [msg] (glog/error logger msg))
  (defn warning [msg] (glog/warning logger msg))
  (defn debug [msg] (glog/fine logger msg))
  (defn info [msg] (glog/info logger msg))
  (defn config [msg] (glog/config logger msg))
  (defn fine [msg] (glog/fine logger msg))
  (defn finer [msg] (glog/finer logger msg))
  (defn finest [msg] (glog/finest logger msg))


)