;; ## Crypto Challenge Set 1
;;
;; This is the **qualifying set**. We picked the exercises in it to ramp
;; developers up gradually into coding cryptography, but also to verify that we
;; were working with people who were ready to write code.
;;
;; This set is **relatively easy**. With one exception, most of these exercises
;; should take only a couple minutes. But don't beat yourself up if it takes
;; longer than that. It took Alex two weeks to get through the set!
;;
;; If you've written any crypto code in the past, you're going to feel like
;; skipping a lot of this. **Don't skip them**. At least two of them (we won't
;; say which) are important stepping stones to later attacks.
;;
(ns hale.cryptopals.set1)


;; TODO:

;; 1. Base64 algorithm:
;;   a. Handle input that isn't a multiple of three (padding)
;;   c. Swap 'unsafe' hex-to-bytes for a safer hex lookup (dict?)
;; 2. Roll your own bitCount function
;; 3. Make everything lazy?
;; 7. Roll your own AES implementation
;; 8. Make 'implement repeating key XOR' a command line function.
;;   1. Make it a binary using GraalVM.
;; 9. Layer on clojure spec for the public fns.
;; 10. experiment with generative testing based on specs
;; 11. Profile with YourKit / VisualVM.
;;   1. This revealed that chi-square is a hotspot, as well as the basic
;;      division and multiplication fns. I think this is because many of the
;;      numbers get really large with that default value of 0.00001 for missing
;;      observations. Look into making that value smaller.
