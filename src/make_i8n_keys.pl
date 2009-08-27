#!/usr/bin/perl

my $properties = shift;

if (length($properties) == 0) {
  printf("usage: make_i8n_keys.pl <properties>\n");
  printf("\nThis script recursively searches all .java files in the current\n"+
         "directory and any subdirectories for internationalized strings and\n"+
         "creates a new property file (based on the original one) on stdout.\n");
  exit(1);
}

# read existing properties
readProps($properties);

# traverse directories and search for .java files
searchdir(".");

# print new properties file
printf("# Property File created by make_i8n_keys.pl:\n# ------------------------------------------\n");
my %data;
foreach $key (sort %keys) {
  if (exists $keys{$key}{txt}) {
    my $txt = $keys{$key}{txt};
    my $file = $keys{$key}{file};
    my $new = $keys{$key}{new};
    $data{$file}{$new}{$key} = $txt;
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
      printf("%s=%s\n",$key,$data{$file}{$new}{$key});
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
  open(JAVA,$file) || die "cannot open source file: $file\n";
  my $isMessage = 0;
  while(<JAVA>) {

    my $txt = "";
    my $discr = "";

    # getString(...) or getStringWithMnemonic(...)
    if (/International.getString[^\(]*\s*\((.*)/) {
      $isMessage = 0;
      getStrings($1);
      if ($#strings <= 1) {
        $txt = $strings[0];
      }
      if ($#strings == 1) {
        $discr = $strings[1];
      }
    }

    # getMessage(...)
    if (/International.getMessage[^\(]*\s*\((.*)/) {
      $isMessage = 1;
      getStrings($1);
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

      # print key and text
      if (exists $keys{$key}) {
        # printf("#DEBUG: Duplicate Key $key=$txt\n");
      } else {
        # printf("#DEBUG: New Key $key=$txt\n");
        $keys{$key}{txt} = $txt;
        $keys{$key}{file} = $file;
        $keys{$key}{new} = 1;
      }
    }

  }

  close(JAVA);
}

sub getStrings {
  my $line = shift;
  # printf("#DEBUG: getStrings(%s)\n",$line);
  my $str = "";
  my $i = 0;
  my $inString = 0;  # 0 = before string; 1 = in string; 2 = after string, search for concatenated strings; 98 = search for next string; 99 = strings complete
  my $inComment = 0; # 0 = no comment; 1 = in comment
  @strings = ();
  while($inString != 100) {

    if ($inString == 98 || $inString == 99) {
      $strings[$#strings+1] = $str;
      # printf("#DEBUG: String complete: >>%s<<\n",$str);
      if ($inString == 98) {
        $inString = 0;
      } else {
        $inString = 100;
      }
      $str = "";
      next;
    }

    my $remaining = substr($line,$i++);
    # printf("#DEBUG: remaining string is >>%s<<\n",$remaining);
    if (length($remaining) == 0) {
      $line = <JAVA>;
      $i = 0;
      next;
    }

    if ($inString == 0 || $inString == 2) { # we're searching for the beginning of a string

      if (!$inComment) { # we're not inside a comment

        # comment until end of line?
        if ($remaining =~ /^\/\//) {
          # printf("#DEBUG: Comment until EOL found: %s\n",$remaining);
          $line = <JAVA>;
          $i = 0;
          next;
        }

        # start of a comment?
        if ($remaining =~ /^\/\*/) {
          # printf("#DEBUG: Beginning of a Comment found: %s\n",$remaining);
          $inComment = 1;
          $i++;
          next;
        }

        # start of a string?
        if ($remaining =~ /^"/) {
          # printf("#DEBUG: Beginning of a String found: %s\n",$remaining);
          $inString = 1;
          next;
        }

        # concatenated string?
        if ($remaining =~ /^\+/) {
          # printf("#DEBUG: Concatenation found: %s\n",$remaining);
          $inString = 2;
          next;
        }

        # next parameter?
        if ($remaining =~ /^,/) {
          # printf("#DEBUG: Next Method Parameter found: %s\n",$remaining);
          $inString = 98;
          next;
        }

        # method finished?
        if ($remaining =~ /^\)/) {
          # printf("#DEBUG: End of Method found: %s\n",$remaining);
          $inString = 99;
          next;
        }

      } else { # we're inside a comment

        # end of a comment?
        if ($remaining =~ /^\*\//) {
          # printf("#DEBUG: End of a Comment found: %s\n",$remaining);
          $inComment = 0;
          $i++;
          next;
        }

      }

    }

    if ($inString == 1) { # we're inside a string, adding characters to the current string
      # printf("#DEBUG: in string: >>%s<<\n",$remaining);

      if ($remaining =~ /^"/) {
        # printf("#DEBUG: end of part of string found, string is now: >>%s<<\n",$str);
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
}

