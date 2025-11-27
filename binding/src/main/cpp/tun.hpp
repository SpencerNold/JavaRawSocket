#pragma once

namespace tun {
    int create();
    void listen(int);
    void free(int);
}