#!/usr/bin/env perl
#
# script: tc-viewer 1.5
# author: Pawel 'snaj' Pawilcz
# email: pawel_pawilcz(at)yahoo.com
# www: http://snaj.ath.cx/
# licence and copyrights: GNU GPL
#
# example: 	tc-viewer --sort --unit=kbit --timer=5 --conf=/etc/tc-viewer.conf
#

use strict;
use Getopt::Long qw(GetOptions);
use File::Basename qw(basename);

my ($conf, $sort, $zero, $top, $ifhelp, $unit, $iface, $counter, $timer, $hfsc, $colors ) = ( "", "0", "0", "", "0", "bit", "", "", 2, "0", "0");
my (%color_set, %names, %speedspom, %classes, %speeds) = ( ( ), ( ), ( ), ( ), ( ) );
my ($pom_line_counter, $line_counter, $class_ok, $byte_index, $pkt_index, $class, $parent, $indent, $mydiv, $count, $pomiface) = (0, 0, 0, 0, 0, "", "", "   ", 1, 0, "" );
my $help = "Usage: ". basename($0) ." [--help] [--iface=<interface>] [--hfsc] [--sort] [--zero] [--timer=<seconds>] [--top=<X>] [--counter=<X>] [--colors] [--unit=<bit,B,kbit,kB>] [--conf=<tc-viewer-conf>]

	--iface=<interface>	interface to listen on; must be specified
	--conf=<filename>	path to configuration file; default none
	--hfsc			hfsc mode; default htb mode
	--sort			sort speeds; default disabled
	--zero			show classes with 0 speeds; default disabled
	--timer=<X>		refresh delay in seconds; default 2
	--top=<X>		show X speeds; default show all speeds
	--counter=<X>		after X refreshes exit; default never exit
	--colors		enable colored output; default disabled
	--unit=<bit,B,kbit,kB>	speed unit; default bit
	--help			prints this info

tc-viewer 1.5 by snaj

";
my %colors = (
	'RST' => "\033[0m",           # reset
	'RED' => "\033[40;31m",       # red
	'GRE' => "\033[40;32m",       # green
	'YEL' => "\033[40;33m",       # yellow
	'BLU' => "\033[40;34m",       # blue
	'PIN' => "\033[40;35m",       # pink
	'LBL' => "\033[40;36m",       # light blue
	'BRED' => "\033[40;31;1m",    # bold red
	'BGRE' => "\033[40;32;1m",    # bold green
	'BYEL' => "\033[40;33;1m",    # bold yellow
	'BBLU' => "\033[40;34;1m",    # bold blue
	'BPIN' => "\033[40;35;1m",    # bold pink
	'BLBL' => "\033[40;36;1m",    # bold light blue
	'NONE' => "",
);

GetOptions(
	'iface=s' => \$iface,
	'conf=s' => \$conf,
	'top=i' => \$top,
	'counter=i' => \$counter,
	'timer=i' => \$timer,
	'sort!' => \$sort,
	'zero!' => \$zero,
	'colors!' => \$colors,
	'hfsc!' => \$hfsc,
	'help' => \$ifhelp,
	'unit=s'=> \$unit,
) || die $help;

die $help if $ifhelp;

$pomiface = $iface if $iface;

eval `cat $conf` || exit 1 if $conf;

$iface = $pomiface if $pomiface;
die $help unless $iface;

$unit = "bit" if !$unit || $unit !~ /^(bit|kbit|B|kB)$/;

$mydiv = 
	$unit eq "bit" ? 1/8 :
	$unit eq "kbit" ? 1024/8 :
	$unit eq "kB" ? 1024 : 1;

$byte_index = ($pkt_index=2)++;

