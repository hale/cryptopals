;; # Fixed XOR
;;
;; Write a function that takes two equal-length buffers and produces their XOR
;; combination.
;;
;; If your function works properly, then when you feed it the string:
;;
;;     1c0111001f010100061a024b53535009181c
;;
;; ... after hex decoding, and when XOR'd against:
;;
;;     686974207468652062756c6c277320657965
;;
;; ... should produce:
;;
;;     746865206b696420646f6e277420706c6179
;;
;; <hr />
(ns hale.cryptopals.set1.challenge2
  (:require [hale.cryptopals.utils :as utils]
            [clojure.test :as t]
            [clojure.string :as str]))

(defn xor-combine
  "XORs two equal length bytestreams"
  [h1 h2]
  (let [b1    (utils/hex-to-bytes h1)
        b2    (utils/hex-to-bytes h2)
        xored (map bit-xor b1 b2)
        hexes (map (partial format "%x") xored)]
    (apply str hexes)))

(t/deftest test-xor-combine
  (t/is (= (xor-combine "1c0111001f010100061a024b53535009181c"
                      "686974207468652062756c6c277320657965")
         "746865206b696420646f6e277420706c6179")))
