;; # Break repeating-key XOR
;;
;; **It is officially on, now.** This challenge isn't conceptually hard, but it
;; involves actual error-prone coding. The other challenges in this set are
;; there to bring you up to speed. This one is there to qualify you. If you can
;; do this one, you're probably just fine up to Set 6.
;;
;; There's a file here. It's been base64'd after being encrypted with repeating-key XOR.
;;
;; Decrypt it.
;;
;; Here's how:
;;
;; 1. Let KEYSIZE be the guessed length of the key; try values from 2 to (say)
;;    40.
;; 2. Write a function to compute the edit distance/Hamming distance between two
;;    strings. The Hamming distance is just the number of differing bits. The
;;    distance between `this is a test` and `wokka wokka!!!` is 37. Make sure
;;    your code agrees before you proceed.
;; 3. For each KEYSIZE, take the first KEYSIZE worth of bytes, and the second
;;    KEYSIZE worth of bytes, and find the edit distance between them. Normalize
;;    this result by dividing by KEYSIZE.
;; 4. The KEYSIZE with the smallest normalized edit distance is probably the
;;    key. You could proceed perhaps with the smallest 2-3 KEYSIZE values. Or
;;    take 4 KEYSIZE blocks instead of 2 and average the distances.
;; 5. Now that you probably know the KEYSIZE: break the ciphertext into blocks
;;    of KEYSIZE length.
;; 6. Now transpose the blocks: make a block that is the first byte of every
;;    block, and a block that is the second byte of every block, and so on.
;; 7. Solve each block as if it was single-character XOR. You already have code
;;    to do this.
;; 8. For each block, the single-byte XOR key that produces the best looking
;;    histogram is the repeating-key XOR key byte for that block. Put them together
;;    and you have the key.
;;
;; This code is going to turn out to be surprisingly useful later on. Breaking
;; repeating-key XOR ("Vigenere") statistically is obviously an academic
;; exercise, a "Crypto 101" thing. But more people "know how" to break it than
;; can actually break it, and a similar technique breaks something much more
;; important.
;;
;; <hr />
(ns hale.cryptopals.set1.challenge6
  (:require [clojure.test :as t]
            [clojure.string :as str]
            [hale.cryptopals.utils :as utils]
            [hale.cryptopals.set1.challenge1 :as base64]
            [hale.cryptopals.set1.challenge3 :as challenge3]))

;; TODO: write your own Hamming weight fn (although hotspot calls a CPU instruction on Core processors :))
(defn- edit-distance-bytes
  [b1 b2]
  (let [xored      (map bit-xor b1 b2)
        bit-counts (map #(Integer/bitCount %) xored)]
    (reduce + bit-counts)))

(defn edit-distance
  "Calculate the edit distance (number of differing bits) between two strings"
  [s1 s2]
  (let [b1 (utils/str-to-bytes s1)
        b2 (utils/str-to-bytes s2)]
    (edit-distance-bytes b1 b2)))

(t/deftest test-edit-distance
  (t/is (= (edit-distance "this is a test" "wokka wokka!!!") 37)))


;; STEP 1 -- Find the key size


(defn evaluate-key-size
  "Score a given keysize based on the hamming-distance of its application against the bytestream"
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
        decrypted (map (partial challenge3/decode-single-char-xor) blocks)]
    (apply str (map :char decrypted))))

(t/deftest test-find-repeating-xor-key
  (t/is (= "Terminator X: Bring the noise"
         (find-repeating-xor-key (base64/base64-decode (slurp "data/s1c6.txt"))))))

(defn decrypt-repeating-key-xor
  "Decodes input bytestream by guessing the key."
  [bs1]
  (let [key (find-repeating-xor-key bs1)
        bs2   (flatten (repeat (map byte (char-array key))))
        xored (map bit-xor bs1 bs2)]
    (utils/bytes-to-str xored)))

(def decrypt-repeating-key-xor-base64
  "Set 1 :: Challenge 6 :: Break repeating-key XOR"
  (comp decrypt-repeating-key-xor base64/base64-decode))

(t/deftest test-decrypt-repeating-key-xor
  (let [ciphertext (slurp "data/s1c6.txt")
        e-lines (str/split (slurp "data/s1c6.solution.txt") #"\n")
        a-lines (str/split (decrypt-repeating-key-xor-base64 ciphertext) #"\n")]
    (t/is (every? (fn [[expected actual]] (= expected actual)) (zipmap e-lines a-lines)))))

