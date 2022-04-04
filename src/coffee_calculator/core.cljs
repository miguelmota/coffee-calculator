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

; UTILS ===============================

(def nff
  (NumberFormat. Format/DECIMAL))

(defn- nf
  [num]
  (.format nff (str num)))

(defn formatNumber [value]
  (nf (gstring/format "%.3f" (if (js/isNaN value) 0 value))))

(defn formatCurrency [value]
  (nf (gstring/format "%.2f" (if (js/isNaN value) 0 value))))

; FUNCTIONS ===============================

(def filteredDevices (hash-map
                      (keyword "aeropress") [true],
                      (keyword "cezve") [true],
                      (keyword "chemex") [true],
                      (keyword "cold-brew") [true],
                      (keyword "espresso") [true],
                      (keyword "french-press") [true],
                      (keyword "moka-pot") [true],
                      (keyword "siphon") [true],
                      (keyword "hario-v60") [true]))

(def devices (hash-map
              (keyword "aeropress") ["AeroPress"]
              (keyword "april") ["April"]
              (keyword "belgian-siphon") ["Belgian Siphon"]
              (keyword "cafelat-robot") ["Cafelat Robot"]
              (keyword "cafflano-kompact") ["Cafflano Kompact"]
              (keyword "cafflano-kompresso") ["Cafflano Kompresso"]
              (keyword "cezve") ["Cezve"]
              (keyword "chemex") ["Chemex"]
              (keyword "clever-dripper") ["Clever Dripper"]
              (keyword "cold-brew") ["Cold Brew"]
              (keyword "delter-press") ["Delter Press"]
              (keyword "espresso") ["Espresso"]
              (keyword "eva-solo") ["Eva Solo"]
              (keyword "flair") ["Flair"]
              (keyword "french-press") ["French Press"]
              (keyword "gina") ["GINA"]
              (keyword "hario-switch") ["Hario Switch"]
              (keyword "hario-v60") ["Hario V60"]
              (keyword "hario-woodneck") ["Hario Woodneck"]
              (keyword "kalita-wave") ["Kalita Wave"]
              (keyword "kono") ["Kono"]
              (keyword "moka-pot") ["Moka Pot"]
              (keyword "neapolitan-flip") ["Neapolitan Flip"]
              (keyword "origami") ["Origami"]
              (keyword "phin") ["Phin"]
              (keyword "rok") ["ROK"]
              (keyword "siphon") ["Siphon"]
              (keyword "staresso-mini") ["Staresso Mini"]
              (keyword "staresso") ["Staresso"]
              (keyword "staresso-pro") ["Staresso Pro"]
              (keyword "stelton-collar") ["Stelton Collar"]
              (keyword "tricolate") ["Tricolate"]
              (keyword "vacone") ["VacOne"]
              (keyword "yama-siphon") ["Yama Siphon"]))

(def cupSizesOz [[1 "Espresso"]
                 [1.5 "Espresso, Moka Pot (standard)"]
                 [2 "Espresso, Moka Pot"]
                 [2.5 "Espresso"]
                 [3 "Espresso, Cezve"]
                 [3.5 "Espresso, Cezve (standard)"]
                 [4 "Espresso, Ristretto, Cortado, French Press (traditional), Cezve, Yama Siphon"]
                 [5 "Cappuccino, Small latte, Chemex"]
                 [6 "Cappuccino, Small latte"]
                 [7 "Latte, Pour Over"]
                 [8 "Standard cup, Latte, Pour Over, AeroPress Go, French Press, Belgian Siphon"]
                 [9 "Latte, Pour Over"]
                 [10 "Latte, Pour Over, AeroPress"]
                 [11 "Latte, Pour Over"]
                 [12 "Latte, Cold brew"]
                 [13 "XL Latte, Cold brew"]
                 [14 "XL Latte, Cold brew, Iced coffee"]
                 [15 "XL Latte, Cold brew, Iced coffee"]
                 [16 "XL Latte, Cold brew, Iced coffee"]])

