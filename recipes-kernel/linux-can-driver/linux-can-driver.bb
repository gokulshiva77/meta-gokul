SUMMARY = "Linux CAN Driver"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=df8cdeaa1ac5c05e71624f0446dbec13"

SRC_URI = "git://github.com/gokulshiva77/linux-can-sdk.git;protocol=https;branch=main \
           file://0001-Fixed-Build-Error.patch \
           "
SRCREV = "4f646860b1ac223acca3ec8b9018ef483ee5a487"

inherit module

S = "${WORKDIR}/git"

DEPENDS += " kernel-devsrc pkgconfig"
EXTRA_OEMAKE = "KDIR=${STAGING_KERNEL_DIR} KERNEL_VERSION=${KERNEL_VERSION} KV_NO_PCI=1"

MODULES_MODULE_SYMVERS_LOCATION = "${S}"

# Skip QA checks that are not relevant for kernel modules
INSANE_SKIP:${PN} = "buildpaths"

do_compile(){
    oe_runmake -C "${S}"
}

do_install(){
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra

    # Install kernel modules if they exist
    if [ -f ${S}/leaf/leaf.ko ]; then
        install -m 644 ${S}/leaf/leaf.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/
    fi
    
    if [ -f ${S}/usbcanII/usbcanII.ko ]; then
        install -m 644 ${S}/usbcanII/usbcanII.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/
    fi
    
    if [ -f ${S}/mhydra/mhydra.ko ]; then
        install -m 644 ${S}/mhydra/mhydra.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/
    fi
    if [ -f ${S}/common/kvcommon.ko ]; then
        install -m 644 ${S}/common/kvcommon.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/
    fi
}

# Blacklist conflicting kernel modules
pkg_postinst:${PN}() {
    if [ -z "$D" ]; then
        # Running on target
        if [ ! -f /etc/modprobe.d/blacklist-kvaser_usb.conf ]; then
            echo "blacklist kvaser_usb" > /etc/modprobe.d/blacklist-kvaser_usb.conf
        fi
        depmod -a
    fi
}

FILES:${PN} = "${libdir}/modules/* ${sysconfdir}/modprobe.d/*"

RPROVIDES:${PN} += " kernel-module-leaf-${KERNEL_VERSION} kernel-module-usbcanii-${KERNEL_VERSION} kernel-module-mhydra-${KERNEL_VERSION} kernel-module-kvcommon-${KERNEL_VERSION}"

KERNEL_MODULE_AUTOLOAD:${PN} = "leaf usbcanII mhydra kvcommon"
