
do_deploy:append(){
    install -d "${D}/boot"
    install -m 0644 "${DEPLOYDIR}/${BOOTFILES_DIR_NAME}/config.txt" "${D}/boot"
}