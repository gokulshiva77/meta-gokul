
# gokul-pi.inc - custom machine for Yocto, based on Raspberry Pi 4
require conf/machine/raspberrypi4.conf

# Custom overrides and additions
MACHINEOVERRIDES .= ":gokul-pi"

# Kernel configuration for gokul-pi machine
KBUILD_DEFCONFIG:gokul-pi ?= "bcm2711_defconfig"

#Enable Uart
ENABLE_UART = "1"

IMAGE_FSTYPES = "wic.gz ext4"
WKS_FILE = "gokul-image.wks"

DISTRO_FEATURES:append = " usrmerge systemd"
DISTRO_FEATURES:remove = "sysvinit"

# Force the use of systemd as the init manager
VIRTUAL-RUNTIME_init_manager = "systemd"

MACHINE_EXTRA_RRECOMMENDS:append = " linux-can-driver"

CORE_IMAGE_EXTRA_INSTALL += "iw \
                            wpa-supplicant \
                            hostapd \
                            openssh \
                            rsync\
                            linux-can-driver \
                            linux-can-sdk \
                            "
IMAGE_FEATURES:append = " serial-autologin-root"

CORE_IMAGE_EXTRA_INSTALL += " ldd \
                            minicom \
                            net-tools \
                            dpkg \
                            apt \
                            tar \
                            packagegroup-core-buildessential \
                            bridge-utils \
                            hostapd \
                            nano \
                            iptables \
                            vim \
                            "
# CORE_IMAGE_EXTRA_INSTALL += " populate-rootfs-b"
