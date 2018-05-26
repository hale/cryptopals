(ns cryptopals.set1-test
  (:require [clojure.test :refer :all]
            [cryptopals.set1 :as s]))

(deftest hex-to-bytes
  (is (= (s/hex-to-bytes "4d616e")
         (map byte [0x4d 0x61 0x6e])
         (map byte [77 97 110])
         (map byte [2r1001101 2r1100001 2r1101110]))))

(deftest print-bytes
  (is (= (apply s/print-bytes (s/hex-to-bytes "4d616e"))
         ["01001101" "01100001" "01101110"])))

(deftest three-bytes-to-four-bytes
  (is (= (s/three-bytes-to-four-bytes (map byte [77 97 110]))
         (map byte [19 22 5 46]))))

;; Set 1 :: Challenge 1 :: Base64 encode a hex string
(deftest hex-to-base64
  (is (= "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t"
         (s/hex-to-base64 "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d"))))

;; Set 1 :: Challenge 2 :: Fixed XOR
(deftest xor-combine
  (is (= (s/xor-combine "1c0111001f010100061a024b53535009181c"
                        "686974207468652062756c6c277320657965")
         "746865206b696420646f6e277420706c6179")))

;; Set 1 :: Challenge 3 :: Single-byte XOR cipher
(deftest decode-single-char-xor-encoded-hex-str
  (let [hex-str "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736"
        result  (s/decode-single-char-xor-encoded-hex-str hex-str)]
    (is (= (:out result) "Cooking MC's like a pound of bacon")
        (= (:char result) \x))))





;; SCRATCH

;; (deftest encode-bits "Encodes a seq of six bits" [bits])

;; My first idea was to iterate through each six-bit pattern, passing the remainder into the loop and processing each byte + remainder in batched of six. However, it's not possible to grab six bits from a byte in Java; a byte is the smallest possible value.
;;
;; New idea must therefore use bitwise operators.
;;
;; So to encode M (01001101) we must take the first 6 bits, and keep the remaining 2 for later...


