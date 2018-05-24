(ns cryptopals.set1)

;; Always operate on raw bytes, never on encoded strings. Only use hex and base64 for pretty-printing.

(defn hex-to-base64
  "Challenge 1: Convert hex to base64"
  [hex]
  (str "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t"))

;; TODO: write implementation, return the raw bytes not a string
