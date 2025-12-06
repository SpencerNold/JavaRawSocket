#pragma once

#include <vector>
#include <cinttypes>

namespace sys {
    std::pair<std::vector<uint8_t>, uint32_t> getSystemAddresses(std::string);
    uint32_t getDefaultGateway();
    std::vector<uint8_t> getRouterMacAddress(uint32_t);

    uint16_t toNetworkOrder(uint16_t);
    uint32_t toNetworkOrder(uint32_t);
}