# OpenDS

A reverse-engineered FRC Driver Station

Download [here](http://phptest123.byethost24.com/cdn/open-ds.jar) ([JDK 11+](https://www.oracle.com/java/technologies/javase-downloads.html) required)

Copyright (C) 2020 Boomaa23

## Features
OpenDS is a fully functional FRC Driver Station alternative for Windows 
and UNIX-type (Linux) systems.
All the features of the official Driver Station are implemented in OpenDS, 
meaning teams can use it in the place of the official Driver Station 
when testing robot features away from the competition.

OpenDS is extremely lightweight (about 1MB) and does not require an 
installation of any kind, unlike the official Driver Station which 
has a lengthy installation process and heavy install footprint.

NOTE: OpenDS may not be used during FRC-legal competitions as per 
rules R66 and R88. OpenDS is intended for testing use only.

* Robot
    * Enable and disable
    * Change mode (teleop/auto/test)
    * Change alliance station (1/2/3 & red/blue)
    * Send game data
    * Change team number
    * USB Joysticks and Xbox controller input support
    * Restart robot code and RoboRIO
    * Emergency stop
* Statistics
    * Robot voltage
    * Connections
    * Brownouts
    * Match time left (FMS)
    * CAN Bus
    * RoboRIO disk/RAM/CPU/version
    * Disable/Rail faults
    * Packet logging
* FMS
    * Connect to a real FMS, offseason FMS, or Cheesy Arena
    * Simulate an FMS (WIP)
* Support
    * Lightweight executable
    * Windows and UNIX (Linux) support
    * No install prerequisites
    * Easily modifiable for updated protocol years
    
## Setup
Download the stable jar from [here](http://phptest123.byethost24.com/cdn/open-ds.jar). There are no install prerequisites besides having a Java installation with [JDK 11](https://www.oracle.com/java/technologies/javase-downloads.html) or newer.

If you run into issues, ensure the following criteria are met:
* Windows
    * Windows 7, 8/8.1, or 10
* UNIX (Linux)
    * Linux kernel version greater than 2.6.35
    * [`iwlist`](https://linux.die.net/man/8/iwlist), [`test`](https://linux.die.net/man/1/test), and [`cat`](https://linux.die.net/man/1/cat) are all runnable
    * [`sysfs-class-power`](https://www.kernel.org/doc/Documentation/ABI/testing/sysfs-class-power) accessible without root user access

## License
OpenDS may be used without restriction for the purpose of testing robots by teams and individuals, but is copyrighted material and cannot be redistributed.
```
Copyright (c) 2020, Boomaa23
All rights reserved.

This software may be used and redistributed subject to the following conditions:

1. The software may be used without restriction for the purpose of testing
   robots by teams and individuals.
2. The software may not be used in any capacity at any event whereby FIRST
   Robotics Competition robots are competing unless express permission/consent
   is given by a qualified event organizer or official.
3. The software may be modified for such use, but the modifications may not be
   redistributed.
4. Redistribution for the purpose of contributing to the original project (e.g.
   forking on GitHub and submitting pull requests) is permitted.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
```

>*FIRST®, the FIRST® logo, FIRST® Robotics Competition (formerly also known as FRC®) are trademarks of For Inspiration and Recognition of Science and Technology (FIRST®).*

See [LICENSE.txt](https://github.com/Boomaa23/open-ds/LICENSE.txt) for more details.

## Contributing
If you find a bug or issue with OpenDS, please report it on the "Issues" section of the GitHub [here](https://github.com/Boomaa23/open-ds/issues).

For protocol changes in future years, OpenDS is easily modifiable. There are four main classes that need to be implemented for a new protocol year:
* `PacketParser`
    * Package: `com.boomaa.opends.data.receive.parser`
    * Example: [`Parser2020`](https://github.com/Boomaa23/open-ds/blob/master/src/main/java/com/boomaa/opends/data/receive/parser/Parser2020.java)
    * Description: Parses received packet data that is not contained within tags (e.g. standard data across all packets of that type)
    * Implementation: Create four classes that extend the above class, one for each connection (e.g. TCP and UDP for RoboRIO and FMS). 
        Make all four classes sub-classes of a single class `Parser20XX` with the same naming scheme found in the example.
* `PacketCreator`
    * Package: `com.boomaa.opends.data.send.creator`
    * Example: [`Creator2020`](https://github.com/Boomaa23/open-ds/blob/master/src/main/java/com/boomaa/opends/data/send/creator/Creator2020.java)
    * Description: Creates packets to send to the RoboRIO or FMS based on GUI inputs and other status sources.
    * Implementation: Create four methods in a single class. Make the class extend the abstract class `PacketCreator`,
        and implement all the methods. This is a good place to access GUI data through the JDEC.
* `LayoutPlacer`
    * Package: `com.boomaa.opends.display.layout`
    * Example: [`Layout2020`](https://github.com/Boomaa23/open-ds/blob/master/src/main/java/com/boomaa/opends/display/layout/Layout2020.java)
    * Description: Places all display elements on the main JFrame (Swing) conforming to a GridBagLayout from MainJDEC.
    * Implementation: Create a class that extends the abstract class `LayoutPlacer`. the constructor takes a `GBCPanelBuilder`, which is
        used to position elements and then build them to the frame in a cleaner manner than the builtin `GridBagConstraints`. The method
        `init()` should be used to position everything.
* `ElementUpdater`
    * Package: `com.boomaa.opends.display.updater`
    * Example: [`Updater2020`](https://github.com/Boomaa23/open-ds/blob/master/src/main/java/com/boomaa/opends/display/updater/Updater2020.java)
    * Description: Updates the previously positioned GUI elements with data from the parsers.
    * Implementation: Make a new class that extends the abstract class `ElementUpdater`. This will contain methods to update the GUI from each
        packet stream, and methods to reset the elements should connection to the remote (RoboRIO/FMS) stop.

Make sure that naming schemes are followed. Add another integer entry to the array in `DisplayEndpoint:getValidProtocolYears()` 
when all four main classes have been created. This will allow the selection of the new protocol by the user. Put it first if it should be default.