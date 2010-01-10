#!/usr/bin/perl

use Encode::Escape;
Encode::Escape::demode 'unicode-escape', 'python';
Encode::Escape::enmode 'unicode-escape', 'python';

my $infile = shift;
open(INFILE,$infile) || die "cannot open $infile!\n";
while(<INFILE>) {
  if (/^#/) {
    next;
  }
  if (/([^=]+)=(.*)/) {
    my $line = sprintf("%s|%s",$1,$2);
    print encode 'utf8', decode 'unicode-escape', $line;
  }
}