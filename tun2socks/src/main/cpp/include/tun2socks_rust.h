//
// Created by Zygisk on 2024/2/21.
//

#ifndef PASTEL_TUN2SOCKS_RUST_H
#define PASTEL_TUN2SOCKS_RUST_H

extern "C" void tun2socks(int fd, const char *log_path);
extern "C" void stop();
/*extern "C" {
int test_num(int fd);
const char *test_cstr(const char *log_path);
const char *test_cstr_with_len(const char *str, int len);
}*/

#endif //PASTEL_TUN2SOCKS_RUST_H
