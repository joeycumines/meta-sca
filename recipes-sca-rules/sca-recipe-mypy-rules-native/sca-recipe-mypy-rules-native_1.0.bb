SUMMARY = "Ruleset for mypy"
DESCRIPTION = " Rules to configure how mypy is affecting the build"
AUTHOR = "Konrad Weihmann <kweihmann@outlook.com>"

SRC_URI = "file://suppress \
           file://fatal"

LICENSE ?= "BSD-2-Clause"
LIC_FILES_CHKSUM ?= "file://${SCA_LAYERDIR}/LICENSE;md5=e926c89aceef6c1a4247d5df08f94533"

inherit native

do_install() {
    install -d "${D}${datadir}"
    install "${WORKDIR}/fatal" "${D}${datadir}/mypy-recipe-fatal"
    install "${WORKDIR}/suppress" "${D}${datadir}/mypy-recipe-suppress"
}

FILES_${PN} = "${datadir}/**"