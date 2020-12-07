(defproject items "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [duct/core "0.8.0"]
                 [duct/module.logging "0.5.0"]
                 ;[com.fzakaria/slf4j-timbre "0.3.14"]
                 [duct/module.sql "0.6.1"]
                 [org.postgresql/postgresql "42.2.18"]
                 [hawk "0.2.11"]
                 [com.layerware/hugsql "0.5.1"]
                 ;; library by myself
                 [com.github.caesarhu/shun "v0.1.1"]
                 [funcool/datoteka "1.2.0"]
                 [clojure.java-time "0.3.2"]
                 [cheshire "5.10.0"]
                 [clj-bom "0.1.2"]
                 [org.clojure/data.csv "1.0.0"]
                 [com.rpl/specter "1.1.3"]
                 [com.draines/postal "2.0.4"]]
  :repositories [["jitpack" "https://jitpack.io"]]
  :plugins [[duct/lein-duct "0.12.1"]]
  :main ^:skip-aot items.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.3.2"]
                                   [orchestra "2020.09.18-1"]
                                   [eftest "0.5.9"]]}})
