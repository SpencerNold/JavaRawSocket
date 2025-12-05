#pragma once

#include <vector>
#include <cinttypes>

namespace sys {
    std::pair<std::vector<uint8_t>, uint32_t> getSystemAddresses(std::string);
    uint32_t getDefaultGateway(std::string);
}