(def ratios [[(keyword "aeropress") 16]
             ;["april" 17]
             ;["belgian-siphon" 16],
             ;["cafelat-robot" 2]
             ;["cafflano-kompact" 14]
             ;["cafflano-kompresso" 2]
             [(keyword "cezve") 9]
             [(keyword "chemex") 17]
             ;["clever-dripper" 18]
             [(keyword "cold-brew") 4]
             ;["delter-press" 16]
             [(keyword "espresso") 2]
             ;["eva-solo" 22]
             ;["flair" 2]
             [(keyword "french-press") 12]
             ;["gina" 16]
             ;["hario-switch" 18]
             [(keyword "hario-v60") 15]
             ;["hario-woodneck" 15]
             ;["kalita-wave" 16]
             ;["kono" 16]
             [(keyword "moka-pot") 7]
             ;["neapolitan Flip" 16]
             ;["origami" 16]
             ;["phin" 8]
             ;["rok" 2]
             [(keyword "siphon") 16],
             ;["staresso-mini" 8],
             ;["staresso" 8],
             ;["staresso-pro" 8],
             ;["tricolate", 20]
             ;["vacone", 14]])
             ;["yama-siphon" 16],
             ])

(def brewDeviceCupSizes (hash-map
                         (keyword "aeropress") [1],
                         (keyword "cezve") [1],
                         (keyword "chemex") [3, 6, 8, 10],
                         (keyword "cold-brew") [1, 2, 4, 8],
                         (keyword "espresso") [1, 2],
                         (keyword "french-press") [1, 2, 3, 5, 6],
                         (keyword "moka-pot") [1, 3, 6, 9, 12],
                         (keyword "siphon") [1],
                         (keyword "hario-v60") [1]))

