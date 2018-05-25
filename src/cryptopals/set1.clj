(ns cryptopals.set1)

;; RULE: Always operate on raw bytes, never on encoded strings. Only use hex and base64 for pretty-printing.

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
        zero-pad  (fn [string] (clojure.string/replace string " " "0"))]
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

(def base16-map
  { 0 \A  1 \B  2 \C  3 \D  4 \E  5 \F  6 \G  7 \H
    8 \I  9 \J 10 \K 11 \L 12 \M 13 \N 14 \O 15 \P
   16 \Q 17 \R 18 \S 19 \T 20 \U 21 \V 22 \W 23 \X
   24 \Y 25 \Z 26 \a 27 \b 28 \c 29 \d 30 \e 31 \f
   32 \g 33 \h 34 \i 35 \j 36 \k 37 \l 38 \m 39 \n
   40 \o 41 \p 42 \q 43 \r 44 \s 45 \t 46 \u 47 \v
   48 \w 49 \x 50 \y 51 \z 52 \0 53 \1 54 \2 55 \3
   56 \4 57 \5 58 \6 59 \7 60 \8 61 \9 62 \+ 63 \- })

;; TODO: change tail call to `(apply str (comp encode flatten expand to-triples))`?
(defn hex-to-base64
  "Base64 encode a hex string"
  [hex]
  (let [bytes     (hex-to-bytes hex)
        triples   (partition 3 bytes)
        expanded  (map three-bytes-to-four-bytes triples)
        flattened (flatten expanded)
        encoded   (map base16-map flattened)]
    (apply str encoded)))

(defn xor-combine
  "Given two hex strings encoding equal-length buffers, produce their XOR combination"
  [h1 h2]
  (let [b1    (hex-to-bytes h1)
        b2    (hex-to-bytes h2)
        xored (map bit-xor b1 b2)
        hexes (map (partial format "%x") xored)]
    (apply str hexes)))


;; TODO:

;; 1. Base64 algorithm:
;;   a. Handle input that isn't a multiple of three (padding)
;;   b. Unit test that above is lazy (encode infinite hex, take first 10)
;;   c. Swap 'unsafe' hex-to-bytes for a safer hex lookup (dict?)
