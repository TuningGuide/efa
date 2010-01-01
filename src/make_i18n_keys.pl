#!/usr/bin/perl

my $properties = shift;
my $options = "";
if ($properties =~ /^-/) {
  $options = $properties;
  $properties = shift;
}

if (length($properties) == 0) {
  printf("usage: make_i18n_keys.pl [-options] <properties>\n");
  printf("\nThis script recursively searches all .java files in the current\n" .
         "directory and any subdirectories for internationalized strings and\n" .
         "creates a new property file (based on the original one) on stdout.\n");
  printf("\nOptions:\n");
  printf("       s   sort all keys independent of source file\n");
  printf("       S   sort all keys independent of source file (case-insentivive)\n");
  printf("       f   print file name as a comment after each key\n");
  printf("       d   print DEBUG messages on stderr\n");
  exit(1);
}

# some global variables
my $_filename;
my $_linenr;
my $_line;

# read existing properties
readProps($properties);

# traverse directories and search for .java files
searchdir(".");

# print new properties file
printf("# Property File created by make_i18n_keys.pl:\n# ------------------------------------------\n");
my %data;
foreach $key (sort %keys) {
  if (exists $keys{$key}{txt}) {
    my $txt = $keys{$key}{txt};
    my $file = $keys{$key}{file};
    my $new = $keys{$key}{new};

    my $sortkey = $key;
    if ($options =~ /s/ || $options =~ /S/) {
      $file = "global";
    }
    if ($options =~ /S/) {
      $sortkey = lc($sortkey);
      while ($data{$file}{$new}{$sortkey}{key}) {
        $sortkey .= "X";
      }
    }

    $data{$file}{$new}{$sortkey}{key} = $key;
    $data{$file}{$new}{$sortkey}{value} = $txt;
  }
}

foreach $file (sort keys %data) {
  printf("# file: $file\n");
  my $old = 1;
  foreach $new (sort keys %{$data{$file}}) {
    if ($old==1 && $new==1) {
      printf("# new keys in $file:\n");
      $old = 0;
    }
    foreach $key (sort keys %{$data{$file}{$new}}) {
      printf("%s=%s%s\n",
                 $data{$file}{$new}{$key}{key},
                 $data{$file}{$new}{$key}{value},
                 ($options =~ /f/ ? "\t\t### " . $keys{$key}{file} : "")
                 );
    }
  }
}

exit(0);



sub readProps {
  my $properties = shift;
  open(PROPS,$properties) || die "cannot open property file: $properties\n";
  my $file = "unknown";
  while(<PROPS>) {
    if (/^# file: (.+)/) {
      $file = $1;
    }
    next if /^#/;
    if (/([^=]+)=(.+)/) {
      my $key = $1;
      my $txt = $2;
      $keys{$key}{txt} = $txt;
      $keys{$key}{file} = $file;
      $keys{$key}{new} = 0;
    }
  }
  close(PROPS);
}


sub searchdir {
  my $dir = shift;
  opendir(DIR,$dir) || die "cannot open directory: $dir\n";
  my @files = readdir(DIR);
  my $file;
  foreach $file (sort @files) {
    if ("$file" eq "." || "$file" eq "..") { next; }
    if (-d "$dir/$file") {
      searchdir("$dir/$file");
    } else {
      if (lc($file) =~ /.java$/) {
        parsefile("$dir/$file");
      }
    }
  }
}

sub parsefile {
  my $file = shift;
  printf STDERR ("#DEBUG: File %s ...\n",$file) unless $options !~ /d/;;
  open(JAVA,$file) || die "cannot open source file: $file\n";
  $_filename = $file;
  $_linenr = 0;
  while(<JAVA>) {
    $_line = $_;
    $_linenr++;
    parseLine($_line);
  }
  close(JAVA);
}

