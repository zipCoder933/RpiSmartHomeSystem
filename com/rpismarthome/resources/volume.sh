
#!/bin/bash
amixer scontrols

amixer sset 'Master' $1%
amixer sset 'Headphone' $1%
amixer sset 'Headphones' $1%


