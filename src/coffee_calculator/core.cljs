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
  [:div {:class "input-container"}
   [:label {:for label}  label]
   [:input {:type "text" :id label :value @value :on-change #(reset! value (-> % .-target .-value))}]])

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
        [:h1 {:class "title"} [:a {:href "/" :rel "noopener noreferrer"} "Coffee Calculator ☕"]]
        [:p "⚠️ Website Under Construction"]
        [:div {:class "settings"}
         [:div {:class "column"}
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
               [:div (formatNumber @coffee-per-cup)]]]]]]]

         [:div {:class "column"}
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
             [:td "1:2"]]]]]]

        [:div "Measurements"]
         [:div {:class "table-container"}
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
           [:td ozToCups]]]]]

        [:div {:class "table-container"}
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

          [calc-rows]]]
        [:div {:class "table-container"}
         [:table
          [:thead
           [:tr
            [:th {:col-span 3} "Factors for units of measures to get Fluid Oz needed"]]
           [:tr
            [:th {:col-span 3} "Multiply unit of measures times Fluid Oz"]]
           [:tr
            [:th "Teaspoons"]
            [:th "Tablespoons"]
            [:th "Cups"]]]

          [:tbody
           [:tr
            [:td "1"]
            [:td "3"]
            [:td "48"]]
           [:tr
            [:td {:col-span 3} "Inverse factors for Fluid oz to get measure of coffee needed"]]
           [:tr
            [:td {:col-span 3} "Multiply Fluid Oz by Inverse Factor to get measure of coffee needed"]]
           [:tr
            [:td "1"]
            [:td "0.3333"]
            [:td "0.0.208"]]]]]

        [:div {:class "table-container"}
         [:table
          [:thead
           [:tr
            [:th {:col-span 6} "Volume Conversions"]]
           [:tr
            [:th "To convert fluid oz to:"]
            [:th "multiply # of Fluid Oz by:"]
            [:th "Inverse factors"]
            [:th "To convert CCs to:"]
            [:th "multiply # of CCs by:"]
            [:th "Inverse factors"]]]

          [:tbody
           [:tr
            [:td "Teaspoons"]
            [:td "6"]
            [:td "0.1667"]
            [:td "Teaspoons"]
            [:td "20.284"]
            [:td "0.0493"]]
           [:tr
            [:td "Tablespoons"]
            [:td "2"]
            [:td "0.5"]
            [:td "Tablespoons"]
            [:td "6.7613"]
            [:td "0.1479"]]
           [:tr
            [:td "Fluid Ounces"]
            [:td "1"]
            [:td "1"]
            [:td "Fluid Ounces"]
            [:td "3.3807"]
            [:td "0.2958"]]
           [:tr
            [:td "1/8 Cups"]
            [:td "1"]
            [:td "1"]
            [:td "1/8 Cups"]
            [:td "3.3807"]
            [:td "0.2958"]]
           [:tr
            [:td "1/4 Cups"]
            [:td "0.5"]
            [:td "2"]
            [:td "1/4 Cups"]
            [:td "1.6903"]
            [:td "0.5916"]]
           [:tr
            [:td "1/3 Cups"]
            [:td "0.375"]
            [:td "2.6667"]
            [:td "1/3 Cups"]
            [:td "1.2678"]
            [:td "0.7888"]]
           [:tr
            [:td "1/2 Cups"]
            [:td "0.25"]
            [:td "4"]
            [:td "1/2 Cups"]
            [:td "0.8452"]
            [:td "1.1832"]]
           [:tr
            [:td "2/3 Cups"]
            [:td "0.1875"]
            [:td "5.3333"]
            [:td "2/3 Cups"]
            [:td "0.6339"]
            [:td "1.5776"]]
           [:tr
            [:td "3/4 Cups"]
            [:td "0.1667"]
            [:td "6"]
            [:td "3/4 Cups"]
            [:td "0.5634"]
            [:td "1.7748"]]
           [:tr
            [:td "Cups"]
            [:td "0.125"]
            [:td "8"]
            [:td "Cups"]
            [:td "0.4226"]
            [:td "2.3664"]]
           [:tr
            [:td "Pints"]
            [:td "0.0625"]
            [:td "16"]
            [:td "Pints"]
            [:td "0.2113"]
            [:td "4.7328"]]
           [:tr
            [:td "Quarts"]
            [:td "0.0313"]
            [:td "32"]
            [:td "Quarts"]
            [:td "0.1056"]
            [:td "9.4656"]]
           [:tr
            [:td "1/2 Gallons"]
            [:td "0.0156"]
            [:td "64"]
            [:td "1/2 Gallons"]
            [:td "0.0528"]
            [:td "18.9312"]]
           [:tr
            [:td "Gallons"]
            [:td "0.0078"]
            [:td "128"]
            [:td "Gallons"]
            [:td "0.0264"]
            [:td "37.8624"]]
           [:tr
            [:td "CCs (Mililiters)"]
            [:td "29.58"]
            [:td "0.0338"]
            [:td "CCs (Mililiters)"]
            [:td "1"]
            [:td "1"]]
           [:tr
            [:td "Liters"]
            [:td "0.0296"]
            [:td "33.8067"]
            [:td "Liters"]
            [:td "0.001"]
            [:td "1000"]]
           [:tr
            [:td {:col-span 6} "To use the inverse factor, multiply the number of units to convert by the inverse factor. For example, to determine how many Fluid oz there are in 37 CCs, multiply 37 time s 0.0338 to get 1.25 Fl oz"]]]]]

        [:p "the proportion of ground coffee used in relation to the amount of water used is the brewing ratio."]
        [:p "the amount of solubles that have been extracted in relation to amount of water after brewing is the drinking ratio."]
        [:p "it's always wiser to brew it on the stronger side and then cut it down to taste by adding water."]
        [:p "water can be added after brewing to reduce concentration, thus changin drinking ratio."]

        ]))))

(comment

  (.render js/ReactDOM
           (.createElement js/React "h3" nil "Hello world")
           (.getElementById js/document "app")))

(rdom/render
 [app]
 (gdom/getElement "app"))