sub parseLine {
  my $line = shift;
  my $remaining = $line;
  my $isMessage = 0;

  while (length($remaining) > 0) {
    $line = $remaining;
    $remaining = "";
    my $txt = "";
    my $discr = "";

    # getString(...) or getStringWithMnemonic(...)
    if ($line =~ /International.getString[^\(]*\s*\((.*)/) {
      $isMessage = 0;
      $remaining = getStrings($1,2);
      if ($#strings <= 1) {
        $txt = $strings[0];
      }
      if ($#strings == 1) {
        $discr = $strings[1];
      }
    }

    # getMessage(...)
    if ($line =~ /International.getMessage[^\(]*\s*\((.*)/) {
      $isMessage = 1;
      $remaining = getStrings($1,1);
      if ($#strings >= 0) {
        $txt = $strings[0];
      }
    }

    # key found?
    if (length($txt) > 0) {

      # create key
      my $key = $txt;
      if (length($discr) > 0) {
        $key .= "___" . $discr;
      }
      $key =~ s/ /_/g;
      $key =~ s/=/_/g;
      $key =~ s/:/_/g;
      $key =~ s/#/_/g;
      $key =~ s/'/_/g;

      # create message text for compound messages
      if ($isMessage) {
        my $i = 1;
        while ($txt =~ /{[^\}]+}/) {
          $txt =~ s/{[^\}]+}/%_1_%$i%_2_%/;
          $i++;
        }
        $txt =~ s/%_1_%/{/g;
        $txt =~ s/%_2_%/}/g;
      }

      # handle special characters in translated text
      $txt =~ s/&/&&/g;
      $txt =~ s/'/''/g;

      # handle special English strings from International.java (keys must remain English, but translation should be German)
      if ($txt =~ /^Default$/) { $txt = "Standard"; }
      if ($txt =~ /^Select Language$/) { $txt = "Sprache wählen"; }
      if ($txt =~ /^Please select your language$/) { $txt = "Bitte wähle Deine Sprache"; }

      # print key and text
      if (exists $keys{$key}) {
        printf(stderr "#DEBUG: Duplicate Key $key=$txt\n") unless $options !~ /d/;
      } else {
        printf(stderr "#DEBUG: New Key $key=$txt\n") unless $options !~ /d/;
        $keys{$key}{txt} = $txt;
        $keys{$key}{file} = $file;
        $keys{$key}{new} = 1;
      }
    }
  }
}

sub getStrings {
  my $line = shift;
  my $nrOfStrings = shift; # number of strings to look for
  printf(stderr "#DEBUG: getStrings(%s)\n",$line) unless $options !~ /d/;
  my $str = "";
  my $i = 0;
  my $remaining = "";

  # 0 = before string
  # 1 = in string
  # 2 = after string, search for concatenated strings
  # 98 = search for next string
  # 99 = strings complete
  my $inString = 0;

  # 0 = no comment
  # 1 = in comment
  my $inComment = 0;

  @strings = ();
  while($inString != 100) {

    if ($inString == 98 || $inString == 99) {
      $strings[$#strings+1] = $str;
      printf(stderr "#DEBUG: String complete: >>%s<<\n",$str) unless $options !~ /d/;
      if ($inString == 98) {
        my $foundStrings = $#strings + 1;
        if ($foundStrings == $nrOfStrings) {
          printf(stderr "#DEBUG: $nrOfStrings Strings found, we're done!\n",$str) unless $options !~ /d/;
          $inString = 100;
        } else {
          printf(stderr "#DEBUG: $foundStrings Strings found, searching for furhter strings ...\n",$str) unless $options !~ /d/;
          $inString = 0;
        }
      } else {
        $inString = 100;
      }
      $str = "";
      next;
    }

    $remaining = substr($line,$i++);
    printf(stderr "#DEBUG: remaining string is >>%s<<\n",$remaining) unless $options !~ /d/;
    if (length($remaining) == 0) {
      $line = <JAVA>;
      chomp($line);
      $_line = $line;
      $_linenr++;
      $i = 0;
      next;
    }

    if ($inString == 0 || $inString == 2) { # we're searching for the beginning of a string

      if (!$inComment) { # we're not inside a comment

        # comment until end of line?
        if ($remaining =~ /^\/\//) {
          printf(stderr "#DEBUG: Comment until EOL found: %s\n",$remaining) unless $options !~ /d/;
          $line = <JAVA>;
          chomp($line);
          $_line = $line;
          $_linenr++;
          $i = 0;
          next;
        }

        # start of a comment?
        if ($remaining =~ /^\/\*/) {
          printf(stderr "#DEBUG: Beginning of a Comment found: %s\n",$remaining) unless $options !~ /d/;
          $inComment = 1;
          $i++;
          next;
        }

        # start of a string?
        if ($remaining =~ /^"/) {
          printf(stderr "#DEBUG: Beginning of a String found: %s\n",$remaining) unless $options !~ /d/;
          $inString = 1;
          next;
        }

        # concatenated string?
        if ($remaining =~ /^\+/) {
          printf(stderr "#DEBUG: Concatenation found: %s\n",$remaining) unless $options !~ /d/;
          $inString = 2;
          next;
        }

        # next parameter?
        if ($remaining =~ /^,/) {
          printf(stderr "#DEBUG: Next Method Parameter found: %s\n",$remaining) unless $options !~ /d/;
          $inString = 98;
          next;
        }

        # method finished?
        if ($remaining =~ /^\)/) {
          printf(stderr "#DEBUG: End of Method found: %s\n",$remaining) unless $options !~ /d/;
          $inString = 99;
          next;
        }
        if ($remaining !~ /^[ \t]/) {
          printf(stderr "#WARNING: %s:%d: unexpected international string will be ignored (dynamic key?): >>%s<<\n %s\n",
                         $_filename,$_linenr,$remaining,$_line);
          $inString = 100;
        }

      } else { # we're inside a comment

        # end of a comment?
        if ($remaining =~ /^\*\//) {
          printf(stderr "#DEBUG: End of a Comment found: %s\n",$remaining) unless $options !~ /d/;
          $inComment = 0;
          $i++;
          next;
        }

      }

    }

    if ($inString == 1) { # we're inside a string, adding characters to the current string
      printf(stderr "#DEBUG: in string: >>%s<<\n",$remaining) unless $options !~ /d/;

      if ($remaining =~ /^"/) {
        printf(stderr "#DEBUG: end of part of string found, string is now: >>%s<<\n",$str) unless $options !~ /d/;
        $inString = 2;
        next;
      }

      if ($remaining =~ /^\\./) {
        $str .= substr($remaining,0,2);
        $i++;
      } else {
        $str .= substr($remaining,0,1);
      }

    }

  }

  printf(stderr "#DEBUG: done getting strings, remaining line: >>%s<<\n",$remaining) unless $options !~ /d/;
  return $remaining;
}

