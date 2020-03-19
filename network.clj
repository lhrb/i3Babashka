#!/usr/bin/env bb
(require '[clojure.java.shell :refer [sh]]
         '[clojure.string :as str])

(def data-format '(:name :uuid :type :device))

(defn active-connections []
  (-> (sh "nmcli" "connection" "show" "--active")
      :out
      (str/split-lines)))

(defn connectivity []
  (-> (sh "nmcli" "networking" "connectivity")
      :out
      str/trim))

(defn split-row [connection]
  (-> connection str/trim (str/split #"\s+")))

(defn in? 
  "true if coll contains elm"
  [coll elm]  
  (some #(= elm %) coll))

(defn parse-connections [coll]
  (->> coll
      (map #(split-row %))
      (filter #(not (in? % "bridge")))
      rest))

(defn display-wlan [connection, connectivity]
  (case connectivity
    "full" (str "" "%{O3}" (:name connection))))

(display-wlan
 (->>
  (parse-connections (active-connections))
  (map #(zipmap data-format %))
  (first)) (connectivity))


