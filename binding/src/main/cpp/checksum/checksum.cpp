#include "checksum.hpp"

namespace ipsum {

    void update(uint32_t* sum, uint16_t* buffer, int offset, int size) {
        if (offset)
            buffer = (uint16_t*) (((char*) buffer) + offset);
        uint32_t value = *sum;
        while (size > 1) {
            value += *buffer++;
            size -= sizeof(uint16_t);
        }
        if (size)
            value += *(uint8_t*) buffer;
        *sum = value;
    }

    uint16_t finish(uint32_t* sum) {
        uint32_t value = *sum;
        while (value >> 16)
            value = (value & 0xffff) + (value >> 16);
        return (uint16_t) (~value);
    }
}