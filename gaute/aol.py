
import sys


def makeJson(name, value):
	return '"' + name + '":"' + value.rstrip() + '"'

if len(sys.argv) >= 2:
	f = open(sys.argv[1])
else:
	f = sys.stdin

f.readline()

for line in f:
	# line = line.rstrip()
	uid, query, time, rank, url = line.split('\t')

	print '{' + ','.join(map(lambda x: makeJson(x[0], x[1]), [('uid', uid), ('query', query), ('time', time), ('rank', rank), ('url', url)])) + '}'

