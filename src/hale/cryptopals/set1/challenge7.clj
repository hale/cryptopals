;; # AES in ECB mode
;;
;; The Base64-encoded content in this file has been encrypted via AES-128 in ECB
;; mode under the key:
;;
;;     "YELLOW SUBMARINE".
;;
;; (case-sensitive, without the quotes; exactly 16 characters; I like "YELLOW
;; SUBMARINE" because it's exactly 16 bytes long, and now you do too).
;;
;; Decrypt it. You know the key, after all.
;;
;; Easiest way: use OpenSSL::Cipher and give it AES-128-ECB as the cipher.
;;
;;_Do this with code._ You can obviously decrypt this using the OpenSSL
;; command-line tool, but we're having you get ECB working in code for a reason.
;; You'll need it a lot later on, and not just for attacking ECB.
;;
;; <hr />
(ns hale.cryptopals.set1.challenge7
  (:require [hale.cryptopals.utils :as utils]
            [hale.cryptopals.set1.challenge1 :as base64]
            [clojure.test :as t])
  (:import [javax.crypto Cipher]
           [javax.crypto.spec SecretKeySpec]))



(defn decrypt-aes-ecb
  [byte-stream key]
  (let [key-spec  (SecretKeySpec. (.getBytes key) "AES")
        cipher    (Cipher/getInstance "AES/ECB/PKCS5Padding")
        _         (.init cipher (int Cipher/DECRYPT_MODE) key-spec)
        decrypted (.doFinal cipher (byte-array byte-stream))]
    decrypted))


(defn decrypt-aes-ecb-base64 [b64 key]
  (decrypt-aes-ecb (base64/base64-decode b64) key))

(def decrypt-aes-ecb-base64-to-str
  "Decrypt AES in ECB mode"
  (comp utils/bytes-to-str decrypt-aes-ecb-base64))

(t/deftest decrypt-aes-in-ecb-mode
  (let [key "YELLOW SUBMARINE"
        ciphertext (slurp "data/s1c7.txt")
        plaintext (slurp "data/s1c7.solution.txt")]
    (t/is (= (decrypt-aes-ecb-base64-to-str ciphertext key) plaintext))))
