;; # Base64 encode a hex string
;;
;;
;; The string:
;;
;;     49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d
;;
;; Should produce:
;;
;;     SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t
;;
;; So go ahead and make that happen. You'll need to use this code for the rest
;; of the exercises.
;;
;; Cryptopals Rule: Always operate on raw bytes, never on encoded strings. Only use hex and base64 for pretty-printing.
;;
(ns hale.cryptopals.set1.challenge1
  (:require [clojure.set :refer [map-invert]]
            [clojure.string :as str]
            [hale.cryptopals.utils :as utils]
            [clojure.test :as t]))


;; The following table visually explains the process of Base64 encoding:
;;
;; <img src="https://pghale.com/images/cryptopals/base64-wiki-table.png" width="100%" />
;;
;; 1. Get the underlying bytestream of the input data. (In the table they are
;;    encoding the _ASCII_ word "Man" wherease the exercise has us encode _hex_)
;; 2. Parse the bytestream in chunks of six and turn each six-bit sequence into a
;;    byte by padding with 2 empty bits (zeros). You can see in the image how
;;    the remaining two bits from the first byte are used to generate the next
;;    byte, etc until all input bytes are consumed.
;; 3. Use the decimal value of each six-bit byte to 'lookup' the ASCII char in the
;;    map. Because there is only six bits of information in each 'byte', the bit
;;    pattern will always resolve to one of the 64 characters in the Base64
;;    encoding map. This is because there are only 64 possible patterns in six
;;    bits of information (n bits yield 2^n patterns).
;;
;; I had originally thought to write an algorithm that processed the list
;; recusively in blocks of 6. However, this is not possible because the smallest
;; unit in the JVM is a Byte. So instead we must process the bytestream in
;; groups of four and use bitwise operators to shift the bits around in each
;; byte.
;;
;; The initial step of parsing hexidecimal strings as bytes is in the utils
;; namespace, as it's something we'll be using a lot in these exercises.
;;
;; <hr />
(def base64-map
  "Axiomatic Base64 map of six-bit bytes (indices) to ASCII characters"
  {  0 \A  1 \B  2 \C  3 \D  4 \E  5 \F  6 \G  7 \H
     8 \I  9 \J 10 \K 11 \L 12 \M 13 \N 14 \O 15 \P
    16 \Q 17 \R 18 \S 19 \T 20 \U 21 \V 22 \W 23 \X
    24 \Y 25 \Z 26 \a 27 \b 28 \c 29 \d 30 \e 31 \f
    32 \g 33 \h 34 \i 35 \j 36 \k 37 \l 38 \m 39 \n
    40 \o 41 \p 42 \q 43 \r 44 \s 45 \t 46 \u 47 \v
    48 \w 49 \x 50 \y 51 \z 52 \0 53 \1 54 \2 55 \3
    56 \4 57 \5 58 \6 59 \7 60 \8 61 \9 62 \+ 63 \/ })

(defn base64-expand-bytes
  "Base64 byte-expansion procedure, to reduce the number of possible values per
  byte from 256 to 64."
  ([b1]
   (let [c1 (bit-shift-right b1 2)
         c2 (bit-or (bit-shift-left (bit-and 2r00000011 b1) 4)
                    (byte 0))]
     [c1 c2 nil nil]))
  ([b1 b2]
   (let [c1 (bit-shift-right b1 2)
         c2 (bit-or (bit-shift-left (bit-and 2r00000011 b1) 4)
                    (bit-shift-right b2 4))
         c3 (bit-or (bit-shift-left (bit-and 2r00001111 b2) 2)
                    (byte 0))]
     [c1 c2 c3 nil]))
  ([b1 b2 b3]
   (let [c1 (bit-shift-right b1 2)                             ; first 6 bits of b1
         c2 (bit-or (bit-shift-left (bit-and 2r00000011 b1) 4) ; last 2 bits of b1 plus first 4 bits of b2
                    (bit-shift-right b2 4))
         c3 (bit-or (bit-shift-left (bit-and 2r00001111 b2) 2) ; last 4 bits of b2 plus first 2 bits of b3
                    (bit-shift-right b3 6))
         c4 (bit-and 2r00111111 b3)]                           ; last 6 bits of b3
   [c1 c2 c3 c4])))

(t/deftest test-base64-expand-bytes
  (t/is (= (apply base64-expand-bytes [2r01001101 2r01100001 2r01101110])
           [2r00010011 2r00010110 2r00000101 2r00101110] [19 22 5 46]))
  (t/is (= (apply base64-expand-bytes (map byte [77 97 110]))
           (map byte [19 22 5 46])))
  (t/is (= (apply base64-expand-bytes (map byte [77 0 0]))
           (map byte [19 16 0 0])))
  (t/is (= (apply base64-expand-bytes (map byte [77 97 0]))
           (map byte [19 22 4 0]))))

