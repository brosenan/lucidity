(ns lucid.unit-test
  (:use hara.test)
  (:require [lucid.unit :refer :all]))


^{:refer lucid.unit/source-namespace :added "1.2"}
(fact "look up the source file, corresponding to the namespace")

^{:refer lucid.unit/import :added "1.2"}
(fact "imports unit tests as docstrings")

^{:refer lucid.unit/purge :added "1.2"}
(fact "purge docstrings and meta from file")

^{:refer lucid.unit/missing :added "1.2"}
(fact "lists the functions that are missing unit tests")

^{:refer lucid.unit/scaffold :added "1.2"}
(fact "builds the unit test scaffolding for the source")

(comment
  (lucid.unit/import)
  )