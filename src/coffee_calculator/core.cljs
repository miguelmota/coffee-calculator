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

; strong 1:12 normal 1:16 weak 1:20
; add pounds and kilos to ground coffee
; add gallons to water and brewed coffee
; slider for strength

(def cups [[1 "Espresso"]
           [2 "Espresso"]
           [3 "Espresso"]
           [4 "Espresso, Ristretto, Cortado"]
           [5 "Cappuccino, Sm latte"]
           [6 "Cappuccino, Sm latte"]
           [7 "Latte"]
           [8 "Latte"]
           [9 "Latte"]
           [10 "Latte"]
           [11 "Latte"]
           [12 "Latte, Cold brew"]
           [13 "XL Latte, Cold brew"]
           [14 "XL Latte, Cold brew, Iced coffee"]
           [15 "XL Latte, Cold brew, Iced coffee"]
           [16 "XL Latte, Cold brew, Iced coffee"]])

(def ratios [["AeroPress" 16]
             ["French Press" 12]
             ["V60" 15]
             ["Chemex" 17]
             ["Moka Pot" 7]
             ["Cold Brew" 4]
             ["Siphon" 16]
             ["Espresso" 2]])

(defn handle-input [event]
  (println event))

(defn text-input [value, label]
  [:div {:class "input-container"}
   [:label {:for label}  label]
   [:input {:type "text" :id label :value @value :on-change #(reset! value (-> % .-target .-value))}]])

(defonce cup-size-input (r/atom "6"))
(defonce brew-ratio-input (r/atom "17"))
(defonce bag-size-oz (r/atom "12"))
(defonce bag-cost (r/atom "14"))

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

(defn formatCurrency [value]
  (nf (gstring/format "%.2f" (if (js/isNaN value) 0 value))))

(defn app []
  ((
    let [coffee-per-cup (r/atom (/ (* (js/parseInt @cup-size-input) ozToGrams) (js/parseInt @brew-ratio-input)))
         water-per-cup (r/atom (* @coffee-per-cup (js/parseInt @brew-ratio-input)))
        ]
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
                       waterCups (/ waterOz 8)
                       waterPints (/ waterOz 16)
                       waterQuarts (/ waterOz 32)
                       waterHalfGallon (/ waterOz 64)
                       waterCCMilli (* waterOz 29.574)
                       waterLiters (/ waterOz 33.814)
                       costPerCup (* coffeeGrams (/ @bag-cost (* @bag-size-oz 28.3495)))
                       v (/ (* waterOz 29.574) @brew-ratio-input)
                       brewedGrams (/ (- (* waterOz 29.574) (* 1.995 v)) 1)
                       brewedOz (/ (- (* waterOz 29.574) (* 1.995 v)) 29.574)]

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
                                  [:td {:class "table-brewed-coffee-body-1"} (formatNumber brewedGrams)]
                                  [:td {:class "table-brewed-coffee-body-1"} (formatNumber brewedOz)]
                                  [:td {:class "table-cost-body-1"} (formatCurrency costPerCup)]])))])

     (fn []
       [:main
        [:header {:class "header"}
         [:h1 {:class "title"} [:a {:href "/" :rel "noopener noreferrer"} "Coffee Calculator ☕"]]]
        [:div {:class "settings"}
         [:div {:class "column"}
          [:table {:class "table ratios-table"}
           [:thead
            [:tr
             [:th {:col-span 2} "Cup sizes (select one)"]]
            [:tr
             [:th {:class "left-align"}
              [:div "Ounces (oz)"]]
             [:th {:class "left-align"} "Example drinks"]]]

           [:tbody
            (for [[oz desc] cups]
              [:tr {:key oz :on-click (fn [event] (reset! cup-size-input (-> oz)))}
               [:td oz]
               [:td desc]])]]]

         [:div {:class "column"}
          [:table {:class "table ratios-table"}
           [:thead
            [:tr
             [:th {:col-span 2} "Coffee to water ratios (select one)"]]
            [:tr
             [:th "Brew device"]
             [:th
              [:div "Ratio"]
              [:small "coffee : water"]]]]

           [:tbody
            (for [[device ratio] ratios]
              [:tr {:key device :on-click (fn [event] (reset! brew-ratio-input (-> ratio)))}
               [:td device]
               [:td (str "1:" ratio)]])]]]

        [:div {:class "column"}
         [:table {:class "table"}
          [:thead
           [:tr
            [:th
             "Settings"]]]
          [:tbody

           [:tr
            [:td
             [text-input cup-size-input "Cup size (oz) (default 8oz)"]]]

           [:tr
            [:td
             [text-input brew-ratio-input, "Brew ratio (g) (default 17g)"]]]

           [:tr
            [:td
             [text-input bag-size-oz, "Bag size (oz) (default 12oz)"]]]

           [:tr
            [:td
             [text-input bag-cost, "Bag cost (USD)"]]]

           [:tr
            [:td
             [:div "Coffee per cup (g)"
              [:div (formatNumber @coffee-per-cup)]]]]

           [:tr
            [:td
             [:div "Water per cup (g)"
              [:div (formatNumber @water-per-cup)]]]]

           ]]]]

        [:div {:class "table-container"}
         [:table {:class "table"}
          [:thead
           [:tr
            [:th {:class "table-index-header-2"} "Cups of coffee"]
            [:th {:col-span 5, :class "table-coffee-header-1"} "Coffee to be used"]
            [:th {:col-span 8, :class "table-water-header-1"} "Water to be used"]
            [:th {:col-span 2, :class "table-brewed-coffee-header-1"} "Brewed coffee yield"]
            [:th {:col-span 1, :class "table-cost-header-1"} "Cost per cup"]]
           [:tr
            [:th {:class "table-index-header-2"} ""]
            [:th {:col-span 2, :class "table-coffee-header-2"} "By weight"]
            [:th {:col-span 3, :class "table-coffee-header-2"} "By measurement"]
            [:th {:col-span 8, :class "table-water-header-2"} "By volume"]
            [:th {:col-span 2, :class "table-brewed-coffee-header-2"} "By volume"]
            [:th {:col-span 1, :class "table-cost-header-1"} ""]]
           [:tr
            [:th {:class "table-index-header-2"} ""]
            [:th {:class "table-coffee-body-1 bold left-align"} "Grams (g)"]
            [:th {:class "table-coffee-body-1 left-align"} "Ounces (oz)"]
            [:th {:class "table-coffee-body-1 left-align"} "Teaspoons (tsp)"]
            [:th {:class "table-coffee-body-1 left-align"} "Tablespoons (tbsp)"]
            [:th {:class "table-coffee-body-1 left-align"} "Cups"]
            [:th {:class "table-water-body-1 bold left-align"} "Grams (g)"]
            [:th {:class "table-water-body-1 left-align"} "Fluid Ounces (fl oz)"]
            [:th {:class "table-water-body-1 left-align"} "Cups"]
            [:th {:class "table-water-body-1 left-align"} "Pints"]
            [:th {:class "table-water-body-1 left-align"} "Quarts"]
            [:th {:class "table-water-body-1 left-align"} "1/2 Gallon"]
            [:th {:class "table-water-body-1 left-align"} "CCs (Millimeters)"]
            [:th {:class "table-water-body-1 left-align"} "Liters"]
            [:th {:class "table-brewed-coffee-body-1 left-align"} "Grams (g)"]
            [:th {:class "table-brewed-coffee-body-1 left-align"} "Fluid ounces (fl oz)"]
            [:th {:class "table-cost-body-1 left-align"} "USD"]]]

          [calc-rows]]]

        [:div {:class "content"}
         [:p
          "To determine the amount of water to be used with fractional amounts of coffee, multiply the weight of the coffee by the following factors: 16 (0.0625 is the inverse factor) to get fluid ounces of water: 16.6945 (0.0599 is the inverse factor) for grams to get CCs of water."]

         [:p
          "For example: if you have 1.2 ounces of coffee (by weight), you would multiply 1.2 times 16.0 to get 19.2 fluid ounces of water needed. If you’re using the metric system, 92.6 grams of coffee would require 1562 CCs (1.56 liters) of water. Use the inverse factor to determine the amount of coffee to use with an unlisted amount of water. In other words, you multiply the inverse factor times the amount of water to determine the weight of the coffee to be used."]
         [:p
          [:strong
           "It is important to know that measuring by volume is not precise due to the fact that different coffees can have different densities. Measuring by weight is the only way to be precise."]]]

        [:div {:class "table-container"}
         [:table {:class "table"}
          [:thead
           [:tr
            [:th {:col-span 5} "Measurements"]]
           [:tr
            [:th {:class "left-align"} "ounces (oz)"]
            [:th {:class "left-align"} "Grams (g)"]
            [:th {:class "left-align"} "Teaspoons (tsp)"]
            [:th {:class "left-align"} "Tablespoons (tbsp)"]
            [:th {:class "left-align"} "Cups"]]]

          [:tbody
           [:tr
            [:td oneOz]
            [:td ozToGrams]
            [:td ozToTsp]
            [:td ozToTbsp]
            [:td ozToCups]]]]]

        [:div {:class "table-container"}
         [:table {:class "table"}
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
         [:table {:class "table"}
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
            [:td "1000"]]]]]

        [:div {:class "content"}
         [:p "To use the inverse factor, multiply the number of units to convert by the inverse factor. For example, to determine how many Fluid oz there are in 37 CCs, multiply 37 time s 0.0338 to get 1.25 Fl oz"]
         [:p "The proportion of ground coffee used in relation to the amount of water used is the brewing ratio."]
         [:p "The amount of solubles that have been extracted in relation to amount of water after brewing is the drinking ratio."]
         [:p "It's always wiser to brew it on the stronger side and then cut it down to taste by adding water."]
         [:p "Water can be added after brewing to reduce concentration, thus changin drinking ratio."]]
        [:footer {:class "footer"}
         "©2021 CoffeeCalculator.net"]]))))

(comment

  (.render js/ReactDOM
           (.createElement js/React "h3" nil "Hello world")
           (.getElementById js/document "app")))

(rdom/render
 [app]
 (gdom/getElement "app"))
