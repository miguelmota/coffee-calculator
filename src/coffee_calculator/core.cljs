(ns coffee-calculator.core
  (:require [react-dom]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [goog.dom :as gdom]
            [goog.string :as gstring]
            [goog.string.format]
            [goog.i18n.NumberFormat.Format])
  (:import
   (goog.i18n NumberFormat)
   (goog.i18n.NumberFormat Format)))

(def nff
  (NumberFormat. Format/DECIMAL))

(defn- nf
  [num]
  (.format nff (str num)))

(println "hello world")
(js/console.log "hello world")

(defn average [a b]
  (/ (+ a b) 2.0))

(println (average 20 13))

(defn handle-input [event]
  (println event))

(defn text-input [value, label]
  [:div
   [:div label]
   [:input {:type "text" :value @value :on-change #(reset! value (-> % .-target .-value))}]])

(defonce cup-size-input (r/atom "6"))
(defonce brew-ratio-input (r/atom "17"))

(def oneOz 1)
(def ozToGrams (* oneOz 28.3495))
(def ozToTsp (* oneOz 6))
(def ozToTbsp (* oneOz 2))
(def ozToCups (/ oneOz 8))

(defn lister [items]
  [:ul
   (for [item items]
     ^{:key item} [:li "Item " item])])

(defn formatNumber [value]
  (nf (gstring/format "%.3f" (if (js/isNaN value) 0 value))))

(defn app []
  ((let [coffee-per-cup (r/atom (/ (* (js/parseInt @cup-size-input) ozToGrams) (js/parseInt @brew-ratio-input)))]
     (defn calc-rows []
       [:tbody
        (doall (for [index (range 1 19)]

                 (let [coffeeGrams (* index @coffee-per-cup)
                       coffeeOz (* coffeeGrams (/ 1 ozToGrams))
                       coffeeTsp (* coffeeOz ozToTsp)
                       coffeeTbsp (* coffeeOz ozToTbsp)
                       coffeeCups (* coffeeOz ozToCups)
                       waterGrams (* coffeeGrams @brew-ratio-input)
                       waterOz (* coffeeGrams (/ @brew-ratio-input ozToGrams))
                       waterCups (* index 0.75)
                       waterPints (* index 0.375)
                       waterQuarts (* index 0.1875)
                       waterHalfGallon (* index 0.09375)
                       waterCCMilli (* index 177.44)
                       waterLiters (* index 0.177441)]

                   ^{:key index} [:tr
                                  [:td {:class "table-index-body-1"} index]
                                  [:td {:class (str "table-coffee-body-1" " bold")} (formatNumber coffeeGrams)]
                                  [:td {:class "table-coffee-body-1"} (formatNumber coffeeOz)]
                                  [:td {:class "table-coffee-body-1"} (formatNumber coffeeTsp)]
                                  [:td {:class "table-coffee-body-1"} (formatNumber coffeeTbsp)]
                                  [:td {:class "table-coffee-body-1"} (formatNumber coffeeCups)]
                                  [:td {:class (str "table-water-body-1" " bold")} (formatNumber waterGrams)]
                                  [:td {:class "table-water-body-1"} (formatNumber waterOz)]
                                  [:td {:class "table-water-body-1"} (formatNumber waterCups)]
                                  [:td {:class "table-water-body-1"} (formatNumber waterPints)]
                                  [:td {:class "table-water-body-1"} (formatNumber waterQuarts)]
                                  [:td {:class "table-water-body-1"} (formatNumber waterHalfGallon)]
                                  [:td {:class "table-water-body-1"} (formatNumber waterCCMilli)]
                                  [:td {:class "table-water-body-1"} (formatNumber waterLiters)]
                                  [:td {:class "table-brewed-coffee-body-1"} (formatNumber 0)]
                                  [:td {:class "table-brewed-coffee-body-1"} (formatNumber 0)]
                                  [:td {:class "table-cost-body-1"} (formatNumber  0)]])))])

     (fn []
       [:main
        [:h1 "Coffee Brew Calculator"]
        [:div "coffeecalculator.net"]
        [:div "Settings"]
        [:table
         [:tbody

          [:tr
           [:td
            [text-input cup-size-input "cup size (oz) (default 8oz)"]]]

          [:tr
           [:td
            [text-input brew-ratio-input, "brew ratio (g) (default 17g)"]]]

          [:tr
           [:td
            [:div "coffee per cup (g)"
             [:div (formatNumber @coffee-per-cup)]]]]]]

        [:div "Coffee to water ratios"]
        [:table
         [:thead
          [:tr
           [:td "Brew device"]
           [:td "Ratio"]]]

         [:tbody
          [:tr
           [:td "AeroPress"]
           [:td "1:6"]]
          [:tr
           [:td "French Press"]
           [:td "1:12"]]

          [:tr
           [:td "V60"]
           [:td "3:50"]]

          [:tr
           [:td "Chemex"]
           [:td "1:17"]]

          [:tr
           [:td "Moka Pot"]
           [:td "1:10"]]

          [:tr
           [:td "Cold Brew"]
           [:td "9:40"]]

          [:tr
           [:td "Siphon"]
           [:td "3:50"]]

          [:tr
           [:td "Espresso"]
           [:td "1:2"]]]]

        [:div "Measurements"]
        [:table
         [:thead
          [:tr
           [:td "ounces (oz)"]
           [:td "grams (g)"]
           [:td "teaspoons (tsp)"]
           [:td "tablespoons (tbsp)"]
           [:td "cups"]]]

         [:tbody
          [:tr
           [:td oneOz]
           [:td ozToGrams]
           [:td ozToTsp]
           [:td ozToTbsp]
           [:td ozToCups]]]]

        [:table
         [:thead
          [:tr
           [:th {:class "table-index-header-2"} "Cups of coffee"]
           [:th {:col-span 5, :class "table-coffee-header-1"} "Coffee to be used"]
           [:th {:col-span 8, :class "table-water-header-2"} "Water to be used"]
           [:th {:col-span 2, :class "table-brewed-coffee-header-1"} "Brewed coffee yield"]
           [:th {:col-span 1, :class "table-cost-header-1"} "Cost per cup"]]
          [:tr
           [:th {:class "table-index-header-2"} ""]
           [:th {:col-span 2, :class "table-coffee-header-2"} "by weight"]
           [:th {:col-span 3, :class "table-coffee-header-2"} "by measurement"]
           [:th {:col-span 8, :class "table-water-header-2"} ""]
           [:th {:col-span 2, :class "table-brewed-coffee-header-2"} ""]
           [:th {:col-span 1, :class "table-cost-header-2"} ""]]
          [:tr
           [:th {:class "table-index-header-2"} ""]
           [:th {:class (str "table-coffee-body-1" " bold")} "grams (g)"]
           [:th {:class "table-coffee-body-1"} "ounces (oz)"]
           [:th {:class "table-coffee-body-1"} "teaspoons (tsp)"]
           [:th {:class "table-coffee-body-1"} "tablespoons (tbsp)"]
           [:th {:class "table-coffee-body-1"} "cups"]
           [:th {:class (str "table-water-body-1" " bold")} "grams (g)"]
           [:th {:class "table-water-body-1"} "fluid ounces (fl oz)"]
           [:th {:class "table-water-body-1"} "cups"]
           [:th {:class "table-water-body-1"} "pints"]
           [:th {:class "table-water-body-1"} "quarts"]
           [:th {:class "table-water-body-1"} "1/2 gallon"]
           [:th {:class "table-water-body-1"} "CCs millimeters"]
           [:th {:class "table-water-body-1"} "liters"]
           [:th {:class "table-brewed-coffee-body-1"} "grams (g)"]
           [:th {:class "table-brewed-coffee-body-1"} "fluid ounces (fl oz)"]
           [:th {:class "table-cost-body-1"} "$ (USD)"]]]

         [calc-rows]]

        [lister (list 2 3 5)]]))))

(comment

  (.render js/ReactDOM
           (.createElement js/React "h3" nil "Hello world")
           (.getElementById js/document "app")))

(rdom/render
 [app]
 (gdom/getElement "app"))
