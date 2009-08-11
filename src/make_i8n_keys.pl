#!/usr/bin/perl

my $properties = shift;

if (length($properties) == 0) {
  printf("usage: make_i8n_keys.pl <properties>\n");
  printf("\nThis script recursively searches all .java files in the current\n"+
         "directory and any subdirectories for internationalized strings and\n"+
         "adds them to the specified properties file.\n");
  exit(1);
}

# read existing properties
open(PROPS,$properties) || die "cannot open property file: $properties\n";
printf("\n\n# Old Keys:\n#-------------------------------------\n");
while(<PROPS>) {
  next if /^#/;
  if (/([^=]+)=(.+)/) {
    my $key = $1;
    my $txt = $2;
    $keys{$key} = $txt;
    printf("$key=$txt\n");
  }
}
close(PROPS);

# traverse directories and search for .java files
printf("\n\n# New Keys:\n#-------------------------------------\n");
searchdir(".");

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
  printf("# $file\n");
  while(<JAVA>) {
    
    # getString(String) or getStringWithMnemonic(String)
    if (/International.getString[^\(]*\s*\(\s*"([^"]+)"\s*\)/) {
      my $txt = $1;
      my $key = $txt;
      $key =~ s/ /_/g;
      $key =~ s/=/_/g;
      if ($keys{$key}) {
        # printf("DUPLICATE $key=$txt\n");
      } else {
        printf("$key=$txt\n");
      }
    }

    # getString(String,String) or getStringWithMnemonic(String,String)
    if (/International.getString[^\(]*\s*\(\s*"([^"]+)"\s*,\s*"([^"]+)"\s*\)/) {
      my $txt = $1;
      my $discr = $2;
      my $key = $txt . "___" . $discr;
      $key =~ s/ /_/g;
      $key =~ s/=/_/g;
      if ($keys{$key}) {
        # printf("DUPLICATE $key=$txt\n");
      } else {
        printf("$key=$txt\n");
      }
    }

    # getMessage(String, ...)
    if (/International.getMessage\s*\(\s*"([^"]+)"\s*,/) {
      my $txt = $1;
      my $key = $txt;
      $key =~ s/ /_/g;
      $key =~ s/=/_/g;
      my $i = 1;
      while ($txt =~ /{[^\}]+}/) {
        $txt =~ s/{[^\}]+}/%_1_%$i%_2_%/;
        $i++;
      }
      $txt =~ s/%_1_%/{/g;
      $txt =~ s/%_2_%/}/g;
      if ($keys{$key}) {
        # printf("DUPLICATE $key=$txt\n");
      } else {
        printf("$key=$txt\n");
      }
    }

  }
  close(JAVA);
}