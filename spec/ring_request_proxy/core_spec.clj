(ns ring-request-proxy.core-spec
  (:require [speclj.core :refer :all]
            [ring-request-proxy.core :refer [proxy-request-by-server]]
            [clj-http.fake :refer [with-fake-routes-in-isolation]]))

(def hello-request
  {"hello.com/gatekeeper" {:get (fn [req]
                                  {:status 200 :body "{}"})}})

(context "ring-request-proxy.core"
  (describe "proxy-request-by-server"
    (context "when forwarding server is not found"
      (with proxy-with-handler-fn (proxy-request-by-server (constantly {:status 200}) {}))
      (with proxy-fn (proxy-request-by-server {}))

      (it "is not found when no optional handler supplied"
        (should= 404
          (:status (@proxy-fn {:server-name "here" :request-method :get}))))

      (it "responds with the optional handler when present"
        (should= 200
          (:status (@proxy-with-handler-fn {:server-name "here" :request-method :get})))))

    (context "when forwarding server is found"
      (with proxy-fn (proxy-request-by-server {"hello" "http://hello.com"}))
      (with response
        (with-fake-routes-in-isolation hello-request
          (@proxy-fn {:server-name "hello"
                      :request-method :get
                      :uri "/gatekeeper"})))
      (it "forwards the response status"
        (should= 200
                 (:status @response)))

      (it "forwards the response body as an input stream"
        (should= "{}"
                 (slurp (:body @response)))))))

(run-specs)
