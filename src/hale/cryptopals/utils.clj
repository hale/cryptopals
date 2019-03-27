(ns hale.cryptopals.utils
  (:require [clojure.test :as t]
            [clojure.string :as str]))

(defn hex-to-bytes
  "FIXME: UNSAFE -- interprets string as hex using read-string"
  [hex] (let [chars (partition 2 hex)
              parse-chars (fn [[c1 c2]] (read-string (str "0x" c1 c2)))]
          (map parse-chars chars)))

(t/deftest test-hex-to-bytes
  (t/is (= (hex-to-bytes "4d616e")
         (map byte [0x4d 0x61 0x6e])
         (map byte [77 97 110])
         (map byte [2r1001101 2r1100001 2r1101110]))))

(defn print-bytes
  "Given a list of bytes, return a string representation of those bytes in
  binary form (ones-and-zeros)"
  [byte & rest]
  (let [stringify (fn [b]      (Integer/toBinaryString b))
        pad       (fn [string] (format "%8s" string))
        zero-pad  (fn [string] (str/replace string " " "0"))]
    (map (comp zero-pad pad stringify) (conj rest byte))))

(t/deftest test-print-bytes
  (t/is (= (apply print-bytes (hex-to-bytes "4d616e"))
         ["01001101" "01100001" "01101110"])))

(defn hex-to-str
  "Also known as 'unhexify'"
  [hex]
  (let [bytes (hex-to-bytes hex)
        chars (map char bytes)]
    (apply str chars)))

(defn bytes-to-str [bytes] (apply str (map char bytes)))

(defn map-vals [f m]
  (into {} (for [[k v] m] [k (f v)])))

(def printable-ascii-chars
  "Source: https://en.wikipedia.org/wiki/ASCII#Printable_characters"
  (map char (range 32 127)))

(defn square [x] (* x x))

(defn str-to-bytes
  [str]
  (let [chars (map char str)]
    (map byte chars)))

(defn bytes-to-hex
  [bytes]
  (apply str (map (partial format "%02x") bytes)))

(defn bytes-to-chars
  "Convert a signed byte (-128 to 127) to an unsigned primitive char."
  [bytes]
  (map (fn [b] (bit-and (byte b) 0xFF)) bytes))

(def squish (comp clojure.string/join clojure.string/split-lines))

(def slurp-squish (comp squish slurp))