(defn textInput [value, label]
  [:div {:class "input-container"}
   [:label {:for label}  label]
   [:input {:type "text" :id label :value @value :on-change #(reset! value (js/parseInt (-> % .-target .-value)))}]])

(def oneOz 1)
(def ozToGrams (* oneOz 28.3495))
(def ozToTsp (* oneOz 6))
(def ozToTbsp (* oneOz 2))
(def ozToCups (/ oneOz 8))

; VARIABLES ===============================

(defonce brewDeviceInput (r/atom (keyword "aeropress")))
(defonce brewDeviceCupSizeInput (r/atom 1))
(defonce cupSizeOzInput (r/atom 6))
(defonce brewRatioInput (r/atom "17"))
(defonce bagSizeOzInput (r/atom "12"))
(defonce bagCostUsdInput (r/atom "14"))

; COMPONENTS ===============================

(defn app []
  ((let [coffeeGramsPerCup (r/atom (/ (* (js/parseInt @cupSizeOzInput) ozToGrams) (js/parseInt @brewRatioInput)))
         waterGramsPerCup (r/atom (* @coffeeGramsPerCup (js/parseInt @brewRatioInput)))]
     (defn renderHeader []
       [:header {:class "header"}
        [:h1 {:class "title"} [:a {:href "/" :rel "noopener noreferrer"} "Coffee Brew Calculator ☕"]]])
     (defn renderMeasurementRows []
       [:tbody
        (doall (for [index (range 1 19)]
                 (let [coffeeGrams (* index @coffeeGramsPerCup)
                       coffeeOz (* coffeeGrams (/ 1 ozToGrams))
                       coffeeTsp (* coffeeOz ozToTsp)
                       coffeeTbsp (* coffeeOz ozToTbsp)
                       coffeeCups (* coffeeOz ozToCups)
                       waterGrams (* coffeeGrams @brewRatioInput)
                       waterOz (* coffeeGrams (/ @brewRatioInput ozToGrams))
                       waterCups (/ waterOz 8)
                       waterPints (/ waterOz 16)
                       waterQuarts (/ waterOz 32)
                       waterHalfGallon (/ waterOz 64)
                       waterCCMilli (* waterOz 29.574)
                       waterLiters (/ waterOz 33.814)
                       costPerCup (* coffeeGrams (/ @bagCostUsdInput (* @bagSizeOzInput 28.3495)))
                       v (/ (* waterOz 29.574) @brewRatioInput)
                       brewedGrams (/ (- (* waterOz 29.574) (* 1.995 v)) 1)
                       brewedOz (/ (- (* waterOz 29.574) (* 1.995 v)) 29.574)]

                   ^{:key index} [:tr {:class (if (= index @brewDeviceCupSizeInput) "selected" "")}
                                  [:td {:class "table-index-body-1"} [:div index]]
                                  [:td {:class (str "table-coffee-body-1" " bold")} [:div (formatNumber coffeeGrams)]]
                                  [:td {:class "table-coffee-body-1"} [:div (formatNumber coffeeOz)]]
                                  [:td {:class "table-coffee-body-1"} [:div (formatNumber coffeeTsp)]]
                                  [:td {:class "table-coffee-body-1"} [:div (formatNumber coffeeTbsp)]]
                                  [:td {:class "table-coffee-body-1"} [:div (formatNumber coffeeCups)]]
                                  [:td {:class (str "table-water-body-1" " bold")} [:div (formatNumber waterGrams)]]
                                  [:td {:class "table-water-body-1"} [:div (formatNumber waterOz)]]
                                  [:td {:class "table-water-body-1"} [:div (formatNumber waterCups)]]
                                  [:td {:class "table-water-body-1"} [:div (formatNumber waterPints)]]
                                  [:td {:class "table-water-body-1"} [:div (formatNumber waterQuarts)]]
                                  [:td {:class "table-water-body-1"} [:div (formatNumber waterHalfGallon)]]
                                  [:td {:class "table-water-body-1"} [:div (formatNumber waterCCMilli)]]
                                  [:td {:class "table-water-body-1"} [:div (formatNumber waterLiters)]]
                                  [:td {:class "table-brewed-coffee-body-1"} [:div (formatNumber brewedGrams)]]
                                  [:td {:class "table-brewed-coffee-body-1"} [:div (formatNumber brewedOz)]]
                                  [:td {:class "table-cost-body-1"} [:div (formatCurrency costPerCup)]]])))])

     (defn renderBrewDeviceSelection []
       [@brewDeviceInput]
       [:div {:class "brew-device"}
        [:div {:class "column"}
         [:table {:class "table ratios-table"}
          [:thead
           [:tr
            [:th {:col-span 1} (str "Select brew device")]]]
          [:tbody
           (for [[device] (sort filteredDevices)]
             [:tr
              {:class (if (= @brewDeviceInput device) "selected" "")
               :key device :on-click (fn [event] ((reset! brewDeviceInput (-> (keyword device)))
                                                  (reset! brewDeviceCupSizeInput (-> (first ((keyword device) brewDeviceCupSizes))))))}
              [:td
               [:div
                [:img {:class "icon" :alt device :on-error (fn [event]
                                                 (-> event .-target .-style .-display (set! "none"))) :src (str "./images/devices/" (subs (str device) 1) ".webp")}]
                (first ((keyword device) devices))]]])]]]])

     (defn renderBrewDeviceSizeSelection []
       [@brewDeviceCupSizeInput]
       [:div {:class "brew-device-size"}

        [:div {:class "column"}
         [:table {:class "table ratios-table"}
          [:thead
           [:tr
            [:th {:col-span 1} (str "Brew device number of cups")]]]
          [:tbody
           (for [size ((keyword @brewDeviceInput) brewDeviceCupSizes)]
             [:tr
              {:class (if (= @brewDeviceCupSizeInput size) "selected" "")
               :key size :on-click (fn [event] (reset! brewDeviceCupSizeInput (-> size)))}
              [:td
               [:div
                (str size " cup")]]])
           [:tr
            [:td [:div
                  "Other"
                  [textInput brewDeviceCupSizeInput "Number of cups"]]]]]]]])

     (defn renderCupSizeSelection []
       [:div {:class "column"}
        [:table {:class "table ratios-table"}
         [:thead
          [:tr
           [:th {:col-span 2} "Select size of cup"]]
          [:tr
           [:th {:class "left-align"}
            [:div "Ounces (oz)"]]
           [:th {:class "left-align"} "Example drinks"]]]

         [:tbody
          (for [[oz desc] cupSizesOz]
            [:tr
             {:class (if (= @cupSizeOzInput oz) "selected" "")
              :key oz :on-click (fn [event] (reset! cupSizeOzInput (-> oz)))}
             [:td
              [:div
               oz]]

             [:td
              [:div
               desc]]])

          [:tr
           [:td {:colSpan 2} [:div
                              "Other"
                              [textInput cupSizeOzInput "Size of cup (oz)"]]]]]]])

     (defn renderBrewStrength []
       [:div {:class "strength"}

        [:div {:class "column"}
         [:table {:class "table"}
          [:thead
           [:tr
            [:th "Strength of coffee"]]]
          [:tbody
           [:tr
            [:td
             [:div
              "Strength is determined by grind size"]]]]]]])

     (defn renderBrewRatios []
       [@brewDeviceInput]
       [:div {:class "column"}
        [:table {:class "table ratios-table"}
         [:thead
          [:tr
           [:th {:col-span 2} (str "Coffee to water ratios (select one)")]]
          [:tr
           [:th "Brew device"]
           [:th
            [:div "Ratio"]
            [:small "coffee : water"]]]]

         [:tbody
          (for [[device ratio] (filter (fn [[device]] (= device @brewDeviceInput)) ratios)]
            [:tr {:key device :on-click (fn [event] (reset! brewRatioInput (-> ratio)))}
             [:td
              [:div
               (first ((keyword device) devices))]]
             [:td [:div
                   (str "1:" ratio)]]])]]])

     (defn renderSettings []
       [:div {:class "column"}
        [:table {:class "table"}
         [:thead
          [:tr
           [:th
            "Settings"]]]
         [:tbody

          [:tr
           [:td
            [textInput cupSizeOzInput "Cup size (oz) (default 8oz)"]]]

          [:tr
           [:td
            [textInput brewRatioInput, "Brew ratio (g) (default 17g)"]]]

          [:tr
           [:td
            [textInput bagSizeOzInput, "Bag size (oz) (default 12oz)"]]]

          [:tr
           [:td
            [textInput bagCostUsdInput, "Bag cost (USD)"]]]

          [:tr
           [:td
            [:div "Coffee per cup (g)"
             [:div (formatNumber @coffeeGramsPerCup)]]]]

          [:tr
           [:td
            [:div "Water per cup (g)"
             [:div (formatNumber @waterGramsPerCup)]]]]]]])

     (defn renderMeasurementsTable []
       [:div {:class "table-container-container"}
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

          [renderMeasurementRows]]]])

     (defn renderContentText []
       [:div {:class "content"}
        [:p
         "To determine the amount of water to be used with fractional amounts of coffee, multiply the weight of the coffee by the following factors: 16 (0.0625 is the inverse factor) to get fluid ounces of water: 16.6945 (0.0599 is the inverse factor) for grams to get CCs of water."]

        [:p
         "For example: if you have 1.2 ounces of coffee (by weight), you would multiply 1.2 times 16.0 to get 19.2 fluid ounces of water needed. If you’re using the metric system, 92.6 grams of coffee would require 1562 CCs (1.56 liters) of water. Use the inverse factor to determine the amount of coffee to use with an unlisted amount of water. In other words, you multiply the inverse factor times the amount of water to determine the weight of the coffee to be used."]
        [:p
         [:strong
          "It is important to know that measuring by volume is not precise due to the fact that different coffees can have different densities. Measuring by weight is the only way to be precise."]]])

     (defn renderOzConversionsTable []
       [:div {:class "table-container-container"}
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
            [:td [:div oneOz]]
            [:td [:div ozToGrams]]
            [:td [:div ozToTsp]]
            [:td [:div ozToTbsp]]
            [:td [:div ozToCups]]]]]]])

     (defn renderFlOzConversionsTable []
       [:div {:class "table-container-container"}
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
            [:td [:div 1]]
            [:td [:div 3]]
            [:td [:div 48]]]
           [:tr
            [:td {:col-span 3} [:div "Inverse factors for Fluid oz to get measure of coffee needed"]]]
           [:tr
            [:td {:col-span 3} [:div "Multiply Fluid Oz by Inverse Factor to get measure of coffee needed"]]]
           [:tr
            [:td [:div 1]]
            [:td [:div "0.3333"]]
            [:td [:div "0.0.208"]]]]]]])
     (defn renderVolumeConversionsTable []
       [:div {:class "table-container-container"}
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
            [:td [:div "Teaspoons"]]
            [:td [:div "6"]]
            [:td [:div "0.1667"]]
            [:td [:div "Teaspoons"]]
            [:td [:div "20.284"]]
            [:td [:div "0.0493"]]]
           [:tr
            [:td [:div "Tablespoons"]]
            [:td [:div "2"]]
            [:td [:div "0.5"]]
            [:td [:div "Tablespoons"]]
            [:td [:div "6.7613"]]
            [:td [:div "0.1479"]]]
           [:tr
            [:td [:div "Fluid Ounces"]]
            [:td [:div "1"]]
            [:td [:div "1"]]
            [:td [:div "Fluid Ounces"]]
            [:td [:div "3.3807"]]
            [:td [:div "0.2958"]]]
           [:tr
            [:td [:div "1/8 Cups"]]
            [:td [:div "1"]]
            [:td [:div "1"]]
            [:td [:div "1/8 Cups"]]
            [:td [:div "3.3807"]]
            [:td [:div "0.2958"]]]
           [:tr
            [:td [:div "1/4 Cups"]]
            [:td [:div "0.5"]]
            [:td [:div "2"]]
            [:td [:div "1/4 Cups"]]
            [:td [:div "1.6903"]]
            [:td [:div "0.5916"]]]
           [:tr
            [:td [:div "1/3 Cups"]]
            [:td [:div "0.375"]]
            [:td [:div "2.6667"]]
            [:td [:div "1/3 Cups"]]
            [:td [:div "1.2678"]]
            [:td [:div "0.7888"]]]
           [:tr
            [:td [:div "1/2 Cups"]]
            [:td [:div "0.25"]]
            [:td [:div "4"]]
            [:td [:div "1/2 Cups"]]
            [:td [:div "0.8452"]]
            [:td [:div "1.1832"]]]
           [:tr
            [:td [:div "2/3 Cups"]]
            [:td [:div "0.1875"]]
            [:td [:div "5.3333"]]
            [:td [:div "2/3 Cups"]]
            [:td [:div "0.6339"]]
            [:td [:div "1.5776"]]]
           [:tr
            [:td [:div "3/4 Cups"]]
            [:td [:div "0.1667"]]
            [:td [:div "6"]]
            [:td [:div "3/4 Cups"]]
            [:td [:div "0.5634"]]
            [:td [:div "1.7748"]]]
           [:tr
            [:td [:div "Cups"]]
            [:td [:div "0.125"]]
            [:td [:div "8"]]
            [:td [:div "Cups"]]
            [:td [:div "0.4226"]]
            [:td [:div "2.3664"]]]
           [:tr
            [:td [:div "Pints"]]
            [:td [:div "0.0625"]]
            [:td [:div "16"]]
            [:td [:div "Pints"]]
            [:td [:div "0.2113"]]
            [:td [:div "4.7328"]]]
           [:tr
            [:td [:div "Quarts"]]
            [:td [:div "0.0313"]]
            [:td [:div "32"]]
            [:td [:div "Quarts"]]
            [:td [:div "0.1056"]]
            [:td [:div "9.4656"]]]
           [:tr
            [:td [:div "1/2 Gallons"]]
            [:td [:div "0.0156"]]
            [:td [:div "64"]]
            [:td [:div "1/2 Gallons"]]
            [:td [:div "0.0528"]]
            [:td [:div "18.9312"]]]
           [:tr
            [:td [:div "Gallons"]]
            [:td [:div "0.0078"]]
            [:td [:div "128"]]
            [:td [:div "Gallons"]]
            [:td [:div "0.0264"]]
            [:td [:div "37.8624"]]]
           [:tr
            [:td [:div "CCs (Mililiters)"]]
            [:td [:div "29.58"]]
            [:td [:div "0.0338"]]
            [:td [:div "CCs (Mililiters)"]]
            [:td [:div "1"]]
            [:td [:div "1"]]]
           [:tr
            [:td [:div "Liters"]]
            [:td [:div "0.0296"]]
            [:td [:div "33.8067"]]
            [:td [:div "Liters"]]
            [:td [:div "0.001"]]
            [:td [:div "1000"]]]]]]])

     (defn renderMoreTextContent []
       [:div {:class "content"}
        [:p "To use the inverse factor, multiply the number of units to convert by the inverse factor. For example, to determine how many Fluid oz there are in 37 CCs, multiply 37 time s 0.0338 to get 1.25 Fl oz"]
        [:p "The proportion of ground coffee used in relation to the amount of water used is the brewing ratio."]
        [:p "The amount of solubles that have been extracted in relation to amount of water after brewing is the drinking ratio."]
        [:p "It's always wiser to brew it on the stronger side and then cut it down to taste by adding water."]
        [:p "Water can be added after brewing to reduce concentration, thus changin drinking ratio."]])

     (defn renderFooter []
       [:footer {:class "footer"}
        (str "©" (.getFullYear (new js/Date)) " ")
        [:a {:href "https://github.com/miguelmota" :rel "noopener noreferrer" :target "blank"} "Miguel Mota"]])

     (fn []
       [:main
        [:div {:class "inner"}
         [renderHeader]
         [renderBrewDeviceSelection]
         [renderBrewDeviceSizeSelection]
         [renderCupSizeSelection]
         [renderBrewStrength]
         [renderBrewRatios]
         [renderSettings]
         [renderMeasurementsTable]
         [renderContentText]
         [renderOzConversionsTable]
         [renderFlOzConversionsTable]
         [renderVolumeConversionsTable]
         [renderMoreTextContent]
         [renderFooter]]]))))

; RENDER ===============================

(rdom/render
 [app]
 (gdom/getElement "app"))

