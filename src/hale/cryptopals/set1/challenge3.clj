;; # Single-byte XOR cipher
;;
;; The hex encoded string:
;;
;;     1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736
;;
;; ...has been XOR'd against a single character. Find the key, decrypt the message.
;;
;; You can do this by hand. But don't: write code to do it for you.
;;
;; How? Devise some method for "scoring" a piece of English plaintext. Character
;; frequency is a good metric. Evaluate each output and choose the one with the
;; best score.
;;
;; <hr />
(ns hale.cryptopals.set1.challenge3
  (:require [clojure.test :as t]
            [clojure.string :as str]
            [clojure.set :refer [difference]]
            [hale.cryptopals.utils :as utils]
            [hale.cryptopals.set1.challenge1 :as base64]))


;; I completed this challenge initially by measuring the simple quantity of
;; weird characters in the output. I had the Base64 vals already to hand, so
;; used that as by definition of normalcy.
(defn str-englishness-weirdness
  "DEPRECATED: Measures 'englishness' of a string by the absence of 'weird' chars"
  [str]
  (let [eng-chars       (vals base64/base64-map)
        str-chars       (seq (char-array str))
        non-eng-chars   (difference (set eng-chars) (set str-chars))
        p-non-eng-chars (/ (count non-eng-chars) (count (eng-chars)))]
    (- 1 p-non-eng-chars)))

;; ...this doesn't work very well. Sentences containing spaces in particular are
;; penalized, whereas jumbmles of ASCII are considered all equally English. Back
;; to the drawing board (what was that they said about frequencies...?)

