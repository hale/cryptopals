(ns cryptopals.set1
  (:require [clojure.set :refer [difference map-invert]]
            [clojure.string :as string]
            [clojure.math.numeric-tower :refer [expt]])
  (:import [javax.crypto Cipher]
           [javax.crypto.spec SecretKeySpec]))

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

;; TODO: rename base64-expand-bytes
(defn three-bytes-to-four-bytes
  "Turn three bytes into four (padded with zeros). Used for base64 encoding"
  [bytes-in]
  (let [[b1 b2 b3] bytes-in
        c1 (bit-shift-right b1 2)                         ; first 6 bits of b1
        c2 (bit-or (bit-shift-left (bit-and 2r11 b1) 4)   ; last 2 bits of b1 plus first 4 bits of b2
                   (bit-shift-right b2 4))
        c3 (bit-or (bit-shift-left (bit-and 2r1111 b2) 2) ; last 4 bits of b2 plus first 2 bits of b3
                   (bit-shift-right b3 6))
        c4 (bit-and 2r111111 b3)]                          ; last 6 bits of b3
    [c1 c2 c3 c4]))

;; (defn take-bits [n b]
;;   (bit-shift-right b (- 8 n)))

;; (defn last-n-bits [n b]
;;   (bit-and 2r11)

;; TODO: rename base64-reduce-bytes
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
   56 \4 57 \5 58 \6 59 \7 60 \8 61 \9 62 \+ 63 \/ })

(def base64-chars (set (vals base64-map)))

;; TODO: separate main algo from hex-to-bytes, def this as a comp fn
(defn hex-to-base64
  "Set 1 :: Challenge 1 :: Base64 encode a hex string"
  [hex]
  (let [bytes   (hex-to-bytes hex)
        triples (partition 3 bytes)
        quads   (map three-bytes-to-four-bytes triples)
        chars   (map base64-map (flatten quads))]
    (apply str chars)))

