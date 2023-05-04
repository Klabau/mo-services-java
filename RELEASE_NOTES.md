ESA CCSDS MO services - Release Notes
========================

These Release Notes include a short summary of the updates done for each version.
The different versions and respective updates are the following:

### Version 10
* TBD

### Version 9 (2023)
* This release is a hybrid between the old and the new MAL (top API layer -> new MAL; low Transport layer -> old MAL)
* Fixes the MAL testbed for the new MAL updates
* Lowers memory footprint
* Removes the generation of the Type Factories from the APIs. Introduces new MALElementsRegistry class as a replacement.
* Adds first iteration of the MO Navigator project
* MAL Broker updated to follow the new MAL PUB-SUB
* Removes the Generic Encoding project (merged into the MAL API)

### Version 8 (January 2021)
* Merged multiple repos into a single one
* Increased the Java supported version from 1.6 to 1.8 (Java 8 has Long Term Support until 2030)
* Improvements in the TCP/IP implementation
* Updated the Common API to follow the latest version of the MO Standards
* Added the ZMTP implementation which has passed interoperability tests
* Improved Generic Transport to support execution of the software with Java 9

### Version 7 (December 2017)
* The APIs now include the xml file as a resource (will enable the provision of the XML via the Directory service)
* Increased the Java supported version from 1.5 to 1.6
* Added the latest version of the TCP/IP transport and Binary encoding implementations
* Added the latest version of the ZMTP
* Optimizations on the MAL level for efficiency

### Version 6 (January 2017)
* Untracked
