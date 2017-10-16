(defproject ring-request-proxy "0.1.6"
  :description "Ring request proxy"
  :url "https://github.com/FundingCircle/ring-request-proxy"
  :license {:name "BSD 3-clause"
            :url "http://opensource.org/licenses/BSD-3-Clause"}
  :plugins [[speclj "3.2.0"]]
  :profiles {:dev {:dependencies [[speclj "3.2.0"]
                                  [speclj-junit "0.0.11"]
                                  [clj-http-fake "1.0.1"]]}}
  :test-paths ["spec"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-http "1.1.2"]
                 [org.clojure/data.json "0.2.6"]]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "--no-sign"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]
  :repositories [["snapshots" {:sign-releases false
                               :url           "https://clojars.org/repo"
                               :username      [:gpg :env]
                               :password      [:gpg :env]}]
                 ["releases"  {:sign-releases false
                               :url           "https://clojars.org/repo"
                               :username      [:gpg :env]
                               :password      [:gpg :env]}]])
