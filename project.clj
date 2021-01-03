(defproject roam-timeline "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [me.raynes/fs "1.4.6"]
                 [org.clojure/data.json "1.0.0"]
                 [metasoarous/oz "1.6.0-alpha6"]
                 ]
  :main ^:skip-aot roam-timeline.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})


