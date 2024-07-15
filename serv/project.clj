(defproject serv "0.0.1-NYANSQ"
  :description "A simple http server"
  :url "http://example.com/FIXME"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.11.1"],
                  [hiccup "2.0.0-RC3"],
                  [http-kit "2.8.0"],
                  [compojure "1.7.1"]]
  :main ^:skip-aot serv.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
