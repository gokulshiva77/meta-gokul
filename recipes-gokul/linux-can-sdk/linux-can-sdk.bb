SUMMARY = "Linux CAN Driver"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=df8cdeaa1ac5c05e71624f0446dbec13"

SRC_URI = "git://github.com/gokulshiva77/linux-can-sdk.git;protocol=https;branch=main \
           file://0001-fix-printf-format-for-64bit-time.patch \
           "
SRCREV = "4f646860b1ac223acca3ec8b9018ef483ee5a487"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "CC='${CC}' LDFLAGS='${LDFLAGS}'"

do_configure() {
    # This source doesn't need any configuration
    true
}

get_canlib_version() {
    local major=$(grep "^MAJOR" ${S}/canlib/version.mk | cut -d'=' -f2 | tr -d ' ')
    local minor=$(grep "^MINOR" ${S}/canlib/version.mk | cut -d'=' -f2 | tr -d ' ')
    local build=$(grep "^BUILD" ${S}/canlib/version.mk | cut -d'=' -f2 | tr -d ' ')
    echo "${major}.${minor}.${build}"
}

get_linlib_version() {
    local major=$(grep "^MAJOR" ${S}/linlib/version.mk | cut -d'=' -f2 | tr -d ' ')
    local minor=$(grep "^MINOR" ${S}/linlib/version.mk | cut -d'=' -f2 | tr -d ' ')
    local build=$(grep "^BUILD" ${S}/linlib/version.mk | cut -d'=' -f2 | tr -d ' ')
    echo "${major}.${minor}.${build}"
}

do_compile() {
    # Get version information from version.mk files
    CANLIB_VERSION=$(get_canlib_version)
    LINLIB_VERSION=$(get_linlib_version)
    
    # Build CAN library
    oe_runmake -C canlib OBJDIR=${S}/canlib libcanlib.so.${CANLIB_VERSION}

    # Build LIN library  
    oe_runmake -C linlib OBJDIR=${S}/linlib liblinlib.so.${LINLIB_VERSION}
}

do_install() {
    # Get version information from version.mk files
    CANLIB_VERSION=$(get_canlib_version)
    LINLIB_VERSION=$(get_linlib_version)
    
    install -d ${D}${libdir}
    install -d ${D}${includedir}

    # Install CAN library
    install -m 0755 ${S}/canlib/libcanlib.so.${CANLIB_VERSION} ${D}${libdir}/
    ln -sf libcanlib.so.${CANLIB_VERSION} ${D}${libdir}/libcanlib.so.1
    ln -sf libcanlib.so.${CANLIB_VERSION} ${D}${libdir}/libcanlib.so

    # Install LIN library
    install -m 0755 ${S}/linlib/liblinlib.so.${LINLIB_VERSION} ${D}${libdir}/
    ln -sf liblinlib.so.${LINLIB_VERSION} ${D}${libdir}/liblinlib.so.1
    ln -sf liblinlib.so.${LINLIB_VERSION} ${D}${libdir}/liblinlib.so

    # Install headers
    install -m 0644 ${S}/include/canlib.h ${D}${includedir}/
    install -m 0644 ${S}/include/linlib.h ${D}${includedir}/
}

FILES:${PN} = "${libdir}/*.so.* "
FILES:${PN}-dev = "${includedir}/* ${libdir}/*.so"
