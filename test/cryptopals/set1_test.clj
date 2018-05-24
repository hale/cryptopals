(ns cryptopals.set1-test
  (:require [clojure.test :refer :all]
            [cryptopals.set1 :as s]))

(deftest hex-to-bytes
  (is (= (s/hex-to-bytes "4d616e")
         (map byte [0x4d 0x61 0x6e])
         (map byte [77 97 110])
         (map byte [2r1001101 2r1100001 2r1101110]))))

(deftest print-bytes
  (is (= (s/print-bytes (s/hex-to-bytes "4d616e"))
         ["1001101" "1100001" "1101110"])))

;; (deftest hex-to-base64
;;     (is (= "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t" (s/hex-to-base64 "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d"))))
