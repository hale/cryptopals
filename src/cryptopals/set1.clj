(ns cryptopals.set1
  (:require [clojure.set :refer [difference map-invert]]
            [clojure.string :as string]))

;; "RULE: Always operate on raw bytes, never on encoded strings. Only use hex and base64 for pretty-printing

;; ===============
;;   CHALLENGE 1
;; ===============

(defn hex-to-bytes
  "UNSAFE -- interprets string as hex using read-string"
  [hex] (let [chars (partition 2 hex)
              parse-chars (fn [[c1 c2]] (read-string (str "0x" c1 c2)))]
          (map parse-chars chars)))

(defn print-bytes
  "[dev] Turn byte arrays into lists of ones-and-zeros"
  [byte & rest]
  (let [stringify (fn [b]      (Integer/toBinaryString b))
        pad       (fn [string] (format "%8s" string))
        zero-pad  (fn [string] (string/replace string " " "0"))]
    (map (comp zero-pad pad stringify) (conj rest byte))))

(defn three-bytes-to-four-bytes
  "Turn three bytes into four (padded with zeros). Used for base64 encoding"
  [bytes-in]
  (let [[b1 b2 b3] bytes-in
        c1 (bit-shift-right b1 2)                         ; first 6 bits of b1
        c2 (bit-or (bit-shift-left (bit-and 2r11 b1) 4)   ; last 2 bits of b1 plus first 4 bits of b2
                   (bit-shift-right b2 4))
        c3 (bit-or (bit-shift-left (bit-and 2r1111 b2) 2) ; last 4 bits of b2 plus first 2 bits of b3
                   (bit-shift-right b3 6))
        c4 (bit-and 2r111111 b3)                          ; last 6 bits of b3
        ]
    [c1 c2 c3 c4]))

;; (defn take-bits [n b]
;;   (bit-shift-right b (- 8 n)))

