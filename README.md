noc
===

NoC - Translation of 'Nature of Code' examples from shiffman.net into
clojure using the incanter processing library from incanter.org.

Usage
-----

For now, its best to start swank, connect to it with emacs and run
individual examples using emacs:

    1. start emacs
    2. start an eshell in emacs, cd to noc directory
    3. at the shell prompt: 
       lein swank
    4. in emacs M-x slime-connect
    5. open an example file, C-c C-k to run it

It seems that the first time you run lein swank, it will run the
main.clj instead of starting a swank server. Exit in eshell, restart
with lein swank.

Installation
------------

Requires leiningen (and emacs+swank-clojure.) After that, clone the
git repo, then follow usage instructions.

License
-------

Copyright (C) 2010 Yavuz Arkun

Distributed under the Eclipse Public License, the same as Clojure.
