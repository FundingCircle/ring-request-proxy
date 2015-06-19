# Ring-Request-Proxy

A Clojure library for proxying requests in ring applications.

## Installation

[ring-request-proxy "0.1.3"]

## Usage

```clojure
(ns myapp.core
  (:require [ring-request-proxy.core :as proxy])

; Middleware format: Delegates request to handler when request can't be forwarded
(def app (-> not-found-handler
             (proxy/proxy-request {:identifier-fn :server-name
                                   :host-fn {"my-server" "http://my-internal-server"}})))

; Handler format: Responds with 404 when request can't be forwarded
(def app (proxy/proxy-request {:identifer-fn :server-name
                               :host-fn (fn [server-name] (if (.startsWith server-name "cool")
                                                              "http://my-internal-server"
                                                              nil))}))
```

### Options

* `identifier-fn`: Maps the request to an identifier that will be be used by `host-fn`
* `host-fn`: Maps the result of `identifier-fn` (or the request if none provided) to the host that will receive the request. If a falsy value is returned, the request will not be forwarded.

The proxy middleware is responsible for forwarding requests to another server. If the request
cannot be forwarded, the request can either receive a default response or continue the request
through the delegation chain.

## License

Copyright © 2015 Funding Circle

Distributed under the BSD 3-Clause License.
