# OpenDS
<a href="https://github.com/Boomaa23/open-ds/actions?query=branch%3Amaster+event%3Apush"><img src="https://github.com/Boomaa23/open-ds/actions/workflows/build.yml/badge.svg" /></a>
<a href="https://github.com/Boomaa23/open-ds/releases/latest"><img src="https://img.shields.io/github/v/release/Boomaa23/open-ds" /></a>

A reverse-engineered lightweight FRC Driver Station alternative for Windows, Linux, and macOS

Download [here](https://boomaa23.github.io/open-ds/#dl-jar) ([JDK/JRE 8+](https://adoptopenjdk.net/) required) or [here](https://boomaa23.github.io/open-ds/#dl-script) (JRE downloaded automatically).

Copyright (C) 2020-2025 Boomaa23


## Features
OpenDS is a fully functional FIRST Robotics Competition (FRC) Driver Station 
alternative for Windows, Linux, and macOS systems.
All the features of the official Driver Station are implemented in OpenDS, 
meaning teams can use it in the place of the official Driver Station 
when testing robot features away from the competition.

OpenDS is extremely lightweight (about 1 MB) and does not require an 
installation of any kind, unlike the official Driver Station which 
has a lengthy installation process and heavy install footprint.

NOTE: OpenDS may not be used during FRC-legal competitions as per 
rules R710 and R901 (previously R66 and R88). 
OpenDS is intended for testing use only.

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
    * Command-line (CLI) parameters
    

## Setup
Download the stable jar from [here](https://boomaa23.github.io/open-ds/#dl-jar) and run. There are no prerequisites besides having a Java installation with [JRE 8](https://adoptopenjdk.net/) or newer. The JRE is included with any installation of the same JDK version.

If you do not have Java and/or want a single install/run script, download [this script](https://boomaa23.github.io/open-ds/#dl-script) instead and use it to start OpenDS. It will download OpenDS and a copy of Java for it to use. Use the same script to re-launch OpenDS.


### Troubleshooting
If you run into issues, ensure that you are running a 64-bit installation of either Windows 7/8.1/10/11, Linux kernel version 2.6.35 or greater, or macOS 10 (OSX) or newer.

Try launching from the command line (`java -jar open-ds.jar`) and observing the console output for additional details. You can also launch with debug (`--debug`) to print more information to the console.

If you are using the WPILib simulator (instead of a physical robot), ensure you have the following line in your `build.gradle` (or equivalent in `build.gradle.kts`).
```groovy
wpi.sim.addDriverstation().defaultEnabled = true
```

If issues persist, please report them on the "Issues" section of the GitHub [here](https://github.com/Boomaa23/open-ds/issues) and they will be resolved as soon as possible.


## License
OpenDS may be used without restriction for the purpose of testing robots by teams and individuals.

See [LICENSE.txt](https://github.com/Boomaa23/open-ds/blob/master/LICENSE.txt) for more details.


## Contributing
If you find a bug or issue with OpenDS, please report it on the "Issues" section of the GitHub [here](https://github.com/Boomaa23/open-ds/issues).

For protocol changes in future years, OpenDS is easily modifiable. Ports, IP addresses, display layouts, and packet creation/parsing are all re-formattable.


## Acknowledgements
Thank you to Jessica Creighton and Alex Spataru for their work on the FRCture documentation and LibDS respectively.
