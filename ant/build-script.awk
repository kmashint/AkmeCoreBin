# build-script.awk
# Sample script to transform a text file.
#
BEGIN {
	FS = "\t"
	OFS = "\t"
}
{
	# 5th field is an ISO dateTtime stamp, need to change T to space for SQL Server
	sub(/T/, " ", $5)
	print
}
END {
}
