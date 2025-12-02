#ifdef __MACH__

#include "../upcap.hpp"

#include <iostream>

extern "C" {
    #include <pcap.h>
}

std::string upcap::getDefaultDevice() {
    char buf[PCAP_ERRBUF_SIZE];
    char* device = pcap_lookupdev(buf);
    if (!device) {
        throw std::runtime_error(std::string("Error finding default device: ") + buf);
    }
    std::string name(device);
    return name;
}

#endif