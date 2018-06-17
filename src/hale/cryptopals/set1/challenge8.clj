;; # Detect AES in ECB mode.
;;
;; In this file are a bunch of hex-encoded ciphertexts. One of them has been
;; encrypted with ECB. Detect it.
;;
;; Remember that the problem with ECB is that it is stateless and deterministic;
;; the same 16 byte plaintext block will always produce the same 16 byte
;; ciphertext.
;;
;; <hr />
(ns hale.cryptopals.set1.challenge8
  (:require [hale.cryptopals.set1.challenge3 :refer [chi-square-distance]]
            [hale.cryptopals.utils :as utils]
            [clojure.string :as str]
            [clojure.test :as t]))

;; Cyphertext known to be encoded with AES in ECB mode will exhibit repeating
;; blocks of bytes (if the plaintext has any repetition). A good heuristic for
;; AES in ECB mode would therefore be 'contains repetition'.
;;
;; Info about the source data:
;;
;; 1. 64 strings to test
;; 2. Each string is 60 chars long (hex encoded, so that's 30 bytes)
;;
;; We already have that chi-squared function which can measure the deviation of
;; observations from what is expected.
;;
;; The opposite of repetition is randomness. Randomness can be defined here as
;; 'each byte is equaly likey to appear'. I.e. the bytes should be uniformally
;; distributed. The least-random ciphertext will therefore be the set of bytes
;; that is most deviant from the uniform distribution (where every outcome is
;; equally likely).

(def uniform-bytes-rel-freq-map
  "Standard uniform distribution for a set of bytes"
  (zipmap (map byte (range 0 128))
          (repeat 128 (/ 1 128))))

(defn detect-aes-in-ecb-mode
  "**Set 1 :: Challenge 8 :: Detect AES in ECB mode**"
  [strs]
  (let [streams (map utils/hex-to-bytes strs)
        e-freqs (utils/map-vals (partial * 30) uniform-bytes-rel-freq-map)
        score   (fn [bs] (chi-square-distance e-freqs (frequencies bs) (/ 1 256)))
        scores  (map #(hash-map :in % :score (score %)) streams)
        winner  (apply max-key :score scores)]
    (utils/bytes-to-hex (:in winner))))

(t/deftest test-detect-aes-in-ecb-mode
  (let [strs (str/split (slurp "data/s1c8.txt") #"\n")
        expected (nth strs 132)
        actual (detect-aes-in-ecb-mode strs)]
    (t/is (= expected actual))))

