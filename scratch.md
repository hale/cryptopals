TODO:

1. Fix unsafe hex-to-bytes function
1. Base64 algorithm:
  a. Handle input that isn't a multiple of three (padding)
  c. Swap 'unsafe' hex-to-bytes for a safer hex lookup (dict?)
2. Roll your own bitCount function / Hamming weight fn (although hotspot calls a CPU instruction on Core processors :))
3. Make everything lazy?
7. Roll your own AES implementation
8. Make 'implement repeating key XOR' a command line function.
  1. Make it a binary using GraalVM.
9. Layer on clojure spec for the public fns.
10. experiment with generative testing based on specs
11. Profile with YourKit / VisualVM.
  1. This revealed that chi-square is a hotspot, as well as the basic
     division and multiplication fns. I think this is because many of the
     numbers get really large with that default value of 0.00001 for missing
     observations. Look into making that value smaller.
12. make base64-to-str output padding '===' chars

;; (t/deftest test-base64-to-str
;;   (let [decoded "Man is distinguished, not only by his reason, but by"
;;         encoded (.encode (Base64/getEncoder) (.getBytes decoded))]
;;     (t/is (= decoded (s/base64-to-str encoded) ))))
