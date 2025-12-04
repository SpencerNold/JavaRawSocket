#pragma once

#include <string>

namespace upcap {
    struct Handle;

    std::string getDefaultDevice();

    Handle* open(std::string);
    void send(Handle*, const unsigned char*, int);
    void listen(Handle*, void (*)(Handle*, const unsigned char*, int));
    void close(Handle*);
}