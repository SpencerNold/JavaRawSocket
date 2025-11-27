#ifdef __MACH__

#include "../tun.hpp"

#include <sys/socket.h>
#include <sys/kern_control.h>
#include <sys/sys_domain.h>
#include <sys/ioctl.h>
#include <net/if_utun.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>

int tun::create() {
    struct sockaddr_ctl addr;
    struct ctl_info info;
    int fd = socket(PF_SYSTEM, SOCK_DGRAM, SYSPROTO_CONTROL);
    if (fd < 0) return -1;
    memset(&info, 0, sizeof(info));
    strncpy(info.ctl_name, UTUN_CONTROL_NAME, sizeof(info.ctl_name));
    if (ioctl(fd, CTLIOCGINFO, &info) < 0)
        return -1;
    memset(&addr, 0, sizeof(addr));
    addr.sc_len = sizeof(addr);
    addr.sc_family = AF_SYSTEM;
    addr.ss_sysaddr = AF_SYS_CONTROL;
    addr.sc_id = info.ctl_id;
    addr.sc_unit = 0;
    if (connect(fd, (struct sockaddr *)&addr, sizeof(addr)) < 0)
        return -1;
    printf("utun%d\n", addr.sc_unit);
    return fd;
}

void tun::listen(int fd) {
    uint8_t buf[2000];
    while (1) {
        ssize_t n = read(fd, buf, sizeof(buf));
        if (n > 0) {
            printf("Captured packet (%zd bytes)\n", n);
        }
    }
}

void tun::free(int fd) {
    close(fd);
}

#endif