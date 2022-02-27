#!/bin/bash

#FOR GOOGLE TEXT TO SPEECH
#THIS ONE IS BETTER, BUT SOMETIMES FAILS.
#say() { local IFS=+;/usr/bin/mplayer -ao alsa -really-quiet -noconsolecontrols>
#say $*

#ESPEAK (LESS QUALITY, BUT OFFLINE AND IS BUG FREE)
espeak $*

