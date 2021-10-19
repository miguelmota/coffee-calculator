build:
	rm public/js/main.js
	lein cljsbuild once

watch:
	lein cljsbuild once

format:
	lein cljfmt fix

open-old:
	clojure -M --main cljs.main --compile coffee-calculator.core --repl

build-old:
	clojure -M --main cljs.main --compile coffee-calculator.core
