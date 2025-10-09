do_deploy:append() {
    install -d "${D}/boot"
    install -m 0644 "${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/cmdline.txt" "${D}/boot"
}

