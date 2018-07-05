(defproject hale.cryptopals "0.1.0-SNAPSHOT"
  :description "

Cryptopals challenges in Clojure. ![CircleCI](https://circleci.com/gh/hale/cryptopals.svg?style=svg)

[A collection of 48 exercises that demonstrate attacks on real-world crypto.](https://cryptopals.com/)

This is a different way to learn about crypto than taking a class or reading a
book. We give you problems to solve. They're derived from weaknesses in
real-world systems and modern cryptographic constructions. We give you enough
info to learn about the underlying crypto concepts yourself. When you're
finished, you'll not only have learned a good deal about how cryptosystems are
built, but you'll also understand how they're attacked.

**Work in progress:**

* <s>Start</s> 24th May 2018
* <s>Set 1</s> 17th June 2018
* Set 2
* Set 3
* Set 4
* Set 5
* Set 6
* Set 7
* Set 8


"
  :url "https://github.com/hale/cryptopals"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :plugins [[lein-marginalia "0.9.1"]
            [test2junit "1.4.2"]]
  :test2junit-output-dir "test-results/clojure.test"
  :test-paths ["src" "test"]
  :aliases {"docs" ["marg"
                    "src/hale/cryptopals/set1.clj"
                    "src/hale/cryptopals/set1/challenge1.clj"
                    "src/hale/cryptopals/set1/challenge2.clj"
                    "src/hale/cryptopals/set1/challenge3.clj"
                    "src/hale/cryptopals/set1/challenge4.clj"
                    "src/hale/cryptopals/set1/challenge5.clj"
                    "src/hale/cryptopals/set1/challenge6.clj"
                    "src/hale/cryptopals/set1/challenge7.clj"
                    "src/hale/cryptopals/set1/challenge8.clj"
                    "src/hale/cryptopals/set2.clj"
                    "src/hale/cryptopals/set2/challenge9.clj"
                    "src/hale/cryptopals/utils.clj"]})
