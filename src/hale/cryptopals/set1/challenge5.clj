(ns hale.cryptopals.set1.challenge5
  (:require [clojure.test :as t]
            [hale.cryptopals.utils :as utils]))


;; ===============
;;   CHALLENGE 5
;; ===============
;;
;; Implement repeating-key XOR
;;

(defn repeating-key-xor
  "Set 1 :: Challenge 5 :: Implement repeating-key XOR

  Encodes an input string using the given key"
  [key str]
  (let [b1    (utils/str-to-bytes str)
        b2    (flatten (repeat (map byte (char-array key))))
        xored (map bit-xor b1 b2)]
    (utils/bytes-to-hex xored)))

(t/deftest test-repeating-key-xor
  (let [input "Burning 'em, if you ain't quick and nimble\nI go crazy when I hear a cymbal"
        key   "ICE"
        result (repeating-key-xor key input)]
    (t/is (= result
           "0b3637272a2b2e63622c2e69692a23693a2a3c6324202d623d63343c2a26226324272765272a282b2f20430a652e2c652a3124333a653e2b2027630c692b20283165286326302e27282f"))))
