#ifdef __MACH__

#include "../upcap.hpp"

#include <iostream>

extern "C" {
    #include <pcap.h>
}

namespace upcap {

    struct Handle {
        pcap_t* handle;
        std::string interface;
    };

    void (*raw_handler)(Handle*, const unsigned char*, int) = nullptr;

    std::string getDefaultDevice() {
        char buf[PCAP_ERRBUF_SIZE];
        char* device = pcap_lookupdev(buf);
        if (!device) {
            throw std::runtime_error(std::string("Error finding default device: ") + buf);
        }
        std::string name(device);
        return name;
    }

    Handle* open(std::string device) {
        char buf[PCAP_ERRBUF_SIZE];
        pcap_t* pcap = pcap_open_live(device.c_str(), 65535, 1, 1000, buf);
        if (!pcap) {
            throw std::runtime_error(std::string("Error instantiating pcap: ") + buf);
        }
        Handle* handle = new Handle;
        handle->handle = pcap;
        handle->interface = device;
        return handle;
    }

    void send(Handle* handle, const unsigned char* data, int length) {
        pcap_sendpacket(handle->handle, data, length);
    }

    void packet_handler(u_char* user_data, const struct pcap_pkthdr* header, const u_char* packet) {
        Handle* handle = (Handle*) user_data;
        int length = header->caplen;
        raw_handler(handle, (const unsigned char*) packet, length);
    }

    void listen(Handle* handle, void (*handler)(Handle*, const unsigned char*, int)) {
        if (raw_handler == nullptr) {
            raw_handler = handler;
            pcap_loop(handle->handle, -1, packet_handler, (u_char*) handle);
        }
    }

    void close(Handle* handle) {
        pcap_close(handle->handle);
        delete(handle);
    }
}

#endif