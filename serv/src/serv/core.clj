(ns serv.core
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as cs])
  (:require [org.httpkit.server :as hk-server])
  (:require [hiccup2.core :as h])
  (:require [compojure.core :refer :all])
  (:require [compojure.route :as route])
  (:require [ring.util.codec :as codec])
  (:gen-class))

(defn html-list [dirpath]
  (def dir-listing (.list (io/file dirpath)))
  [:ul (for [file dir-listing]
    [:li [:a {:href (if (.isDirectory (io/file (str dirpath "/" file)))
                      (str (codec/url-encode file) "/")
                      (codec/url-encode file))}
             file]])])

(defn html-body [heading content]
  [:body [:h1 heading] [:hr] content [:hr]])

(defn html-page [title body]
  [:html
    [:head
      [:meta
        {:http-equiv "Content-Type"
         :content "text/html; charset=utf-8"}]
      [:title title]]
    (html-body title body)])

(defn make-dir-listing-page [dirpath uri]
  (str (h/html (html-page (str "Listing of " uri) (html-list dirpath)))))

(defn get-file-contents [filepath]
  (def data (-> filepath io/file .toPath java.nio.file.Files/readAllBytes))
  (def content-type (-> filepath io/file .toPath java.nio.file.Files/probeContentType))
  {:status  200
   :headers {"Content-Type" content-type}
   :body    data})

(defn show-page [req]
  (def referer (get (get req :headers) "referer"))
  (def srvpath
    (codec/url-decode (if referer
      (str "/" (do (cs/replace-first referer #"^http.*/" "")))
      (do ""))))
  (def uri (codec/url-decode (get req :uri)))
  (def acwd (str (.getAbsolutePath (io/file ""))))
  (def apath(str acwd srvpath uri))
  (if (.isDirectory (io/file apath))
      (do (make-dir-listing-page apath uri))
      (do (get-file-contents apath))))

(defroutes all-routes
  (GET "*" [] show-page))

(defn -main
  "A simple http server that servers CWD"
  [& args]
  (println "Starting http server on http://localhost:8080")
  (hk-server/run-server all-routes {:port 8080}))
