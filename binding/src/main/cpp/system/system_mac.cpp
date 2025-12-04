#ifdef __MACH__

#include "system.hpp"

#include <string>

#include <sys/socket.h>
#include <arpa/inet.h>
#include <ifaddrs.h>
#include <net/if_dl.h>
#include <sys/sysctl.h>
#include <net/route.h>
#include <string.h>
#include <netinet/if_ether.h>

namespace sys {

    std::pair<std::vector<uint8_t>, uint32_t> getSystemAddresses(std::string interface) {
        struct ifaddrs *ifaddr, *ifa;
        if (getifaddrs(&ifaddr) == -1) {
            throw std::runtime_error(std::string("Error loading system MAC address"));
        }
        std::vector<uint8_t> v_mac;
        uint32_t i_ipv4;
        for (ifa = ifaddr; ifa != NULL; ifa = ifa->ifa_next) {
            if (ifa->ifa_addr == NULL) {
                continue;
            }
            if (strcmp(ifa->ifa_name, interface.c_str()) == 0) {
                // MAC Addr
                if (ifa->ifa_addr->sa_family == AF_LINK) {
                    struct sockaddr_dl* sdl = (struct sockaddr_dl*) ifa->ifa_addr;
                    unsigned char* mac = (unsigned char*) LLADDR(sdl);
                    for (int i = 0; i < 6; i++) {
                        v_mac.push_back(mac[i]);
                    }
                }
                // IPv4 Addr
                if (ifa->ifa_addr->sa_family == AF_INET) {
                    struct sockaddr_in* sin = (struct sockaddr_in*) ifa->ifa_addr;
                    i_ipv4 = htonl(sin->sin_addr.s_addr);
                }
            }
        }
        return { v_mac, i_ipv4 };
    }
}

#endif