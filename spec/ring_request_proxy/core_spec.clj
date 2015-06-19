(ns ring-request-proxy.core-spec
  (:require [speclj.core :refer :all]
            [ring-request-proxy.core :refer [proxy-request]]
            [clj-http.fake :refer [with-fake-routes-in-isolation]]))

(def hello-request
  {"hello.com/gatekeeper" {:get (fn [req]
                                  {:status 200
                                   :headers {:content-type "application/json"}
                                   :body "{}"})}})

(def post-request
  {"hello.com/postit" {:post (fn [req]
                               (if (get-in req [:headers "content-length"])
                                 (throw (Exception. "content-length header already present"))
                                 {:status 201 :body (slurp (:body req))}))}})

(context "ring-request-proxy.core"
  (describe "proxy-request"
    (context "when forwarding server is not found"
      (with proxy-with-handler-fn (proxy-request (constantly {:status 200})
                                                 {:identifier-fn :server-name
                                                  :host-fn {}}))
      (with proxy-fn (proxy-request {:identifier-fn :server-name
                                     :host-fn {}}))

      (it "is not found when no optional handler supplied"
        (should= 404
                 (:status (@proxy-fn {:server-name "here" :request-method :get}))))

      (it "responds with the optional handler when present"
        (should= 200
                 (:status (@proxy-with-handler-fn {:server-name "here" :request-method :get})))))

    (context "when forwarding server is found"
      (with proxy-fn (proxy-request {:identifier-fn :server-name
                                     :host-fn {"hello" "http://hello.com"}}))
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
                 (slurp (:body @response))))

      (it "forwards the response header"
        (should= {:content-type "application/json"}
                (:headers @response))))

    (context "when forwarding server is found"
      (with proxy-fn (proxy-request (fn [handler]
                                      (fn [request]
                                        (handler request))) {:identifier-fn :server-name
                                     :host-fn       {"hello" "http://hello.com"}}))
      (with response
            (with-fake-routes-in-isolation post-request
                                           (@proxy-fn {:server-name    "hello"
                                                       :request-method :post
                                                       :uri            "/postit"
                                                       :headers {"content-length" (count "some post body")}
                                                       :body "some post body"})))
      (it "does not throw content-length already present exception"
        (let [_ @response]
          (should-not-throw (Exception.)))))))


(run-specs)
