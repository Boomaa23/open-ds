# OpenDS
![Java CI](https://github.com/Boomaa23/open-ds/workflows/Java%20CI/badge.svg)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/Boomaa23/open-ds)

A reverse-engineered lightweight FRC Driver Station alternative for Windows, Linux, and macOS

Download [here](https://boomaa23.github.io/open-ds/) ([JDK 8+](https://adoptopenjdk.net/) required)

Copyright (C) 2020-2021 Boomaa23

## Features
OpenDS is a fully functional FIRST Robotics Competition (FRC) Driver Station 
alternative for Windows, Linux, and macOS systems.
All the features of the official Driver Station are implemented in OpenDS, 
meaning teams can use it in the place of the official Driver Station 
when testing robot features away from the competition.

OpenDS is extremely lightweight (about 0.25 MB) and does not require an 
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
    * USB Joystick and Xbox controller input support
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
    * Logging to `.dslog` files
* NetworkTables
    * Read Shuffleboard and SmartDashboard packets
    * Display NetworkTables passed data
* FMS
    * Connect to a offseason FMS or Cheesy Arena
    * Choose to connect or not
* Support
    * Lightweight executable
    * Windows, Linux, and macOS support
    * No install prerequisites
    * Easily modifiable for updated protocol years
    
## Setup
Download the stable jar from [here](https://boomaa23.github.io/open-ds/) and run. There are no prerequisites besides having a Java installation with [JDK 8](https://adoptopenjdk.net/) or newer.

### Troubleshooting
If you run into issues, ensure that you are running a 64-bit installation of either Windows 7/8.1/10/11, Linux kernel version 2.6.35 or greater, or macOS 10 (OSX) or newer.

Try launching from the command line (`java -jar open-ds.jar`) and observing the console output for additional details.

If issues persist, please report them on the "Issues" section of the GitHub [here](https://github.com/Boomaa23/open-ds/issues) and they will be resolved as soon as possible.



## License
OpenDS may be used without restriction for the purpose of testing robots by teams and individuals.

See [LICENSE.txt](https://github.com/Boomaa23/open-ds/blob/master/LICENSE.txt) for more details.

## Contributing
If you find a bug or issue with OpenDS, please report it on the "Issues" section of the GitHub [here](https://github.com/Boomaa23/open-ds/issues).

For protocol changes in future years, OpenDS is easily modifiable. Ports, IP addresses, display layouts, and packet creation/parsing are all re-formattable.

## Acknowledgements
Thank you to Jessica Creighton for the FRCture documentation and to Alex Spataru for his work on LibDS.