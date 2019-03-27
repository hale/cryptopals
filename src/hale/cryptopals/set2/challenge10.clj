;; # Implement CBC mode

;; CBC mode is a block cipher mode that allows us to encrypt irregularly-sized
;; messages, despite the fact that a block cipher natively only transforms
;; individual blocks.

;; In CBC mode, each ciphertext block is added to the next plaintext block
;; before the next call to the cipher core.

;; The first plaintext block, which has no associated previous ciphertext block,
;; is added to a "fake 0th ciphertext block" called the initialization vector,
;; or IV.

;; Implement CBC mode by hand by taking the ECB function you wrote earlier,
;; making it encrypt instead of decrypt (verify this by decrypting whatever you
;; encrypt to test), and using your XOR function from the previous exercise to
;; combine them.

;; The file here is intelligible (somewhat) when CBC decrypted against "YELLOW
;; SUBMARINE" with an IV of all ASCII 0 (\x00\x00\x00 &c)
;;
;; <hr />
(ns hale.cryptopals.set2.challenge10
  (:require [hale.cryptopals.utils :as utils]
            [hale.cryptopals.set1.challenge1 :as base64]
            [hale.cryptopals.set1.challenge7 :as challenge7]
            [clojure.test :as t])
  (:import [javax.crypto Cipher]
           [javax.crypto.spec SecretKeySpec]))


(defn encrypt-aes-ecb
  [byte-stream key]
  (let [key-spec  (SecretKeySpec. (.getBytes key) "AES")
        cipher    (Cipher/getInstance "AES/ECB/PKCS5Padding")
        _         (.init cipher (int Cipher/ENCRYPT_MODE) key-spec)
        encrypted (.doFinal cipher (byte-array byte-stream))]
    encrypted))

(defn encrypt-aes-ecb-str-to-base64
  [text key]
  "Encrypt AES in ECB mode"
  (base64/base64-encode (encrypt-aes-ecb (utils/str-to-bytes text) key)))

(t/deftest encrypt-aes-in-ecb-mode
  (let [key "YELLOW SUBMARINE"
        ciphertext (utils/slurp-squish "data/s1c7.txt")
        plaintext (slurp "data/s1c7.solution.txt")]
    (t/is (= (encrypt-aes-ecb-str-to-base64 plaintext key) ciphertext))))

(t/deftest encrypt-decrypt
  (let [key "YELLOW SUBMARINE"
        text (slurp "data/s1c7.solution.txt")
        encrypted (encrypt-aes-ecb-str-to-base64 text key)
        decrypted (challenge7/decrypt-aes-ecb-base64-to-str encrypted key)]
    (t/is (= decrypted text))))

























