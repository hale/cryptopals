;;
;; # Implement CBC mode
;;
;; CBC mode is a block cipher mode that allows us to encrypt irregularly-sized
;; messages, despite the fact that a block cipher natively only transforms
;; individual blocks.
;;
;; In CBC mode, each ciphertext block is added to the next plaintext block
;; before the next call to the cipher core.
;;
;; The first plaintext block, which has no associated previous ciphertext block,
;; is added to a "fake 0th ciphertext block" called the initialization vector,
;; or IV.
;;
;; Implement CBC mode by hand by taking the ECB function you wrote earlier,
;; making it encrypt instead of decrypt (verify this by decrypting whatever you
;; encrypt to test), and using your XOR function from the previous exercise to
;; combine them.
;;
;; The file here is intelligible (somewhat) when CBC decrypted against "YELLOW
;; SUBMARINE" with an IV of all ASCII 0 (\x00\x00\x00 &c)
;;
;; <hr />
(ns hale.cryptopals.set2.challenge10
  (:require [hale.cryptopals.utils :as utils]
            [hale.cryptopals.set1.challenge1 :as base64]
            [hale.cryptopals.set1.challenge7 :refer [decrypt-aes-ecb-base64-to-str]]
            [clojure.test :as t])
  (:import [javax.crypto Cipher]
           [javax.crypto.spec SecretKeySpec]))



(defn encrypt-aes-ecb
  [key bs]
  (let [key-spec  (SecretKeySpec. (.getBytes key) "AES")
        cipher    (Cipher/getInstance "AES/ECB/NoPadding")
        _         (.init cipher (int Cipher/ENCRYPT_MODE) key-spec)
        encrypted (.doFinal cipher (byte-array bs))]
    encrypted))

(defn encrypt-aes-ecb-str-to-base64
  [key str]
  (->> str
       utils/str-to-bytes
       ((partial encrypt-aes-ecb key))
       base64/base64-encode))

;; (let [key       "ICEICEBABY123456"
;;       plaintext "YELLOW SUBMARINE"
;;       encrypted (encrypt-aes-ecb-str-to-base64 key plaintext)
;;       _ (println encrypted)
;;       _ (println (count encrypted))
;;       decrypted (decrypt-aes-ecb-base64-to-str key encrypted)
;;       ])

;; (decrypt-aes-ecb-base64-to-str encrypted key)

(t/deftest encrypt-aes-in-ecb-mode
  (let [key "YELLOW SUBMARINE"
        ciphertext (slurp "data/s1c7.txt")
        plaintext (slurp "data/s1c7.solution.txt")]
    (t/is (= (encrypt-aes-ecb-str-to-base64 key plaintext) ciphertext))))



;; I found this image on Wikipedia helpful for understanding how CBC encryption works:
;;
;; <img src="https://pghale.com/images/cryptopals/CBC_encryption.svg.png" width="100%" />
;;
;; So we need a function that takes an IV + plaintext and returns ciphertext.


(defn encrypt-aes-ecb
  "bs   -- bytestream input plaintext
   iv   -- initialization vector
   blocks -- number of bytes per block"
  [bs iv blocks])


(def input (base64/base64-decode (slurp "data/s2c10.txt")))
