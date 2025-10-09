# meta-gokul Layer

This README file contains information on the contents of the meta-gokul layer for **Raspberry Pi 4B**.

## Yocto Version

**Yocto Version:** walnascar  
**Target Hardware:** Raspberry Pi 4B

## Recent Updates

**Latest Changes (October 2025):**

- **Open Source Migration**: Moved CAN SDK source from internal repository to public GitHub
- **Enhanced CAN Support**: Added comprehensive CAN and LIN library support (libcanlib, liblinlib)
- **Kernel Module Improvements**: Added kvcommon module and automatic module loading
- **Bug Fixes**: Applied 64-bit time format fixes for modern systems
- **Developer Libraries**: Added development headers and versioned shared libraries
- **USB CAN Support**: Enhanced support for Kvaser USB CAN interfaces

## Prerequisites

**Ubuntu Version:** 22.04

Install the required packages:

```bash
sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib \
     build-essential chrpath socat cpio python3 python3-pip python3-pexpect \
     xz-utils debianutils iputils-ping python3-git python3-jinja2 libegl1-mesa libsdl1.2-dev \
     pylint3 xterm
```

## 1. Setup

### Create workspace and clone repositories

Clone the required Yocto repositories and the meta-gokul layer:

```bash
mkdir yocto
cd yocto

git clone git://git.yoctoproject.org/poky -b walnascar
git clone git://git.yoctoproject.org/meta-raspberrypi -b walnascar
git clone https://git.openembedded.org/meta-openembedded -b walnascar
git clone https://github.com/gokulshiva77/meta-gokul.git
```

### Initialize build environment

```bash
sources/poky/oe-init-build-env build_pi
```

## 2. Configuration

### bblayers.conf

Add the following to your `bblayers.conf` file:

```bash
# POKY_BBLAYERS_CONF_VERSION is increased each time build/conf/bblayers.conf
# changes incompatibly
POKY_BBLAYERS_CONF_VERSION = "2"

BBPATH = "${TOPDIR}"
BBFILES ?= ""

BBLAYERS ?= " \
  ${TOPDIR}/../poky/meta \
  ${TOPDIR}/../poky/meta-poky \
  ${TOPDIR}/../poky/meta-yocto-bsp \
  ${TOPDIR}/../poky/meta-raspberrypi \
  ${TOPDIR}/../poky/meta-openembedded/meta-oe \
  ${TOPDIR}/../poky/meta-openembedded/meta-multimedia \
  ${TOPDIR}/../poky/meta-openembedded/meta-networking \
  ${TOPDIR}/../poky/meta-openembedded/meta-python \
  ${TOPDIR}/../poky/meta-gokul \
  "
```

### local.conf

Add the following to your `local.conf` file:

```bash
CONF_VERSION = "2"

# MACHINE
MACHINE ??= "gokul-pi"

PARALLEL_MAKE ?= "-j ${@oe.utils.cpu_count()}"
BB_NUMBER_THREADS ?= "${@oe.utils.cpu_count()}"
LICENSE_FLAGS_ACCEPTED += " commercial"

INHERIT += "rm_work"

LICENSE_FLAGS_ACCEPTED = "synaptics-killswitch"
```

## 3. Build

```bash
bitbake core-image-gokul
```

## 4. Flash

### Navigate to deployment directory

```bash
cd tmp/deploy/images/gokul-pi
```

### Extract the deployment package

```bash
unzip gokul-pi-image.zip
```

### Run the interactive flash script

```bash
./flash-script.sh
```

The script will:

- Auto-detect the WIC image file
- Decompress if needed
- Show available removable storage devices
- Prompt for safe device selection
- Flash with progress indication

## 5. Layer Customizations

This meta-gokul layer provides several customizations for Raspberry Pi-based embedded systems:

### Custom Machine Configuration (`gokul-pi`)

- **Base Machine**: Built on top of Raspberry Pi 4B 64-bit configuration (`raspberrypi4-64.conf`)
- **Target Hardware**: Raspberry Pi 4B with 64-bit ARM Cortex-A72 processor
- **System Init**: Uses systemd instead of sysvinit
- **UART Support**: UART is enabled by default for debugging and development
- **User Merge**: Implements usrmerge feature for modern Linux filesystem layout
- **Auto Login**: Configured for serial auto-login as root for development

