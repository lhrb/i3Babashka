#!/usr/bin/env bb
(require '[clojure.string :as str])

(def filepath "/sys/class/power_supply/")
(def charge "/sys/class/power_supply/AC/online")

(defn find-batteries [path]
  (->>
   (.list (io/file path))
   (filter #(.contains % "BAT"))
   (map #(str path %))))

(defn parse [x]
  (-> (slurp x) (str/trim) (Integer/parseInt)))

(defn type [t l]
  (reduce + (map #(parse (str % t)) l)))

(defn battery [bat]
  (int (* 100 (/ (type "/energy_now" bat) (type "/energy_full" bat)))))

(defn symbol [x]
  (cond
    (< x 10) ""
    (< x 25) ""
    (< x 50) ""
    (< x 75) ""
    :else "" ))

(let [x (battery (find-batteries filepath))
      sym (if (= 1 (parse charge)) "" (symbol x))]
 (str sym " " x "%"))

