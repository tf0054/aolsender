# aolsender

A Clojure library designed to test genn.ai with AOL search query data.

## Usage

### exec
cat gaute/user-ct-test-collection-01.txt.head1000.json | ~/bin/lein run

### output
Return status, duration(millsec), sended line, (thread-id)

```204 5 {"uid":"142","query":"westchester.gov","time":"2006-03-20 03:55:57","rank":"1","url":"http://www.westchestergov.com"} (13)
```

Copyright © 2014 genn.ai core team

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
