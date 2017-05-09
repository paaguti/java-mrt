java-mrt
========

Java library to parse the binary MRT format.

I have used this library over the last years extensively.
Please send any bug fixes to me paaguti :#at#: gmail _dot_ com

This library is released under LGPL license. Read LICENSE.txt.

Based on:
* RFC 6396
* RFC 8050

Limitations:
* RIB_IPv4_Multicast not supported
* RIB_IPv6_Multicast not supported
* RIB_GENERIC not supported
* BGP4MP_MESSAGE_LOCAL not supported
* BGP4MP_MESSAGE_LOCAL_AS4 not supported
* all previous named codes in the *_ADDPATH versions are not supported

This is great work but needs a code review / polishing and test cases.