(defn base64-encode
  "Base64 encode a stream of bytes"
  [bytes]
  (let [triples (partition 3 3 [] bytes)
        quads   (map (partial apply base64-expand-bytes) triples)
        chars   (map (fn [k] (get base64-map k \=)) (flatten quads))]
    (apply str chars)))

(def base64-encode-str (comp base64-encode utils/str-to-bytes))

;; Test vectors from [RFC4648](https://tools.ietf.org/html/rfc4648#section-10)
(t/deftest test-base64-encode
  (t/is (= ""         (base64-encode-str "")))
  (t/is (= "Zg=="     (base64-encode-str "f")))
  (t/is (= "Zm8="     (base64-encode-str "fo")))
  (t/is (= "Zm9v"     (base64-encode-str "foo")))
  (t/is (= "Zm9vYg==" (base64-encode-str "foob")))
  (t/is (= "Zm9vYmE=" (base64-encode-str "fooba")))
  (t/is (= "Zm9vYmFy" (base64-encode-str "foobar"))))


(def hex-to-base64
  "Set 1 :: Challenge 1 :: Base64 encode a hex string"
  (comp base64-encode utils/hex-to-bytes))

(t/deftest test-hex-to-base64
  (t/is (= (hex-to-base64
            (str "49276d206b696c6c696e6720796f757220627261696e206c"
                 "696b65206120706f69736f6e6f7573206d757368726f6f6d"))
            "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t")))


;; ## Decode
;;
;; Decoding reads from the above table backwards:
;;
;; 1. Reverse lookup the Base64 map to get the index.
;; 2. Remove the 2 bits of padding from each byte until we end up with regular
;;    8-bit bytes again.

(def base64-chars (set (vals base64-map)))

(defn base64-reduce-bytes
  "Base64 reinflation procedure, to remove the zero-padding and turn the
  sequence of six-bit bytes back in to regular 8-bit bytes."
  ([b1 b2]
   (let [c1 (bit-or (bit-shift-left b1 2)
                    (bit-shift-right b2 4))]
     [c1]))
  ([b1 b2 b3]
   (let [c1 (bit-or (bit-shift-left b1 2)
                    (bit-shift-right b2 4))
         c2 (bit-or (bit-shift-left (bit-and 2r1111 b2) 4)
                    (bit-shift-right b3 2))]
     [c1 c2]))
  ([b1 b2 b3 b4]
   (let [c1 (bit-or (bit-shift-left b1 2)                  ; b1 shifted to make room
                    (bit-shift-right b2 4))                ; first 2 bits of b2
         c2 (bit-or (bit-shift-left (bit-and 2r1111 b2) 4) ; last 4 bits of b2
                    (bit-shift-right b3 2))                ; first 4 bits of b3
         c3 (bit-or (bit-shift-left (bit-and 2r11 b3) 6)   ; last 4 bits of b3
                    b4)]                                   ; b4
     [c1 c2 c3])))

(t/deftest test-base64-reduce-bytes
  (t/is (= (apply base64-reduce-bytes [2r00010011 2r00010110 2r00000101 2r00101110])
         [2r01001101 2r01100001 2r01101110])))

(defn base64-decode
  "Decode a base64 encoded string (or bytestream)"
  [str]
  (let [chars     (map char str)
        sanitized (filter (partial contains? base64-chars) chars)
        indices   (map (map-invert base64-map) sanitized)
        quads     (partition 4 4 [] (map byte indices))
        triples   (map (partial apply base64-reduce-bytes) quads)]
    (flatten triples)))

(t/deftest test-base64-decode-long
  (t/is (= (str "49276d206b696c6c696e6720796f757220627261696e206c"
                "696b65206120706f69736f6e6f7573206d757368726f6f6d")
           (utils/bytes-to-hex
            (base64-decode
             "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t")))))

(def base64-to-str (comp utils/bytes-to-str base64-decode))


;; Test vectors from [RFC4648](https://tools.ietf.org/html/rfc4648#section-10)
(t/deftest test-base64-decode
  (t/is (= ""       (base64-to-str "")))
  (t/is (= "f"      (base64-to-str "Zg==")))
  (t/is (= "fo"     (base64-to-str "Zm8=")))
  (t/is (= "foo"    (base64-to-str "Zm9v")))
  (t/is (= "fooba"  (base64-to-str "Zm9vYmE=")))
  (t/is (= "foobar" (base64-to-str "Zm9vYmFy")))
  (t/is (= "foob"   (base64-to-str "Zm9vYg=="))))

