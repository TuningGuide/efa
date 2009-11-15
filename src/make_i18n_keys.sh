#!/bin/sh

LANG=de_DE.UTF-8
export LANG
./make_i18n_keys.pl $* | iconv -f UTF-8 -t ISO-8859-1
