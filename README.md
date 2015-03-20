# Ring-Request-Proxy

A Clojure library for proxying requests in ring applications.

## Installation

TBD

## Usage

### Request Proxy

```clojure
(ns myapp.core
  (:require [ring-request-proxy.core :as proxy])

; Middleware format: Delegates request to handler when request can't be forwarded
(def app (-> not-found-handler
             (proxy/proxy-request-by-server {"my-server" "http://my-internal-server"})))

; Handler format: Responds with 404 when request can't be forwarded
(def app (proxy/proxy-request-by-server {"my-server" "http://my-internal-server"}))
```

The proxy middleware is responsible for forwarding requests to another server. If the request
cannot be forwarded, the request can either receive a default response or continue the request
through the delegation chain.

Currently, the following proxy functions are supported:

* `proxy-request-by-server`: Proxies the request based on the server name

## License

Copyright © 2015 Funding Circle

Distributed under the BSD 3-Clause License.
