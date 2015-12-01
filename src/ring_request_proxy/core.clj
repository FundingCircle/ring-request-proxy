(ns ring-request-proxy.core
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(def ^:private not-found-response {:status 404
                                   :body (json/write-str {:message "Not found"})})

(defn- build-url [host path query-string]
  (let [url (.toString (java.net.URL. (java.net.URL. host) path))]
    (if (not-empty query-string)
      (str url "?" query-string)
      url)))

(defn- handle-not-found [request]
  not-found-response)

(defn- create-proxy-fn [handler opts]
  (let [identifier-fn (get opts :identifier-fn identity)
        server-mapping (get opts :host-fn {})]
    (fn [request]
      (let [request-key (identifier-fn request)
            host (server-mapping request-key)
            stripped-headers (dissoc (:headers request) "content-length")]
        (if host
          (select-keys
           (client/request {:url              (build-url
                                               host (:uri request)
                                               (:query-string request))
                            :method           (:request-method request)
                            :body             (:body request)
                            :headers          stripped-headers
                            :throw-exceptions false
                            :as               :stream})
           [:status :headers :cookies :body])
          (handler request))))))

(defn proxy-request
  ([opts]
   (proxy-request handle-not-found opts))
  ([handler opts]
   (create-proxy-fn handler opts)))
