;; # Implement PKCS#7 padding
;;
;; A block cipher transforms a fixed-sized block (usually 8 or 16 bytes) of
;; plaintext into ciphertext. But we almost never want to transform a single
;; block; we encrypt irregularly-sized messages.
;;
;; One way we account for irregularly-sized messages is by padding, creating a
;; plaintext that is an even multiple of the blocksize. The most popular padding
;; scheme is called PKCS#7.
;;
;; So: pad any block to a specific block length, by appending the number of
;; bytes of padding to the end of the block. For instance,
;;
;;     "YELLOW SUBMARINE"
;;
;; ... padded to 20 bytes would be:
;;
;;     "YELLOW SUBMARINE\x04\x04\x04\x04"
;;
;; <hr />
(ns hale.cryptopals.set2.challenge9
  (:require [clojure.test :as t]
            [hale.cryptopals.utils :as utils]))

(defn pad-block
  ([block] (pad-block block 16 0x04))
  ([block blocksize] (pad-block block blocksize 0x04))
  ([block blocksize pad]
  (let [pad-length (- blocksize (count block))]
    (flatten (conj (vec block) (repeat pad-length pad))))))

(t/deftest test-pad-block
  (t/is (= (map char [\Y \E \L \L \O \W \space \S \U \B
                       \M \A \R \I \N \E 0x04 0x04 0x04 0x04])
           (map char (pad-block (utils/str-to-bytes "YELLOW SUBMARINE") 20)))))
