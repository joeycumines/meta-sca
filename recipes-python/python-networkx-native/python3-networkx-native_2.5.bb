SUMMARY = "Python package for creating and manipulating graphs and networks"
HOMEPAGE = "https://github.com/networkx/networkx"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a24ea029adac8935699bf69b2e38c728"

DEPENDS += "python3-decorator-native"

PYPI_PACKAGE = "networkx"

SRC_URI[md5sum] = "21f25be1f4373e19153a9beca63346e7"
SRC_URI[sha256sum] = "7978955423fbc9639c10498878be59caf99b44dc304c2286162fd24b458c1602"

inherit pypi
inherit setuptools3
inherit native
