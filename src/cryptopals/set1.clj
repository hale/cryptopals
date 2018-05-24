(ns cryptopals.set1)

;; RULE: Always operate on raw bytes, never on encoded strings. Only use hex and base64 for pretty-printing.

;; Step 1: the input is a string, we must convert this to an array of bytes
;;  1a. Split the string into chunks of two, as each HEX value is a pair of chars
;;  1b. Interpret the two-char string as a hex
(defn hex-to-bytes
  "UNSAFE -- interprets string as hex using read-string"
  [hex] (let [chars (partition 2 hex)
              parse-chars (fn [[c1 c2]] (read-string (str "0x" c1 c2)))]
          (map parse-chars chars)))

(defn print-bytes
  "[dev] Turn byte arrays into lists of ones-and-zeros"
  [bytes] (map #(Integer/toBinaryString %) bytes))


;; TODO:

;; 1. Base64 algorithm:
;;   a. transform seq of 8-bit bytes into seq of 6-bit patterns
;;   b. look up decimal representation of 6-bit pattern
;;   c. use this decimal as the index in the base64 table
;;   d. repeat the above until we run out of 6-bit seqs
;; 2. Swap 'unsafe' hex-to-bytes for a safer hex lookup (dict?)


;; Step 2: encode the byte array in Base64
;; (defn hex-to-base64
;;   "Challenge 1: Convert hex to base64"
;;   [hex] (let [bytes (hex-to-bytes hex)]) bytes)
