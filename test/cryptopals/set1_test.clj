(ns cryptopals.set1-test
  (:require [clojure.test :refer :all]
            [clojure.string :as string]
            [cryptopals.set1 :as s])
  (:import java.util.Base64))


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
    (is (= (:out result) "Cooking MC's like a pound of bacon"))
    (is (= (:char result) \X))))


(deftest detect-single-char-xor-sanity-check
  (let [strings ["7b5a4215415d544115415d5015455447414c155c46155f4058455c5b523f"]
        result  (s/detect-single-char-xor strings)]
    (is (= (:out result) "Now that the party is jumping\n"))))

;; Set 1 :: Challenge 4 :: Detect single-character XOR
(deftest detect-single-char-xor
  (let [strings (string/split (slurp "data/s1c4.txt") #"\s")
        result  (s/detect-single-char-xor strings)]
    (is (= (:in result) "7b5a4215415d544115415d5015455447414c155c46155f4058455c5b523f"))
    (is (= (:out result) "Now that the party is jumping\n"))
    (is (= (:char result) \5))))

;; Set 1 :: Challenge 5 :: Implement repeating-key XOR
(deftest repeating-key-xor
  (let [input "Burning 'em, if you ain't quick and nimble\nI go crazy when I hear a cymbal"
        key   "ICE"
        result (s/repeating-key-xor key input)]
    (is (= result
           "0b3637272a2b2e63622c2e69692a23693a2a3c6324202d623d63343c2a26226324272765272a282b2f20430a652e2c652a3124333a653e2b2027630c692b20283165286326302e27282f"))))

(deftest edit-distance
    (is (= (s/edit-distance "this is a test" "wokka wokka!!!") 37)))

(deftest base64-to-bytes
  (is (= "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d"
         (s/bytes-to-hex (s/base64-to-bytes "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t")))))

;; TODO: make base64-to-str output padding '===' chars
;; (deftest base64-to-str
;;   (let [decoded "Man is distinguished, not only by his reason, but by"
;;         encoded (.encode (Base64/getEncoder) (.getBytes decoded))]
;;     (is (= decoded (s/base64-to-str encoded) ))))

(deftest three-bytes-to-four-bytes
  (is (= [2r00010011 2r00010110 2r00000101 2r00101110] [19 22 5 46]
         (s/three-bytes-to-four-bytes [2r01001101 2r01100001 2r01101110]))))

(deftest four-bytes-to-three-bytes
  (is (= [2r01001101 2r01100001 2r01101110]
         (s/four-bytes-to-three-bytes [2r00010011 2r00010110 2r00000101 2r00101110]))))

(deftest find-repeating-xor-key
  (is (= "Terminator X: Bring the noise"
         (s/find-repeating-xor-key (s/base64-to-bytes (slurp "data/s1c6.txt"))))))

(deftest str-iciness
  (let [icy "Now that the party is jumping\n"
        hot "}E4*^F\"9{a0+VEN 3*smL /TcA"]
    (is (> (s/str-iciness icy) (s/str-iciness hot)))))

;; Set 1 :: Challenge 6 :: Break repeating-key XOR
(deftest decrypt-repeating-key-xor
  (let [ciphertext (slurp "data/s1c6.txt")
        e-lines (string/split (slurp "data/s1c6.solution.txt") #"\n")
        a-lines (string/split (s/decrypt-repeating-key-xor-base64 ciphertext) #"\n")]
    (is (every? (fn [[expected actual]] (= expected actual)) (zipmap e-lines a-lines)))))

;; Set 1 :: Challenge 7 :: AES in ECB mode
(deftest decrypt-aes-in-ecb-mode
  (let [key "YELLOW SUBMARINE"
        ciphertext (slurp "data/s1c7.txt")
        plaintext (slurp "data/s1c7.solution.txt")]
    (is (= s/decrypt-aes-ecb key ciphertext) plaintext)))