if ($colors eq "0") {
	$color_set{'Name'} = $color_set{'Speed'} = $color_set{'Range'} = $color_set{'Neutral'} = $color_set{'Reset'} = 'NONE';
} else {
	$color_set{'Name'} = 'GRE' unless $color_set{'Name'};
	$color_set{'Speed'} = 'RED' unless $color_set{'Speed'};
	$color_set{'Range'} = 'YEL' unless $color_set{'Range'};
	$color_set{'Neutral'} = 'BPIN' unless $color_set{'Neutral'};
	$color_set{'Reset'} = 'RST';
}
$|=1;

sub view_speeds {
	(my @TC = `tc -s class show dev $iface`) || exit 1;

	for (@TC) {
		if ($hfsc eq "1") {
			next	if(/^( period| backlog)/ || /^$/ );
			if(/^class hfsc (\S+) (root|parent (\S+) (sc|ls) m1 (\S+) d 0us m2 (\S+) ul m1 (\S+) d 0us m2 (\S+)) /) {
				my ($pom4, $pom5) = ($6, $8);
				$class = $1;
				$parent=$2;
                                if ($parent !~ /root/) {
                                        $parent=$3
                                }
				$class .= "0" if ($class =~ /:$/);
				$parent .= "0" if ($parent =~ /:$/);
                                push @{$speeds{$class}}, $pom4;
                                push @{$speeds{$class}}, $pom5;
				push @{$classes{$parent}}, $class unless $class_ok;

				next;
			}
		} else {
			next	if(/^( rate| lended| tokens)/ || /^$/);

			if(/^class htb (\S+) (root|parent (\S+)) .*rate (\S+) ceil (\S+) /) {
				my ($pom4, $pom5) = ($4, $5);
				$class = $1;
				($parent=$2)=~s/parent //;				
				$class .= "0" if ($class =~ /:$/);
				$parent .= "0" if ($parent =~ /:$/);
				push @{$speeds{$class}}, $pom4;
				push @{$speeds{$class}}, $pom5;
				push @{$classes{$parent}}, $class unless $class_ok;
				next;
			}
		} 

		if(/^ Sent (\d+) bytes (\d+) pkt/) {
			push @{$speeds{$class}}, $1;
			push @{$speeds{$class}}, $2;			
			if ($zero eq "0") {
				if ($class_ok && ((@{$speeds{$class}}[$byte_index] -= @{$speedspom{$class}}[$byte_index]) == 0)) {				
					if ( $hfsc eq "0" || !exists $classes{$class} ) {
						@{$classes{$parent}}[$_] cmp $class || !(splice @{$classes{$parent}}, $_, 1) || last  foreach (0..$#{$classes{$parent}});
						delete $speeds{$class};
					} 
					if ( $hfsc eq "0" ) {
						delete $classes{$parent} unless @{$classes{$parent}};
					}
					next;
				}
			} else {
				@{$speeds{$class}}[$byte_index] -= @{$speedspom{$class}}[$byte_index] if $class_ok;
			}
			
			@{$speeds{$class}}[$pkt_index] -= @{$speedspom{$class}}[$pkt_index] if $class_ok;
			next;
		}
	}
	$class_ok ? $class_ok-- : $class_ok++;
}

sub exit_handler {
	die "\e[?25h\n\n";
}
     
sub sum() {
	my ($class) = @_;	
	return unless exists $classes{$class};
	return unless @{$speeds{$class}}[0] == 0;

	&sum($_) foreach (@{$classes{$class}});
	
	@{$speeds{$class}}[0] += @{$speeds{$_}}[0] foreach (@{$classes{$class}});
	@{$speeds{$class}}[1] += @{$speeds{$_}}[1] foreach (@{$classes{$class}});
}

$SIG{$_} = 'exit_handler' foreach (qw/INT KILL TERM/);

print "\e[?25l\e[2J\e[1;1H\n\tCalculating ...\n";

while (1) {
	&view_speeds;
	%speedspom = %speeds;
	%speeds = ( );
	sleep $timer;
	&view_speeds;
	
	if ($hfsc eq "1") {
		&sum($_) foreach (@{$classes{'root'}});
	}
	
	if ($sort eq "1") {
		@{$classes{$_}} = reverse sort { @{$speeds{$a}}[$byte_index] <=> @{$speeds{$b}}[$byte_index] || substr( $a, (index $a, ':')+1) cmp substr( $b, index ($b, ':')+1) } @{$classes{$_}} foreach (keys %classes);
	} else {
		@{$classes{$_}} = sort { substr($a, index($a, ':')+1) cmp substr($b, index($b, ':')+1) } @{$classes{$_}} foreach (keys %classes);
	}

	system("clear");
	system("date");
#	print strftime "\e[1;1H \n$colors{$color_set{'Neutral'}}   %a %b %e %H:%M:%S %Y", localtime;

	print "\n\n\tMode: ", $hfsc eq "0" ? "HTB" : "HFSC" ,"        ^C to QUIT$colors{$color_set{'Reset'}}\n\n";

	if (exists $classes{'root'}) {
		my @pom_tab = (keys %speeds);
		if ($top) {
			if ($sort && $sort eq "1") {
				@pom_tab = reverse sort { @{$speeds{$a}}[$byte_index] <=> @{$speeds{$b}}[$byte_index] || substr( $a, (index $a, ':')+1) <=> substr( $b, index ($b, ':')+1) } @pom_tab;
			} else {	
				@pom_tab = sort { substr($a, index($a, ':')+1) <=> substr($b, index($b, ':')+1) } @pom_tab;
			}
			for (1..$top)	{
				if (defined $pom_tab[0] && exists $classes{$pom_tab[0]}) {
					shift @pom_tab;
					redo;
				} else {
					shift @pom_tab;
				}
			}
			delete $speeds{$_} foreach (@pom_tab);
		}	
				

		foreach (@{$classes{'root'}}) {
			if ($hfsc eq "1") {				
				foreach (@{$classes{$_}}) {
					&show($_, "");
					last unless !$top || $count <= $top;
				}
			} else {
				&show($_, "");
			}
			last unless !$top || $count <= $top;
		}
	} else {
		print "\n\t$colors{$color_set{'Neutral'}}No transfers ...$colors{$color_set{'Reset'}}\n";
	}
	%classes = %speeds = %speedspom = ( );
	--$counter || &exit_handler if $counter;
	print "\n\e[2K" x ($pom_line_counter - $line_counter) if ($line_counter < $pom_line_counter);

	$pom_line_counter = $line_counter;
	$line_counter = $count = 0;
}

sub show {
	my ($class, $indent_new) = @_;
	my $myindent = $indent_new ? $indent_new.$indent : $indent_new." ";
	return unless (exists $speeds{$class});
	printf "\n\e[2K%s%s%-20s%s < %s%10s%s - %s%10s%s > %s%15s%s  (%s%3dpps%s)", $colors{$color_set{'Name'}}, $myindent, exists $names{$class} ? $names{$class} : $class , $colors{$color_set{'Reset'}}, $colors{$color_set{'Range'}}, @{$speeds{$class}}[0], $colors{$color_set{'Reset'}}, $colors{$color_set{'Range'}}, @{$speeds{$class}}[1], $colors{$color_set{'Reset'}}, $colors{$color_set{'Speed'}}, sprintf("%.1f", @{$speeds{$class}}[2]/($timer*$mydiv)).' '.$unit.'/s', $colors{$color_set{'Reset'}}, $colors{$color_set{'Neutral'}}, @{$speeds{$class}}[3]/$timer, $colors{$color_set{'Reset'}};
	$line_counter++;
	if (exists $classes{$class}) {
		foreach (@{$classes{$class}}) {
			&show($_, $myindent);
			return unless !$top || $count <= $top;
		}
	} else {
		$count++;
		return unless !$top || $count <= $top;
	}
}

&exit_handler;
