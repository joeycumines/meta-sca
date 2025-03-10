# Modified version from poky upstream + adjustments from https://github.com/voxtok/meta-oad-ruby/tree/dizzy/recipes-devtools/ruby
# SPDX-License-Identifier: MIT, BSD-2-Clause
# Copyright (c) 2015, Michaël Burtin
# Copyright (c) 2020, Konrad Weihmann
# Copyright (c) 2020, Yocto maintainers

FILESEXTRAPATHS_prepend := "${THISDIR}/files/3.0.0:"

SUMMARY = "An interpreter of object-oriented scripting language"
DESCRIPTION = "Ruby cross variant"
HOMEPAGE = "http://www.ruby-lang.org/"
SECTION = "devel/ruby"
LICENSE = "Ruby | BSD-2-Clause | BSD-3-Clause | GPLv2 | ISC | MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=5b8c87559868796979806100db3f3805 \
                    file://BSDL;md5=8b50bc6de8f586dc66790ba11d064d75 \
                    file://GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LEGAL;md5=cfe5b0bc9f051b58c7e78db882ca5f9b \
                    "

DEPENDS += "\
            gdbm \
            libffi \
            libyaml \
            openssl \
            readline \
            ruby-native \
            zlib \
           "

PN = "ruby-cross-${TARGET_ARCH}"

# Ignore how TARGET_ARCH is computed.
TARGET_ARCH[vardepvalue] = "${TARGET_ARCH}"

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI_append = " \
                  http://cache.ruby-lang.org/pub/ruby/${SHRT_VER}/ruby-${PV}.tar.gz \
                  file://remove_has_include_macros.patch \
                  file://0001-template-Makefile.in-do-not-write-host-cross-cc-item.patch \
                  file://0001-Makefile-cross-compile-fixes.patch \
                 "

SRC_URI[sha256sum] = "a13ed141a1c18eb967aac1e33f4d6ad5f21be1ac543c344e0d6feeee54af8e28"

S = "${WORKDIR}/ruby-${PV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[valgrind] = "--with-valgrind=yes, --with-valgrind=no, valgrind"
PACKAGECONFIG[gmp] = "--with-gmp=yes, --with-gmp=no, gmp"
PACKAGECONFIG[ipv6] = "--enable-ipv6, --disable-ipv6,"

inherit autotools cross

EXTRA_OECONF = "\
    --disable-versioned-paths \
    --disable-rpath \
    --enable-shared \
    --enable-load-relative \
    --host=${TARGET_SYS} \
    --libdir=${STAGING_DIR_TARGET}${target_libdir} \
    --includedir=${STAGING_DIR_TARGET}${target_includedir} \
    --bindir='$''{exec_prefix}/bin' \
    --sbindir='$''{exec_prefix}/sbin' \
    --libexecdir='$''{exec_prefix}/libexec' \
    --with-coroutine=copy \
"

# This snippet lets compiled extensions which rely on external libraries,
# such as zlib, compile properly.  If we don't do this, then when extmk.rb
# runs, it uses the native libraries instead of the target libraries, and so
# none of the linking operations succeed -- which makes extconf.rb think
# that the libraries aren't available and hence that the extension can't be
# built.
do_configure_prepend() {
    sed -i "s#%%TARGET_CFLAGS%%#$TARGET_CFLAGS#; s#%%TARGET_LDFLAGS%%#$TARGET_LDFLAGS#" ${S}/common.mk
    rm -rf ${S}/ruby/
}

do_install() {
    export CPPFLAGS_FROM_CROSS="${CPPFLAGS}"
    export CPPFLAGS_FOR_TARGET="${TARGET_CPPFLAGS}"
    export STRIP_FOR_TARGET="${TARGET_SYS}-strip"
    export NM_FOR_TARGET="${TARGET_SYS}-nm"
    export OBJDUMP_FOR_TARGET="${TARGET_SYS}-objdump"
    export AS_FOR_TARGET="${TARGET_SYS}-as"
    export AR_FOR_TARGET="${TARGET_SYS}-ar"
    export RANLIB_FOR_TARGET="${TARGET_SYS}-ranlib"
    export CPP_FOR_TARGET="${TARGET_SYS}-gcc -E --sysroot=${STAGING_DIR_TARGET} ${TARGET_CC_ARCH}"
    export CXXFLAGS_FROM_CROSS="${CXXFLAGS}"
    export CXXFLAGS_FOR_TARGET="${TARGET_CXXFLAGS}"
    export CXX_FOR_TARGET="${TARGET_SYS}-g++ --sysroot=${STAGING_DIR_TARGET} ${TARGET_CC_ARCH}"
    export LDFLAGS_FROM_CROSS="${LDFLAGS}"
    export LDFLAGS_FOR_TARGET="${TARGET_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
    export CFLAGS_FROM_CROSS="${CFLAGS}"
    export CFLAGS_FOR_TARGET="${TARGET_CFLAGS}"
    export CC_FOR_TARGET="${TARGET_SYS}-gcc --sysroot=${STAGING_DIR_TARGET} ${TARGET_CC_ARCH}"
    export CROSS_TARGET_SYS_DIR="${CROSS_TARGET_SYS_DIR}"
    oe_runmake 'DESTDIR=${D}' install-cross
}

FILES_${PN} = "${libdir}/ruby/*/*"
FILES_${PN}-dbg += "${libdir}/ruby/*/.debug \
                    ${libdir}/ruby/*/*/.debug \
                    ${libdir}/ruby/*/*/*/.debug"
SYSROOT_PREPROCESS_FUNCS += "ruby_cross_populate"
ruby_cross_populate() {
    sysroot_stage_dir ${D}/${STAGING_DIR_NATIVE}${target_libdir} ${SYSROOT_DESTDIR}/${STAGING_DIR_NATIVE}${target_libdir}
}

UPSTREAM_CHECK_URI = "https://www.ruby-lang.org/en/downloads/"
