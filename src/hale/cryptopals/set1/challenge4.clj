;; # Detect single-character XOR
;;
;; One of the 60 character strings in this file has been encrypted by
;; single-character XOR. Find it.
;;
;; (Your code from #3 should help.)
;;
;; <hr />
(ns hale.cryptopals.set1.challenge4
  (:require [hale.cryptopals.set1.challenge3 :as challenge3]
            [clojure.test :as t]
            [clojure.string :as str]))

(defn detect-single-char-xor
  "From a seq of hex strings, finds and decodes the one which has been encrypted
  by single-character XOR"
  [strings]
  (last (sort-by :score (map challenge3/decode-single-char-xor-encoded-hex-str strings))))

(t/deftest test-detect-single-char-xor
  (let [strings (str/split (slurp "data/s1c4.txt") #"\s")
        result  (detect-single-char-xor strings)]
    (t/is (= (:in result) "7b5a4215415d544115415d5015455447414c155c46155f4058455c5b523f"))
    (t/is (= (:out result) "Now that the party is jumping\n"))
    (t/is (= (:char result) \5))))
