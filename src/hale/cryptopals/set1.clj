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
