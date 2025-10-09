DESCRIPTION = "Image with customizations"
LICENSE = "MIT"

inherit core-image
inherit deploy

DEPENDS += "flash-scripts"

do_zip[depends] += "zip-native:do_populate_sysroot"

DEPLOY_FILES += " \
        core-image-gokul-${MACHINE}.rootfs.wic.gz \
        flash-script.sh \
        "

# Function to make Zip
do_zip() {
    ZIP_FILE="${MACHINE}-image.zip"
    cd "${DEPLOY_DIR}/images/${MACHINE}"
    for file in ${DEPLOY_FILES}
    do
        if [ ! -e "${file}" ]; then
            echo "ERROR: file ${DEPLOY_DIR}/images/${MACHINE}/${file} not found!"
            exit 1
        fi
    done
    zip -r "${ZIP_FILE}" ${DEPLOY_FILES}
}

do_clean_zip () {
    ZIP_FILE="${DEPLOY_DIR}/images/${MACHINE}/${MACHINE}-image.zip"
    rm -f ${ZIP_FILE}
}

addtask do_zip after do_image_complete before do_rm_work

# do_clean_zip is placed after do_unpack and before do_build
addtask do_clean_zip before do_build after do_unpack