### Custom Image (`core-image-gokul`)

The layer provides a custom image recipe with the following additional packages:

#### Development Tools

- `packagegroup-core-buildessential` - Essential build tools
- `ldd` - Library dependency lister
- `tar` - Archive utility
- `dpkg` & `apt` - Package management tools

#### Network Tools

- `net-tools` - Network configuration utilities
- `bridge-utils` - Bridge configuration tools
- `hostapd` - WiFi access point daemon
- `iptables` - Firewall configuration

#### System Utilities

- `minicom` - Serial communication program
- `nano` & `vim` - Text editors
- `iw` - Wireless configuration utility
- `wpa-supplicant` - WiFi authentication
- `openssh` - SSH server and client
- `rsync` - File synchronization tool

#### CAN/LIN Support

- `linux-can-driver` - Kernel modules for Kvaser CAN interfaces
- `linux-can-sdk` - CAN and LIN communication libraries (libcanlib, liblinlib)

### Dual-Boot Partitioning (`gokul-image.wks`)

The layer includes a custom WIC (Wic Image Creator) file that creates a dual-boot partition scheme optimized for Raspberry Pi 4B:

- **Boot Partition**: 256MB FAT partition for Raspberry Pi 4B bootloader and firmware
- **Root A Partition**: 5GB ext4 partition (Primary root filesystem)
- **Root B Partition**: 5GB ext4 partition (Secondary root for A/B updates)
- **SD Card Compatibility**: Optimized for SD card and eMMC storage on Raspberry Pi 4B
- Support for dual-root A/B update mechanism

### Network Configuration

Custom systemd network configuration optimized for Raspberry Pi 4B:

- **Static IP**: Configures Raspberry Pi 4B Ethernet (eth0) with static IP `192.168.8.1/24`
- **Gigabit Ethernet**: Takes advantage of Raspberry Pi 4B's Gigabit Ethernet capability
- **DNS**: Uses Google DNS (8.8.8.8)
- **NFS Boot**: Disabled for normal SD card/eMMC operation

### Kernel Modules and Libraries

#### Linux CAN SDK (`linux-can-sdk`)

- **Purpose**: Comprehensive CAN (Controller Area Network) and LIN (Local Interconnect Network) library support
- **Hardware Support**: Utilizes Raspberry Pi 4B for CAN/LIN communication via USB adapters
- **Source**: Open-source repository on GitHub (`github.com/gokulshiva77/linux-can-sdk`)
- **License**: GPLv2
- **Features**:
  - CAN bus communication library (libcanlib) with versioned shared libraries
  - LIN bus communication library (liblinlib) with versioned shared libraries
  - Header files for application development (canlib.h, linlib.h)
  - Dynamic version detection from source
  - Development package support for custom applications

#### Linux CAN Driver (`linux-can-driver`)

- **Purpose**: Kernel module drivers for Kvaser CAN interfaces
- **Supported Hardware**: Kvaser Leaf, USBcanII, mHydra, and common modules
- **Source**: Open-source repository on GitHub (`github.com/gokulshiva77/linux-can-sdk`)
- **License**: GPLv2
- **Features**:
  - Kernel modules: `leaf.ko`, `usbcanII.ko`, `mhydra.ko`, `kvcommon.ko`
  - Automatic module loading on boot
  - Blacklisting of conflicting default kernel modules
  - Proper kernel version compatibility

### Boot Files Configuration

#### Raspberry Pi 4B Boot Files (`recipes-bsp/bootfiles`)

- **rpi-cmdline.bbappend**: Customizes Raspberry Pi 4B kernel command line parameters
- **rpi-config.bbappend**: Modifies Raspberry Pi 4B specific configuration settings (GPU memory, HDMI, etc.)
- **Boot Directory**: Installs boot configuration files to `/boot` directory for Pi 4B firmware

### Dual-Boot Support

#### Root Filesystem Population (`populate-rootfs-b`)

- **Purpose**: Automatic population of secondary root filesystem (rootfsB) from primary (rootfsA)
- **Service**: Systemd service that runs on first boot
- **Functionality**:
  - Detects rootfsA and rootfsB partitions by label
  - Creates ext4 filesystem on rootfsB if needed
  - Uses rsync to copy all data except pseudo-filesystems and boot partition
  - Sets initialization flag to prevent re-running
  - Enables A/B update mechanism for system redundancy

