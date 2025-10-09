SUMMARY = "Populate Root FS B from A"
DESCRIPTION = "Custom Raspberry Pi recipe to Populate Root FS B from A"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://populate-rootfs-b.sh \
           file://populate-rootfs-b.service \
          "

inherit systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install(){
    install -d ${D}${bindir}
    install -d ${D}${systemd_system_unitdir}

    install -m 0755 ${S}/populate-rootfs-b.sh ${D}${bindir}/populate-rootfs-b.sh
    install -m 0644 ${S}/populate-rootfs-b.service ${D}${systemd_system_unitdir}/populate-rootfs-b.service
}

SYSTEMD_SERVICE:${PN} = "populate-rootfs-b.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

FILES:${PN} += "${bindir} ${systemd_system_unitdir}"