;; Still ignoring the advice about frequencies, I wrote this which looks at the
;; proportion of word characters (this time including space!). This was good
;; enough to pass Challenge 3.
(defn str-englishness-words
  "DEPRECATED: Measures 'englishness' of a string by propotion of word chars"
  [str]
  (let [eng-chars (count (re-seq #"[a-zA-Z ]" str))]
    (/ eng-chars (count str))))

;; ## Frequencies!
;;
;; After a bit of research, I stumbled across the **Chi-squared** test as a way
;; of measuring a set of observations against expected outcomes.
;;
;; The test takes two sets of observations (actual and expected), and reduces
;; the differences between them to a single number.

(defn chi-square [expected actual]
  (/ (utils/square (- actual expected)) expected))

;; Note that this takes a third argument for the 'default' value if nothing was
;; observed. This is not actually supported by the underlying statistic. In
;; fact, the test is unreliable when the number of observations is less than
;; five. However, it works for our purposes if we assign a default small value
;; for missing occurrences.
(defn chi-square-distance
  "Reduces the difference between two sets of observations to a single number.
  Lower numbers indicate greater convergence."
  ([e-freqs a-freqs] (chi-square-distance e-freqs a-freqs 0.000001))
  ([e-freqs a-freqs missing]
  (let [distances (map (fn [[a-key a-val]] (chi-square (get e-freqs a-key missing) a-val)) a-freqs)]
    (reduce + distances))))

;; Clojure has a built in `frequencies` function, which we can use to turn the
;; string under test into a map of actual character observations.

(defn chi-sq-str-fit
  "Measures the similarity between a string and a target corpus of string. 'Does
  the string 'fit in' with the other strings?"
  [rel-freq str]
  (let [chars   (char-array (str/lower-case str))
        e-freqs (utils/map-vals (partial * (count chars)) rel-freq)
        a-freqs (frequencies chars)]
    (* -1 (chi-square-distance e-freqs a-freqs))))


;; Relative character frequencies in the English language are well known. Here
;; is one such map from Wikipedia:

(def eng-char-rel-freq
  "Source: [Wikipedia/Letter_frequency](https://en.wikipedia.org/wiki/Letter_frequency#Relative_frequencies_of_letters_in_the_English_language)"
  { \a 0.08167 \b 0.01492 \c 0.02782 \d 0.04253
   \e 0.12702 \f 0.02228 \g 0.02015 \h 0.06094
   \i 0.06966 \j 0.00153 \k 0.00772 \l 0.04025
   \m 0.02406 \n 0.06749 \o 0.07507 \p 0.01929
   \q 0.00095 \r 0.05987 \s 0.06327 \t 0.09056
   \u 0.02758 \v 0.00978 \w 0.02360 \x 0.00150
   \y 0.01974 \z 0.00074 })

(def str-englishness (partial chi-sq-str-fit eng-char-rel-freq))

;; There is one problem with the above approach: it ignores punctiation. It
;; turns out the plaintext of many of these solutions contains lots of spaces,
;; enough that when it was reused in later challenges the wrong result was
;; sometimes returned.
;;
;; How can we include `\space` and other commmon chars in the frequency map?
;;
;; We could juggle the numbers or find another map that includes punctuation,
;; but I thought it would be fun to produce my own, since we have this handy
;; `frequencies` function:

(def training-text
  "Reads text files in data/training-text returning a single string with every
  file concatonated. _This project is not distributed with any training data;
  please supply your own by pasting text files in data/training-text_"
  (let [contents  (file-seq (clojure.java.io/file "./data/training-text"))
        filenames (filter #(.isFile %) contents)]
    (str/join (map slurp filenames))))

(defn custom-char-rel-freq
  "Given some text and some allowed characters from that text, returns a map of
  the relative frequency of each character."
  ([text] (custom-char-rel-freq text #"[a-z ']"))
  ([text regex]
   (let [sanitized (str/lower-case text)
         chars     (map #(char (first %)) (re-seq regex sanitized))
         freqs     (frequencies chars)
         rel-freqs (utils/map-vals (fn [v] (/ v (count chars))) freqs)]
     rel-freqs)))

(def trained-char-rel-freq (custom-char-rel-freq training-text))

(def str-fitness (partial chi-sq-str-fit trained-char-rel-freq))

;; What shall we use for training data? Well given this quote:
;;
;;   > An appreciation for early-90's MTV hip-hop can't hurt either.
;;
;; ...let's create a frequency map of Vanilla Ice lyrics. The results are as
;; follows:

(def vanilla-ice-rel-freq
  "Expected relative frequencies of each letter in a Vanilla Ice lyric"
  { \a 0.06212 \b 0.01350 \c 0.02465 \d 0.02556
    \e 0.08375 \f 0.01177 \g 0.01883 \h 0.03980
    \i 0.06460 \j 0.00380 \k 0.01630 \l 0.03510
    \m 0.02448 \n 0.05429 \o 0.07037 \p 0.01495
    \q 0.00032 \r 0.03612 \s 0.04152 \t 0.07494
    \u 0.02708 \v 0.00732 \w 0.01795 \x 0.00162
    \y 0.02644 \z 0.00150 \' 0.01737 \space 0.18394 })

(def str-iciness
  "Measures the likelyhood of a string being a Vanilla Ice lyric, using a
  chi-squared test against a relative character frequency map trained on
  lyrics."
  (partial chi-sq-str-fit vanilla-ice-rel-freq))

(t/deftest test-str-iciness
  (let [icy "Now that the party is jumping\n"
        eng "I have of late, but wherefore I know not, lost all my mirth"]
    (t/is (> (str-iciness icy) (str-iciness eng)))))

(defn single-char-xor
  "XORs a bytestream against the given char"
  [bytestream char]
  (let [b1 bytestream
        b2 (map byte (repeat char))]
    (map bit-xor b1 b2)))

(defn xor-with-score
  "XOR the bytstream against the char then evaluate its fitness"
  ([bytestream char] (xor-with-score bytestream char str-iciness))
  ([bytestream char fitness-fn]
  (let [xored (single-char-xor bytestream char)]
    {:in bytestream
     :out xored
     :char char
     :score (fitness-fn (utils/bytes-to-str xored))})))


(defn decode-single-char-xor
  [bytestream]
  (let [candidates (map (partial xor-with-score bytestream) utils/printable-ascii-chars)
        sorted     (sort-by :score candidates)
        winner     (last sorted)]
    winner))

(defn decode-single-char-xor-encoded-hex-str
  "Set 1 :: Challenge 3 :: Single-byte XOR cipher"
  [str]
  (let [bytes  (utils/hex-to-bytes str)
        winner (decode-single-char-xor bytes)]
    (-> winner
        (update :out utils/bytes-to-str)
        (assoc :in str))))

(t/deftest test-decode-single-char-xor-encoded-hex-str
  (let [hex-str "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736"
        result  (decode-single-char-xor-encoded-hex-str hex-str)]
    (t/is (= (:out result) "Cooking MC's like a pound of bacon"))
    (t/is (= (:char result) \X))))