;; (defn last-n-bits [n b]
;;   (bit-and 2r11)

(defn four-bytes-to-three-bytes
  "Turn a zero-padded seq of four bytes into the original three. Used for base64 decoding"
  [bytes-in]
  (let [[b1 b2 b3 b4] bytes-in
        c1 (bit-or (bit-shift-left b1 2)                  ; b1, shifted left to make room
                   (bit-shift-right b2 4))                ; first 2 bits of b2
        c2 (bit-or (bit-shift-left (bit-and 2r1111 b2) 4) ; last 4 bits of b2
                   (bit-shift-right b3 2))                ; first 4 bits of b3
        c3 (bit-or (bit-shift-left (bit-and 2r11 b3) 6)   ; last 4 bits of b3
                   b4)]                                   ; b4
    [c1 c2 c3]))


(def base64-map
  { 0 \A  1 \B  2 \C  3 \D  4 \E  5 \F  6 \G  7 \H
    8 \I  9 \J 10 \K 11 \L 12 \M 13 \N 14 \O 15 \P
   16 \Q 17 \R 18 \S 19 \T 20 \U 21 \V 22 \W 23 \X
   24 \Y 25 \Z 26 \a 27 \b 28 \c 29 \d 30 \e 31 \f
   32 \g 33 \h 34 \i 35 \j 36 \k 37 \l 38 \m 39 \n
   40 \o 41 \p 42 \q 43 \r 44 \s 45 \t 46 \u 47 \v
   48 \w 49 \x 50 \y 51 \z 52 \0 53 \1 54 \2 55 \3
   56 \4 57 \5 58 \6 59 \7 60 \8 61 \9 62 \+ 63 \- })

(def base64-chars (set (vals base64-map)))

;; TODO: change tail call to `(apply str (comp encode flatten expand to-triples))`?
(defn hex-to-base64
  "Set 1 :: Challenge 1 :: Base64 encode a hex string"
  [hex]
  (let [bytes   (hex-to-bytes hex)
        triples (partition 3 bytes)
        quads   (map three-bytes-to-four-bytes triples)
        chars   (map base64-map (flatten quads))]
    (apply str chars)))

(defn base64-to-bytes
  "Decode base64 encoded data"
  [str]
  (let [chars (map char str)
        sanitized (filter (partial contains? base64-chars) chars)
        indices (map (map-invert base64-map) sanitized)
        bytes (map byte indices)
        quads (partition 4 bytes)
        triples (map four-bytes-to-three-bytes quads)]
    (flatten triples)))

;; ===============
;;   CHALLENGE 2
;; ===============

(defn xor-combine
  "Set 1 :: Challenge 2 :: Fixed XOR"
  [h1 h2]
  (let [b1    (hex-to-bytes h1)
        b2    (hex-to-bytes h2)
        xored (map bit-xor b1 b2)
        hexes (map (partial format "%x") xored)]
    (apply str hexes)))

;; ===============
;;   CHALLENGE 3
;; ===============

(defn hex-to-str
  "Also known as 'unhexify'"
  [hex]
  (let [bytes (hex-to-bytes hex)
        chars (map char bytes)]
    (apply str chars)))

(defn bytes-to-str
  [bytes]
  (let [to-char (fn [b] (try (char b)
                         (catch IllegalArgumentException e
                           (println (str "Error decoding " (print-bytes b)))
                           "?")))
        chars (map to-char bytes)]
    (apply str chars)))

(defn str-englishness-old
  "DEPRECATED: Measures 'englishness' of a string by the absence of 'weird' chars"
  [str]
  (let [eng-chars       (vals base64-map)
        str-chars       (seq (char-array str))
        non-eng-chars   (difference (set eng-chars) (set str-chars))
        p-non-eng-chars (/ (count non-eng-chars) (count (eng-chars)))]
    (- 1 p-non-eng-chars)))

(defn str-englishness
  "Measures 'englishness' of a string by propotion of word chars"
  [str]
  (let [eng-chars (count (re-seq #"[a-zA-A ]" str))]
    (/ eng-chars (count str))))


(defn single-char-xor
  "XORs a bytestream against the given char"
  [bytestream char]
  (let [b1 bytestream
        b2 (map byte (repeat char))]
    (map bit-xor b1 b2)))

(defn decode [bytestream char]
  (let [xored (single-char-xor bytestream char)]
    {:in bytestream
     :out xored
     :char char
     :score (str-englishness (bytes-to-str xored))}))

(defn decode-single-char-xor
  [bytestream]
  (let [candidates (map (partial decode bytestream) base64-chars)
        sorted     (sort-by :score candidates)
        winner     (last sorted)]
    winner))

(defn decode-single-char-xor-encoded-hex-str
  "Set 1 :: Challenge 3 :: Single-byte XOR cipher"
  [str]
  (let [bytes      (hex-to-bytes str)
        winner     (decode-single-char-xor bytes)]
    (-> winner
        (update :out bytes-to-str)
        (assoc :in str))))

;; ===============
;;   CHALLENGE 4
;; ===============
;;
;; One of the 60 character strings in this file has been encrypted by single-character XOR. Find it.
;;

(defn detect-single-char-xor
  "From a seq of hex strings, finds and decodes the one which has been encrypted by single-character XOR"
  [strings]
  (last (sort-by :score (map decode-single-char-xor-encoded-hex-str strings))))

;; ===============
;;   CHALLENGE 5
;; ===============
;;
;; Implement repeating-key XOR
;;

(defn str-to-bytes
  [str]
  (let [chars (map char str)]
    (map byte chars)))

(defn bytes-to-hex
  [bytes]
  (apply str (map (partial format "%02x") bytes)))

(defn repeating-key-xor
  "Encodes input string using the given key"
  [key str]
  (let [b1 (str-to-bytes str)
        b2 (flatten (repeat (map byte (char-array key))))
        xored (map bit-xor b1 b2)]
    (bytes-to-hex xored)))

;; ===============
;;   CHALLENGE 6
;; ===============
;;
;; Break repeating-key XOR
;;

(def input (slurp "data/s1c6.txt"))

(defn- edit-distance-bytes
  [b1 b2]
  (let [xored (map bit-xor b1 b2)
        bit-counts (map #(Integer/bitCount %) xored)]
    (reduce + bit-counts)))

(defn edit-distance
  "Calculate the edit distance (number of differing bits) between two strings"
  [s1 s2]
  (let [b1 (str-to-bytes s1)
        b2 (str-to-bytes s2)]
    (edit-distance-bytes b1 b2)))



;; STEP 1 -- Find the key size


(defn evaluate-key-size
  "Score a given key based on the hamming-distance of its application against the bytestream"
  [bytes ks]
  (let [byte-pairs (partition 2 (partition ks bytes))
        scores (flatten (map #(apply edit-distance-bytes %) byte-pairs))
        avg-score (/ (apply + scores) (count scores))
        normalized (/ avg-score ks)]
    {:score normalized :ks ks}))

(defn determine-key-size
  [bytes n]
  (let [candidates (range 2 n)
        scores (map #(evaluate-key-size bytes %) candidates)]
    (apply min-key :score scores)))


(determine-key-size (str-to-bytes input) 40)

;; STEP 2 -- transpose input using the discovered key size

(defn transpose [m] (apply mapv vector m))

;; STEP 3 -- solve each block as if it was single-char XOR


(def input (slurp "data/s1c6.txt"))
(def decoded (base64-to-bytes input))
(def keysize (:ks (determine-key-size decoded 500)))
(def blocks (transpose (partition keysize decoded)))

;; (def transposed (decrypt-vigenere decoded))
(def decrypted (map (partial decode-single-char-xor) blocks))
;; (map :score decoded)

(def output (map bytes-to-str (map :out decrypted)))





;; TODO:

;; 1. Base64 algorithm:
;;   a. Handle input that isn't a multiple of three (padding)
;;   b. Unit test that above is lazy (encode infinite hex, take first 10)
;;   c. Swap 'unsafe' hex-to-bytes for a safer hex lookup (dict?)
;; 2. Roll your own bitCount function
