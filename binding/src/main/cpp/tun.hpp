#pragma once

#include <string>

namespace tun {
    int create();
    void bind(int);
    void listen(int);
    void free(int);
}