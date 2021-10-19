(defproject coffee-calculator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
    [org.clojure/clojure "1.10.3"]
    [org.clojure/clojurescript "1.10.879"]
    [reagent "1.1.0"]
    [reagent-utils "0.3.3"]
    [cljsjs/react "17.0.2-0"]
    [cljsjs/react-dom "17.0.2-0"]
  ]
  :repl-options {:init-ns coffee-calculator.core}
  :plugins [[lein-cljfmt "0.8.0"] [lein-cljsbuild "1.1.8"]]
  :cljsbuild {
    :builds [{
        ; The path to the top-level ClojureScript source directory:
        :source-paths ["src"]
        ; The standard ClojureScript compiler options:
        ; (See the ClojureScript compiler documentation for details.)
        :compiler {
          :output-to "public/js/main.js"  ; default: target/cljsbuild-main.js
          :optimizations :advanced
          :pretty-print false}}]}
  )
