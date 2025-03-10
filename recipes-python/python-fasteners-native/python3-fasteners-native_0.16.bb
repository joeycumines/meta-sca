SUMMARY = "A python package that provides useful locks"
HOMEPAGE = "https://github.com/harlowja/fasteners"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4476c4be31402271e101d9a4a3430d52"

PYPI_PACKAGE = "fasteners"

SRC_URI[md5sum] = "35f400771864c68145fb3a0b47ec99db"
SRC_URI[sha256sum] = "c995d8c26b017c5d6a6de9ad29a0f9cdd57de61ae1113d28fac26622b06a0933"

inherit pypi
inherit setuptools3
inherit native
