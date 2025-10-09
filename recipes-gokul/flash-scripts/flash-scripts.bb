DESCRIPTION = "Flash configuration"
LICENSE = "Proprietary"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://flash-script.sh"

S = "${UNPACKDIR}"
inherit deploy

do_deploy() {
    install -m 755 ${S}/flash-script.sh ${DEPLOYDIR}/flash-script.sh
}

do_deploy[cleandirs] = "${DEPLOYDIR}"

ALLOW_EMPTY:${PN} = "1"

addtask deploy before do_package after do_install