### Flash Scripts

#### Automated Image Flashing (`flash-scripts`)

- **Purpose**: Simplified image flashing to removable storage devices
- **Features**:
  - Auto-detects `.wic.gz` files in current directory
  - Automatically decompresses compressed images
  - Lists available removable storage devices
  - Interactive device selection with safety prompts
  - Progress monitoring during flash operation
  - Uses `dd` with optimized block size and sync

### Enhanced Build System

#### Automated Packaging

- **ZIP Creation**: Automatically creates deployment packages
- **Deploy Files**: Includes WIC image and flash script in deployment
- **File Validation**: Ensures all required files exist before packaging
- **Clean Tasks**: Manages build artifacts and cleanup

### Build Configuration

- **Layer Priority**: Set to 6
- **Dependencies**: Requires core layer
- **Compatibility**: Compatible with walnascar Yocto release
- **Pattern**: Follows standard Yocto layer structure

## 6. Build Output

The build will generate files optimized for Raspberry Pi 4B:

- **Image File**: A custom Linux image with all specified packages and configurations for Pi 4B
- **WIC Image**: Dual-boot capable image with A/B partitioning scheme ready for flashing to Raspberry Pi 4B SD card or eMMC
- **Root Filesystem**: Complete filesystem with systemd, development tools, and CAN support optimized for Pi 4B hardware
- **Deployment Package**: Automated ZIP file (`gokul-pi-image.zip`) containing:
  - Compressed WIC image (`.wic.gz`) for Raspberry Pi 4B
  - Flash script for easy deployment to SD cards
  - All necessary deployment files
- **Flash Script**: Interactive script for safe image flashing to removable devices (SD cards, USB drives)

## 7. First Boot Behavior

After flashing and booting the Raspberry Pi 4B:

1. **Automatic A/B Setup**: The system automatically populates the secondary root filesystem (rootfsB) from the primary (rootfsA)
2. **Dual-Boot Ready**: Raspberry Pi 4B becomes ready for A/B updates and redundancy
3. **Network Configuration**: Raspberry Pi 4B Ethernet interface configured with static IP `192.168.8.1/24`
4. **Development Access**: Serial console auto-login enabled for development via Pi 4B UART pins
5. **CAN Support**: Linux CAN SDK kernel module loaded and ready for Pi 4B GPIO CAN communication
6. **Systemd Services**: Modern init system with custom network configuration optimized for Pi 4B

## 8. Troubleshooting

### Common Build Issues

**Build Errors with CAN SDK:**

- Ensure all required packages are installed (see Prerequisites section)
- Check that Git has proper access to GitHub repositories
- Verify network connectivity for repository cloning

**Missing Dependencies:**

```bash
# If build fails due to missing dependencies, clean and rebuild
bitbake -c cleanall linux-can-sdk linux-can-driver
bitbake core-image-gokul
```

**Flash Script Issues:**

- Ensure the script has execute permissions: `chmod +x flash-script.sh`
- Run with sudo if permission errors occur: `sudo ./flash-script.sh`
- Verify the target device is properly connected and detected

### First Boot Issues

**Network Not Working:**

- Check Ethernet cable connection
- Verify network configuration in `/etc/systemd/network/wired.network`
- Restart networking: `systemctl restart systemd-networkd`

**CAN Interface Problems:**

- Check if modules are loaded: `lsmod | grep -E "(leaf|usbcan|mhydra|kvcommon)"`
- Manually load modules: `modprobe leaf usbcanII mhydra kvcommon`
- Check USB CAN device connection: `lsusb`

## 9. System Features Summary

- **Raspberry Pi 4B Optimized**: Specifically designed and optimized for Raspberry Pi 4B hardware
- **Professional Deployment**: Automated packaging and safe flashing tools for SD cards
- **Dual-Boot Architecture**: A/B update mechanism for reliable system updates on Pi 4B
- **Development Ready**: Complete build environment and debugging tools
- **Network Configured**: Static IP setup with DNS configuration utilizing Pi 4B Gigabit Ethernet
- **CAN Bus Support**: Hardware-specific CAN communication capabilities using Pi 4B GPIO pins
- **Modern Init System**: Systemd with custom service configurations optimized for embedded Pi 4B systems