;; TODO: make sanitized a sanitize closure, called in indices
;; TODO: inline bytes in quads
(defn base64-to-bytes
  "Decode base64 encoded data"
  [str]
  (let [chars     (map char str)
        sanitized (filter (partial contains? base64-chars) chars)
        indices   (map (map-invert base64-map) sanitized)
        bytes     (map byte indices)
        quads     (partition 4 bytes)
        triples   (map four-bytes-to-three-bytes quads)]
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

(defn bytes-to-str [bytes] (apply str (map char bytes)))

(defn str-englishness-weird
  "V1 -- Measures 'englishness' of a string by the absence of 'weird' chars"
  [str]
  (let [eng-chars       (vals base64-map)
        str-chars       (seq (char-array str))
        non-eng-chars   (difference (set eng-chars) (set str-chars))
        p-non-eng-chars (/ (count non-eng-chars) (count (eng-chars)))]
    (- 1 p-non-eng-chars)))

(defn str-englishness-regex
  "V2 -- Measures 'englishness' of a string by propotion of word chars"
  [str]
  (let [eng-chars (count (re-seq #"[a-zA-Z ]" str))]
    (/ eng-chars (count str))))

(defn map-vals [f m]
  (into {} (for [[k v] m] [k (f v)])))

(def ice-text
  (let [contents  (file-seq (clojure.java.io/file "./data/vi-lyrics"))
        filenames (filter #(.isFile %) contents)]
    (string/join (map slurp filenames))))

(def ice-char-rel-freq
  (let [sanitized (string/lower-case ice-text)
        chars     (map #(char (first %)) (re-seq #"[a-z ']" sanitized))
        freqs     (frequencies chars)
        rel-freqs (map-vals (fn [v] (/ v (count chars))) freqs)]
    rel-freqs))

(def ice-char-rel-freq-hardcoded-dec
  { \a 0.06212 \b 0.01350 \c 0.02465 \d 0.02556
    \e 0.08375 \f 0.01177 \g 0.01883 \h 0.03980
    \i 0.06460 \j 0.00380 \k 0.01630 \l 0.03510
    \m 0.02448 \n 0.05429 \o 0.07037 \p 0.01495
    \q 0.00032 \r 0.03612 \s 0.04152 \t 0.07494
    \u 0.02708 \v 0.00732 \w 0.01795 \x 0.00162
    \y 0.02644 \z 0.00150 \' 0.01737 \space 0.18394 })

(def eng-char-rel-freq
  { \a 0.08167 \b 0.01492 \c 0.02782 \d 0.04253
    \e 0.12702 \f 0.02228 \g 0.02015 \h 0.06094
    \i 0.06966 \j 0.00153 \k 0.00772 \l 0.04025
    \m 0.02406 \n 0.06749 \o 0.07507 \p 0.01929
    \q 0.00095 \r 0.05987 \s 0.06327 \t 0.09056
    \u 0.02758 \v 0.00978 \w 0.02360 \x 0.00150
    \y 0.01974 \z 0.00074 })

(defn square [x] (expt x 2))

(defn chi-square [expected actual]
  (/ (square (- actual expected)) expected))

;; TODO: document, talk about that 0.000001 value for missing observations (the presence of which invalidates chi-sq)
(defn chi-square-distance
  "Reduces the difference between two sets of observations to a single number.
  Lower numbers indicate greater convergence."
  [expected-freqs actual-freqs]
  (let [distances (map (fn [[a-key a-val]] (chi-square (get expected-freqs a-key 0.000001) a-val)) actual-freqs)]
    (reduce + distances)))


;; TODO: extract a generic fn that measures the 'fit' of a string, taking the rel freq map as an argument
(defn str-iciness
  "V3 -- Measures the likelyhood of a string being a Vanilla Ice lyric, using a
  chi-squared test against a relative character frequency map trained on
  lyrics."
  [str]
  (let [chars   (char-array (string/lower-case str))
        e-freqs (map-vals (partial * (count chars)) ice-char-rel-freq)
        a-freqs (frequencies chars)]
    (* -1 (chi-square-distance e-freqs a-freqs))))


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
     :score (str-iciness (bytes-to-str xored))}))


(def printable-ascii-chars
  "Source: https://en.wikipedia.org/wiki/ASCII#Printable_characters"
  (map char (range 32 127)))

(defn decode-single-char-xor
  [bytestream]
  (let [candidates (map (partial decode bytestream) printable-ascii-chars)
        sorted     (sort-by :score candidates)
        winner     (last sorted)]
    winner))

(defn decode-single-char-xor-encoded-hex-str
  "Set 1 :: Challenge 3 :: Single-byte XOR cipher"
  [str]
  (let [bytes  (hex-to-bytes str)
        winner (decode-single-char-xor bytes)]
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
  "Set 1 :: Challenge 4 :: Detect single-character XOR

  From a seq of hex strings, finds and decodes the one which has been encrypted
  by single-character XOR"
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
  "Set 1 :: Challenge 5 :: Implement repeating-key XOR

  Encodes an input string using the given key"
  [key str]
  (let [b1    (str-to-bytes str)
        b2    (flatten (repeat (map byte (char-array key))))
        xored (map bit-xor b1 b2)]
    (bytes-to-hex xored)))

;; ===============
;;   CHALLENGE 6
;; ===============
;;
;; Break repeating-key XOR
;;

(def input (slurp "data/s1c6.txt"))

;; TODO: write your own Hamming weight fn (although hotspot calls a CPU instruction on Core processors :))
(defn- edit-distance-bytes
  [b1 b2]
  (let [xored      (map bit-xor b1 b2)
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
        scores     (flatten (map #(apply edit-distance-bytes %) byte-pairs))
        avg-score  (/ (apply + scores) (count scores))
        normalized (/ avg-score ks)]
    {:score normalized :ks ks}))

(defn determine-key-size
  [bytes n]
  (let [candidates (range 2 n)
        scores (map #(evaluate-key-size bytes %) candidates)]
    (apply min-key :score scores)))



;; STEP 2 -- transpose input using the discovered key size

(defn transpose [m] (apply mapv vector m))

;; STEP 3 -- solve each block as if it was single-char XOR

(defn find-repeating-xor-key
  "Given bytes encrypted with repeating-key XOR, find the key"
  [bs]
  (let [keysize   (:ks (determine-key-size bs 50))
        ;; _ (println (str "best guess key size is " keysize " (tried up to 50)"))
        blocks    (transpose (partition keysize bs))
        decrypted (map (partial decode-single-char-xor) blocks)]
    (apply str (map :char decrypted))))

(defn decrypt-repeating-key-xor
  "Decodes input bytestream by guessing the key."
  [bs1]
  (let [key (find-repeating-xor-key bs1)
        bs2   (flatten (repeat (map byte (char-array key))))
        xored (map bit-xor bs1 bs2)]
    (bytes-to-str xored)))

(def decrypt-repeating-key-xor-base64
  "Set 1 :: Challenge 6 :: Break repeating-key XOR"
  (comp decrypt-repeating-key-xor base64-to-bytes))

(def base64-to-str (comp bytes-to-str base64-to-bytes))


;; ===============
;;   CHALLENGE 7
;; ==============
;;
;; Decrypt AES in ECB mode (given the key)
;;

(defn decrypt-aes-ecb
  "Set 1 :: Challenge 7 :: AES in ECB mode"
  [byte-stream key]
  (let [key-spec  (SecretKeySpec. (.getBytes key) "AES")
        cipher    (Cipher/getInstance "AES/ECB/PKCS5Padding")
        _         (.init cipher (int Cipher/DECRYPT_MODE) key-spec)
        decrypted (.doFinal cipher (byte-array byte-stream))]
    decrypted))


(defn decrypt-aes-ecb-base64 [base64 key]
  (decrypt-aes-ecb (base64-to-bytes base64) key))

(def decrypt-aes-ecb-base64-to-str (comp bytes-to-str decrypt-aes-ecb-base64))

;; TODO:

;; 1. Base64 algorithm:
;;   a. Handle input that isn't a multiple of three (padding)
;;   c. Swap 'unsafe' hex-to-bytes for a safer hex lookup (dict?)
;; 2. Roll your own bitCount function
;; 3. Make everything lazy
;  4. Refactor with an eye for a) writing idiomatic clojure and b) using more language features (where necessary) e.g. perhaps multimethods for fns that can take either bytes or strings
;; 5. Blog about your solutions, problems encountered, experiences. This is as important as the puzzles themselved.
;; 6. Set up CI to run the tests
;; 7. Roll your own AES implementation
;; 8. Make 'implement repeating key XOR' a command line function.
;;   i. Make it a binary using GraalVM.
;; 9. Layer on clojure spec for the public fns.
;; 10. Extract the utility fns to a separate namespace
;; 11. experiment with generative testing based on specs
;; 12. Nest everything under a hale namespace
