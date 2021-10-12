open:
	clojure -M --main cljs.main --compile coffee-calculator.core --repl

build:
	clojure -M --main cljs.main --compile coffee-calculator.core

format:
	lein cljfmt fix
