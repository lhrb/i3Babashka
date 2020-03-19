#!/usr/bin/env bb
(require '[clojure.java.shell :refer [sh]])

(def weather-icon
  '({:id "01" :icon "B"}
    {:id "02" :icon "H"}
    {:id "03" :icon "N"}
    {:id "04" :icon "Y"}
    {:id "09" :icon "R" }
    {:id "10" :icon "Q"}
    {:id "11" :icon "0"}
    {:id "13" :icon "G"}
    {:id "50" :icon "M"}))

(def api-key (first *input*))

(defn req [key]
  (-> (sh "curl" "--request" "GET"
          "--url" "https://community-open-weather-map.p.rapidapi.com/weather?id=2886242&units=metric&q=Cologne"
          "--header" "x-rapidapi-host: community-open-weather-map.p.rapidapi.com"
          "--header" (str "x-rapidapi-key: " key))
      :out
      (json/parse-string true)))

(defn font [w]
  (str "%{T3}" w "%{T-}"))

(defn temperatur [data]
  (-> (get-in data [:main :temp]) Math/ceil int))

(defn format-tmp [tmp]
  (str tmp "°C"))

(defn parse-weather [data]
  (-> (get-in data [:weather]) first (get-in [:icon]) (subs 0 2)))

(defn get-weather-icon [icon-id]
  (-> (filter #(= (:id %) icon-id) weather-icon)
      first
      :icon))

(let [resp (req api-key)]
  (println
   (str (-> resp parse-weather get-weather-icon font)
        "%{O3}"
        (-> resp temperatur format-tmp))))
