serve:
	(cd public && python -m http.server --bind 0.0.0.0 8000)

build:
	rm public/js/main.js
	lein cljsbuild once

watch:
	lein cljsbuild auto

lint:
	lein cljfmt fix

open-old:
	clojure -M --main cljs.main --compile coffee-calculator.core --repl

build-old:
	clojure -M --main cljs.main --compile coffee-calculator.core
