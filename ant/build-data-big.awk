# Example: gawk -W posix -v n=1000000 build-data-big.awk input.txt >output.txt
NR==1 {
	for (i=1; i<=n; i++) print;
}