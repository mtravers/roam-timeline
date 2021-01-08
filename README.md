# roam-timeline

Generate timelines from Roam data. 

Different graphs are possible, current is a stacked bar chart, each bar is a day, stacks are counts of common tags.

Status: crude but working.


## Usage

Export a graph from Roam in JSON  format, then:

    $ lein run <zipfile>


### TODO

### Options
- Limit to n tags or tags with > m usages (currently fixed at 3)
- Limit to Daily Notes
- Filter to time range (currently uses latest year)

### Legend
- order by count

## License

Copyright © 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
