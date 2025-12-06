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

    #define ROUNDUP(a, size) (((a) & ((size) - 1)) ? (1 + ((a) | ((size) - 1))) : (a))

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

    uint32_t getDefaultGateway() {
        int mib[6] = { CTL_NET, PF_ROUTE, 0, AF_INET, NET_RT_FLAGS, RTF_GATEWAY };
        size_t needed;
        if (sysctl(mib, 6, NULL, &needed, NULL, 0) < 0) {
            throw std::runtime_error(std::string("Default gateway not found"));
        }
        char data[needed];
        if (sysctl(mib, 6, data, &needed, NULL, 0)) {
            throw std::runtime_error(std::string("Default gateway not found"));
        }
        char* buf = (char*) data;
        char* lim = buf + needed;
        char* next;
        struct rt_msghdr* rtm;
        struct sockaddr* sa;
        struct sockaddr_in* sin;
        struct sockaddr_dl* sdl;
        int found = 0;
        for (next = buf; next < lim; next += rtm->rtm_msglen) {
            rtm = (struct rt_msghdr*) next;
            sa = (struct sockaddr *)(rtm + 1);
            for (int i = 0; i < RTAX_MAX; i++) {
                if (rtm->rtm_addrs & (1 << i)) {
                    if (i == RTAX_DST) {
                        sin = (struct sockaddr_in*) sa;
                        if (sin->sin_addr.s_addr == 0) {
                            found = 1;
                        }
                    }
                    if (found && i == RTAX_GATEWAY) {
                        if (sa->sa_family == AF_INET) {
                            sin = (struct sockaddr_in*) sa;
                            return sin->sin_addr.s_addr;
                        }
                    }
                    sa = (struct sockaddr *)((char *)sa + ROUNDUP(sa->sa_len, sizeof(long)));
                }
            }
        }
        throw std::runtime_error(std::string("Default gateway not found"));
    }

    std::vector<uint8_t> getRouterMacAddress(uint32_t address) {
        std::vector<uint8_t> v_mac;
        int mib[6] = { CTL_NET, PF_ROUTE, 0, AF_INET, NET_RT_FLAGS, RTF_LLINFO };
        size_t needed;
        if (sysctl(mib, 6, NULL, &needed, NULL, 0) < 0) {
            throw std::runtime_error(std::string("Router mac not found"));
        }
        char data[needed];
        if (sysctl(mib, 6, data, &needed, NULL, 0) < 0) {
            throw std::runtime_error(std::string("Router mac not found"));
        }
        char* buf = (char*) data;
        char* lim = buf + needed;
        char* next;
        struct rt_msghdr* rtm;
        struct sockaddr_inarp* sin;
        struct sockaddr_dl* sdl;
        for (next = buf; next < lim; next += rtm->rtm_msglen) {
            rtm = (struct rt_msghdr*) next;
            sin = (struct sockaddr_inarp*) (rtm + 1);
            sdl = (struct sockaddr_dl*) (sin + 1);
            if (address != sin->sin_addr.s_addr) {
                continue;
            }
            unsigned char* mac_addr = (unsigned char*) LLADDR(sdl);
            for (int i = 0; i < 6; i++) {
                v_mac.push_back(mac_addr[i]);
            }
            return v_mac;
        }
        throw std::runtime_error(std::string("Router mac not found"));
    }

    uint16_t toNetworkOrder(uint16_t i) {
        return htons(i);
    }

    uint32_t toNetworkOrder(uint32_t i) {
        return htonl(i);
    }
}

#endif